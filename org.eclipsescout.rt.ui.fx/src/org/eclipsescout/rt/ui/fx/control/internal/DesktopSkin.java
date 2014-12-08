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
import javafx.collections.SetChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;

import org.eclipsescout.rt.ui.fx.control.Desktop;
import org.eclipsescout.rt.ui.fx.control.IDesktopRootPane;
import org.eclipsescout.rt.ui.fx.control.InternalWindow;
import org.eclipsescout.rt.ui.fx.control.MultiSplitPane;

public class DesktopSkin extends SkinBase<Desktop> {

  public static final String DEFAULT_STYLE_CLASS_TASKBAR = "taskbar";

  // keys for the listeners which are registered on the internal windows
  private static final String LISTENER = "desktopListener";
  private static final String CLOSED_LISTENER = "desktopClosedListener";

  /**
   * Desktop root pane contains all windows which are not maximized or iconified
   */
  private final IDesktopRootPane rootPane;

  /**
   * Taskbar of the desktop contains all iconified internal windows
   */
  private final HBox taskbar = new HBox();

  /* *************************************************************
   * Constructors
   * ************************************************************/

  /**
   * Constructor for all DesktopSkin instances.
   *
   * @param desktop
   *          The {@code Desktop} for which this Skin should attach to.
   */
  public DesktopSkin(Desktop desktop) {
    super(desktop);

    rootPane = createDesktopRootPane();

    init();
  }

  /* *************************************************************
   * Methods
   * ************************************************************/

  /**
   * Adds listeners to the desktop properties
   */
  private void init() {
    // add listener to the child list of the taskbar to add or remove the taskbar from the desktop
    taskbar.getChildren().addListener(new InvalidationListener() {
      private int oldSize;

      @Override
      public void invalidated(Observable observable) {
        if (taskbar.getChildren().size() == 0) {
          getChildren().remove(taskbar);
        }
        if (oldSize == 0 && taskbar.getChildren().size() > 0) {
          getChildren().add(taskbar);
        }
        oldSize = taskbar.getChildren().size();
      }
    });

    // add listener to get notification about added and removed internal windows
    getSkinnable().getInternalWindows().addListener(new SetChangeListener<InternalWindow>() {
      @Override
      public void onChanged(SetChangeListener.Change<? extends InternalWindow> change) {
        if (change.wasRemoved()) {
          handleInternalWindowRemoved(change.getElementRemoved());
        }
        if (change.wasAdded()) {
          handleInternalWindowAdded(change.getElementAdded());
        }
      }
    });
    // add all existing windows
    for (InternalWindow window : getSkinnable().getInternalWindows()) {
      handleInternalWindowAdded(window);
    }

    getChildren().add(rootPane.getNode());

    taskbar.getStyleClass().setAll(DEFAULT_STYLE_CLASS_TASKBAR);
  }

  /**
   * Creates and returns a desktop root pane which is used to add internal windows.
   * This method will be called once when the desktop skin will be created.
   *
   * @return desktop root pane
   */
  protected IDesktopRootPane createDesktopRootPane() {
    return new MultiSplitPane(3, 3, Orientation.HORIZONTAL);
  }

  /**
   * This method will be called when the specified {@code InternalWindow} will be added to this desktop.
   *
   * @param w
   *          The internal window to add
   */
  private void handleInternalWindowAdded(InternalWindow w) {
    addListeners(w);

    // call this method to put the window to the right place
    handleInternalWindowPropertyChanged(w);

    if (w.isClosed()) {
      w.setOpen(true);
    }
  }

  /**
   * This method will be called when the specified {@code InternalWindow} will be removed from this desktop.
   *
   * @param w
   *          The internal window to remove
   */
  private void handleInternalWindowRemoved(InternalWindow w) {
    removeListeners(w);

    // remove window from all elements
    taskbar.getChildren().remove(w);
    getChildren().remove(w);
    rootPane.removeWindow(w);

    if (w.isOpen()) {
      w.setClosed(true);
    }
  }

