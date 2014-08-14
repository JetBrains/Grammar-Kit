package org.intellij.grammar.expression;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import org.intellij.grammar.LightPsi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author greg
 */
public class Main {
  public static void main(String[] args) throws Exception {
    try {
      runConsole();
    }
    catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    finally {
      System.exit(0);
    }
  }

  private static void runConsole() throws IOException {
    System.out.println("Welcome to interactive expression console");
    System.out.print("> ");
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String s;
    while((s = in.readLine()) != null) {
      dump(s, false);
      System.out.print("> ");
    }
  }

  private static void dump(String text, boolean usePsi) {
    ExpressionParserDefinition parserDefinition = new ExpressionParserDefinition();
    String treeDump;
    if (usePsi) {
      PsiFile psiFile = LightPsi.parseFile("a.expr", text, parserDefinition);
      treeDump = DebugUtil.psiToString(psiFile, false);
    }
    else {
      ASTNode astNode = LightPsi.parseText(text, parserDefinition);
      treeDump = DebugUtil.nodeTreeToString(astNode, true);
    }
    treeDump = treeDump.replaceAll("\\w*\\(((?:[^)]|'\\)')*)\\)", "$1 ");
    System.out.println(treeDump);
  }
}
