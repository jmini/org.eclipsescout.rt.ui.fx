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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Side;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * This class stores all properties, which are used in the charts.
 */
public class ChartProperties {

  public enum AxisName {
    X, Y
  }

  public final AxisProperties yAxisProperties = new AxisProperties(AxisName.Y);
  public final AxisProperties xAxisProperties = new AxisProperties(AxisName.X);

  public final ObjectProperty<Side> legendSideProperty = new SimpleObjectProperty<Side>(Side.BOTTOM);
  public final BooleanProperty legendVisibleProperty = new SimpleBooleanProperty(true);
  public final StringProperty titleProperty = new SimpleStringProperty("");
  public final ObjectProperty<Side> titleSideProperty = new SimpleObjectProperty<Side>(Side.TOP);

  public final BooleanProperty horizontalGridLinesVisibleProperty = new SimpleBooleanProperty(true);
  public final BooleanProperty horizontalZeroLineVisibleProperty = new SimpleBooleanProperty(true);
  public final BooleanProperty verticalGridLinesVisibleProperty = new SimpleBooleanProperty(true);
  public final BooleanProperty verticalZeroLineVisibleProperty = new SimpleBooleanProperty(true);

  public final BooleanProperty axisXAutoRangingProperty = new SimpleBooleanProperty(true);
  public final ObjectProperty<Side> axisXSideProperty = new SimpleObjectProperty<Side>(Side.BOTTOM);
  public final DoubleProperty axisXTickLabelGapProperty = new SimpleDoubleProperty(3.0);
  public final BooleanProperty axisXTickLabelsVisibleProperty = new SimpleBooleanProperty(true);
  public final DoubleProperty axisXTickLengthProperty = new SimpleDoubleProperty(8.0);
  public final BooleanProperty axisXTickMarkVisibleProperty = new SimpleBooleanProperty(true);
  public final DoubleProperty axisXTickLabelRotationProperty = new SimpleDoubleProperty(0.0);

  public final BooleanProperty axisYAutoRangingProperty = new SimpleBooleanProperty(true);
  public final ObjectProperty<Side> axisYSideProperty = new SimpleObjectProperty<Side>(Side.LEFT);
  public final DoubleProperty axisYTickLabelGapProperty = new SimpleDoubleProperty(3.0);
  public final BooleanProperty axisYTickLabelsVisibleProperty = new SimpleBooleanProperty(true);
  public final DoubleProperty axisYTickLengthProperty = new SimpleDoubleProperty(8.0);
  public final BooleanProperty axisYTickMarkVisibleProperty = new SimpleBooleanProperty(true);
  public final DoubleProperty axisYTickLabelRotationProperty = new SimpleDoubleProperty(0.0);

  public final BooleanProperty clockwiseProperty = new SimpleBooleanProperty(true);
  public final BooleanProperty labelsVisibleProperty = new SimpleBooleanProperty(true);
  public final DoubleProperty labelLineLengthProperty = new SimpleDoubleProperty(20.0);
  public final DoubleProperty startAngleProperty = new SimpleDoubleProperty(0.0);

  @SuppressWarnings("unchecked")
  protected void bindToSettings(Property setting, Property chart, Property control) {
    setting.unbind();
    if (!control.isBound()) {
      control.setValue(setting.getValue());
    }
    setting.bind(control);
    chart.bind(setting);
  }

  public AxisProperties getAxisProperties(AxisName axis) {
    switch (axis) {
      case X:
        return xAxisProperties;
      case Y:
        return yAxisProperties;
      default:
        return null;
    }
  }

  public class AxisProperties {

    public final BooleanProperty autoRangingProperty = new SimpleBooleanProperty(true);
    public final ObjectProperty<Side> sideProperty = new SimpleObjectProperty<Side>();
    public final DoubleProperty tickLabelGapProperty = new SimpleDoubleProperty(3.0);
    public final BooleanProperty tickLabelsVisibleProperty = new SimpleBooleanProperty(true);
    public final DoubleProperty tickLengthProperty = new SimpleDoubleProperty(8.0);
    public final BooleanProperty tickMarkVisibleProperty = new SimpleBooleanProperty(true);
    public final DoubleProperty tickLabelRotationProperty = new SimpleDoubleProperty(0.0);
    public final ObjectProperty<Paint> tickLabelFillProperty = new SimpleObjectProperty<Paint>();

    public AxisProperties(AxisName axis) {
      switch (axis) {
        case X:
          sideProperty.setValue(Side.BOTTOM);
          break;
        case Y:
          sideProperty.setValue(Side.LEFT);
          break;
        default:
          break;
      }
      tickLabelFillProperty.setValue(Color.BLACK);
    }
  }

}
