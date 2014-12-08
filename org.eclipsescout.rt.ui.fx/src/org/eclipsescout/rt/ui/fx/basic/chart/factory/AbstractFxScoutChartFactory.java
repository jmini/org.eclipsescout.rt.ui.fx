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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.rt.ui.fx.FxStyleUtility;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;
import org.eclipsescout.rt.ui.fx.basic.chart.FxScoutChartRowData;

/**
 *
 */
public abstract class AbstractFxScoutChartFactory implements IFxScoutChartFactory {

  private Chart m_chart;
  private GridPane m_controlPanel;
  private int m_controlRowCount;
  private ObservableList<FxScoutChartRowData> m_data;
  private ObservableList<String> m_columnTitles;
  private ObservableList<String> m_categoryTitles;
  private IFxEnvironment m_fxEnvironment;
  private ChartProperties m_chartProperties;
  private boolean m_isInverse = false;

  public AbstractFxScoutChartFactory(ObservableList<FxScoutChartRowData> data, Collection<String> columns, ChartProperties chartProperties, IFxEnvironment fxEnvironment) {
    m_controlPanel = new GridPane();
    m_data = FXCollections.observableArrayList();
    m_data.addAll(data);
    m_columnTitles = FXCollections.observableArrayList(columns);
    m_categoryTitles = FXCollections.observableArrayList();
    for (FxScoutChartRowData row : data) {
      m_categoryTitles.add(row.getRowName());
    }
    m_chartProperties = chartProperties;
    m_fxEnvironment = fxEnvironment;
  }

  @Override
  public Chart getChart() {
    return m_chart;
  }

  public void setChart(Chart chart) {
    m_chart = chart;
  }

  @Override
  public Pane getControlPanel() {
    return m_controlPanel;
  }

  public ObservableList<FxScoutChartRowData> getData() {
    return m_data;
  }

  public ObservableList<String> getColumnTitles() {
    return m_columnTitles;
  }

  public ObservableList<String> getCategoryTitles() {
    return m_categoryTitles;
  }

  /**
   * Should be implemented by overwritten charts to build the appropriate chart.
   */
  protected abstract void buildChart();

  protected abstract void buildData();

  @Override
  public void rebuildFromData(ObservableList<FxScoutChartRowData> data, Collection<String> columnTitles) {
    m_columnTitles.setAll(columnTitles);
    m_data.setAll(data);
    m_categoryTitles.clear();
    for (FxScoutChartRowData row : data) {
      m_categoryTitles.add(row.getRowName());
    }
    buildData();
  }

  /**
   * Should be overwritten if additional functionality will be provided. A super call as the first operation is
   * mandatory.
   */
  protected void buildControlPanel() {
    m_controlPanel = new GridPane();
    m_controlPanel.setHgap(10);
    m_controlPanel.setVgap(5);
    FxStyleUtility.setPadding(m_controlPanel, 5);
    ColumnConstraints c1 = new ColumnConstraints();
    c1.setHalignment(HPos.RIGHT);
    ColumnConstraints c2 = new ColumnConstraints();
    c2.setHalignment(HPos.LEFT);
    m_controlPanel.getColumnConstraints().addAll(c1, c2);
    m_controlRowCount = 0;

    addSeparatorElement(TEXTS.get("GeneralControls"));

    // legend side
    ObservableList<Side> side = FXCollections.observableArrayList(Side.values());
    ChoiceBox<Side> legendSide = new ChoiceBox<Side>(side);
    // legend visible
    CheckBox legendVisible = new CheckBox();
    // title
    TextField title = new TextField(getChart().getTitle());
    title.setPromptText("title");
    // title side
    ChoiceBox<Side> titleSide = new ChoiceBox<Side>(side);

    if (getChartProperties() != null) {
      getChartProperties().bindToSettings(getChartProperties().legendSideProperty, getChart().legendSideProperty(), legendSide.valueProperty());
      getChartProperties().bindToSettings(getChartProperties().legendVisibleProperty, getChart().legendVisibleProperty(), legendVisible.selectedProperty());
      getChartProperties().bindToSettings(getChartProperties().titleSideProperty, getChart().titleSideProperty(), titleSide.valueProperty());
      getChartProperties().bindToSettings(getChartProperties().titleProperty, getChart().titleProperty(), title.textProperty());
    }
    else {
      legendSide.setValue(getChart().getLegendSide());
      getChart().legendSideProperty().bind(legendSide.valueProperty());
      legendVisible.setSelected(getChart().isLegendVisible());
      getChart().legendVisibleProperty().bind(legendVisible.selectedProperty());
      titleSide.setValue(getChart().getTitleSide());
      getChart().titleSideProperty().bind(titleSide.valueProperty());
      title.setText(getChart().getTitle());
      getChart().titleProperty().bind(title.textProperty());
    }

    addControlElement(new Label(TEXTS.get("LegendSide")), legendSide);
    addControlElement(new Label(TEXTS.get("LegendVisible")), legendVisible);
    addControlElement(new Label(TEXTS.get("Title")), title);
    addControlElement(new Label(TEXTS.get("TitleSide")), titleSide);
  }

  protected void addControlElement(Node... n) {
    m_controlPanel.addRow(m_controlRowCount++, n);
  }

  protected void addSeparatorElement(String name) {
    Label label = new Label(" " + name + " ");
    Separator left = new Separator(Orientation.HORIZONTAL);
    HBox.setHgrow(left, Priority.ALWAYS);
    Separator right = new Separator(Orientation.HORIZONTAL);
    HBox.setHgrow(right, Priority.ALWAYS);
    HBox separator = new HBox(left, label, right);
    separator.setAlignment(Pos.CENTER);
    m_controlPanel.add(separator, 0, m_controlRowCount++, 3, 1);
  }

  @Override
  public ChartProperties getChartProperties() {
    return m_chartProperties;
  }

  public void inverseChart() {
    m_isInverse = !m_isInverse;
  }

  public boolean isInverse() {
    return m_isInverse;
  }

}
