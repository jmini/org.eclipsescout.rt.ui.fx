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
package org.eclipsescout.rt.ui.fx.form.fields.groupbox;

import java.util.List;

import javafx.beans.binding.StringBinding;
import javafx.beans.binding.When;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.IGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.tabbox.ITabBox;
import org.eclipsescout.rt.ui.fx.LogicalGridData;
import org.eclipsescout.rt.ui.fx.LogicalGridPane;
import org.eclipsescout.rt.ui.fx.form.fields.FxScoutFieldComposite;
import org.eclipsescout.rt.ui.fx.form.fields.FxScoutFormFieldGridData;
import org.eclipsescout.rt.ui.fx.form.fields.IFxScoutFormField;
import org.eclipsescout.rt.ui.fx.layout.HorizontalAlignment;
import org.eclipsescout.rt.ui.fx.layout.VerticalAlignment;

/**
 * A group box is a composite of the following structure: groupBox bodyPart
 * processButtonPart systemProcessButtonPart customProcessButtonPart
 */
public class FxScoutGroupBox extends FxScoutFieldComposite<IGroupBox> implements IFxScoutGroupBox {

  private final double TOP_MARGIN_BUTTON_BAR = 10;

  private Pane m_fxBodyPart;
  private Pane m_fxButtonBarPart;
  protected StringProperty containerLabel = new SimpleStringProperty(this, "containerLabel", "");
  protected String containerImage;
  protected HorizontalAlignment containerImageHAlign = HorizontalAlignment.LEFT;
  protected VerticalAlignment containerImageVAlign = VerticalAlignment.TOP;
  protected boolean containerBorderVisible;
  protected String containerBorderDecoration;

  @Override
  protected void initialize() {
    m_fxBodyPart = new LogicalGridPane(getFxEnvironment(), getFxEnvironment().getFormColumnGap(), getFxEnvironment().getFormRowGap());
    m_fxButtonBarPart = createButtonBarPart();
    // main panel: NORTH=sectionHeader, CENTER=bodyPanel, SOUTH=buttonPanel
    BorderPane fxBox = new BorderPane();
    if (getScoutObject().isScrollable()) {
      ScrollPane scrollPane = new ScrollPane(m_fxBodyPart);
      scrollPane.setBorder(null);
      fxBox.setCenter(scrollPane);
    }
    else {
      fxBox.setCenter(m_fxBodyPart);
    }
    BorderPane.setMargin(m_fxButtonBarPart, new Insets(TOP_MARGIN_BUTTON_BAR, 0, 0, 0));
    fxBox.setBottom(m_fxButtonBarPart);
    interceptBorderStyle(getScoutObject());
    if (isSection()) {
      TitledPane section = new TitledPane();
      section.setContent(fxBox);
      section.textProperty().bind(containerLabel);
      section.expandedProperty().addListener(new P_ExpansionListener());
      setFxField(section);
      setExpandableFromScout();
      setExpandedFromScout();
      setFxStatusPane(null);
      Pane sectionContainer = new LogicalGridPane(getFxEnvironment(), 0, 0);
      sectionContainer.getChildren().add(section);
      setFxContainer(sectionContainer);
    }
    else if (isLine()) {
      Label title = new Label();
      StringBinding s = new When(containerLabel.isEmpty()).then("").otherwise(new SimpleStringProperty(" ").concat(containerLabel.concat(" ")));
      title.textProperty().bind(s);
      title.getStyleClass().add("bordered-group-box-title");
      StackPane.setAlignment(title, Pos.TOP_LEFT);

      StackPane contentPane = new StackPane();
      contentPane.getStyleClass().add("bordered-group-box-content");
      contentPane.getChildren().add(fxBox);

      StackPane borderedStackPane = new StackPane(title, contentPane);
      borderedStackPane.getStyleClass().add("bordered-group-box-border");

      setFxField(borderedStackPane);
      setFxStatusPane(null);
      setFxContainer(borderedStackPane);
    }
    else {
      setFxField(fxBox);
      setFxStatusPane(null);
      setFxContainer(fxBox);
    }

    if (containerBorderVisible && getScoutObject().isMainBox()) {
      getFxContainer().getStyleClass().add("main-box");
    }

    // items without process buttons
    List<IFormField> scoutFields = getScoutObject().getControlFields();
    for (int i = 0; i < scoutFields.size(); i++) {
      // create item
      IFxScoutFormField fxScoutField = getFxEnvironment().createFormField(m_fxBodyPart, scoutFields.get(i));
      // create layout constraints
      FxScoutFormFieldGridData cons = new FxScoutFormFieldGridData(scoutFields.get(i));
      fxScoutField.getFxContainer().getProperties().put(LogicalGridData.CLIENT_PROPERTY_NAME, cons);
      m_fxBodyPart.getChildren().add(fxScoutField.getFxContainer());
    }
  }

