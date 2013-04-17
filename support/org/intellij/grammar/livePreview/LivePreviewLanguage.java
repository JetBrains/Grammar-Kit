/*
 * Copyright 2011-2013 Gregory Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.grammar.livePreview;

import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.asm.*;
import static org.objectweb.asm.Opcodes.*;


/**
 * @author gregsh
 */
public class LivePreviewLanguage extends Language {

  private final VirtualFilePointer myFilePointer;
  public static final Language BASE_INSTANCE = new Language("BNF_LP") {
    @Override
    public String getDisplayName() {
      return "Grammar Live Preview";
    }
  };

  private static final MyClassLoader ourClassLoader = new MyClassLoader();

  protected LivePreviewLanguage(@NotNull VirtualFile grammarFile) {
    super(BASE_INSTANCE, grammarFile.getPath());
    myFilePointer = VirtualFilePointerManager.getInstance().create(
      grammarFile, ApplicationManager.getApplication(), null);
  }

  @Override
  public String getDisplayName() {
    VirtualFile file = getGrammarFile();
    return file == null ? getID() : "'" + file.getName() + "' grammar";
  }

  @Nullable
  public VirtualFile getGrammarFile() {
    return myFilePointer.getFile();
  }

  @Nullable
  public BnfFile getGrammar(@Nullable Project project) {
    VirtualFile file = project == null? null : getGrammarFile();
    PsiFile psiFile = file == null? null : PsiManager.getInstance(project).findFile(file);
    return psiFile instanceof BnfFile? (BnfFile)psiFile : null;
  }

  public static LivePreviewLanguage newInstance(VirtualFile vFile) {
    try {
      return (LivePreviewLanguage)ourClassLoader.createClass().getDeclaredConstructors()[0].newInstance(vFile);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static class MyClassLoader extends ClassLoader {
    private final AtomicInteger myCounter = new AtomicInteger();

    MyClassLoader() {
      super(LivePreviewHelper.class.getClassLoader());
    }

    Class<LivePreviewLanguage> createClass() throws Exception {
      int index = myCounter.incrementAndGet();
      byte[] b = dump(index);
      return (Class<LivePreviewLanguage>)defineClass(LivePreviewLanguage.class.getName() + index, b, 0, b.length);
    }

    public static byte[] dump(int index) throws Exception {
      // TG there's ASM Bytecode plugin!
      ClassWriter cw = new ClassWriter(0);
      FieldVisitor fv;
      MethodVisitor mv;
      AnnotationVisitor av0;

      cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, "org/intellij/grammar/livePreview/LivePreviewLanguage"+index, null,
               "org/intellij/grammar/livePreview/LivePreviewLanguage", null);

      cw.visitSource("LivePreviewLanguage1.java", null);

      {
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lcom/intellij/openapi/vfs/VirtualFile;)V", null, null);
        {
          av0 = mv.visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false);
          av0.visitEnd();
        }
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 1);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNONNULL, l1);
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn(
          "Argument 0 for @NotNull parameter of org/intellij/grammar/livePreview/LivePreviewLanguage1.<init> must not be null");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        mv.visitLabel(l1);
        mv.visitLineNumber(11, l1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "org/intellij/grammar/livePreview/LivePreviewLanguage", "<init>",
                           "(Lcom/intellij/openapi/vfs/VirtualFile;)V");
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLineNumber(12, l2);
        mv.visitInsn(RETURN);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLocalVariable("this", "Lorg/intellij/grammar/livePreview/LivePreviewLanguage1;", null, l0, l3, 0);
        mv.visitLocalVariable("grammarFile", "Lcom/intellij/openapi/vfs/VirtualFile;", null, l0, l3, 1);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
      }
      cw.visitEnd();

      return cw.toByteArray();
    }
  }


}

