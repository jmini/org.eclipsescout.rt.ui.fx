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
package org.eclipsescout.rt.ui.fx.form;

import javafx.scene.layout.Pane;

import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;
import org.eclipsescout.rt.ui.fx.basic.IFxScoutComposite;
import org.eclipsescout.rt.ui.fx.window.IFxScoutView;

/**
 * 
 */
public interface IFxScoutForm extends IFxScoutComposite<IForm> {

  /**
   * @return
   */
  public Pane getFxFormPane();

  /**
   * 
   */
  public void setInitialFocus();

  /**
   * @return The view which is currently displaying this form
   */
  public IFxScoutView getView();

  /**
   * This method can (doesn't need to) be called when a form is to be detached
   * from its view but the view is kept open. For example wizard views may use
   * this method to display a series of forms one after another. {@link IFxEnvironment#createForm(IFxScoutView, IForm)}
   * to attach {@link IFxScoutForm#detachFxView()} to detach.
   * TODO: check comment
   */
  public void detachFxView();

}
