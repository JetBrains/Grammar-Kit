/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.fleet.FleetBnfFileImpl;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static java.lang.String.format;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.ParserGeneratorUtil.getPsiImplClassFormat;
import static org.intellij.grammar.generator.RuleGraphHelper.hasPsiClass;
import static org.intellij.grammar.generator.fleet.FleetConstants.FLEET_NAMESPACE;
import static org.intellij.grammar.generator.fleet.FleetConstants.FLEET_NAMESPACE_PREFIX;

public abstract class GeneratorBase {
  public static final Logger LOG = Logger.getInstance(GeneratorBase.class);

  private final BnfFile myFile;


  protected final Boolean myGenerateForFleet;

  private final String myOutputPath;
  private final String myPackagePrefix;
  protected final String mySourcePath;
  protected final String myGrammarRoot;
  protected final String myGrammarRootParser;

  protected final NameFormat myIntfClassFormat;
  protected final NameFormat myImplClassFormat;

  private int myOffset;
  private PrintWriter myOut;
  private NameShortener myShortener;

  protected final GenOptions G;

  protected BnfFile getFile() {
    return myFile;
  }

  protected enum Java {CLASS, INTERFACE, ABSTRACT_CLASS}

  protected static class RuleInfo {
    final String name;
    final boolean isFake;
    final String elementType;
    final String parserClass;
    final String intfPackage;
    final String implPackage;
    final String intfClass;
    final String implClass;
    final String mixin;
    final String stub;
    String realStubClass;
    Set<String> superInterfaces;
    boolean mixedAST;
    String realSuperClass;
    boolean isAbstract;
    boolean isInElementType;

    RuleInfo(String name, boolean isFake,
             String elementType, String parserClass,
             String intfPackage, String implPackage,
             String intfClass, String implClass, String mixin, String stub) {
      this.name = name;
      this.isFake = isFake;
      this.elementType = elementType;
      this.parserClass = parserClass;
      this.intfPackage = intfPackage;
      this.implPackage = implPackage;
      this.stub = stub;
      this.intfClass = intfPackage + "." + intfClass;
      this.implClass = implPackage + "." + implClass;
      this.mixin = mixin;
    }
  }

  protected final Map<String, RuleInfo> myRuleInfos = new TreeMap<>();

  protected GeneratorBase(@NotNull BnfFile psiFile,
                          @NotNull String sourcePath,
                          @NotNull String outputPath,
                          @NotNull String packagePrefix) {
    myFile = psiFile;
    myGenerateForFleet = psiFile instanceof FleetBnfFileImpl;

    G = new GenOptions(psiFile);
    mySourcePath = sourcePath;
    myOutputPath = outputPath;
    myPackagePrefix = packagePrefix;
    myIntfClassFormat = getPsiClassFormat(myFile);
    myImplClassFormat = getPsiImplClassFormat(myFile);

    List<BnfRule> rules = psiFile.getRules();
    BnfRule rootRule = rules.isEmpty() ? null : rules.get(0);
    myGrammarRoot = rootRule == null ? null : rootRule.getName();
    for (BnfRule r : rules) {
      String ruleName = r.getName();
      boolean noPsi = !hasPsiClass(r);
      myRuleInfos.put(ruleName, new ParserGenerator.RuleInfo(
        ruleName, ParserGeneratorUtil.Rule.isFake(r),
        ParserGeneratorUtil.getElementType(r, G.generateElementCase), getAttribute(r, KnownAttribute.PARSER_CLASS),
        noPsi ? null : getAttribute(r, KnownAttribute.PSI_PACKAGE),
        noPsi ? null : getAttribute(r, KnownAttribute.PSI_IMPL_PACKAGE),
        noPsi ? null : getRulePsiClassName(r, myIntfClassFormat), noPsi ? null : getRulePsiClassName(r, myImplClassFormat),
        noPsi ? null : getAttribute(r, KnownAttribute.MIXIN), noPsi ? null : getAttribute(r, KnownAttribute.STUB_CLASS)));
    }
    myGrammarRootParser = rootRule == null ? null : ruleInfo(rootRule).parserClass;
  }

  public abstract void generate() throws IOException;

