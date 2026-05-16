/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.containers.MultiMap;
import org.intellij.grammar.BnfPathsResolution;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.PsiHelperFactory;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getTokenType;
import static org.intellij.grammar.generator.RuleGraphHelper.hasElementType;
import static org.intellij.grammar.psi.BnfAst.getTokenTextToNameMap;
import static org.intellij.grammar.psi.BnfAttributes.getAttribute;
import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;
import static org.intellij.grammar.psi.BnfRules.getSynonymTargetOrSelf;

/**
 * Sealed base for the BNF-to-source code emitters. A {@code Generator} consumes a parsed
 * {@link BnfFile} together with its {@link GenOptions} and produces a parser (and, depending on
 * the target, PSI / element-type holders) by writing source files through {@link FilePrinter}
 * and {@link OutputOpener}.
 * <p>
 * This class owns target-agnostic concerns: locating rules, computing element types, building
 * the rule graph and FIRST/NEXT analysis, gathering token sets emitted for token-choice
 * expressions, and the shared printer / file-opening plumbing. Parser-specific concerns
 * (node emission, parser lambdas, FIRST checks, auto-recovery) live in
 * {@link ParserGenerator}; PSI emission lives in {@link JavaPsiGenerator}.
 * <p>
 * Subclasses are sealed to {@link ParserGenerator} and {@link JavaPsiGenerator}. Instances are
 * single-use: state accumulated during one {@link #generate()} call is not reset.
 */
public sealed abstract class Generator permits ParserGenerator, JavaPsiGenerator {
  private static final Logger LOG = Logger.getInstance(Generator.class);

  /**
   * The input BNF file to generate the parser from.
   */
  protected final @NotNull BnfFile myFile;
  protected final @NotNull BnfPathsResolution myPaths;

  /**
   * The package prefix to use for the generated parser.
   */
  protected final @NotNull String myPackagePrefix;
  protected final @NotNull String mySourcePath;
  protected final @Nullable String myGrammarRoot;
  protected final @Nullable String myGrammarRootParser;

  protected final @NotNull GenOptions G;
  protected final @NotNull NameRenderer R;
  final Names N;

  protected final JavaHelper myJavaHelper;
  private final @NotNull Map<KnownAttribute<?>, JavaHelper> myScopedHelpers = new HashMap<>();

  private final @NotNull String myOutputFileExtension;
  protected final @NotNull OutputOpener myOpener;
  protected NameShortener myShortener;
  private FilePrinter myPrinter;
  protected final RuleGraphHelper myGraphHelper;
  protected final BnfFirstNextAnalyzer myFirstNextAnalyzer;

  final @NotNull Map<String, RuleInfo> myRuleInfos = new TreeMap<>();

  /**
   * Collection of token sets corresponding to each of the token choice
   * expressions in the grammar.
   */
  protected final Map<String, Collection<String>> myChoiceTokenSets = new TreeMap<>();

  /**
   * Contains information regarding all the tokens in the grammar.
   * The entries are divided into two categories:
   * 1. Token entries based on the `tokens` attribute in the grammar.
   * These entries are *reversed* and map the token text to the token name.
   * 2.
   */
  protected final Map<String, String> mySimpleTokens;

  protected Generator(@NotNull BnfFile psiFile,
                      @NotNull String sourcePath,
                      @NotNull String packagePrefix,
                      @NotNull String outputFileExtension,
                      @NotNull OutputOpener outputOpener,
                      @NotNull NameRenderer nameRenderer,
                      @NotNull BnfPathsResolution paths) {
    myFile = psiFile;

    G = new GenOptions(psiFile);
    N = G.names;
    R = nameRenderer;
    mySourcePath = sourcePath;
    myPaths = paths;
    myPackagePrefix = packagePrefix;
    myOutputFileExtension = outputFileExtension;
    myOpener = outputOpener;

    myJavaHelper = JavaHelper.getJavaHelper(myFile);

    List<BnfRule> rules = psiFile.getRules();
    BnfRule rootRule = rules.isEmpty() ? null : rules.get(0);
    myGrammarRoot = rootRule == null ? null : rootRule.getName();
    myGrammarRootParser = rootRule == null ? null : getRootAttribute(rootRule, KnownAttribute.PARSER_CLASS);
    mySimpleTokens = new LinkedHashMap<>(getTokenTextToNameMap(myFile));
    myGraphHelper = RuleGraphHelper.getCached(psiFile);
    myFirstNextAnalyzer = BnfFirstNextAnalyzer.createAnalyzer(true);
  }

