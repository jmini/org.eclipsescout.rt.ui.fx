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
package org.eclipsescout.rt.ui.fx.control;

import java.util.LinkedHashSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import org.eclipsescout.rt.ui.fx.control.internal.DesktopSkin;

public class Desktop extends Control {

  public static final String DEFAULT_STYLE = "/css/defaultDesktop.css";
  public static final String DEFAULT_STYLE_CLASS = "desktop";

  /**************************************************************
   * Constructor
   *************************************************************/

  /**
   * Creates a desktop manager.
   */
  public Desktop() {
    getStyleClass().setAll(DEFAULT_STYLE_CLASS);

    setFocusTraversable(false);
  }

  /**************************************************************
   * Properties
   *************************************************************/

  /**
   * An ObservableSet of {@code InternalWindow}s which are added to the desktop
   */
  private final ObservableSet<InternalWindow> internalWindows = FXCollections.observableSet(new LinkedHashSet<InternalWindow>());

  public final ObservableSet<InternalWindow> getInternalWindows() {
    return internalWindows;
  }

  /**************************************************************
   * Methods
   *************************************************************/

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getUserAgentStylesheet() {
    return this.getClass().getResource(DEFAULT_STYLE).toExternalForm();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Skin<?> createDefaultSkin() {
    return new DesktopSkin(this);
  }

}
