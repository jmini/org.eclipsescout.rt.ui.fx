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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;
import org.eclipsescout.rt.ui.fx.basic.chart.FxScoutChartRowData;
import org.eclipsescout.rt.ui.fx.basic.chart.PieChart3D;
import org.eclipsescout.rt.ui.fx.basic.chart.PieChart3D.Data;

/**
 *
 */
public class FxScout3DPieChartFactory extends AbstractFxScoutChartFactory {

  public final static String FactoryName = "3DPieChart";
  private static final String TOTALLABEL = "Total";

  private ObservableList<Data> m_chartData = FXCollections.observableArrayList();
  private PieChart3D chart;
  private P_DataChangedListener dataChangedListener = new P_DataChangedListener();
  private ChoiceBox<String> m_columnChooser;

  /**
   * @param data
   * @param columns
   * @param chartProperties
   * @param fxEnvironment
   */
  public FxScout3DPieChartFactory(ObservableList<FxScoutChartRowData> data, Collection<String> columns, ChartProperties chartProperties, IFxEnvironment fxEnvironment) {
    super(data, columns, chartProperties, fxEnvironment);
    chart = new PieChart3D(m_chartData);

    m_columnChooser = new ChoiceBox<String>(getColumnTitles());
    m_columnChooser.valueProperty().addListener(dataChangedListener);

    buildData();

    if (!getColumnTitles().contains(TOTALLABEL)) {
      getColumnTitles().add(TOTALLABEL);
    }

    buildChart();
    buildControlPanel();
  }

  @Override
  public String getName() {
    return FxScout3DPieChartFactory.FactoryName;
  }

  @Override
  protected void buildChart() {
    setChart(chart);
    for (Data d : chart.getData()) {
      d.getCircularSector().setOnMouseClicked(new P_OnSliceClickedListener(d.getName()));
    }
  }

  @Override
  protected void buildData() {
    if (m_chartData != null) {
      for (Data d : m_chartData) {
        d.getCircularSector().setOnMouseClicked(null);
      }
    }
    if (isInverse()) {
      int selectedIndex = getCategoryTitles().indexOf(m_columnChooser.getValue());
      if (selectedIndex != -1) {
        ObservableList<Data> list = FXCollections.observableArrayList();
        FxScoutChartRowData data = getData().get(selectedIndex);
        for (int i = 0; i < data.getRowValues().length; i++) {
          double val = 0;
          if (data.getRowValues()[i] != null) {
            val = data.getRowValues()[i].doubleValue();
          }
          if (val != 0) {
            list.add(new Data(getColumnTitles().get(i), val));
          }
        }
        chart.setData(list);
      }
      else if (m_columnChooser.getItems().size() > 0) {
        m_columnChooser.getSelectionModel().select(0);
      }
    }
    else {
      int selectedIndex = getColumnTitles().indexOf(m_columnChooser.getValue());
      if (selectedIndex != -1 && selectedIndex < getColumnTitles().size() - 1) {
        ObservableList<Data> list = FXCollections.observableArrayList();
        for (FxScoutChartRowData data : getData()) {
          double val = 0;
          if (data.getRowValues()[selectedIndex] != null) {
            val = data.getRowValues()[selectedIndex].doubleValue();
          }
          if (val != 0) {
            list.add(new Data(data.getRowName(), val));
          }
        }
        chart.setData(list);
      }
      else if (selectedIndex != -1 && selectedIndex == getColumnTitles().indexOf(TOTALLABEL)) {
        ObservableList<Data> list = FXCollections.observableArrayList();
        for (FxScoutChartRowData data : getData()) {
          if (data.getTotal().doubleValue() != 0) {
            list.add(new Data(data.getRowName(), data.getTotal().doubleValue()));
          }
        }
        chart.setData(list);
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
    addSeparatorElement(TEXTS.get("3DPieChartControls"));
    // column chooser
    addControlElement(new Label(TEXTS.get("Category")), m_columnChooser);
  }

  private class P_DataChangedListener implements ChangeListener<String> {

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      buildData();
      buildChart();
    }

  }

  private class P_OnSliceClickedListener implements EventHandler<MouseEvent> {

    private String toSelect;

    public P_OnSliceClickedListener(String toSelect) {
      this.toSelect = toSelect;
    }

    @Override
    public void handle(MouseEvent event) {
      if (event.isStillSincePress() && event.getEventType() == MouseEvent.MOUSE_CLICKED) {
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
