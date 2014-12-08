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
import javafx.scene.chart.BarChart;

import org.eclipsescout.rt.ui.fx.IFxEnvironment;
import org.eclipsescout.rt.ui.fx.basic.chart.FxScoutChartRowData;

/**
 *
 */
public class FxScoutBarChartFactory extends AbstractFxScoutXYChartFactory {

  public static final String FactoryName = "BarChart";

  public FxScoutBarChartFactory(ObservableList<FxScoutChartRowData> data, Collection<String> columns, ChartProperties chartProperties, IFxEnvironment fxEnvironment) {
    super(data, columns, chartProperties, fxEnvironment);
  }

  @Override
  protected void buildChart() {
    this.setChart(new BarChart<String, Number>(getxAxis(), getyAxis(), getChartData()));
  }

  @Override
  public String getName() {
    return FxScoutBarChartFactory.FactoryName;
  }

}
