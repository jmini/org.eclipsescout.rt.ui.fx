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
package org.eclipsescout.rt.ui.fx.layout;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

import org.eclipsescout.rt.ui.fx.FxStyleUtility;

/**
 * Extension of {@link BorderPane} to provide horizontal and vertical gaps between the child nodes.
 * The nodes can be added as normal. Each time the pane is layouted a margin is set to the child nodes.
 */
public class BorderPaneEx extends BorderPane {
  private int hgap;
  private int vgap;

  public BorderPaneEx() {
    this(0, 0);
  }

  public BorderPaneEx(int hgap, int vgap) {
    super();
    this.hgap = hgap;
    this.vgap = vgap;
  }

  /**
   * Sets the background color.
   * 
   * @param color
   *          html color
   */
  public void setBackgroundColor(String color) {
    FxStyleUtility.setBackgroundColor(this, color);
  }

  @Override
  protected void layoutChildren() {
    if (getTop() != null) {
      setMargin(getTop(), new Insets(0, 0, vgap, 0));
    }
    if (getRight() != null) {
      setMargin(getRight(), new Insets(0, 0, 0, hgap));
    }
    if (getBottom() != null) {
      setMargin(getBottom(), new Insets(vgap, 0, 0, 0));
    }
    if (getLeft() != null) {
      setMargin(getLeft(), new Insets(0, hgap, 0, 0));
    }
    super.layoutChildren();
  }

  /**
   * Removes all child nodes from this node by calling getChildren().clear().
   */
  public void clearChildren() {
    getChildren().clear();
  }

}
