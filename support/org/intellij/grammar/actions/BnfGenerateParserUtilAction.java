package org.intellij.grammar.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfFile;

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
    final String qualifiedName = JavaHelper.getJavaHelper(project).createClass(
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
    new WriteCommandAction.Simple(project, file) {
      @Override
      protected void run() throws Throwable {
        int position = document.getLineEndOffset(document.getLineNumber(anchorOffset));
        document.insertString(position, text);
      }
    }.execute();

  }

  static String getGrammarPackage(BnfFile bnfFile) {
    return StringUtil.getPackageName(bnfFile.findAttributeValue(null, KnownAttribute.PARSER_CLASS, null));
  }

  static String getGrammarName(BnfFile bnfFile) {
    String parser = bnfFile.findAttributeValue(null, KnownAttribute.PARSER_CLASS, null);
    if (!KnownAttribute.PARSER_CLASS.getDefaultValue().equals(parser)) {
      String shortName = StringUtil.getShortName(parser);
      int len = "Parser".length();
      String result = shortName.endsWith("Parser") ? shortName.substring(0, shortName.length() - len) : shortName;
      if (StringUtil.isNotEmpty(result)) return result;
    }
    return StringUtil.capitalize(FileUtil.getNameWithoutExtension(bnfFile.getName()));
  }

}
