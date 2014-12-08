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
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.exception.IProcessingStatus;
import org.eclipse.scout.rt.client.ui.form.fields.ScoutFieldStatus;
import org.eclipse.scout.rt.shared.AbstractIcons;
import org.eclipse.scout.rt.shared.data.basic.FontSpec;
import org.eclipsescout.rt.ui.fx.Activator;
import org.eclipsescout.rt.ui.fx.FxIcons;
import org.eclipsescout.rt.ui.fx.LogicalGridData;
import org.eclipsescout.rt.ui.fx.layout.BorderPaneEx;
import org.eclipsescout.rt.ui.fx.layout.HBoxEx;
import org.eclipsescout.rt.ui.fx.layout.HorizontalAlignment;

/**
 * 
 */
public class FxStatusPaneEx extends BorderPaneEx {

  private IProcessingStatus m_status;
  private boolean m_mandatoryLabelVisible;

  private HBoxEx m_labelPane;
  private LabelEx m_label;
  private LabelEx m_mandatoryLabel;
  private HBoxEx m_graphicPane;
  private LabelEx m_statusLabel;

  private Tooltip m_tooltip;

  private ImageView m_mandatoryImageViewEnabled;
  private ImageView m_mandatoryImageViewDisabled;

  private boolean statusHidesMandatoryGraphicEnabled;

  /**
   * 
   */
  public FxStatusPaneEx() {
    super(0, 0);

    m_tooltip = new Tooltip();

    m_mandatoryImageViewEnabled = Activator.getImageView(FxIcons.Mandantory);
    m_mandatoryImageViewDisabled = Activator.getImageView(FxIcons.MandantoryDisabled);

    m_labelPane = new HBoxEx(HorizontalAlignment.RIGHT, 0);
    setCenter(m_labelPane);

    m_graphicPane = new HBoxEx(HorizontalAlignment.RIGHT, 0);
    setRight(m_graphicPane);

    m_label = new LabelEx();
    m_label.setAlignment(Pos.CENTER_RIGHT);
    m_label.setVisible(false);
    m_labelPane.getChildren().add(m_label);

    m_statusLabel = new LabelEx();
    m_statusLabel.setVisible(false);
    m_graphicPane.getChildren().add(m_statusLabel);

    m_mandatoryLabel = new LabelEx();
    m_mandatoryLabel.setGraphic(m_mandatoryImageViewEnabled);
    m_mandatoryLabel.setVisible(false);
    m_mandatoryLabelVisible = false;
    setStatusHidesMandatoryIconEnabled(true);
    m_graphicPane.getChildren().add(m_mandatoryLabel);

    // listener to change graphic when this pane is enabled / disabled
    disabledProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        m_label.setDisable(newValue);
        m_mandatoryLabel.setGraphic(newValue ? m_mandatoryImageViewDisabled : m_mandatoryImageViewEnabled);
      }
    });
  }

  public void setMandatory(boolean b) {
    m_label.setFontWeight(b ? "bold" : "normal");
  }

  public void setLabelFont(FontSpec font) {
    m_label.setTextFont(font);
  }

  public void setLabelTextColor(String color) {
    m_label.setTextColor(color);
  }

  @Override
  public void setBackgroundColor(String color) {
    super.setBackgroundColor(color);
    m_label.setBackgroundColor(color);
  }

  public void showMandatoryIcon(boolean b) {
    m_mandatoryLabelVisible = b;
    if (isStatusHidesMandatoryIconEnabled() && m_status != null) {
      // Do not actually show the label, the error status always "wins" (bsh 2010-10-08)
      return;
    }
    m_mandatoryLabel.setVisible(b);
  }

  /**
   * makes the label fixed sized width
   */
  public void setFixedSize(int w) {
    LogicalGridData data = (LogicalGridData) getProperties().get(LogicalGridData.CLIENT_PROPERTY_NAME);
    if (data != null) {
      if (w > 0) {
        data.widthHint = w;
      }
      else {
        data.widthHint = 0;
      }
    }
  }

  public void setLayoutWidthHint(int w) {
    LogicalGridData data = (LogicalGridData) getProperties().get(LogicalGridData.CLIENT_PROPERTY_NAME);
    if (data != null) {
      data.widthHint = w;
    }
  }

  public void setLabelPosition(Pos pos) {
    m_labelPane.setAlignment(pos);
  }

  public String getText() {
    return m_label.getText();
  }

  public void setText(String text) {
    m_label.setText(text);
    m_label.setVisible(StringUtility.hasText(text)); // Hide empty labels (so the spacing is not too big within SequenceBoxes)
  }

  public void setStatus(IProcessingStatus status) {
    m_status = status;
    if (m_status == null) {
      m_statusLabel.setVisible(false);
      m_statusLabel.setGraphic(null);
      Tooltip.uninstall(m_statusLabel, m_tooltip);
      m_mandatoryLabel.setVisible(m_mandatoryLabelVisible);
    }
    else {
      // icon
      String iconId = (m_status instanceof ScoutFieldStatus ? ((ScoutFieldStatus) m_status).getIconId() : null);
      if (iconId == null) {
        switch (m_status.getSeverity()) {
          case IProcessingStatus.FATAL:
          case IProcessingStatus.ERROR:
            iconId = AbstractIcons.StatusError;
            break;
          case IProcessingStatus.WARNING:
            iconId = AbstractIcons.StatusWarning;
            break;
          default:
            iconId = AbstractIcons.StatusInfo;
            break;
        }
      }
      m_statusLabel.setGraphic(Activator.getImageView(iconId));
      // tooltip
      StringBuffer buf = new StringBuffer();
      if (m_status.getTitle() != null) {
        buf.append(m_status.getTitle());
      }
      if (m_status.getMessage() != null) {
        if (buf.length() > 0) {
          buf.append("\n");
        }
        buf.append(m_status.getMessage());
      }
      m_tooltip.setText(buf.toString());
      Tooltip.install(m_statusLabel, m_tooltip);
      // visibility
      m_statusLabel.setVisible(true);

      if (isStatusHidesMandatoryIconEnabled()) {
        m_mandatoryLabel.setVisible(false);
      }
    }
  }

  public boolean isStatusHidesMandatoryIconEnabled() {
    return statusHidesMandatoryGraphicEnabled;
  }

  public void setStatusHidesMandatoryIconEnabled(boolean statusHidesMandatoryIconEnabled) {
    this.statusHidesMandatoryGraphicEnabled = statusHidesMandatoryIconEnabled;
  }

  public void setTooltip(Tooltip tooltip) {
    Tooltip.install(this, tooltip);
  }

}