  protected void generateClassHeader(String className,
                                     Set<String> imports,
                                     String annos,
                                     ParserGenerator.Java javaType,
                                     String... supers) {
    generateFileHeader(className);
    String packageName = generatePackageName(className);
    String shortClassName = StringUtil.getShortName(className);
    out("package %s;", packageName);
    newLine();
    Set<String> includedPackages = JBIterable.from(imports)
      .filter(o -> !o.startsWith("static") && o.endsWith(".*"))
      .map(o -> StringUtil.trimEnd(o, ".*"))
      .append(packageName).toSet();
    Set<String> includedClasses = new HashSet<>();
    for (RuleInfo info : myRuleInfos.values()) {
      if (includedPackages.contains(info.intfPackage)) includedClasses.add(StringUtil.getShortName(info.intfClass));
      if (includedPackages.contains(info.implPackage)) includedClasses.add(StringUtil.getShortName(info.implClass));
    }
    NameShortener shortener = new NameShortener(packageName, !G.generateFQN);
    shortener.addImports(imports, includedClasses);
    for (String s : shortener.getImports()) {
      out("import %s;", s);
    }
    if (G.generateFQN && imports.contains("#forced")) {
      for (String s : JBIterable.from(imports).filter(o -> !"#forced".equals(o))) {
        out("import %s;", s);
      }
    }
    newLine();
    StringBuilder sb = new StringBuilder();
    for (int i = 0, supersLength = supers.length; i < supersLength; i++) {
      String aSuper = supers[i];
      if (StringUtil.isEmpty(aSuper)) continue;
      if (imports.contains(aSuper + ";")) {
        aSuper = StringUtil.getShortName(aSuper);
      }
      if (i == 0) {
        sb.append(" extends ").append(shortener.shorten(aSuper));
      }
      else if (javaType != ParserGenerator.Java.INTERFACE && i == 1) {
        sb.append(" implements ").append(shortener.shorten(aSuper));
      }
      else {
        sb.append(", ").append(shortener.shorten(aSuper));
      }
    }
    if (StringUtil.isNotEmpty(annos)) {
      out(shortener.shorten(annos));
    }
    out("public %s %s%s {", Case.LOWER.apply(javaType.name()).replace('_', ' '), shortClassName, sb.toString());
    newLine();
    myShortener = shortener;
  }

  private void generateFileHeader(String className) {
    String header = getRootAttribute(myFile, KnownAttribute.CLASS_HEADER, className);
    String text = StringUtil.isEmpty(header) ? "" : getStringOrFile(header);
    if (StringUtil.isNotEmpty(text)) {
      out(text);
    }
    resetOffset();
  }

  @NotNull
  protected String generatePackageName(String className) {
    var packageName = StringUtil.getPackageName(className);
    if (myGenerateForFleet && G.adjustPackagesForFleet && !className.startsWith(FLEET_NAMESPACE)) {
      if (packageName.isEmpty()) {
        return FLEET_NAMESPACE;
      }
      return FLEET_NAMESPACE_PREFIX + packageName;
    }

    return packageName;
  }

  private String getStringOrFile(String classHeader) {
    try {
      File file = new File(mySourcePath, classHeader);
      if (file.exists()) return FileUtil.loadFile(file);
    }
    catch (IOException ex) {
      LOG.error(ex);
    }
    return classHeader.startsWith("//") || classHeader.startsWith("/*") ? classHeader :
           StringUtil.countNewLines(classHeader) > 0 ? "/*\n" + classHeader + "\n*/" :
           "// " + classHeader;
  }

  protected void openOutput(String className) throws IOException {
    String classNameAdjusted = myPackagePrefix.isEmpty() ? className : StringUtil.trimStart(className, myPackagePrefix + ".");
    File file = new File(myOutputPath, classNameAdjusted.replace('.', File.separatorChar) + ".java");
    myOut = openOutputInner(className, file);
  }

  protected PrintWriter openOutputInner(String className, File file) throws IOException {
    //noinspection ResultOfMethodCallIgnored
    file.getParentFile().mkdirs();
    return new PrintWriter(new FileOutputStream(file), false, myFile.getVirtualFile().getCharset());
  }

  protected void closeOutput() {
    myOut.close();
  }

  public void out(String s, Object... args) {
    out(format(s, args));
  }

  public void out(String s) {
    int length = s.length();
    if (length == 0) {
      myOut.println();
      return;
    }
    boolean newStatement = true;
    for (int start = 0, end; start < length; start = end + 1) {
      boolean isComment = s.startsWith("//", start);
      end = StringUtil.indexOf(s, '\n', start, length);
      if (end == -1) end = length;
      String substring = s.substring(start, end);
      if (!isComment && (substring.startsWith("}") || substring.startsWith(")"))) {
        myOffset--;
        newStatement = true;
      }
      if (myOffset > 0) {
        myOut.print(StringUtil.repeat("  ", newStatement ? myOffset : myOffset + 1));
      }
      myOut.println(substring);
      if (isComment) {
        newStatement = true;
      }
      else if (substring.endsWith("{")) {
        myOffset++;
        newStatement = true;
      }
      else if (substring.endsWith("(")) {
        myOffset++;
        newStatement = false;
      }
      else {
        newStatement = substring.endsWith(";") || substring.endsWith("}");
      }
    }
  }

  public void newLine() {
    out("");
  }

  public @NotNull String shorten(@NotNull String s) {
    return myShortener.shorten(s);
  }


  @NotNull
  ParserGenerator.RuleInfo ruleInfo(BnfRule rule) {
    return Objects.requireNonNull(myRuleInfos.get(rule.getName()));
  }

  protected void resetOffset() {
    myOffset = 0;
  }
}
