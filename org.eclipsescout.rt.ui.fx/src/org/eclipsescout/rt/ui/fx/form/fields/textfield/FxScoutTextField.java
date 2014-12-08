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
package org.eclipsescout.rt.ui.fx.form.fields.textfield;

import java.beans.PropertyChangeEvent;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import org.eclipse.scout.commons.CompareUtility;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.job.JobEx;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.IStringField;
import org.eclipsescout.rt.ui.fx.LogicalGridPane;
import org.eclipsescout.rt.ui.fx.ext.FxStatusPaneEx;
import org.eclipsescout.rt.ui.fx.ext.TextFieldEx;
import org.eclipsescout.rt.ui.fx.form.fields.FxScoutValueFieldComposite;

/**
 *
 */
public class FxScoutTextField extends FxScoutValueFieldComposite<IStringField> implements IFxScoutTextField {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(FxScoutTextField.class);

  private boolean m_initialized;
  private boolean m_decorationLink;
  private boolean m_validateOnAnyKey;
  private EventHandler<MouseEvent> m_linkTrigger;
  private P_TextChangedListener m_textChangedTrigger;
  private P_KeyTextChangedHandler m_keyTextChangedHandler;

  private boolean m_upperCase;
  private boolean m_lowerCase;
  private boolean m_multilineText;

  @Override
  protected void initialize() {
    LogicalGridPane pane = new LogicalGridPane(getFxEnvironment(), 1, 0);

    FxStatusPaneEx label = getFxEnvironment().createStatusLabel(getScoutObject());
    pane.getChildren().add(label);

    TextFieldEx textField = new TextFieldEx();
    pane.getChildren().add(textField);

    setFxContainer(pane);
    setFxStatusPane(label);
    setFxField(textField);
  }

  @Override
  public void setFxField(Node fxField) {
    super.setFxField(fxField);
    if (fxField instanceof TextField) {
      TextField textField = (TextField) fxField;
      textField.caretPositionProperty().addListener(new P_FxCaretListener());

      m_textChangedTrigger = new P_TextChangedListener();
      m_keyTextChangedHandler = new P_KeyTextChangedHandler();
      textField.textProperty().addListener(m_textChangedTrigger);
      textField.addEventFilter(KeyEvent.KEY_TYPED, m_keyTextChangedHandler);
      textField.setText(getScoutObject().getDisplayText());
    }
  }

  @Override
  public TextFieldEx getFxTextField() {
    return (TextFieldEx) getFxField();
  }

  @Override
  protected void attachScout() {
    IStringField f = getScoutObject();
    setDecorationLinkFromScout(f.isDecorationLink());
    setFormatFromScout(f.getFormat());
    setMaxLengthFromScout(f.getMaxLength());
    setValidateOnAnyKeyFromScout(f.isValidateOnAnyKey());
    setMultilineTextFromScout(f.isMultilineText());

    super.attachScout();

    setSelectionFromScout();
  }

  @Override
  protected boolean handleFxInputVerifier() {
    final String text = getFxTextField().getText();
    if (CompareUtility.equals(text, getScoutObject().getDisplayText()) && getScoutObject().getErrorStatus() != null) {
      return true;
    }
    Runnable r = new Runnable() {

      @Override
      public void run() {
        getScoutObject().getUIFacade().setTextFromUI(text, false);
      }

    };
    JobEx job = getFxEnvironment().invokeScoutLater(r, 0);
    try {
      job.join(2345);
    }
    catch (InterruptedException e) {
      // nop
    }
    setInputDirty(false);
    getFxEnvironment().dispatchImmediateFxJobs();
    if (!m_validateOnAnyKey) {
      setSelectionFromFx();
    }
    return true;
  }

  @Override
  protected void handleScoutPropertyChange(String name, Object newValue) {
    super.handleScoutPropertyChange(name, newValue);
    if (name.equals(IStringField.PROP_DECORATION_LINK)) {
      setDecorationLinkFromScout(((Boolean) newValue).booleanValue());
    }
    else if (name.equals(IStringField.PROP_MAX_LENGTH)) {
      setMaxLengthFromScout(((Number) newValue).intValue());
    }
    else if (name.equals(IStringField.PROP_INSERT_TEXT)) {
      setDoInsertFromScout((String) newValue);
    }
    else if (name.equals(IStringField.PROP_VALIDATE_ON_ANY_KEY)) {
      setValidateOnAnyKeyFromScout(((Boolean) newValue).booleanValue());
    }
    else if (name.equals(IStringField.PROP_SELECTION_START)) {
      setSelectionFromScout();
    }
    else if (name.equals(IStringField.PROP_SELECTION_END)) {
      setSelectionFromScout();
    }
    else if (name.equals(IStringField.PROP_MULTILINE_TEXT)) {
      setMultilineTextFromScout(((Boolean) newValue).booleanValue());
    }
  }

