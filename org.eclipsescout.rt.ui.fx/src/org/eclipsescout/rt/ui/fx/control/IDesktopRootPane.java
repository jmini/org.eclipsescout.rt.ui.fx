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

import javafx.scene.Node;

/**
 * Interface which must be implemented on the root pane of the desktop. It offers methods to add and remove internal
 * windows.
 */
public interface IDesktopRootPane {

  /**
   * Adds the specified internal window to the desktop root pane
   *
   * @param w
   *          The internal window to add
   */
  public void addWindow(InternalWindow w);

  /**
   * Removes the specified internal window from the desktop root pane
   *
   * @param w
   *          The internal window ro remove
   */
  public void removeWindow(InternalWindow w);

  /**
   * Returns the node which represents the desktop root pane.
   * This method can be used to retrieve the root pane and add it to the scene graph.
   *
   * @return the root pane
   */
  public Node getNode();

}
