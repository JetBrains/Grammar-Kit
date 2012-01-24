/*
 * Copyright 2011-2012 Gregory Shrago
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

package org.intellij.grammar.java;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

/**
 * @author gregsh
 */
public class JavaHelper {
  
  public static JavaHelper getJavaHelper(Project project) {
    JavaHelper service = ServiceManager.getService(project, JavaHelper.class);
    return service == null? new JavaHelper() : service;
  }

  @Nullable
  public NavigatablePsiElement findClass(String className) { return null; }
  @Nullable
  public NavigatablePsiElement findClassMethod(String className, String methodName) { return null; }
  
  private static class Impl extends JavaHelper {
    private final JavaPsiFacade myFacade;

    private Impl(JavaPsiFacade facade) {
      myFacade = facade;
    }

    @Override
    public PsiClass findClass(String className) {
      return myFacade.findClass(className, GlobalSearchScope.allScope(myFacade.getProject()));
    }

    @Override
    public PsiMethod findClassMethod(String className, String methodName) {
      PsiClass aClass = findClass(className);
      PsiMethod[] methods = aClass == null? PsiMethod.EMPTY_ARRAY : aClass.findMethodsByName(methodName, true);
      return methods.length == 1 ? methods[0] : null;
    }
  }
}
