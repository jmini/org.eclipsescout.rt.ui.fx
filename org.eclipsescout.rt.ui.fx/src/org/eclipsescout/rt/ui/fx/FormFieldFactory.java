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

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.scene.Node;

import org.eclipse.core.runtime.Platform;
import org.eclipse.scout.commons.CompositeObject;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipsescout.rt.ui.fx.extension.FormFieldsExtensionPoint;
import org.eclipsescout.rt.ui.fx.extension.IFormFieldExtension;
import org.eclipsescout.rt.ui.fx.extension.IFormFieldFactory;
import org.eclipsescout.rt.ui.fx.form.fields.IFxScoutFormField;
import org.osgi.framework.Bundle;

public class FormFieldFactory implements IFormFieldFactory {
  private static IScoutLogger LOG = ScoutLogManager.getLogger(FormFieldFactory.class);

  private LinkedHashMap<Class<?>, IFormFieldFactory> m_fields;

  @SuppressWarnings("unchecked")
  public FormFieldFactory() {
    TreeMap<CompositeObject, P_FormFieldExtension> sortedMap = new TreeMap<CompositeObject, P_FormFieldExtension>();
    // Traverse all Extensions of the formfield-ExtensionPoint
    for (IFormFieldExtension extension : FormFieldsExtensionPoint.getFormFieldExtensions()) {
      if (extension.isActive()) {
        // Get corresponding Bundle to load classes
        Bundle loaderBundle = Platform.getBundle(extension.getContibuterBundleId());
        if (loaderBundle != null) {
          // Extract the needed model class and determine wheter or not the Extension is a ui-Element or a Factory
          Class<?> modelClazz;
          Class<? extends IFxScoutFormField> uiClazz = null;
          Class<? extends IFormFieldFactory> factoryClazz = null;
          try {
            modelClazz = loaderBundle.loadClass(extension.getModelClassName());
            if (!StringUtility.isNullOrEmpty(extension.getUiClassName())) {
              uiClazz = (Class<? extends IFxScoutFormField>) loaderBundle.loadClass(extension.getUiClassName());
              if (!IFxScoutFormField.class.isAssignableFrom(uiClazz)) {
                LOG.warn("extension '" + extension.getName() + "' contributed by '" + extension.getContibuterBundleId() + "' has an ui class not instanceof " + IFxScoutFormField.class.getName() + ".");
                uiClazz = null;
              }
            }
            else if (!StringUtility.isNullOrEmpty(extension.getFactoryClassName())) {
              factoryClazz = (Class<? extends IFormFieldFactory>) loaderBundle.loadClass(extension.getFactoryClassName());
              if (!IFormFieldFactory.class.isAssignableFrom(factoryClazz)) {
                LOG.warn("extension '" + extension.getName() + "' contributed by '" + extension.getContibuterBundleId() + "' has a facotry class not instanceof " + IFormFieldFactory.class.getName() + ".");
                factoryClazz = null;
              }
            }
            // Create a factory to produce the needed Field.
            IFormFieldFactory factory = null;
            if (uiClazz != null) {
              factory = new P_DirectLinkFormFieldFactory(uiClazz);
            }
            else if (factoryClazz != null) {
              try {
                factory = factoryClazz.newInstance();
              }
              catch (Exception e) {
                LOG.warn("could not create a factory instance of '" + factoryClazz.getName() + "' ", e);
              }
            }
            else {
              LOG.debug("extension '" + extension.getName() + "' contributed by '" + extension.getContibuterBundleId() + "' has neither an UiClass nor a factory defined! Skipping extension.");
              break;
            }
            // Calculate distance to the Interface IFormField. This information is needed for the CompositeObject for comparison.
            int distance = -distanceToIFormField(modelClazz, 0);
            CompositeObject key = new CompositeObject(distance, modelClazz.getName());
            if (sortedMap.containsKey(key)) {
              P_FormFieldExtension existingExt = sortedMap.get(key);
              // check scope
              if (existingExt.getFormFieldExtension().getScope() == extension.getScope()) {
                LOG.warn("The bundles '" + extension.getContibuterBundleId() + "' and '" + existingExt.getFormFieldExtension().getContibuterBundleId() + "' are both providing " + "an form field extension to '" + extension.getModelClassName() + "' with the same scope.");
              }
              else if (existingExt.getFormFieldExtension().getScope() < extension.getScope()) {
                // replace
                sortedMap.put(key, new P_FormFieldExtension(modelClazz, factory, extension));
              }
            }
            else {
              sortedMap.put(key, new P_FormFieldExtension(modelClazz, factory, extension));
            }
          }
          catch (ClassNotFoundException e) {
            LOG.debug("local extension '" + extension.getName() + "' contributed by '" + extension.getContibuterBundleId() + "' is not visible from bundle: '" + loaderBundle.getSymbolicName() + "'.");
          }
        }

      }
    }

    m_fields = new LinkedHashMap<Class<?>, IFormFieldFactory>();
    // Sorted ascending means, that IFormField is the last one because he has the value 0.
    for (P_FormFieldExtension ext : sortedMap.values()) {
      m_fields.put(ext.getModelClazz(), ext.getFactory());
    }

  }

