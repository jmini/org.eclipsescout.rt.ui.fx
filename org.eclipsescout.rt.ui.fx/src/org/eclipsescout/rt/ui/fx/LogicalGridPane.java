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

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;

/**
 * Dynamic layout using logical grid data {@link LogicalGridData} to arrange
 * fields. The grid data per field can be passed when adding the node to
 * the pane {@link LogicalGridPane#add(Node, LogicalGridData)} or set as client property with name
 * {@link LogicalGridData#CLIENT_PROPERTY_NAME}.
 */
public class LogicalGridPane extends Pane {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(LogicalGridPane.class);

  public static final float EPS = 1E-6f;
  public static final int MIN_SIZE = 0;
  public static final int PREF_SIZE = 1;
  public static final int MAX_SIZE = 2;

  private LogicalGridPaneInfo m_info;
  private IFxEnvironment m_env;
  private int m_hgap;
  private int m_vgap;
  private boolean m_dirty = true;
  private boolean m_debug = false;

  /**
   *
   */
  public LogicalGridPane(IFxEnvironment env, int hgap, int vgap) {
    super();
    //LOG.setLevel(IScoutLogger.LEVEL_DEBUG);
    m_env = env;
    m_hgap = hgap;
    m_vgap = vgap;
    if (LOG.isDebugEnabled()) {
      setStyle("-fx-background-color:" + getRandomColor() + "; -fx-border-color:" + getRandomColor() + "; -fx-border-width: 1px;");
    }

  }

  // TODO: remove
  private String getRandomColor() {
    String[] letters = "0123456789ABCDEF".split("");
    String color = "#";
    for (int i = 0; i < 6; i++) {
      color += letters[(int) (Math.random() * letters.length)];
    }
    return color;
  }

  public void setDebug(boolean b) {
    m_debug = b;
  }

  /**
   * Adds the node as child to this pane and adds the specified LogicalGridData to the properties of the node.
   *
   * @param node
   *          Node to add
   * @param logicalGridData
   *          LogicalGridData to set in the node
   */
  public void add(Node node, LogicalGridData logicalGridData) {
    node.getProperties().put(LogicalGridData.CLIENT_PROPERTY_NAME, logicalGridData);
    super.getChildren().add(node);
  }

  @Override
  protected double computeMinHeight(double width) {
    return computeHeight(MIN_SIZE);
  }

  @Override
  protected double computePrefHeight(double width) {
    return computeHeight(PREF_SIZE);
  }

  @Override
  protected double computeMaxHeight(double width) {
//    return Double.MAX_VALUE;
    return computeHeight(MAX_SIZE);
  }

  private double computeHeight(int sizeflag) {
    validateLayout();
    double height = 0;
    int useCount = 0;
    for (int i = 0; i < m_info.rows; i++) {
      double h = m_info.height[i][sizeflag];
      if (useCount > 0) {
        height += m_vgap;
      }
      height += h;
      useCount++;
    }
    if (height > 0) {
      Insets insets = getInsets();
      height += insets.getTop() + insets.getBottom();
    }
    return height;
  }

  @Override
  protected double computeMinWidth(double height) {
    return computeWidth(MIN_SIZE);
  }

  @Override
  protected double computePrefWidth(double height) {
    return computeWidth(PREF_SIZE);
  }

  @Override
  protected double computeMaxWidth(double height) {
//    return Double.MAX_VALUE;
    return computeWidth(MAX_SIZE);
  }

  private double computeWidth(int sizeflag) {
    validateLayout();
    double width = 0;
    int useCount = 0;
    for (int i = 0; i < m_info.cols; i++) {
      double w = m_info.width[i][sizeflag];
      if (useCount > 0) {
        width += m_hgap;
      }
      width += w;
      useCount++;
    }
    if (width > 0) {
      Insets insets = getInsets();
      width += insets.getLeft() + insets.getRight();
    }
    return width;
  }

