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
import java.util.WeakHashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.scout.commons.job.JobEx;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ClientSyncJob;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.basic.table.ITable;
import org.eclipse.scout.rt.client.ui.desktop.DesktopEvent;
import org.eclipse.scout.rt.client.ui.desktop.DesktopListener;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.ScoutInfoForm.MainBox.GroupBox.HtmlField;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.messagebox.IMessageBox;
import org.eclipsescout.rt.ui.fx.basic.NodeExporter;
import org.eclipsescout.rt.ui.fx.basic.table.FxScoutTable;
import org.eclipsescout.rt.ui.fx.basic.table.IFxScoutTable;
import org.eclipsescout.rt.ui.fx.concurrency.FxScoutSynchronizer;
import org.eclipsescout.rt.ui.fx.control.MultiSplitPane;
import org.eclipsescout.rt.ui.fx.control.MultiSplitPane.MultiSplitPaneConstraint;
import org.eclipsescout.rt.ui.fx.ext.ApplicationExt;
import org.eclipsescout.rt.ui.fx.ext.FxStatusPaneEx;
import org.eclipsescout.rt.ui.fx.form.FxScoutForm;
import org.eclipsescout.rt.ui.fx.form.IFxScoutForm;
import org.eclipsescout.rt.ui.fx.form.fields.IFxScoutFormField;
import org.eclipsescout.rt.ui.fx.window.IFxScoutView;
import org.eclipsescout.rt.ui.fx.window.desktop.FxScoutRootStage;
import org.eclipsescout.rt.ui.fx.window.desktop.IFxScoutRootStage;
import org.eclipsescout.rt.ui.fx.window.desktop.menu.FxMenuCreator;
import org.eclipsescout.rt.ui.fx.window.internalwindow.FxScoutInternalWindow;
import org.eclipsescout.rt.ui.fx.window.messagebox.FxScoutMessageBox;
import org.eclipsescout.rt.ui.fx.window.messagebox.IFxScoutMessageBox;

/**
 *
 */
public class AbstractFxEnvironment implements IFxEnvironment {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(AbstractFxEnvironment.class);

  private IClientSession m_scoutSession;
  private final FxLaunchJob m_launchJob;
  private final FxScoutSynchronizer m_synchronizer;
  private IFxScoutRootStage m_scoutRootStage;
  private FxIconLocator m_iconLocator;
  private FormFieldFactory m_formFieldFactory;
  private CssExtensionPointReader m_cssFactory;
  private final WeakHashMap<IForm, IFxScoutForm> m_standaloneFormComposites;
  private final Object m_immediateFxJobsLock = new Object();
  private final List<Runnable> m_immediateFxJobs = new ArrayList<Runnable>();

  public AbstractFxEnvironment() {
    m_launchJob = new FxLaunchJob();
    m_synchronizer = new FxScoutSynchronizer(this);
    m_standaloneFormComposites = new WeakHashMap<IForm, IFxScoutForm>();
    LOG.setLevel(IScoutLogger.LEVEL_DEBUG);
  }

  @Override
  public Job getLaunchJob() {
    return m_launchJob;
  }

  @Override
  public void startFxAsync() {
    getLaunchJob().schedule();
  }

  @Override
  public void init(IClientSession clientSession) {
    m_scoutSession = clientSession;
  }

