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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.checkbox.ICheckBoxMenu;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;

/**
 * 
 */
public class FxMenuCreator {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(FxMenuCreator.class);

  public FxMenuCreator() {
  }

  /**
   * @param env
   * @param scoutMenus
   * @return
   */
  public List<Menu> createTopLevelMenus(IFxEnvironment env, List<? extends IMenu> scoutMenus) {
    List<MenuItem> fxTopLevelMenuItems = createSubMenus(env, scoutMenus);
    List<Menu> fxTopLevelMenus = new ArrayList<Menu>(scoutMenus.size());
    for (MenuItem fxMenuItem : fxTopLevelMenuItems) {
      if (fxMenuItem instanceof Menu) {
        // menu item has children add it to menu bar
        fxTopLevelMenus.add((Menu) fxMenuItem);
      }
      else {
        // menu item has no children don't add it to menu bar
        LOG.warn("Can not add " + fxMenuItem.getId() + " to top level menu");
      }
    }
    return fxTopLevelMenus;
  }

  private List<MenuItem> createSubMenus(IFxEnvironment env, List<? extends IMenu> scoutMenus) {
    List<MenuItem> fxMenuItems = new ArrayList<MenuItem>(scoutMenus.size());
    boolean lastActionWasSeparator = true;
    for (IMenu scoutMenu : scoutMenus) {
      if (scoutMenu.isSeparator()) {
        if (!lastActionWasSeparator && scoutMenu.isVisible()) {
          fxMenuItems.add(new SeparatorMenuItem());
          lastActionWasSeparator = true;
        }
      }
      else {
        if (scoutMenu.isVisible()) {
          lastActionWasSeparator = false;
        }
        MenuItem menuItem = createMenuItem(scoutMenu, env);
        if (menuItem instanceof Menu && scoutMenu.hasChildActions()) {
          // go recursive
          List<MenuItem> childFxMenuItems = createSubMenus(env, scoutMenu.getChildActions());
          ((Menu) menuItem).getItems().addAll(childFxMenuItems);
        }
        fxMenuItems.add(menuItem);
      }
    }

    // remove trailing separators
    ListIterator<MenuItem> it = fxMenuItems.listIterator(fxMenuItems.size());
    MenuItem item;
    while (it.hasPrevious()) {
      item = it.previous();
      if (item instanceof SeparatorMenuItem) {
        it.remove();
      }
      else if (!item.isVisible()) {
        // nop
      }
      else {
        break;
      }
    }
    return fxMenuItems;
  }

  private MenuItem createMenuItem(IMenu scoutMenu, IFxEnvironment env) {
    if (scoutMenu instanceof ICheckBoxMenu) {
      FxScoutCheckBoxMenu fxMenu = new FxScoutCheckBoxMenu();
      fxMenu.createField((ICheckBoxMenu) scoutMenu, env);
      return fxMenu.getFxMenu();
    }
    else {
      if (scoutMenu.hasChildActions()) {
        FxScoutMenu fxMenu = new FxScoutMenu();
        fxMenu.createField(scoutMenu, env);
        return fxMenu.getFxMenu();
      }
      else {
        FxScoutMenuItem fxMenu = new FxScoutMenuItem();
        fxMenu.createField(scoutMenu, env);
        return fxMenu.getFxMenu();
      }
    }
  }
}