  private void validateLayout() {
    if (true) {
      ArrayList<Node> visibleNodes = new ArrayList<Node>();
      ArrayList<LogicalGridData> visibleCons = new ArrayList<LogicalGridData>();

      for (Node node : getChildren()) {
        node.autosize(); // necessary to get default size
        if (node.isVisible()) {
          visibleNodes.add(node);
          LogicalGridData cons = getLayoutDataByRef(node);
          cons.validate();
          visibleCons.add(cons);
        }
      }
      m_info = new LogicalGridPaneInfo(m_env, visibleNodes.toArray(new Node[visibleNodes.size()]), visibleCons.toArray(new LogicalGridData[visibleCons.size()]), m_hgap, m_vgap);
      m_dirty = false;
    }
  }

  /**
   * @param node
   * @return
   */
  private LogicalGridData getLayoutDataByRef(Node node) {
    LogicalGridData data = (LogicalGridData) node.getProperties().get(LogicalGridData.CLIENT_PROPERTY_NAME);
    if (data == null) {
      data = new LogicalGridData();
      LOG.error("missing clientProperty " + LogicalGridData.CLIENT_PROPERTY_NAME + " in " + node + " parent is " + node.getParent());
    }
    return data;
  }

  @Override
  protected void layoutChildren() {
    if (m_debug) {
      dumpLayoutInfo();
    }

    // sizes of all cells
    Bounds[][] cellBounds = m_info.layoutCellBounds(getWidth(), getHeight(), getInsets());

    // bounds
    int n = m_info.nodes.length;
    for (int i = 0; i < n; i++) {
      Node node = m_info.nodes[i];
      LogicalGridData data = m_info.gridDatas[i];
      Bounds b1 = cellBounds[data.gridy][data.gridx];
      Bounds b2 = cellBounds[data.gridy + data.gridh - 1][data.gridx + data.gridw - 1];
      double x = (b1.getMinX() < b2.getMinX()) ? b1.getMinX() : b2.getMinX();
      double y = (b1.getMinY() < b2.getMinY()) ? b1.getMinY() : b2.getMinY();
      double maxX = (b1.getMaxX() > b2.getMaxX()) ? b1.getMaxX() : b2.getMaxX();
      double maxY = (b1.getMaxY() > b2.getMaxY()) ? b1.getMaxY() : b2.getMaxY();
      double width = maxX - x;
      double height = maxY - y;
      if (data.topInset > 0) {
        y += data.topInset;
        height -= data.topInset;
      }
      if (data.fillHorizontal && data.fillVertical) {
        // ok
      }
      else {
        if (!data.fillHorizontal) {
          double nodeWidth;
          Orientation orientation = node.getContentBias();
          if (orientation == null || orientation == Orientation.HORIZONTAL) {
            nodeWidth = node.prefWidth(-1);
          }
          else { // orientation vertical
            nodeWidth = node.prefWidth(1);
          }
          if (nodeWidth < width) {
            double delta = width - nodeWidth;
            width = nodeWidth;
            if (data.horizontalAlignment == 0) {
              x += Math.ceil(delta / 2.0);
            }
            else if (data.horizontalAlignment > 0) {
              x += delta;
            }
          }
        }
        if (!data.fillVertical) {
          double nodeHeight;
          Orientation orientation = node.getContentBias();
          if (orientation == null || orientation == Orientation.VERTICAL) {
            nodeHeight = node.prefHeight(-1);
          }
          else { // orientation horizontal
            nodeHeight = node.prefHeight(1);
          }
          if (nodeHeight < height) {
            double delta = height - nodeHeight;
            if (data.heightHint == 0) {
              height = nodeHeight;
            }
            else {
              height = data.heightHint;
            }
            if (data.verticalAlignment == 0) {
              y += Math.ceil(delta / 2.0);
            }
            else if (data.verticalAlignment > 0) {
              y += delta;
            }
          }
        }
      }
      node.relocate(x, y);
      node.resize(width, height);
    }
  }

