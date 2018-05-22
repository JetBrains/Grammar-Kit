package org.intellij.jflex.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 15.03.2008
 * Time: 18:52:38
 */
public interface JFlexJavaCodeInjected extends JFlexComposite, PsiLanguageInjectionHost {

    boolean isMatchAction();

}