  /**
   * Decorates the textfield with a link if the parameter is true.
   * Sets the preferred cursor-style on the textfield and registers a trigger
   * for the link.
   *
   * @param b
   *          whether or not the textfield contains a link
   */
  protected void setDecorationLinkFromScout(boolean b) {
    if (m_decorationLink != b) {
      m_decorationLink = b;
      TextFieldEx textField = getFxTextField();
      if (b) {
        m_linkTrigger = new P_FxLinkTrigger();
        textField.setCursor(Cursor.HAND);
      }
      else {
        m_linkTrigger = null;
        textField.setCursor(Cursor.DEFAULT);
      }
      textField.setOnMouseClicked(m_linkTrigger);
      setForegroundFromScout(getScoutObject().getForegroundColor());
    }
  }

  /**
   * Sets the format (upper or lower) of the text within the textfield.
   *
   * @param s
   *          format to be set
   */
  protected void setFormatFromScout(String s) {
    m_upperCase = false;
    m_lowerCase = false;
    if (IStringField.FORMAT_UPPER.equals(s)) {
      m_upperCase = true;
    }
    else if (IStringField.FORMAT_LOWER.equals(s)) {
      m_lowerCase = true;
    }
  }

  /**
   * Sets the maximum allowed length of the text within the textfield.
   *
   * @param n
   *          number of characters to be set as maximum length
   */
  protected void setMaxLengthFromScout(int n) {
    m_textChangedTrigger.setMaxLength(n);
    m_keyTextChangedHandler.setMaxLength(n);
  }

  /**
   * Inserts the string into the current selection or replaces it.
   *
   * @param s
   *          string to insert
   */
  protected void setDoInsertFromScout(String s) {
    getFxTextField().replaceSelection(s);
  }

  /**
   * @param b
   *          wheter or not the content of the textfield should be validated on any key
   */
  protected void setValidateOnAnyKeyFromScout(boolean b) {
    m_validateOnAnyKey = b;
  }

  /**
   * Reads the needed informations from the scout object
   * and sets the anchor and the caret position according to these informations.
   */
  protected void setSelectionFromScout() {
    TextFieldEx textField = getFxTextField();
    int startIndex = getScoutObject().getSelectionStart();
    int endIndex = getScoutObject().getSelectionEnd();
    int textLength = textField.getText().length();
    if (startIndex < 0) {
      startIndex = textField.getAnchor();
    }
    if (endIndex < 0) {
      endIndex = textField.getCaretPosition();
    }
    startIndex = Math.min(Math.max(startIndex, -1), textLength);
    endIndex = Math.min(Math.max(endIndex, 0), textLength);
    if (textField.getAnchor() != startIndex || textField.getCaretPosition() != endIndex) {
      textField.selectRange(startIndex, endIndex);
    }
  }

  /**
   * Gets the anchor and the caret position from the textfield,
   * calculates the start and end index of the selection
   * and sets this selection on the scout object.
   */
  protected void setSelectionFromFx() {
    TextFieldEx textField = getFxTextField();
    int anchor = textField.getAnchor();
    int caret = textField.getCaretPosition();
    int textLen = textField.getText().length();
    final int startIndex = Math.min(Math.max(anchor, -1), textLen);
    final int endIndex = Math.min(Math.max(caret, 0), textLen);
    Runnable t = new Runnable() {
      @Override
      public void run() {
        try {
          addIgnoredScoutEvent(PropertyChangeEvent.class, IStringField.PROP_SELECTION_START);
          addIgnoredScoutEvent(PropertyChangeEvent.class, IStringField.PROP_SELECTION_END);
          //
          getScoutObject().getUIFacade().setSelectionFromUI(startIndex, endIndex);
        }
        finally {
          removeIgnoredScoutEvent(PropertyChangeEvent.class, IStringField.PROP_SELECTION_START);
          removeIgnoredScoutEvent(PropertyChangeEvent.class, IStringField.PROP_SELECTION_END);
        }
      }
    };
    getFxEnvironment().invokeScoutLater(t, 0);
  }

  /**
   * Sets the text on the textfield
   * and updates the selection through the caret and anchor positions.
   *
   * @param s
   *          displayed text to be set on the textfield
   */
  @Override
  protected void setDisplayTextFromScout(String s) {
    if (m_validateOnAnyKey && getFxContainer().isFocused()) {
      return;
    }
    TextFieldEx textField = getFxTextField();
    String oldText = textField.getText();
    if (s == null) {
      s = "";
    }
    if (oldText == null) {
      oldText = "";
    }
    if (oldText.equals(s)) {
      return;
    }
    int startIndex = -1;
    int endIndex = -1;
    ReadOnlyIntegerProperty caret = textField.caretPositionProperty();
    ReadOnlyIntegerProperty anchor = textField.anchorProperty();
    if (caret != null) {
      startIndex = caret.get();
    }
    if (anchor != null) {
      endIndex = anchor.get();
    }
    textField.setText(s);
    int textLength = textField.getText().length();
    if (caret != null && anchor != null) {
      startIndex = Math.min(Math.max(startIndex, -1), textLength);
      endIndex = Math.min(Math.max(endIndex, 0), textLength);
      textField.selectRange(endIndex, startIndex);
    }
  }

