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
package org.eclipsescout.rt.ui.fx.window.messagebox;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import org.eclipse.scout.rt.client.ui.messagebox.IMessageBox;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxEvent;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxListener;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.rt.ui.fx.basic.FxScoutComposite;
import org.eclipsescout.rt.ui.fx.ext.LabelEx;
import org.eclipsescout.rt.ui.fx.layout.FlowPaneEx;
import org.eclipsescout.rt.ui.fx.layout.HorizontalAlignment;

/**
 *
 */
public class FxScoutMessageBox extends FxScoutComposite<IMessageBox> implements IFxScoutMessageBox {

  private static final int MAX_WIDTH = 800;
  private static final int MAX_HEIGHT = 600;

  private static final String STYLE_CLASS_MESSAGE_BOX = "message-box";
  private static final String STYLE_CLASS_INTRO_TEXT_PANE = "intro-text-pane";
  private static final String STYLE_CLASS_ACTION_TEXT_PANE = "action-text-pane";
  private static final String STYLE_CLASS_BUTTON_PANE = "button-pane";
  private static final String STYLE_CLASS_YES_BUTTON = "yes-button";
  private static final String STYLE_CLASS_NO_BUTTON = "no-button";
  private static final String STYLE_CLASS_CANCEL_BUTTON = "cancel-button";
  private static final String STYLE_CLASS_COPY_BUTTON = "copy-button";

  private P_ScoutMessageBoxListener m_scoutMessageBoxListener;

  private Window m_fxParent;
  private Stage m_fxStage;

  private Button m_fxButtonYes;
  private Button m_fxButtonNo;
  private Button m_fxButtonCancel;
  private Button m_fxButtonCopy;

  /**
   *
   */
  public FxScoutMessageBox(Window fxParent) {
    super();
    m_fxParent = fxParent;
  }

  @Override
  protected void initialize() {
    m_fxStage = new Stage();
    getFxEnvironment().getCSSURLsFromExtensionPoint();
    m_fxStage.initStyle(StageStyle.UTILITY);
    m_fxStage.initModality(Modality.APPLICATION_MODAL);
    m_fxStage.initOwner(m_fxParent);
    m_fxStage.setOnHidden(new P_FxStageHiddenEventHandler());

    m_fxStage.setTitle(getScoutObject().getTitle());

    BorderPane contentPane = new BorderPane();
    contentPane.getStyleClass().add(STYLE_CLASS_MESSAGE_BOX);

    if (getScoutObject().getIntroText() != null) {
      FlowPaneEx labelPane = new FlowPaneEx(HorizontalAlignment.LEFT);
      labelPane.getStyleClass().add(STYLE_CLASS_INTRO_TEXT_PANE);
      LabelEx label = new LabelEx();
      label.setText(getScoutObject().getIntroText());
      labelPane.getChildren().add(label);
      contentPane.setTop(labelPane);
    }
    if (getScoutObject().getActionText() != null) {
      FlowPaneEx labelPane = new FlowPaneEx(HorizontalAlignment.LEFT);
      labelPane.getStyleClass().add(STYLE_CLASS_ACTION_TEXT_PANE);
      LabelEx label = new LabelEx();
      label.setText(getScoutObject().getActionText());
      labelPane.getChildren().add(label);
      contentPane.setCenter(labelPane);
    }

    FlowPaneEx buttonPane = new FlowPaneEx(HorizontalAlignment.RIGHT);
    buttonPane.getStyleClass().add(STYLE_CLASS_BUTTON_PANE);
    if (getScoutObject().getYesButtonText() != null) {
      m_fxButtonYes = createButton(getScoutObject().getYesButtonText());
      m_fxButtonYes.getStyleClass().add(STYLE_CLASS_YES_BUTTON);
      buttonPane.getChildren().add(m_fxButtonYes);
    }
    if (getScoutObject().getNoButtonText() != null) {
      m_fxButtonNo = createButton(getScoutObject().getNoButtonText());
      m_fxButtonNo.getStyleClass().add(STYLE_CLASS_NO_BUTTON);
      buttonPane.getChildren().add(m_fxButtonNo);
    }
    if (getScoutObject().getCancelButtonText() != null) {
      m_fxButtonCancel = createButton(getScoutObject().getCancelButtonText());
      m_fxButtonCancel.getStyleClass().add(STYLE_CLASS_CANCEL_BUTTON);
      buttonPane.getChildren().add(m_fxButtonCancel);
    }
    if (getScoutObject().getHiddenText() != null) {
      m_fxButtonCopy = createButton(TEXTS.get("Copy"));
      m_fxButtonCopy.getStyleClass().add(STYLE_CLASS_COPY_BUTTON);
      buttonPane.getChildren().add(m_fxButtonCopy);
    }
    contentPane.setBottom(buttonPane);

    // set default button
    if (m_fxButtonYes != null) {
      m_fxButtonYes.setDefaultButton(true);
    }
    else if (m_fxButtonNo != null) {
      m_fxButtonNo.setDefaultButton(true);
    }
    else if (m_fxButtonCancel != null) {
      m_fxButtonCancel.setDefaultButton(true);
    }
    // set cancel button
    if (m_fxButtonCancel != null) {
      m_fxButtonCancel.setCancelButton(true);
    }
    else if (m_fxButtonNo != null) {
      m_fxButtonNo.setCancelButton(true);
    }
    else if (m_fxButtonYes != null) {
      m_fxButtonYes.setCancelButton(true);
    }

    Scene scene = new Scene(contentPane);
    scene.getStylesheets().addAll(getFxEnvironment().getCSSURLsFromExtensionPoint());
    m_fxStage.setScene(scene);
    m_fxStage.sizeToScene();
    m_fxStage.setMaxHeight(MAX_HEIGHT);
    m_fxStage.setMaxWidth(MAX_WIDTH);

    KeyCodeCombination kcc = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
    m_fxStage.getScene().getAccelerators().put(kcc, new Runnable() {

      @Override
      public void run() {
        handleCopyAction();
      }
    });
  }

