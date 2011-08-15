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

package org.intellij.grammar.debugger;

import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiNonJavaFileReferenceProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.FactoryMap;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.BnfDummyElementImpl;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author gregsh
 */
public class BnfPositionManager implements PositionManager {
  private final DebugProcess myProcess;
  private final Map<String, Collection<PsiFile>> myGrammars = new FactoryMap<String, Collection<PsiFile>>() {
    @Override
    protected Collection<PsiFile> create(String key) {
      PsiManager manager = PsiManager.getInstance(myProcess.getProject());
      final Ref<Collection<PsiFile>> result = Ref.create(null);
      manager.getSearchHelper().processUsagesInNonJavaFiles(key, new PsiNonJavaFileReferenceProcessor() {
        @Override
        public boolean process(PsiFile file, int startOffset, int endOffset) {
          if (!(file instanceof BnfFileImpl)) return true;
          BnfAttr attr = PsiTreeUtil.getParentOfType(file.findElementAt(startOffset), BnfAttr.class);
          if (attr == null || !"parserClass".equals(attr.getName())) return true;
          if (result.isNull()) result.set(new LinkedHashSet<PsiFile>(1));
          result.get().add(file); 
          return true;
        }
      }, GlobalSearchScope.allScope(manager.getProject()));
      return result.isNull()? Collections.<PsiFile>emptyList() : result.get();
    }
  };

  public BnfPositionManager(DebugProcess process) {
    myProcess = process;
  }

  @Override
  public SourcePosition getSourcePosition(@Nullable Location location) throws NoDataException {
    if (location == null) throw new NoDataException();

    final ReferenceType refType = location.declaringType();
    if (refType == null) throw new NoDataException();

    final String qname = refType.name();
    if (qname.contains("$")) throw new NoDataException();

    final String name = location.method().name();
    final int line = location.lineNumber() - 1;

    for (PsiFile file : myGrammars.get(qname)) {
      BnfExpression expression = findExpressionByName(file, name);
      if (expression != null &&
          qname.equals(ParserGeneratorUtil.getAttribute(PsiTreeUtil.getParentOfType(expression, BnfRule.class), "parserClass", ""))) {
        SourcePosition position = SourcePosition.createFromElement(expression);
        int lineNumber = getLineNumber(expression, qname);
        if (lineNumber == line) {
          return position;
        }
        break;
      }
    }
    throw new NoDataException();
  }

  @Nullable
  private BnfExpression findExpressionByName(PsiFile file, final String name) {
    final Ref<BnfExpression> result = Ref.create(null);
    file.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof BnfRule) {
          String ruleName = ((BnfRule)element).getName();
          if (ruleName != null && name.startsWith(ruleName)) {
            if (name.equals(ruleName) || !name.substring(ruleName.length()).matches("(?:_:digit:+)+")) {
              result.set(((BnfRule)element).getExpression());
              stopWalking();
            }
          }
        }
        else if (element instanceof BnfDummyElementImpl) {
          super.visitElement(element);
        }
      }
    });
    return result.get();
  }

  @NotNull
  @Override
  public List<ReferenceType> getAllClasses(SourcePosition classPosition) throws NoDataException {
    String parserClass = getParserClass(classPosition);

    List<ReferenceType> referenceTypes = myProcess.getVirtualMachineProxy().classesByName(parserClass);
    if (referenceTypes.isEmpty()) {
      throw new NoDataException();
    }
    return referenceTypes;
  }

  @NotNull
  @Override
  public List<Location> locationsOfLine(ReferenceType type, SourcePosition position) throws NoDataException {
    String parserClass = getParserClass(position);
    int line = getLineNumber(position.getElementAt(), parserClass);
    try {
      return type.locationsOfLine(line + 1);
    }
    catch (AbsentInformationException e) {
      // ignore
    }
    throw new NoDataException();
  }

  private int getLineNumber(PsiElement element, String parserClass) {
    int line = 0;
    AccessToken token = ReadAction.start();
    try {
      PsiClass aClass = JavaPsiFacade.getInstance(myProcess.getProject()).findClass(parserClass, myProcess.getSearchScope());
      BnfRule rule = PsiTreeUtil.getParentOfType(element, BnfRule.class);
      if (rule != null) {
        String name = rule.getName();
        PsiMethod[] methods = aClass.findMethodsByName(name, false);
        PsiStatement[] statements = methods.length == 1? methods[0].getBody().getStatements() : PsiStatement.EMPTY_ARRAY;
        // skip recursion guard, and get the next statement
        if (statements.length > 1) {
          int startOffset = statements[1].getTextRange().getStartOffset();
          line = PsiDocumentManager.getInstance(myProcess.getProject()).getDocument(aClass.getContainingFile()).getLineNumber(startOffset);
        }
      }
    }
    finally {
      token.finish();
    }
    return line;
  }

  @Override
  public ClassPrepareRequest createPrepareRequest(ClassPrepareRequestor requestor, SourcePosition position) throws NoDataException {
    return myProcess.getRequestsManager().createClassPrepareRequest(requestor, getParserClass(position));
  }

  @NotNull
  private String getParserClass(SourcePosition classPosition) throws NoDataException {
    BnfRule rule = getRuleAt(classPosition);
    String parserClass = ParserGeneratorUtil.getAttribute(rule, "parserClass", "");
    if (StringUtil.isEmpty(parserClass)) throw new NoDataException();
    return parserClass;
  }

  @NotNull
  private BnfRule getRuleAt(SourcePosition position) throws NoDataException {
    PsiFile file = position.getFile();
    if (!(file instanceof BnfFileImpl)) throw new NoDataException();
    BnfRule rule = PsiTreeUtil.getParentOfType(position.getElementAt(), BnfRule.class);
    if (rule == null) throw new NoDataException();
    return rule;
  }
}