  @Override
  public void showGUI(Stage rootStage) {
    checkThread();

    m_iconLocator = createIconLocator();

    final IDesktop desktop = m_scoutSession.getDesktop();
    if (desktop != null) {
      m_scoutSession.getDesktop().addDesktopListener(new DesktopListener() {
        @Override
        public void desktopChanged(final DesktopEvent e) {
          switch (e.getType()) {
            case DesktopEvent.TYPE_FORM_ADDED: {
              Runnable t = new Runnable() {
                @Override
                public void run() {
                  showStandaloneForm(null, e.getForm());
                }
              };
              invokeFxLater(t);
              break;
            }
            case DesktopEvent.TYPE_FORM_REMOVED: {
              Runnable t = new Runnable() {
                @Override
                public void run() {
                  hideStandaloneForm(e.getForm());
                }
              };
              invokeFxLater(t);
              break;
            }
            case DesktopEvent.TYPE_FORM_ENSURE_VISIBLE: {
              //TODO: implement
              LOG.debug("TYPE_FORM_ENSURE_VISIBLE: ");
              break;
            }
            case DesktopEvent.TYPE_MESSAGE_BOX_ADDED: {
              Runnable t = new Runnable() {
                @Override
                public void run() {
                  showMessageBox(null, e.getMessageBox());
                }
              };
              invokeFxLater(t);
              break;
            }
            case DesktopEvent.TYPE_FILE_CHOOSER_ADDED: {
              //TODO: implement
              LOG.debug("TYPE_FILE_CHOOSER_ADDED: ");
              break;
            }
            case DesktopEvent.TYPE_FIND_FOCUS_OWNER: {
              //TODO: implement
              LOG.debug("TYPE_FIND_FOCUS_OWNER: ");
              break;
            }
          }
        }
      });

      setStageIcons(rootStage);

      m_scoutRootStage = new FxScoutRootStage(rootStage);
      m_scoutRootStage.createField(desktop, this);
      m_scoutRootStage.showFxStage();

      for (IForm f : desktop.getViewStack()) {
        if (f.isAutoAddRemoveOnDesktop()) {
          showStandaloneForm(m_scoutRootStage.getFxField(), f);
        }
      }
      // messageboxes
      for (IMessageBox mb : desktop.getMessageBoxStack()) {
        showMessageBox(getScoutRootStage().getFxContainer(), mb);
      }

      // notify desktop that it is loaded
      new ClientSyncJob("Desktop opened", m_scoutSession) {
        @Override
        protected void runVoid(IProgressMonitor monitor) throws Throwable {
          desktop.getUIFacade().fireDesktopOpenedFromUI();
          desktop.getUIFacade().fireGuiAttached();
        }
      }.schedule();
    }

  }

  /**
   * Factory
   *
   * @return
   */
  private FxIconLocator createIconLocator() {
    return new FxIconLocator(getScoutSession().getIconLocator());
  }

  private void checkThread() {
    if (!Platform.isFxApplicationThread()) {
      throw new IllegalStateException("Must be called in fx thread");
    }
  }

  protected void setStageIcons(Stage stage) {
    // legacy
    Image legacyIcon = getImage(FxIcons.Window);
    if (legacyIcon != null) {
      stage.getIcons().add(legacyIcon);
    }
    else {
      for (String name : new String[]{FxIcons.Window16, FxIcons.Window32, FxIcons.Window48, FxIcons.Window256}) {
        Image img = getImage(name);
        if (img != null) {
          stage.getIcons().add(img);
        }
      }
    }
  }

  @Override
  public void showStandaloneForm(Node parent, IForm f) {
    if (f == null) {
      return;
    }
    IFxScoutView view = null;
    IFxScoutForm formComposite = m_standaloneFormComposites.get(f);
    if (formComposite != null) {
      view = formComposite.getView();
    }
    if (view != null) {
      return;
    }

    switch (f.getDisplayHint()) {
      case IForm.DISPLAY_HINT_DIALOG: {
        if (f.isModal()) {
          // TODO: implement
          LOG.debug("TYPE_FORM_ADDED: Dialog");
        }
        else {
          LOG.debug("TYPE_FORM_ADDED: Frame");
          // TODO: implement
        }

        // TODO: remove test data
        boolean dialogShown = false;
        for (IFormField field : f.getAllFields()) {
          if (field instanceof HtmlField) {
            dialogShown = true;
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            webEngine.loadContent(((HtmlField) field).getDisplayText());
            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(browser);
            dialog.setScene(scene);
            dialog.show();
          }
        }
        if (!dialogShown) {
          Stage dialog = new Stage();
          dialog.initStyle(StageStyle.UTILITY);
          Scene scene = new Scene(new Label("new dialog"));
          dialog.setScene(scene);
          dialog.setWidth(300);
          dialog.setHeight(300);
          dialog.show();
        }

        break;
      }
      case IForm.DISPLAY_HINT_VIEW: {
        // TODO: improve
        view = createView(f);
        break;
      }
      case IForm.DISPLAY_HINT_POPUP_WINDOW: {
        // TODO: implement
        LOG.debug("TYPE_FORM_ADDED: View");
        break;
      }
      case IForm.DISPLAY_HINT_POPUP_DIALOG: {
        // TODO: implement
        LOG.debug("TYPE_FORM_ADDED: View");
        break;
      }
    }

    if (view != null) {
      formComposite = createForm(view, f);
      m_standaloneFormComposites.put(f, formComposite);
      view.openView();
    }

  }

