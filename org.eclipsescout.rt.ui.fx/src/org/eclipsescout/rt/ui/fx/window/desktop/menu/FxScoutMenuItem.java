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

import javafx.scene.control.MenuItem;

import org.eclipse.scout.rt.client.ui.action.menu.IMenu;

/**
 * 
 */
public class FxScoutMenuItem extends AbstractFxScoutMenu<IMenu, MenuItem> {

  @Override
  public void initializeFx() {
    MenuItem menuItem = new MenuItem();
    setFxMenu(menuItem);
  }

}
