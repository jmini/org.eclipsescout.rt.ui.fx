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
package org.eclipsescout.rt.ui.fx.window.desktop.menu;

import javafx.scene.control.CheckMenuItem;

import org.eclipse.scout.rt.client.ui.action.menu.checkbox.ICheckBoxMenu;

/**
 * 
 */
public class FxScoutCheckBoxMenu extends AbstractFxScoutMenu<ICheckBoxMenu, CheckMenuItem> {

  @Override
  public void initializeFx() {
    CheckMenuItem checkMenuItem = new CheckMenuItem();
    checkMenuItem.setSelected(getScoutMenu().isSelected());

    setFxMenu(checkMenuItem);
  }

}
