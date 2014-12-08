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
package org.eclipsescout.rt.ui.fx.form;

import java.util.WeakHashMap;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ui.IEventHistory;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.button.IButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.IGroupBox;
import org.eclipsescout.rt.ui.fx.FxUtility;
import org.eclipsescout.rt.ui.fx.LogicalGridPane;
import org.eclipsescout.rt.ui.fx.basic.FxScoutComposite;
import org.eclipsescout.rt.ui.fx.basic.IFxScoutComposite;
import org.eclipsescout.rt.ui.fx.form.fields.FxScoutFormFieldGridData;
import org.eclipsescout.rt.ui.fx.form.fields.groupbox.IFxScoutGroupBox;
import org.eclipsescout.rt.ui.fx.window.FxScoutViewEvent;
import org.eclipsescout.rt.ui.fx.window.IFxScoutView;
import org.eclipsescout.rt.ui.fx.window.IFxScoutViewListener;

/**
 *
 */
public class FxScoutForm extends FxScoutComposite<IForm> implements IFxScoutForm {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(FxScoutForm.class);

  private IFxScoutGroupBox m_mainBoxComposite;
  private FormListener m_scoutFormListener;
  private IFxScoutViewListener m_fxScoutViewListener;
  private IFxScoutView m_viewComposite;
  private WeakHashMap<FormEvent, Object> m_consumedScoutFormEvents = new WeakHashMap<FormEvent, Object>();

  /**
   * @param targetViewComposite
   */
  public FxScoutForm(IFxScoutView targetViewComposite) {
    m_viewComposite = targetViewComposite;
  }

  @Override
  protected void initialize() {
    IGroupBox rootGroupBox = getScoutForm().getRootGroupBox();
    Pane parent = null;
    if (m_viewComposite != null) {
      parent = m_viewComposite.getFxContentPane();
    }
    m_mainBoxComposite = (IFxScoutGroupBox) getFxEnvironment().createFormField(parent, rootGroupBox);
    m_mainBoxComposite.createField(rootGroupBox, getFxEnvironment());
    // TODO: check container class
    Pane paneRootGroupBox = (Pane) m_mainBoxComposite.getFxContainer();
    setFxField(paneRootGroupBox);
    if (m_viewComposite != null) {
      // attach to view
      m_viewComposite.getFxContentPane().getChildren().clear();
      // use grid layout with decent min-width
      LogicalGridPane optimalSizePane = new LogicalGridPane(getFxEnvironment(), 0, 0);
      FxScoutFormFieldGridData layoutData = new FxScoutFormFieldGridData(getScoutForm().getRootGroupBox());
      optimalSizePane.add(getFxFormPane(), layoutData);
      m_viewComposite.getFxContentPane().getChildren().add(optimalSizePane);
      attachFxView();
    }
  }

  @Override
  public Pane getFxFormPane() {
    return (Pane) getFxField();
  }

  public IForm getScoutForm() {
    return getScoutObject();
  }

  @Override
  public void detachFxView() {
    if (m_viewComposite != null) {
      if (m_fxScoutViewListener != null) {
        m_viewComposite.removeFxScoutViewListener(m_fxScoutViewListener);
        m_fxScoutViewListener = null;
      }
      // remove content
      m_viewComposite.getFxContentPane().getChildren().clear();
    }
    // force disconnect from model
    disconnectFromScout();
  }