  /**
   * set the values {@link #containerBorderVisible} and {@link #containerBorderDecoration}
   *
   * @param scoutObject
   */
  private void interceptBorderStyle(IGroupBox scoutObject) {
    containerBorderVisible = scoutObject.isBorderVisible();
    containerBorderDecoration = IGroupBox.BORDER_DECORATION_EMPTY;
    if (containerBorderVisible) {
      if (IGroupBox.BORDER_DECORATION_SECTION.equals(scoutObject.getBorderDecoration())) {
        containerBorderDecoration = IGroupBox.BORDER_DECORATION_SECTION;
      }
      else if (IGroupBox.BORDER_DECORATION_LINE.equals(scoutObject.getBorderDecoration())) {
        containerBorderDecoration = IGroupBox.BORDER_DECORATION_LINE;
      }
      else if (IGroupBox.BORDER_DECORATION_AUTO.equals(scoutObject.getBorderDecoration())) {
        // auto default cases
        if (scoutObject.isMainBox()) {
          containerBorderDecoration = IGroupBox.BORDER_DECORATION_EMPTY;
        }
        else if (scoutObject.isExpandable()) {
          containerBorderDecoration = IGroupBox.BORDER_DECORATION_SECTION;
        }
        else if (scoutObject.getParentField() instanceof ITabBox) {
          containerBorderDecoration = IGroupBox.BORDER_DECORATION_EMPTY;
        }
        else {
          containerBorderDecoration = IGroupBox.BORDER_DECORATION_LINE;
        }
      }
    }
  }

  /**
   * @return Container of FxScoutGrouptButtonBarPart
   */
  private Pane createButtonBarPart() {
    FxScoutGroupBoxButtonBar fxScoutGroupBoxButtonBar = new FxScoutGroupBoxButtonBar();
    fxScoutGroupBoxButtonBar.createField(getScoutObject(), getFxEnvironment());
    return fxScoutGroupBoxButtonBar.getFxContainer();
  }

  @Override
  public Pane getFxGroupBox() {
    return new Pane(getFxField());
  }

  @Override
  public Pane getFxBodyPart() {
    return m_fxBodyPart;
  }

  @Override
  public Pane getFxButtonBarPart() {
    return m_fxButtonBarPart;
  }

  @Override
  protected void attachScout() {
    super.attachScout();
    IGroupBox scoutGroupBox = getScoutObject();
    setBackgroundImageFromScout(scoutGroupBox.getBackgroundImageName());
    setBackgroundImageHorizontalAlignFromScout(scoutGroupBox.getBackgroundImageHorizontalAlignment());
    setBackgroundImageVerticalAlignFromScout(scoutGroupBox.getBackgroundImageVerticalAlignment());
    setExpandedFromScout();
    // ensure foreground color
    setEnabledFromScout(scoutGroupBox.isEnabled());
    changeContainerLabel();
  }

  protected boolean isSection() {
    return containerBorderVisible && IGroupBox.BORDER_DECORATION_SECTION.equals(containerBorderDecoration);
  }

  protected boolean isLine() {
    return containerBorderVisible && IGroupBox.BORDER_DECORATION_LINE.equals(containerBorderDecoration);
  }

  /**
   * scout settings
   */
  @Override
  protected void setEnabledFromScout(boolean b) {
    // deactivated
  }

  @Override
  protected void setLabelVisibleFromScout() {
    super.setLabelVisibleFromScout();
    changeContainerLabel();
  }

  // override to set outer border to line border
  @Override
  protected void setLabelFromScout(String s) {
    super.setLabelFromScout(s);
    changeContainerLabel();
  }

