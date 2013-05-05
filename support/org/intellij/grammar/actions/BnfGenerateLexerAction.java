package org.intellij.grammar.actions;

import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.FileContentUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfReferenceOrToken;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

/**
 * @author greg
 */
public class BnfGenerateLexerAction extends AnAction {
  @Override
  public void update(AnActionEvent e) {
    PsiFile file = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    e.getPresentation().setEnabledAndVisible(file instanceof BnfFile);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final PsiFile file = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    if (!(file instanceof BnfFile)) return;

    final Project project = file.getProject();

    final BnfFile bnfFile = (BnfFile) file;
    final String flexFileName = getFlexFileName(bnfFile);

    Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(project, flexFileName, ProjectScope.getAllScope(project));
    VirtualFile firstItem = ContainerUtil.getFirstItem(files);

    final VirtualFileWrapper fileWrapper = FileChooserFactory.getInstance().createSaveFileDialog(new FileSaverDescriptor("Save JFlex Lexer", "", "flex"), project).
        save(firstItem != null ? firstItem.getParent() : null, firstItem != null ? firstItem.getName() : flexFileName);
    if (fileWrapper == null) return;
    final VirtualFile virtualFile = fileWrapper.getVirtualFile(true);
    if (virtualFile == null) return;

    new WriteCommandAction.Simple(project) {
      @Override
      protected void run() throws Throwable {
        try {
          PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile.getParent());
          assert psiDirectory != null;
          PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
          String packageName = aPackage == null ? null : aPackage.getQualifiedName();

          String text = generateLexerText(bnfFile, packageName);
          PsiFile psiFile = psiDirectory.findFile(flexFileName);
          if (psiFile == null) psiFile = psiDirectory.createFile("_" + flexFileName);

          FileContentUtil.setFileText(project, virtualFile, text);

          Notifications.Bus.notify(new Notification(BnfConstants.GENERATION_GROUP,
              psiFile.getName() + " generated", "to " + virtualFile.getParent().getPath(),
              NotificationType.INFORMATION), project);

        }
        catch (final IncorrectOperationException e) {
          ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
              Messages.showErrorDialog(project, "Unable to create file " + flexFileName + "\n" + e.getLocalizedMessage(), "Create JFlex Lexer");
            }
          });
        }
      }
    }.execute();

  }

  private String generateLexerText(final BnfFile bnfFile, @Nullable String packageName) {
    Map<String,String> tokenMap = RuleGraphHelper.getTokenMap(bnfFile);

    final int[] maxLen = {"{WHITE_SPACE}".length()};
    final Set<String> simpleTokens = new TreeSet<String>();
    Map<String, String> regexpTokens = new LinkedHashMap<String, String>();
    for (String token : tokenMap.keySet()) {
      String name = tokenMap.get(token).toUpperCase(Locale.ENGLISH);
      if (ParserGeneratorUtil.isRegexpToken(token)) {
        regexpTokens.put(name, ParserGeneratorUtil.getRegexpTokenRegexp(token));
      }
      else {
        simpleTokens.add(name);
      }
      maxLen[0] = Math.max(name.length() + 2, maxLen[0]);
    }

    bnfFile.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        String text = element instanceof BnfReferenceOrToken? element.getText() : null;
        if (text != null && bnfFile.getRule(text) == null) {
          simpleTokens.add(text.toUpperCase(Locale.ENGLISH));
          maxLen[0] = Math.max(text.length(), maxLen[0]);
        }
        super.visitElement(element);
      }
    });

    FileTemplateUtil.class.hashCode(); // init shared velocity instance
    VelocityContext context = new VelocityContext();
    context.put("lexerClass", getLexerName(bnfFile));
    context.put("packageName", StringUtil.notNullize(packageName, StringUtil.getPackageName(getRootAttribute(bnfFile, KnownAttribute.PARSER_CLASS))));
    context.put("tokenPrefix", getRootAttribute(bnfFile, KnownAttribute.ELEMENT_TYPE_PREFIX));
    context.put("typesClass", getRootAttribute(bnfFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS));
    context.put("tokenPrefix", getRootAttribute(bnfFile, KnownAttribute.ELEMENT_TYPE_PREFIX));
    context.put("simpleTokens", simpleTokens);
    context.put("regexpTokens", regexpTokens);
    context.put("StringUtil", StringUtil.class);
    context.put("maxTokenLength", maxLen[0]);

    StringWriter out = new StringWriter();
    Velocity.evaluate(context, out, "lexer.flex.template", new InputStreamReader(getClass().getResourceAsStream("/templates/lexer.flex.template")));
    return out.toString();
  }

  static String getFlexFileName(BnfFile bnfFile) {
    return getLexerName(bnfFile) + ".flex";
  }

  private static String getLexerName(BnfFile bnfFile) {
    return "_" + BnfGenerateParserUtilAction.getGrammarName(bnfFile);
  }

}
