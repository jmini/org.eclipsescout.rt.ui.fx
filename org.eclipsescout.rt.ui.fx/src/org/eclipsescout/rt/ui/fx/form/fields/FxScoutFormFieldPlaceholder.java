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
package org.eclipsescout.rt.ui.fx.form.fields;

import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipsescout.rt.ui.fx.ext.LabelEx;
import org.eclipsescout.rt.ui.fx.layout.BorderPaneEx;

/**
 * used for field models that have no ui implementation
 */
public class FxScoutFormFieldPlaceholder extends FxScoutFieldComposite<IFormField> implements IFxScoutFormField<IFormField> {

  @Override
  protected void initialize() {
    LabelEx label = new LabelEx();
    label.setText("Placeholder for " + getScoutObject().getClass().getSimpleName());
    BorderPaneEx container = new BorderPaneEx(0, 0);
    container.setCenter(label);
    //
    setFxField(label);
    setFxContainer(container);
  }

}
