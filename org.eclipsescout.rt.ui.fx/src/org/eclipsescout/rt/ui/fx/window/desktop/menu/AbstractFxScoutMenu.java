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
package org.eclipsescout.rt.ui.fx.window.desktop.menu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipsescout.rt.ui.fx.IFxEnvironment;

/**
 * 
 */
public abstract class AbstractFxScoutMenu<T extends IMenu, U extends MenuItem> implements IFxScoutMenu<T, U> {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(AbstractFxScoutMenu.class);

  private IFxEnvironment m_env;
  private T m_menu;
  private U m_fxMenu;

  @Override
  public void createField(T menu, IFxEnvironment env) {
    m_menu = menu;
    m_env = env;

    initializeFx();

    m_fxMenu.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        Runnable t = new Runnable() {
          @Override
          public void run() {
            try {
              m_menu.doAction();
            }
            catch (ProcessingException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        };
        getFxEnvironment().invokeScoutLater(t, 0);
      }
    });

    //TODO: check mnemonic support
    {
      String originalText = m_menu.getText() == null ? "" : m_menu.getText();
      if (originalText.contains("_")) {
        LOG.warn("menu " + m_menu.getActionId() + " contains already a '_', mnemonic not working correct");
      }
      int pos = originalText.indexOf(m_menu.getMnemonic());
      String newText;
      if (pos >= 0) {
        newText = originalText.substring(0, pos) + '_' + originalText.substring(pos);
      }
      else {
        newText = originalText;
      }
      m_fxMenu.setText(newText);
      m_fxMenu.setMnemonicParsing(true);
    }

    m_fxMenu.setId(m_menu.getActionId());
    m_fxMenu.setDisable(!m_menu.isEnabled());
    m_fxMenu.setVisible(m_menu.isVisible());
    if (m_menu.getTooltipText() != null) {
      LOG.warn("It is not possible to attach tooltip to a menu " + m_menu.getActionId());
    }
    if (m_menu.getKeyStroke() != null) {
      m_fxMenu.setAccelerator(KeyCombination.keyCombination(m_menu.getKeyStroke()));
    }
    if (m_menu.getIconId() != null) {
      m_fxMenu.setGraphic(getFxEnvironment().getImageView(m_menu.getIconId()));
    }

  }

  @Override
  public T getScoutMenu() {
    return m_menu;
  }

  @Override
  public IFxEnvironment getFxEnvironment() {
    return m_env;
  }

  @Override
  public void setFxMenu(U menu) {
    this.m_fxMenu = menu;
  }

  @Override
  public U getFxMenu() {
    return m_fxMenu;
  }

}
