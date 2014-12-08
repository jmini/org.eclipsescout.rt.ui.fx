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
package org.eclipsescout.rt.ui.fx;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;

/**
 *
 */
public final class FxLayoutUtility {
  public static final int MIN = 0;
  public static final int PREF = 1;
  public static final int MAX = 2;

  private static int textFieldTopInset = 0;

  public static int getTextFieldTopInset() {
    return textFieldTopInset;
  }

  /**
   * Calculates min, max and pref sizes for a given node.
   * The LayoutBoundsProperty of this node will be used as default-value
   * for the calculation.
   *
   * @param node
   * @return heights and widths in a double[][]. Position 0 contains the heights and Position 1 the widths.
   */
  public static double[][] getValidatedSizes(Node node) {
    double defaultH = node.layoutBoundsProperty().getValue().getHeight();
    double defaultW = node.layoutBoundsProperty().getValue().getWidth();
    double[] heights = new double[]{
        node.minHeight(defaultW),
        node.prefHeight(defaultW),
        node.maxHeight(defaultW)
    };
    double[] widths = new double[]{
        node.minWidth(defaultH),
        node.prefWidth(defaultH),
        node.maxWidth(defaultH)
    };
    // validation
    if (heights[MIN] > heights[PREF]) {
      heights[MIN] = heights[PREF];
    }
    if (heights[MIN] > heights[PREF]) {
      heights[MIN] = heights[PREF];
    }
    if (heights[MAX] < heights[PREF]) {
      heights[MAX] = heights[PREF];
    }
    if (heights[MAX] < heights[PREF]) {
      heights[MAX] = heights[PREF];
    }
    return new double[][]{heights, widths};
  }

  /**
   * @param scoutAlign
   * @return
   */
  public static HPos createHorizontalAlignment(int scoutAlign) {
    switch (scoutAlign) {
      case -1: {
        return HPos.LEFT;
      }
      case 0: {
        return HPos.CENTER;
      }
      default: { // case: 1
        return HPos.RIGHT;
      }
    }
  }

  /**
   * @param scoutAlign
   * @return
   */
  public static VPos createVerticalAlignment(int scoutAlign) {
    switch (scoutAlign) {
      case 0: {
        return VPos.CENTER;
      }
      case 1: {
        return VPos.BOTTOM;
      }
      default: { // case: -1
        return VPos.TOP;
      }
    }
  }

  /**
   * @param vPos
   * @param hPos
   * @return
   */
  public static Pos createAlignment(VPos vPos, HPos hPos) {
    Pos pos = Pos.CENTER;
    switch (vPos) {
      case TOP:
        switch (hPos) {
          case LEFT:
            pos = Pos.TOP_LEFT;
            break;
          case CENTER:
            pos = Pos.TOP_CENTER;
            break;
          case RIGHT:
            pos = Pos.TOP_RIGHT;
            break;
        }
        break;
      case CENTER:
        switch (hPos) {
          case LEFT:
            pos = Pos.CENTER_LEFT;
            break;
          case CENTER:
            pos = Pos.CENTER;
            break;
          case RIGHT:
            pos = Pos.CENTER_RIGHT;
            break;
        }
        break;
      case BASELINE:
        switch (hPos) {
          case LEFT:
            pos = Pos.BASELINE_LEFT;
            break;
          case CENTER:
            pos = Pos.BASELINE_CENTER;
            break;
          case RIGHT:
            pos = Pos.BASELINE_RIGHT;
            break;
        }
        break;
      case BOTTOM:
        switch (hPos) {
          case LEFT:
            pos = Pos.BOTTOM_LEFT;
            break;
          case CENTER:
            pos = Pos.BOTTOM_CENTER;
            break;
          case RIGHT:
            pos = Pos.BOTTOM_RIGHT;
            break;
        }
        break;
    }
    return pos;
  }

}
