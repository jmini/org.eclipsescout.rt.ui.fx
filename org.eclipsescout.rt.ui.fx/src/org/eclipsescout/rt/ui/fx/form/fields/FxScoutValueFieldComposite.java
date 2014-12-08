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
package org.eclipsescout.rt.ui.fx.form.fields;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.scout.commons.exception.IProcessingStatus;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;

/**
 *
 */
public abstract class FxScoutValueFieldComposite<T extends IValueField<?>> extends FxScoutFieldComposite<T> {

  private final static String OK_STYLE_CLASS = "value-field-ok";
  private final static String INFO_STYLE_CLASS = "value-field-info";
  private final static String CANCEL_STYLE_CLASS = "value-field-cancel";
  private final static String ERROR_STYLE_CLASS = "value-field-error";
  private final static String WARNING_STYLE_CLASS = "value-field-warning";
  private final static String FATAL_STYLE_CLASS = "value-field-fatal";

  @Override
  protected void attachScout() {
    super.attachScout();
    IValueField f = getScoutObject();
    setValueFromScout(f.getValue());
    setDisplayTextFromScout(f.getDisplayText());
  }

  protected void setValueFromScout(Object o) {
  }

  protected void setDisplayTextFromScout(String s) {
  }

  /**
   * Calculates a disabled form of a given original color.
   *
   * @param origColor
   *          original color
   * @return disabled form of the original color
   */
  protected String getDisabledColor(String origColor) {
    if (origColor == null) {
      return null;
    }
    /**
     * some users wish that also the disabled fg color is the same as the fg
     * color, others wished the contrary. As a consequence, the disabled color
     * is now a ligthened up version of the fg color
     */
    String newColor = "";
    String colorPartStr = null;
    int colorPartInt = 0;
    for (int i = 0; i < 3; i++) {
      colorPartStr = origColor.substring(i * 2, i * 2 + 2);
      colorPartInt = Integer.parseInt(colorPartStr, 16);
      colorPartInt = (colorPartInt * 2 + 255) / 3;
      newColor += Integer.toHexString(colorPartInt);
    }
    return newColor;
  }

  @Override
  protected void handleScoutPropertyChange(String name, Object newValue) {
    super.handleScoutPropertyChange(name, newValue);
    if (name.equals(IValueField.PROP_VALUE)) {
      setValueFromScout(newValue);
    }
    else if (name.equals(IValueField.PROP_DISPLAY_TEXT)) {
      setDisplayTextFromScout((String) newValue);
    }
  }

  @Override
  protected void setErrorStatusFromScout(IProcessingStatus s) {
    super.setErrorStatusFromScout(s);

    if (getFxField() != null) {
      getFxField().getStyleClass().removeAll(OK_STYLE_CLASS, INFO_STYLE_CLASS, CANCEL_STYLE_CLASS, WARNING_STYLE_CLASS, ERROR_STYLE_CLASS, FATAL_STYLE_CLASS);
      if (s != null) {
        String styleClass = null;
        switch (s.getSeverity()) {
          case IStatus.OK:
            styleClass = OK_STYLE_CLASS;
            break;
          case IStatus.INFO:
            styleClass = INFO_STYLE_CLASS;
            break;
          case IStatus.CANCEL:
            styleClass = CANCEL_STYLE_CLASS;
            break;
          case IStatus.WARNING:
            styleClass = WARNING_STYLE_CLASS;
            break;
          case IStatus.ERROR:
            styleClass = ERROR_STYLE_CLASS;
            break;
          case IProcessingStatus.FATAL:
            styleClass = FATAL_STYLE_CLASS;
            break;
        }
        getFxField().getStyleClass().add(styleClass);
      }
    }
  }

}