  /**
   * Runs the full generation pipeline for this target: emits the parser and any auxiliary
   * artifacts (element-type holders, PSI interfaces/impls, visitor) the target supports.
   */
  public abstract void generate() throws IOException;

  /** Emits the {@link KnownAttribute#CLASS_HEADER classHeader} preamble for {@code className}, if one is configured. */
  protected final void generateFileHeader(String className) {
    String header = getRootAttribute(myFile, KnownAttribute.CLASS_HEADER, className);
    String text = StringUtil.isEmpty(header) ? "" : getStringOrFile(header);
    if (StringUtil.isNotEmpty(text)) {
      out(text);
    }
    resetOffset();
  }

  /**
   * If the classHeader is a file path, loads the file content and returns it.
   * If it's a string representing a comment, returns it as is.
   * Otherwise, wraps the string in a comment.
   */
  private String getStringOrFile(String classHeader) {
    try {
      File file = new File(mySourcePath, classHeader);
      if (file.exists()) return FileUtil.loadFile(file);
    }
    catch (IOException ex) {
      LOG.error(ex);
    }
    return classHeader.startsWith("//") || classHeader.startsWith("/*")
           ? classHeader
           : StringUtil.countNewLines(classHeader) > 0
             ? "/*\n" + classHeader + "\n*/"
             :
             "// " + classHeader;
  }

  /**
   * Opens an output file for {@code className} under {@code basePath} (with {@link #myPackagePrefix}
   * stripped from the FQN), and installs a fresh {@link FilePrinter} as the current output sink.
   * Callers pass the resolved path attribute that owns the artifact, e.g.
   * {@code myPaths.pathString(KnownAttribute.PARSER_OUTPUT_PATH)} —
   * {@link BnfPathsResolution#pathString} throws if the attribute has no value, so a missing
   * directory surfaces at the call site rather than here.
   */
  protected void openOutput(@NotNull String className, @NotNull String basePath) throws IOException {
    String classNameAdjusted = myPackagePrefix.isEmpty() ? className : StringUtil.trimStart(className, myPackagePrefix + ".");
    File file = new File(basePath, classNameAdjusted.replace('.', File.separatorChar) + "." + myOutputFileExtension);
    PrintWriter output = myOpener.openOutput(className, file, myFile);
    myPrinter = new FilePrinter(output);
  }

  protected void closeOutput() {
    myPrinter.close();
  }

  /**
   * Returns a {@link JavaHelper} whose class-lookup scope is narrowed by the {@code *InputPath}
   * sibling of {@code attribute}, anchored at the BNF psi node that declares it. When the
   * attribute is not declared in the grammar, falls back to the rule (or file) — in which case
   * only the global {@code inputPath} default applies.
   */
  protected final @NotNull JavaHelper helperFor(@Nullable BnfRule rule, @NotNull KnownAttribute<?> attribute) {
    // BnfPaths.referencePath consults grammar-level (root) attributes; per-rule scoping is not
    // supported today. The rule parameter is retained for callers' clarity and future expansion.
    PsiHelperFactory factory = myFile.getProject().getService(PsiHelperFactory.class);
    if (factory == null) {
      // Headless / CLI: AsmHelper has no scope concept; one shared instance is sufficient.
      return myJavaHelper;
    }
    return myScopedHelpers.computeIfAbsent(attribute, attr -> factory.getInstance(myPaths, attr));
  }

  public void out(String s, Object... args) {
    myPrinter.out(s, args);
  }

  public void out(String s) {
    myPrinter.out(s);
  }

  public void newLine() {
    out("");
  }

  public @NotNull String shorten(@NotNull String s) {
    return myShortener.shorten(s);
  }

  /** Sets indent in the printer to zero. */
  protected void resetOffset() {
    myPrinter.resetOffset();
  }

  protected enum TypeKind {CLASS, INTERFACE, ABSTRACT_CLASS}

  /**
   * Returns the short names of generated PSI interface/impl classes that are reachable from
   * {@code packageName} via {@code imports} (own package or wildcard imports). The result feeds
   * the name shortener so that those generated classes are not accidentally short-named to clash.
   */
  protected @NotNull Set<String> collectClasses(@NotNull Set<String> imports, @NotNull String packageName) {
    Set<String> includedPackages = JBIterable.from(imports)
      .filter(o -> !o.startsWith("static") && o.endsWith(".*"))
      .map(o -> StringUtil.trimEnd(o, ".*"))
      .append(packageName).toSet();
    Set<String> includedClasses = new HashSet<>();
    for (RuleInfo info : myRuleInfos.values()) {
      if (includedPackages.contains(info.intfPackage)) includedClasses.add(StringUtil.getShortName(info.intfClass));
      if (includedPackages.contains(info.implPackage)) includedClasses.add(StringUtil.getShortName(info.implClass));
    }
    return includedClasses;
  }

