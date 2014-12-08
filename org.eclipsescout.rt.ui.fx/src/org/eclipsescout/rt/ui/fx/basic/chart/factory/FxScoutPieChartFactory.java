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

import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;
import org.eclipsescout.rt.ui.fx.basic.chart.FxScoutChartRowData;

/**
 *
 */
public class FxScoutPieChartFactory extends AbstractFxScoutChartFactory {

  public final static String FactoryName = "PieChart";

  private static final Duration ANIMATION_DURATION = new Duration(500);
  private static final double ANIMATION_DISTANCE = 0.15;
  private static final String TOTALLABEL = "Total";

  private P_DataChangedListener dataChangedListener = new P_DataChangedListener();

  private ObservableList<PieChart.Data> m_chartData;
  private ChoiceBox<String> m_columnChooser;

  private boolean chartInit = false;

  /**
   * @param data
   * @param columns
   */
  public FxScoutPieChartFactory(ObservableList<FxScoutChartRowData> data, Collection<String> columns, ChartProperties chartProperties, IFxEnvironment fxEnvironment) {
    super(data, columns, chartProperties, fxEnvironment);
    m_chartData = FXCollections.observableArrayList();

    m_columnChooser = new ChoiceBox<String>(getColumnTitles());
    m_columnChooser.valueProperty().addListener(dataChangedListener);

    buildData();

    if (!getColumnTitles().contains(TOTALLABEL)) {
      getColumnTitles().add(TOTALLABEL);
    }

    buildChart();
    buildControlPanel();
  }

  public ObservableList<PieChart.Data> getChartData() {
    return m_chartData;
  }

  public void setChartData(ObservableList<PieChart.Data> chartData) {
    m_chartData.setAll(chartData);
  }

  @Override
  protected void buildChart() {
    if (!chartInit) {
      setChart(new PieChart(getChartData()));
      chartInit = true;
    }
    if (getChartData().size() == 1) {
      PieChart.Data d = getChartData().get(0);
      d.getNode().setOnMouseClicked(new P_OnSliceClickedListener(d.getName()));
      Tooltip.install(d.getNode(), new Tooltip(Double.toString(d.getPieValue())));
    }
    else {
      for (PieChart.Data d : getChartData()) {
        P_PieSliceAnimationListener psal = new P_PieSliceAnimationListener(d);
        d.getNode().setOnMouseClicked(new P_OnSliceClickedListener(d.getName()));
        d.getNode().setOnMouseEntered(psal);
        d.getNode().setOnMouseExited(psal);
        Tooltip.install(d.getNode(), new Tooltip(Double.toString(d.getPieValue())));
      }
    }
  }

  @Override
  protected void buildData() {
    if (getChartData() != null) {
      for (PieChart.Data d : getChartData()) {
        d.getNode().setOnMouseClicked(null);
        d.getNode().setOnMouseEntered(null);
        d.getNode().setOnMouseExited(null);
        Tooltip.uninstall(d.getNode(), null);
      }
    }
    if (isInverse()) {
      int selectedIndex = getCategoryTitles().indexOf(m_columnChooser.getValue());
      if (selectedIndex != -1) {
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        FxScoutChartRowData data = getData().get(selectedIndex);
        for (int i = 0; i < data.getRowValues().length; i++) {
          double val = 0;
          if (data.getRowValues()[i] != null) {
            val = data.getRowValues()[i].doubleValue();
          }
          if (val != 0) {
            list.add(new PieChart.Data(getColumnTitles().get(i), val));
          }
        }
        setChartData(list);
      }
      else if (m_columnChooser.getItems().size() > 0) {
        m_columnChooser.getSelectionModel().select(0);
      }
    }
    else {
      int selectedIndex = getColumnTitles().indexOf(m_columnChooser.getValue());
      if (selectedIndex != -1 && selectedIndex < getColumnTitles().size() - 1) {
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        for (FxScoutChartRowData data : getData()) {
          double val = 0;
          if (data.getRowValues()[selectedIndex] != null) {
            val = data.getRowValues()[selectedIndex].doubleValue();
          }
          if (val != 0) {
            list.add(new PieChart.Data(data.getRowName(), val));
          }
        }
        setChartData(list);
      }
      else if (selectedIndex != -1 && selectedIndex == getColumnTitles().indexOf(TOTALLABEL)) {
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        for (FxScoutChartRowData data : getData()) {
          if (data.getTotal().doubleValue() != 0) {
            list.add(new PieChart.Data(data.getRowName(), data.getTotal().doubleValue()));
          }
        }
        setChartData(list);
      }
      else if (m_columnChooser.getItems().size() > 0) {
        m_columnChooser.getSelectionModel().select(0);
      }
    }
  }

