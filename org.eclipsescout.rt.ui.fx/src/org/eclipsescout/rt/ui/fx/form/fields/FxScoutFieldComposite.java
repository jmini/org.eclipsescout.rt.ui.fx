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

import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import org.eclipse.scout.commons.exception.IProcessingStatus;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.shared.data.basic.FontSpec;
import org.eclipsescout.rt.ui.fx.FxLayoutUtility;
import org.eclipsescout.rt.ui.fx.FxStyleUtility;
import org.eclipsescout.rt.ui.fx.LogicalGridData;
import org.eclipsescout.rt.ui.fx.basic.FxScoutComposite;
import org.eclipsescout.rt.ui.fx.ext.FxStatusPaneEx;

/**
 *
 */
public abstract class FxScoutFieldComposite<T extends IFormField> extends FxScoutComposite<T> implements IFxScoutFormField<T> {

  private Node m_fxContainer;
  private FxStatusPaneEx m_statusPane;
  // cache
  private List<IKeyStroke> m_installedScoutKs;

  public FxScoutFieldComposite() {
    super();
  }

  @Override
  protected void attachScout() {
    super.attachScout();
    IFormField scoutField = getScoutObject();
    if (scoutField != null) {
      setVisibleFromScout(scoutField.isVisible());
      setLabelWidthInPixelFromScout();
      setLabelHorizontalAlignmentFromScout();
      setEnabledFromScout(scoutField.isEnabled());

      setLabelBackgroundFromScout(scoutField.getLabelBackgroundColor());
      setLabelForegroundFromScout(scoutField.getLabelForegroundColor());
      setLabelFontFromScout(scoutField.getLabelFont());

      setMandatoryFromScout(scoutField.isMandatory());

      setErrorStatusFromScout(scoutField.getErrorStatus());
      setLabelFromScout(scoutField.getLabel());
      setLabelVisibleFromScout();
      setTooltipTextFromScout(scoutField.getTooltipText());
      if (getScoutObject().getLabelPosition() == IFormField.LABEL_POSITION_ON_FIELD && scoutField.getLabel() != null && scoutField.getTooltipText() == null) {
        setTooltipTextFromScout(scoutField.getLabel());
      }
      setBackgroundFromScout(scoutField.getBackgroundColor());
      setForegroundFromScout(scoutField.getForegroundColor());
      setFontFromScout(scoutField.getFont());
      setSaveNeededFromScout(scoutField.isSaveNeeded());
      setEmptyFromScout(scoutField.isEmpty());
      setFocusableFromScout(scoutField.isFocusable());
      setKeyStrokesFromScout();
    }
  }

  @Override
  public Node getFxContainer() {
    return m_fxContainer;
  }

  protected void setFxContainer(Node fxContainer) {
    m_fxContainer = fxContainer;
  }

  @Override
  public FxStatusPaneEx getFxStatusPane() {
    return m_statusPane;
  }

  protected void setFxStatusPane(FxStatusPaneEx statusPane) {
    m_statusPane = statusPane;
    if (m_statusPane != null) {
      m_statusPane.setText(getScoutObject().getLabel());
      LogicalGridData statusLabelGridData = null;
      if (getScoutObject().getLabelPosition() == IFormField.LABEL_POSITION_TOP) {
        statusLabelGridData = LogicalGridDataBuilder.createLabelOnTop(((IFormField) getScoutObject()).getGridData());
      }
      else {
        statusLabelGridData = LogicalGridDataBuilder.createLabel(getFxEnvironment(), ((IFormField) getScoutObject()).getGridData());
      }
      m_statusPane.getProperties().put(LogicalGridData.CLIENT_PROPERTY_NAME, statusLabelGridData);
    }
  }

  @Override
  protected boolean isHandleScoutPropertyChange(final String name, final Object newValue) {
    if (name.equals(IFormField.PROP_ENABLED) || name.equals(IFormField.PROP_VISIBLE)) {
      getFxEnvironment().postImmediateFxJob(new Runnable() {
        @Override
        public void run() {
          handleScoutPropertyChange(name, newValue);
        }
      });
    }
    return super.isHandleScoutPropertyChange(name, newValue);
  }

