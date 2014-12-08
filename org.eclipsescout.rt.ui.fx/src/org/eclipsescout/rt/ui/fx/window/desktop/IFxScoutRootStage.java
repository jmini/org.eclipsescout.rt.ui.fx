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
package org.eclipsescout.rt.ui.fx.window.desktop;

import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipsescout.rt.ui.fx.basic.IFxScoutComposite;
import org.eclipsescout.rt.ui.fx.control.InternalWindow;

/**
 *
 */
public interface IFxScoutRootStage extends IFxScoutComposite<IDesktop> {

  /**
   * start GUI process by presenting a desktop frame use this method to show the
   * stage, not getFxStage().show()
   */
  void showFxStage();

  void addView(InternalWindow window);

  void removeView(InternalWindow window);

}
