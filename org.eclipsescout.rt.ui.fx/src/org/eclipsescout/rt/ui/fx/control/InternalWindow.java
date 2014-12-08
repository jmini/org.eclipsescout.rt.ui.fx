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

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;

import org.eclipsescout.rt.ui.fx.control.event.InternalWindowEvent;
import org.eclipsescout.rt.ui.fx.control.internal.InternalWindowSkin;

/**
 * This class represents only an internal window. There is no behavior on the window buttons. The parent node must react
 * on this status changes and implement the associated behavior.
 */
public class InternalWindow extends Control {

  public static final String DEFAULT_STYLE = "/css/defaultInternalWindow.css";
  public static final String DEFAULT_STYLE_CLASS = "internal-window";

  /* *************************************************************
   * Constructors
   * ************************************************************/

  /**
   * Creates an iconifiable, maximizable and closable internal window with no title.
   */
  public InternalWindow() {
    super();

    getStyleClass().setAll(DEFAULT_STYLE_CLASS);

    setFocusTraversable(false);

    setEventHandler(InternalWindowEvent.ANY, new EventHandler<InternalWindowEvent>() {
      @Override
      public void handle(InternalWindowEvent event) {
        handleInternalWindowEvent(event);
      }
    });
  }

  /**
   * Creates an iconifiable, maximizable and closable internal window with the specified title.
   *
   * @param title
   *          the string to display in the title bar
   */
  public InternalWindow(String title) {
    this();

    setTitle(title);
  }

  /**
   * Creates an internal window with the specified title, iconifiability, maximizability and closability.
   *
   * @param title
   *          the string to display in the title bar
   * @param iconifiable
   *          if true, the internal window can be iconified
   * @param maximizable
   *          if true, the internal window can be maximized
   * @param closable
   *          if true, the internal window can be closed
   */
  public InternalWindow(String title, boolean iconifiable, boolean maximizable, boolean closable) {
    this(title);

    setIconifiable(iconifiable);
    setMaximizable(maximizable);
    setClosable(closable);
  }

  /* *************************************************************
   * Properties
   * ************************************************************/

  /**
   * Css pseudo class which indicates the active internal window (a child of the internal window is the focus owner)
   */
  private static final PseudoClass activePseudoClass = PseudoClass.getPseudoClass("active");
  private static final PseudoClass iconifiedPseudoClass = PseudoClass.getPseudoClass("iconified");

  /**
   * Defines the root Node of the internal window.
   */
  private final ReadOnlyObjectWrapper<Node> root = new ReadOnlyObjectWrapper<Node>(InternalWindow.this, "root", null) {
    private Node oldRoot;

    @Override
    protected void invalidated() {
      super.invalidated();

      // add or remove root as child of the internal window
      if (oldRoot != null) {
        getChildren().remove(oldRoot);
      }
      oldRoot = getValue();
      if (getValue() != null) {
        getChildren().add(getValue());
      }
    }
  };

  public final ReadOnlyObjectProperty<Node> rootProperty() {
    return root.getReadOnlyProperty();
  }

  public final void setRoot(Node pane) {
    root.setValue(pane);
  }

  public final Node getRoot() {
    return root.getValue();
  }

  /**
   * Defines the title of the internal window, which is shown in the title bar.
   */
  private final ReadOnlyObjectWrapper<String> title = new ReadOnlyObjectWrapper<String>(InternalWindow.this, "title", "");

  public final ReadOnlyObjectProperty<String> titleProperty() {
    return title.getReadOnlyProperty();
  }

  public final void setTitle(String value) {
    title.setValue(value);
  }

  public final String getTitle() {
    return title.getValue();
  }

  /**
   * Defines the icon of the internal window, which is shown in the title bar.
   */
  private final ReadOnlyObjectWrapper<Image> icon = new ReadOnlyObjectWrapper<Image>(InternalWindow.this, "icon");

  public final ReadOnlyObjectProperty<Image> iconProperty() {
    return icon.getReadOnlyProperty();
  }

  public final void setIcon(Image value) {
    icon.setValue(value);
  }

  public final Image getIcon() {
    return icon.getValue();
  }

