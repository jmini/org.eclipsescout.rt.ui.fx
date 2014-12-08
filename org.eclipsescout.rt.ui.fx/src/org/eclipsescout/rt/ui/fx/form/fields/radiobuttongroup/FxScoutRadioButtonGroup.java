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
package org.eclipsescout.rt.ui.fx.form.fields.radiobuttongroup;

import java.util.List;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import org.eclipse.scout.rt.client.ui.form.fields.GridData;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.radiobuttongroup.IRadioButtonGroup;
import org.eclipsescout.rt.ui.fx.LogicalGridPane;
import org.eclipsescout.rt.ui.fx.ext.FxStatusPaneEx;
import org.eclipsescout.rt.ui.fx.form.fields.FxScoutValueFieldComposite;
import org.eclipsescout.rt.ui.fx.form.fields.IFxScoutFormField;

/**
 *
 */
public class FxScoutRadioButtonGroup extends FxScoutValueFieldComposite<IRadioButtonGroup<?>> implements IFxScoutRadioButtonGroup {

  @Override
  protected void initialize() {
    LogicalGridPane container = new LogicalGridPane(getFxEnvironment(), 1, 0);
    FxStatusPaneEx label = getFxEnvironment().createStatusLabel(getScoutObject());
    container.getChildren().add(label);

    int hgap = 0;
    int vgap = 0;

    GridData scoutGridData = getScoutObject().getGridData();
    boolean usesLogicalGrid = (getScoutObject().getGridRowCount() == scoutGridData.h && !scoutGridData.useUiHeight);
    if (usesLogicalGrid) {
      hgap = getFxEnvironment().getFormColumnGap();
      vgap = getFxEnvironment().getFormRowGap();
    }

    // TODO: fix layout
//    RadioButtonGroupLayout buttonPane = new RadioButtonGroupLayout(getFxEnvironment(), hgap, vgap);
    HBox buttonPane = new HBox(hgap);

    // add all radio buttons
    List<IFormField> scoutFields = getScoutObject().getFields();
    for (int i = 0; i < scoutFields.size(); i++) {
      IFxScoutFormField comp = getFxEnvironment().createFormField(buttonPane, scoutFields.get(i));
      buttonPane.getChildren().add(comp.getFxContainer());
    }
    container.getChildren().add(buttonPane);

    setFxStatusPane(label);
    setFxField(buttonPane);
    setFxContainer(container);

  }

  @Override
  public Pane getFxRadioButtonPane() {
    return (Pane) getFxField();
  }
}