  @Override
  public void requestLayout() {
    m_dirty = true;
    super.requestLayout();
  }

  public void dumpLayoutInfo() {
    dumpLayoutInfo(new PrintWriter(System.out));
  }

  public void dumpLayoutInfo(PrintWriter out) {
    out.println("**************** LogicalGridLayout[" + getId() + "]");
    Node[] c = m_info.nodes;
    String[] names = new String[c.length];
    String[] constraints = new String[c.length];
    for (int i = 0; i < c.length; i++) {
      String cls = c[i].getClass().getName();
      int dot = Math.max(cls.lastIndexOf('.'), cls.lastIndexOf('>'));
      if (dot >= 0) {
        cls = cls.substring(dot + 1);
      }
      names[i] = cls + " (" + c[i].getId() + (c[i].isVisible() ? "" : " invisible") + ")";
      constraints[i] = m_info.gridDatas[i].toString();
    }
    out.println("Layout Info");
    out.println("  hgap=" + m_hgap);
    out.println("  vgap=" + m_vgap);
    out.println("  size=" + getWidth() + " " + getHeight());
    out.println("  minSize=" + computeWidth(MIN_SIZE) + " / " + computeHeight(MIN_SIZE));
    out.println("  prfSize=" + computeWidth(PREF_SIZE) + " / " + computeHeight(PREF_SIZE));
    out.println("  maxSize=" + computeWidth(MAX_SIZE) + " / " + computeHeight(MAX_SIZE));
    out.println("  insets=" + getInsets());
    out.println("  components=" + dump(names));
    out.println("  constraints=" + dump(constraints));
    out.println("  cols=" + m_info.cols);
    out.println("  rows=" + m_info.rows);
    out.println("  col-width=" + dump(m_info.width));
    out.println("  row-height=" + dump(m_info.height));
    out.println("  col-weightX=" + dump(m_info.weightX));
    out.println("  row-weightY=" + dump(m_info.weightY));

    Bounds[][] cellBounds = m_info.layoutCellBounds(getWidth(), getHeight(), getInsets());
    if (cellBounds != null) {
      for (int row = 0; row < cellBounds.length; row++) {
        for (int col = 0; col < cellBounds[row].length; col++) {
          out.println("  cell[" + row + "][" + col + "]=" + cellBounds[row][col]);
        }
      }
      if (cellBounds.length > 0) {
        Bounds last = cellBounds[cellBounds.length - 1][cellBounds[cellBounds.length - 1].length - 1];
        if (getWidth() > 0 && getHeight() > 0) {
          if (last.getMinX() + last.getWidth() > getWidth() - getInsets().getLeft() - getInsets().getRight()) {
            out.println("!!! width too large: " + (last.getMinX() + last.getWidth()) + " > " + (getWidth() - getInsets().getLeft() - getInsets().getRight()));
          }
          if (last.getMinY() + last.getHeight() > getHeight() - getInsets().getTop() - getInsets().getBottom()) {
            out.println("!!! height too large: " + (last.getMinY() + last.getHeight()) + " > " + (getHeight() - getInsets().getTop() - getInsets().getBottom()));
          }
        }
      }
    }
    out.flush();
  }

  public static String dump(Object o) {
    if (o == null) {
      return "null";
    }
    else if (o.getClass().isArray()) {
      int n = Array.getLength(o);
      StringBuffer b = new StringBuffer();
      b.append("[");
      for (int i = 0; i < n; i++) {
        if (i > 0) {
          b.append(",");
        }
        b.append(dump(Array.get(o, i)));
      }
      b.append("]");
      return b.toString();
    }
    else {
      String s = o.toString();
      if (o instanceof Number) {
        s = s.replaceAll("\\.0$", "");
      }
      return s;
    }
  }
}
