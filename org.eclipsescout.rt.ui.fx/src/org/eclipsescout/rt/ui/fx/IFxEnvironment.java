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
package org.eclipsescout.rt.ui.fx;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.scout.commons.job.JobEx;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.basic.table.ITable;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.messagebox.IMessageBox;
import org.eclipsescout.rt.ui.fx.basic.table.IFxScoutTable;
import org.eclipsescout.rt.ui.fx.control.MultiSplitPane.MultiSplitPaneConstraint;
import org.eclipsescout.rt.ui.fx.ext.FxStatusPaneEx;
import org.eclipsescout.rt.ui.fx.form.fields.IFxScoutFormField;
import org.eclipsescout.rt.ui.fx.window.IFxScoutView;
import org.eclipsescout.rt.ui.fx.window.desktop.IFxScoutRootStage;
import org.eclipsescout.rt.ui.fx.window.messagebox.IFxScoutMessageBox;

/**
 *
 */
public interface IFxEnvironment {

  /**
   * @return
   */
  public Job getLaunchJob();

  /**
   *
   */
  public void startFxAsync();

  /**
   * @param clientSession
   */
  public void init(IClientSession clientSession);

  /**
   * This method has to be invoked in the JavaFx-Thread.
   *
   * @param rootStage
   */
  public void showGUI(Stage rootStage);

  /**
   * @return
   */
  public IClientSession getScoutSession();

  /**
   * @param r
   */
  public void invokeFxLater(Runnable r);

  /**
   * @param r
   * @param cancelTimeout
   * @return
   */
  public JobEx invokeScoutLater(Runnable r, long cancelTimeout);

  /**
   * @return
   */
  public int getFormColumnWidth();

  /**
   * @return
   */
  public int getFormColumnGap();

  /**
   * @return
   */
  public int getFormRowGap();

  /**
   * @return
   */
  public int getFormRowHeight();

  /**
   * @return
   */
  public int getFieldLabelWidth();

  /**
   * @param menus
   * @return
   */
  public List<Menu> createTopLevelMenus(List<? extends IMenu> menus);

  /**
   * @param name
   * @return
   */
  public Image getImage(String name);

  /**
   * @param name
   * @return
   */
  public ImageView getImageView(String name);

  /**
   * @param f
   * @return
   */
  public IFxScoutView createView(IForm f);

  public IFxScoutRootStage getScoutRootStage();

  /**
   * @param parent
   * @param rootGroupBox
   * @return
   */
  public IFxScoutFormField createFormField(Parent parent, IFormField model);

  /**
   * Called from scout job/thread to post an immediate fx job into the waiting queue.
   * <p>
   * These jobs are run when calling {@link #dispatchImmediateFxJobs()}. Normally this kind of code is only used to
   * early apply visible and enabled properties in
   * {@link org.eclipsescout.rt.ui.fx.form.fields.FxScoutFieldComposite#handleFxInputVerifier()
   * FxScoutFieldComposite#handleFxInputVerifier()} in order to have before-focus-traversal visible/enabled state-update
   */
  void postImmediateFxJob(Runnable r);

  void dispatchImmediateFxJobs();

  /**
   * @param viewId
   * @return
   */
  MultiSplitPaneConstraint getViewLayoutConstraintsFor(String viewId);

  /**
   * @param f
   * @return
   */
  MultiSplitPaneConstraint getViewLayoutConstraintsFor(IForm f);

  /**
   * @param parent
   * @param f
   */
  void showStandaloneForm(Node parent, IForm f);

  /**
   * @param f
   */
  void hideStandaloneForm(IForm f);

  /**
   * @return
   */
  public ArrayList<String> getCSSURLsFromExtensionPoint();

  /**
   * @param formField
   * @return
   */
  public FxStatusPaneEx createStatusLabel(IFormField formField);

  /**
   * @param newTable
   * @return
   */
  public IFxScoutTable createTable(ITable table);

  /**
   * Prints the specified node
   * Must be called in fx thread!
   *
   * @param node
   */
  public void printNode(Node node);

  /**
   * Exports the specified node as image
   * Must be called in fx thread!
   *
   * @param chart
   */
  public void exportNodeAsImage(Node node);

  /**
   * @param parent
   * @param mb
   */
  void showMessageBox(Node parent, IMessageBox mb);

  /**
   * @param w
   * @param mb
   * @return
   */
  IFxScoutMessageBox createMessageBox(Window w, IMessageBox mb);
}
