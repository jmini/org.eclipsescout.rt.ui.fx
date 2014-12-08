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
package org.eclipsescout.rt.ui.fx.basic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.eclipse.scout.commons.OptimisticLock;
import org.eclipse.scout.commons.beans.IPropertyObserver;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.profiler.DesktopProfiler;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;
import org.eclipsescout.rt.ui.fx.LogicalGridData;
import org.eclipsescout.rt.ui.fx.form.fields.LogicalGridDataBuilder;

/**
 *
 */
public abstract class FxScoutComposite<T extends IPropertyObserver> implements IFxScoutComposite<T> {
  public static final String PROP_FX_SCOUT_COMPOSITE = "IFxScoutComposite";

  private T m_scoutObject;
  private IFxEnvironment m_env;
  private Set<String> m_ignoredScoutEvents;
  private final OptimisticLock m_updateFxFromScoutLock;
  private P_ScoutPropertyChangeListener m_scoutPropertyListener;
  private boolean m_connectedToScout;
  private boolean m_inputDirty;
  private Node m_fxField;
  private P_FxDefaultFocusListener m_fxDefaultFocusListener;
  private boolean m_initialized;
  private P_FxKeyEventHandler m_keyEventHandler;

  public FxScoutComposite() {
    super();
    if (DesktopProfiler.getInstance().isEnabled()) {
      DesktopProfiler.getInstance().registerObject(this);
    }
    m_ignoredScoutEvents = new HashSet<String>();
    m_updateFxFromScoutLock = new OptimisticLock();
  }

  /**
   * @return the lock used in the swing thread when applying scout changes
   */
  public OptimisticLock getUpdateFxFromScoutLock() {
    return m_updateFxFromScoutLock;
  }

  @Override
  public final void createField(T scoutObject, IFxEnvironment environment) {
    m_scoutObject = scoutObject;
    m_env = environment;
    if (!m_initialized) {
      m_initialized = true;
      initialize();

      if (getFxContainer() != null) {
        if (getCompositeOnWidget(getFxContainer()) == null) {
          registerCompositeOnWidget(getFxContainer(), this);
        }
        P_FxAddRemoveListener listener = new P_FxAddRemoveListener();
        getFxContainer().sceneProperty().addListener(listener);
      }

      if (getFxField() != null) {
        if (getCompositeOnWidget(getFxField()) == null) {
          registerCompositeOnWidget(getFxField(), this);
        }
        P_FxAddRemoveListener listener = new P_FxAddRemoveListener();
        getFxField().sceneProperty().addListener(listener);
      }

      connectToScout();
    }
  }

  /**
   * Links the scout model with the ui.
   * This includes registering a listener
   * and applying informations in the scout model to the ui fields
   * in subclasses.
   */
  protected void attachScout() {
    if (m_scoutObject != null && m_scoutPropertyListener == null) {
      m_scoutPropertyListener = new P_ScoutPropertyChangeListener();
      m_scoutObject.addPropertyChangeListener(m_scoutPropertyListener);
    }
  }

  /**
   * Disconnects the link between the scout model and the ui field.
   */
  protected void detachScout() {
    if (m_scoutObject != null && m_scoutPropertyListener != null) {
      m_scoutObject.removePropertyChangeListener(m_scoutPropertyListener);
      m_scoutPropertyListener = null;
    }
  }

  protected void setFxField(Node fxField) {
    if (DesktopProfiler.getInstance().isEnabled()) {
      DesktopProfiler.getInstance().registerObject(fxField);
    }
    if (m_fxField == fxField) {
      return;
    }

    // remove old
    if (m_fxField != null) {
      if (m_fxDefaultFocusListener != null) {
        m_fxField.focusedProperty().removeListener(m_fxDefaultFocusListener);
        m_fxDefaultFocusListener = null;
      }
    }

    // add new
    m_fxField = fxField;
    if (m_fxField != null) {
      if (getScoutObject() instanceof IFormField) {
        // TODO: check if correct grid data are build
        m_fxField.getProperties().put(LogicalGridData.CLIENT_PROPERTY_NAME, LogicalGridDataBuilder.createField(getFxEnvironment(), ((IFormField) getScoutObject()).getGridData()));
      }
      m_fxField.focusedProperty().addListener(m_fxDefaultFocusListener = new P_FxDefaultFocusListener());
      m_keyEventHandler = new P_FxKeyEventHandler();
      m_fxField.addEventHandler(KeyEvent.KEY_RELEASED, m_keyEventHandler);
    }
  }

  @Override
  public Node getFxField() {
    return m_fxField;
  }

  @Override
  public Node getFxContainer() {
    return m_fxField;
  }

  @Override
  public final void connectToScout() {
    if (!m_connectedToScout) {
      m_connectedToScout = true;
      //TODO connect child nodes if necessary
      try {
        m_updateFxFromScoutLock.acquire();
        attachScout();
      }
      finally {
        m_updateFxFromScoutLock.release();
      }
    }
  }

  @Override
  public final void disconnectFromScout() {
    if (m_connectedToScout) {
      m_connectedToScout = false;
      //TODO disconnect child nodes if necessary
      try {
        m_updateFxFromScoutLock.acquire();
        detachScout();
      }
      finally {
        m_updateFxFromScoutLock.release();
      }
    }
  }

  /**
   * Initializes the ui field and its components.
   */
  protected abstract void initialize();

