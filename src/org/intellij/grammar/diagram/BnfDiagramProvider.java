/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.diagram;

import com.intellij.diagram.*;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.diagram.presentation.DiagramLineType;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleColoredText;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.PlatformIcons;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.GrammarKitBundle;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getAttribute;

/**
 * @author gregsh
 */
public class BnfDiagramProvider extends DiagramProvider<BnfRule> {
  public static final String ID = "GRAMMAR";

  private record Item(BnfRule rule, RuleGraphHelper.Cardinality cardinality) {
  }

  private final DiagramVisibilityManager myVisibilityManager = new EmptyDiagramVisibilityManager();

  private static final DiagramCategory[] CATEGORIES = new DiagramCategory[]{
    new DiagramCategory(GrammarKitBundle.messagePointer("diagram.category.properties"), PlatformIcons.PROPERTY_ICON, true, false)};

  private static class BnfNodeContentManager extends AbstractDiagramNodeContentManager {
    @Override
    public boolean isInCategory(@Nullable Object nodeElement,
                                @Nullable Object item,
                                @NotNull DiagramCategory category,
                                @Nullable DiagramBuilder builder) {
      return true;
    }

    @Override
    public DiagramCategory @NotNull [] getContentCategories() {
      return CATEGORIES;
    }
  }