  protected void setExpandableFromScout() {
    if (getFxField() instanceof TitledPane) {
      TitledPane section = (TitledPane) getFxField();
      section.setCollapsible(getScoutObject().isExpandable());
    }
  }

  protected void setExpandedFromScout() {
    if (getScoutObject().isExpandable() && getFxField() instanceof TitledPane) {
      TitledPane section = (TitledPane) getFxField();
      section.setExpanded(getScoutObject().isExpanded());
    }
  }

  protected void setBackgroundImageFromScout(String imageName) {
    if (imageName != containerImage) {
      containerImage = imageName;

      if (getFxField() instanceof Region) {
        // TODO: background is only visible when there is no border, because it has conflicts with css definitions of the background (e.g. insets)
        if (containerImage != null) {
          ((Region) getFxField()).setBackground(new Background(new BackgroundImage(getFxEnvironment().getImage(containerImage), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, null)));
        }
        else {
          ((Region) getFxField()).setBackground(null);
        }
      }
    }
  }

  protected void setBackgroundImageHorizontalAlignFromScout(int halign) {
    containerImageHAlign = createHorizontalAlignment(halign);
    // TODO: implement background image adjustment
  }

  protected void setBackgroundImageVerticalAlignFromScout(int valign) {
    containerImageVAlign = createVerticalAlignment(valign);
    // TODO: implement background image adjustment
  }

  private HorizontalAlignment createHorizontalAlignment(int scoutAlign) {
    switch (scoutAlign) {
      case -1: {
        return HorizontalAlignment.LEFT;
      }
      case 0: {
        return HorizontalAlignment.CENTER;
      }
      case 1: {
        return HorizontalAlignment.RIGHT;
      }
      default: {
        return HorizontalAlignment.LEFT;
      }
    }
  }

  private VerticalAlignment createVerticalAlignment(int scoutAlign) {
    switch (scoutAlign) {
      case -1: {
        return VerticalAlignment.TOP;
      }
      case 0: {
        return VerticalAlignment.CENTER;
      }
      case 1: {
        return VerticalAlignment.BOTTOM;
      }
      default: {
        return VerticalAlignment.TOP;
      }
    }
  }

  protected void changeContainerLabel() {
    String s = getScoutObject().isLabelVisible() ? getScoutObject().getLabel() : null;
    s = StringUtility.emptyIfNull(s);
    if (!s.equals(containerLabel.getValue())) {
      containerLabel.setValue(s);
    }
  }

  /**
   * scout property observer
   */
  @Override
  protected void handleScoutPropertyChange(String name, Object newValue) {
    super.handleScoutPropertyChange(name, newValue);
    IGroupBox gb = getScoutObject();
    if (name.equals(IGroupBox.PROP_EXPANDED)) {
      setExpandedFromScout();
    }
    else if (name.equals(IGroupBox.PROP_BACKGROUND_IMAGE_NAME)) {
      setBackgroundImageFromScout(gb.getBackgroundImageName());
    }
    else if (name.equals(IGroupBox.PROP_BACKGROUND_IMAGE_HORIZONTAL_ALIGNMENT)) {
      setBackgroundImageHorizontalAlignFromScout(gb.getBackgroundImageHorizontalAlignment());
    }
    else if (name.equals(IGroupBox.PROP_BACKGROUND_IMAGE_VERTICAL_ALIGNMENT)) {
      setBackgroundImageVerticalAlignFromScout(gb.getBackgroundImageVerticalAlignment());
    }
    else if (name.equals(IGroupBox.PROP_EXPANDABLE)) {
      setExpandableFromScout();
    }
  }

  protected void handleFxGroupBoxExpanded(final boolean expanded) {
    if (getUpdateFxFromScoutLock().isAcquired()) {
      return;
    }
    //
    //notify Scout
    Runnable t = new Runnable() {
      @Override
      public void run() {
        getScoutObject().getUIFacade().setExpandedFromUI(expanded);
      }
    };
    getFxEnvironment().invokeScoutLater(t, 0);
    //end notify
  }

  private class P_ExpansionListener implements ChangeListener<Boolean> {

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
      handleFxGroupBoxExpanded(newValue);
    }

  }

}