  @Override
  public void hideStandaloneForm(IForm f) {
    if (f != null) {
      IFxScoutForm formComposite = m_standaloneFormComposites.remove(f);
      if (formComposite != null) {
        IFxScoutView viewComposite = formComposite.getView();
        formComposite.detachFxView();
        if (viewComposite != null) {
          viewComposite.closeView();
        }
      }
    }
  }

  @Override
  public void showMessageBox(Node parent, IMessageBox mb) {
    IFxScoutMessageBox mbox = createMessageBox(getOwnerForChildWindow(parent), mb);
    mbox.showFxMessageBox();
  }

  /**
   * @param parent
   * @return
   */
  protected Window getOwnerForChildWindow(Node parent) {
    if (parent != null && parent.getScene() != null && parent.getScene().getWindow() != null) {
      return parent.getScene().getWindow();
    }
    return getScoutRootStage().getFxContainer().getScene().getWindow();
  }

  @Override
  public IFxScoutMessageBox createMessageBox(Window w, IMessageBox mb) {
    IFxScoutMessageBox ui = new FxScoutMessageBox(w);
    ui.createField(mb, this);
    setStageIcons(ui.getFxStage());
    return ui;
  }

  /**
   * @param view
   * @param f
   * @return
   */
  private IFxScoutForm createForm(IFxScoutView targetViewComposite, IForm model) {
    IFxScoutForm ui = new FxScoutForm(targetViewComposite);
    ui.createField(model, this);
    return ui;
  }

  /**
   * @param f
   * @return
   */
  @Override
  public IFxScoutView createView(IForm form) {
    // TODO: improve
    IFxScoutView ui = createInternalWindow(form);
    ui.setName("Synth.View");
    return ui;
  }

  /**
   * @param form
   * @return
   */
  private IFxScoutView createInternalWindow(IForm form) {
    // TODO: improve
    FxScoutInternalWindow w = new FxScoutInternalWindow(this);

    w.getFxInternalWindow().getProperties().put(MultiSplitPane.CONSTRAINT_PROPERTY_NAME, getViewLayoutConstraintsFor(form));
    return w;
  }

  @Override
  public MultiSplitPaneConstraint getViewLayoutConstraintsFor(IForm f) {
    String viewId = f.getDisplayViewId();
    return getViewLayoutConstraintsFor(viewId);
  }

  @Override
  public MultiSplitPaneConstraint getViewLayoutConstraintsFor(String viewId) {
    // begin mapping
    if (IForm.VIEW_ID_OUTLINE_SELECTOR.equalsIgnoreCase(viewId)) {
      viewId = IForm.VIEW_ID_SW;
    }
    else if (IForm.VIEW_ID_OUTLINE.equalsIgnoreCase(viewId)) {
      viewId = IForm.VIEW_ID_NW;
    }
    else if (IForm.VIEW_ID_PAGE_DETAIL.equalsIgnoreCase(viewId)) {
      viewId = IForm.VIEW_ID_N;
    }
    else if (IForm.VIEW_ID_PAGE_TABLE.equalsIgnoreCase(viewId)) {
      viewId = IForm.VIEW_ID_CENTER;
    }
    else if (IForm.VIEW_ID_PAGE_SEARCH.equalsIgnoreCase(viewId)) {
      viewId = IForm.VIEW_ID_S;
    }
    // end mapping
    if (viewId == null) {
      return new MultiSplitPaneConstraint(4);
    }
    else if (viewId.equalsIgnoreCase(IForm.VIEW_ID_N)) {
      return new MultiSplitPaneConstraint(3);
    }
    else if (viewId.equalsIgnoreCase(IForm.VIEW_ID_NE)) {
      return new MultiSplitPaneConstraint(6);
    }
    else if (viewId.equalsIgnoreCase(IForm.VIEW_ID_E)) {
      return new MultiSplitPaneConstraint(7);
    }
    else if (viewId.equalsIgnoreCase(IForm.VIEW_ID_SE)) {
      return new MultiSplitPaneConstraint(8);
    }
    else if (viewId.equalsIgnoreCase(IForm.VIEW_ID_S)) {
      return new MultiSplitPaneConstraint(5);
    }
    else if (viewId.equalsIgnoreCase(IForm.VIEW_ID_SW)) {
      return new MultiSplitPaneConstraint(2);
    }
    else if (viewId.equalsIgnoreCase(IForm.VIEW_ID_W)) {
      return new MultiSplitPaneConstraint(1);
    }
    else if (viewId.equalsIgnoreCase(IForm.VIEW_ID_NW)) {
      return new MultiSplitPaneConstraint(0);
    }
    else if (viewId.equalsIgnoreCase(IForm.VIEW_ID_CENTER)) {
      return new MultiSplitPaneConstraint(4);
    }
    else {
      LOG.warn("unexpected viewId \"" + viewId + "\"");
      return new MultiSplitPaneConstraint(4);
    }
  }

