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
public class MinFunction extends AbstractFunction {

  @Override
  public String getName() {
    return "FxChartMinFunction";
  }

  @Override
  protected Number calculateShort(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }

    short min = (Short) list.get(0);
    for (int i = 1; i < list.size(); i++) {
      short s = (Short) list.get(i);
      if (s < min) {
        min = s;
      }
    }
    return min;
  }

  @Override
  protected Number calculateByte(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }

    byte min = (Byte) list.get(0);
    for (int i = 1; i < list.size(); i++) {
      byte s = (Byte) list.get(i);
      if (s < min) {
        min = s;
      }
    }
    return min;
  }

  @Override
  protected Number calculateInteger(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }

    int min = (Integer) list.get(0);
    for (int i = 1; i < list.size(); i++) {
      int s = (Integer) list.get(i);
      if (s < min) {
        min = s;
      }
    }
    return min;
  }

  @Override
  protected Number calculateLong(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }

    long min = (Long) list.get(0);
    for (int i = 1; i < list.size(); i++) {
      long s = (Long) list.get(i);
      if (s < min) {
        min = s;
      }
    }
    return min;
  }

  @Override
  protected Number calculateFloat(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }

    float min = (Float) list.get(0);
    for (int i = 1; i < list.size(); i++) {
      float s = (Float) list.get(i);
      if (s < min) {
        min = s;
      }
    }
    return min;
  }

  @Override
  protected Number calculateDouble(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }

    double min = (Double) list.get(0);
    for (int i = 1; i < list.size(); i++) {
      double s = (Double) list.get(i);
      if (s < min) {
        min = s;
      }
    }
    return min;
  }

  @Override
  protected Number calculateBigInteger(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }

    BigInteger min = (BigInteger) list.get(0);
    for (int i = 1; i < list.size(); i++) {
      BigInteger bi = (BigInteger) list.get(i);
      if (min.compareTo(bi) > 0) {
        min = bi;
      }
    }
    return min;
  }

  @Override
  protected Number calculateBigDecimal(List<Object> list) {
    if (list.size() == 0) {
      return null;
    }

    BigDecimal min = (BigDecimal) list.get(0);
    for (int i = 1; i < list.size(); i++) {
      BigDecimal bi = (BigDecimal) list.get(i);
      if (min.compareTo(bi) > 0) {
        min = bi;
      }
    }
    return min;
  }

}
