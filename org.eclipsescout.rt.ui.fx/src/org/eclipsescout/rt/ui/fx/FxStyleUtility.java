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

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.Node;

import org.eclipse.scout.rt.shared.data.basic.FontSpec;

/**
 * Utility to define and set the css-style of a javafx ui-field.
 */
public class FxStyleUtility {

  /**
   * Key to store and fetch the map from the client properties of a field,
   * where all the style informations are stored.
   */
  public static final String CLIENT_PROPERTY_NAME = FxStyleUtility.class.getName();

  public static void setBackgroundColor(Node node, String color) {
    if (color != null) {
      color = "#" + color;
    }
    setStyle(node, "-fx-background-color", color);
  }

  public static void setTextColor(Node node, String color) {
    if (color != null) {
      color = "#" + color;
    }
    setStyle(node, "-fx-text-fill", color);
  }

  public static void setFontFamily(Node node, String family) {
    setStyle(node, "-fx-font-family", family);
  }

  public static void setFontSize(Node node, Double size) {
    setStyle(node, "-fx-font-size", size);
  }

  public static void setFontWeight(Node node, String weight) {
    setStyle(node, "-fx-font-weight", weight);
  }

  public static void setFontStyle(Node node, String style) {
    setStyle(node, "-fx-font-style", style);
  }

  public static void setOpacity(Node node, double opacity) {
    setStyle(node, "-fx-opacity", opacity);
  }

  public static void setTextFont(Node node, FontSpec scoutFont) {
    Double fontSize = null;
    String fontFamily = null;
    String fontStyle = null;
    String fontWeight = null;
    if (scoutFont != null) {
      if (scoutFont.getSize() != 0) {
        fontSize = (double) scoutFont.getSize();
      }
      if (scoutFont.getName() != null) {
        fontFamily = scoutFont.getName();
      }
      if (scoutFont.isItalic()) {
        fontStyle = "italic";
      }
      else {
        // TODO: check if else can be removed
        fontStyle = "normal";
      }
      if (scoutFont.isBold()) {
        fontWeight = "bold";
      }
      else {
        // TODO: check if else can be removed
        fontWeight = "normal";
      }
    }
    setFontSize(node, fontSize);
    setFontFamily(node, fontFamily);
    setFontStyle(node, fontStyle);
    setFontWeight(node, fontWeight);
  }

  /**
   * Sets the color of the border. Color should have a # as first character or should be the name of a colors.
   *
   * @param node
   * @param color
   */
  public static void setBorderColor(Node node, String color) {
    setStyle(node, "-fx-border-color", color);
  }

  /**
   * Sets customized color for each site of the border. Colors should have a # as first character or should be names of
   * colors.
   *
   * @param node
   * @param colTop
   * @param colRight
   * @param colBottom
   * @param colLeft
   */
  public static void setBorderColor(Node node, String colTop, String colRight, String colBottom, String colLeft) {
    setStyle(node, "-fx-border-color", colTop + ", " + colRight + ", " + colBottom + ", " + colLeft);
  }

  public static void setBorderInsets(Node node, Insets insets) {
    String properties = null;
    if (insets != null) {
      properties = insets.getTop() + ", " + insets.getRight() + ", " + insets.getBottom() + ", " + insets.getLeft();
    }
    setStyle(node, "-fx-border-insets", properties);
  }

  public static void setPadding(Node node, Insets insets) {
    String properties = null;
    if (insets != null) {
      properties = insets.getTop() + ", " + insets.getRight() + ", " + insets.getBottom() + ", " + insets.getLeft();
    }
    setStyle(node, "-fx-padding", properties);
  }

  public static void setPadding(Node node, double topRightBottomLeft) {
    setStyle(node, "-fx-padding", topRightBottomLeft);
  }

  /**
   * Gets a map from the client property in which the style informations are stored.
   * Manipulates this property according to the parameters.
   * Stores this map in the client property.
   * Parses this map to a string and sets this string as the new css-style on the node.
   *
   * @param node
   *          JavaFX ui-element
   * @param property
   *          property to be changed
   * @param value
   *          new value of the property
   */
  private static void setStyle(Node node, String property, Object value) {
    @SuppressWarnings("unchecked")
    Map<String, Object> styles = (Map<String, Object>) node.getProperties().get(CLIENT_PROPERTY_NAME);
    if (styles == null) {
      styles = new HashMap<String, Object>(10);
    }

    if (value == null) {
      styles.remove(property);
    }
    else {
      styles.put(property, value);
    }

    node.getProperties().put(CLIENT_PROPERTY_NAME, styles);

    StringBuffer sb = new StringBuffer(20 * styles.size());
    for (Map.Entry<String, Object> entry : styles.entrySet()) {
      sb.append(entry.getKey() + ":" + entry.getValue() + ";");
    }
    node.setStyle(sb.toString());
  }

}