  @Override
  protected void setForegroundFromScout(String scoutColor) {
    if (scoutColor == null && m_decorationLink) {
      scoutColor = "#445599";
    }
    TextFieldEx textField = getFxTextField();
    if (textField != null) {
      String disColor = getDisabledColor(scoutColor);
      textField.setEnabledColor(scoutColor);
      textField.setDisabledColor(disColor);
      if (textField.isDisabled()) {
        textField.setTextColor(disColor);
      }
      else {
        textField.setTextColor(scoutColor);
      }
    }
  }

  /**
   * @param multilineText
   *          wheter or not new lines in the text should be allowed or not
   */
  protected void setMultilineTextFromScout(boolean multilineText) {
    m_multilineText = multilineText;
  }

  /**
   * This private class is meant to be installed on the textfield if the text contains a link
   * so that it can handle clicks on this link.
   */
  private class P_FxLinkTrigger implements EventHandler<MouseEvent> {

    @Override
    public void handle(MouseEvent evt) {
      if (evt.getClickCount() == 2) {
        handleFxLinkTrigger();
      }
    }
  }// end private class

  /**
   * Called if the link in the textfield have been double clicked.
   * It fires a LinkAction from the ui to the scout framework.
   */
  protected void handleFxLinkTrigger() {
    final String text = getFxTextField().getText();
    Runnable r = new Runnable() {
      @Override
      public void run() {
        getScoutObject().getUIFacade().fireLinkActionFromUI(text);
      }
    };
    getFxEnvironment().invokeScoutLater(r, 0);
  }

  /**
   * Ensures that a given string has the configured text format.
   *
   * @param s
   *          string which should be manipulated
   * @return string with the configured text format
   */
  private String ensureConfiguredTextFormat(String s) {
    if (s == null) {
      s = "";
    }

    if (m_upperCase) {
      s = s.toUpperCase();
    }
    else if (m_lowerCase) {
      s = s.toLowerCase();
    }
    if (!m_multilineText) {
      // omit leading and trailing newlines
      s = StringUtility.trimNewLines(s);
      // replace newlines by spaces
      s = s.replaceAll("\r\n", " ").replaceAll("[\r\n]", " ");
    }
    return s;
  }

  /**
   * This private class ensures that, when the text on the textfield changes,
   * the maximum length of the text is not exceeded. If needed a verification request
   * will be sent to the scout framework.
   */
  private class P_TextChangedListener implements ChangeListener<String> {
    int m_maxLength;

    public void setMaxLength(int n) {
      m_maxLength = n;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      newValue = ensureConfiguredTextFormat(newValue);
      int cut = m_maxLength > newValue.length() ? newValue.length() : m_maxLength;
      getFxTextField().setText(newValue.substring(0, cut));
      setInputDirty(true);
      if (m_validateOnAnyKey) {
        sendVerifyToScoutAndIgnoreResponses();
      }
    }

    /**
     * Transfers the text from the ui to the scout model,
     * as this class will be likely to change the text itself,
     * so the consistency is being retained.
     */
    private void sendVerifyToScoutAndIgnoreResponses() {
      final String text = getFxTextField().getText();
      Runnable r = new Runnable() {
        @Override
        public void run() {
          getScoutObject().getUIFacade().setTextFromUI(text, true);
        }
      };
      getFxEnvironment().invokeScoutLater(r, 0);
    }
  }

  /**
   * Private class that implements a workaround for reverting an input on the textfield
   * by the key combination ctrl + z. For this purpose it implements an EventHandler with
   * KeyEvent as the generic type.
   */
  private class P_KeyTextChangedHandler implements EventHandler<KeyEvent> {
    int m_maxLength;

    public void setMaxLength(int n) {
      m_maxLength = n;
    }

    @Override
    public void handle(KeyEvent event) {
      TextField fld = (TextField) event.getSource();
      // workaround: ctrl + z on a textfield with limited characters led to an IndexOutOfBoundsException
      if (fld.getText().length() >= m_maxLength && fld.getSelectedText().length() == 0) {
        event.consume();
      }
    }
  } // end private class

  /**
   * Private class that should listen to selections made by the user
   * and set those selection in the scout model.
   */
  private class P_FxCaretListener implements ChangeListener<Object> {

    @Override
    public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
      if (m_validateOnAnyKey) {
        setSelectionFromFx();
      }
    }

  } // end private class

  //TODO implement Drag and Drop

}
