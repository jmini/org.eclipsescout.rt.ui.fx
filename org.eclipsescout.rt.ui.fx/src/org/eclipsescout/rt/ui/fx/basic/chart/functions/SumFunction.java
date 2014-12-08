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
public class SumFunction extends AbstractFunction {

  @Override
  public String getName() {
    return "FxChartSumFunction";
  }

  /**
   * @param list
   * @return
   */
  @Override
  protected Number calculateShort(List<Object> list) {
    long result = 0;
    for (Object o : list) {
      result = result + (Short) o;
    }
    return result;
  }

  /**
   * @param list
   * @return
   */
  @Override
  protected Number calculateByte(List<Object> list) {
    long result = 0;
    for (Object o : list) {
      result += (Byte) o;
    }
    return result;
  }

  /**
   * @param list
   * @return
   */
  @Override
  protected Number calculateInteger(List<Object> list) {
    long result = 0;
    for (Object o : list) {
      if (o != null) {
        result += (Integer) o;
      }
    }
    return result;
  }

  /**
   * @param list
   * @return
   */
  @Override
  protected Number calculateLong(List<Object> list) {
    long result = 0L;
    for (Object o : list) {
      if (o != null) {
        result += (Long) o;
      }
    }
    return result;
  }

  /**
   * @param list
   * @return
   */
  @Override
  protected Number calculateFloat(List<Object> list) {
    float result = 0;
    for (Object o : list) {
      if (o != null) {
        result += (Float) o;
      }
    }
    return result;
  }

  /**
   * @param list
   * @return
   */
  @Override
  protected Number calculateDouble(List<Object> list) {
    double result = 0;
    for (Object o : list) {
      if (o != null) {
        result += (Double) o;
      }
    }
    return result;
  }

  /**
   * @param list
   * @return
   */
  @Override
  protected Number calculateBigInteger(List<Object> list) {
    BigInteger result = new BigInteger("0");
    for (Object o : list) {
      if (o != null) {
        result = result.add((BigInteger) o);
      }
    }
    return result;
  }

  /**
   * @param list
   * @return
   */
  @Override
  protected Number calculateBigDecimal(List<Object> list) {
    BigDecimal result = new BigDecimal("0");
    for (Object o : list) {
      if (o != null) {
        result = result.add((BigDecimal) o);
      }
    }
    return result;
  }

}
