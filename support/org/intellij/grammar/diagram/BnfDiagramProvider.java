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

package org.intellij.grammar.diagram;

import com.intellij.diagram.*;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.diagram.presentation.DiagramLineType;
import com.intellij.diagram.presentation.DiagramState;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ui.SimpleColoredText;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.PlatformIcons;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashMap;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getAttribute;

/**
 * @author gregsh
 */
public class BnfDiagramProvider extends DiagramProvider<PsiNamedElement> {
  public static final String ID = "GRAMMAR";
  private final DiagramVisibilityManager myVisibilityManager = new EmptyDiagramVisibilityManager();

  private static final DiagramCategory[] CATEGORIES = new DiagramCategory[]{new DiagramCategory("Properties", PlatformIcons.PROPERTY_ICON, true)};
  private final DiagramNodeContentManager myNodeContentManager = new AbstractDiagramNodeContentManager() {

    @Override
    public boolean isInCategory(Object o, DiagramCategory diagramCategory, DiagramState diagramState) {
      return true;
    }

    @Override
    public DiagramCategory[] getContentCategories() {
      return CATEGORIES;
    }
  };
  private final DiagramElementManager myElementManager = new AbstractDiagramElementManager<PsiNamedElement>() {
    @Override
    public PsiNamedElement findInDataContext(DataContext context) {
      PsiFile file = LangDataKeys.PSI_FILE.getData(context);
      return file instanceof BnfFile ? file : null;
    }

    @Override
    public boolean isAcceptableAsNode(Object o) {
      return o instanceof PsiNamedElement;
    }

    @Override
    public String getElementTitle(PsiNamedElement bnfFile) {
      return bnfFile.getName();
    }

    @Override
    public SimpleColoredText getPresentableName(Object o, DiagramState diagramState) {
      if (o instanceof Map.Entry) o = ((Map.Entry)o).getKey();
      if (o instanceof PsiNamedElement) {
        return new SimpleColoredText(StringUtil.notNullize(((PsiNamedElement)o).getName()), DEFAULT_TITLE_ATTR);
      }
      return null;
    }

    @Override
    public Object[] getNodeElements(PsiNamedElement parent) {
      if (parent instanceof BnfRule) {
        Map<PsiElement, RuleGraphHelper.Cardinality> map = myGraphHelper.getFor((BnfRule)parent);
        Object[] objects = ContainerUtil.findAll(map.entrySet(), new Condition<Map.Entry<PsiElement, RuleGraphHelper.Cardinality>>() {
          @Override
          public boolean value(Map.Entry<PsiElement, RuleGraphHelper.Cardinality> p) {
            return p.getKey() instanceof BnfRule;
          }
        }).toArray();
        Arrays.sort(objects, new Comparator<Object>() {
          @Override
          public int compare(Object o, Object o1) {
            return Comparing.compare(((Map.Entry<BnfRule, ?>)o).getKey().getName(), ((Map.Entry<BnfRule, ?>)o1).getKey().getName());
          }
        });
        return objects;
      }
      return super.getNodeElements(parent);
    }

    @Override
    public SimpleColoredText getPresentableType(Object element) {
      if (element instanceof Map.Entry) {
        RuleGraphHelper.Cardinality cardinality = (RuleGraphHelper.Cardinality)((Map.Entry)element).getValue();
        String text = RuleGraphHelper.getCardinalityText(cardinality);
        if (StringUtil.isNotEmpty(text)) {
          return new SimpleColoredText(" "+text+" ", SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
        }
      }
      return super.getPresentableType(element);
    }

    @Override
    public Icon getNodeElementIcon(Object element, DiagramState presentation) {
      if (element instanceof Map.Entry) element = ((Map.Entry)element).getKey();
      return super.getNodeElementIcon(element, presentation);
    }

    @Override
    public String getElementDescription(PsiNamedElement bnfFile) {
      return null;
    }
  };

  private final DiagramVfsResolver myVfsResolver = new DiagramVfsResolver<PsiNamedElement>() {
    @Override
    public String getQualifiedName(PsiNamedElement bnfFile) {
      VirtualFile virtualFile = bnfFile instanceof PsiFile ? ((PsiFile)bnfFile).getVirtualFile() : null;
      return virtualFile == null? null : virtualFile.getUrl();
    }

    @Override
    public PsiNamedElement resolveElementByFQN(String s, Project project) {
      VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(s);
      PsiFile psiFile = virtualFile == null? null : PsiManager.getInstance(project).findFile(virtualFile);
      return psiFile instanceof BnfFile? psiFile : null;
    }
    
  };
  private final DiagramRelationshipManager myRelationshipManager = new DiagramRelationshipManager<PsiNamedElement>() {
    @Override
    public DiagramRelationshipInfo getDependencyInfo(PsiNamedElement e1, PsiNamedElement e2, DiagramCategory diagramCategory) {
      return null;
    }

    @Override
    public DiagramCategory[] getContentCategories() {
      return CATEGORIES;
    }
  };
  private final DiagramExtras myExtras = new DiagramExtras();
  private RuleGraphHelper myGraphHelper;

  public BnfDiagramProvider() {
    myElementManager.setUmlProvider(this);
  }

  @Pattern("[a-zA-Z0-9_-]*")
  @Override
  public String getID() {
    return ID;
  }

  @Override
  public DiagramVisibilityManager createVisibilityManager() {
    return myVisibilityManager;
  }

  @Override
  public DiagramNodeContentManager getNodeContentManager() {
    return myNodeContentManager;
  }

  @Override
  public DiagramElementManager<PsiNamedElement> getElementManager() {
    return myElementManager;
  }

  @Override
  public DiagramVfsResolver<PsiNamedElement> getVfsResolver() {
    return myVfsResolver;
  }

  @Override
  public DiagramRelationshipManager<PsiNamedElement> getRelationshipManager() {
    return myRelationshipManager;
  }

  @Override
  public DiagramDataModel<PsiNamedElement> createDataModel(@NotNull final Project project,
                                                   @Nullable final PsiNamedElement element,
                                                   @Nullable final VirtualFile file,
                                                   DiagramPresentationModel presentationModel) {
    return new MyDataModel(project, (BnfFile)element, this);
  }

  @NotNull
  @Override
  public DiagramExtras getExtras() {
    return myExtras;
  }

  @Override
  public String getActionName(boolean isPopup) {
    return "Visualisation";
  }

  @Override
  public String getPresentableName() {
    return "Grammar Diagrams";
  }

  private static class MyDataModel extends DiagramDataModel<PsiNamedElement> implements ModificationTracker {

    private final BnfFile myFile;

    private final Collection<DiagramNode<PsiNamedElement>> myNodes = new HashSet<DiagramNode<PsiNamedElement>>();
    private final Collection<DiagramEdge<PsiNamedElement>> myEdges = new HashSet<DiagramEdge<PsiNamedElement>>();


    MyDataModel(Project project, BnfFile file, BnfDiagramProvider provider) {
      super(project, provider);
      myFile = file;
    }

    @NotNull
    @Override
    public Collection<DiagramNode<PsiNamedElement>> getNodes() {
      return myNodes;
    }

    @NotNull
    @Override
    public Collection<DiagramEdge<PsiNamedElement>> getEdges() {
      return myEdges;
    }

    @NotNull
    @Override
    public String getNodeName(DiagramNode<PsiNamedElement> node) {
      return StringUtil.notNullize(node.getName());
    }

    @Override
    public DiagramNode<PsiNamedElement> addElement(PsiNamedElement psiElement) {
      return null;
    }

    @Override
    public void refreshDataModel() {
      myNodes.clear();
      myEdges.clear();

      RuleGraphHelper ruleGraphHelper = RuleGraphHelper.getCached(myFile);
      ((BnfDiagramProvider)getProvider()).myGraphHelper = ruleGraphHelper;

      Map<BnfRule, DiagramNode<PsiNamedElement>> nodeMap = new THashMap<BnfRule, DiagramNode<PsiNamedElement>>();
      List<BnfRule> rules = myFile.getRules();
      BnfRule root = ContainerUtil.getFirstItem(rules);
      for (BnfRule rule : rules) {
        if (rule != root && !RuleGraphHelper.shouldGeneratePsi(rule, true)) continue;
        DiagramNode<PsiNamedElement> diagramNode = new PsiDiagramNode<PsiNamedElement>(rule, getProvider()) {
          @Override
          public String getName() {
            return getIdentifyingElement().getName();
          }
        };
        nodeMap.put(rule, diagramNode);
        myNodes.add(diagramNode);
      }
      for (BnfRule rule : rules) {
        if (rule != root && !RuleGraphHelper.shouldGeneratePsi(rule, true)) continue;
        Map<PsiElement, RuleGraphHelper.Cardinality> map = ruleGraphHelper.getFor(rule);

        BnfRule superRule = myFile.getRule(getAttribute(rule, KnownAttribute.EXTENDS));
        if (superRule != null) {
          DiagramNode<PsiNamedElement> source = nodeMap.get(rule);
          DiagramNode<PsiNamedElement> target = nodeMap.get(superRule);

          myEdges.add(new DiagramEdgeBase<PsiNamedElement>(source, target, new DiagramRelationshipInfoAdapter("EXTENDS", DiagramLineType.DASHED, "extends") {

              public Shape getStartArrow() {
                return DELTA;
              }
            }) {

              @Override
              public String getName() {
                return "";
              }
            });
        }
        for (final PsiElement element : map.keySet()) {
          if (!(element instanceof BnfRule)) continue;
          final RuleGraphHelper.Cardinality cardinality = map.get(element);
          assert cardinality != RuleGraphHelper.Cardinality.NONE;

          DiagramNode<PsiNamedElement> source = nodeMap.get(rule);
          DiagramNode<PsiNamedElement> target = nodeMap.get(element);
          myEdges.add(new DiagramEdgeBase<PsiNamedElement>(source, target, new DiagramRelationshipInfoAdapter("CONTAINS", DiagramLineType.SOLID, "") {
            @Override
            public String getLabel() {
              return cardinality.name().toLowerCase();
            }

            public Shape getStartArrow() {
              return DELTA;
            }
          } ) {

            @Override
            public Object getSourceAnchor() {
              return element;
            }

            @Override
            public Object getTargetAnchor() {
              return element;
            }

            @Override
            public Color getAnchorColor() {
              return Color.BLUE;
            }

            @Override
            public String getName() {
              return "";
            }
          });
        }
      }
    }

    @NotNull
    @Override
    public ModificationTracker getModificationTracker() {
      return this;
    }

    @Override
    public void dispose() {
    }

    @Override
    public long getModificationCount() {
      return myFile.getModificationStamp();
    }
  }
}
