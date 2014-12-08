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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ui.basic.table.columns.IColumn;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.rt.ui.fx.FxStyleUtility;
import org.eclipsescout.rt.ui.fx.basic.chart.functions.IFunction;
import org.eclipsescout.rt.ui.fx.basic.table.FxScoutTable;

/**
 *
 */
public class ChartDataChooser extends GridPane {

  private static final IScoutLogger LOG = ScoutLogManager.getLogger(FxScoutTable.class);

  private static final String FX_CHART_ROW_LABELS = "FxChartRowLabels";
  private static final String FX_CHART_COLUMN_LABELS = "FxChartColumnLabels";
  private static final String FX_CHART_VALUES = "FxChartValues";
  private static final String FX_CHART_FUNCTION = "FxChartFunction";

  private final double HEIGHT = 100;

  private ListView<IColumn<?>> m_rows;
  private ListView<IColumn<?>> m_columns;
  private ListView<IColumn<?>> m_functionColumn;
  private ComboBox<IFunction> m_functions;

  /**
   *
   */
  public ChartDataChooser(ObservableList<IColumn<?>> columns, ObservableList<IFunction> functions) {
    super();
    setHgap(20);
    setVgap(5);
    FxStyleUtility.setPadding(this, 5);

    ScoutColumnListCellFactory scoutColumnListCellFactory = new ScoutColumnListCellFactory();

    m_rows = new ListView<IColumn<?>>();
    m_rows.setCellFactory(scoutColumnListCellFactory);
    m_rows.setItems(columns);
    m_rows.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    m_columns = new ListView<IColumn<?>>();
    m_columns.setCellFactory(scoutColumnListCellFactory);
    m_columns.setItems(columns);
    m_columns.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    m_functionColumn = new ListView<IColumn<?>>();
    m_functionColumn.setCellFactory(scoutColumnListCellFactory);
    m_functionColumn.setItems(columns);

    FunctionListCellFactory functionListCellFactory = new FunctionListCellFactory();

    m_functions = new ComboBox<IFunction>();
    m_functions.setCellFactory(functionListCellFactory);
    m_functions.setItems(functions);
    m_functions.setButtonCell(functionListCellFactory.call(null));
    m_functions.getSelectionModel().select(0);

    m_rows.setPrefHeight(HEIGHT);
    m_columns.setPrefHeight(HEIGHT);
    m_functionColumn.setPrefHeight(HEIGHT);

    add(new Text(TEXTS.get(FX_CHART_ROW_LABELS)), 0, 0);
    add(new Text(TEXTS.get(FX_CHART_COLUMN_LABELS)), 1, 0);
    add(new Text(TEXTS.get(FX_CHART_VALUES)), 2, 0);
    add(m_rows, 0, 1);
    add(m_columns, 1, 1);
    add(m_functionColumn, 2, 1);
    HBox v = new HBox(20, new Text(TEXTS.get(FX_CHART_FUNCTION)), m_functions);
    add(v, 2, 2);

  }

  public ObservableList<IColumn<?>> rowSelectedItems() {
    return m_rows.getSelectionModel().getSelectedItems();
  }

  public ObservableList<IColumn<?>> columnSelectedItems() {
    return m_columns.getSelectionModel().getSelectedItems();
  }

  public ReadOnlyObjectProperty<IColumn<?>> functionColumnSelectedItem() {
    return m_functionColumn.getSelectionModel().selectedItemProperty();
  }

  public IColumn<?> getFunctionColumnSelectedItem() {
    return m_functionColumn.getSelectionModel().selectedItemProperty().get();
  }

  public ReadOnlyObjectProperty<IFunction> functionSelectedItem() {
    return m_functions.getSelectionModel().selectedItemProperty();
  }

  public IFunction getFunctionSelectedItem() {
    return m_functions.getSelectionModel().selectedItemProperty().get();
  }

  private class ScoutColumnListCellFactory implements Callback<ListView<IColumn<?>>, ListCell<IColumn<?>>> {
    @Override
    public ListCell<IColumn<?>> call(ListView<IColumn<?>> param) {
      return new ScoutColumnListCell();
    }
  }

  private class ScoutColumnListCell extends ListCell<IColumn<?>> {

    @Override
    protected void updateItem(IColumn<?> item, boolean empty) {
      super.updateItem(item, empty);
      if (item != null) {
        String text = item.getHeaderCell().getText();
        if (StringUtility.isNullOrEmpty(text)) {
          text = TEXTS.get("Column") + " " + item.getColumnIndex();
        }
        item.getValues();

        setText(text);
      }
    }
  }

  private class FunctionListCellFactory implements Callback<ListView<IFunction>, ListCell<IFunction>> {
    @Override
    public ListCell<IFunction> call(ListView<IFunction> param) {
      return new FunctionListCell();
    }
  }

  private class FunctionListCell extends ListCell<IFunction> {

    @Override
    protected void updateItem(IFunction item, boolean empty) {
      super.updateItem(item, empty);
      if (item != null) {
        setText(TEXTS.get(item.getName()));
      }
    }
  }

}
