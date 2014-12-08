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
package org.eclipsescout.rt.ui.fx.basic.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ui.basic.cell.ICell;
import org.eclipse.scout.rt.client.ui.basic.table.ITable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.TableEvent;
import org.eclipse.scout.rt.client.ui.basic.table.TableListener;
import org.eclipse.scout.rt.client.ui.basic.table.columns.IColumn;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.rt.ui.fx.basic.FxScoutComposite;
import org.eclipsescout.rt.ui.fx.basic.chart.factory.ChartProperties;
import org.eclipsescout.rt.ui.fx.basic.chart.factory.FxScout3DPieChartFactory;
import org.eclipsescout.rt.ui.fx.basic.chart.factory.FxScoutAreaChartFactory;
import org.eclipsescout.rt.ui.fx.basic.chart.factory.FxScoutBarChartFactory;
import org.eclipsescout.rt.ui.fx.basic.chart.factory.FxScoutLineChartFactory;
import org.eclipsescout.rt.ui.fx.basic.chart.factory.FxScoutPieChartFactory;
import org.eclipsescout.rt.ui.fx.basic.chart.factory.IFxScoutChartFactory;
import org.eclipsescout.rt.ui.fx.basic.chart.functions.AvgFunction;
import org.eclipsescout.rt.ui.fx.basic.chart.functions.CountFunction;
import org.eclipsescout.rt.ui.fx.basic.chart.functions.IFunction;
import org.eclipsescout.rt.ui.fx.basic.chart.functions.MaxFunction;
import org.eclipsescout.rt.ui.fx.basic.chart.functions.MinFunction;
import org.eclipsescout.rt.ui.fx.basic.chart.functions.SumFunction;
import org.eclipsescout.rt.ui.fx.basic.chart.table.ChartTableView;
import org.eclipsescout.rt.ui.fx.layout.BorderPaneEx;

/**
 *
 */
public class FxScoutChart extends FxScoutComposite<ITable> implements IFxScoutChart {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(FxScoutChart.class);
  {
    LOG.setLevel(IScoutLogger.LEVEL_DEBUG);
  }

  private final static String FX_CHART_DIAGRAM = "FxChartDiagram";
  private final static String TABLE_NAME = "TableName";
  private final static String FX_CHART_FIELD_LIST = "FxChartFieldList";

  private ObservableList<FxScoutChartRowData> m_data;
  private Map<List<Object>, String> m_columnTitles;
  private Map<List<Object>, String> m_rowTitles;

  private ChartTableView m_chartTableView;
  private ChartDataChooser m_chartDataCooser;

  private ObservableList<IFunction> m_functions;
  private ObservableList<IColumn<?>> m_columns;

  private P_ScoutTableListener m_scoutTableListener;

  /**
   * Caches data for the final pivot-table calculation
   * Map<Row-Value, Map<Column-Value, Function-Value>>
   */
  private Map<List<Object>, Map<List<Object>, List<Object>>> m_cachedData;

  private BorderPane m_controlPane;
  private Node m_chartControlPane;
  private Tab chartTab;
  private Tab tableTab;
  private IFxScoutChartFactory m_chart;
  private ComboBox<String> m_chartBox;
  private P_ChartSwitchedHandler m_chartSwitcher;

  private SplitPane m_pane;

  private ChartProperties m_chartProperties;

