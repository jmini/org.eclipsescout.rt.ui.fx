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
package org.eclipsescout.rt.ui.fx.extension;

import org.eclipsescout.rt.ui.fx.form.fields.IFxScoutFormField;

/**
 * <h3>IFormFieldExtension</h3> The extension bean defining a form field
 * extension. A form field extension can have direct link to an ui class
 * (Class<? extends {@link IFxScoutFormField}>) or a form field factory.</br>
 * Every extension has an attribute called scope. The scope might have one of
 * the following values:
 * <ul>
 * <li>
 *      {@link IFormFieldExtension#SCOPE_DEFAULT} to indicate this extension to be the default implementation. Is usually
 * used of external component providers (e.g. a PhotoShopField).</li>
 * <li>
 *      {@link IFormFieldExtension#SCOPE_GLOBAL} to indicate this extension to have a global scope (whole eclipse).
 * Global defined extensions overwrite the default implementation.</li>
 * </ul>
 */
public interface IFormFieldExtension {
  int SCOPE_DEFAULT = 0;
  int SCOPE_GLOBAL = 1;

  String getName();

  /**
   * <ul>
   * <li>
   *      {@link IFormFieldExtension#SCOPE_DEFAULT} to indicate this extension to be the default implementation.</li>
   * <li>
   *      {@link IFormFieldExtension#SCOPE_GLOBAL} to indicate this extension to have a global scope (whole eclipse).
   * Global defined extensions overwrite the default implementation.</li>
   * </ul>
   * 
   * @return one of {@link IFormFieldExtension#SCOPE_DEFAULT}, {@link IFormFieldExtension#SCOPE_GLOBAL}
   */
  int getScope();

  /**
   * users of this interface should type check
   * 
   * @return either a marker interface class name or a class name instanceof
   *         IFormField
   */
  String getModelClassName();

  /**
   * users of this interface should type check
   * 
   * @return a class name instanceof ISwtScoutFormField
   */
  String getUiClassName();

  /**
   * users of this interface should type check
   * 
   * @return a class name instanceof IFormFieldFactory
   */
  String getFactoryClassName();

  boolean isActive();

  /**
   * The id of the contibutor bundle.
   * 
   * @return
   */
  String getContibuterBundleId();

}
