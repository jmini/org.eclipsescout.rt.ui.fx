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
package org.eclipsescout.rt.ui.fx.concurrency;

import javafx.application.Platform;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.scout.commons.job.JobEx;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ClientSyncJob;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;

public class FxScoutSynchronizer {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(FxScoutSynchronizer.class);

  private IFxEnvironment m_env;
  //loop detection from javafx to scout
  private LoopDetector m_loopDetector;

  public FxScoutSynchronizer(IFxEnvironment env) {
    m_env = env;
    m_loopDetector = new LoopDetector(5000L, 2500, 10);
  }

  private boolean isModelThread() {
    return ClientSyncJob.getCurrentSession() == m_env.getScoutSession() && ClientSyncJob.isSyncClientJob();
  }

  /**
   * Calling from javafx thread and posting scout job to complete.
   * <p>
   * The job is only run, when it is processed before timeout (value > 0), otherwise it is ignored.
   * <p>
   * A timeout value &lt;= 0 means no timeout.
   */
  public JobEx invokeScoutLater(final Runnable j, long cancelTimeout) {
    if (isModelThread()) {
      LOG.warn("trying to queue scout runnable but already in scout thread: " + j);
      j.run();
      return null;
    }
    else if (!Platform.isFxApplicationThread()) {
      throw new IllegalStateException("queueing scout runnable from outside javafx thread: " + j);
    }
    //
    m_loopDetector.addSample();
    if (m_loopDetector.isArmed()) {
      LOG.warn("loop detection: " + j, new Exception("Loop detected"));
      return null;
    }
    //send job
    final long deadLine = cancelTimeout > 0 ? System.currentTimeMillis() + cancelTimeout : -1;
    ClientSyncJob eclipseJob = new ClientSyncJob("JavaFX post::" + j, m_env.getScoutSession()) {
      @Override
      protected void runVoid(IProgressMonitor monitor) throws Throwable {
        if (deadLine < 0 || deadLine > System.currentTimeMillis()) {
          j.run();
        }
      }
    };
    eclipseJob.schedule();
    return eclipseJob;
  }

  /**
   * calling from scout thread and waiting for javafx job to complete
   */
  public void invokeFxLater(final Runnable j) {
    if (Platform.isFxApplicationThread()) {
      LOG.warn("trying to queue javafx runnable but already in javafx thread: " + j);
      j.run();
      return;
    }
    if (!isModelThread()) {
      throw new IllegalStateException("queueing javafx runnable from outside scout thread: " + j);
    }
    //
    Platform.runLater(j);
  }

}
