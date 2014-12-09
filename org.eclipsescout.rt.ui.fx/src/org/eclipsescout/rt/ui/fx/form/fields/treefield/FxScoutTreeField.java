/*******************************************************************************
 * Copyright (c) 2014 Jeremie Bresson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 ******************************************************************************/
package org.eclipsescout.rt.ui.fx.form.fields.treefield;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

import org.eclipse.scout.rt.client.ui.basic.tree.ITree;
import org.eclipse.scout.rt.client.ui.basic.tree.ITreeNode;
import org.eclipse.scout.rt.client.ui.form.fields.treefield.ITreeField;
import org.eclipsescout.rt.ui.fx.LogicalGridPane;
import org.eclipsescout.rt.ui.fx.ext.FxStatusPaneEx;
import org.eclipsescout.rt.ui.fx.form.fields.FxScoutFieldComposite;

/**
 *
 */
public class FxScoutTreeField extends FxScoutFieldComposite<ITreeField> implements IFxScoutTreeField {

  private TreeItem<ITreeNode> m_treeRoot;

  @Override
  protected void initialize() {
    LogicalGridPane pane = new LogicalGridPane(getFxEnvironment(), 1, 0);

    FxStatusPaneEx label = getFxEnvironment().createStatusLabel(getScoutObject());
    pane.getChildren().add(label);

    m_treeRoot = new TreeItem<ITreeNode>();
    m_treeRoot.setExpanded(true);

    final TreeView<ITreeNode> treeView = new TreeView<ITreeNode>();
    treeView.setShowRoot(false);
    treeView.setRoot(m_treeRoot);
    treeView.setCellFactory(new Callback<TreeView<ITreeNode>, TreeCell<ITreeNode>>() {

      @Override
      public TreeCell<ITreeNode> call(TreeView<ITreeNode> node) {
        return new FxScoutTreeCell(getFxEnvironment());
      }
    });

    pane.getChildren().add(treeView);

    setFxContainer(pane);
    setFxStatusPane(label);
    setFxField(treeView);
  }

  @Override
  protected void attachScout() {
    super.attachScout();
    setTreeFromScout(getScoutObject().getTree());
  }

  protected void setTreeFromScout(ITree tree) {
    mapTreeItem(tree.getRootNode(), m_treeRoot);
  }

  private void mapTreeItem(ITreeNode scoutParentNode, TreeItem<ITreeNode> fxParentNode) {
    fxParentNode.setValue(scoutParentNode);
    for (ITreeNode scoutNode : scoutParentNode.getChildNodes()) {
      TreeItem<ITreeNode> fxNode = new TreeItem<ITreeNode>();
      fxParentNode.getChildren().add(fxNode);
      mapTreeItem(scoutNode, fxNode);
    }
  }

  @Override
  protected void handleScoutPropertyChange(String name, Object newValue) {
    super.handleScoutPropertyChange(name, newValue);
    if (name.equals(ITreeField.PROP_TREE)) {
      setTreeFromScout((ITree) newValue);
    }
  }

}
