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
package org.eclipsescout.rt.ui.fx.basic.chart.factory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;
import org.eclipsescout.rt.ui.fx.basic.chart.FxScoutChartRowData;
import org.eclipsescout.rt.ui.fx.basic.chart.factory.ChartProperties.AxisName;

/**
 *
 */
public abstract class AbstractFxScoutXYChartFactory extends AbstractFxScoutChartFactory {

  private ObservableList<XYChart.Series<String, Number>> m_chartData;
  private CategoryAxis xAxis;
  private NumberAxis yAxis;

  public AbstractFxScoutXYChartFactory(ObservableList<FxScoutChartRowData> data, Collection<String> columns, ChartProperties chartProperties, IFxEnvironment fxEnvironment) {
    super(data, columns, chartProperties, fxEnvironment);
    xAxis = new CategoryAxis(getCategoryTitles());
    xAxis.setAutoRanging(true);
    yAxis = new NumberAxis();
    yAxis.setAutoRanging(true);
    m_chartData = FXCollections.observableArrayList();
    buildData();
    buildChart();
    buildControlPanel();
  }

  public ObservableList<XYChart.Series<String, Number>> getChartData() {
    return m_chartData;
  }

  public void setChartData(List<XYChart.Series<String, Number>> chartData) {
    m_chartData.setAll(chartData);
  }

  public CategoryAxis getxAxis() {
    return xAxis;
  }

  public NumberAxis getyAxis() {
    return yAxis;
  }

  @Override
  public void rebuildFromData(ObservableList<FxScoutChartRowData> data, Collection<String> columnTitles) {
    getChart().setAnimated(false);
    super.rebuildFromData(data, columnTitles);
    getChart().setAnimated(true);
  }

  @Override
  protected void buildData() {
    List<XYChart.Series<String, Number>> chartData = new LinkedList<XYChart.Series<String, Number>>();

    for (int i = 0; i < getColumnTitles().size(); i++) {
      ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
      for (FxScoutChartRowData row : getData()) {
        data.add(new XYChart.Data<String, Number>(row.getRowName(), row.getRowValues()[i] == null ? 0 : row.getRowValues()[i]));
      }
      XYChart.Series<String, Number> serie = new XYChart.Series<String, Number>(getColumnTitles().get(i), data);
      chartData.add(serie);
    }

    this.setChartData(chartData);
  }

