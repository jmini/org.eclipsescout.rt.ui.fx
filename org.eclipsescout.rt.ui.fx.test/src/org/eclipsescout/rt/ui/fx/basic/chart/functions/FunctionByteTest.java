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

/**
 *
 */
public class FunctionByteTest extends BaseFunctionTest {

  @Override
  protected String getFilename() {
    return "chart_function_test_data_byte";
  }

  @Override
  protected Class<?> getDataType() {
    return Byte.class;
  }

  @Override
  protected Object convertInput(String value) {
    return Byte.valueOf(value);
  }

  @Override
  protected Number convertCountRes(String value) {
    return Integer.valueOf(value);
  }

  @Override
  protected Number convertSumRes(String value) {
    return Long.valueOf(value);
  }

  @Override
  protected Number convertAvgRes(String value) {
    return Double.valueOf(value);
  }

  @Override
  protected Number convertMinRes(String value) {
    return Byte.valueOf(value);
  }

  @Override
  protected Number convertMaxRes(String value) {
    return Byte.valueOf(value);
  }
}
