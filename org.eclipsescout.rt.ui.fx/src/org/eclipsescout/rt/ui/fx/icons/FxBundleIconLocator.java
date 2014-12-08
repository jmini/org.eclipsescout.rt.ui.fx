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
package org.eclipsescout.rt.ui.fx.icons;

import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.services.common.icon.IconProviderService;
import org.eclipse.scout.rt.client.services.common.icon.IconSpec;
import org.eclipse.scout.rt.client.ui.IIconLocator;
import org.eclipse.scout.rt.shared.AbstractIcons;
import org.eclipsescout.rt.ui.fx.services.FxBundleIconProviderService;

/**
 * IIconLocator implementation which use only the {@link FxBundleIconProviderService} to find icons.
 */
public class FxBundleIconLocator implements IIconLocator {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(FxBundleIconLocator.class);

  private final IconProviderService m_fxIconProviderService;

  public FxBundleIconLocator() {
    m_fxIconProviderService = new FxBundleIconProviderService();
  }

  @Override
  public IconSpec getIconSpec(String name) {
    if (name == null || AbstractIcons.Null.equals(name)) {
      return null;
    }
    return m_fxIconProviderService.getIconSpec(name);
  }
}