  /**
   * Defines whether the internal windows is activated or not.
   * An internal window is activated when the focus owner of the scene is a child of the internal window.
   */
  private final ReadOnlyBooleanWrapper activated = new ReadOnlyBooleanWrapper(InternalWindow.this, "activated", false) {
    @Override
    protected void invalidated() {
      super.invalidated();
      // set pseudo class state
      InternalWindow.this.pseudoClassStateChanged(activePseudoClass, getValue());

      // fire event
      if (getValue()) {
        InternalWindow.this.fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_ACTIVATED));
      }
      else {
        InternalWindow.this.fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_DEACTIVATED));
      }
    }
  };

  public final ReadOnlyBooleanProperty activatedProperty() {
    return activated.getReadOnlyProperty();
  }

  public final void setActivated(boolean newValue) {
    activated.setValue(newValue);
  }

  public final boolean isActivated() {
    return activated.getValue();
  }

  // -------------------------------------------------

  /**
   * Defines whether the internal window can be iconified or not.
   */
  private final ReadOnlyBooleanWrapper iconifiable = new ReadOnlyBooleanWrapper(InternalWindow.this, "iconifiable", true);

  public final ReadOnlyBooleanProperty iconifiableProperty() {
    return iconifiable.getReadOnlyProperty();
  }

  public final void setIconifiable(boolean value) {
    iconifiable.setValue(value);
  }

  public final boolean isIconifiable() {
    return iconifiable.getValue();
  }

  /**
   * Defines whether the internal window is iconified or not.
   */
  private final ReadOnlyBooleanWrapper iconified = new ReadOnlyBooleanWrapper(InternalWindow.this, "iconified", false) {
    @Override
    protected void invalidated() {
      super.invalidated();
      // set pseudo class state
      InternalWindow.this.pseudoClassStateChanged(iconifiedPseudoClass, getValue());

      // fire event
      if (getValue()) {
        InternalWindow.this.fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_ICONIFIED));
      }
      else {
        InternalWindow.this.fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_DEICONIFIED));
      }
    }
  };

  public final ReadOnlyBooleanProperty iconifiedProperty() {
    return iconified.getReadOnlyProperty();
  }

  public final void setIconified(boolean value) {
    iconified.setValue(value);
  }

  public final boolean isIconified() {
    return iconified.getValue();
  }

  /**
   * Called when there is an external request to iconify this internal window. The installed event handler can prevent
   * iconifing by consuming the received event.
   */
  private final ReadOnlyObjectWrapper<EventHandler<InternalWindowEvent>> onIconifyRequest = new ReadOnlyObjectWrapper<EventHandler<InternalWindowEvent>>(
      InternalWindow.this, "onIconifyRequest") {
    @Override
    protected void invalidated() {
      InternalWindow.this.setEventHandler(InternalWindowEvent.INTERNAL_WINDOW_ICONIFY_REQUEST, getValue());
    }
  };

  public final ReadOnlyObjectProperty<EventHandler<InternalWindowEvent>> onIconifyRequestProperty() {
    return onIconifyRequest.getReadOnlyProperty();
  }

  public final void setOnIconifyRequest(EventHandler<InternalWindowEvent> value) {
    onIconifyRequest.setValue(value);
  }

  public final EventHandler<InternalWindowEvent> getOnIconifyRequest() {
    return onIconifyRequest.getValue();
  }

  // -------------------------------------------------

  /**
   * Defines if the internal window can be maximized to the size of the desktop or not.
   */
  private final ReadOnlyBooleanWrapper maximizable = new ReadOnlyBooleanWrapper(InternalWindow.this, "maximizable", true);

  public final ReadOnlyBooleanProperty maximizableProperty() {
    return maximizable.getReadOnlyProperty();
  }

  public final void setMaximizable(boolean value) {
    maximizable.setValue(value);
  }

  public final boolean isMaximizable() {
    return maximizable.getValue();
  }

  /**
   * Defines whether the Stage is maximized or not.
   */
  private final ReadOnlyBooleanWrapper maximized = new ReadOnlyBooleanWrapper(InternalWindow.this, "maximized", false) {
    @Override
    protected void invalidated() {
      if (getValue()) {
        fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_MAXIMIZED));
      }
      else {
        fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_RESTORED));
      }
    }
  };

  public final ReadOnlyBooleanProperty maximizedProperty() {
    return maximized.getReadOnlyProperty();
  }

  public final void setMaximized(boolean value) {
    maximized.setValue(value);
  }

  public final boolean isMaximized() {
    return maximized.getValue();
  }

  /**
   * Called when there is an external request to maximize this internal window. The installed event handler can prevent
   * maximizing by consuming the received event.
   */
  private final ReadOnlyObjectWrapper<EventHandler<InternalWindowEvent>> onMaximizeRequest = new ReadOnlyObjectWrapper<EventHandler<InternalWindowEvent>>(
      InternalWindow.this, "onMaximizeRequest") {
    @Override
    protected void invalidated() {
      setEventHandler(InternalWindowEvent.INTERNAL_WINDOW_MAXIMIZE_REQUEST, getValue());
    }
  };

  public final ReadOnlyObjectProperty<EventHandler<InternalWindowEvent>> onMaximizeRequestProperty() {
    return onMaximizeRequest.getReadOnlyProperty();
  }

  public final void setOnMaximizeRequest(EventHandler<InternalWindowEvent> value) {
    onMaximizeRequest.setValue(value);
  }

  public final EventHandler<InternalWindowEvent> getOnMaximizeRequest() {
    return onMaximizeRequest.getValue();
  }

  /**
   * Called when there is an external request to restore this internal window. The installed event handler can prevent
   * restoring by consuming the received event.
   */
  private final ReadOnlyObjectWrapper<EventHandler<InternalWindowEvent>> onRestoreRequest = new ReadOnlyObjectWrapper<EventHandler<InternalWindowEvent>>(
      InternalWindow.this, "onRestoreRequest") {
    @Override
    protected void invalidated() {
      setEventHandler(InternalWindowEvent.INTERNAL_WINDOW_RESTORE_REQUEST, getValue());
    }
  };

  public final ReadOnlyObjectProperty<EventHandler<InternalWindowEvent>> onRestoreRequestProperty() {
    return onRestoreRequest.getReadOnlyProperty();
  }

  public final void setOnRestoreRequest(EventHandler<InternalWindowEvent> value) {
    onRestoreRequest.setValue(value);
  }

  public final EventHandler<InternalWindowEvent> getOnRestoreRequest() {
    return onRestoreRequest.getValue();
  }

  // -------------------------------------------------

  /**
   * Defines if the internal window can be closed or not.
   */
  private final ReadOnlyBooleanWrapper closable = new ReadOnlyBooleanWrapper(InternalWindow.this, "closable", true);

  public final ReadOnlyBooleanProperty closableProperty() {
    return closable.getReadOnlyProperty();
  }

  public final void setClosable(boolean value) {
    closable.setValue(value);
  }

  public final boolean isClosable() {
    return closable.getValue();
  }

  /**
   * Defines whether the internal window is closed or not.
   */
  private final ReadOnlyBooleanWrapper closed = new ReadOnlyBooleanWrapper(InternalWindow.this, "closed", true) {
    @Override
    protected void invalidated() {
      if (getValue()) {
        fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_CLOSED));
      }
      else {
        fireEvent(new InternalWindowEvent(InternalWindowEvent.INTERNAL_WINDOW_OPENED));
      }
    }
  };

  public final ReadOnlyBooleanProperty closedProperty() {
    return closed.getReadOnlyProperty();
  }

  public final void setClosed(boolean value) {
    closed.setValue(value);
  }

  public final void setOpen(boolean value) {
    closed.setValue(!value);
  }

  public final boolean isClosed() {
    return closed.getValue();
  }

  public final boolean isOpen() {
    return !closed.getValue();
  }

  /**
   * Called when there is an external request to close this internal window. The installed event handler can prevent
   * closing by consuming the received event.
   */
  private final ReadOnlyObjectWrapper<EventHandler<InternalWindowEvent>> onCloseRequest = new ReadOnlyObjectWrapper<EventHandler<InternalWindowEvent>>(
      InternalWindow.this, "onCloseRequest") {
    @Override
    protected void invalidated() {
      super.invalidated();
      setEventHandler(InternalWindowEvent.INTERNAL_WINDOW_CLOSE_REQUEST, getValue());
    }
  };

  public final ReadOnlyObjectProperty<EventHandler<InternalWindowEvent>> onCloseRequestProperty() {
    return onCloseRequest.getReadOnlyProperty();
  }

  public final void setOnCloseRequest(EventHandler<InternalWindowEvent> value) {
    onCloseRequest.setValue(value);
  }

  public final EventHandler<InternalWindowEvent> getOnCloseRequest() {
    return onCloseRequest.getValue();
  }

  /* *************************************************************
   * Methods
   * ************************************************************/

  /**
   * This method will be called when an event of the type {@link InternalWindowEvent.ANY} occurs
   * on this class. It implements the default behavior of the external requests.
   *
   * @param event
   */
  protected void handleInternalWindowEvent(InternalWindowEvent event) {
    if (!event.isConsumed()) {
      if (event.getEventType() == InternalWindowEvent.INTERNAL_WINDOW_ICONIFY_REQUEST) {
        setIconified(true);
        event.consume();
      }
      else if (event.getEventType() == InternalWindowEvent.INTERNAL_WINDOW_MAXIMIZE_REQUEST) {
        setMaximized(true);
        event.consume();
      }
      else if (event.getEventType() == InternalWindowEvent.INTERNAL_WINDOW_RESTORE_REQUEST) {
        setMaximized(false);
        event.consume();
      }
      else if (event.getEventType() == InternalWindowEvent.INTERNAL_WINDOW_CLOSE_REQUEST) {
        setClosed(true);
        event.consume();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Skin<?> createDefaultSkin() {
    return new InternalWindowSkin(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getUserAgentStylesheet() {
    return this.getClass().getResource(DEFAULT_STYLE).toExternalForm();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("InternalWindow [");

    sb.append("title = ").append(getTitle());
    sb.append(", icon = ").append(getIcon());
    sb.append(", root = ").append(getRoot());
    sb.append(", isActivated = ").append(isActivated());
    sb.append(", isIconifiable = ").append(isIconifiable());
    sb.append(", isIconified = ").append(isIconified());
    sb.append(", isMaximizable = ").append(isMaximizable());
    sb.append(", isMaximized = ").append(isMaximized());
    sb.append(", isClosable = ").append(isClosable());
    sb.append(", isClosed = ").append(isClosed());

    return sb.append("]").toString();
  }

}
