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

import org.eclipse.scout.rt.client.ui.basic.tree.ITreeNode;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;

/**
 *
 */
public class FxScoutTreeCell extends TreeCell<ITreeNode> {

  private IFxEnvironment fxEnvironment;

  /**
   * @param fxEnvironment
   */
  public FxScoutTreeCell(IFxEnvironment fxEnvironment) {
    this.fxEnvironment = fxEnvironment;
  }

  @Override
  protected void updateItem(ITreeNode node, boolean empty) {
    super.updateItem(node, empty);
    if (empty) {
      setText(null);
      setGraphic(null);
    }
    else {
      setText(node.getCell().getText());
      setGraphic(fxEnvironment.getImageView(node.getCell().getIconId()));
    }
  }
}
