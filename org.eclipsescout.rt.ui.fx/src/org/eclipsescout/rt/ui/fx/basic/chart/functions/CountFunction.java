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

import java.util.List;

/**
 *
 */
public class CountFunction implements IFunction {

  @Override
  public String getName() {
    return "FxChartCountFunction";
  }

  @Override
  public Number calculate(List<Object> list, Class<?> dataType) {
    return calculate(list);
  }

  @Override
  public Number calculate(List<Object> list) {
    return list.size();
  }

}
