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

import java.util.concurrent.atomic.AtomicReference;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.IColumn;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipsescout.rt.ui.fx.basic.IFxScoutComposite;
import org.eclipsescout.rt.ui.fx.form.fields.IFxScoutFormField;

/**
 *
 */
public class FxScoutTableCell extends TableCell<ITableRow, Object> {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(FxScoutTableCell.class);
  {
    LOG.setLevel(IScoutLogger.LEVEL_DEBUG);
  }

  private final boolean m_isEditorCompositePermanentVisible;
  private final IFxScoutTable m_tableComposite;
  private final int m_columnIndex;
  private Node m_cachedFxEditorComponent;

  public FxScoutTableCell(IFxScoutTable tableComposite, int columnIndex, boolean isEditorCompositePermanentVisible) {
    // TODO: check if it works with permanent editable cells
    m_tableComposite = tableComposite;
    m_columnIndex = columnIndex;
    m_isEditorCompositePermanentVisible = isEditorCompositePermanentVisible;
  }

  public final int getColumnIndex() {
    return m_columnIndex;
  }

  public final int getRowIndex() {
    return getIndex();
  }

  protected Node getCachedEditorComposite() {
    if (m_cachedFxEditorComponent == null) {
      IFxScoutComposite<? extends IFormField> editorComposite = createEditorComposite();
      if (editorComposite != null) {
        decorateEditorComposite(editorComposite);
        m_cachedFxEditorComponent = editorComposite.getFxContainer();
      }
      else {
        m_cachedFxEditorComponent = null;
      }
    }
    return m_cachedFxEditorComponent;
  }

  protected IFxScoutFormField<?> createEditorComposite() {
    final ITableRow scoutRow = m_tableComposite.getScoutObject().getFilteredRow(getRowIndex());
    final IColumn scoutColumn = m_tableComposite.getScoutObject().getColumnSet().getVisibleColumn(getColumnIndex());

    final AtomicReference<IFormField> fieldRef = new AtomicReference<IFormField>();
    if (scoutRow != null && scoutColumn != null) {
      Runnable r = new Runnable() {
        @Override
        public void run() {
          fieldRef.set(m_tableComposite.getScoutObject().getUIFacade().prepareCellEditFromUI(scoutRow, scoutColumn));
          synchronized (fieldRef) {
            fieldRef.notifyAll();
          }
        }
      };
      synchronized (fieldRef) {
        m_tableComposite.getFxEnvironment().invokeScoutLater(r, 2345);
        try {
          fieldRef.wait(2345);
        }
        catch (InterruptedException e) {
          //nop
        }
      }
    }
    IFormField formField = fieldRef.get();
    if (formField == null) {
      return null;
    }
    return m_tableComposite.getFxEnvironment().createFormField(m_tableComposite.getFxTable(), formField);
  }

  protected void decorateEditorComposite(IFxScoutComposite<? extends IFormField> editorComposite) {
    EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
          // dummy value, table content must be reset
          commitEdit("");
        }
        else if (event.getCode() == KeyCode.ESCAPE) {
          cancelEdit();
        }
      }
    };
    addEventHandler(KeyEvent.KEY_RELEASED, keyEventHandler);
  }

  protected void saveEditorFromFx() {
    if (m_cachedFxEditorComponent != null) {
      m_cachedFxEditorComponent = null;
      Runnable t = new Runnable() {
        @Override
        public void run() {
          m_tableComposite.getScoutObject().getUIFacade().completeCellEditFromUI();
        }
      };
      m_tableComposite.getFxEnvironment().invokeScoutLater(t, 0);
    }
  }

  protected void cancelEditorFromFx() {
    if (m_cachedFxEditorComponent != null) {
      m_cachedFxEditorComponent = null;
      Runnable t = new Runnable() {
        @Override
        public void run() {
          m_tableComposite.getScoutObject().getUIFacade().cancelCellEditFromUI();
        }
      };
      m_tableComposite.getFxEnvironment().invokeScoutLater(t, 0);
    }
  }

  @Override
  public void startEdit() {
    super.startEdit();

    setEditorComposite();
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();

    cancelEditorFromFx();
    removeEditorComposite();
  }

  @Override
  public void commitEdit(Object newValue) {
    super.commitEdit(newValue);

    saveEditorFromFx();
    removeEditorComposite();
  }

  @Override
  protected void updateItem(Object item, boolean empty) {
    super.updateItem(item, empty);

    if (empty) {
      setText(null);
      setGraphic(null);
    }
    else {
      if (m_isEditorCompositePermanentVisible) {
        setEditorComposite();
      }
      else {
        if (isEditing()) {
          setEditorComposite();
        }
        else {
          removeEditorComposite();
        }
      }
    }
  }

  private void removeEditorComposite() {
    if (m_isEditorCompositePermanentVisible) {
      return;
    }

    setText(getItem() == null ? "" : getItem().toString());
    setGraphic(null);
  }

  private void setEditorComposite() {
    setText(null);
    setGraphic(getCachedEditorComposite());
  }

}