  private void attachFxView() {
    if (m_viewComposite != null) {
      IForm scoutForm = getScoutForm();
      m_viewComposite.setTitle(scoutForm.getTitle());
      m_viewComposite.setMaximizeEnabled(scoutForm.isMaximizeEnabled());
      m_viewComposite.setMinimizeEnabled(scoutForm.isMinimizeEnabled());
      m_viewComposite.setIcon(getFxEnvironment().getImage(scoutForm.getIconId()));
      boolean closable = false;
      for (IFormField f : scoutForm.getAllFields()) {
        if (f.isEnabled() && f.isVisible() && (f instanceof IButton)) {
          switch (((IButton) f).getSystemType()) {
            case IButton.SYSTEM_TYPE_CLOSE:
            case IButton.SYSTEM_TYPE_CANCEL: {
              closable = true;
              break;
            }
          }
        }
        if (closable) {
          break;
        }
      }
      m_viewComposite.setCloseEnabled(closable);
      m_viewComposite.setMaximized(scoutForm.isMaximized());
      m_viewComposite.setMinimized(scoutForm.isMinimized());

      m_fxScoutViewListener = new P_FxScoutViewListener();
      m_viewComposite.addFxScoutViewListener(m_fxScoutViewListener);
      // generate events if view is already showing or active
      if (m_viewComposite.isVisible()) {
        m_fxScoutViewListener.viewChanged(new FxScoutViewEvent(m_viewComposite, FxScoutViewEvent.TYPE_OPENED));
        if (m_viewComposite.isActive()) {
          m_fxScoutViewListener.viewChanged(new FxScoutViewEvent(m_viewComposite, FxScoutViewEvent.TYPE_ACTIVATED));
        }
      }
    }
  }

  @Override
  public IFxScoutView getView() {
    return m_viewComposite;
  }

  @Override
  protected void attachScout() {
    super.attachScout();
    if (m_scoutFormListener == null) {
      m_scoutFormListener = new P_ScoutFormListener();
      getScoutForm().addFormListener(m_scoutFormListener);
    }
    // process all pending events, except requestFocus
    IEventHistory<FormEvent> h = getScoutObject().getEventHistory();
    if (h != null) {
      for (FormEvent e : h.getRecentEvents()) {
        switch (e.getType()) {
          case FormEvent.TYPE_TO_BACK:
          case FormEvent.TYPE_TO_FRONT:
          case FormEvent.TYPE_PRINT: {
            handleScoutFormEventInUi(e);
            break;
          }
        }
      }
    }
  }

  @Override
  protected void detachScout() {
    super.detachScout();
    if (m_scoutFormListener != null) {
      getScoutForm().removeFormListener(m_scoutFormListener);
      m_scoutFormListener = null;
    }
  }

  @Override
  public void setInitialFocus() {
    IFormField modelField = null;
    //check for request focus events in history
    IEventHistory<FormEvent> h = getScoutObject().getEventHistory();
    if (h != null) {
      for (FormEvent e : h.getRecentEvents()) {
        if (e.getType() == FormEvent.TYPE_REQUEST_FOCUS) {
          modelField = e.getFormField();
          break;
        }
      }
    }
    if (modelField != null) {
      handleRequestFocusFromScout(modelField, true);
    }
  }

  @Override
  protected void handleScoutPropertyChange(String name, Object newValue) {
    super.handleScoutPropertyChange(name, newValue);
    if (name.equals(IForm.PROP_TITLE)) {
      if (m_viewComposite != null) {
        m_viewComposite.setTitle((String) newValue);
      }
    }
    else if (name.equals(IForm.PROP_MINIMIZE_ENABLED)) {
      if (m_viewComposite != null) {
        m_viewComposite.setMinimizeEnabled(((Boolean) newValue).booleanValue());
      }
    }
    else if (name.equals(IForm.PROP_MAXIMIZE_ENABLED)) {
      if (m_viewComposite != null) {
        m_viewComposite.setMaximizeEnabled(((Boolean) newValue).booleanValue());
      }
    }
    else if (name.equals(IForm.PROP_MINIMIZED)) {
      if (m_viewComposite != null) {
        m_viewComposite.setMinimized(((Boolean) newValue).booleanValue());
      }
    }
    else if (name.equals(IForm.PROP_MAXIMIZED)) {
      if (m_viewComposite != null) {
        m_viewComposite.setMaximized(((Boolean) newValue).booleanValue());
      }
    }
  }