  @Override
  protected void initialize() {
    m_data = FXCollections.observableArrayList();
    m_columnTitles = new HashMap<List<Object>, String>();
    m_rowTitles = new HashMap<List<Object>, String>();
    m_cachedData = new HashMap<List<Object>, Map<List<Object>, List<Object>>>();

    m_chartProperties = new ChartProperties();

    m_functions = buildFunctionList();
    m_columns = FXCollections.observableArrayList();
    m_chartDataCooser = new ChartDataChooser(m_columns, m_functions);
    m_chartTableView = new ChartTableView();
    m_chartDataCooser.rowSelectedItems().addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable observable) {
        rebuildCachedData();
      }
    });
    m_chartDataCooser.columnSelectedItems().addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable observable) {
        rebuildCachedData();
      }
    });
    m_chartDataCooser.functionColumnSelectedItem().addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable observable) {
        rebuildCachedData();
      }
    });
    m_chartDataCooser.functionSelectedItem().addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable observable) {
        rebuildData();
      }
    });

    chartTab = new Tab();
    chartTab.setText(TEXTS.get(FX_CHART_DIAGRAM));
    chartTab.setClosable(false);

    tableTab = new Tab();
    tableTab.setText(TEXTS.get(TABLE_NAME));
    tableTab.setContent(m_chartTableView);
    tableTab.setClosable(false);

    TabPane tabPane = new TabPane();
    tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
    tabPane.setSide(Side.TOP);
    tabPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    tabPane.getTabs().addAll(chartTab, tableTab);

    m_pane = new SplitPane();
    m_pane.setOrientation(Orientation.HORIZONTAL);
    m_pane.setDividerPositions(0.9, 0.1);

    BorderPaneEx borderPaneEx = new BorderPaneEx(10, 10);
    borderPaneEx.setCenter(tabPane);

    TitledPane titledPane = new TitledPane(TEXTS.get(FX_CHART_FIELD_LIST), m_chartDataCooser);
    borderPaneEx.setBottom(titledPane);

    setFxField(borderPaneEx);

    initializeChartControl();
  }

  @Override
  protected void attachScout() {
    super.attachScout();

    if (m_scoutTableListener == null) {
      m_scoutTableListener = new P_ScoutTableListener();
      getScoutObject().addUITableListener(m_scoutTableListener);
    }

    rebuildChart();
  }

  @Override
  protected void detachScout() {
    super.detachScout();
    if (getScoutObject() == null) {
      return;
    }

    if (m_scoutTableListener != null) {
      getScoutObject().removeTableListener(m_scoutTableListener);
      m_scoutTableListener = null;
    }
  }

  protected void rebuildChart() {
    m_columns.clear();
    for (IColumn<?> col : getScoutObject().getColumns()) {
      if (col.isDisplayable()) {
        m_columns.add(col);
      }
    }
  }

  protected ObservableList<IFunction> buildFunctionList() {
    ObservableList<IFunction> list = FXCollections.observableArrayList();
    list.add(new CountFunction());
    list.add(new SumFunction());
    list.add(new AvgFunction());
    list.add(new MinFunction());
    list.add(new MaxFunction());
    return list;
  }

  /**
   * Caches data to provide a database for the pivot-table.
   */
  protected void rebuildCachedData() {
    ObservableList<IColumn<?>> selectedRows = m_chartDataCooser.rowSelectedItems();
    ObservableList<IColumn<?>> selectedColumns = m_chartDataCooser.columnSelectedItems();

    // rebuild only if at least one row, one column and one function column is selected
    if (selectedRows.size() == 0 || selectedColumns.size() == 0 || m_chartDataCooser.getFunctionColumnSelectedItem() == null) {
      return;
    }

    m_cachedData.clear();
    m_columnTitles.clear();
    m_rowTitles.clear();

    // gather all chosen row-values
    for (ITableRow tableRow : getScoutObject().getRows()) {
      List<Object> chosedRowValues = new ArrayList<Object>(selectedRows.size());
      StringBuilder sb = new StringBuilder();
      for (IColumn<?> selectedRow : selectedRows) {
        ICell cell = tableRow.getCell(selectedRow);
        chosedRowValues.add(cell.getValue());
        sb.append(cell.getText());
        sb.append(", ");
      }
      sb.deleteCharAt(sb.length() - 2);

      String rowTitle = m_rowTitles.get(chosedRowValues);
      if (rowTitle == null) {
        m_rowTitles.put(chosedRowValues, sb.toString());
      }

      // gather all chosen column-values
      List<Object> chosedColumnValues = new ArrayList<Object>(selectedColumns.size());
      sb = new StringBuilder();
      for (IColumn<?> selectedColumn : selectedColumns) {
        ICell cell = tableRow.getCell(selectedColumn);
        chosedColumnValues.add(cell.getValue());
        sb.append(cell.getText());
        sb.append(", ");
      }
      sb.deleteCharAt(sb.length() - 2);

      String columnTitle = m_columnTitles.get(chosedColumnValues);
      if (columnTitle == null) {
        m_columnTitles.put(chosedColumnValues, sb.toString());
      }

      // get the function-value
      Object actualCellValue = tableRow.getCell(m_chartDataCooser.getFunctionColumnSelectedItem()).getValue();

      // add the informations to the right place in the cache
      Map<List<Object>, List<Object>> oldRows = m_cachedData.get(chosedRowValues);
      if (oldRows == null) { // add a new row-list
        Map<List<Object>, List<Object>> v = new HashMap<List<Object>, List<Object>>();
        List<Object> c = new ArrayList<Object>();
        c.add(actualCellValue);
        v.put(chosedColumnValues, c);
        m_cachedData.put(chosedRowValues, v);
      }
      else {
        List<Object> oldColumns = oldRows.get(chosedColumnValues);
        if (oldColumns == null) { // add a new column-list
          List<Object> c = new ArrayList<Object>();
          c.add(actualCellValue);
          oldRows.put(chosedColumnValues, c);
        }
        else { // add the value
          oldColumns.add(actualCellValue);
        }
      }
    }
    rebuildData();
  }

  /**
   * Builds a pivot-table from the cached data for a given function.
   */
  protected void rebuildData() {
    FxScoutChartRowData[] data = new FxScoutChartRowData[m_cachedData.size()];

    // one entry in the cache resembles the database for one row in the pivot-table
    int row = 0;
    for (Entry<List<Object>, Map<List<Object>, List<Object>>> entry : m_cachedData.entrySet()) {
      List<Object> keys = entry.getKey();
      Map<List<Object>, List<Object>> innerMap = entry.getValue();
      String rowName = m_rowTitles.get(keys);

      // one row in the pivot-table
      FxScoutChartRowData currentChartRowData = new FxScoutChartRowData(rowName, new Number[m_columnTitles.size()]);

      // one entry in the inner map of the cache resembles the values for each column in the pivot-table
      int column = 0;
      for (Entry<List<Object>, String> columnTitle : m_columnTitles.entrySet()) {
        List<Object> o = innerMap.get(columnTitle.getKey());
        Number result;
        if (o == null) { // inner map does not contain values for this column
          result = null;
        }
        else { // calculate the value with the help of the chosen function
          result = m_chartDataCooser.getFunctionSelectedItem().calculate(o, m_chartDataCooser.getFunctionColumnSelectedItem().getDataType());
        }
        currentChartRowData.getRowValues()[column++] = result;
      }

      // calculate total
      Object[] a = currentChartRowData.getRowValues();
      List<Object> b = Arrays.asList(a);
      Number total = new SumFunction().calculate(b);
      currentChartRowData.setTotal(total);

      data[row++] = currentChartRowData;
    }
    m_data.setAll(data);
    m_chartTableView.initialize(m_data, m_columnTitles.values());
    handleFxChartSwitchedEvent();
  }

  @Override
  public BorderPaneEx getFxChartContainer() {
    return (BorderPaneEx) getFxField();
  }

  private class P_ChartSwitchedHandler implements EventHandler<ActionEvent> {

    @Override
    public void handle(ActionEvent event) {
      handleFxChartSwitchedEvent();
    }
  }

  private ObservableList<String> createChartsList() {
    ObservableList<String> charts = FXCollections.observableArrayList(
        TEXTS.get(FxScoutBarChartFactory.FactoryName),
        TEXTS.get(FxScoutAreaChartFactory.FactoryName),
        TEXTS.get(FxScoutLineChartFactory.FactoryName),
        TEXTS.get(FxScoutPieChartFactory.FactoryName),
        TEXTS.get(FxScout3DPieChartFactory.FactoryName)
        );
    return charts;
  }

  private void initializeChartControl() {
    m_chartBox = new ComboBox<String>(createChartsList());
    m_chartBox.setMaxWidth(Double.MAX_VALUE);
    BorderPane.setMargin(m_chartBox, new Insets(5, 20, 5, 5));
    m_chartBox.addEventHandler(ActionEvent.ACTION, m_chartSwitcher = new P_ChartSwitchedHandler());
    m_chartControlPane = new ScrollPane();
    m_controlPane = new BorderPane();
    m_controlPane.setTop(m_chartBox);
    m_controlPane.setCenter(m_chartControlPane);
    m_chartBox.getSelectionModel().select(0);
  }

  private void handleFxChartSwitchedEvent() {
    String selectedItem = m_chartBox.getValue();
    if (m_chart != null && TEXTS.get(m_chart.getName()).equals(selectedItem)) {
      m_chart.rebuildFromData(m_data, m_columnTitles.values());
    }
    else if (TEXTS.get(FxScoutBarChartFactory.FactoryName).equals(selectedItem)) {
      m_chart = new FxScoutBarChartFactory(m_data, m_columnTitles.values(), m_chartProperties, getFxEnvironment());
    }
    else if (TEXTS.get(FxScoutAreaChartFactory.FactoryName).equals(selectedItem)) {
      m_chart = new FxScoutAreaChartFactory(m_data, m_columnTitles.values(), m_chartProperties, getFxEnvironment());
    }
    else if (TEXTS.get(FxScoutLineChartFactory.FactoryName).equals(selectedItem)) {
      m_chart = new FxScoutLineChartFactory(m_data, m_columnTitles.values(), m_chartProperties, getFxEnvironment());
    }
    else if (TEXTS.get(FxScoutPieChartFactory.FactoryName).equals(selectedItem)) {
      m_chart = new FxScoutPieChartFactory(m_data, m_columnTitles.values(), m_chartProperties, getFxEnvironment());
    }
    else if (TEXTS.get(FxScout3DPieChartFactory.FactoryName).equals(selectedItem)) {
      m_chart = new FxScout3DPieChartFactory(m_data, m_columnTitles.values(), m_chartProperties, getFxEnvironment());
    }
    refreshChart();
  }

  private void refreshChart() {
    double[] divs = m_pane.getDividerPositions();
    m_pane.getItems().clear();

    // print button
    Button printButton = new Button();
    printButton.getStyleClass().setAll("chart-button", "chart-print-button");
    printButton.setTooltip(new Tooltip(TEXTS.get("FxChartPrint")));
    printButton.setGraphic(getFxEnvironment().getImageView("printer"));
    printButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        getFxEnvironment().printNode(m_chart.getChart());
      }
    });

    // export image button
    Button exportImageButton = new Button();
    exportImageButton.getStyleClass().setAll("chart-button", "chart-export-image-button");
    exportImageButton.setTooltip(new Tooltip(TEXTS.get("FxChartSaveImage")));
    exportImageButton.setGraphic(getFxEnvironment().getImageView("image"));
    exportImageButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        getFxEnvironment().exportNodeAsImage(m_chart.getChart());
      }
    });

    AnchorPane anchorPane = new AnchorPane();
    // chart
    AnchorPane.setTopAnchor(m_chart.getChart(), 0.0);
    AnchorPane.setRightAnchor(m_chart.getChart(), 0.0);
    AnchorPane.setBottomAnchor(m_chart.getChart(), 0.0);
    AnchorPane.setLeftAnchor(m_chart.getChart(), 0.0);
    // print button
    AnchorPane.setTopAnchor(printButton, 10.0);
    AnchorPane.setRightAnchor(printButton, 10.0);
    // export image button
    AnchorPane.setTopAnchor(exportImageButton, 10.0);
    AnchorPane.setRightAnchor(exportImageButton, 30.0);

    anchorPane.getChildren().add(m_chart.getChart());
    anchorPane.getChildren().add(printButton);
    anchorPane.getChildren().add(exportImageButton);
    m_pane.getItems().add(anchorPane);

    m_chartControlPane = new ScrollPane(m_chart.getControlPanel());
    m_controlPane.setCenter(m_chartControlPane);

    m_pane.getItems().add(m_controlPane);
    m_pane.setDividerPositions(divs);
    chartTab.setContent(m_pane);
  }

  protected boolean isHandleScoutTableEvent(List<? extends TableEvent> batch) {
    for (int i = 0; i < batch.size(); i++) {
      switch (batch.get(i).getType()) {
//        case TableEvent.TYPE_REQUEST_FOCUS:
//        case TableEvent.TYPE_REQUEST_FOCUS_IN_CELL:
        case TableEvent.TYPE_ROWS_INSERTED:
        case TableEvent.TYPE_ROWS_UPDATED:
        case TableEvent.TYPE_ROWS_DELETED:
        case TableEvent.TYPE_ALL_ROWS_DELETED:
        case TableEvent.TYPE_ROW_ORDER_CHANGED:
        case TableEvent.TYPE_ROW_FILTER_CHANGED:
        case TableEvent.TYPE_COLUMN_ORDER_CHANGED:
        case TableEvent.TYPE_COLUMN_HEADERS_UPDATED:
        case TableEvent.TYPE_COLUMN_STRUCTURE_CHANGED:
        case TableEvent.TYPE_ROWS_SELECTED:
        case TableEvent.TYPE_SCROLL_TO_SELECTION: {
          return true;
        }
      }
    }
    return false;
  }

  protected void handleScoutTableEventInFx(TableEvent e) {
    switch (e.getType()) {
      case TableEvent.TYPE_ROWS_DELETED:
      case TableEvent.TYPE_ALL_ROWS_DELETED:
      case TableEvent.TYPE_ROWS_UPDATED:
      case TableEvent.TYPE_ROW_ORDER_CHANGED:
      case TableEvent.TYPE_ROWS_INSERTED:
      case TableEvent.TYPE_ROW_FILTER_CHANGED: {
        rebuildCachedData();
        break;
      }
      case TableEvent.TYPE_COLUMN_HEADERS_UPDATED:
      case TableEvent.TYPE_COLUMN_STRUCTURE_CHANGED: {
        rebuildChart();
        rebuildCachedData();
        break;
      }
    }
  }

  /**
   * Table listener for scout model
   */
  private class P_ScoutTableListener implements TableListener {

    @Override
    public void tableChanged(final TableEvent e) {
      // 22 Types
      if (isHandleScoutTableEvent(Collections.singletonList(e))) {
        if (isIgnoredScoutEvent(TableEvent.class, "" + e.getType())) {
          return;
        }
        //
        Runnable r = new Runnable() {
          @Override
          public void run() {
            handleScoutTableEventInFx(e);
          }
        };
        getFxEnvironment().invokeFxLater(r);
      }
    }

    @Override
    public void tableChangedBatch(List<? extends TableEvent> batch) {
      if (isHandleScoutTableEvent(batch)) {
        final List<TableEvent> filteredList = new ArrayList<TableEvent>();
        for (int i = 0; i < batch.size(); i++) {
          if (!isIgnoredScoutEvent(TableEvent.class, "" + batch.get(i).getType())) {
            filteredList.add(batch.get(i));
          }
        }
        if (filteredList.size() == 0) {
          return;
        }
        Runnable r = new Runnable() {
          @Override
          public void run() {
            for (TableEvent e : filteredList) {
              handleScoutTableEventInFx(e);
            }
          }
        };
        getFxEnvironment().invokeFxLater(r);
      }
    }
  }
}
