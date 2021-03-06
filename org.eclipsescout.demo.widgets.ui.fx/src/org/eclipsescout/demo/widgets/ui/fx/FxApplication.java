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
package org.eclipsescout.demo.widgets.ui.fx;

import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.services.common.session.IClientSessionRegistryService;
import org.eclipsescout.rt.ui.fx.AbstractFxApplication;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;
import org.eclipse.scout.service.SERVICES;
import org.eclipsescout.demo.widgets.client.ClientSession;

public class FxApplication extends AbstractFxApplication {

	@Override
	public IClientSession getClientSession() {
		return SERVICES.getService(IClientSessionRegistryService.class).newClientSession(ClientSession.class, initUserAgent());
	}

	@Override
	public IFxEnvironment createFxEnvironment() {
	  return new FxEnvironment();
	}

}