  /**
   * Adds listeners to the iconified, maximized, closed properties of the specified internal window.
   * The listeners can be removed with {@code DesktopSkin#removeListeners(InternalWindow)}
   *
   * @param w
   *          The internal window to add the listeners
   */
  private void addListeners(final InternalWindow w) {
    // iconified / maximized listener
    InvalidationListener listener = new InvalidationListener() {
      @Override
      public void invalidated(Observable observable) {
        handleInternalWindowPropertyChanged(w);
      }
    };
    w.iconifiedProperty().addListener(listener);
    w.maximizedProperty().addListener(listener);
    w.getProperties().put(LISTENER, listener);

    // closed listener: removes the internal window from the list of the desktop
    listener = new InvalidationListener() {
      @Override
      public void invalidated(Observable observable) {
        if (w.isClosed()) {
          getSkinnable().getInternalWindows().remove(w);
        }
      }
    };
    w.closedProperty().addListener(listener);
    w.getProperties().put(CLOSED_LISTENER, listener);
  }

  /**
   * Removes listeners from the iconified, maximized, closed properties of the specified internal window.
   * The listeners has to be added with {@code DesktopSkin#addListeners(InternalWindow)}
   *
   * @param w
   *          The internal window to remove the listeners
   */
  private void removeListeners(InternalWindow w) {
    InvalidationListener listener = (InvalidationListener) w.getProperties().remove(LISTENER);
    w.iconifiedProperty().removeListener(listener);
    w.maximizedProperty().removeListener(listener);

    listener = (InvalidationListener) w.getProperties().remove(CLOSED_LISTENER);
    w.closedProperty().removeListener(listener);
  }

  /**
   * Adds the specified {@code InternalWindow} to the taskbar, maximized windows or root pane depending on the state of
   * the internal windows.
   *
   * @param w
   */
  private void handleInternalWindowPropertyChanged(InternalWindow w) {
    if (w == null) {
      return;
    }

    if (w.isIconified()) {
      rootPane.removeWindow(w);
      getChildren().remove(w);
      taskbar.getChildren().add(w);
    }
    else if (w.isMaximized()) {
      rootPane.removeWindow(w);
      taskbar.getChildren().remove(w);
      getChildren().add(w);
    }
    else {
      getChildren().remove(w);
      taskbar.getChildren().remove(w);
      rootPane.addWindow(w);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
    double taskBarHeight = 0.0;
    if (!taskbar.getChildren().isEmpty()) {
      // layout taskbar only when there are iconified windows
      taskBarHeight = taskbar.getHeight();
      layoutInArea(taskbar, 0, contentHeight - taskBarHeight, contentWidth, taskBarHeight, -1, HPos.LEFT, VPos.TOP);
    }

    contentHeight -= taskBarHeight;
    for (Node n : getChildren()) {
      if (n != taskbar) {
        layoutInArea(n, 0, 0, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER);
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    double minY = 0;
    double maxY = 0;
    boolean firstManagedChild = true;
    for (Node node : getChildren()) {
      if (node.isManaged() && node != taskbar) {
        final double y = node.getLayoutBounds().getMinY() + node.getLayoutY();
        if (!firstManagedChild) { // branch prediction favors most often used condition
          minY = Math.min(minY, y);
          maxY = Math.max(maxY, y + node.minHeight(-1));
        }
        else {
          minY = y;
          maxY = y + node.minHeight(-1);
          firstManagedChild = false;
        }
      }
    }
    double minHeight = maxY - minY;
    if (!taskbar.getChildren().isEmpty()) {
      minHeight += taskbar.minHeight(-1);
    }
    return topInset + minHeight + bottomInset;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    double minY = 0;
    double maxY = 0;
    boolean firstManagedChild = true;
    for (Node node : getChildren()) {
      if (node.isManaged() && node != taskbar) {
        final double y = node.getLayoutBounds().getMinY() + node.getLayoutY();
        if (!firstManagedChild) { // branch prediction favors most often used condition
          minY = Math.min(minY, y);
          maxY = Math.max(maxY, y + node.prefHeight(-1));
        }
        else {
          minY = y;
          maxY = y + node.prefHeight(-1);
          firstManagedChild = false;
        }
      }
    }
    double prefHeight = maxY - minY;
    if (!taskbar.getChildren().isEmpty()) {
      prefHeight += taskbar.prefHeight(-1);
    }
    return prefHeight;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return Double.MAX_VALUE;
  }

}