  @Override
  public IClientSession getScoutSession() {
    return m_scoutSession;
  }

  @Override
  public void postImmediateFxJob(Runnable r) {
    synchronized (m_immediateFxJobsLock) {
      m_immediateFxJobs.add(r);
    }
  }

  @Override
  public void dispatchImmediateFxJobs() {
    List<Runnable> list;
    synchronized (m_immediateFxJobsLock) {
      list = new ArrayList<Runnable>(m_immediateFxJobs);
      m_immediateFxJobs.clear();
    }
    for (Runnable r : list) {
      try {
        r.run();
      }
      catch (Throwable t) {
        LOG.warn("running " + r, t);
      }
    }
  }

  @Override
  public void invokeFxLater(Runnable j) {
    synchronized (m_immediateFxJobsLock) {
      m_immediateFxJobs.clear();
    }
    m_synchronizer.invokeFxLater(j);
  }

  @Override
  public JobEx invokeScoutLater(Runnable j, long cancelTimeout) {
    return m_synchronizer.invokeScoutLater(j, cancelTimeout);
  }

  /**
   * @return The client session for this application. </br>
   *         May return a new instance every time it is called.
   */
  //protected abstract IClientSession getClientSession();

  private final static class FxLaunchJob extends Job {
    public FxLaunchJob() {
      super("FX Job");
      setSystem(true);
      setUser(false);
      setPriority(Job.DECORATE);
    }

    @Override
    protected org.eclipse.core.runtime.IStatus run(org.eclipse.core.runtime.IProgressMonitor monitor) {
      Application.launch(ApplicationExt.class);
      return Status.OK_STATUS;
    }
  }

  @Override
  public int getFormColumnWidth() {
    return 360;
  }

  @Override
  public int getFormColumnGap() {
    return 12;
  }

  @Override
  public int getFormRowGap() {
    return 6;
  }

  @Override
  public int getFormRowHeight() {
    return 23;
  }

  @Override
  public int getFieldLabelWidth() {
    return 130;
  }

  @Override
  public List<Menu> createTopLevelMenus(List<? extends IMenu> actions) {
    return new FxMenuCreator().createTopLevelMenus(this, actions);
  }

  @Override
  public Image getImage(String name) {
    Image image = m_iconLocator.getImage(name);
    // No application image could be found. Look for a respective Scout image.
    if (image == null) {
      image = Activator.getImage(name);
    }
    return image;
  }

  @Override
  public ImageView getImageView(String name) {
    ImageView imageView = m_iconLocator.getImageView(name);
    // No application image could be found. Look for a respective Scout image.
    if (imageView == null) {
      imageView = Activator.getImageView(name);
    }
    return imageView;
  }

  @Override
  public IFxScoutRootStage getScoutRootStage() {
    return m_scoutRootStage;
  }

  @Override
  public IFxScoutFormField createFormField(Parent parent, IFormField model) {
    if (m_formFieldFactory == null) {
      m_formFieldFactory = new FormFieldFactory();
    }
    IFxScoutFormField ui = m_formFieldFactory.createFormField(parent, model, this);
    return ui;
  }

  @Override
  public ArrayList<String> getCSSURLsFromExtensionPoint() {
    if (m_cssFactory == null) {
      m_cssFactory = new CssExtensionPointReader();
    }
    return m_cssFactory.getURLs();
  }

  @Override
  public FxStatusPaneEx createStatusLabel(IFormField formField) {
    return new FxStatusPaneEx();
  }

  @Override
  public IFxScoutTable createTable(ITable table) {
    return new FxScoutTable();
  }

  @Override
  public void printNode(Node node) {
    checkThread();
    new NodeExporter().print(node);
  }

  @Override
  public void exportNodeAsImage(Node node) {
    checkThread();
    new NodeExporter().saveSnapshot(node);
  }
}
