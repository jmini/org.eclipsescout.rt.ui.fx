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
package org.eclipsescout.rt.ui.fx.form.fields.button;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;

import org.eclipse.scout.commons.WeakEventListener;
import org.eclipse.scout.rt.client.ui.form.fields.button.ButtonEvent;
import org.eclipse.scout.rt.client.ui.form.fields.button.ButtonListener;
import org.eclipse.scout.rt.client.ui.form.fields.button.IButton;
import org.eclipsescout.rt.ui.fx.LogicalGridPane;
import org.eclipsescout.rt.ui.fx.form.fields.FxScoutFieldComposite;

// TODO: implement completely
/**
 *
 */
public class FxScoutButton extends FxScoutFieldComposite<IButton> implements IFxScoutButton {

  private ButtonListener m_scoutButtonListener;
  private boolean m_handleActionPending;

  @Override
  protected void initialize() {
    ButtonBase button;
    switch (getScoutObject().getDisplayStyle()) {
      case IButton.DISPLAY_STYLE_TOGGLE: {
        ToggleButton t = new ToggleButton();
        t.selectedProperty().addListener(new P_ToggleButtonListener());
        button = t;
        break;
      }
      case IButton.DISPLAY_STYLE_RADIO: {
        RadioButton r = new RadioButton();
        r.selectedProperty().addListener(new P_ToggleButtonListener());
        button = r;
        break;
      }
      case IButton.DISPLAY_STYLE_LINK: {
        button = new Hyperlink();
        break;
      }
      default: {
        Button b = new Button();
        b.setDefaultButton(getScoutObject().getSystemType() == IButton.SYSTEM_TYPE_OK);
        b.setCancelButton(getScoutObject().getSystemType() == IButton.SYSTEM_TYPE_CANCEL);
        button = b;
        break;
      }
    }

    button.setOnAction(new P_FxActionListener());

    setFxStatusPane(null);
    setFxField(button);

    LogicalGridPane container = new LogicalGridPane(getFxEnvironment(), 0, 0);
    container.getChildren().add(button);

    setFxContainer(container);
  }

  @Override
  public ButtonBase getFxButton() {
    return (ButtonBase) getFxField();
  }

  @Override
  protected void setLabelFromScout(String s) {
    String label = s.replace("&", "_");
    getFxButton().setMnemonicParsing(true);
    getFxButton().setText(label);
  }

  @Override
  protected void attachScout() {
    super.attachScout();
    if (m_scoutButtonListener == null) {
      m_scoutButtonListener = new P_ScoutButtonListener();
      getScoutObject().addButtonListener(m_scoutButtonListener);
    }
    IButton b = getScoutObject();
    setSelectionFromScout(b.isSelected());
  }

  @Override
  protected void detachScout() {
    super.detachScout();
    if (m_scoutButtonListener != null) {
      getScoutObject().removeButtonListener(m_scoutButtonListener);
      m_scoutButtonListener = null;
    }
  }

  @Override
  protected void handleScoutPropertyChange(String name, Object newValue) {
    super.handleScoutPropertyChange(name, newValue);
    if (name.equals(IButton.PROP_SELECTED)) {
      setSelectionFromScout(((Boolean) newValue).booleanValue());
    }
  }

  /**
   * @param selected
   */
  protected void setSelectionFromScout(boolean selected) {
    if (getFxButton() instanceof ToggleButton) {
      ((ToggleButton) getFxButton()).setSelected(selected);
    }
  }

  /**
   *
   */
  protected void handleFxAction() {
    if (!m_handleActionPending) {
      m_handleActionPending = true;
      //notify Scout
      Runnable t = new Runnable() {
        @Override
        public void run() {
          try {
            getScoutObject().getUIFacade().fireButtonClickedFromUI();
          }
          finally {
            m_handleActionPending = false;
          }
        }
      };
      getFxEnvironment().invokeScoutLater(t, 0);
      //end notify
    }
  }

  protected void disarmButtonFromScout() {
    getFxButton().disarm();
  }

  protected void setSelectionFromFx(final boolean b) {
    if (getUpdateFxFromScoutLock().isAcquired()) {
      return;
    }
    //
    if (getScoutObject().isSelected() != b) {
      // radio button behavior since swing fires deselections
      if (getFxButton() instanceof RadioButton && !b) {
        // avoid deselection
        ((RadioButton) getFxButton()).setSelected(getScoutObject().isSelected());
      }
      else {
        // notify Scout
        Runnable t = new Runnable() {
          @Override
          public void run() {
            getScoutObject().getUIFacade().setSelectedFromUI(b);
          }
        };

        getFxEnvironment().invokeScoutLater(t, 0);
        // end notify
      }
    }
  }

  private class P_ToggleButtonListener implements InvalidationListener {

    @Override
    public void invalidated(Observable observable) {
      setSelectionFromFx(((ToggleButton) getFxButton()).isSelected());
    }

  }

  private class P_FxActionListener implements EventHandler<ActionEvent> {

    @Override
    public void handle(ActionEvent event) {
      handleFxAction();
    }

  }

  private class P_ScoutButtonListener implements ButtonListener, WeakEventListener {

    @Override
    public void buttonChanged(ButtonEvent e) {
      if (isIgnoredScoutEvent(ButtonEvent.class, "" + e.getType())) {
        return;
      }
      //
      switch (e.getType()) {
        case ButtonEvent.TYPE_DISARM: {
          getFxEnvironment().invokeFxLater(
              new Runnable() {
                @Override
                public void run() {
                  try {
                    getUpdateFxFromScoutLock().acquire();
                    //
                    disarmButtonFromScout();
                  }
                  finally {
                    getUpdateFxFromScoutLock().release();
                  }
                }
              });
          break;
        }
        case ButtonEvent.TYPE_REQUEST_POPUP: {
          getFxEnvironment().invokeFxLater(
              new Runnable() {
                @Override
                public void run() {
                  try {
                    getUpdateFxFromScoutLock().acquire();
                    //
                    //TODO implement request popup
//                    requestPopupFromScout();
                  }
                  finally {
                    getUpdateFxFromScoutLock().release();
                  }
                }
              });
          break;
        }
      }
    }

  }

}
