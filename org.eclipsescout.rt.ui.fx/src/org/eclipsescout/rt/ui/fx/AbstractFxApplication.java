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

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.shared.ui.UiDeviceType;
import org.eclipse.scout.rt.shared.ui.UiLayer;
import org.eclipse.scout.rt.shared.ui.UserAgent;

/**
 *
 */
public abstract class AbstractFxApplication implements IApplication {

  IFxEnvironment m_env;

  @Override
  public Object start(IApplicationContext context) throws Exception {
    final IClientSession clientSession = getClientSession();

    m_env = createFxEnvironment();
    m_env.init(clientSession);
    Activator.getDefault().setFxEnv(m_env);
    m_env.startFxAsync();

    return runWhileActive(clientSession);
  }

  @Override
  public void stop() {
    getClientSession().stopSession();
  }

  /**
   * Blocks the main thread as as the client session is active.
   *
   * @param clientSession
   * @return exit code {@link org.eclipse.equinox.app.IApplication#EXIT_OK EXIT_OK},
   *         {@link org.eclipse.equinox.app.IApplication#EXIT_RELAUNCH EXIT_RELAUNCH},
   *         {@link org.eclipse.equinox.app.IApplication#EXIT_RESTART EXIT_RESTART}
   * @throws InterruptedException
   */
  private int runWhileActive(IClientSession clientSession) throws InterruptedException {
    Object stateLock = clientSession.getStateLock();
    while (true) {
      synchronized (stateLock) {
        if (clientSession.isActive()) {
          stateLock.wait();
        }
        else {
          Activator.getDefault().getFxEnv().getLaunchJob().join();
          return clientSession.getExitCode();
        }
      }
    }
  }

  protected UserAgent initUserAgent() {
    // TODO: change UILayer
    return UserAgent.create(UiLayer.SWING, UiDeviceType.DESKTOP);
  }

  public abstract IClientSession getClientSession();

  public abstract IFxEnvironment createFxEnvironment();

}
