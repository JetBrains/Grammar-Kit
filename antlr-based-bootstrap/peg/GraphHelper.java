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

import org.antlr.runtime.tree.Tree;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static peg.GeneratorUtil.Rule;
import static peg.GeneratorUtil.getAttribute;
import static peg.GrammarParser.*;
import static peg.GraphHelper.Cardinality.*;

/**
 * @author gregory
 *         Date: 15.07.11 18:19
 */
public class GraphHelper {
    public enum Cardinality{NONE, OPTIONAL, REQUIRED, AT_LEAST_ONE, ANY_NUMBER;
        boolean optional() { return this == OPTIONAL || this == ANY_NUMBER || this == NONE; }
        boolean many() { return this == AT_LEAST_ONE || this == ANY_NUMBER; }
        public static Cardinality fromNodeType(int type) {
            if (type == OPT) return OPTIONAL;
            else if (type == SEQ || type == ID) return REQUIRED;
            else if (type == CHOICE) return Cardinality.OPTIONAL;
            else if (type == ONEMORE) return AT_LEAST_ONE;
            else if (type == ZEROMORE) return ANY_NUMBER;
            else throw new AssertionError(GrammarParser.tokenNames[type]);
        }
        public Cardinality and(Cardinality c) {
            if (this == NONE || c == NONE) return NONE;
            if (optional() || c.optional()) {
                return many() || c.many() ? ANY_NUMBER : OPTIONAL;
            } else {
                return many() || c.many() ? AT_LEAST_ONE : REQUIRED;
            }
        }
        public Cardinality or(Cardinality c) {
            if (c == null) c = NONE;
            if (this == NONE && c == NONE) return NONE;
            if (this == NONE) return c;
            if (c == NONE) return this;
            return optional() && c.optional()? ANY_NUMBER : AT_LEAST_ONE;
        }
    }


    public static Distance NO_WAY = new Distance(0, NONE);
    public static class Distance {

        int num = 0;
        Cardinality card = NONE;

        public Distance(int num, Cardinality card) {
            this.num = num;
            this.card = card;
        }

        public Distance update(Tree iNode, Distance d1, Distance d2, Tree kNode) {
            if (d1 == NO_WAY || d2 == NO_WAY) return this;
            // calc d1 + d2
            Cardinality c = d1.card.and(fromNodeType(kNode.getType())).and(d2.card);
            return or(iNode, c);
        }

        public Distance or(Tree iNode, Cardinality c) {
            if (this == NO_WAY) {
                return new Distance(1, c);
            }
            int type = iNode.getType();
            if (type == CHOICE && c != NONE) {
                num++;
                if (num == iNode.getChildCount()) {
                    assert card == OPTIONAL;
                    card = REQUIRED;
                }
            }
            else if (type == SEQ && c != NONE) {
                num ++;
                card = AT_LEAST_ONE;
            }
            else {
                card = card.or(c);
            }
            return this;
        }

        public String toString() { return card+":"+num; }
    }


    private Map<String, Tree> ruleMap;

    public GraphHelper(Map<String, Tree> ruleMap) {
        this.ruleMap = ruleMap;
    }

    @Nullable
    private static Tree resolveRule(Map<String, Tree> ruleMap, String text) {
        Tree rule = ruleMap.get(text);
        if (rule == null) return null;
        String superRuleName = getAttribute(rule, "extends", null);
        if (superRuleName == null) return null;
        Tree superRule = ruleMap.get(superRuleName);
        return superRule == null? rule : superRule;
    }


    public Map<Tree, Cardinality> getFor(Tree rule) {
        return collectMembers(Rule.body(rule), new HashSet<Tree>());
    }


    private Map<Tree, Cardinality> collectMembers(Tree tree, HashSet<Tree> visited) {
        int type = tree.getType();
        if (type == AND || type == NOT) return Collections.emptyMap();
        if (type == STRING || type == NUMBER) return Collections.singletonMap(tree, REQUIRED);

        if (!visited.add(tree)) return Collections.singletonMap(tree, REQUIRED);
        if (type == ID) {
            Tree targetRule = resolveRule(ruleMap, tree.getText());
            if (targetRule != null) {
                if (Rule.isPrivate(targetRule)) {
                    Tree body = Rule.body(targetRule);
                    Map<Tree, Cardinality> map = collectMembers(body, visited);
                    return map.containsKey(body) ? joinMaps(ZEROMORE, Arrays.asList(map, map)) : map;
                }
                else {
                    return Collections.singletonMap(targetRule, REQUIRED);
                }
            }
            return Collections.singletonMap(tree, REQUIRED);
        }

        List<Map<Tree, Cardinality>> list = new ArrayList<Map<Tree, Cardinality>>();
        for (int i=0, len = tree.getChildCount(); i<len; i++) {
            list.add(collectMembers(tree.getChild(i), visited));
        }
        return joinMaps(tree.getType(), list);
    }

    private Map<Tree, Cardinality> joinMaps(int type, List<Map<Tree, Cardinality>> list) {
        if (list.isEmpty()) return Collections.emptyMap();
        if (type == OPT || type == ONEMORE || type == ZEROMORE) {
            HashMap<Tree, Cardinality> map = new HashMap<Tree, Cardinality>();
            assert list.size() == 1;
            Map<Tree, Cardinality> m = list.get(0);
            for (Tree t : m.keySet()) {
                map.put(t, fromNodeType(type).and(m.get(t)));
            }
            return map;
        }
        else if (type == SEQ) {
            if (list.size() == 1) return list.get(0);
            HashMap<Tree, Cardinality> map = new HashMap<Tree, Cardinality>();
            for (Map<Tree, Cardinality> m : list) {
                for (Tree t : m.keySet()) {
                    map.put(t, m.get(t).or(map.get(t)));
                }
            }
            return map;
        }
        else if (type == CHOICE) {
            HashMap<Tree, Cardinality> map = new HashMap<Tree, Cardinality>();
            Map<Tree, Cardinality> m0 = list.get(0);
            map.putAll(m0);
            for (Map<Tree, Cardinality> m : list) {
                map.keySet().retainAll(m.keySet());
            }
            for (Tree t : map.keySet()) {
                map.put(t, REQUIRED.and(m0.get(t)));
                for (Map<Tree, Cardinality> m : list) {
                    if (m == list.get(0)) continue;
                    map.put(t, map.get(t).and(m.get(t)));
                }
            }
            for (Map<Tree, Cardinality> m : list) {
                for (Tree t : m.keySet()) {
                    if (map.containsKey(t)) continue;
                    map.put(t, OPTIONAL.and(m.get(t)));
                }
            }
            return map;
        }
        else throw new AssertionError(GrammarParser.tokenNames[type]);
    }


}
