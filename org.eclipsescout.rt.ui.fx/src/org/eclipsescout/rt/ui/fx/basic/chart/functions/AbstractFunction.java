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
public abstract class AbstractFunction implements IFunction {

  /**
   *
   */
  public AbstractFunction() {
    super();
  }

  @Override
  public Number calculate(List<Object> list) {
    Class<?> dataType = null;
    for (Object object : list) {
      if (object != null) {
        dataType = object.getClass();
      }
    }
    if (dataType == null) {
      return null;
    }
    return calculate(list, dataType);
  }

  @Override
  public final Number calculate(List<Object> list, Class<?> dataType) {
    if (!Number.class.isAssignableFrom(dataType)) {
      return null;
    }
    Number result = null;
    if (dataType == Long.class) {
      result = calculateLong(list);
    }
    else if (dataType == Short.class) {
      result = calculateShort(list);
    }
    else if (dataType == Integer.class) {
      result = calculateInteger(list);
    }
    else if (dataType == Byte.class) {
      result = calculateByte(list);
    }
    else if (dataType == Double.class) {
      result = calculateDouble(list);
    }
    else if (dataType == Float.class) {
      result = calculateFloat(list);
    }
    else if (dataType == BigInteger.class) {
      result = calculateBigInteger(list);
    }
    else if (dataType == BigDecimal.class) {
      result = calculateBigDecimal(list);
    }
    return result;
  }

  protected abstract Number calculateShort(List<Object> list);

  protected abstract Number calculateByte(List<Object> list);

  protected abstract Number calculateInteger(List<Object> list);

  protected abstract Number calculateLong(List<Object> list);

  protected abstract Number calculateFloat(List<Object> list);

  protected abstract Number calculateDouble(List<Object> list);

  protected abstract Number calculateBigInteger(List<Object> list);

  protected abstract Number calculateBigDecimal(List<Object> list);

}
