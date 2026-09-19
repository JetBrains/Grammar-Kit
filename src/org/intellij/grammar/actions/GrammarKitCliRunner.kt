/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.ide.trustedProjects.TrustedProjects
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.asContextElement
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.concurrency.annotations.RequiresEdt
import com.intellij.util.execution.ParametersListUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.intellij.grammar.generator.CommonBnfConstants
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.VisibleForTesting
import java.io.File

/**
 * Runs a user-configured command line over grammar files.
 *
 * The contract is intentionally minimal: the plugin executes the command line once, from the
 * project's base directory, with every selected grammar passed as an absolute path. Output
 * directories, generator flags and everything else are the concern of whatever the command line
 * points at.
 *
 * The paths go where `$GrammarFiles$` appears as a standalone argument, so that trailing flags
 * remain expressible; with no such argument they are appended, which is the common
 * `<cli text> <absolute grammar file path>...` shape.
 *
 * Optionally, the command may report what it produced by printing lines of the form
 * `grammar-kit:generated <path>` on either output stream. When at least one such line appears,
 * only those files are refreshed; when the command reports nothing, the plugin falls back to
 * refreshing every project content root, since it has no way to know where an opaque command
 * wrote its output.
 *
 * The text is split into arguments first — quote an argument containing spaces — and project
 * path macros (`$PROJECT_DIR$` and user-defined ones) are then expanded within each argument, so
 * a command referring to a script inside the repository stays portable even when the repository
 * lives under a path with a space in it. The text is *not* run through a shell: `&&`, pipes, `~`
 * and `$VAR` are not interpreted. Use `sh -c '...'` or a script file for those.
 *
 * The service's own [CoroutineScope] is what keeps the children under control: it is cancelled
 * when the project closes and, unlike the project itself, also when the plugin is unloaded, so
 * no spawned command outlives either.
 */
@Service(Service.Level.PROJECT)
class GrammarKitCliRunner(private val project: Project, private val cs: CoroutineScope) {

  /**
   * Runs [cliText] once over the whole selection, streaming its output into a console tab.
   * Returns immediately; the process runs in the background.
   *
   * Must be called on the EDT: the project's content roots are captured up front, while the
   * project is known to be alive and read access is implicit.
   *
   * @return the job running the command, already completed if nothing was started
   */
  @RequiresEdt
  fun run(files: List<VirtualFile>, cliText: String, title: String): Job {
    if (files.isEmpty()) return completedJob()

    // The command comes from .idea/grammarKit.xml, which is meant to be committed — so in a
    // freshly cloned project it is somebody else's code. Running it needs the same trust that
    // running a build script does.
    if (!TrustedProjects.isProjectTrusted(project)) {
      notifyError("$title generation skipped",
                  "The configured command line is project content, and this project is not trusted.<br>" +
                  "Trust the project to run it.")
      return completedJob()
    }

    val commandLine = try {
      buildCommandLine(project, files, cliText)
    }
    catch (e: IllegalArgumentException) {
      notifyError("$title generation failed", e.message.orEmpty())
      return completedJob()
    }

    // Captured while the project is alive and we are on the EDT, so that nothing downstream has
    // to touch the project model again.
    val contentRoots = ProjectRootManager.getInstance(project).contentRoots

    return cs.launch(CoroutineName("Grammar-Kit CLI: $title")) {
      execute(commandLine, title, contentRoots)
    }
  }

  /**
   * Spawns the command, gives it a console, and refreshes whatever it produced.
   *
   * Cancelling this — which is what closing the project or unloading the plugin does — kills the
   * child through the `finally` below, so it can never be left running detached with nobody
   * reading its output.
   */
  private suspend fun execute(commandLine: GeneralCommandLine,
                              title: String,
                              contentRoots: Array<VirtualFile>) {
    val collector = GeneratedFilesCollector(commandLine.workDirectory)

    // Forking is a slow operation: never on the EDT, and never on the scope's default dispatcher.
    val processHandler = try {
      withContext(Dispatchers.IO) { OSProcessHandler(commandLine) }
    }
    catch (e: ExecutionException) {
      notifyError("$title generation failed",
                  "Unable to run ${commandLine.exePath}<br>${e.localizedMessage}")
      return
    }

    // Completed from the listener rather than from Process.onExit(): startNotify() drains both
    // output pumps before firing processTerminated, so this is the only signal that is ordered
    // after the last onTextAvailable. onExit() would race the tail of the output.
    val terminated = CompletableDeferred<Int>()
    // Added before startNotify, so no output can be missed whatever happens on the EDT below.
    processHandler.addProcessListener(object : ProcessListener {
      override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        collector.append(outputType, event.text)
      }

      override fun processTerminated(event: ProcessEvent) {
        terminated.complete(event.exitCode)
      }
    })

