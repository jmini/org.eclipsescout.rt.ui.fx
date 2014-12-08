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
package org.eclipsescout.rt.ui.fx.window.internalwindow;

import java.util.EventListener;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import org.eclipse.scout.commons.EventListenerList;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;
import org.eclipsescout.rt.ui.fx.control.InternalWindow;
import org.eclipsescout.rt.ui.fx.control.event.InternalWindowEvent;
import org.eclipsescout.rt.ui.fx.window.FxScoutViewEvent;
import org.eclipsescout.rt.ui.fx.window.IFxScoutView;
import org.eclipsescout.rt.ui.fx.window.IFxScoutViewListener;

/**
 *
 */
public class FxScoutInternalWindow implements IFxScoutView {

  private IFxEnvironment m_env;
  private boolean m_addedToDesktop;
  private EventListenerList m_listenerList;
  private InternalWindow m_fxView;

  private final EventHandler<InternalWindowEvent> internalWindowEventHandler = new EventHandler<InternalWindowEvent>() {
    @Override
    public void handle(InternalWindowEvent event) {
      if (event.getEventType() == InternalWindowEvent.INTERNAL_WINDOW_OPENED) {
        fireFxScoutViewEvent(new FxScoutViewEvent(FxScoutInternalWindow.this, FxScoutViewEvent.TYPE_OPENED));
        if (m_fxView.isVisible() && !m_fxView.isIconified() && !m_fxView.isActivated()) {
          m_fxView.requestFocus();
        }
      }
      else if (event.getEventType() == InternalWindowEvent.INTERNAL_WINDOW_ACTIVATED) {
        fireFxScoutViewEvent(new FxScoutViewEvent(FxScoutInternalWindow.this, FxScoutViewEvent.TYPE_ACTIVATED));
      }
      else if (event.getEventType() == InternalWindowEvent.INTERNAL_WINDOW_CLOSE_REQUEST) {
        event.consume();
        // TODO: inputverifier?
        fireFxScoutViewEvent(new FxScoutViewEvent(FxScoutInternalWindow.this, FxScoutViewEvent.TYPE_CLOSING));
      }
      else if (event.getEventType() == InternalWindowEvent.INTERNAL_WINDOW_CLOSED) {
        // TODO: inputverifier?
        fireFxScoutViewEvent(new FxScoutViewEvent(FxScoutInternalWindow.this, FxScoutViewEvent.TYPE_CLOSED));
      }
    }
  };

  /**
   * @param abstractFxEnvironment
   */
  public FxScoutInternalWindow(IFxEnvironment env) {
    m_env = env;

    m_listenerList = new EventListenerList();

    m_fxView = new InternalWindow("", false, false, false);

    StackPane p = new StackPane();
    p.setAlignment(Pos.TOP_LEFT);
    m_fxView.setRoot(p);

    m_fxView.addEventHandler(InternalWindowEvent.INTERNAL_WINDOW_OPENED, internalWindowEventHandler);
    m_fxView.addEventHandler(InternalWindowEvent.INTERNAL_WINDOW_ACTIVATED, internalWindowEventHandler);
    m_fxView.setOnCloseRequest(internalWindowEventHandler);
    m_fxView.addEventHandler(InternalWindowEvent.INTERNAL_WINDOW_CLOSED, internalWindowEventHandler);

  }

  public InternalWindow getFxInternalWindow() {
    return m_fxView;
  }

  @Override
  public void setName(String name) {
    m_fxView.setId(name);
  }

  @Override
  public void openView() {
    // TODO: change
    m_env.getScoutRootStage().addView(m_fxView);
    m_addedToDesktop = true;
  }

  @Override
  public void setTitle(String title) {
    if (title == null) {
      title = "";
    }
    m_fxView.setTitle(title);
  }

  @Override
  public void closeView() {
    // TODO: change
    m_addedToDesktop = false;
    m_env.getScoutRootStage().removeView(m_fxView);
  }

  @Override
  public boolean isVisible() {
    return m_addedToDesktop;
  }

  @Override
  public boolean isActive() {
    return m_fxView != null && m_fxView.isActivated();
  }

  @Override
  public void setCloseEnabled(boolean b) {
    m_fxView.setClosable(b);
  }

  @Override
  public void setMaximizeEnabled(boolean b) {
    m_fxView.setMaximizable(b);
  }

  @Override
  public void setMinimizeEnabled(boolean b) {
    m_fxView.setIconifiable(b);
  }

  @Override
  public void setMaximized(boolean b) {
    m_fxView.setMaximized(b);
  }

  @Override
  public void setMinimized(boolean b) {
    m_fxView.setIconified(b);
  }

  @Override
  public void setIcon(Image image) {
    m_fxView.setIcon(image);
  }

  @Override
  public void addFxScoutViewListener(IFxScoutViewListener listener) {
    m_listenerList.add(IFxScoutViewListener.class, listener);
  }

  @Override
  public void removeFxScoutViewListener(IFxScoutViewListener listener) {
    m_listenerList.remove(IFxScoutViewListener.class, listener);
  }

  @Override
  public Pane getFxContentPane() {
    return (Pane) m_fxView.getRoot();
  }

  private void fireFxScoutViewEvent(FxScoutViewEvent e) {
    EventListener[] listeners = m_listenerList.getListeners(IFxScoutViewListener.class);
    if (listeners != null && listeners.length > 0) {
      for (int i = 0; i < listeners.length; i++) {
        try {
          ((IFxScoutViewListener) listeners[i]).viewChanged(e);
        }
        catch (Throwable t) {
          t.printStackTrace();
        }
      }
    }
  }

}