  @Override
  protected void buildControlPanel() {
    super.buildControlPanel();

    XYChart chart = (XYChart) getChart();
    addSeparatorElement(TEXTS.get("XYChartControls"));
    // horizontal grid lines visible
    CheckBox hGridVisible = new CheckBox();
    // horizontal zero lines visible
    CheckBox hZLineVisible = new CheckBox();
    // vertical grid lines visible
    CheckBox vGridVisible = new CheckBox();
    // vertical zero lines visible
    CheckBox vZLineVisible = new CheckBox();

    if (getChartProperties() != null) {
      getChartProperties().bindToSettings(getChartProperties().horizontalGridLinesVisibleProperty, chart.horizontalGridLinesVisibleProperty(), hGridVisible.selectedProperty());
      getChartProperties().bindToSettings(getChartProperties().horizontalZeroLineVisibleProperty, chart.horizontalZeroLineVisibleProperty(), hZLineVisible.selectedProperty());
      getChartProperties().bindToSettings(getChartProperties().verticalGridLinesVisibleProperty, chart.verticalGridLinesVisibleProperty(), vGridVisible.selectedProperty());
      getChartProperties().bindToSettings(getChartProperties().verticalZeroLineVisibleProperty, chart.verticalZeroLineVisibleProperty(), vZLineVisible.selectedProperty());
    }
    else {
      hGridVisible.setSelected(chart.isHorizontalGridLinesVisible());
      chart.horizontalGridLinesVisibleProperty().bind(hGridVisible.selectedProperty());
      hZLineVisible.setSelected(chart.isHorizontalZeroLineVisible());
      chart.horizontalZeroLineVisibleProperty().bind(hZLineVisible.selectedProperty());
      vGridVisible.setSelected(chart.getVerticalGridLinesVisible());
      chart.verticalGridLinesVisibleProperty().bind(vGridVisible.selectedProperty());
      vZLineVisible.setSelected(chart.isVerticalZeroLineVisible());
      chart.verticalZeroLineVisibleProperty().bind(vZLineVisible.selectedProperty());
    }

    addControlElement(new Label(TEXTS.get("HorizontalGridLinesVisible")), hGridVisible);
    addControlElement(new Label(TEXTS.get("HorizontalZeroLineVisible")), hZLineVisible);
    addControlElement(new Label(TEXTS.get("VerticalGridLinesVisible")), vGridVisible);
    addControlElement(new Label(TEXTS.get("VerticalZeroLineVisible")), vZLineVisible);

    // axis properties
    for (int i = 0; i < 2; i++) {
      String axisLabel;
      AxisName axisName;
      Axis<?> axis;
      if (i == 0) {
        axis = chart.getXAxis();
        axisName = AxisName.X;
        axisLabel = "XAxis";
      }
      else {
        axis = chart.getYAxis();
        axisName = AxisName.Y;
        axisLabel = "YAxis";
      }
      addSeparatorElement(TEXTS.get(axisLabel));
      // autoRanging
      CheckBox autoranging = new CheckBox();
      // axisSide
      ComboBox<Side> axisSide = new ComboBox<Side>(FXCollections.observableArrayList(Side.values()));
      // tickLabelFill
      ColorPicker tickLabelFill = new ColorPicker();
      // tickLabelGap
      Slider tickLabelGap = new Slider(0, 100, axis.getTickLabelGap());
      // tickLabelsVisible
      CheckBox tickLabelsVisible = new CheckBox();
      // tickLength
      Slider tickLength = new Slider(0, 100, axis.getTickLength());
      // tickMarkVisible
      CheckBox tickMarkVisible = new CheckBox();
      // tickLabelRotation
      Slider tickLabelRotation = new Slider(0, 360, axis.getTickLabelRotation());

      if (getChartProperties() != null) {
        getChartProperties().bindToSettings(getChartProperties().getAxisProperties(axisName).autoRangingProperty, axis.autoRangingProperty(), autoranging.selectedProperty());
        getChartProperties().bindToSettings(getChartProperties().getAxisProperties(axisName).sideProperty, axis.sideProperty(), axisSide.valueProperty());
        getChartProperties().bindToSettings(getChartProperties().getAxisProperties(axisName).tickLabelFillProperty, axis.tickLabelFillProperty(), tickLabelFill.valueProperty());
        getChartProperties().bindToSettings(getChartProperties().getAxisProperties(axisName).tickLabelGapProperty, axis.tickLabelGapProperty(), tickLabelGap.valueProperty());
        getChartProperties().bindToSettings(getChartProperties().getAxisProperties(axisName).tickLabelsVisibleProperty, axis.tickLabelsVisibleProperty(), tickLabelsVisible.selectedProperty());
        getChartProperties().bindToSettings(getChartProperties().getAxisProperties(axisName).tickLengthProperty, axis.tickLengthProperty(), tickLength.valueProperty());
        getChartProperties().bindToSettings(getChartProperties().getAxisProperties(axisName).tickMarkVisibleProperty, axis.tickMarkVisibleProperty(), tickMarkVisible.selectedProperty());
      }
      else {
        autoranging.selectedProperty().set(axis.autoRangingProperty().get());
        axisSide.valueProperty().set(axis.sideProperty().get());
        tickLabelFill.valueProperty().set((Color) axis.tickLabelFillProperty().get());
        tickLabelGap.valueProperty().set(axis.tickLabelGapProperty().get());
        tickLabelsVisible.selectedProperty().set(axis.tickLabelsVisibleProperty().get());
        tickLength.valueProperty().set(axis.tickLengthProperty().get());
        tickMarkVisible.selectedProperty().set(axis.tickMarkVisibleProperty().get());

        axis.autoRangingProperty().bind(autoranging.selectedProperty());
        axis.sideProperty().bind(axisSide.valueProperty());
        axis.tickLabelFillProperty().bind(tickLabelFill.valueProperty());
        axis.tickLabelGapProperty().bind(tickLabelGap.valueProperty());
        axis.tickLabelsVisibleProperty().bind(tickLabelsVisible.selectedProperty());
        axis.tickLengthProperty().bind(tickLength.valueProperty());
        axis.tickMarkVisibleProperty().bind(tickMarkVisible.selectedProperty());

      }

      addControlElement(new Label(TEXTS.get("AutoRanging")), autoranging);
      addControlElement(new Label(TEXTS.get("Side")), axisSide);
      addControlElement(new Label(TEXTS.get("TickLabelColor")), tickLabelFill);
      addControlElement(new Label(TEXTS.get("TickLabelGap")), tickLabelGap);
      addControlElement(new Label(TEXTS.get("TickLabelsVisible")), tickLabelsVisible);
      tickLength.setShowTickLabels(true);
      addControlElement(new Label(TEXTS.get("TickLength")), tickLength);
      addControlElement(new Label(TEXTS.get("TickMarkVisible")), tickMarkVisible);
      tickLabelRotation.setShowTickLabels(true);
      axis.tickLabelRotationProperty().bindBidirectional(tickLabelRotation.valueProperty());
      addControlElement(new Label(TEXTS.get("TickLabelRotation")), tickLabelRotation);
    }

  }
}