    try {
      // The console must be attached before startNotify, or the first output is lost. Hence
      // ModalityState.any(): under the default modality an open dialog would defer this, and the
      // unread child would wedge on a full pipe. UI only, so any() is safe.
      val attached = withContext(Dispatchers.EDT + ModalityState.any().asContextElement()) {
        if (project.isDisposed) false
        else try {
          GrammarKitConsole.showConsole(project, title, "cli@" + System.nanoTime(), processHandler)
          true
        }
        catch (e: CancellationException) {
          // A closing project is not a failure: let it cancel, and leave the child to the
          // `finally` below. Caught explicitly because CancellationException is an Exception,
          // and reporting it as a fatal error would pop an error dialog on every project close.
          throw e
        }
        catch (e: Exception) {
          // Nobody will read the output now, so the child has to go; without this it would wedge
          // on a full pipe and the job would never finish.
          LOG.error(e)
          false
        }
      }
      processHandler.startNotify()
      if (!attached) processHandler.destroyProcess()

      val exitCode = terminated.await()
      if (exitCode != 0) {
        notifyError("$title generation failed", "Exit code $exitCode")
      }
      val reported = collector.finish()
      // Blocking I/O, and worth finishing once started: a half-done refresh leaves the VFS
      // disagreeing with the files the command actually wrote.
      withContext(Dispatchers.IO + NonCancellable) {
        // A closed project has nothing to refresh into, and this is a synchronous refresh over
        // state captured before the close.
        if (!project.isDisposed) {
          if (reported.isNotEmpty()) refreshReported(reported)
          // Only worth scanning the whole project for output a successful run may have left.
          else if (exitCode == 0) VfsUtil.markDirtyAndRefresh(false, true, true, *contentRoots)
        }
      }
    }
    finally {
      // destroyProcess() postpones itself until startNotify(), so a cancellation that landed
      // before the EDT hop completed would otherwise leak the child forever. startNotify() logs
      // an error if called twice, hence the guard rather than an unconditional call.
      if (!processHandler.isStartNotified) processHandler.startNotify()
      processHandler.destroyProcess()
    }
  }

  /** Refreshes exactly the files a command said it produced. */
  private fun refreshReported(reported: List<File>) {
    val fileSystem = LocalFileSystem.getInstance()
    // refreshAndFindFileByIoFile walks the parents, so a file in a brand-new directory is found too.
    val files = reported.mapNotNull { fileSystem.refreshAndFindFileByIoFile(it) }
    if (files.isNotEmpty()) {
      VfsUtil.markDirtyAndRefresh(false, false, false, *files.toTypedArray())
    }
  }

  private fun notifyError(title: String, content: String) {
    if (project.isDisposed) return
    Notifications.Bus.notify(
      Notification(CommonBnfConstants.GENERATION_GROUP, title, content, NotificationType.ERROR), project)
  }

  private fun completedJob(): Job = Job().apply { complete() }

  companion object {

    /**
     * Prefix a command may use to report a file it produced: `grammar-kit:generated <path>`.
     * The path is the rest of the line, trimmed — no quoting is needed for paths containing
     * spaces. Relative paths resolve against the command's own working directory, whatever
     * [workDirectory] settled on — so what the plugin refreshes is what the command itself
     * would have written.
     */
    const val GENERATED_MARKER: String = "grammar-kit:generated"

    /**
     * Argument that expands to the absolute paths of the selected grammars, in selection order.
     * Recognised as a whole argument only; embedded in a larger one it stays literal, since there
     * is no sensible way to join several paths into one argument.
     */
    const val GRAMMAR_FILES_MACRO: String = "\$GrammarFiles\$"

    private val LOG = Logger.getInstance(GrammarKitCliRunner::class.java)

    @JvmStatic
    fun getInstance(project: Project): GrammarKitCliRunner = project.service()

    /**
     * Splits [cliText] into executable and arguments, substitutes the absolute path of every
     * grammar in [files] for a standalone [GRAMMAR_FILES_MACRO] argument — appending them at the
     * end if there is none — and expands path macros in every remaining argument.
     *
     * Splitting comes first on purpose: expanding `$PROJECT_DIR$` into a path containing a space
     * and splitting afterwards would silently tear one argument into two.
     *
     * @throws IllegalArgumentException if [files] is empty, if the text contains no executable
     * — including the case where [GRAMMAR_FILES_MACRO] stands where the executable should — or
     * if it uses [GRAMMAR_FILES_MACRO] more than once
     */
    @JvmStatic
    @ApiStatus.Internal
    @VisibleForTesting
    fun buildCommandLine(project: Project, files: List<VirtualFile>, cliText: String): GeneralCommandLine {
      require(files.isNotEmpty()) { "No grammar files to generate from." }
      val tokens = ParametersListUtil.parse(cliText, false, true)
      require(tokens.isNotEmpty()) { "The configured command line is empty." }
      // Otherwise the first grammar would silently become the executable and the run would fail
      // at spawn time with a baffling "cannot run program <some>.bnf".
      require(GRAMMAR_FILES_MACRO != tokens[0]) {
        "The configured command line starts with $GRAMMAR_FILES_MACRO and names no executable."
      }

      val macroManager = PathMacroManager.getInstance(project)
      val parts = ArrayList<String>(tokens.size + files.size)
      var filesPlaced = false
      for (token in tokens) {
        if (GRAMMAR_FILES_MACRO == token) {
          require(!filesPlaced) { "The configured command line uses $GRAMMAR_FILES_MACRO more than once." }
          filesPlaced = true
          files.mapTo(parts) { grammarPath(it) }
          continue
        }
        parts.add(macroManager.expandPath(token) ?: token)
      }

      val commandLine = GeneralCommandLine(parts)
        .withWorkDirectory(workDirectory(project, files))
        // No withCharset: that decodes the child's output, which has nothing to do with the
        // grammar files' own encoding. The platform default console encoding is the right guess.
        .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
      if (!filesPlaced) {
        commandLine.addParameters(files.map { grammarPath(it) })
      }
      return commandLine
    }

    private fun grammarPath(file: VirtualFile): String = VfsUtilCore.virtualToIoFile(file).absolutePath

    /**
     * The project's base directory: the one vantage point that does not depend on which grammars
     * happen to be selected, or in what order. Falls back to the first grammar's own directory
     * for a project without a base path, and to the IDE's own working directory — by leaving it
     * unset — for a grammar without a parent. Whatever it returns is also what a reported
     * relative path resolves against, so the plugin and the command always agree on where a
     * relative path points.
     */
    private fun workDirectory(project: Project, files: List<VirtualFile>): File? {
      val basePath = project.basePath
      if (basePath != null) return File(basePath)
      return files.firstOrNull()?.let { VfsUtilCore.virtualToIoFile(it).parentFile }
    }
  }

  /**
   * Scans process output for [GENERATED_MARKER] lines. Output arrives in arbitrary chunks, so
   * partial lines are held back until their newline shows up — separately per stream, since
   * stdout and stderr are pumped by different threads and would otherwise splice each other's
   * lines in half.
   *
   * Duplicates are collapsed and order is preserved. Accessed from the reader threads and then
   * from the coroutine awaiting termination, hence the synchronization.
   */
  @ApiStatus.Internal
  @VisibleForTesting
  class GeneratedFilesCollector(private val workDirectory: File?) {

    // Insertion-ordered so that the tails flushed by finish() come out in a defined order.
    private val pending = LinkedHashMap<Any, StringBuilder>()
    private val files = LinkedHashSet<File>()

    fun append(text: String) {
      append(ProcessOutputTypes.STDOUT, text)
    }

    @Synchronized
    fun append(stream: Any, text: String) {
      val buffer = pending.getOrPut(stream) { StringBuilder() }
      // Everything before this point is a partial line already known to hold no terminator.
      val searchFrom = buffer.length
      buffer.append(text)
      var start = 0
      for (i in searchFrom until buffer.length) {
        val c = buffer[i]
        // '\r' counts: a progress bar redrawing itself with carriage returns would otherwise
        // never terminate a line, growing the buffer without bound.
        if (c != '\n' && c != '\r') continue
        acceptLine(buffer.substring(start, i))
        start = i + 1
      }
      buffer.delete(0, start)
      if (buffer.length > MAX_PENDING) {
        // Far longer than any path: this is not a marker line, so stop holding on to it.
        buffer.setLength(0)
      }
    }

    /** Consumes any trailing line without a newline and returns everything collected. */
    @Synchronized
    fun finish(): List<File> {
      for (buffer in pending.values) {
        acceptLine(buffer.toString())
        buffer.setLength(0)
      }
      return ArrayList(files)
    }

    private fun acceptLine(line: String) {
      val trimmed = line.trim()
      if (!trimmed.startsWith(GENERATED_MARKER)) return
      val rest = trimmed.substring(GENERATED_MARKER.length)
      // Require a separator so that a hypothetical "grammar-kit:generatedXxx" is not a match.
      if (rest.isNotEmpty() && !rest[0].isWhitespace()) return
      val path = rest.trim()
      if (path.isEmpty()) return
      val file = File(path)
      // A relative File resolves against the IDE's working directory, which is exactly the one
      // the child inherited when no working directory was set — so both agree either way.
      files.add(if (file.isAbsolute || workDirectory == null) file else File(workDirectory, path))
    }

    private companion object {
      private const val MAX_PENDING = 8192
    }
  }
}
