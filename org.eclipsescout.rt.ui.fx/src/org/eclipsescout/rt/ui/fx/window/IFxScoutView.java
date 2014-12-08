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
package org.eclipsescout.rt.ui.fx.window;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 *
 */
public interface IFxScoutView {

  public Pane getFxContentPane();

  public void setName(String name);

  public void openView();

  public void closeView();

  public boolean isVisible();

  public boolean isActive();

  public void setTitle(String s);

  public void setCloseEnabled(boolean b);

  public void setMaximizeEnabled(boolean b);

  public void setMinimizeEnabled(boolean b);

  public void setMaximized(boolean b);

  public void setMinimized(boolean b);

  public void addFxScoutViewListener(IFxScoutViewListener listener);

  public void removeFxScoutViewListener(IFxScoutViewListener listener);

  /**
   * @param image
   */
  public void setIcon(Image image);

}
