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
package org.eclipsescout.rt.ui.fx.form.fields.groupbox;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.button.IButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.IGroupBox;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;
import org.eclipsescout.rt.ui.fx.form.fields.IFxScoutFormField;
import org.eclipsescout.rt.ui.fx.layout.BorderPaneEx;
import org.eclipsescout.rt.ui.fx.layout.HBoxEx;
import org.eclipsescout.rt.ui.fx.layout.HorizontalAlignment;

/**
 * 
 */
public class FxScoutGroupBoxButtonBar implements IFxScoutGroupBoxButtonBar {
  private IGroupBox m_scoutGroupBox;
  private IFxEnvironment m_environment;

  private BorderPane m_fxContainer;
  private Pane m_leftButtonPart;
  private Pane m_rightButtonPart;

  public void createField(IGroupBox scoutGroupBox, IFxEnvironment env) {
    m_scoutGroupBox = scoutGroupBox;
    m_environment = env;
    m_fxContainer = new BorderPaneEx(4, 0);

    m_leftButtonPart = new HBoxEx(HorizontalAlignment.LEFT, 12);
    m_rightButtonPart = new HBoxEx(HorizontalAlignment.RIGHT, 12);
    m_fxContainer.setLeft(m_leftButtonPart);
    m_fxContainer.setRight(m_rightButtonPart);

    // buttons
    for (IFormField f : m_scoutGroupBox.getFields()) {
      if (f instanceof IButton) {
        IButton b = (IButton) f;
        if (b.isProcessButton()) {
          if (b.getGridData().horizontalAlignment <= 0) {
            IFxScoutFormField<IFormField> fxScoutComposite = m_environment.createFormField(m_leftButtonPart, b);
            m_leftButtonPart.getChildren().add(fxScoutComposite.getFxContainer());
          }
          else {
            IFxScoutFormField<IFormField> fxScoutComposite = m_environment.createFormField(m_rightButtonPart, b);
            m_rightButtonPart.getChildren().add(fxScoutComposite.getFxContainer());
          }
        }
      }
    }
  }

  @Override
  public Pane getFxContainer() {
    return m_fxContainer;
  }

  @Override
  public Pane getLeftButtonPart() {
    return m_leftButtonPart;
  }

  @Override
  public Pane getRightButtonPart() {
    return m_rightButtonPart;
  }

  @Override
  public IGroupBox getScoutGroupBox() {
    return m_scoutGroupBox;
  }

}