  /**
   * Calculates the distance from the visitee to the Interface {@link IFormField}.
   * Each class between this interface and and the visitee counts as one.
   *
   * @param visitee
   *          Class to which the distance should be calculated
   * @param dist
   *          Distance to start from (0 for the first call)
   * @return Distance from {@link IFormField} to the visitee if visitee is a subclass from {@link IFormField} or 100000
   */
  private static int distanceToIFormField(Class<?> visitee, int dist) {
    if (visitee == IFormField.class) {
      return dist;
    }
    else {
      // Default distance
      int locDist = 100000;
      // Check if the visitee extends from superclasses
      Class<?> superclass = visitee.getSuperclass();
      if (superclass != null) {
        locDist = distanceToIFormField(superclass, (dist + 1));
      }
      // Check if the visitee implements interfaces
      Class[] interfaces = visitee.getInterfaces();
      if (interfaces != null) {
        for (Class<?> i : interfaces) {
          locDist = Math.min(locDist, distanceToIFormField(i, (dist + 1)));
        }
      }
      dist = locDist;
      return dist;
    }
  }

  @Override
  public IFxScoutFormField<?> createFormField(Node parent, IFormField model, IFxEnvironment environment) {
    IFormFieldFactory factory = null;
    for (Entry<Class<?>, IFormFieldFactory> link : m_fields.entrySet()) {
      // Check if the model can be assigned to a class from the set
      if (link.getKey().isAssignableFrom(model.getClass())) {
        // create instance
        factory = link.getValue();
        try {
          return factory.createFormField(parent, model, environment);
        }
        catch (Throwable e) {
          LOG.error("could not create form field for: [model = '" + model.getClass().getName() + "'; ui = '" + factory.toString() + "'].", e);
        }
      }
    }
    // If factory could not be found, check if an exception is blocking the process
    if (factory != null) {
      try {
        return factory.createFormField(parent, model, environment);
      }
      catch (Throwable t) {
        t.printStackTrace();
        return null;
      }
    }
    return null;
  }

  /**
   * Factory that produces a Field out of one Class
   */
  private class P_DirectLinkFormFieldFactory implements IFormFieldFactory {
    private final Class<? extends IFxScoutFormField> m_uiClazz;

    public P_DirectLinkFormFieldFactory(Class<? extends IFxScoutFormField> uiClazz) {
      m_uiClazz = uiClazz;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IFxScoutFormField<?> createFormField(Node parent, IFormField field, IFxEnvironment environment) {
      try {
        IFxScoutFormField newInstance = m_uiClazz.newInstance();
        newInstance.createField(field, environment);
        return newInstance;
      }
      catch (Exception e) {
        LOG.warn(null, e);
        return null;
      }
    }

    @Override
    public String toString() {
      return "DirectLinkFactory to: " + m_uiClazz.getName();
    }
  }// end class P_DirectLinkFormFieldFactory

  /**
   * Class to hold certain variables which are gathered from processing one extension.
   */
  private class P_FormFieldExtension {
    private final Class<?> m_modelClazz;
    private final IFormFieldFactory m_factory;
    private final IFormFieldExtension m_formFieldExtension;

    public P_FormFieldExtension(Class<?> modelClazz, IFormFieldFactory factory, IFormFieldExtension formFieldExtension) {
      m_modelClazz = modelClazz;
      m_factory = factory;
      m_formFieldExtension = formFieldExtension;
    }

    public Class<?> getModelClazz() {
      return m_modelClazz;
    }

    public IFormFieldFactory getFactory() {
      return m_factory;
    }

    public IFormFieldExtension getFormFieldExtension() {
      return m_formFieldExtension;
    }
  } // end class P_FormFieldExtension

}
