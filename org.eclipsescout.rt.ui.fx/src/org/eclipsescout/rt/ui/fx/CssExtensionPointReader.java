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

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * 
 */
public class CssExtensionPointReader {

  private ArrayList<String> bundleFileUrls;

  public CssExtensionPointReader() {
    bundleFileUrls = new ArrayList<String>();
    IExtensionRegistry reg = Platform.getExtensionRegistry();
    IExtensionPoint xp = reg.getExtensionPoint(Activator.PLUGIN_ID, "resources.css");
    IExtension[] extensions = xp.getExtensions();
    for (IExtension extension : extensions) {
      IConfigurationElement[] elements = extension.getConfigurationElements();
      for (IConfigurationElement element : elements) {
        Bundle bundle = Platform.getBundle(element.getNamespaceIdentifier());
        URL url = bundle.getEntry(element.getAttribute("cssURL"));
        bundleFileUrls.add(url.toExternalForm());
      }
    }
  }

  public ArrayList<String> getURLs() {
    return bundleFileUrls;
  }
}
