/*******************************************************************************
 * Copyright (c) 2014 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipsescout.rt.ui.fx.control.internal;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;

import org.eclipsescout.rt.ui.fx.control.MultiSplitPane;

public class MultiSplitPaneSkin extends SkinBase<MultiSplitPane> {

  private PrimarySplitPane primarySplitPane;

  public MultiSplitPaneSkin(MultiSplitPane multiSplitPane) {
    super(multiSplitPane);

    init();
  }

  private void init() {
    Orientation secondaryOrientation;
    if (Orientation.HORIZONTAL == getSkinnable().getPrimaryOrientation()) {
      secondaryOrientation = Orientation.VERTICAL;
    }
    else {
      secondaryOrientation = Orientation.HORIZONTAL;
    }

    ReadOnlyProperty<Node>[][] nodes = getSkinnable().getElementsUnmodifiable();
    SecondarySplitPane[] splitPanes = new SecondarySplitPane[nodes.length];
    for (int i = 0; i < nodes.length; i++) {
      splitPanes[i] = new SecondarySplitPane(nodes[i]);
      splitPanes[i].setOrientation(secondaryOrientation);
    }

    primarySplitPane = new PrimarySplitPane(splitPanes);
    primarySplitPane.setOrientation(getSkinnable().getPrimaryOrientation());

    getChildren().add(primarySplitPane);
  }

  private final class PrimarySplitPane extends SplitPane {

    private final String DEFAULT_STYLE_CLASS = "primary-split-pane";

    private final SecondarySplitPane[] splitPanes;

    public PrimarySplitPane(SecondarySplitPane[] splitPanes) {
      this.splitPanes = splitPanes;

      getStyleClass().add(DEFAULT_STYLE_CLASS);

      // register list change listener
      for (int i = 0; i < this.splitPanes.length; i++) {
        this.splitPanes[i].getItems().addListener(new P_ListChangeListener(this.splitPanes[i]));
        // add nodes if they already exist
        if (this.splitPanes[i].getItems().size() != 0) {
          handleItemAdd(this.splitPanes[i]);
        }
      }
    }

    private void rearangeDividers() {
      if (!getItems().isEmpty()) {
        double delta = 1.0 / getItems().size();
        for (int i = 0; i < getItems().size() - 1; i++) {
          setDividerPosition(i, delta * i + delta);
        }
      }
    }

    private void handleItemRemove(Node sp) {
      getItems().remove(sp);
      rearangeDividers();
    }

    private void handleItemAdd(Node sp) {
      int c = 0;
      int i = 0;
      while (i < splitPanes.length && splitPanes[i] != sp) {
        if (getItems().contains(splitPanes[i])) {
          c++;
        }
        i++;
      }
      getItems().add(c, sp);
      rearangeDividers();
    }

    private final class P_ListChangeListener implements ListChangeListener<Node> {

      private final Node splitPane;
      private int lastSize = 0;

      public P_ListChangeListener(Node sp) {
        splitPane = sp;
      }

      @Override
      public void onChanged(Change<? extends Node> c) {
        if (c.getList().size() == 0) {
          handleItemRemove(splitPane);
        }
        else if (c.getList().size() == 1 && lastSize == 0) {
          handleItemAdd(splitPane);
        }
        lastSize = c.getList().size();
      }
    }
  }

  private final class SecondarySplitPane extends SplitPane {

    private final String DEFAULT_STYLE_CLASS = "secondary-split-pane";
    private final ReadOnlyProperty<Node>[] nodes;

    public SecondarySplitPane(ReadOnlyProperty<Node>[] objectProperties) {
      this.nodes = objectProperties;

      getStyleClass().add(DEFAULT_STYLE_CLASS);

      // register change listener
      for (int i = 0; i < this.nodes.length; i++) {
        this.nodes[i].addListener(new P_ChangeListener());
        // add nodes if they already exist
        if (this.nodes[i].getValue() != null) {
          handleItemAdd(this.nodes[i].getValue());
        }
      }
    }

    private void rearangeDividers() {
      if (!getItems().isEmpty()) {
        double delta = 1.0 / getItems().size();
        for (int i = 0; i < getItems().size() - 1; i++) {
          setDividerPosition(i, delta * i + delta);
        }
      }
    }

    protected void handleItemRemove(Node sp) {
      getItems().remove(sp);
      rearangeDividers();
    }

    protected void handleItemAdd(Node sp) {
      int c = 0;
      int i = 0;
      while (i < nodes.length && nodes[i].getValue() != sp) {
        if (getItems().contains(nodes[i].getValue())) {
          c++;
        }
        i++;
      }
      getItems().add(c, sp);
      rearangeDividers();
    }

    private final class P_ChangeListener implements ChangeListener<Node> {

      @Override
      public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
        if (oldValue != null) {
          handleItemRemove(oldValue);
        }
        if (newValue != null) {
          handleItemAdd(newValue);
        }
      }

    }
  }
}
