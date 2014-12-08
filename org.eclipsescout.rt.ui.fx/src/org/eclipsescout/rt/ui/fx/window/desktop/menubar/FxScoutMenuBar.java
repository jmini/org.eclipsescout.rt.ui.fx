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
package org.eclipsescout.rt.ui.fx.window.desktop.menubar;

import java.util.List;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipsescout.rt.ui.fx.basic.FxScoutComposite;

/**
 *
 */
public class FxScoutMenuBar extends FxScoutComposite<IDesktop> {

  private int m_topLevelMenuCount;

  @Override
  protected void initialize() {
    m_topLevelMenuCount = getScoutObject().getMenus().size();
    MenuBar menuBar = new MenuBar();
    setFxField(menuBar);
    rebuildMenuBar();
  }

  public boolean isEmpty() {
    return m_topLevelMenuCount == 0;
  }

  public MenuBar getFxMenuBar() {
    return (MenuBar) getFxField();
  }

  /**
   *
   */
  private void rebuildMenuBar() {
    List<IMenu> topLevelMenus = getScoutObject().getMenus();
    MenuBar menuBar = getFxMenuBar();
    menuBar.getMenus().clear();
    List<Menu> menus = getFxEnvironment().createTopLevelMenus(topLevelMenus);
    menuBar.getMenus().addAll(menus);
    //menuBar.getMenus().addAll(new AppendActionsInjector().createTopLevelMenus(null, Arrays.asList(topLevelMenus)));
  }
}
