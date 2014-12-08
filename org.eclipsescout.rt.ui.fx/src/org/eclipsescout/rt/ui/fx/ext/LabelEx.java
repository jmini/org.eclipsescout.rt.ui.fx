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
package org.eclipsescout.rt.ui.fx.ext;

import javafx.scene.control.Label;

import org.eclipse.scout.commons.exception.IProcessingStatus;
import org.eclipse.scout.rt.shared.data.basic.FontSpec;
import org.eclipsescout.rt.ui.fx.FxStyleUtility;

/**
 * Extended version of the javafx Label.
 */
public class LabelEx extends Label {

  private IProcessingStatus m_status;

  /**
   * Sets the font weight by calling the
   * corresponding method of the FxStyleUtility.
   * 
   * @param weight
   *          weight to be set
   */
  public void setFontWeight(String weight) {
    FxStyleUtility.setFontWeight(this, weight);
  }

  /**
   * Sets the background color by calling the
   * corresponding method of the FxStyleUtility.
   * 
   * @param color
   *          color to be set
   */
  public void setBackgroundColor(String color) {
    FxStyleUtility.setBackgroundColor(this, color);
  }

  /**
   * Sets the text color by calling the
   * corresponding method of the FxStyleUtility.
   * 
   * @param color
   *          color to be set
   */
  public void setTextColor(String color) {
    FxStyleUtility.setTextColor(this, color);
  }

  /**
   * Sets the text font by calling the
   * corresponding method of the FxStyleUtility.
   * 
   * @param font
   *          font specification of
   *          the scout framework to be set
   */
  public void setTextFont(FontSpec font) {
    FxStyleUtility.setTextFont(this, font);
  }
}