  private final DiagramElementManager<BnfRule> myElementManager = new AbstractDiagramElementManager<>() {
    @Override
    public BnfRule findInDataContext(@NotNull DataContext context) {
      PsiFile file = CommonDataKeys.PSI_FILE.getData(context);
      if (!(file instanceof BnfFile bnfFile)) return null;
      List<BnfRule> rules = bnfFile.getRules();
      return ContainerUtil.getFirstItem(rules);
    }

    @Override
    public boolean isAcceptableAsNode(Object o) {
      return o instanceof PsiNamedElement;
    }

    @Override
    public @Nullable @Nls String getEditorTitle(BnfRule element, @NotNull Collection<BnfRule> additionalElements) {
      PsiFile file = element == null ? null : element.getContainingFile();
      return file == null ? null : file.getName();
    }

    @Override
    public String getElementTitle(BnfRule o) {
      return o.getName();
    }

    @Override
    public @Nullable SimpleColoredText getItemName(@Nullable BnfRule element, @Nullable Object item, @NotNull DiagramBuilder builder) {
      if (item instanceof Item o) item = o.rule;
      if (item instanceof PsiNamedElement) {
        return new SimpleColoredText(StringUtil.notNullize(((PsiNamedElement)item).getName()), DEFAULT_TITLE_ATTR);
      }
      return null;
    }

    @Override
    public Object @NotNull [] getNodeItems(BnfRule element) {
      Map<PsiElement, RuleGraphHelper.Cardinality> map = myGraphHelper.getFor(element);
      List<Item> entries = ContainerUtil.mapNotNull(map.entrySet(), p -> p.getKey() instanceof BnfRule o ? new Item(o, p.getValue()) : null);
      Collections.sort(entries, (o, o1) -> Comparing.compare(o.rule.getName(), o1.rule.getName()));
      return entries.toArray();
    }

    @Override
    public @Nullable SimpleColoredText getItemType(@Nullable BnfRule element, @Nullable Object item, @Nullable DiagramBuilder builder) {
      if (item == null) return null;
      RuleGraphHelper.Cardinality cardinality = ((Item)item).cardinality;
      String text = RuleGraphHelper.getCardinalityText(cardinality);
      if (StringUtil.isNotEmpty(text)) {
        return new SimpleColoredText(" " + text + " ", SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
      }
      return null;
    }

    @Override
    public @Nullable Icon getItemIcon(@Nullable BnfRule element, @Nullable Object item, @Nullable DiagramBuilder builder) {
      if (item == null) return null;
      return ((Item)item).rule.getIcon(0);
    }

    @Override
    public @Nullable @Nls String getNodeTooltip(BnfRule rule) {
      return null;
    }
  };

  private final DiagramVfsResolver<BnfRule> myVfsResolver = new DiagramVfsResolver<>() {
    @Override
    public String getQualifiedName(BnfRule element) {
      if (element == null) return null;
      PsiFile psiFile = element.getContainingFile();
      VirtualFile virtualFile = psiFile == null ? null : psiFile.getVirtualFile();
      if (virtualFile == null) return null;
      return String.format("%s?rule=%s", virtualFile.getUrl(), element.getName());
    }

    @Override
    public BnfRule resolveElementByFQN(@NotNull String s, @NotNull Project project) {
      List<String> parts = StringUtil.split(s, "?rule=");
      if (parts.size() < 1 || parts.size() > 2) return null;
      VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(parts.get(0));
      PsiFile psiFile = virtualFile == null ? null : PsiManager.getInstance(project).findFile(virtualFile);
      if (!(psiFile instanceof BnfFile bnfFile)) return null;
      return parts.size() == 2 ? ((BnfFile)psiFile).getRule(parts.get(1)) : ContainerUtil.getFirstItem(bnfFile.getRules());
    }
  };
  private final DiagramRelationshipManager<BnfRule> myRelationshipManager = new DiagramRelationshipManager<>() {
    @Override
    public @Nullable DiagramRelationshipInfo getDependencyInfo(BnfRule rule, BnfRule t1, DiagramCategory category) {
      return null;
    }
  };
  private final DiagramExtras<BnfRule> myExtras = new DiagramExtras<>();
  private RuleGraphHelper myGraphHelper;

  public BnfDiagramProvider() {
    myElementManager.setUmlProvider(this);
  }

  @Pattern("[a-zA-Z0-9_-]*")
  @Override
  public @NotNull String getID() {
    return ID;
  }

  @Override
  public @NotNull DiagramVisibilityManager createVisibilityManager() {
    return myVisibilityManager;
  }

  @Override
  public @NotNull DiagramNodeContentManager createNodeContentManager() {
    return new BnfNodeContentManager();
  }

  @Override
  public @NotNull DiagramElementManager<BnfRule> getElementManager() {
    return myElementManager;
  }

  @Override
  public @NotNull DiagramVfsResolver<BnfRule> getVfsResolver() {
    return myVfsResolver;
  }

  @Override
  public @NotNull DiagramRelationshipManager<BnfRule> getRelationshipManager() {
    return myRelationshipManager;
  }

  @Override
  public @NotNull DiagramDataModel<BnfRule> createDataModel(@NotNull Project project,
                                                            @Nullable BnfRule element,
                                                            @Nullable VirtualFile file,
                                                            @NotNull DiagramPresentationModel presentationModel) {
    return new MyDataModel(project, element == null ? null : (BnfFile)element.getContainingFile(), this);
  }

  @Override
  public @NotNull DiagramExtras<BnfRule> getExtras() {
    return myExtras;
  }

  @Override
  public DiagramCategory @NotNull [] getAllContentCategories() {
    return CATEGORIES;
  }

  @Override
  public @NotNull String getActionName(boolean isPopup) {
    return GrammarKitBundle.message("diagram.action.name");
  }

  @Override
  public @NotNull String getPresentableName() {
    return GrammarKitBundle.message("diagram.presentable.name");
  }

  private static class MyDataModel extends DiagramDataModel<BnfRule> implements ModificationTracker {

    private final BnfFile myFile;
    private final Collection<DiagramNode<BnfRule>> myNodes = new HashSet<>();
    private final Collection<DiagramEdge<BnfRule>> myEdges = new HashSet<>();

    MyDataModel(Project project, BnfFile file, BnfDiagramProvider provider) {
      super(project, provider);
      myFile = file;
    }

    @Override
    public @NotNull Collection<DiagramNode<BnfRule>> getNodes() {
      return myNodes;
    }

    @Override
    public @NotNull Collection<DiagramEdge<BnfRule>> getEdges() {
      return myEdges;
    }

    @Override
    public @NotNull String getNodeName(DiagramNode<BnfRule> node) {
      return StringUtil.notNullize(node.getTooltip());
    }

    @Override
    public DiagramNode<BnfRule> addElement(BnfRule psiElement) {
      return null;
    }

    @Override
    public void refreshDataModel() {
      myNodes.clear();
      myEdges.clear();

      RuleGraphHelper ruleGraphHelper = RuleGraphHelper.getCached(myFile);
      ((BnfDiagramProvider)getProvider()).myGraphHelper = ruleGraphHelper;

      Map<BnfRule, DiagramNode<BnfRule>> nodeMap = new HashMap<>();
      List<BnfRule> rules = myFile.getRules();
      BnfRule root = ContainerUtil.getFirstItem(rules);
      for (BnfRule rule : rules) {
        if (rule != root && !RuleGraphHelper.hasPsiClass(rule)) continue;
        DiagramNode<BnfRule> diagramNode = new PsiDiagramNode<>(rule, getProvider()) {
          @Override
          public String getTooltip() {
            return getIdentifyingElement().getName();
          }
        };
        nodeMap.put(rule, diagramNode);
        myNodes.add(diagramNode);
      }
      for (BnfRule rule : rules) {
        if (rule != root && !RuleGraphHelper.hasPsiClass(rule)) continue;
        Map<PsiElement, RuleGraphHelper.Cardinality> map = ruleGraphHelper.getFor(rule);

        BnfRule superRule = myFile.getRule(getAttribute(rule, KnownAttribute.EXTENDS));
        if (superRule != null) {
          DiagramNode<BnfRule> source = nodeMap.get(rule);
          DiagramNode<BnfRule> target = nodeMap.get(superRule);
          if (source == null || target == null) continue;
          myEdges.add(new DiagramEdgeBase<>(source, target,
                                            new DiagramRelationshipInfoAdapter("EXTENDS", DiagramLineType.DASHED,
                                                                               "extends") {

                                              @Override
                                              public Shape getStartArrow() {
                                                return DELTA;
                                              }
                                            }) {

            @Override
            public @NotNull String getName() {
              return "";
            }
          });
        }
        for (PsiElement element : map.keySet()) {
          if (!(element instanceof BnfRule)) continue;
          RuleGraphHelper.Cardinality cardinality = map.get(element);
          assert cardinality != RuleGraphHelper.Cardinality.NONE;

          DiagramNode<BnfRule> source = nodeMap.get(rule);
          DiagramNode<BnfRule> target = nodeMap.get(element);
          if (source == null || target == null) continue;
          myEdges.add(
            new DiagramEdgeBase<>(source, target, new DiagramRelationshipInfoAdapter("CONTAINS", DiagramLineType.SOLID, "") {
              @Override
              public Label getUpperCenterLabel() {
                return new ColoredLabel(StringUtil.toLowerCase(cardinality.name()));
              }

              @Override
              public Shape getStartArrow() {
                return DELTA;
              }
            }) {

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
                return JBColor.BLUE;
              }

              @Override
              public @NotNull String getName() {
                return "";
              }
            });
        }
      }
    }

    @Override
    public @NotNull ModificationTracker getModificationTracker() {
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
