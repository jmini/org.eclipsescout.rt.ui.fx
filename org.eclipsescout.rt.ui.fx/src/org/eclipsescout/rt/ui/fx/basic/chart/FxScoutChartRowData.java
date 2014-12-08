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
package org.eclipsescout.rt.ui.fx.basic.chart;

import java.util.Arrays;

/**
 *
 */
public class FxScoutChartRowData {
  private String rowName;
  private Number[] rowValues;
  private Number total;

  /**
   * @param rowName
   * @param rowValues
   */
  public FxScoutChartRowData(String rowName, Number[] rowValues) {
    super();
    this.rowName = rowName;
    this.rowValues = rowValues;
  }

  public String getRowName() {
    return rowName;
  }

  public void setRowName(String rowName) {
    this.rowName = rowName;
  }

  public Number[] getRowValues() {
    return rowValues;
  }

  public void setRowValues(Number[] rowValues) {
    this.rowValues = rowValues;
  }

  public Number getTotal() {
    return total;
  }

  public void setTotal(Number total) {
    this.total = total;
  }

  @Override
  public String toString() {
    return "FxScoutChartRowData [rowName=" + rowName + ", rowValues=" + Arrays.toString(rowValues) + ", total=" + total + "]";
  }

}
