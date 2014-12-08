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
package org.eclipsescout.rt.ui.fx.control.internal;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import org.eclipsescout.rt.ui.fx.FxUtility;
import org.eclipsescout.rt.ui.fx.control.InternalWindow;
import org.eclipsescout.rt.ui.fx.control.event.InternalWindowEvent;

/**
 * Skin for an internal window, which includes a title bar.
 * When the internal window is iconified it will be represented as taskbar button otherwise as normal window.
 */
public class InternalWindowSkin extends SkinBase<InternalWindow> {

  // css style classes
  public static final String DEFAULT_STYLE_CLASS_TITLE_BAR = "title-bar";
  public static final String DEFAULT_STYLE_CLASS_ICON = "icon";
  public static final String DEFAULT_STYLE_CLASS_TITLE = "title";
  public static final String DEFAULT_STYLE_CLASS_BUTTON = "window-button";
  public static final String DEFAULT_STYLE_CLASS_ICONIFY_BUTTON = "iconify";
  public static final String DEFAULT_STYLE_CLASS_RESTORE_BUTTON = "restore";
  public static final String DEFAULT_STYLE_CLASS_MAXIMIZE_BUTTON = "maximize";
  public static final String DEFAULT_STYLE_CLASS_CLOSE_BUTTON = "close";
  public static final String DEFAULT_STYLE_CLASS_TASKBAR_BUTTON = "taskbar-button";
  public static final String DEFAULT_STYLE_CLASS_ROOT = "root";

  // size constants
  private static final int TITLE_BAR_HEIGHT = 30;
  private static final int ICON_SIZE = 20;
  private static final int MARGIN_LEFT_RIGHT = 10;
  private static final int MARGIN_TOP_BOTTOM = (TITLE_BAR_HEIGHT - ICON_SIZE) / 2;
  private static final int GAP = 10;
  private static final double DEFAULT_HEIGHT_CONTENT = 30;

  /**
   * The title bar of the internal window.
   */
  private final TitleBar titleBar;

  /**
   * button to use for the taskbar of the desktop
   */
  private final Button taskbarButton;