  @Override
  public void rebuildFromData(ObservableList<FxScoutChartRowData> data, Collection<String> columnTitles) {
    super.rebuildFromData(data, columnTitles);
    if (!getColumnTitles().contains(TOTALLABEL)) {
      getColumnTitles().add(TOTALLABEL);
    }
    buildChart();
  }

  @Override
  protected void buildControlPanel() {
    super.buildControlPanel();
    addSeparatorElement(TEXTS.get("PieChartControls"));

    PieChart chart = (PieChart) this.getChart();
    // column chooser
    addControlElement(new Label(TEXTS.get("Category")), m_columnChooser);
    // clockwise
    CheckBox clockwise = new CheckBox();
    // labelsVisible
    CheckBox labelsVisible = new CheckBox();
    // labelLineLength
    Slider labelLineLength = new Slider(1, 101, chart.getLabelLineLength());
    // startAngle
    Slider startAngle = new Slider(0, 360, chart.getStartAngle());

    if (getChartProperties() != null) {
      getChartProperties().bindToSettings(getChartProperties().clockwiseProperty, chart.clockwiseProperty(), clockwise.selectedProperty());
      getChartProperties().bindToSettings(getChartProperties().labelsVisibleProperty, chart.labelsVisibleProperty(), labelsVisible.selectedProperty());
      getChartProperties().bindToSettings(getChartProperties().labelLineLengthProperty, chart.labelLineLengthProperty(), labelLineLength.valueProperty());
      getChartProperties().bindToSettings(getChartProperties().startAngleProperty, chart.startAngleProperty(), startAngle.valueProperty());
    }
    else {
      clockwise.selectedProperty().set(chart.clockwiseProperty().get());
      labelsVisible.selectedProperty().set(chart.labelsVisibleProperty().get());
      labelLineLength.valueProperty().set(chart.labelLineLengthProperty().get());
      startAngle.valueProperty().set(chart.startAngleProperty().get());

      chart.clockwiseProperty().bind(clockwise.selectedProperty());
      chart.labelsVisibleProperty().bind(labelsVisible.selectedProperty());
      chart.labelLineLengthProperty().bind(labelLineLength.valueProperty());
      chart.startAngleProperty().bind(startAngle.valueProperty());
    }

    addControlElement(new Label(TEXTS.get("Clockwise")), clockwise);
    addControlElement(new Label(TEXTS.get("LabelsVisible")), labelsVisible);
    labelLineLength.setShowTickLabels(true);
    addControlElement(new Label(TEXTS.get("LabelLineLength")), labelLineLength);
    startAngle.setShowTickLabels(true);
    addControlElement(new Label(TEXTS.get("StartAngle")), startAngle);

  }

  @Override
  public String getName() {
    return FxScoutPieChartFactory.FactoryName;
  }

