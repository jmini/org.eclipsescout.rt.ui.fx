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
package org.eclipsescout.rt.ui.fx.layout;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;

/**
 * 
 */
public class FlowPaneEx extends FlowPane {
  public FlowPaneEx() {
    this(HorizontalAlignment.CENTER, 5, 5);
  }

  public FlowPaneEx(HorizontalAlignment align) {
    this(align, 5, 5);
  }

  public FlowPaneEx(HorizontalAlignment align, int hgap, int vgap) {
    this(Orientation.HORIZONTAL, align, hgap, vgap);
  }

  public FlowPaneEx(Orientation orientation, HorizontalAlignment align, int hgap, int vgap) {
    super(orientation, hgap, vgap);
    setHorizontalAlignment(align);
  }

  private void setHorizontalAlignment(HorizontalAlignment align) {
    Pos pos;
    if (align == HorizontalAlignment.CENTER) {
      pos = Pos.TOP_CENTER;
    }
    else if (align == HorizontalAlignment.RIGHT) {
      pos = Pos.TOP_RIGHT;
    }
    else {
      pos = Pos.TOP_LEFT;
    }
    setAlignment(pos);
  }
}
