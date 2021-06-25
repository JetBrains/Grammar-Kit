/*
 * Copyright 2011-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.compiler;

import com.intellij.lang.LanguageASTFactory;
import com.intellij.lang.LanguageBraceMatching;
import com.intellij.mock.MockProject;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.CharSequenceReader;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.comp.Check;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Todo;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import org.intellij.grammar.*;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.BnfFile;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author gregsh
 */
public class GrammarPlugin implements com.sun.source.util.Plugin {
  @Override
  public String getName() {
    return "GrammarPlugin";
  }

  @Override
  public void init(JavacTask task, String... args) {
    List<String> paths = args == null || args.length == 0 || args[0] == null ? Collections.emptyList() : parsePaths(args[0]);
    if (paths.isEmpty()) return;
    Context context = ((BasicJavacTask)task).getContext();
    JavacProcessingEnvironment processingEnv = JavacProcessingEnvironment.instance(context);
    initGenerator(processingEnv);
    task.setTaskListener(new TaskListener() {
      boolean entered, analyzed;
      @Override
      public void started(TaskEvent e) {
        if (e.getKind() == TaskEvent.Kind.PARSE) return;
        if (e.getKind() == TaskEvent.Kind.ENTER && !entered) {
          entered = true;
          doGenerate(context, processingEnv, paths, false);
        }
        else if (e.getKind() == TaskEvent.Kind.ANALYZE && !analyzed) {
          analyzed = true;
          doGenerate(context, processingEnv, paths, true);
        }
      }

      @Override
      public void finished(TaskEvent e) {
        TaskListener.super.finished(e);
      }
    });
  }

  static List<String> parsePaths(String option) {
    String[] value = StringUtil.notNullize(option).split(Pattern.quote(File.pathSeparator));
    return ContainerUtil.findAll(value, o -> !StringUtil.isEmptyOrSpaces(o));
  }

  static void initGenerator(ProcessingEnvironment processingEnv) {
    LightPsi.init();
    LightPsi.Init.addKeyedExtension(LanguageASTFactory.INSTANCE, BnfLanguage.INSTANCE, new BnfASTFactory(), null);
    LightPsi.Init.addKeyedExtension(LanguageBraceMatching.INSTANCE, BnfLanguage.INSTANCE, new BnfBraceMatcher(), null);

    PsiFile testPsi = LightPsi.parseFile("test.bnf", "", new BnfParserDefinition());
    if (!(testPsi instanceof BnfFile) || !(testPsi.getProject() instanceof MockProject)) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Grammar parser failed to initialize");
    }
    else {
      MockProject project = (MockProject)testPsi.getProject();
      project.getPicoContainer().unregisterComponent(JavaHelper.class.getName());
      project.registerService(JavaHelper.class, new JavacJavaHelper(processingEnv));
    }
  }

  static void doGenerate(Context context,
                         ProcessingEnvironment processingEnv,
                         List<String> grammarPaths,
                         boolean isEnterDone) {
    for (String path : grammarPaths) {
      try {
        File file = new File(path);
        PsiFile bnfFile = LightPsi.parseFile(file, new BnfParserDefinition());
        if (bnfFile instanceof BnfFile) {
          new ParserGenerator((BnfFile)bnfFile, file.getParentFile().getCanonicalPath(), "", "") {
            @Override
            protected PrintWriter openOutputInner(String className, File file) {
              URI uri = URI.create("string://" + file.getPath().replace('\\', '/'));
              if (isEnterDone) {
                // drop symbols and files from the first pass
                Symtab symtab = Symtab.instance(context);
                Name name = Names.instance(context).fromString(className);
                Symbol.ClassSymbol classSym = symtab.getClass(symtab.unnamedModule, name);
                for (Symbol symbol : classSym.members().getSymbols()) {
                  if (symbol instanceof Symbol.ClassSymbol) {
                    Check.instance(context).removeCompiled((Symbol.ClassSymbol)symbol);
                  }
                }
                Check.instance(context).removeCompiled(classSym);
                Todo.instance(context).removeIf(o -> uri.equals(o.toplevel.sourcefile.toUri()));
              }
              Writer writer = new StringWriter() {
                @Override
                public void close() {
                  JCTree.JCCompilationUnit unit = JavaCompiler.instance(context).parse(new SimpleJavaFileObject(
                    uri, JavaFileObject.Kind.SOURCE) {
                    @Override
                    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                      return getBuffer();
                    }
                  });
                  unit.modle = Symtab.instance(context).unnamedModule;
                  Enter.instance(context).visitTopLevel(unit);

                  if (isEnterDone) {
                    // dump generated files on the second pass to see what's there
                    try {
                      FileObject resource =
                        processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", className.replace('.', '/') + ".java");
                      try (Writer out = resource.openWriter()) {
                        new CharSequenceReader(getBuffer()).transferTo(out);
                      }
                    }
                    catch (IOException e) {
                      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
                    }
                  }
                }
              };
              return new PrintWriter(writer);
            }

            @Override
            public void addWarning(String text) {
              if (isEnterDone) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, text);
              }
            }
          }
            .generate();
          if (isEnterDone) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, file.getCanonicalPath() + " generated");
          }
        }
        else {
          processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Not a grammar file: " + file.getCanonicalPath());
        }
      }
      catch (IOException e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
      }
    }
  }
}