  private class P_DataChangedListener implements ChangeListener<String> {

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      buildData();
      buildChart();
    }

  }

  /**
   * Private class that listens to entering and exiting of a Mouse on a PieChart-Node.
   * This class calculates the translation needed to highlight one node and manipulates this node according to the
   * calculations.
   */
  private class P_PieSliceAnimationListener implements EventHandler<MouseEvent> {

    private PieChart chart;
    private PieChart.Data data;

    final Duration ANIMATION_DURATION = new Duration(750);
    final double ANIMATION_DISTANCE_FACTOR = 0.2;

    double a = 0.0;
    private double total = 0.0;
    private double sin = 0.0;
    private double cos = 0.0;

    private double transX = 0.0;
    private double transY = 0.0;

    private double startX = 0.0;
    private double startY = 0.0;

    private double clockwise = 1.0;

    private TranslateTransition anim;

    public P_PieSliceAnimationListener(PieChart.Data data) {
      super();
      this.chart = (PieChart) getChart();
      this.data = data;

      // store current x and y position of the node, this will be needed as anchor for the reverse translation
      this.startX = data.getNode().getBoundsInParent().getMinX();
      this.startY = data.getNode().getBoundsInParent().getMinY();

      // calculate the sum of the chart
      this.total = calculateTotalValue(chart);

      // calculate the angle of the node this listener listens to
      this.a = 360.0 * (data.getPieValue() / total);

      this.anim = new TranslateTransition(ANIMATION_DURATION, data.getNode());
      this.anim.setAutoReverse(false);
    }

    /**
     * Calculates the attributes for the translation.
     * Should recalculated if the attributes transX and transY are needed, as depending attributes could have been
     * changed.
     */
    private void calculateTransition() {
      // adjust leading sign for a non-clockwise pie chart
      if (!chart.isClockwise()) {
        clockwise = -1.0;
      }
      else {
        clockwise = 1.0;
      }

      // calculate the angle from the start of the list for this node
      double m = 0.0;

      int chartIndex = 0;
      while (this.data != chart.getData().get(chartIndex)) {
        m += 360.0 * (chart.getData().get(chartIndex++).getPieValue() / total);
      }
      m += a / 2.0;
      m += chart.getStartAngle() * -clockwise;
      m *= clockwise;

      cos = Math.cos(Math.toRadians(m));
      sin = Math.sin(Math.toRadians(m));

      // Radius, calculated by the bounding boxes of the PieChart nodes.
      double minX = data.getNode().getBoundsInParent().getMinX();
      double maxX = data.getNode().getBoundsInParent().getMaxX();
      for (PieChart.Data d : chart.getData()) {
        minX = Math.min(minX, d.getNode().getBoundsInParent().getMinX());
        maxX = Math.max(maxX, d.getNode().getBoundsInParent().getMaxX());
      }
      double r = (maxX - minX) / 2.0;

      // calculation according to parametric form of this circle equation:
      // en.wikipedia.org/wiki/Circle#Equations
      transX = 0.0 + r * ANIMATION_DISTANCE_FACTOR * cos;
      transY = 0.0 + r * ANIMATION_DISTANCE_FACTOR * sin;
    }

    /**
     * Calculates the total value of a given PieChart
     *
     * @param c
     *          A PieChart
     * @return total value
     */
    private double calculateTotalValue(PieChart c) {
      double res = 0.0;
      for (PieChart.Data d : c.getData()) {
        res += d.getPieValue();
      }
      return res;
    }

    @Override
    public void handle(MouseEvent event) {
      this.anim.stop();
      if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
        calculateTransition();
        this.anim.setToX(transX);
        this.anim.setToY(transY);
      }
      else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
        this.anim.setToX(startX);
        this.anim.setToY(startY);
      }
      this.anim.play();
    }
  }

  private class P_OnSliceClickedListener implements EventHandler<MouseEvent> {

    private String toSelect;

    public P_OnSliceClickedListener(String toSelect) {
      this.toSelect = toSelect;
    }

    @Override
    public void handle(MouseEvent event) {
      if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
        inverseChart();
        if (isInverse()) {
          m_columnChooser.setItems(getCategoryTitles());
        }
        else {
          m_columnChooser.setItems(getColumnTitles());
        }
        m_columnChooser.getSelectionModel().select(toSelect);
      }
    }
  }

}