  protected void handleScoutFormEventInUi(final FormEvent e) {
    if (m_consumedScoutFormEvents.containsKey(e)) {
      return;
    }
    m_consumedScoutFormEvents.put(e, Boolean.TRUE);
    //
    switch (e.getType()) {
      case FormEvent.TYPE_PRINT: {
        handlePrintFromScout(e);
        break;
      }
      case FormEvent.TYPE_TO_FRONT: {
        handleToFrontFromScout();
        break;
      }
      case FormEvent.TYPE_TO_BACK: {
        handleToBackFromScout();
        break;
      }
      case FormEvent.TYPE_REQUEST_FOCUS: {
        handleRequestFocusFromScout(e.getFormField(), false);
        break;
      }
    }
  }

  protected void handlePrintFromScout(final FormEvent e) {
    // TODO: implement
    LOG.warn("printing not yet implemented");
  }

  protected void handleToFrontFromScout() {
    if (getView() == null) {
      return;
    }
    Stage s = (Stage) getView().getFxContentPane().getScene().getWindow();
    if (s.isShowing()) {
      s.toFront();
    }
  }

  protected void handleToBackFromScout() {
    if (getView() == null) {
      return;
    }
    Stage s = (Stage) getView().getFxContentPane().getScene().getWindow();
    if (s.isShowing()) {
      s.toBack();
    }
  }

  protected void handleRequestFocusFromScout(IFormField modelField, boolean force) {
    if (modelField == null) {
      return;
    }
    Node node = findUiField(modelField);
    if (node != null && node.isVisible()) {
      node.requestFocus();
    }
  }

  private Node findUiField(IFormField modelField) {
    if (modelField == null) {
      return null;
    }
    for (Node node : FxUtility.findAllChildNodes(getFxContainer())) {
      IFxScoutComposite<?> composite = FxScoutComposite.getCompositeOnWidget(node);
      if (composite != null && composite.getScoutObject() == modelField) {
        return composite.getFxField();
      }
    }
    return null;
  }

  private class P_FxScoutViewListener implements IFxScoutViewListener {

    @Override
    public void viewChanged(FxScoutViewEvent e) {
      switch (e.getType()) {
        case FxScoutViewEvent.TYPE_OPENED: {
          break;
        }
        case FxScoutViewEvent.TYPE_ACTIVATED: {
          // notify Scout
          Runnable t = new Runnable() {
            @Override
            public void run() {
              getScoutForm().getUIFacade().fireFormActivatedFromUI();
            }
          };

          getFxEnvironment().invokeScoutLater(t, 0);
          // end notify
          break;
        }
        case FxScoutViewEvent.TYPE_CLOSING: {
          // notify Scout
          Runnable t = new Runnable() {
            @Override
            public void run() {
              getScoutForm().getUIFacade().fireFormClosingFromUI();
            }
          };

          getFxEnvironment().invokeScoutLater(t, 0);
          // end notify
          break;
        }
        case FxScoutViewEvent.TYPE_CLOSED: {
          // notify Scout
          Runnable t = new Runnable() {
            @Override
            public void run() {
              getScoutForm().getUIFacade().fireFormKilledFromUI();
            }
          };

          getFxEnvironment().invokeScoutLater(t, 0);
          // end notify
          break;
        }
      }
    }

  }

  private class P_ScoutFormListener implements FormListener {

    @Override
    public void formChanged(final FormEvent e) throws ProcessingException {
      switch (e.getType()) {
        case FormEvent.TYPE_STRUCTURE_CHANGED: {
          // TODO: check if correct and complete implemented
          Window w = getFxContainer().getScene().getWindow();
          if (!w.isFocused()) {
            w = null;
          }
          if (w != null) {
            Node n = getFxContainer().getScene().getFocusOwner();
            if (n == null) {
              getFxContainer().requestFocus();
            }
          }
          break;
        }
        case FormEvent.TYPE_PRINT:
        case FormEvent.TYPE_TO_FRONT:
        case FormEvent.TYPE_TO_BACK:
        case FormEvent.TYPE_REQUEST_FOCUS: {
          Runnable t = new Runnable() {
            @Override
            public void run() {
              handleScoutFormEventInUi(e);
            }
          };
          getFxEnvironment().invokeFxLater(t);
          break;
        }
      }
    }

  }

}
