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
package org.eclipsescout.rt.ui.fx.basic.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ui.basic.cell.ICell;
import org.eclipse.scout.rt.client.ui.basic.table.ITable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.TableEvent;
import org.eclipse.scout.rt.client.ui.basic.table.TableListener;
import org.eclipse.scout.rt.client.ui.basic.table.columns.IColumn;
import org.eclipsescout.rt.ui.fx.basic.FxScoutComposite;

/**
 *
 */
public class FxScoutTable extends FxScoutComposite<ITable> implements IFxScoutTable {

  private static final IScoutLogger LOG = ScoutLogManager.getLogger(FxScoutTable.class);
  {
    LOG.setLevel(IScoutLogger.LEVEL_DEBUG);
  }

  private P_ScoutTableListener m_scoutTableListener;
  private ObservableList<ITableRow> m_tableData;

  @Override
  protected void initialize() {

    // TODO: improve complete implementation
    TableView<ITableRow> fxTable = new TableView<ITableRow>();
    fxTable.setEditable(true);

    for (final IColumn<?> scoutColumn : getScoutObject().getColumns()) {

      TableColumn<ITableRow, Object> fxColumn = new TableColumn<ITableRow, Object>(scoutColumn.getHeaderCell().getText());

      // factory to display content
      fxColumn.setCellValueFactory(new CellValueFactory(scoutColumn.getColumnIndex()));

      fxColumn.setEditable(scoutColumn.isEditable());
      fxColumn.setPrefWidth(scoutColumn.getInitialWidth());
      fxColumn.setVisible(scoutColumn.isVisible());

      // factory to edit content
      fxColumn.setCellFactory(new CellFactory(scoutColumn.getColumnIndex(), isEditorCompositePermanentVisible(scoutColumn.getDataType())));

      fxTable.getColumns().add(fxColumn);
    }

    m_tableData = FXCollections.observableArrayList(getScoutObject().getRows());
    rebuildTableData();
    fxTable.setOnMouseClicked(new P_FxRowMouseListener());
    fxTable.setItems(m_tableData);

    setFxField(fxTable);

  }

  protected void rebuildTableData() {
    m_tableData.setAll(getScoutObject().getRows());
  }

  @Override
  protected void attachScout() {
    super.attachScout();
    if (getScoutObject() == null) {
      return;
    }

    if (m_scoutTableListener == null) {
      m_scoutTableListener = new P_ScoutTableListener();
      getScoutObject().addUITableListener(m_scoutTableListener);
    }
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

  protected boolean isEditorCompositePermanentVisible(Class<?> cls) {
    return Boolean.class.isAssignableFrom(cls);
  }

  @SuppressWarnings("unchecked")
  @Override
  public TableView<ITableRow> getFxTable() {
    return (TableView<ITableRow>) getFxField();
  }

  protected boolean isHandleScoutTableEvent(List<? extends TableEvent> a) {
    for (int i = 0; i < a.size(); i++) {
      switch (a.get(i).getType()) {
        case TableEvent.TYPE_REQUEST_FOCUS:
        case TableEvent.TYPE_REQUEST_FOCUS_IN_CELL:
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
      case TableEvent.TYPE_REQUEST_FOCUS: {
        getFxTable().requestFocus();
        break;
      }
      case TableEvent.TYPE_ROWS_DELETED:
      case TableEvent.TYPE_ALL_ROWS_DELETED: {
        rebuildTableData();
        break;
      }
      case TableEvent.TYPE_ROWS_UPDATED:
      case TableEvent.TYPE_ROW_ORDER_CHANGED: {
        rebuildTableData();
        break;
      }
      case TableEvent.TYPE_ROWS_INSERTED:
      case TableEvent.TYPE_ROW_FILTER_CHANGED: {
        rebuildTableData();
        break;
      }
      case TableEvent.TYPE_COLUMN_ORDER_CHANGED:
      case TableEvent.TYPE_COLUMN_HEADERS_UPDATED:
      case TableEvent.TYPE_COLUMN_STRUCTURE_CHANGED: {
        rebuildTableData();
        break;
      }
    }
  }

  protected void handleFxRowClick() {
    if (getUpdateFxFromScoutLock().isAcquired()) {
      return;
    }
    //
    final ITableRow scoutRow = getFxTable().getSelectionModel().getSelectedItem();
    if (getScoutObject() != null && scoutRow != null) {
      // notify Scout
      Runnable t = new Runnable() {
        @Override
        public void run() {
          getScoutObject().getUIFacade().fireRowClickFromUI(scoutRow);
        }
      };
      getFxEnvironment().invokeScoutLater(t, 0);
      // end notify
    }
  }

  protected void handleFxRowAction() {
    if (getUpdateFxFromScoutLock().isAcquired()) {
      return;
    }
    //
    final ITableRow scoutRow = getFxTable().getSelectionModel().getSelectedItem();
    if (getScoutObject() != null && scoutRow != null) {
      // notify Scout
      Runnable r = new Runnable() {
        @Override
        public void run() {
          getScoutObject().getUIFacade().fireRowActionFromUI(scoutRow);
        }
      };
      getFxEnvironment().invokeScoutLater(r, 0);
      // end notify
    }
  }

//
//
// Private Classes
//
//

  /**
   * cell value factory, returns an observable value with the content of the cell
   */
  private class CellValueFactory implements Callback<TableColumn.CellDataFeatures<ITableRow, Object>, ObservableValue<Object>> {

    private final int column;

    public CellValueFactory(int column) {
      this.column = column;
    }

    @Override
    public ObservableValue<Object> call(CellDataFeatures<ITableRow, Object> param) {
      ITableRow row = param.getValue();
      ICell cell = row.getCell(column);
      return new SimpleObjectProperty<Object>(cell.getText());
    }
  }

  /**
   * cell factory which returns a table cell to render in the table view
   */
  private class CellFactory implements Callback<TableColumn<ITableRow, Object>, TableCell<ITableRow, Object>> {

    private int m_columnIndex;
    private boolean m_isEditorCompositePermanentVisible;

    /**
     * @param fxScoutTable
     * @param columnIndex
     * @param isEditorCompositePermanentVisible
     */
    public CellFactory(int columnIndex, boolean isEditorCompositePermanentVisible) {
      m_columnIndex = columnIndex;
      m_isEditorCompositePermanentVisible = isEditorCompositePermanentVisible;
    }

    @Override
    public TableCell<ITableRow, Object> call(TableColumn<ITableRow, Object> param) {
      return new FxScoutTableCell(FxScoutTable.this, m_columnIndex, m_isEditorCompositePermanentVisible);
    }
  }

  private class P_FxRowMouseListener implements EventHandler<MouseEvent> {
    @Override
    public void handle(MouseEvent event) {
      if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
        handleFxRowClick();
      }
      else if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
        handleFxRowAction();
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
