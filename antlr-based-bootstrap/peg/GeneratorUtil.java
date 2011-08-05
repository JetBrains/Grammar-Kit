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
import org.antlr.runtime.tree.Tree;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static peg.GrammarParser.*;

public class GeneratorUtil {
    private static final Object NULL = new Object();

    public static <T>T getAttribute(Tree node, String attrName, @Nullable T def) {
        Tree parent = findTopLevel(node);
        if (parent == null) return def;
        Tree attr = findAttributeValueNode(findChild(parent, ATTRS), attrName);
        Object attrVal = getLiteralValue(attr);
        if (attrVal != null) return attr == NULL ? def : (T)attrVal;
        Tree root = parent.getParent();
        for (int i = parent.getChildIndex(); i>=0; i--) {
            Tree child = root.getChild(i);
            if (child.getType() == ATTRS) {
                Tree attr2 = findAttributeValueNode(child, attrName);
                Object attrVal2 = getLiteralValue(attr2);
                if (attrVal2 != null &&
                        attr2.getTokenStartIndex() < node.getTokenStopIndex()) {
                    return attr2 == NULL ? def : (T)attrVal2;
                }
            }
        }
        return def;
    }

    public static String getAttributeName(Tree node, String value) {
        Tree parent = findTopLevel(node);
        if (parent == null) return null;
        Tree attr = findAttributeByValue(findChild(parent, ATTRS), value);
        if (attr != null) return findChild(attr, ID).getText();
        Tree root = parent.getParent();
        for (int i = parent.getChildIndex(); i>=0; i--) {
            Tree child = root.getChild(i);
            if (child.getType() == ATTRS) {
                attr = findAttributeByValue(child, value);
                if (attr != null) return findChild(attr, ID).getText();
            }
        }
        return null;
    }

    public static Tree findTopLevel(Tree node) {
        if (node == null) return node;
        while (true) {
            Tree parent = node.getParent();
            if (parent == null) return node;
            if (parent.getType() == 0) return node;
            node = parent;
        }
    }

    public static Tree findAttributeValueNode(Tree attrs, String attrName) {
        for (Tree tree : getChildren(attrs, ATTR)) {
            Tree id = findChild(tree, ID);
            if (attrName.equals(id.getText())) {
                return tree.getChild(id.getChildIndex() + 1);
            }
        }
        return null;
    }

    public static Tree findAttributeByValue(Tree attrs, String value) {
        for (Tree tree : getChildren(attrs, ATTR)) {
            Tree id = findChild(tree, ID);
            if (value.equals(getLiteralValue(tree.getChild(id.getChildIndex() + 1)))) {
                return tree;
            }
        }
        return null;
    }

    public static Object getLiteralValue(Tree child) {
        if (child == null) return null;
        String text = child.getText();
        if (child.getType() == STRING) return StringUtil.stripQuotesAroundValue(text);
        if (child.getType() == NUMBER) return Integer.parseInt(text);
        if (child.getType() == ID) {
            if (text.equals("true") || text.equals("false")) return Boolean.parseBoolean(text);
            if (text.equals("null")) return NULL;
            Object attribute = getAttribute(child.getParent(), text, null);
            if (attribute == null) {
                // todo look for rule
            }
            return attribute;
        }
        return null;
    }

    public static Tree findChild(Tree node, int type) {
        if (node == null) return null;
        for (int i = 0, len = node.getChildCount(); i < len; i++) {
            Tree child = node.getChild(i);
            if (child.getType() == type) return child;
        }
        return null;
    }

    public static Tree findChild(Tree node, int type, String text) {
        if (node == null) return null;
        for (int i = 0, len = node.getChildCount(); i < len; i++) {
            Tree child = node.getChild(i);
            if ((type < 0 || child.getType() == type) && text.equals(child.getText())) {
                return child;
            }
        }
        return null;
    }

    public static List<Tree> getChildren(Tree node) {
        return getChildren(node, -1);
    }

    public static List<Tree> getChildren(Tree node, int type) {
        if (node == null) return Collections.emptyList();
        List<Tree> result = null;
        for (int i = 0, len = node.getChildCount(); i < len; i++) {
            Tree child = node.getChild(i);
            if (type < 0 || child.getType() == type) {
                if (result == null) result = new ArrayList<Tree>();
                result.add(child);
            }
        }
        return result == null? Collections.<Tree>emptyList() : result;
    }

    public static boolean isTrivialNode(Tree node) {
        int type = node.getType();
        if (node.getChildCount() == 1 && (type == CHOICE || type == SEQ)) {
            Tree child = node.getChild(0);
            int ct = child.getType();
            return !(ct == STRING || ct == NUMBER || ct == ID); //getAttribute(rule, "doNotCollapse", Boolean.FALSE);
        }
        return false;
    }


    public static class Rule {
        public static boolean is(Tree node) {
            return node != null && node.getType() == RULE;
        }

        public static List<Tree> list(Tree parent) {
            return getChildren(parent, RULE);
        }

        public static boolean isPrivate(Tree node) {
            return is(node) && findChild(node, MODIFIER, "private") != null;
        }

        public static String name(Tree node) {
            final Tree child = is(node)? findChild(node, ID) : null;
            return child == null? null : child.getText();
        }

        public static Tree body(Tree rule) {
            return is(rule)? findChild(rule, CHOICE) : null;
        }

        public static Tree firstNotTrivial(Tree rule) {
            for (Tree tree = body(rule); tree != null; tree = tree.getChild(0)) {
                if (!isTrivialNode(tree)) return tree;
            }
            return null;
        }

        public static <T>T attribute(Tree rule, String attrName, @Nullable T def) {
            Tree attr = is(rule)? findAttributeValueNode(findChild(rule, ATTRS), attrName) : null;
            Object attrVal = getLiteralValue(attr);
            if (attrVal != null) return attr == NULL ? def : (T) attrVal;
            return def;
        }

    }

}
