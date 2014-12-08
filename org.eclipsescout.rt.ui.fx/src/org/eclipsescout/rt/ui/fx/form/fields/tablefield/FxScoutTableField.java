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

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import org.eclipse.scout.commons.exception.IProcessingStatus;
import org.eclipse.scout.rt.client.ui.basic.table.ITable;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.ITableField;
import org.eclipsescout.rt.ui.fx.LogicalGridPane;
import org.eclipsescout.rt.ui.fx.basic.table.IFxScoutTable;
import org.eclipsescout.rt.ui.fx.ext.FxStatusPaneEx;
import org.eclipsescout.rt.ui.fx.form.fields.FxScoutFieldComposite;

/**
 *
 */
public class FxScoutTableField extends FxScoutFieldComposite<ITableField<?>> implements IFxScoutTableField {

  private IFxScoutTable m_tableComposite;
  private IFxTableStatus m_fxTableStatus;

  @Override
  protected void initialize() {
    LogicalGridPane container = new LogicalGridPane(getFxEnvironment(), 1, 0);
    FxStatusPaneEx statusPane = getFxEnvironment().createStatusLabel(getScoutObject());
    container.getChildren().add(statusPane);
    setFxContainer(container);
    setFxStatusPane(statusPane);

    m_fxTableStatus = createFxTableStatus(container);
  }

  /**
   * @param container
   * @return
   */
  protected IFxTableStatus createFxTableStatus(LogicalGridPane container) {
    if (getScoutObject().isTableStatusVisible()) {
      return new FxTableStatus();
    }
    return null;
  }

  @Override
  public TableView<?> getFxTable() {
    return m_tableComposite != null ? m_tableComposite.getFxTable() : null;
  }

  @Override
  public IFxTableStatus getFxTableStatus() {
    return m_fxTableStatus;
  }

  @Override
  protected void attachScout() {
    super.attachScout();
    setTableFromScout();
    setTableStatusFromScout();
  }

  @Override
  protected void setEnabledFromScout(boolean b) {
    // no super call, don't disable table to further support selection and menus
    getFxStatusPane().setDisable(!b);
  }

  @Override
  protected void handleScoutPropertyChange(String name, Object newValue) {
    super.handleScoutPropertyChange(name, newValue);
    if (name.equals(ITableField.PROP_TABLE)) {
      setTableFromScout();
    }
    else if (name.equals(ITableField.PROP_TABLE_SELECTION_STATUS)) {
      setTableStatusFromScout();
    }
    else if (name.equals(ITableField.PROP_TABLE_POPULATE_STATUS)) {
      setTableStatusFromScout();
    }
  }

  /**
   *
   */
  protected void setTableStatusFromScout() {
    if (m_fxTableStatus != null) {
      IProcessingStatus dataStatus = getScoutObject().getTablePopulateStatus();
      IProcessingStatus selectionStatus = getScoutObject().getTableSelectionStatus();
      m_fxTableStatus.setStatus(dataStatus, selectionStatus);
    }
  }

  /**
   *
   */
  protected void setTableFromScout() {
    ITable oldTable = m_tableComposite != null ? m_tableComposite.getScoutObject() : null;
    ITable newTable = getScoutObject().getTable();
    if (oldTable != newTable) {
      removeOldTable();
      if (newTable != null) {
        IFxScoutTable newTableComposite = getFxEnvironment().createTable(newTable);
        newTableComposite.createField(newTable, getFxEnvironment());

        m_tableComposite = newTableComposite;

        setFxField(getFxFieldForSetter());
        decorateContentPaneChild(m_tableComposite.getFxTable());
        getContentPane().getChildren().add(m_tableComposite.getFxTable());
      }
    }
  }

  protected void decorateContentPaneChild(Node node) {
  }

  protected Node getFxFieldForSetter() {
    return m_tableComposite.getFxTable();
  }

  /**
   * Returns the pane which contains the table view.
   *
   * @return
   */
  protected Pane getContentPane() {
    return (LogicalGridPane) getFxContainer();
  }

  protected void removeOldTable() {
    if (m_tableComposite != null) {
      getContentPane().getChildren().remove(m_tableComposite.getFxTable());
      setFxField(null);
      m_tableComposite.disconnectFromScout();
      m_tableComposite = null;
    }
  }
}
