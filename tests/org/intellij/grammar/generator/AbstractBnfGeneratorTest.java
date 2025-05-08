/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.BnfGeneratorTestCase;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * @author gregsh
 */
public abstract class AbstractBnfGeneratorTest extends BnfGeneratorTestCase {

  private final @NotNull String myResultFileExtension;

  public AbstractBnfGeneratorTest(
    @NotNull String generatedParsersFolder,
    @NotNull String resultFileExtension
  ) {
    super(generatedParsersFolder);
    myResultFileExtension = resultFileExtension;
  }

  protected final @NotNull OutputOpener getTestOpener() {
    return (className, fileToOpen, myBnfFile) -> {
      final var grammarName = FileUtil.getNameWithoutExtension(myBnfFile.getName());
      final var fileName = FileUtil.getNameWithoutExtension(fileToOpen);
      final var name =
        grammarName + (fileName.startsWith(grammarName) || fileName.endsWith("Parser") ? "" : ".PSI") + myResultFileExtension;
      final var targetFile = new File(FileUtilRt.getTempDirectory(), name);
      targetFile.getParentFile().mkdirs();
      final var outputStream = new FileOutputStream(targetFile, true);
      final var out = new PrintWriter(new OutputStreamWriter(outputStream, myBnfFile.getVirtualFile().getCharset()));
      out.println("// ---- " + FileUtil.getRelativePath(new File(myFullDataPath), fileToOpen.getAbsoluteFile()) + " -----------------");
      return out;
    };
  }

  protected abstract @NotNull List<@NotNull Generator> createGenerators(
    @NotNull BnfFile psiFile,
    @NotNull String outputPath,
    @NotNull OutputOpener outputOpener
  );

  @Override
  protected String loadFile(@NonNls @NotNull String name) throws IOException {
    if (name.equals("SelfBnf.bnf")) return super.loadFile("../../grammars/Grammar.bnf");
    if (name.equals("SelfFlex.bnf")) return super.loadFile("../../grammars/JFlex.bnf");
    try {
      // first, see if there is a test in the testDir folder
      return loadFileDefault(this.myBnfFilesDir, name);
    }
    catch (IOException e) {
      // if not, try loading from the appropriate test data folder
      LOG.info("Couldn't open file from `%s`, searching for it in `%s`.".formatted(BNF_FILES_DIR, this.myFullDataPath));
      return super.loadFile(name);
    }
  }

  protected final void doParserTest() throws Exception {
    doGenTest(false);
  }

  protected final void doPsiTest() throws Exception {
    doGenTest(true);
  }

  protected final void doGenTest(boolean generatePsi) throws Exception {
    final var testName = getTestName(false);
    final var bnfFileContent = loadFile(testName + "." + myFileExt);
    myFile = createBnfFile(generatePsi, testName, bnfFileContent);

    final var filesToCheck = new ArrayList<File>();
    filesToCheck.add(new File(FileUtilRt.getTempDirectory(), testName + myResultFileExtension));
    if (generatePsi) {
      filesToCheck.add(new File(FileUtilRt.getTempDirectory(), testName + ".PSI" + myResultFileExtension));
    }
    for (final var file : filesToCheck) {
      if (file.exists()) {
        assertTrue(file.delete());
      }
    }

    for (final var generator : createGenerators((BnfFile)myFile, myFullDataPath, getTestOpener())) {
      if (generatePsi) {
        generator.generate();
      }
      else {
        generator.generateParser();
      }
    }

    for (final var file : filesToCheck) {
      assertTrue("Generated file not found: " + file, file.exists());
      String expectedName = FileUtil.getNameWithoutExtension(file) + ".expected" + myResultFileExtension;
      String result = FileUtil.loadFile(file, CharsetToolkit.UTF8, true);
      doCheckResult(myFullDataPath, expectedName, result);
    }
  }

  protected void doTestEmpty() throws IOException {
    myFile = createPsiFile("empty.bnf", "{ }");
    for (final var generator : createGenerators((BnfFile)myFile, myFullDataPath, getTestOpener())) {
      generator.generate();
    }
  }

  protected PsiFile createBnfFile(boolean generatePsi, String name, String text) {
    return createPsiFile(name, text.replaceAll("generatePsi=[^\n]*", "generatePsi=" + generatePsi));
  }
}
