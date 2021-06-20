/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.psi.impl;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.TreeTraversal;
import org.intellij.jflex.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gregsh
 */
public class JavaRefHelper {

  public static PsiReference[] getReferences(JFlexJavaCode o) {
    PsiElement parent = o.getParent();
    if (parent instanceof JFlexRule) {
      return getRuleReferences(o);
    }
    // JFlexUserCodeSection, JFlexOption, etc.
    return PsiReference.EMPTY_ARRAY;
  }

  private static final Pattern RETURN_PAT = Pattern.compile("return\\s+([^;}]+)");
  private static final Pattern YYBEGIN_PAT = Pattern.compile("yybegin\\s*\\(\\s*([^)]+)\\)");

  private static PsiReference @NotNull [] getRuleReferences(@NotNull JFlexJavaCode o) {
    String text = o.getText();
    List<PsiReference> list = new SmartList<>();
    {
      Matcher matcher = YYBEGIN_PAT.matcher(text);
      for (int offset = 0; matcher.find(offset); offset = matcher.end() + 1) {
        list.add(new StateRef(o, TextRange.create(matcher.start(1), matcher.end(1))));
      }
    }
    {
      Matcher matcher = RETURN_PAT.matcher(text);
      for (int offset = 0; matcher.find(offset); offset = matcher.end() + 1) {
        String refText = matcher.group(1);
        PsiFile javaFile = createJavaFileForExpr("return " + refText + ";", o);
        ContainerUtil.addAll(list, wrapJavaReferences(o, matcher.start(1), javaFile, refText));
      }
    }
    return list.isEmpty() ? PsiReference.EMPTY_ARRAY : list.toArray(PsiReference.EMPTY_ARRAY);
  }

  public static PsiReference @NotNull [] getReferences(@NotNull JFlexJavaType o) {
    String refText = o.getText();
    PsiFile javaFile = createJavaFileForExpr(refText + " val", o);
    return wrapJavaReferences(o, 0, javaFile, refText);
  }

  private static PsiReference[] wrapJavaReferences(@NotNull PsiElement o, int javaOffset, PsiFile javaFile, String targetText) {
    int start = javaFile.getText().lastIndexOf(targetText);
    int end = start + targetText.length();

    List<PsiReference> list = new SmartList<>();
    for (PsiElement e : SyntaxTraverser.psiTraverser(javaFile).traverse(TreeTraversal.LEAVES_BFS)) {
      TextRange r = e.getTextRange();
      if (!r.intersects(start, end)) continue;
      PsiReference ref = javaFile.findReferenceAt(r.getStartOffset());
      if (ref != null) {
        TextRange rr = ref.getRangeInElement();
        TextRange er = ref.getElement().getTextRange();
        list.add(new PsiReferenceBase<>(o, rr.shiftRight(javaOffset + er.getStartOffset() - start)) {
          @Override
          public @Nullable PsiElement resolve() {
            return ref.resolve();
          }

          @Override
          public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
            PsiElement e = getElement();
            String text = StringUtil.replaceSubstring(e.getText(), getRangeInElement(), newElementName);
            return e.replace(JFlexPsiElementFactory.createJavaTypeFromText(e.getProject(), text));
          }

          @Override
          public Object @NotNull [] getVariants() {
            return ArrayUtil.EMPTY_OBJECT_ARRAY;
          }
        });
      }
    }
    return list.isEmpty() ? PsiReference.EMPTY_ARRAY : list.toArray(PsiReference.EMPTY_ARRAY);
  }

  public static @NotNull PsiFile createJavaFileForExpr(String text, PsiElement context) {
    JFlexJavaCode userCode = SyntaxTraverser.psiTraverser(context.getContainingFile()).filter(JFlexJavaCode.class).first();
    String imports = userCode == null || !(userCode.getParent() instanceof JFlexUserCodeSection) ? "" : userCode.getText();

    String fileText = imports + "\n" + "class A { void f() { " + text + " } }";
    FileType javaType = FileTypeManager.getInstance().getFileTypeByExtension("java");
    return PsiFileFactory.getInstance(context.getProject()).createFileFromText("a.java", javaType, fileText);
  }
}
