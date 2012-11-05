package org.intellij.grammar.expression;

import com.intellij.openapi.util.text.StringUtil;
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
      dump(s);
      System.out.print("> ");
    }
  }

  private static void dump(String text) {
    PsiFile psiFile = LightPsi.parseFile("a.expr", text, new ExpressionParserDefinition());
    String tree = DebugUtil.psiToString(psiFile, false);
    tree = StringUtil.replace(tree, "(BAD_CHARACTER)", "");
    System.out.println(tree);
  }
}
