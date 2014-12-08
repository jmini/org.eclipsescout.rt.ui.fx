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
package org.eclipsescout.rt.ui.fx.form.fields.tablefield;

import javafx.scene.control.TableView;

import org.eclipse.scout.rt.client.ui.form.fields.tablefield.ITableField;
import org.eclipsescout.rt.ui.fx.form.fields.IFxScoutFormField;

/**
 *
 */
public interface IFxScoutTableField extends IFxScoutFormField<ITableField<?>> {

  public TableView<?> getFxTable();

  public IFxTableStatus getFxTableStatus();

}
