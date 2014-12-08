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
package org.eclipsescout.rt.ui.fx.basic;

import javafx.scene.Node;

import org.eclipse.scout.commons.beans.IPropertyObserver;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;

/**
 * 
 */
public interface IFxScoutComposite<T extends IPropertyObserver> {

  /**
   * Creates and initializes the field.
   * 
   * @param model
   *          corresponding scout model
   * @param environment
   *          environment in which the field lives
   */
  public void createField(T model, IFxEnvironment environment);

  /**
   * @return scout model
   */
  public T getScoutObject();

  /**
   * @return environment
   */
  public IFxEnvironment getFxEnvironment();

  /**
   * Connects the ui field with the scout model.
   */
  void connectToScout();

  /**
   * Disconnects the ui field from the scout model.
   */
  void disconnectFromScout();

  /**
   * @return the core ui element
   */
  Node getFxField();

  /**
   * @return pane, which consists of all corresponding
   *         elements of this textfield and itself
   */
  Node getFxContainer();

}