  @NotNull RuleInfo ruleInfo(@NotNull BnfRule rule) {
    return Objects.requireNonNull(myRuleInfos.get(rule.getName()));
  }

  /**
   * Marks rules whose generated PSI impl should be {@code abstract}: rules without modifiers,
   * recovery, or hooks, that aren't reused as another rule's element type, aren't the grammar
   * root, and the rule graph reports as collapsible with no incoming references.
   */
  protected void calcAbstractRules() {
    final var reusedRules = new HashSet<String>();
    for (BnfRule rule : myFile.getRules()) {
      String elementType = getAttribute(rule, KnownAttribute.ELEMENT_TYPE);
      BnfRule r = elementType != null ? myFile.getRule(elementType) : null;
      if (r != null && r != rule) reusedRules.add(r.getName());
    }
    for (BnfRule rule : myFile.getRules()) {
      if (reusedRules.contains(rule.getName())) continue;
      if (myGrammarRoot.equals(rule.getName())) continue;
      if (!rule.getModifierList().isEmpty()) continue;
      if (getAttribute(rule, KnownAttribute.RECOVER_WHILE) != null) continue;
      if (!getAttribute(rule, KnownAttribute.HOOKS).isEmpty()) continue;

      if (myGraphHelper.canCollapse(rule) && myGraphHelper.getFor(rule).isEmpty()) {
        ruleInfo(rule).isAbstract = true;
      }
    }
  }

  /**
   * Maps an entry of a FIRST set (token name, quoted text, or rule reference marker) to the
   * generated element-type constant. Returns {@code null} for non-token entries (rules,
   * external markers like {@code <<…>>}) that have no concrete element type.
   */
  protected @Nullable String firstToElementType(String first) {
    if (first.startsWith("#") || first.startsWith("-") || first.startsWith("<<")) return null;
    String value = GrammarUtil.unquote(first);
    //noinspection StringEquality
    if (first != value) {
      String attributeName = getTokenName(value);
      if (attributeName != null && !first.startsWith("\"")) {
        return getElementType(attributeName);
      }
      return null;
    }
    return getElementType(first);
  }

  protected @Nullable String getTokenName(String value) {
    return mySimpleTokens.get(value);
  }

  protected final @NotNull String getElementType(String token) {
    return getTokenType(myFile, token, G.generateTokenCase);
  }

  protected final @NotNull String getElementType(BnfRule rule) {
    return CommonRendererUtils.getElementType(rule, G.generateElementCase);
  }

  /**
   * From the rule {@code extends} graph, builds the {@code EXTENDS_SETS_} groups: the element-type
   * sets the runtime uses to answer "is X-as-element-type also a Y?" Skips fake/synonym rules
   * with no own element type and drops smaller sets fully contained in larger ones.
   */
  protected @NotNull List<Set<String>> buildExtendsSet(@NotNull MultiMap<BnfRule, BnfRule> map) {
    if (map.isEmpty()) return Collections.emptyList();
    List<Set<String>> result = new ArrayList<>();
    for (Map.Entry<BnfRule, Collection<BnfRule>> entry : map.entrySet()) {
      Set<String> set = null;
      for (BnfRule rule : entry.getValue()) {
        RuleInfo ruleInfo = this.ruleInfo(rule);
        if (!hasElementType(rule)) continue;
        String elementType = ruleInfo.isFake && !ruleInfo.isInElementType ||
                             getSynonymTargetOrSelf(rule) != rule ? null : ruleInfo.elementType;
        if (StringUtil.isEmpty(elementType)) continue;
        if (set == null) set = new TreeSet<>();
        set.add(elementType);
      }
      if (set != null && set.size() > 1) result.add(set);
    }
    result.sort(Comparator.comparingInt(Set::size));
    for (ListIterator<Set<String>> it = result.listIterator(); it.hasNext(); ) {
      Set<String> smaller = it.next();
      for (Set<String> bigger : result.subList(it.nextIndex(), result.size())) {
        if (bigger.containsAll(smaller)) {
          it.remove();
          break;
        }
      }
    }
    return result;
  }

  /** Reports a generator warning — to {@code stdout} under tests, otherwise as an IDE notification. */
  public void addWarning(String text) {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      //noinspection UseOfSystemOutOrSystemErr
      System.out.println(text);
    }
    else {
      NotificationGroupManager.getInstance()
        .getNotificationGroup("grammarkit.parser.generator.log")
        .createNotification(text, MessageType.WARNING).notify(myFile.getProject());
    }
  }
}
