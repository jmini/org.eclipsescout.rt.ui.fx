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
package org.eclipsescout.rt.ui.fx.window.desktop;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.eclipse.scout.rt.client.ui.ClientUIPreferences;
import org.eclipse.scout.rt.client.ui.desktop.DesktopEvent;
import org.eclipse.scout.rt.client.ui.desktop.DesktopListener;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.shared.data.basic.BoundsSpec;
import org.eclipsescout.rt.ui.fx.basic.FxScoutComposite;
import org.eclipsescout.rt.ui.fx.control.Desktop;
import org.eclipsescout.rt.ui.fx.control.InternalWindow;
import org.eclipsescout.rt.ui.fx.window.desktop.menubar.FxScoutMenuBar;

/**
 *
 */
public class FxScoutRootStage extends FxScoutComposite<IDesktop> implements IFxScoutRootStage {

  private static final int DEFAULT_SCREEN_SIZE_REDUCTION_X_DIRECTION = 120;
  private static final int DEFAULT_SCREEN_SIZE_REDUCTION_Y_DIRECTION = 80;

  private Stage m_stage;
  private BorderPane m_contentPane;
  private FxScoutMenuBar m_menuBarComposite;
  private final Desktop m_desktop;

  /**
   *
   */
  public FxScoutRootStage(Stage stage) {
    super();
    //LOG.setLevel(IScoutLogger.LEVEL_DEBUG);
    m_stage = stage;
    m_contentPane = new BorderPane();
    m_desktop = new Desktop();
  }

  @Override
  protected void initialize() {
    setFxField(m_desktop);
    Scene scene = new Scene(m_contentPane);
    scene.getStylesheets().addAll(getFxEnvironment().getCSSURLsFromExtensionPoint());
    m_stage.setScene(scene);

    ClientUIPreferences prefs = ClientUIPreferences.getInstance(getFxEnvironment().getScoutSession());
    BoundsSpec spec = prefs.getApplicationWindowBounds();
    double x, y, width, height;
    if (spec == null) {
      Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
      x = primaryScreenBounds.getMinX() + DEFAULT_SCREEN_SIZE_REDUCTION_X_DIRECTION;
      y = primaryScreenBounds.getMinY() + DEFAULT_SCREEN_SIZE_REDUCTION_Y_DIRECTION;
      width = primaryScreenBounds.getWidth() - 2 * DEFAULT_SCREEN_SIZE_REDUCTION_X_DIRECTION;
      height = primaryScreenBounds.getHeight() - 2 * DEFAULT_SCREEN_SIZE_REDUCTION_X_DIRECTION;
    }
    else {
      x = spec.x;
      y = spec.y;
      width = spec.width;
      height = spec.height;
    }
    m_stage.setX(x);
    m_stage.setY(y);
    m_stage.setWidth(width);
    m_stage.setHeight(height);
    m_stage.setMaximized(prefs.getApplicationWindowMaximized());

    m_stage.setOnCloseRequest(new P_FxStageEventHandler());
    m_stage.setTitle(getScoutObject().getTitle());
    getScoutObject().addDesktopListener(new P_ScoutDesktopListener());

    VBox topPane = new VBox();

    // menubar
    m_menuBarComposite = new FxScoutMenuBar();
    m_menuBarComposite.createField(getScoutObject(), getFxEnvironment());
    if (!m_menuBarComposite.isEmpty()) {
      MenuBar menuBar = m_menuBarComposite.getFxMenuBar();
      topPane.getChildren().add(menuBar);
    }

    // header pane (navigation, view buttons, tool buttons)
    //TODO: remove dummy buttons and implement correctly
    ToolBar toolBar = new ToolBar(
        new Button(null, getFxEnvironment().getImageView("folder")),
        new Button(null, getFxEnvironment().getImageView("add")),
        new Separator(),
        new Button(null, getFxEnvironment().getImageView("table")),
        new Button(null, getFxEnvironment().getImageView("chart_bar"))
        );
    topPane.getChildren().add(toolBar);

    m_contentPane.setTop(topPane);

    // main pane
    m_contentPane.setCenter(m_desktop);

    // status bar
    // TODO: remove dummy element and implement correctly
    Pane statusPane = new Pane();
    statusPane.getChildren().add(new Label("status pane"));
    m_contentPane.setBottom(statusPane);
  }

  @Override
  public void showFxStage() {
    m_stage.show();
  }

  @Override
  public void addView(InternalWindow window) {
    // TODO: improve
    m_desktop.getInternalWindows().add(window);
  }

  @Override
  public void removeView(InternalWindow window) {
    // TODO: improve
    m_desktop.getInternalWindows().remove(window);
  }

  /**
   * @param e
   */
  private void handleScoutDesktopClosedInFx(DesktopEvent e) {
    ClientUIPreferences prefs = ClientUIPreferences.getInstance(getFxEnvironment().getScoutSession());
    boolean maximized = m_stage.isMaximized();
    if (maximized) {
      m_stage.setMaximized(false);
    }
    BoundsSpec spec = new BoundsSpec((int) m_stage.getX(), (int) m_stage.getY(), (int) m_stage.getWidth(), (int) m_stage.getHeight());
    prefs.setApplicationWindowPreferences(spec, maximized);

    m_stage.close();
  }

  /**
   *
   */
  private void handleFxWindowClosing() {
    Runnable t = new Runnable() {
      @Override
      public void run() {
        getScoutObject().getUIFacade().fireDesktopClosingFromUI(false);
      }
    };
    getFxEnvironment().invokeScoutLater(t, 0);
  }

  private class P_ScoutDesktopListener implements DesktopListener {

    @Override
    public void desktopChanged(final DesktopEvent e) {
      switch (e.getType()) {
        case DesktopEvent.TYPE_DESKTOP_CLOSED: {
          Runnable t = new Runnable() {
            @Override
            public void run() {
              handleScoutDesktopClosedInFx(e);
            }
          };
          getFxEnvironment().invokeFxLater(t);
          break;
        }
      }
    }

  }

  private class P_FxStageEventHandler implements EventHandler<WindowEvent> {

    @Override
    public void handle(WindowEvent event) {
      event.consume();
      handleFxWindowClosing();
    }

  }

}
