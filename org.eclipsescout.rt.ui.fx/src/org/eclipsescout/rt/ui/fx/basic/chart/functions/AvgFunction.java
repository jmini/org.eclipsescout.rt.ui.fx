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
package org.eclipsescout.rt.ui.fx.basic.chart.functions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 *
 */
public class AvgFunction extends SumFunction {

  @Override
  public String getName() {
    return "FxChartAvgFunction";
  }

  @Override
  protected Number calculateLong(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }
    long res = (Long) super.calculateLong(list);
    return res / (double) list.size();
  }

  @Override
  protected Number calculateShort(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }
    long res = (Long) super.calculateShort(list);
    return res / (double) list.size();
  }

  @Override
  protected Number calculateInteger(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }
    long res = (Long) super.calculateInteger(list);
    return res / (double) list.size();
  }

  @Override
  protected Number calculateByte(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }
    long res = (Long) super.calculateByte(list);
    return res / (double) list.size();
  }

  @Override
  protected Number calculateDouble(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }
    double res = (Double) super.calculateDouble(list);
    return res / list.size();
  }

  @Override
  protected Number calculateFloat(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }
    float res = (Float) super.calculateFloat(list);
    return res / (double) list.size();
  }

  @Override
  protected Number calculateBigInteger(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }
    BigDecimal res = new BigDecimal((BigInteger) super.calculateBigInteger(list));
    return res.divide(new BigDecimal("" + list.size()));
  }

  @Override
  protected Number calculateBigDecimal(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }
    BigDecimal res = (BigDecimal) super.calculateBigDecimal(list);
    return res.divide(new BigDecimal("" + list.size()));
  }

}
