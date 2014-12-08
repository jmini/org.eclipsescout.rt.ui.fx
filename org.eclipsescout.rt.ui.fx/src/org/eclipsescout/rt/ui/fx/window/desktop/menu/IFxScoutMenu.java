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
import org.eclipsescout.rt.ui.fx.IFxEnvironment;

/**
 * 
 */
public interface IFxScoutMenu<T extends IMenu, U extends MenuItem> {

  /**
   * @param menu
   * @param env
   */
  public void createField(T menu, IFxEnvironment env);

  /**
 * 
 */
  public void initializeFx();

  /**
   * @return
   */
  public T getScoutMenu();

  /**
   * @return
   */
  public IFxEnvironment getFxEnvironment();

  /**
   * @param fxMenu
   */
  public void setFxMenu(U fxMenu);

  /**
   * @return
   */
  public U getFxMenu();

}