  @Override
  public T getScoutObject() {
    return m_scoutObject;
  }

  @Override
  public IFxEnvironment getFxEnvironment() {
    return m_env;
  }

  /**
   * @param inputDirty
   *          whether or not the input is dirty
   */
  protected void setInputDirty(boolean inputDirty) {
    m_inputDirty = inputDirty;
  }

  /**
   * @return true, if input is dirty
   */
  protected boolean isInputDirty() {
    return m_inputDirty;
  }

  /**
   * add an event description that, when scout sends it, is ignored
   */
  public void addIgnoredScoutEvent(Class eventType, String name) {
    m_ignoredScoutEvents.add(eventType.getSimpleName() + ":" + name);
  }

  /**
   * remove an event description so that when scout sends it, it is processed
   */
  public void removeIgnoredScoutEvent(Class eventType, String name) {
    m_ignoredScoutEvents.remove(eventType.getSimpleName() + ":" + name);
  }

  /**
   * @return true if that scout event is ignored
   */
  public boolean isIgnoredScoutEvent(Class eventType, String name) {
    if (m_ignoredScoutEvents.isEmpty()) {
      return false;
    }
    return m_ignoredScoutEvents.contains(eventType.getSimpleName() + ":" + name);
  }

  /**
   * pre-processor for scout properties (in Scout Thread) decision whether a
   * handleScoutPropertyChange is queued to the fx thread runs in scout
   * thread
   */
  protected boolean isHandleScoutPropertyChange(String name, Object newValue) {
    return true;
  }

  /**
   * Verifies that the input on the field has been changed
   * and sets this input on the scout model to keep consistency.
   *
   * @return whether or not the input has been verified
   */
  protected boolean handleFxInputVerifier() {
    return true;
  }

  protected void handleFxFocusGained() {
  }

  protected void handleFxFocusLost() {
  }

  /**
   * Handles changes to the scout model
   *
   * @param name
   *          naming of the property in the scout model
   * @param newValue
   *          changed value of the property
   */
  protected void handleScoutPropertyChange(String name, Object newValue) {
  }

  protected void handleFxAddNotify() {
    connectToScout();
  }

  public void handleFxRemoveNotify() {
    disconnectFromScout();
  }

  /**
   * Puts the specified fx-scout composite into the property list of the specified node.
   *
   * @param node
   * @param ui
   */
  public static void registerCompositeOnWidget(Node node, IFxScoutComposite ui) {
    if (node != null) {
      node.getProperties().put(PROP_FX_SCOUT_COMPOSITE, new WeakReference<IFxScoutComposite>(ui));
    }
  }

  /**
   * Returns the fx-scout composite of the specifed node or null.
   *
   * @param node
   * @return
   */
  public static IFxScoutComposite<?> getCompositeOnWidget(Node node) {
    @SuppressWarnings("unchecked")
    WeakReference<IFxScoutComposite> ref = (WeakReference<IFxScoutComposite>) node.getProperties().get(PROP_FX_SCOUT_COMPOSITE);
    return ref != null ? ref.get() : null;
  }

  /**
   * Listener that listens on changes in the scout model.
   */
  private class P_ScoutPropertyChangeListener implements PropertyChangeListener {

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
      if (isIgnoredScoutEvent(PropertyChangeEvent.class, evt.getPropertyName())) {
        return;
      }
      if (isHandleScoutPropertyChange(evt.getPropertyName(), evt.getNewValue())) {
        Runnable r = new Runnable() {
          @Override
          public void run() {
            try {
              m_updateFxFromScoutLock.acquire();
              handleScoutPropertyChange(evt.getPropertyName(), evt.getNewValue());
            }
            finally {
              m_updateFxFromScoutLock.release();
            }
          }

        };
        getFxEnvironment().invokeFxLater(r);
      }
    }
  }// end private class

  /**
   * Listener meant to react when a field gains or looses focus.
   */
  private class P_FxDefaultFocusListener implements ChangeListener<Boolean> {
    private boolean m_lastResult = true;

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
      // focus lost
      if (oldValue && !newValue) {
        handleFxFocusLost();
        if (isInputDirty()) {
          if (m_updateFxFromScoutLock.isReleased()) {
            m_lastResult = handleFxInputVerifier();
          }
        }
        if (m_lastResult) {
          setInputDirty(false);
        }
      }
      // focus gained
      if (!oldValue && newValue) {
        setInputDirty(true);
        handleFxFocusGained();
      }
    }
  }// end private class

  private class P_FxAddRemoveListener implements ChangeListener<Scene> {

    @Override
    public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
      if (!m_initialized || oldValue == newValue) {
        return;
      }
      // detached
      if (oldValue != null) {
        handleFxRemoveNotify();
      }
      // attached
      if (newValue != null) {
        handleFxAddNotify();
      }
    }
  }// end private class

  private class P_FxKeyEventHandler implements EventHandler<KeyEvent> {
    private boolean m_lastResult = true;

    @Override
    public void handle(KeyEvent event) {
      if (event.getCode() == KeyCode.ENTER) {
        if (isInputDirty()) {
          if (m_updateFxFromScoutLock.isReleased()) {
            m_lastResult = handleFxInputVerifier();
          }
        }
        if (m_lastResult) {
          setInputDirty(false);
        }
      }
    }
  }
}
