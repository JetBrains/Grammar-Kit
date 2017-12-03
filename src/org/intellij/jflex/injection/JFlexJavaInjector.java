package org.intellij.jflex.injection;

import com.intellij.lang.ASTNode;
import com.intellij.lang.StdLanguages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.jflex.psi.*;
import org.intellij.jflex.psi.impl.JFlexFile;
import org.jetbrains.annotations.NotNull;

public class JFlexJavaInjector implements LanguageInjector {

    public static final String DEFCLASS = "Yylex";
    public static final String DEFTYPE = "int";

    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost _host, @NotNull InjectedLanguagePlaces registrar) {

        if (!(_host instanceof JFlexJavaCodeInjected)) return;

        JFlexJavaCodeInjected host = (JFlexJavaCodeInjected) _host;

        assert host.getContainingFile() instanceof JFlexFile;
        JFlexFile file = (JFlexFile) host.getContainingFile();

        JFlexJavaCodeInjected importSection = null;
        ASTNode user_code_sec = file.getNode().findChildByType(JFlexTypes.FLEX_USER_CODE_SECTION);
        if (user_code_sec != null) {
            importSection = (JFlexJavaCodeInjected) user_code_sec.findChildByType(JFlexTypes.FLEX_JAVA_CODE).getPsi();
        }
        //processing imports and package section
        if (host == importSection) {
            registrar.addPlace(StdLanguages.JAVA, new TextRange(0, host.getTextLength()), null, "\npublic class a{}");
            return;
        }

        //let's add some imports and package statements from flex file header
        StringBuilder prefix = new StringBuilder();

        if (importSection != null) {
            prefix.append(importSection.getText());
        }

        String classnamestr = getJavaOptions(file, JFlexTypes.FLEX_OPT_CLASS, DEFCLASS);

        String returntypestr = getJavaOptions(file, JFlexTypes.FLEX_OPT_TYPE, DEFTYPE);

        String implementedstr = getJavaOptions(file, JFlexTypes.FLEX_OPT_IMPLEMENTS, "");
        if (implementedstr.length() !=0 ) {
            implementedstr=" implements " + implementedstr;
        }

        prefix.append("\npublic class ").append(classnamestr).append(implementedstr).append("{");

        StringBuilder suffix = new StringBuilder();

        if (host.isMatchAction()) {
            prefix.append("public ").append(returntypestr).append(" yylex(){");
            suffix.append("}}");
        } else {
            suffix.append("}");
        }

        registrar.addPlace(StdLanguages.JAVA, new TextRange(0, host.getTextLength()), prefix.toString(), suffix.toString());
    }

    private String getJavaOptions(@NotNull JFlexFile file,@NotNull IElementType option_type, String defaultVal) {
        StringBuilder result = new StringBuilder();

        ASTNode dec_section = file.getNode().findChildByType(JFlexTypes.FLEX_DECLARATIONS_SECTION);
        if (dec_section != null) {
            for (ASTNode flex_option : dec_section.getChildren(TokenSet.create(JFlexTypes.FLEX_OPTION))) {
                if (flex_option.findChildByType( option_type ) != null) {
                    ASTNode[] nodes = flex_option.getChildren(TokenSet.create(JFlexTypes.FLEX_JAVA_TYPE));
                    for (ASTNode flex_java_type : nodes) {
                        result.append( flex_java_type.findChildByType(JFlexTypes.FLEX_ID).getPsi().getText());
                        if (flex_java_type != nodes[nodes.length-1]) {
                            result.append(",");
                        }
                    }
                    break;
                }
            }
        }
        return result.length()==0 ? defaultVal : result.toString();
    }


}