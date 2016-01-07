/*
 * Copyright 2011-2014 Gregory Shrago
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

package org.intellij.grammar.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
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
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.NullLogChute;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.Case;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfReferenceOrToken;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    FileSaverDescriptor descriptor = new FileSaverDescriptor("Save JFlex Lexer", "", "flex");
    VirtualFile baseDir = firstItem != null ? firstItem.getParent() : bnfFile.getVirtualFile().getParent();
    VirtualFileWrapper fileWrapper = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project).
      save(baseDir, firstItem != null ? firstItem.getName() : flexFileName);
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

          FileContentUtil.setFileText(project, virtualFile, text);

          Notifications.Bus.notify(new Notification(BnfConstants.GENERATION_GROUP,
              virtualFile.getName() + " generated", "to " + virtualFile.getParent().getPath(),
              NotificationType.INFORMATION), project);

          associateFileTypeAndNavigate(project, virtualFile);
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

  private static void associateFileTypeAndNavigate(Project project, VirtualFile virtualFile) {
    String extension = virtualFile.getExtension();
    FileTypeManagerEx fileTypeManagerEx = FileTypeManagerEx.getInstanceEx();
    if (extension != null && fileTypeManagerEx.getFileTypeByExtension(extension) == FileTypes.UNKNOWN) {
      fileTypeManagerEx.associateExtension(StdFileTypes.PLAIN_TEXT, "flex");
    }
    FileEditorManager.getInstance(project).openFile(virtualFile, false, true);
    //new OpenFileDescriptor(project, virtualFile).navigate(false);
  }

  private String generateLexerText(final BnfFile bnfFile, @Nullable String packageName) {
    Map<String,String> tokenMap = RuleGraphHelper.getTokenMap(bnfFile);

    final int[] maxLen = {"{WHITE_SPACE}".length()};
    final Map<String, String> simpleTokens = new LinkedHashMap<String, String>();
    final Map<String, String> regexpTokens = new LinkedHashMap<String, String>();
    for (String token : tokenMap.keySet()) {
      String name = tokenMap.get(token);
      if (name == null) continue;
      String pattern = token2JFlex(token);
      boolean isRE = ParserGeneratorUtil.isRegexpToken(token);
      (isRE ? regexpTokens : simpleTokens).put(Case.UPPER.apply(name), pattern);
      maxLen[0] = Math.max((isRE ? name : pattern).length() + 2, maxLen[0]);
    }

    bnfFile.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof BnfAttrs) return;

        if (GrammarUtil.isExternalReference(element)) return;
        String text = element instanceof BnfReferenceOrToken? element.getText() : null;
        if (text != null && bnfFile.getRule(text) == null) {
          String name = Case.UPPER.apply(text);
          if (!simpleTokens.containsKey(name) && !regexpTokens.containsKey(name)) {
            simpleTokens.put(name, text2JFlex(text, false));
            maxLen[0] = Math.max(text.length(), maxLen[0]);
          }
        }
        super.visitElement(element);
      }
    });

    VelocityEngine ve = new VelocityEngine();
    ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
    ve.init();
    
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
    ve.evaluate(context, out, "lexer.flex.template", new InputStreamReader(getClass().getResourceAsStream("/templates/lexer.flex.template")));
    return StringUtil.convertLineSeparators(out.toString());
  }

  @NotNull
  public static String token2JFlex(@NotNull String tokenText) {
    if (ParserGeneratorUtil.isRegexpToken(tokenText)) {
      return javaPattern2JFlex(ParserGeneratorUtil.getRegexpTokenRegexp(tokenText));
    }
    else {
      return text2JFlex(tokenText, false);
    }
  }

  private static String javaPattern2JFlex(String javaRegexp) {
    Matcher m = Pattern.compile("\\[(?:[^]\\\\]|\\\\.)*\\]").matcher(javaRegexp);
    int start = 0;
    StringBuilder sb = new StringBuilder();
    while (m.find(start)) {
      sb.append(text2JFlex(javaRegexp.substring(start, m.start()), true));
      // escape only double quotes inside character class [..]
      sb.append(javaRegexp.substring(m.start(), m.end()).replaceAll("\"", "\\\\\""));
      start = m.end();
    }
    sb.append(text2JFlex(javaRegexp.substring(start), true));
    return sb.toString();
  }

  private static String text2JFlex(String text, boolean isRegexp) {
    String s;
    if (!isRegexp) {
      s = text.replaceAll("(\"|\\\\)", "\\\\$1");
      return s;
    }
    else {
      String spaces = " \\\\t\\\\n\\\\x0B\\\\f\\\\r";
      s = text.replaceAll("\"", "\\\\\"");
      s = s.replaceAll("(/+)", "\"$1\"");
      s = s.replaceAll("\\\\d", "[0-9]");
      s = s.replaceAll("\\\\D", "[^0-9]");
      s = s.replaceAll("\\\\s", "[" + spaces + "]");
      s = s.replaceAll("\\\\S", "[^" + spaces + "]");
      s = s.replaceAll("\\\\w", "[a-zA-Z_0-9]");
      s = s.replaceAll("\\\\W", "[^a-zA-Z_0-9]");
      s = s.replaceAll("\\\\p\\{Space\\}", "[" + spaces + "]");
      s = s.replaceAll("\\\\p\\{Digit\\}", "[:digit:]");
      s = s.replaceAll("\\\\p\\{Alpha\\}", "[:letter:]");
      s = s.replaceAll("\\\\p\\{Lower\\}", "[:lowercase:]");
      s = s.replaceAll("\\\\p\\{Upper\\}", "[:uppercase:]");
      s = s.replaceAll("\\\\p\\{Alnum\\}", "([:letter:]|[:digit:])");
      s = s.replaceAll("\\\\p\\{ASCII\\}", "[\\x00-\\x7F]");
      return s;
    }
  }

  static String getFlexFileName(BnfFile bnfFile) {
    return getLexerName(bnfFile) + ".flex";
  }

  private static String getLexerName(BnfFile bnfFile) {
    return "_" + BnfGenerateParserUtilAction.getGrammarName(bnfFile) + "Lexer";
  }

}