  @Override
  protected void attachScout() {
    super.attachScout();
    if (m_scoutMessageBoxListener == null) {
      m_scoutMessageBoxListener = new P_ScoutMessageBoxListener();
      getScoutObject().addMessageBoxListener(m_scoutMessageBoxListener);
    }
  }

  @Override
  protected void detachScout() {
    super.detachScout();
    if (m_scoutMessageBoxListener != null) {
      getScoutObject().removeMessageBoxListener(m_scoutMessageBoxListener);
      m_scoutMessageBoxListener = null;
    }
  }

  @Override
  public Stage getFxStage() {
    return m_fxStage;
  }

  @Override
  public void showFxMessageBox() {
    m_fxStage.show();
  }

  private Button createButton(String text) {
    final Button b = new Button(text.replace('&', '_'));
    b.setMnemonicParsing(true);
    b.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        handleFxButtonAction(b);
      }
    });
    return b;
  }

  private void handleCopyAction() {
    // copy message to clipboard
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(getScoutObject().getHiddenText());
    clipboard.setContent(content);
  }

  /**
   * @param b
   */
  protected void handleFxButtonAction(Button b) {
    if (b == m_fxButtonCopy) {
      handleCopyAction();
    }
    else {
      int resultOption = -1;
      if (b == m_fxButtonYes) {
        resultOption = IMessageBox.YES_OPTION;
      }
      else if (b == m_fxButtonNo) {
        resultOption = IMessageBox.NO_OPTION;
      }
      else if (b == m_fxButtonCancel) {
        resultOption = IMessageBox.CANCEL_OPTION;
      }
      if (resultOption != -1) {
        final int fOption = resultOption;
        Runnable t = new Runnable() {
          @Override
          public void run() {
            getScoutObject().getUIFacade().setResultFromUI(fOption);
          }
        };

        getFxEnvironment().invokeScoutLater(t, 0);
      }
    }
  }

  protected void handleScoutMessageBoxClosed(MessageBoxEvent e) {
    disconnectFromScout();
    m_fxStage.close();
  }

  protected void handleFxWindowHidden(WindowEvent e) {
    // notify Scout
    Runnable t = new Runnable() {
      @Override
      public void run() {
        getScoutObject().getUIFacade().setResultFromUI(IMessageBox.CANCEL_OPTION);
      }
    };

    getFxEnvironment().invokeScoutLater(t, 0);
  }

  private class P_ScoutMessageBoxListener implements MessageBoxListener {
    @Override
    public void messageBoxChanged(final MessageBoxEvent e) {
      switch (e.getType()) {
        case MessageBoxEvent.TYPE_CLOSED: {
          Runnable t = new Runnable() {
            @Override
            public void run() {
              switch (e.getType()) {
                case MessageBoxEvent.TYPE_CLOSED: {
                  handleScoutMessageBoxClosed(e);
                  break;
                }
                default:
                  break;
              }
            }
          };
          getFxEnvironment().invokeFxLater(t);
          break;
        }
        default:
          break;
      }
    }
  }

  private class P_FxStageHiddenEventHandler implements EventHandler<WindowEvent> {

    @Override
    public void handle(WindowEvent event) {
      handleFxWindowHidden(event);
    }

  }

}
