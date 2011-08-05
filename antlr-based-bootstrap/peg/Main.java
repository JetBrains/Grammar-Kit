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

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import java.io.File;

public class Main {
    public static void main(String... args) throws Exception {
        String filePath = args[0];
        String toDir = args.length > 1? args[1] :
                new File(new File(filePath).getParentFile(), "gen").getAbsolutePath();
        GrammarLexer lex = new GrammarLexer(new ANTLRFileStream(filePath));
        CommonTokenStream tokens = new CommonTokenStream(lex);
        GrammarParser parser = new GrammarParser(tokens);

        CommonTree tree = parser.file().tree;
        System.out.println(dumpTree(tree, new StringBuilder(), 0));
        new Generator(tree, toDir).generate();
    }

    public static StringBuilder dumpTree(Tree tree, StringBuilder sb, int level) {
        int childCount = tree.getChildCount();
        int type = tree.getType();
        if (level > 0 && (tree.getParent().getType() == GrammarParser.OR || tree.getChildIndex() == 0 || childCount > 0)) {
            sb.append("\n");
            for (int i=0; i<level; i++) sb.append("  ");
        }
        else if (level > 0) sb.append(" ");
        sb.append(GrammarParser.tokenNames[type]);
        if (childCount == 0) {
            sb.append("(").append(tree.getText()).append(")");
        }
        else {
            sb.append(":");
            for (int i = 0; i< childCount; i++) {
                dumpTree(tree.getChild(i), sb, level + 1);
            }
        }
        return sb;
    }
}
