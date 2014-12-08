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

 import javafx.scene.image.Image;
 import javafx.scene.image.ImageView;

 import org.eclipse.core.runtime.Plugin;
 import org.eclipsescout.rt.ui.fx.icons.FxBundleIconLocator;
 import org.osgi.framework.BundleContext;

 public class Activator extends Plugin {

   public static String PLUGIN_ID = "org.eclipsescout.rt.ui.fx";

   private static BundleContext context;
   private static Activator activator;

   private IFxEnvironment m_fxEnv;
   private FxIconLocator m_iconLocator;

   static BundleContext getContext() {
     return context;
   }

   @Override
   public void start(BundleContext bundleContext) throws Exception {
     super.start(bundleContext);
     Activator.context = bundleContext;
     activator = this;
     FxBundleIconLocator iconLocator = new FxBundleIconLocator();
     m_iconLocator = new FxIconLocator(iconLocator);
   }

   @Override
   public void stop(BundleContext bundleContext) throws Exception {
     Activator.context = null;
     activator = null;
     m_iconLocator = null;
     super.stop(bundleContext);
   }

   public static Activator getDefault() {
     return activator;
   }

   public IFxEnvironment getFxEnv() {
     return m_fxEnv;
   }

   public void setFxEnv(IFxEnvironment fxEnv) {
     m_fxEnv = fxEnv;
   }

   public static Image getImage(String name) {
     return getDefault().getImageImpl(name);
   }

   public static ImageView getImageView(String name) {
     // TODO: check if "if" is necessary
     Activator act = getDefault();
     if (act != null) {
       return act.getImageViewImpl(name);
     }
     return null;
   }

   private Image getImageImpl(String name) {
     return m_iconLocator.getImage(name);
   }

   private ImageView getImageViewImpl(String name) {
     return m_iconLocator.getImageView(name);
   }
 }
