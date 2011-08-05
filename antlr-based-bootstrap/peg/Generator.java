/*
 * Copyright 2011-2011 Gregory Shrago
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

package peg;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.MultiMap;
import org.antlr.runtime.tree.Tree;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static peg.GeneratorUtil.*;
import static peg.GrammarParser.*;

/*
Known attributes:
parserClass - inheritable, with splitting support

<token> = <token-text>
extends
implements

mixin (rule)
pin (rule)
recoverRoot (rule)
external (rule)

 */

/*
 * todo ?? Grammar preprocessing: Left recursion -> suffix
 *
 * todo tree parsing - for big files support
 * todo rewrite generator on BnfPsi, get rid of antlr, support "external" modifier
 * todo build test plugin
 */
public class Generator {
    public static final String IELEMENTTYPE_CLASS = "com.intellij.psi.tree.IElementType";
    private final Map<String, Tree> ruleMap = new TreeMap<String, Tree>();
    private final Map<String, String> ruleParserClasses = new TreeMap<String, String>();
    private final Set<String> simpleTokens = new HashSet<String>();
    private final Tree treeRoot;
    private String rootPath;
    private final String grammarRoot;
    private PrintWriter out;

    public Generator(Tree tree, String path) {
        treeRoot = tree;
        rootPath = path;
        final List<Tree> rules = Rule.list(tree);
        grammarRoot = rules.isEmpty()? null : Rule.name(rules.get(0));
        for (Tree r : rules) {
            ruleMap.put(Rule.name(r), r);
            ruleParserClasses.put(Rule.name(r), getAttribute(r, "parserClass", "generated.Parser"));
        }
    }

    public void out(int level, String s) {
        if (level > 0) {
            out.print(StringUtil.repeat("  ",  level));
        }
        out.println(s);
    }

    private void newLine() {
        out(0, "");
    }

    public void generate() throws IOException {
        {
            for (String className : ruleParserClasses.values()) {
                TreeMap<String, Tree> map = new TreeMap<String, Tree>();
                for (String ruleName : ruleMap.keySet()) {
                    if (className.equals(ruleParserClasses.get(ruleName))) {
                        map.put(ruleName, ruleMap.get(ruleName));
                    }
                }
                File parserFile = new File(rootPath + File.separatorChar + className.replace('.', File.separatorChar) + ".java");
                parserFile.getParentFile().mkdirs();
                out = new PrintWriter(new FileOutputStream(parserFile));
                try {
                    generateParser(className, map);
                } finally {
                    out.close();
                }
            }
        }
        {
            String className = getAttribute(treeRoot, "elementTypeHolderClass", "generated.ParserTypes");
            File parserFile = new File(rootPath+File.separatorChar +className.replace('.', File.separatorChar)+".java");
            parserFile.getParentFile().mkdirs();
            out = new PrintWriter(new FileOutputStream(parserFile));
            try {
                generateElementTypesHolder(className);
            }
            finally {
                out.close();
            }
        }
        Map<String, String> infClasses = new HashMap<String, String>();
        GraphHelper graphHelper = new GraphHelper(ruleMap);
        for (String ruleName : ruleMap.keySet()) {
            Tree rule = ruleMap.get(ruleName);
            if (Rule.isPrivate(rule)) continue;
            String psiPackage = getAttribute(treeRoot, "psiPackage", "generated.psi");
            String psiClass = psiPackage +"."+getRulePsiClassName(rule, ruleName, true);
            infClasses.put(ruleName, psiClass);
            File psiFile = new File(rootPath + File.separatorChar + psiClass.replace('.', File.separatorChar) + ".java");
            psiFile.getParentFile().mkdirs();
            out = new PrintWriter(new FileOutputStream(psiFile));
            try {
                generatePsiIntf(rule, psiClass, getSuperClassName(rule, true, psiPackage, ""), graphHelper);
            } finally {
                out.close();
            }
        }
        for (String ruleName : ruleMap.keySet()) {
            Tree rule = ruleMap.get(ruleName);
            if (Rule.isPrivate(rule)) continue;
            String psiPackage = getAttribute(treeRoot, "psiImplPackage", "generated.psi.impl");
            String suffix = getAttribute(treeRoot, "psiImplClassSuffix", "Impl");
            String psiClass = psiPackage +"."+getRulePsiClassName(rule, ruleName, true) + suffix;
            File psiFile = new File(rootPath + File.separatorChar + psiClass.replace('.', File.separatorChar) + ".java");
            psiFile.getParentFile().mkdirs();
            out = new PrintWriter(new FileOutputStream(psiFile));
            try {
                generatePsiImpl(rule, psiClass, infClasses.get(ruleName), getSuperClassName(rule, false, psiPackage, suffix), graphHelper);
            } finally {
                out.close();
            }
        }

    }

