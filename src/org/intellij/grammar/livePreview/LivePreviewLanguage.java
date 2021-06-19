/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.lang.Language;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.Label;
import org.jetbrains.org.objectweb.asm.MethodVisitor;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.intellij.grammar.generator.BnfConstants.LP_DISPLAY_NAME;
import static org.jetbrains.org.objectweb.asm.Opcodes.*;


/**
 * @author gregsh
 */
public class LivePreviewLanguage extends Language {

  private final VirtualFilePointer myFilePointer;
  private final SoftReference<BnfFile> myBnfFile;
  public static final Language BASE_INSTANCE = new Language("BNF_LP") {
    @Override
    public @NotNull String getDisplayName() {
      return LP_DISPLAY_NAME;
    }
  };

  private static final MyClassLoader ourClassLoader = new MyClassLoader();

  protected LivePreviewLanguage(@NotNull BnfFile grammarFile) {
    super(BASE_INSTANCE, Objects.requireNonNull(grammarFile.getVirtualFile()).getPath());
    VirtualFile virtualFile = Objects.requireNonNull(grammarFile.getVirtualFile());
    Application app = ApplicationManager.getApplication();
    if (app.isUnitTestMode()) {
      myBnfFile = new SoftReference<>(grammarFile);
      myFilePointer = null;
    }
    else {
      myFilePointer = VirtualFilePointerManager.getInstance().create(virtualFile, app, null);
      myBnfFile = null;
    }
  }

  @Override
  public @NotNull String getDisplayName() {
    VirtualFile file = getGrammarFile();
    return file == null ? getID() : "'" + file.getName() + "' grammar";
  }

  public @Nullable VirtualFile getGrammarFile() {
    if (myBnfFile != null) {
      BnfFile file = myBnfFile.get();
      return file == null? null : file.getVirtualFile();
    }
    else {
      return myFilePointer.getFile();
    }
  }

  public @Nullable BnfFile getGrammar(@Nullable Project project) {
    if (myBnfFile != null) return myBnfFile.get();
    VirtualFile file = project == null? null : getGrammarFile();
    PsiFile psiFile = file == null? null : PsiManager.getInstance(project).findFile(file);
    return psiFile instanceof BnfFile? (BnfFile)psiFile : null;
  }

  public static @NotNull LivePreviewLanguage newInstance(PsiFile psiFile) {
    try {
      return (LivePreviewLanguage)ourClassLoader.createClass().getDeclaredConstructors()[0].newInstance(psiFile);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static @Nullable LivePreviewLanguage findInstance(PsiFile psiFile) {
    VirtualFile vFile = psiFile.getVirtualFile();
    if (vFile == null) return null;
    for (Language language : Language.getRegisteredLanguages()) {
      if (language instanceof LivePreviewLanguage &&
          vFile.equals(((LivePreviewLanguage)language).getGrammarFile())) {
        return (LivePreviewLanguage)language;
      }
    }
    return null;
  }

  public @NotNull List<Editor> getGrammarEditors(@NotNull Project project) {
    VirtualFile file = getGrammarFile();
    if (file == null) return Collections.emptyList();
    FileEditor[] editors = FileEditorManager.getInstance(project).getAllEditors(file);
    if (editors.length == 0) return Collections.emptyList();
    List<Editor> result = new ArrayList<>();
    for (FileEditor editor : editors) {
      if (editor instanceof TextEditor) result.add(((TextEditor)editor).getEditor());
    }
    return result;
  }

  public @NotNull List<Editor> getPreviewEditors(@NotNull Project project) {
    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
    VirtualFile[] files = fileEditorManager.getOpenFiles();
    if (files.length == 0) return Collections.emptyList();
    List<Editor> result = new ArrayList<>();
    PsiManager psiManager = PsiManager.getInstance(project);
    for (VirtualFile file : files) {
      PsiFile psiFile = psiManager.findFile(file);
      Language language = psiFile == null ? null : psiFile.getLanguage();
      if (language == this) {
        for (FileEditor editor : fileEditorManager.getAllEditors(file)) {
          if (editor instanceof TextEditor) result.add(((TextEditor)editor).getEditor());
        }
      }
    }
    return result;
  }

  private static class MyClassLoader extends ClassLoader {
    private final AtomicInteger myCounter = new AtomicInteger();

    MyClassLoader() {
      super(LivePreviewHelper.class.getClassLoader());
    }

    Class<LivePreviewLanguage> createClass() throws Exception {
      int index = myCounter.incrementAndGet();
      String className = LivePreviewLanguage.class.getName() + "$$_" + index;
      byte[] b = dump(className);
      return (Class<LivePreviewLanguage>)defineClass(className, b, 0, b.length);
    }

    public static byte[] dump(String className) {
      ClassWriter cw = new ClassWriter(0);
      MethodVisitor mv;

      cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className.replace(".", "/"), null,
               "org/intellij/grammar/livePreview/LivePreviewLanguage", null);
      {
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/intellij/grammar/psi/BnfFile;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "org/intellij/grammar/livePreview/LivePreviewLanguage", "<init>",
                           "(Lorg/intellij/grammar/psi/BnfFile;)V", false);
        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "L" + className.replace(".", "/") + ";", null, l0, l1, 0);
        mv.visitLocalVariable("grammarFile", "Lorg/intellij/grammar/psi/BnfFile;", null, l0, l1, 1);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
      }
      cw.visitEnd();

      return cw.toByteArray();
    }
  }


}

