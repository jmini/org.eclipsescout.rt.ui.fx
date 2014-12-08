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
package org.eclipsescout.rt.ui.fx.basic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.print.PrintQuality;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.imageio.ImageIO;

import org.eclipse.scout.rt.shared.TEXTS;

/**
 *
 */
public class NodeExporter {

  /**
   * Shows a print dialog and prints the specified node.
   * IMPORTANT: The node will be scaled, which is visible to the user. Must be called in fx thread.
   *
   * @param node
   */
  public void print(Node node) {
    PrinterJob job = PrinterJob.createPrinterJob();
    if (job != null) {
      boolean success = job.showPrintDialog(node.getScene().getWindow());
      if (success) {
        job.getJobSettings().setPrintQuality(PrintQuality.HIGH);

        double aspectRatioPaper = job.getJobSettings().getPageLayout().getPrintableWidth() / job.getJobSettings().getPageLayout().getPrintableHeight();
        double aspectRatioNode = node.getBoundsInParent().getWidth() / node.getBoundsInParent().getHeight();

        double scaleValue;
        if (aspectRatioNode >= aspectRatioPaper) {
          scaleValue = job.getJobSettings().getPageLayout().getPrintableWidth() / node.getBoundsInParent().getWidth();
        }
        else {
          scaleValue = job.getJobSettings().getPageLayout().getPrintableHeight() / node.getBoundsInParent().getHeight();
        }
        Scale scale = new Scale(scaleValue, scaleValue);
        node.getTransforms().add(scale);
        success = job.printPage(node);
        if (success) {
          job.endJob();
        }
        node.getTransforms().remove(scale);
      }
    }
  }

  public boolean saveSnapshot(Node node) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(TEXTS.get("Save"));
    fileChooser.getExtensionFilters().addAll(
        new ExtensionFilter("png", "*.png"));
    File file = fileChooser.showSaveDialog(node.getScene().getWindow());
    if (file != null) {
      WritableImage image = node.snapshot(null, null);
      BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
      try {
        return ImageIO.write(bImage, "png", file);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

}
