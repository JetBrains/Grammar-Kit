/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions

import com.intellij.ide.trustedProjects.TrustedProjects
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.ui.content.MessageView
import kotlinx.coroutines.Job
import java.io.File

/**
 * Tests [GrammarKitCliRunner.run] itself: a real child process, its console, and the refresh of
 * what it reported. The unit and command-line tests next door stop at building the command.
 *
 * The child is a bare JVM running [EchoGeneratedMain] out of the test classes directory, which
 * keeps the test identical on every OS — no shell, no temporary scripts, no `sh` vs `cmd`.
 */
class GrammarKitCliRunnerRunPlatformTest : BasePlatformTestCase() {

  /**
   * The coroutine has to reach the EDT to attach its console, so the test cannot be sitting on
   * the EDT waiting for it — that deadlocks.
   */
  override fun runInDispatchThread(): Boolean = false

  private lateinit var workDir: File
  private lateinit var grammar: VirtualFile
  private var trustedBefore = false

  override fun setUp() {
    super.setUp()
    workDir = FileUtil.createTempDirectory("grammar-kit-cli", null, true)
    val grammarFile = File(workDir, "Grammar.bnf")
    FileUtil.writeToFile(grammarFile, "root ::= 'x'")
    grammar = requireNotNull(LocalFileSystem.getInstance().refreshAndFindFileByIoFile(grammarFile))
    // The command is spawned from the project base directory, and a light fixture's can be a
    // leftover temp path that some earlier test has already cleaned up.
    project.basePath?.let { FileUtil.createDirectory(File(it)) }
    // The runner refuses to run project content in an untrusted project, which a fixture is.
    trustedBefore = TrustedProjects.isProjectTrusted(project)
    TrustedProjects.setProjectTrusted(project, true)
    assertTrue("the fixture project must be trusted, or nothing is ever spawned",
               TrustedProjects.isProjectTrusted(project))
  }

  override fun tearDown() {
    try {
      releaseConsoles()
      TrustedProjects.setProjectTrusted(project, trustedBefore)
    }
    finally {
      super.tearDown()
    }
  }

  /**
   * Each run leaves a console tab, and with it an editor, on the light project's Messages tool
   * window — which outlives the test. Nothing disposes them there, so the fixture's
   * "editor hasn't been released" check fails unless the tabs go with the test that opened them.
   */
  private fun releaseConsoles() {
    ApplicationManager.getApplication().invokeAndWait {
      val contentManager = MessageView.getInstance(project).contentManager
      contentManager.contents.forEach { contentManager.removeContent(it, true) }
    }
  }

  fun testReportedFileIsRefreshedIntoTheVfs() {
    val generated = File(workDir, "gen/Parser.java")

    await(launch(cli(outputs = listOf(generated))))

    assertTrue("the command should have written the file", generated.isFile)
    assertNotNull("a reported file must be brought into the VFS",
                  LocalFileSystem.getInstance().findFileByIoFile(generated))
  }

  fun testEveryReportedFileIsRefreshed() {
    val parser = File(workDir, "gen/Parser.java")
    // A directory of its own: refreshAndFindFileByIoFile has to walk the new parents too.
    val psi = File(workDir, "gen/psi/Root.java")

    await(launch(cli(outputs = listOf(parser, psi))))

    val fileSystem = LocalFileSystem.getInstance()
    assertNotNull(parser.path, fileSystem.findFileByIoFile(parser))
    assertNotNull(psi.path, fileSystem.findFileByIoFile(psi))
  }

  fun testNonZeroExitFinishesTheJobInsteadOfHanging() {
    val generated = File(workDir, "gen/Parser.java")

    val job = launch(cli(exitCode = 3, outputs = listOf(generated)))
    await(job)

    // The point is that it completes at all: a run that never resolves used to leave every
    // waiter hanging, and the timeout in `await` is what would catch a regression.
    assertTrue(job.isCompleted)
  }

  fun testUntrustedProjectRunsNothing() {
    TrustedProjects.setProjectTrusted(project, false)
    val generated = File(workDir, "gen/Parser.java")

    await(launch(cli(outputs = listOf(generated))))

    assertFalse("an untrusted project must not spawn the command", generated.exists())
  }

  /** [GrammarKitCliRunner.run] reads the project model, so it has to be called on the EDT. */
  private fun launch(cliText: String): Job {
    lateinit var job: Job
    ApplicationManager.getApplication().invokeAndWait {
      job = GrammarKitCliRunner.getInstance(project).run(listOf(grammar), cliText, "Parser")
    }
    return job
  }

  private fun await(job: Job) = timeoutRunBlocking { job.join() }

  /**
   * A command line running [EchoGeneratedMain] in a bare JVM. Every argument is quoted, since the
   * text is split before macros are expanded and a temporary directory may contain spaces.
   */
  private fun cli(exitCode: Int = 0, outputs: List<File>): String {
    val java = File(File(System.getProperty("java.home"), "bin"), "java").absolutePath
    val classes = requireNotNull(PathManager.getJarPathForClass(EchoGeneratedMain::class.java))
    return buildString {
      append(quoted(java))
      append(" -cp ").append(quoted(classes))
      append(' ').append(EchoGeneratedMain::class.java.name)
      append(" --exit ").append(exitCode)
      outputs.forEach { append(" --out ").append(quoted(it.absolutePath)) }
    }
  }

  private fun quoted(path: String) = "\"" + path + "\""
}
