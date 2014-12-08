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
package org.eclipsescout.rt.ui.fx.basic.chart.table;

import java.util.Collection;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.rt.ui.fx.basic.chart.FxScoutChartRowData;

/**
 *
 */
public class ChartTableView extends TableView<FxScoutChartRowData> {

  private static final int TITLE_COLUMN = -1;
  private static final int TOTAL_COLUMN = -2;

  /**
   *
   */
  public ChartTableView() {

  }

  public void initialize(ObservableList<FxScoutChartRowData> data, Collection<String> columnNames) {
    getColumns().clear();

    TableColumn<FxScoutChartRowData, Object> column = new TableColumn<FxScoutChartRowData, Object>("");
    // factory to display content
    column.setCellValueFactory(new CellValueFactory(TITLE_COLUMN));
    getColumns().add(column);

    int i = 0;
    for (String columnName : columnNames) {
      column = new TableColumn<FxScoutChartRowData, Object>(columnName);
      // factory to display content
      column.setCellValueFactory(new CellValueFactory(i++));
      getColumns().add(column);
    }
    column = new TableColumn<FxScoutChartRowData, Object>(TEXTS.get("FxChartTableTotal"));
    // factory to display content
    column.setCellValueFactory(new CellValueFactory(TOTAL_COLUMN));
    getColumns().add(column);
    setItems(data);
  }

  /**
   * cell value factory, returns an observable value with the content of the cell
   */
  private class CellValueFactory implements Callback<TableColumn.CellDataFeatures<FxScoutChartRowData, Object>, ObservableValue<Object>> {

    private final int column;

    public CellValueFactory(int column) {
      this.column = column;
    }

    @Override
    public ObservableValue<Object> call(CellDataFeatures<FxScoutChartRowData, Object> param) {
      FxScoutChartRowData row = param.getValue();
      String text;
      if (column == TOTAL_COLUMN) {
        text = row.getTotal() == null ? null : row.getTotal().toString();
      }
      else if (column == TITLE_COLUMN) {
        text = row.getRowName();
      }
      else {
        text = row.getRowValues()[column] == null ? null : row.getRowValues()[column].toString();
      }
      return new SimpleObjectProperty<Object>(text);
    }

  }
}