  /**
   * Focus owner listener on the current scene to set the activated property in the internal window. The internal window
   * is activated when the focus owner is a child of the internal window.
   */
  private final ChangeListener<Node> focusOwnerChangeListener = new ChangeListener<Node>() {
    @Override
    public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
      getSkinnable().setActivated(FxUtility.isParent(newValue, getSkinnable()));
    }
  };

  /* *************************************************************
   * Constructors
   * ************************************************************/

  /**
   * Constructor for all InternalWindowSkin instances.
   *
   * @param internalWindow
   *          The {@code InternalWindow} for which this Skin should attach to.
   */
  public InternalWindowSkin(InternalWindow internalWindow) {
    super(internalWindow);

    titleBar = new TitleBar();
    taskbarButton = createTaskbarButton();

    init();
  }

  /* *************************************************************
   * Methods
   * ************************************************************/

  /**
   * Adds listeners to the internal window properties
   */
  private void init() {
    getSkinnable().sceneProperty().addListener(new ChangeListener<Scene>() {
      @Override
      public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        // remove the focus owner change listener from the old scene
        if (oldValue != null) {
          oldValue.focusOwnerProperty().removeListener(focusOwnerChangeListener);
        }
        // add the focus owner change listener to the new scene
        if (newValue != null) {
          newValue.focusOwnerProperty().addListener(focusOwnerChangeListener);
        }
      }
    });
    getSkinnable().getScene().focusOwnerProperty().addListener(focusOwnerChangeListener);

    // request the focus when a mouse pressed event occurs on the internal window
    getSkinnable().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        if (!getSkinnable().activatedProperty().get()) {
          getSkinnable().requestFocus();
        }
      }
    });

    // add style class to root node
    getSkinnable().rootProperty().addListener(new ChangeListener<Node>() {
      @Override
      public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
        if (oldValue != null) {
          oldValue.getStyleClass().remove(DEFAULT_STYLE_CLASS_ROOT);
        }
        if (newValue != null) {
          newValue.getStyleClass().add(DEFAULT_STYLE_CLASS_ROOT);
        }
      }
    });
    if (getSkinnable().getRoot() != null) {
      getSkinnable().getRoot().getStyleClass().add(DEFAULT_STYLE_CLASS_ROOT);
    }

    getSkinnable().iconifiedProperty().addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable observable) {
        handleIconifiedChanged();
      }
    });
    handleIconifiedChanged();
  }

  /**
   * Creates and returns a taskbar button which represents this internal window on the taskbar.
   * This method will be called once when the internal window skin will be created.
   *
   * @return taskbar button
   */
  protected Button createTaskbarButton() {
    Button b = new Button();
    b.getStyleClass().add(DEFAULT_STYLE_CLASS_TASKBAR_BUTTON);
    ImageView iconView = new ImageView();
    iconView.imageProperty().bind(getSkinnable().iconProperty());
    b.setGraphic(iconView);
    b.textProperty().bind(getSkinnable().titleProperty());

    Tooltip tt = new Tooltip();
    tt.textProperty().bind(getSkinnable().titleProperty());
    b.setTooltip(tt);

    b.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        InternalWindowSkin.this.getSkinnable().setIconified(false);
      }
    });

    return b;
  }

  /**
   * Removes the root and title bar from the children and adds the taskbar button when the internal window is iconified.
   * Otherwise it removes the taskbar button and adds the root node and the title bar.
   */
  private void handleIconifiedChanged() {
    if (getSkinnable().isIconified()) {
      if (getSkinnable().getRoot() != null) {
        getChildren().remove(getSkinnable().getRoot());
      }
      getChildren().remove(titleBar);

      getChildren().add(taskbarButton);
    }
    else {
      getChildren().remove(taskbarButton);

      if (getSkinnable().getRoot() != null && !getChildren().contains(getSkinnable().getRoot())) {
        getChildren().add(getSkinnable().getRoot());
      }
      getChildren().add(titleBar);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
    if (getSkinnable().isIconified()) {
      layoutInArea(taskbarButton, contentX, contentY, contentWidth, contentHeight, -1, HPos.LEFT, VPos.TOP);
    }
    else {
      layoutInArea(titleBar, contentX, contentY, contentWidth, TITLE_BAR_HEIGHT, -1, HPos.LEFT, VPos.TOP);
      if (getSkinnable().getRoot() != null) {
        layoutInArea(getSkinnable().getRoot(), contentX, contentY + TITLE_BAR_HEIGHT, contentWidth, contentHeight - TITLE_BAR_HEIGHT, -1,
            HPos.LEFT, VPos.TOP);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    if (getSkinnable().isIconified()) {
      return leftInset + taskbarButton.minWidth(height) + rightInset;
    }
    else {
      double contentWidth = getSkinnable().getRoot() == null ? 0 : getSkinnable().getRoot().minWidth(height);
      return leftInset + Math.max(titleBar.minWidth(height), contentWidth) + rightInset;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    if (getSkinnable().isIconified()) {
      return leftInset + taskbarButton.prefWidth(height) + rightInset;
    }
    else {
      double contentWidth = getSkinnable().getRoot() == null ? 0 : getSkinnable().getRoot().prefWidth(height);
      return leftInset + Math.max(titleBar.prefWidth(height), contentWidth) + rightInset;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    return Double.MAX_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    if (getSkinnable().isIconified()) {
      return topInset + taskbarButton.minHeight(-1) + bottomInset;
    }
    else {
      double contentHeight = getSkinnable().getRoot() == null ? DEFAULT_HEIGHT_CONTENT : getSkinnable().getRoot().minHeight(-1);
      return topInset + titleBar.minHeight(-1) + contentHeight + bottomInset;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    if (getSkinnable().isIconified()) {
      return topInset + taskbarButton.prefWidth(-1) + bottomInset;
    }
    else {
      double contentHeight = getSkinnable().getRoot() == null ? DEFAULT_HEIGHT_CONTENT : getSkinnable().getRoot().prefHeight(-1);
      return topInset + titleBar.prefHeight(-1) + contentHeight + bottomInset;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return Double.MAX_VALUE;
  }

/* *************************************************************
 * inner class
 * ************************************************************/

  /**
   * Title bar of the internal window which consists of the following optional parts:
   * <ul>
   * <li>Icon</li>
   * <li>Title</li>
   * <li>Button to iconify the internal window</li>
   * <li>Button to maximize or restore the internal window</li>
   * <li>Button to close the internal window</li>
   * </ul>
   */
  private final class TitleBar extends Region {

    /**
     * Image view for the icon. The maximal height is {@link InternalWindowSkin#ICON_SIZE}
     */
    private final ImageView icon = new ImageView();

    private final Text title = new Text();
    private final Button iconifyButton = new Button();
    private final Button resizeButton = new Button();
    private final Button closeButton = new Button();

    /**
     * Event handler for the maximize button, which fires an
     * {@link InternalWindowEvent#INTERNAL_WINDOW_MAXIMIZE_REQUEST}
     */
    private final EventHandler<ActionEvent> maximizeButtonHandler = new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        getSkinnable().fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_MAXIMIZE_REQUEST));
      }
    };

    /**
     * Event handler for the restore button, which fires an {@link InternalWindowEvent#INTERNAL_WINDOW_RESTORE_REQUEST}
     */
    private final EventHandler<ActionEvent> restoreButtonHandler = new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        getSkinnable().fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_RESTORE_REQUEST));
      }
    };

    /* *************************************************************
     * Constructor
     * ************************************************************/

    /**
     * Creates a title bar
     */
    public TitleBar() {
      super();

      init();
    }

    /* *************************************************************
     * Methods
     * ************************************************************/

    private void init() {
      // title bar
      getStyleClass().setAll(DEFAULT_STYLE_CLASS_TITLE_BAR);

      // icon
      icon.getStyleClass().setAll(DEFAULT_STYLE_CLASS_ICON);
      icon.imageProperty().bind(getSkinnable().iconProperty());
      icon.setFitHeight(ICON_SIZE);
      icon.setPreserveRatio(true);
      getChildren().add(icon);

      // title
      title.getStyleClass().setAll(DEFAULT_STYLE_CLASS_TITLE);
      title.textProperty().bind(getSkinnable().titleProperty());
      title.setTextAlignment(TextAlignment.CENTER);
      title.setTextOrigin(VPos.CENTER);
      getChildren().add(title);

      // iconify button
      addStyleClass(iconifyButton, DEFAULT_STYLE_CLASS_ICONIFY_BUTTON);
      iconifyButton.disableProperty().bind(getSkinnable().iconifiableProperty().not());
      iconifyButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          getSkinnable().fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_ICONIFY_REQUEST));
        }
      });

      // resize button
      addStyleClass(resizeButton, DEFAULT_STYLE_CLASS_MAXIMIZE_BUTTON);
      resizeButton.disableProperty().bind(getSkinnable().maximizableProperty().not());
      resizeButton.setOnAction(maximizeButtonHandler);
      getSkinnable().maximizedProperty().addListener(new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
          handleMaximizedChanged();
        }
      });
      handleMaximizedChanged();

      // close button
      addStyleClass(closeButton, DEFAULT_STYLE_CLASS_CLOSE_BUTTON);
      closeButton.disableProperty().bind(getSkinnable().closableProperty().not());
      closeButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          getSkinnable().fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_CLOSE_REQUEST));
        }
      });

      // add all buttons
      getChildren().addAll(iconifyButton, resizeButton, closeButton);
    }

    /**
     * Removes all style classes and adds the specified string and {@link InternalWindowSkin#DEFAULT_STYLE_CLASS_BUTTON}
     * as style classes to the specified button.
     *
     * @param b
     *          The button to add the style classes
     * @param s
     *          The style class to add
     */
    private void addStyleClass(Button b, String s) {
      b.getStyleClass().clear();
      b.getStyleClass().addAll(DEFAULT_STYLE_CLASS_BUTTON, s);
    }

    /**
     * adjust the style class and the action depending on the current state of the resize button
     */
    private void handleMaximizedChanged() {
      if (getSkinnable().isMaximized()) {
        resizeButton.getStyleClass().remove(DEFAULT_STYLE_CLASS_MAXIMIZE_BUTTON);
        resizeButton.getStyleClass().add(DEFAULT_STYLE_CLASS_RESTORE_BUTTON);

        resizeButton.setOnAction(restoreButtonHandler);
      }
      else {
        resizeButton.getStyleClass().remove(DEFAULT_STYLE_CLASS_RESTORE_BUTTON);
        resizeButton.getStyleClass().add(DEFAULT_STYLE_CLASS_MAXIMIZE_BUTTON);

        resizeButton.setOnAction(maximizeButtonHandler);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void layoutChildren() {
      double x = MARGIN_LEFT_RIGHT;
      if (icon.isVisible() && icon.getImage() != null) {
        // layout icon only when it is available
        layoutInArea(icon, x, MARGIN_TOP_BOTTOM, ICON_SIZE, ICON_SIZE, -1, HPos.LEFT, VPos.CENTER);
        x += ICON_SIZE;
        x += GAP;
      }

      // layout title
      layoutInArea(title, x, MARGIN_TOP_BOTTOM, ICON_SIZE, ICON_SIZE, -1, HPos.LEFT, VPos.CENTER);

      // layout from right to left to align buttons on the right side
      x = getWidth() - MARGIN_LEFT_RIGHT;
      if (closeButton.isVisible()) {
        // layout close button only when it is visible
        x -= ICON_SIZE;
        layoutInArea(closeButton, x, MARGIN_TOP_BOTTOM, ICON_SIZE, ICON_SIZE, -1, HPos.LEFT, VPos.CENTER);
      }
      if (resizeButton.isVisible()) {
        // layout resize button only when it is visible
        x -= ICON_SIZE;
        layoutInArea(resizeButton, x, MARGIN_TOP_BOTTOM, ICON_SIZE, ICON_SIZE, -1, HPos.LEFT, VPos.CENTER);
      }
      if (iconifyButton.isVisible()) {
        // layout iconify button only when it is visible
        x -= ICON_SIZE;
        layoutInArea(iconifyButton, x, MARGIN_TOP_BOTTOM, ICON_SIZE, ICON_SIZE, -1, HPos.LEFT, VPos.CENTER);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computeMinWidth(double height) {
      double width = snappedLeftInset();
      if (icon.isVisible() && icon.getImage() != null) {
        width += ICON_SIZE + GAP;
      }
      width += title.minWidth(height) + GAP;
      if (iconifyButton.isVisible()) {
        width += ICON_SIZE;
      }
      if (resizeButton.isVisible()) {
        width += ICON_SIZE;
      }
      if (closeButton.isVisible()) {
        width += ICON_SIZE;
      }
      return width + MARGIN_LEFT_RIGHT + snappedRightInset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computePrefWidth(double height) {
      return computeMinWidth(height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computeMaxWidth(double height) {
      return Double.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computeMinHeight(double width) {
      return TITLE_BAR_HEIGHT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computePrefHeight(double width) {
      return TITLE_BAR_HEIGHT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computeMaxHeight(double width) {
      return TITLE_BAR_HEIGHT;
    }
  }
}
