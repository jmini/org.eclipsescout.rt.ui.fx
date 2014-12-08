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
package org.eclipsescout.rt.ui.fx.control;

import java.lang.reflect.Array;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import org.eclipsescout.rt.ui.fx.control.internal.MultiSplitPaneSkin;

/**
 * The Nodes are layouted depending on the primary orientation:
 * example for 3 columns and 3 rows with horizontal orientation: <code>
 * ┌───┬─────┬─┐
 * │ 0 │     │6│
 * ├───┤  3  │ │
 * │   │     ├─┤
 * │   ├─────┤ │
 * │ 1 │  4  │7│
 * │   ├─────┤ │
 * │   │     ├─┤
 * ├───┤  5  │8│
 * │ 2 │     │ │
 * └───┴─────┴─┘
 * </code> <br>
 * <br>
 * example for 3 columns and 3 rows with vertical orientation: <code>
 * ┌───┬───────┬───┐
 * │ 0 │   3   │ 6 │
 * ├───┴─┬───┬─┴───┤
 * │     │   │     │
 * │  1  │ 4 │  7  │
 * │     │   │     │
 * ├─┬───┴───┴───┬─┤
 * │2│     5     │8│
 * └─┴───────────┴─┘
 * </code> examples for column and row indices (both orientations):<br>
 * element 2 is: column 0 and row 3<br>
 * element 3 is: column 1 and row 0
 */
public class MultiSplitPane extends Control implements IDesktopRootPane {

  public static final String CONSTRAINT_PROPERTY_NAME = "MultiSplitPaneConstraint";

  public static final String DEFAULT_STYLE = "/css/defaultMultiSplitPane.css";
  public static final String DEFAULT_STYLE_CLASS = "multi-split-pane";

  private final ObjectProperty<Node>[][] elements;
  private final ReadOnlyProperty<Node>[][] unmodifiableElements;

  private final int columnNumbers;
  private final int rowNumbers;
  private final int elementNumbers;
  private final Orientation primaryOrientation;

  /**************************************************************
   * Constructor
   *************************************************************/

  /**
   * @param columnNumber
   *          maximal numbers of columns
   * @param rowNumber
   *          maximal numbers of rows
   * @param primaryDirection
   *          Orientation of the primary split pane
   */
  @SuppressWarnings("unchecked")
  public MultiSplitPane(int columnNumber, int rowNumber, Orientation primaryOrientation) {
    super();

    setFocusTraversable(false);

    columnNumbers = columnNumber;
    rowNumbers = rowNumber;
    this.primaryOrientation = primaryOrientation;

    getStyleClass().setAll(DEFAULT_STYLE_CLASS);

    int firstD = columnNumbers;
    int secondD = rowNumbers;
    if (primaryOrientation == Orientation.VERTICAL) {
      firstD = rowNumbers;
      secondD = columnNumbers;
    }

    elementNumbers = columnNumbers * rowNumbers;
    elements = (SimpleObjectProperty<Node>[][]) Array.newInstance(SimpleObjectProperty[].class, firstD);
    unmodifiableElements = (ReadOnlyProperty<Node>[][]) Array.newInstance(ReadOnlyProperty[].class, firstD);
    for (int i = 0; i < elements.length; i++) {
      elements[i] = (SimpleObjectProperty<Node>[]) Array.newInstance(SimpleObjectProperty.class, secondD);
      unmodifiableElements[i] = (ReadOnlyProperty<Node>[]) Array.newInstance(ReadOnlyProperty.class, secondD);
    }

    init();
  }

  /**************************************************************
   * Methods
   *************************************************************/

  private void init() {
    for (int i = 0; i < elements.length; i++) {
      for (int j = 0; j < elements[i].length; j++) {
        elements[i][j] = new SimpleObjectProperty<Node>();
        unmodifiableElements[i][j] = elements[i][j];
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Skin<?> createDefaultSkin() {
    return new MultiSplitPaneSkin(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getUserAgentStylesheet() {
    return this.getClass().getResource(DEFAULT_STYLE).toExternalForm();
  }

  /**
   * Sets the specified node to the specified index. For the numbering look at the class level java doc.
   *
   * @param element
   * @param index
   */
  public void setElement(Node element, int index) {
    int row = index % getRowNumbers();
    int column = (index - row) / getRowNumbers();
    setElement(element, column, row);
  }

  /**
   * Sets the specified node to the specified column and row. For the numbering look at the class level java doc.
   *
   * @param element
   * @param columnIndex
   * @param rowIndex
   */
  public void setElement(Node element, int columnIndex, int rowIndex) {
    if (columnIndex < 0 || columnIndex >= getColumnNumbers()) {
      throw new IllegalArgumentException("columnIndex must be >= 0 and < " + getColumnNumbers() + " is: " + columnIndex);
    }
    if (rowIndex < 0 || rowIndex >= getRowNumbers()) {
      throw new IllegalArgumentException("rowIndex must be >= 0 and < " + getRowNumbers() + " is: " + rowIndex);
    }
    if (getPrimaryOrientation() == Orientation.VERTICAL) {
      int tmp = columnIndex;
      columnIndex = rowIndex;
      rowIndex = tmp;
    }
    elements[columnIndex][rowIndex].setValue(element);
  }

  /**
   * Removes the node from the specified index. For the numbering look at the class level java doc.
   *
   * @param index
   */
  public void removeElement(int index) {
    setElement(null, index);
  }

  /**
   * Removes the node from specified column and row. For the numbering look at the class level java doc.
   *
   * @param element
   * @param columnIndex
   * @param rowIndex
   */
  public void removeElement(int columnIndex, int rowIndex) {
    setElement(null, columnIndex, rowIndex);
  }

  /**
   * Returns a two dimensional array with all nodes of this multi split pane
   *
   * @return
   */
  public ReadOnlyProperty<Node>[][] getElementsUnmodifiable() {
    return unmodifiableElements;
  }

  public int getColumnNumbers() {
    return columnNumbers;
  }

  public int getRowNumbers() {
    return rowNumbers;
  }

  public int getNodeNumbers() {
    return elementNumbers;
  }

  public Orientation getPrimaryOrientation() {
    return primaryOrientation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addWindow(InternalWindow w) {
    final MultiSplitPaneConstraint c = getConstraint(w);
    setElement(w, c.getPos());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeWindow(InternalWindow w) {
    MultiSplitPaneConstraint c = getConstraint(w);
    removeElement(c.getPos());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Node getNode() {
    return this;
  }

  /**
   * Returns the {@code MultiSplitPaneConstraint} of the specified {@code InternalWindow} or null.
   *
   * @param w
   *          The internal window
   * @return The multi split pane constraint or null
   */
  private static MultiSplitPaneConstraint getConstraint(InternalWindow w) {
    return (MultiSplitPaneConstraint) w.getProperties().get(CONSTRAINT_PROPERTY_NAME);
  }

  public static class MultiSplitPaneConstraint {
    private int pos;

    /**
     *
     */
    public MultiSplitPaneConstraint(int pos) {
      this.pos = pos;
    }

    public int getPos() {
      return pos;
    }
  }

}
