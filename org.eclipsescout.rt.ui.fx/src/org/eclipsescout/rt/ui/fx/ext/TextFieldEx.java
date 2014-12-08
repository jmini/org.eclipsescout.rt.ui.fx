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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import org.eclipse.scout.rt.shared.data.basic.FontSpec;
import org.eclipsescout.rt.ui.fx.FxStyleUtility;

/**
 * Extended version of the javafx TextField.
 */
public class TextFieldEx extends TextField {

  private String m_enabledCol;
  private String m_disabledCol;

  public TextFieldEx() {
    super();
    this.disabledProperty().addListener(new P_DisabledChanged());
  }

  public TextFieldEx(String text) {
    super(text);
    this.disabledProperty().addListener(new P_DisabledChanged());
  }

  public void setDisabledColor(String c) {
    m_disabledCol = c;
  }

  public void setEnabledColor(String c) {
    m_enabledCol = c;
  }

  public String getDisabledColor() {
    return m_disabledCol;
  }

  public String getEnabledColor() {
    return m_enabledCol;
  }

  /**
   * Listener to react on changes of the disabled property.
   * Changes the color of the text.
   */
  private class P_DisabledChanged implements ChangeListener<Boolean> {

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
      changeColor(newValue);
    }

  }// end private class

  /**
   * Changes the color of the text.
   * 
   * @param disabled
   *          whether or not the field is disabled.
   */
  private void changeColor(boolean disabled) {
    if (disabled) {
      setTextColor(m_disabledCol);
    }
    else {
      setTextColor(m_enabledCol);
    }
  }

  /**
   * Sets the text color by calling the corresponding
   * method of the FxStyleUtility.
   * 
   * @param color
   *          color to be set
   */
  public void setTextColor(String color) {
    FxStyleUtility.setTextColor(this, color);
  }

  /**
   * Sets the background color by calling the corresponding
   * method of the FxStyleUtility.
   * 
   * @param scoutColor
   *          scout color to be set
   */
  public void setBackgroundColor(String color) {
    FxStyleUtility.setBackgroundColor(this, color);
  }

  /**
   * Sets the text font by calling the corresponding
   * method of the FxStyleUtility.
   * 
   * @param font
   *          font specification to be set
   */
  public void setTextFont(FontSpec font) {
    FxStyleUtility.setTextFont(this, font);
  }

  //TODO implement OnFieldLabelHandling

}
