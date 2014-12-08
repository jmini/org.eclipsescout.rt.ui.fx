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
package org.eclipsescout.rt.ui.fx.form.fields.tablefield;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import org.eclipse.scout.rt.client.ui.basic.table.ITable;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.rt.ui.fx.LogicalGridPane;
import org.eclipsescout.rt.ui.fx.basic.chart.FxScoutChart;
import org.eclipsescout.rt.ui.fx.basic.chart.IFxScoutChart;
import org.eclipsescout.rt.ui.fx.layout.BorderPaneEx;

/**
 *
 */
public class FxScoutTableFieldWithChart extends FxScoutTableField {

  private final double ANIMATION_DURATION = 500;
  private boolean m_isTableShown = true;
  private IFxScoutChart m_chartComposite;
  private P_KeyListener m_keyListener;
  private DoubleProperty m_opacityValue = new SimpleDoubleProperty(1);
  private Timeline m_flash;

  private AnchorPane m_anchorPane;
  private Button m_swapButton;

  @Override
  protected void initialize() {
    super.initialize();

    m_anchorPane = new AnchorPane();
    m_swapButton = new Button();
    m_swapButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        swap();
      }
    });
    adjustSwapButton();
    AnchorPane.setTopAnchor(m_swapButton, 5.0);
    AnchorPane.setRightAnchor(m_swapButton, 5.0);

    m_anchorPane.getChildren().add(m_swapButton);
    ((LogicalGridPane) getFxContainer()).getChildren().add(m_anchorPane);

    m_keyListener = new P_KeyListener();
    getFxContainer().addEventHandler(KeyEvent.KEY_RELEASED, m_keyListener);
  }

  @Override
  protected void setTableFromScout() {
    super.setTableFromScout();
    if (getFxTable() != null) {
      getFxTable().opacityProperty().bind(m_opacityValue);
    }
    setChartFromScout();
    if (m_isTableShown) {
      getFxTable().toFront();
    }
    else {
      getFxChart().toFront();
    }
    m_swapButton.toFront();
  }

  public BorderPaneEx getFxChart() {
    return m_chartComposite != null ? m_chartComposite.getFxChartContainer() : null;
  }

  protected void setChartFromScout() {
    ITable oldTable = m_chartComposite != null ? m_chartComposite.getScoutObject() : null;
    ITable newTable = getScoutObject().getTable();
    if (oldTable != newTable) {
      removeChart();
      if (newTable != null) {
        IFxScoutChart newChartComposite = new FxScoutChart();
        newChartComposite.createField(getScoutObject().getTable(), getFxEnvironment());
        m_chartComposite = newChartComposite;
        decorateContentPaneChild(m_chartComposite.getFxChartContainer());
        getContentPane().getChildren().add(m_chartComposite.getFxChartContainer());
        setFxField(getFxFieldForSetter());

        m_chartComposite.getFxChartContainer().opacityProperty().bind(m_opacityValue.add(-1.0).negate());
      }
    }
  }

  protected void removeChart() {
    if (m_chartComposite != null) {
      m_chartComposite.getFxChartContainer().opacityProperty().unbind();
      getContentPane().getChildren().remove(m_chartComposite.getFxChartContainer());
      setFxField(null);
      m_chartComposite.disconnectFromScout();
      m_chartComposite = null;
    }
  }

  @Override
  protected void decorateContentPaneChild(Node node) {
    AnchorPane.setTopAnchor(node, 0.0);
    AnchorPane.setRightAnchor(node, 0.0);
    AnchorPane.setBottomAnchor(node, 0.0);
    AnchorPane.setLeftAnchor(node, 0.0);
  }

  @Override
  protected Node getFxFieldForSetter() {
    return m_anchorPane;
  }

  @Override
  protected Pane getContentPane() {
    return m_anchorPane;
  }

  @Override
  protected void removeOldTable() {
    if (getFxTable() != null) {
      getFxTable().opacityProperty().unbind();
    }
    super.removeOldTable();
  }

  protected void swap() {
    m_isTableShown = !m_isTableShown;
    if (m_isTableShown) {
      getFxTable().toFront();
      m_flash = new Timeline(
          new KeyFrame(Duration.millis(0), new KeyValue(m_opacityValue, 0, Interpolator.LINEAR)),
          new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(m_opacityValue, 1, Interpolator.LINEAR))
          );
    }
    else {
      getFxChart().toFront();
      m_flash = new Timeline(
          new KeyFrame(Duration.millis(0), new KeyValue(m_opacityValue, 1, Interpolator.LINEAR)),
          new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(m_opacityValue, 0, Interpolator.LINEAR))
          );
    }
    adjustSwapButton();
    m_swapButton.toFront();
    m_flash.play();
    m_flash = null;
  }

  protected void adjustSwapButton() {
    String styleClasse;
    String tooltipText;
    String imageName;
    if (m_isTableShown) {
      styleClasse = "a";
      tooltipText = "FxChartSwapToChart";
      imageName = "chart_bar";
    }
    else {
      styleClasse = "b";
      tooltipText = "FxChartSwapToTable";
      imageName = "table";
    }
    m_swapButton.getStyleClass().setAll("chart-button", "chart-swap-button", styleClasse);
    m_swapButton.setTooltip(new Tooltip(TEXTS.get(tooltipText)));
    m_swapButton.setGraphic(getFxEnvironment().getImageView(imageName));
  }

  private class P_KeyListener implements EventHandler<KeyEvent> {

    @Override
    public void handle(KeyEvent event) {
      if ((event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) && event.isAltDown()) {
        swap();
      }
    }
  }

}