    @Nullable
    private String getSuperClassName(Tree rule, boolean intf, String psiPackage, String suffix) {
        String superRuleName = getAttribute(rule, "extends", null);
        Tree superRule = superRuleName == null? null : ruleMap.get(superRuleName);
        if (superRule == null) return intf? getAttribute(rule, "implements", (String)null) : superRuleName;
        return psiPackage +"." +getRulePsiClassName(superRule, Rule.name(superRule), true) + suffix;
    }

    private static String getRulePsiClassName(Tree rule, String ruleName, boolean withPrefix) {
        StringBuilder sb = new StringBuilder();
        if (withPrefix) {
            sb.append(getAttribute(rule, "psiClassPrefix", ""));
        }
        for (String s : ruleName.split("_")) {
            if (s.length() == 0) continue;
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    public void generateParser(String parserClass, Map<String, Tree> ruleMap) {
        final String elementTypeHolderClass = getAttribute(treeRoot, "elementTypeHolderClass", "generated.ParserTypes");
        final String stubParser = getAttribute(treeRoot, "stubParserClass", "generated.ParserUtil");
        int offset = 1;
        String rootParserClass = ruleParserClasses.get(grammarRoot);
        boolean rootParser = parserClass.equals(rootParserClass);
        generateClassHeader(parserClass,
                "org.jetbrains.annotations.*;" +
                        "com.intellij.lang.LighterASTNode;" +
                        "com.intellij.lang.PsiBuilder;" +
                        "com.intellij.lang.PsiBuilder.Marker;" +
                        "com.intellij.openapi.diagnostic.Logger;" +
                        "static " + elementTypeHolderClass+".*;" +
                        "static " + stubParser+".*;"+
                (!rootParser? "static "+rootParserClass+".recursion_guard_;" +
                        "static " + rootParserClass + ".type_extends_;":
                        IELEMENTTYPE_CLASS + ";" +
                        "com.intellij.lang.ASTNode;" +
                        "com.intellij.psi.tree.TokenSet;" +
                        "com.intellij.lang.PsiParser;")
                , "@SuppressWarnings({\"SimplifiableIfStatement\", \"UnusedAssignment\"})",
                false, "", rootParser? "PsiParser": "");

        out(offset, "public static Logger LOG_ = Logger.getInstance(\""+parserClass+"\");");
        newLine();

        if (rootParser) {
            out(offset, "@NotNull");
            out(offset++, "public ASTNode parse(final IElementType root_, final PsiBuilder builder_) {");
            out(offset, "boolean result_;");
            boolean first = true;
            for (String ruleName : ruleMap.keySet()) {
                Tree rule = ruleMap.get(ruleName);
                if (Rule.isPrivate(rule) || grammarRoot.equals(ruleName)) continue;
                String elementType = getElementType(rule);
                out(offset++, (first?"" : "else ") + "if (root_ == "+elementType +") {");
                out(offset, "result_ = "+ruleName+"(builder_, false, 0);");
                out(--offset, "}");
                if (first) first = false;
            }
            if (first) {
                out(offset, "result_ = " + grammarRoot+"(builder_, false, 0);");
            }
            else {
                out(offset++, "else {");
                out(offset, "Marker marker_ = builder_.mark();");
                out(offset++, "try {");
                out(offset, "result_ = " + grammarRoot + "(builder_, false, 0);");
                out(offset++, "while (builder_.getTokenType() != null) {");
                out(offset, "builder_.advanceLexer();");
                out(--offset, "}");
                out(--offset, "}");
                out(offset++, "finally {");
                out(offset, "marker_.done(root_);");
                out(--offset, "}");
                out(--offset, "}");
            }
            out(offset, "return builder_.getTreeBuilt();");
            out(--offset, "}");
            newLine();
            int maxRecursionLevel = getAttribute(treeRoot, "maxRecursionLevel", 100);
            out(offset++, "public static boolean recursion_guard_(PsiBuilder builder_, int level_, String funcName_) {");
            out(offset++, "if (level_ > " + maxRecursionLevel + ") {");
            out(offset, "builder_.error(\"Maximum recursion level (\"+"+maxRecursionLevel+"+\") reached in\"+funcName_);");
            out(offset, "return false;");
            out(--offset, "}");
            out(offset, "return true;");
            out(--offset, "}");
            newLine();

            MultiMap<String, String> extendsMap = new MultiMap<String, String>();
            for (String ruleName : ruleMap.keySet()) {
                Tree rule = ruleMap.get(ruleName);
                if (Rule.isPrivate(rule)) continue;
                Tree superRule = ruleMap.get(Rule.attribute(rule, "extends", ""));
                if (superRule == null) continue;
                extendsMap.putValue(getElementType(superRule), getElementType(rule));
            }
            out(offset++, "private static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {");
            for (String ruleName : extendsMap.keySet()) {
                extendsMap.putValue(ruleName, ruleName); // add super to itself
                out(offset, "TokenSet.create("+StringUtil.join(extendsMap.get(ruleName), ",")+"),");
            }
            out(--offset, "};");
            out(offset++, "public static boolean type_extends_(IElementType child_, IElementType parent_) {");
            out(offset++, "for (TokenSet set : EXTENDS_SETS_) {");
            out(offset, "if (set.contains(child_) && set.contains(parent_)) return true;");
            out(--offset, "}");
            out(offset, "return false;");
            out(--offset, "}");
            newLine();
        }
        for (String ruleName : ruleMap.keySet()) {
            Tree rule = ruleMap.get(ruleName);
            out(offset, "/* ********************************************************** */");
            generateNode(offset, rule, Rule.body(rule), Rule.isPrivate(rule), ruleName, new HashSet<Tree>());
            newLine();
        }

        out(--offset, "}");
    }

    private void generateClassHeader(String parserClass, String imports, String annos, boolean intf, String... supers) {
        out(0, "package " + StringUtil.getPackageName(parserClass) +";");
        newLine();
        for (String s : imports.split(";")) {
            out(0, "import "+s+";");
        }
        newLine();
        StringBuilder sb = new StringBuilder();
        for (int i = 0, supersLength = supers.length; i < supersLength; i++) {
            String aSuper = supers[i];
            if (StringUtil.isEmpty(aSuper)) continue;
            if (i == 0) sb.append(" extends ").append(aSuper);
            else if (i == 1) sb.append(" implements ").append(aSuper);
            else sb.append(", ").append(aSuper);
        }
        if (StringUtil.isNotEmpty(annos)) {
            out(0, annos);
        }
        out(0, "public "+(intf?"interface ":"class ")+StringUtil.getShortName(parserClass)+sb.toString() +" {");
        newLine();
    }

    private void generateNode(int offset, Tree rule, Tree node, boolean shouldBePrivate, String funcName, HashSet<Tree> visited) {
        int type = node.getType();
        if (type == STRING || type == NUMBER || type == ID || !visited.add(node)) return;

        boolean isPrivate = shouldBePrivate || grammarRoot.equals(Rule.name(rule));
        out(offset, "// " +(isPrivate?"private":"")+ node.toStringTree());
        boolean isRule = node.getParent() == rule;
        boolean firstNonTrivial = node == Rule.firstNotTrivial(rule);
        final boolean recoverRoot = firstNonTrivial && Rule.attribute(rule, "recoverRoot", false);

        out(offset++, (isPrivate || !isRule ? "private " : "public ") + "static boolean " + funcName + "(PsiBuilder builder_, boolean optional_, int level_) {");
        if (node.getChildCount() == 0 && type == SEQ) {
            out(offset, "return true;");
            out(--offset, "}");
            return;
        }

        String debugFuncName = funcName; // + ":" + node.toStringTree();
        out(offset, "if (!recursion_guard_(builder_, level_, \"" + debugFuncName +"\")) return false;");
        List<Tree> children = getChildren(node);
        if (isTrivialNode(node)) {
            Tree child = node.getChild(0);
            out(offset, "return " + generateNodeCall(rule, child, getNextName(funcName, 0), "optional_") + ";");
            out(--offset, "}");
            newLine();
            generateNode(offset, rule, child, shouldBePrivate, getNextName(funcName, 0), visited);
            return;
        }

        final int pin = firstNonTrivial && type == SEQ? Rule.attribute(rule, "pin", children.size()) : children.size();
        if (!children.isEmpty() && (pin <= 0 || pin > children.size())) {
            throw new IllegalArgumentException("wrong pin in "+funcName+": "+ pin + " [1, "+children.size()+"]");
        }
        out(offset, "boolean result_ = " + (type == ZEROMORE || type == OPT) + ";");
        if (type == SEQ && pin > 0 && pin < children.size()) {
            out(offset, "boolean pinned_;");
        }
        if (!isPrivate && (type == SEQ || type == CHOICE || type == ONEMORE || type == ZEROMORE)) {
            out(offset, "final int start_ = builder_.getCurrentOffset();");
        }
        out(offset, "final Marker marker_ = builder_.mark();");
        out(offset++, "try {");

        if (type == CHOICE || recoverRoot) {
            out(offset, "enterErrorRecording(builder_, level_);");
        }

        for (int i = 0, childrenSize = children.size(); i < childrenSize; i++) {
            Tree child = children.get(i);

            boolean optional = type == OPT || type == ZEROMORE;
            String nodeCall = generateNodeCall(rule, child, getNextName(funcName, i), Boolean.toString(optional));
            switch (type) {
                case CHOICE: {
                    out(offset++, (i > 0? "if (!result_) ":"")+"{");
                    out(offset, "Marker m_ = builder_.mark();");
                    out(offset++, "try {");
                    out(offset, "result_ = " + nodeCall+";");
                    out(--offset, "}");
                    out(offset++, "finally {");
                    out(offset++, "if (!result_) {");
                    out(offset, "m_.rollbackTo();");
                    out(--offset, "}");
                    out(offset++, "else {");
                    out(offset, "m_.drop();");
                    out(--offset, "}");
                    out(--offset, "}");
                    out(--offset, "}");
                    break;
                }
                case SEQ: {
                    if (i == pin) out(offset, "pinned_ = result_;");
                    if (i >= pin) out(offset, "result_ = result_ && (" +nodeCall + " || pinned_);");
                    else if (i > 0) out(offset, "result_ = result_ && " + nodeCall + ";");
                    else out(offset, "result_ = "+nodeCall + ";");
                    break;
                }
                case OPT: {
                    out(offset, nodeCall + ";");
                    break;
                }
                case ONEMORE:
                    out(offset, "result_ = " + nodeCall + ";");
                    nodeCall = generateNodeCall(rule, child, getNextName(funcName, i), Boolean.toString(true));
                    // fall through
                case ZEROMORE: {
                    out(offset, "int offset_ = builder_.getCurrentOffset();");
                    out(offset++, "while (!builder_.eof() && " + nodeCall +") {");
                    out(offset++, "if (offset_ == builder_.getCurrentOffset()) {");
                    out(offset, "builder_.error(\"Empty element parsed in "+debugFuncName+"\");");
                    out(offset, "break;");
                    out(--offset, "}");
                    out(offset, "offset_ = builder_.getCurrentOffset();");
                    out(--offset, "}");
                    break;
                }
                case AND: {
                    out(offset, "result_ = " + nodeCall+";");
                    break;
                }
                case NOT: {
                    out(offset, "result_ = !" + nodeCall+";");
                    break;
                }
                default: throw new AssertionError(GrammarParser.tokenNames[type]);
            }
        }
        out(--offset, "}");
        out(offset++, "finally {");

        if (type == CHOICE && !recoverRoot) {
            out(offset, "result_ = exitErrorRecording(builder_, result_, level_, " + recoverRoot + ");");
        }
        if (!isPrivate) {
            String elementType = getElementType(rule);
            if (type == SEQ || type == CHOICE || type == ONEMORE || type == ZEROMORE) {
                out(offset, "LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;");
                out(offset++, "if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), "+elementType+")) {");
                out(offset, "marker_.drop();");
                out(--offset, "}");
                out(offset++, "else if (result_) {");
            }
            else {
                out(offset++, "if (result_) {");
            }
            out(offset, "marker_.done("+ elementType +");");
            out(--offset, "}");
            if (recoverRoot) {
                out(offset++, "else {");
                out(offset, "marker_.rollbackTo();");
                out(--offset, "}");
            }
            else {
                out(offset++, "else if (optional_) {");
                out(offset, "marker_.rollbackTo();");
                out(--offset, "}");
                out(offset++, "else {");
                out(offset, "marker_.drop();");
                out(--offset, "}");
            }
        }
        else {
            if (type == AND || type == NOT) {
                out(offset, "marker_.rollbackTo();");
            }
            else if (type != OPT && type != ZEROMORE) {
                out(offset++, "if (!result_ && optional_) {");
                out(offset, "marker_.rollbackTo();");
                out(--offset, "}");
                out(offset++, "else {");
                out(offset, "marker_.drop();");
                out(--offset, "}");
            }
            else {
                out(offset, "marker_.drop();");
            }
        }
        if (recoverRoot) {
            out(offset, "result_ = exitErrorRecording(builder_, result_, level_, " + recoverRoot + ");");
        }
        out(--offset, "}");
        out(offset, "return result_;");
        out(--offset, "}");
        newLine();
        for (int i = 0, childrenSize = children.size(); i < childrenSize; i++) {
            generateNode(offset, rule, children.get(i), true, getNextName(funcName, i), visited);
        }
    }

    private String generateNodeCall(Tree rule, Tree node, String nextName, String optional) {
        int type = node.getType();
        String text = node.getText();
        if (type == STRING) {
            String value = StringUtil.stripQuotesAroundValue(text);
            String attributeName = getAttributeName(rule, value);
            if (attributeName != null) {
                return generateConsumeToken(rule, attributeName, optional);
            }
            return generateConsumeTextToken(value, optional);
        }
        else if (type == NUMBER) {
            return generateConsumeTextToken(text, optional);
        }
        else if (type == ID) {
            Tree subRule = ruleMap.get(text);
            if (subRule != null) {
                String external = Rule.attribute(subRule, "external", null);
                String method;
                if (StringUtil.isNotEmpty(external)) {
                    method = external;
                }
                else {
                    method = Rule.name(subRule);
                    String parserClass = ruleParserClasses.get(method);
                    if (!parserClass.equals(ruleParserClasses.get(Rule.name(rule)))) {
                        method = StringUtil.getShortName(parserClass) + "." + method;
                    }
                }
                return method+"(builder_, " + optional+", level_ + 1)";
            }
            return generateConsumeToken(rule, text, optional);
        }
        else {
            return nextName+"(builder_, " + optional+", level_ + 1)";
        }
    }

    private String generateConsumeTextToken(String tokenText, String optional) {
        return "consumeToken(builder_, "+optional+", \""+tokenText+"\")";
    }

    private String generateConsumeToken(Tree rule, String tokenName, String optional) {
        simpleTokens.add(tokenName);
        return "consumeToken(builder_, " + optional + ", "+getElementType(rule, tokenName)+")";
    }

    private String getNextName(String funcName, int i) {
        return funcName + "_" + i;
    }

    private String getElementType(Tree rule) {
        return getElementType(rule,  Rule.name(rule));
    }

    private String getElementType(Tree rule, String token) {
        return getAttribute(rule, "elementTypePrefix", "") + token.toUpperCase();
    }

    /*ElementTypes******************************************************************/

    private void generateElementTypesHolder(String className) {
        String implPackage = getAttribute(treeRoot, "psiImplPackage", "generated.psi.impl");
        final String elementTypeClass = getAttribute(treeRoot, "elementTypeClass", IELEMENTTYPE_CLASS);
        final String tokenTypeClass = getAttribute(treeRoot, "tokenTypeClass", IELEMENTTYPE_CLASS);
        generateClassHeader(className,
                IELEMENTTYPE_CLASS+";" +
                "com.intellij.psi.PsiElement;" +
                "com.intellij.lang.ASTNode;" +
                elementTypeClass+";" +
                tokenTypeClass+";" +
                implPackage + ".*;", "", true);
        for (String ruleName : ruleMap.keySet()) {
            final Tree rule = ruleMap.get(ruleName);
            final boolean isPrivate = Rule.isPrivate(rule);
            if (isPrivate || grammarRoot.equals(ruleName)) continue;
            final String elementType = getElementType(rule);
            out(1, "public static final IElementType " + elementType + " = new "
                    + StringUtil.getShortName(elementTypeClass) + "(\"" + elementType + "\");");
        }
        newLine();
        for (String token : simpleTokens) {
            String name = getAttribute(treeRoot, token, token);
            out(1, "public static final IElementType " + getElementType(treeRoot, token) + " = new "
                    + StringUtil.getShortName(tokenTypeClass) + "(\"" + name + "\");");

        }
        newLine();
        out(1, "public static class Factory {");
        out(2, "public static PsiElement createElement(ASTNode node) {");
        out(3, "IElementType type = node.getElementType();");
        String suffix = getAttribute(treeRoot, "psiImplClassSuffix", "Impl");
        boolean first = true;
        for (String ruleName : ruleMap.keySet()) {
            final Tree rule = ruleMap.get(ruleName);
            final boolean isPrivate = Rule.isPrivate(rule);
            if (isPrivate || grammarRoot.equals(ruleName)) continue;
            String psiClass = getRulePsiClassName(rule, ruleName, true) + suffix;
            out(3, (!first?"else ":"")+" if (type == "+ getElementType(rule)+") {");
            out(4, "return new "+psiClass+"(node);");
            first = false;
            out(3, "}");
        }
        out(3, "throw new AssertionError(\"Unknown element type: \" + type);");

        out(2, "}");
        out(1, "}");
        out(0, "}");
    }


    /*PSI******************************************************************/
    private void generatePsiIntf(Tree rule, String psiClass, String superRuleClass, GraphHelper helper) {
        String psiSuper = superRuleClass;
        generateClassHeader(psiClass, "org.jetbrains.annotations.*;" +
                "java.util.List;" +
                "com.intellij.psi.PsiElement;" +
                psiSuper, "", true, StringUtil.getShortName(psiSuper));
        Map<Tree, GraphHelper.Cardinality> accessors = helper.getFor(rule);
        for (Tree tree : getSortedPublicRules(accessors.keySet())) {
            generatePsiAccessor(rule, tree, accessors.get(tree), true);
        }
        for (Tree tree : getSortedSimpleTokens(accessors.keySet())) {
            generatePsiAccessor(rule, tree, accessors.get(tree), true);
        }
        out(0, "}");
    }

    private void generatePsiImpl(Tree rule, String psiClass, String superInterface, String superRuleClass, GraphHelper helper) {
        String typeHolderClass = getAttribute(treeRoot, "elementTypeHolderClass", "generated.ParserTypes");
        // direct mixin attribute overrides "extends":
        String implSuper = Rule.attribute(rule, "mixin", null);
        if (implSuper == null) implSuper = superRuleClass;
        generateClassHeader(psiClass,
                "java.util.List;" +
                "org.jetbrains.annotations.*;" +
                "com.intellij.lang.ASTNode;" +
                "com.intellij.psi.PsiElement;" +
                "com.intellij.psi.util.PsiTreeUtil;" +
                "static " + typeHolderClass +".*;" +
                (StringUtil.isNotEmpty(implSuper)? implSuper+";":"") +
                StringUtil.getPackageName(superInterface)+".*", "", false, StringUtil.getShortName(implSuper),
                StringUtil.getShortName(superInterface));
        out(1, "public " + StringUtil.getShortName(psiClass) + "(ASTNode node) {");
        out(2, "super(node);");
        out(1, "}");
        newLine();
        Map<Tree, GraphHelper.Cardinality> accessors = helper.getFor(rule);
        for (Tree tree : getSortedPublicRules(accessors.keySet())) {
            generatePsiAccessor(rule, tree, accessors.get(tree), false);
        }
        for (Tree tree : getSortedSimpleTokens(accessors.keySet())) {
            generatePsiAccessor(rule, tree, accessors.get(tree), false);
        }
        out(0, "}");
    }

    private void generatePsiAccessor(Tree rule, Tree tree, GraphHelper.Cardinality type, boolean intf) {
        boolean token = !Rule.is(tree);

        boolean many = type == GraphHelper.Cardinality.AT_LEAST_ONE || type == GraphHelper.Cardinality.ANY_NUMBER;

        if (token && many) return;

        String ruleName;
        if (token) {
            if (tree.getType() == STRING) {
                String value = StringUtil.stripQuotesAroundValue(tree.getText());
                ruleName = getAttributeName(rule, value);
            }
            else if (tree.getType() == ID) {
                ruleName = tree.getText();
            }
            else ruleName = null; // do not bother generate numbers & simple literals
        }
        else {
            ruleName = Rule.name(tree);
        }
        if (ruleName == null) return;
        String getterNameBody = getRulePsiClassName(rule, ruleName, false);
        String getterName = "get" + getterNameBody + (many ? "List" : "");
        if (!intf) out(1, "@Override");
        if (type == GraphHelper.Cardinality.REQUIRED) {
            out(1, "@NotNull");
        } else if (type == GraphHelper.Cardinality.OPTIONAL) {
            out(1, "@Nullable");
        }
        else {
            out(1, "@NotNull");
        }
        String className = token? "PsiElement" : getRulePsiClassName(rule, Rule.name(tree), true);
        String tail = intf? "();" : "() {";
        out(1, "public "+(many?"List<":"") + className + (many ? "> " : " ") + getterName + tail);
        if (!intf) {
            if (token) {
                out(2, "ASTNode child = getNode().findChildByType("+getElementType(rule, ruleName)+");");
                out(2, "return child == null? null : child.getPsi();");
            }
            else {
                if (many) {
                    out(2, "return PsiTreeUtil.getChildrenOfTypeAsList(this, " + className + ".class);");
                }
                else {
                    out(2, "return PsiTreeUtil.getChildOfType(this, " + className + ".class);");
                }
            }
            out(1, "}");
        }
        newLine();
    }

    private Collection<Tree> getSortedPublicRules(Collection<Tree> accessors) {
        TreeMap<String, Tree> result = new TreeMap<String, Tree>();
        for (Tree tree : accessors) {
            if (!Rule.is(tree) || Rule.isPrivate(tree)) continue;
            result.put(Rule.name(tree), tree);
        }
        return result.values();
    }

    private Collection<Tree> getSortedSimpleTokens(Collection<Tree> accessors) {
        TreeMap<String, Tree> result = new TreeMap<String, Tree>();
        for (Tree tree : accessors) {
            int type = tree.getType();
            if (type == ID && simpleTokens.contains(tree.getText()) /*|| type == STRING || type == NUMBER*/) {
                result.put(tree.getText(), tree);
            }
        }
        return result.values();
    }


}
