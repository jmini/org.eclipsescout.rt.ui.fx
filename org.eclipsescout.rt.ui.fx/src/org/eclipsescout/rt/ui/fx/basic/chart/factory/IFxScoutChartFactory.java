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
package org.eclipsescout.rt.ui.fx.basic.chart.factory;

import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.scene.chart.Chart;
import javafx.scene.layout.Pane;

import org.eclipsescout.rt.ui.fx.basic.chart.FxScoutChartRowData;

/**
 *
 */
public interface IFxScoutChartFactory {
  public Chart getChart();

  public ChartProperties getChartProperties();

  public Pane getControlPanel();

  public String getName();

  public void rebuildFromData(ObservableList<FxScoutChartRowData> data, Collection<String> columnTitles);

}
