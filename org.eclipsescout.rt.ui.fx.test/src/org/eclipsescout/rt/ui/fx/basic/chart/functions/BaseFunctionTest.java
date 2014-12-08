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

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class BaseFunctionTest {

  private final String cvsSplitBy = ";";
  private final float MAX_FLOAT_DIFFERENCE = 0.1f;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testEmptyList() {
    List<Object> data = new ArrayList<Object>();

    IFunction function = new CountFunction();
    Number res = function.calculate(data, getDataType());
    Assert.assertEquals(0, res);

    function = new SumFunction();
    res = function.calculate(data, getDataType());
    assertEquals(convertSumRes("0"), res);

    function = new AvgFunction();
    res = function.calculate(data, getDataType());
    Assert.assertNull(res);

    function = new MinFunction();
    res = function.calculate(data, getDataType());
    Assert.assertNull(res);

    function = new MaxFunction();
    res = function.calculate(data, getDataType());
    Assert.assertNull(res);
  }

  @Test
  public void testCalculate() {
    BufferedReader br = null;
    String line = null;

    try {

      br = new BufferedReader(new FileReader("res/chart/" + getFilename() + ".csv"));
      br.readLine();
      while ((line = br.readLine()) != null) {

        String[] column = line.split(cvsSplitBy);

        List<Object> data = new ArrayList<Object>();
        for (int i = 0; i < 4; i++) {
          data.add(convertInput(column[i]));
        }

        int counter = 4;

        IFunction function = new CountFunction();
        Number res = function.calculate(data, getDataType());
        assertEquals(convertCountRes(column[counter++]), res);

        function = new SumFunction();
        res = function.calculate(data, getDataType());
        assertEquals(convertSumRes(column[counter++]), res);

        function = new AvgFunction();
        res = function.calculate(data, getDataType());
        assertEquals(convertAvgRes(column[counter++]), res);

        function = new MinFunction();
        res = function.calculate(data, getDataType());
        assertEquals(convertMinRes(column[counter++]), res);

        function = new MaxFunction();
        res = function.calculate(data, getDataType());
        assertEquals(convertMaxRes(column[counter++]), res);
      }

    }
    catch (Exception e) {
      Assert.fail(e.getMessage());
    }
    finally {
      if (br != null) {
        try {
          br.close();
        }
        catch (Exception e) {
          Assert.fail(e.getMessage());
        }
      }
    }
  }

  private void assertEquals(Object expected, Object actual) {
    if (expected instanceof Double) {
      Assert.assertEquals(((Double) expected).doubleValue(), ((Double) actual).doubleValue(), MAX_FLOAT_DIFFERENCE);
    }
    else if (expected instanceof Float) {
      Assert.assertEquals(((Float) expected).floatValue(), ((Float) actual).floatValue(), MAX_FLOAT_DIFFERENCE);
    }
    else if (expected instanceof BigDecimal) {
      BigDecimal b1 = (BigDecimal) expected;
      BigDecimal b2 = (BigDecimal) expected;
      Assert.assertEquals(0, b1.compareTo(b2));
    }
    else {
      Assert.assertEquals(expected, actual);
    }
  }

  protected abstract String getFilename();

  protected abstract Class<?> getDataType();

  protected abstract Object convertInput(String value);

  protected abstract Number convertCountRes(String value);

  protected abstract Number convertSumRes(String value);

  protected abstract Number convertAvgRes(String value);

  protected abstract Number convertMinRes(String value);

  protected abstract Number convertMaxRes(String value);

}
