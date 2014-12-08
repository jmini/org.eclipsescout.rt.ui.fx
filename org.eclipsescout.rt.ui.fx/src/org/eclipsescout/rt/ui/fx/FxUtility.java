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

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * 
 */
public final class FxUtility {

  private FxUtility() {
  }

  /**
   * Returns true when the specified node is a child of the specified parent otherwise false.
   * 
   * @param node
   * @param parent
   * @return True when the node is a child of parent otherwise false
   */
  public static boolean isParent(Node node, Parent parent) {
    if (node == null || parent == null) {
      return false;
    }
    while (node != null && node != parent) {
      if (node instanceof Parent) {
        node = ((Parent) node).getParent();
      }
    }
    return node == parent;
  }

  public static List<Node> findAllChildNodes(Node parent) {
    ArrayList<Node> nodes = new ArrayList<Node>();
    findAllChildNodesRec(parent, nodes);
    return nodes;
  }

  private static void findAllChildNodesRec(Node parent, List<Node> list) {
    if (parent instanceof Parent) {
      list.addAll(((Parent) parent).getChildrenUnmodifiable());
      for (Node node : ((Parent) parent).getChildrenUnmodifiable()) {
        findAllChildNodesRec(node, list);
      }
    }
  }
}
