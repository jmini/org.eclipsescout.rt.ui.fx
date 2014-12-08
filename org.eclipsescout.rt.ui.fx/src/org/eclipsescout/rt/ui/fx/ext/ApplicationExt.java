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
package org.eclipsescout.rt.ui.fx.ext;

import javafx.application.Application;
import javafx.stage.Stage;

import org.eclipsescout.rt.ui.fx.Activator;

/**
 * Extended version of the javafx Application.
 */
public class ApplicationExt extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Activator.getDefault().getFxEnv().showGUI(primaryStage);

    String userAgentStylesheet = Activator.getDefault().getBundle().getBundleContext().getProperty("javafx.userAgentStylesheet");
    if ("caspian".equals(userAgentStylesheet)) {
      setUserAgentStylesheet(STYLESHEET_CASPIAN);
    }
    else {
      setUserAgentStylesheet(STYLESHEET_MODENA);
    }
  }

}