  @Override
  protected void handleScoutPropertyChange(String name, Object newValue) {
    super.handleScoutPropertyChange(name, newValue);
    if (name.equals(IFormField.PROP_ENABLED)) {
      setEnabledFromScout(((Boolean) newValue).booleanValue());
    }
    else if (name.equals(IFormField.PROP_FOCUSABLE)) {
      setFocusableFromScout(((Boolean) newValue).booleanValue());
    }
    else if (name.equals(IFormField.PROP_LABEL)) {
      setLabelFromScout((String) newValue);
    }
    else if (name.equals(IFormField.PROP_LABEL_VISIBLE)) {
      setLabelVisibleFromScout();
    }
    else if (name.equals(IFormField.PROP_TOOLTIP_TEXT)) {
      setTooltipTextFromScout((String) newValue);
    }
    else if (name.equals(IFormField.PROP_VISIBLE)) {
      setVisibleFromScout(((Boolean) newValue).booleanValue());
    }
    else if (name.equals(IFormField.PROP_MANDATORY)) {
      setMandatoryFromScout(((Boolean) newValue).booleanValue());
    }
    else if (name.equals(IFormField.PROP_ERROR_STATUS)) {
      setErrorStatusFromScout((IProcessingStatus) newValue);
    }
    else if (name.equals(IFormField.PROP_FOREGROUND_COLOR)) {
      setForegroundFromScout((String) newValue);
    }
    else if (name.equals(IFormField.PROP_BACKGROUND_COLOR)) {
      setBackgroundFromScout((String) newValue);
    }
    else if (name.equals(IFormField.PROP_FONT)) {
      setFontFromScout((FontSpec) newValue);
    }
    else if (name.equals(IFormField.PROP_LABEL_FOREGROUND_COLOR)) {
      setLabelForegroundFromScout((String) newValue);
    }
    else if (name.equals(IFormField.PROP_LABEL_BACKGROUND_COLOR)) {
      setLabelBackgroundFromScout((String) newValue);
    }
    else if (name.equals(IFormField.PROP_LABEL_FONT)) {
      setLabelFontFromScout((FontSpec) newValue);
    }
    else if (name.equals(IFormField.PROP_SAVE_NEEDED)) {
      setSaveNeededFromScout(((Boolean) newValue).booleanValue());
    }
    else if (name.equals(IFormField.PROP_EMPTY)) {
      setEmptyFromScout(((Boolean) newValue).booleanValue());
    }
    else if (name.equals(IFormField.PROP_KEY_STROKES)) {
      setKeyStrokesFromScout();
    }
  }

  /**
   * Disabled, Enables and controls wheter or not
   * the textfield is editable.
   *
   * @param b
   *          wheter or not the textfield is enabled
   */
  protected void setEnabledFromScout(boolean b) {
    if (getFxField() instanceof TextField) {
      TextField textField = (TextField) getFxField();
      textField.setEditable(b);
    }
    if (getFxField() != null) {
      getFxField().setDisable(!b);
    }
    if (getFxStatusPane() != null) {
      getFxStatusPane().setDisable(!b);
    }
  }

  /**
   * Defines wheter or not the textfield can be focused
   * when the user traverses the UI.
   *
   * @param b
   *          wheter or not the textfield is focusable
   */
  protected void setFocusableFromScout(boolean b) {
    if (getFxField() != null) {
      getFxField().setFocusTraversable(b);
    }
  }

  /**
   * Sets the label of the text if the parameter is not null.
   *
   * @param s
   *          text of the label to be set
   */
  protected void setLabelFromScout(String s) {
    if (getFxStatusPane() != null) {
      getFxStatusPane().setText(s);
    }
    if (getScoutObject().getLabelPosition() == IFormField.LABEL_POSITION_ON_FIELD) {
      // TODO implement onfieldlabelhandler
    }
  }

  /**
   * Gets the needed information from the scout object,
   * positions the label on the status pane,
   * sets the visibility of the status pane
   * and requests a layout pass on the parent pane,
   * on which all the elements are based on.
   */
  protected void setLabelVisibleFromScout() {
    if (getFxStatusPane() == null) {
      return;
    }

    boolean b = getScoutObject().isLabelVisible();
    if (getScoutObject().getLabelPosition() == IFormField.LABEL_POSITION_ON_FIELD) {
      getFxStatusPane().setText(null);
      LogicalGridData data = (LogicalGridData) getFxStatusPane().getProperties().get(LogicalGridData.CLIENT_PROPERTY_NAME);
      if (data != null) {
        data.widthHint = 0;
      }
    }
    getFxStatusPane().setVisible(b);
    if (getFxContainer() != null && getFxContainer() instanceof Parent) {
      ((Parent) getFxContainer()).requestLayout();
    }
  }

  /**
   * If the parameter is not null and not empty,
   * a new tooltip will be created and set on the textfield and on the status pane,
   * if they are not null.
   *
   * @param s
   *          text of the tooltip
   */
  protected void setTooltipTextFromScout(String s) {
    if (s != null && s.length() > 0) {
      Tooltip tt = new Tooltip(s);
      /*
       * FxNode has to be cast to Control as a Tooltip can only be set in this class.
       */
      if (Control.class.isAssignableFrom(getFxField().getClass())) {
        Control c = (Control) getFxField();
        if (c != null) {
          c.setTooltip(tt);
        }
      }

      if (getFxStatusPane() != null) {
        getFxStatusPane().setTooltip(tt);
      }
    }
  }

