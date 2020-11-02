/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.codeInsight.daemon.impl.quickfix.CreateClassKind;
import com.intellij.codeInsight.intention.impl.CreateClassDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Consumer;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author greg
 */
public class BnfGenerateParserUtilAction extends AnAction {
  @Override
  public void update(AnActionEvent e) {
    PsiFile file = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    if (!(file instanceof BnfFile)) {
      e.getPresentation().setEnabledAndVisible(false);
    }
    else {
      boolean enabled = ((BnfFile) file).findAttribute(null, KnownAttribute.PARSER_UTIL_CLASS, null) == null;
      e.getPresentation().setEnabledAndVisible(enabled);
    }
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    PsiFile file = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    if (!(file instanceof BnfFile)) return;

    Project project = file.getProject();
    BnfFile bnfFile = (BnfFile) file;
    final String qualifiedName = createClass(
        bnfFile, "Create Parser Util Class", BnfConstants.GPUB_CLASS,
        getGrammarName(bnfFile) + "ParserUtil",
        getGrammarPackage(bnfFile));
    if (qualifiedName == null) return;

    final int anchorOffset;
    final String text;
    String definition = "\n  " + KnownAttribute.PARSER_UTIL_CLASS.getName() + "=\"" + qualifiedName + "\"";
    BnfAttr attrParser = bnfFile.findAttribute(null, KnownAttribute.PARSER_CLASS, null);
    if (attrParser == null) {
      BnfAttrs rootAttrs = ContainerUtil.getFirstItem(bnfFile.getAttributes());
      if (rootAttrs == null) {
        anchorOffset = 0;
        text = "{" + definition + "\n}";
      }
      else {
        anchorOffset = rootAttrs.getFirstChild().getTextOffset();
        text = definition;
      }
    }
    else {
      anchorOffset = attrParser.getTextRange().getEndOffset();
      text = definition;
    }
    final Document document = PsiDocumentManager.getInstance(project).getDocument(bnfFile);
    if (document == null) return;
    WriteCommandAction.writeCommandAction(project, file)
      .run(() -> {
        int position = document.getLineEndOffset(document.getLineNumber(anchorOffset));
        document.insertString(position, text);
      });
  }

  static String getGrammarPackage(BnfFile bnfFile) {
    String value = bnfFile.findAttributeValue(null, KnownAttribute.PARSER_CLASS, null);
    return value == null ? "" : StringUtil.getPackageName(value);
  }

  static String getGrammarName(BnfFile bnfFile) {
    String parser = bnfFile.findAttributeValue(null, KnownAttribute.PARSER_CLASS, null);
    if (!KnownAttribute.PARSER_CLASS.getDefaultValue().equals(parser)) {
      String shortName = parser == null ? "" : StringUtil.getShortName(parser);
      int len = "Parser".length();
      String result = shortName.endsWith("Parser") ? shortName.substring(0, shortName.length() - len) : shortName;
      if (StringUtil.isNotEmpty(result)) return result;
    }
    return StringUtil.capitalize(FileUtil.getNameWithoutExtension(bnfFile.getName()));
  }

  public static String createClass(@NotNull PsiFile origin,
                                   @NotNull final String title,
                                   @Nullable final String baseClass,
                                   @NotNull String suggestedName,
                                   @NotNull String suggestedPackage) {
    Project project = origin.getProject();
    Module module = ModuleUtilCore.findModuleForPsiElement(origin);
    CreateClassDialog dialog = new CreateClassDialog(project, title, suggestedName, suggestedPackage, CreateClassKind.CLASS, true, module);
    if (!dialog.showAndGet()) return null;

    final String className = dialog.getClassName();
    final PsiDirectory targetDirectory = dialog.getTargetDirectory();
    return createClass(className, targetDirectory, baseClass, title, null);
  }

  static String createClass(String className, PsiDirectory targetDirectory,
                            String baseClass,
                            String title,
                            Consumer<PsiClass> consumer) {
    Project project = targetDirectory.getProject();
    Ref<PsiClass> resultRef = Ref.create();
    WriteCommandAction.writeCommandAction(project)
      .withName(title)
      .run(() -> {
        IdeDocumentHistory.getInstance(project).includeCurrentPlaceAsChangePlace();

        PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiJavaCodeReferenceElement ref = baseClass == null ? null : elementFactory.createReferenceElementByFQClassName(
          baseClass, GlobalSearchScope.allScope(project));

        try {
          PsiClass resultClass = JavaDirectoryService.getInstance().createClass(targetDirectory, className);
          resultRef.set(resultClass);
          if (ref != null) {
            PsiElement clazz = ref.resolve();
            boolean isInterface = clazz instanceof PsiClass && ((PsiClass)clazz).isInterface();
            PsiReferenceList targetReferenceList = isInterface ? resultClass.getImplementsList() : resultClass.getExtendsList();
            assert targetReferenceList != null;
            targetReferenceList.add(ref);
          }
          if (consumer != null) {
            consumer.consume(resultClass);
          }
        }
        catch (final IncorrectOperationException e) {
          ApplicationManager.getApplication().invokeLater(
            () -> Messages.showErrorDialog(project, "Unable to create class " + className + "\n" + e.getLocalizedMessage(), title));
        }
      });
    return resultRef.isNull() ? null : resultRef.get().getQualifiedName();
  }
}