  /**
   * Sets the visibility of the textfield.
   *
   * @param b
   *          wheter or not the textfield is visible
   */
  protected void setVisibleFromScout(boolean b) {
    if (getFxContainer() != null) {
      getFxContainer().setVisible(b);
    }
    else if (getFxField() != null) {
      getFxField().setVisible(b);
    }
  }

  protected void setLabelHorizontalAlignmentFromScout() {
    if (getFxStatusPane() != null) {
      HPos hPos = FxLayoutUtility.createHorizontalAlignment(getScoutObject().getLabelHorizontalAlignment());
      Pos pos = FxLayoutUtility.createAlignment(VPos.CENTER, hPos);
      getFxStatusPane().setLabelPosition(pos);
    }
  }

  protected void setLabelWidthInPixelFromScout() {
    if (getFxStatusPane() != null) {
      int w = getScoutObject().getLabelWidthInPixel();
      if (w > 0) {
        getFxStatusPane().setLayoutWidthHint(w);
      }
      else if (w == IFormField.LABEL_WIDTH_DEFAULT) {
        getFxStatusPane().setLayoutWidthHint(getFxEnvironment().getFieldLabelWidth());
      }
      else if (w == IFormField.LABEL_WIDTH_UI) {
        getFxStatusPane().setLayoutWidthHint(0);
      }
    }
  }

  /**
   * Sets the font of the status label bold if true or normal if false.
   *
   * @param b
   *          true if font should be bold, false if normal
   */
  protected void setMandatoryFromScout(boolean b) {
    if (getFxStatusPane() != null) {
      getFxStatusPane().setMandatory(b);
    }
  }

  /**
   * Calls setStatus on the status pane to set the IProcessingStatus.
   *
   * @param s
   *          processing status
   */
  protected void setErrorStatusFromScout(IProcessingStatus s) {
    if (getFxStatusPane() != null) {
      getFxStatusPane().setStatus(s);
    }
  }

  /**
   * Sets the color from the scout framework as the new
   * enabled and calculated disabled textcolor for the textfield.
   *
   * @param scoutColor
   *          color defined in the scout framework
   */
  protected void setForegroundFromScout(String scoutColor) {
    Node n = getFxField();
    if (n != null && scoutColor != null) {
      FxStyleUtility.setTextColor(n, scoutColor);
    }
  }

  /**
   * Sets the desired background color on the textfield.
   *
   * @param scoutColor
   *          color from the scout framework
   */
  protected void setBackgroundFromScout(String scoutColor) {
    Node n = getFxField();
    if (n != null && scoutColor != null) {
      FxStyleUtility.setBackgroundColor(n, scoutColor);
    }
  }

  // TODO somehow can't render vivaldi and bold
  /**
   * Calls setTextFont on the textfield to set the font specification
   *
   * @param scoutFont
   *          font specification from the scout framework
   */
  protected void setFontFromScout(FontSpec scoutFont) {
    Node n = getFxField();
    if (n != null && scoutFont != null) {
      FxStyleUtility.setTextFont(n, scoutFont);
    }
  }

  /**
   * Sets the color of the text displayed on the label of the status pane.
   *
   * @param scoutColor
   *          color from the scout framework
   */
  protected void setLabelForegroundFromScout(String scoutColor) {
    if (getFxStatusPane() != null) {
      getFxStatusPane().setLabelTextColor(scoutColor);
    }
  }

  /**
   * Sets the background color of the status pane where the label is located.
   *
   * @param scoutColor
   *          color from the scout framework
   */
  protected void setLabelBackgroundFromScout(String scoutColor) {
    if (getFxStatusPane() != null) {
      getFxStatusPane().setBackgroundColor(scoutColor);
    }
  }

  /**
   * Sets the front of the status pane where the label is located.
   *
   * @param scoutFont
   *          font specification from the scout framework
   */
  protected void setLabelFontFromScout(FontSpec scoutFont) {
    if (getFxStatusPane() != null) {
      getFxStatusPane().setLabelFont(scoutFont);
    }
  }

  protected void setSaveNeededFromScout(boolean b) {
  }

  protected void setEmptyFromScout(boolean b) {
  }

  /**
   * Sets the keystrokes gathered from the scout model.
   */
  protected void setKeyStrokesFromScout() {
    Node node = getFxContainer();
    if (node == null) {
      node = getFxField();
    }
    if (node != null) {
      // remove old key strokes
      if (m_installedScoutKs != null) {
        for (int i = 0; i < m_installedScoutKs.size(); i++) {
          IKeyStroke scoutKs = m_installedScoutKs.get(i);
          // TODO implement keystrokes
        }
      }
      m_installedScoutKs = null;
      // add new key strokes
      List<IKeyStroke> scoutKeyStrokes = getScoutObject().getKeyStrokes();
      for (IKeyStroke scoutKs : scoutKeyStrokes) {
        // TODO implement keystrokes
      }
      m_installedScoutKs = scoutKeyStrokes;
    }
  }
}
