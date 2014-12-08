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
package org.eclipsescout.rt.ui.fx.control.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Event related to internal window actions.
 */
public class InternalWindowEvent extends Event {

  private static final long serialVersionUID = -9052417756609437473L;

  /**
   * Common supertype for all internal window event types.
   */
  public static final EventType<InternalWindowEvent> ANY = new EventType<InternalWindowEvent>(Event.ANY, "INTERNAL_WINDOW");

  // -------------------------------------------------
  // OPEN / CLOSE

  /**
   * This event occurs on an internal window just after it is opened.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_OPENED = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_OPENED");

  /**
   * This event is delivered to an internal window when there is an external request to close that window. If the event
   * is not consumed by any event handler, the default handler for this event sets the property closed to true.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_CLOSE_REQUEST = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_CLOSE_REQUEST");

  /**
   * This event occurs on internal window just after it is closed.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_CLOSED = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_CLOSED");

  // -------------------------------------------------
  // MAXIMAZE / RESTORE

  /**
   * This event is delivered to an internal window when there is an external request to maximize that window. If the
   * event is not consumed by any event handler, the default handler for this event sets the property maximized to true.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_MAXIMIZE_REQUEST = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_MAXIMIZE_REQUEST");

  /**
   * This event occurs on internal window just after it is maximized.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_MAXIMIZED = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_MAXIMIZED");

  /**
   * This event is delivered to an internal window when there is an external request to restore the size of that window.
   * If the event is not consumed by any event handler, the default handler for this event sets the property maximized
   * to false.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_RESTORE_REQUEST = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_RESTORE_REQUEST");

  /**
   * This event occurs on internal window just after the size is restored.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_RESTORED = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_RESTORED");

  // -------------------------------------------------
  // ICONIFY / DEICONIFY

  /**
   * This event is delivered to an internal window when there is an external request to iconify that window. If the
   * event is not consumed by any event handler, the default handler for this event sets the property iconified to true.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_ICONIFY_REQUEST = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_ICONIFY_REQUEST");

  /**
   * This event occurs on internal window just after it is iconified.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_ICONIFIED = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_ICONIFIED");

  /**
   * This event occurs on internal window just after it is deiconified.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_DEICONIFIED = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_DEICONIFIED");

  // -------------------------------------------------
  // ACTIVATED / DEACTIVATED

  /**
   * This event occurs on internal window after it is activated.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_ACTIVATED = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_ACTIVATED");

  /**
   * This event occurs on internal window after it is deactivated.
   */
  public static final EventType<InternalWindowEvent> INTERNAL_WINDOW_DEACTIVATED = new EventType<InternalWindowEvent>(
      InternalWindowEvent.ANY, "INTERNAL_WINDOW_DEACTIVATED");

  // -------------------------------------------------

  /**
   * Construct a new {@code InternalWindowEvent} with the specified event type. The source and target of the event is
   * set to {@code Event#NULL_SOURCE_TARGET}.
   * 
   * @param eventType
   *          the event type
   */
  public InternalWindowEvent(EventType<? extends Event> eventType) {
    super(eventType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InternalWindowEvent copyFor(Object newSource, EventTarget newTarget) {
    return (InternalWindowEvent) super.copyFor(newSource, newTarget);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public EventType<InternalWindowEvent> getEventType() {
    return (EventType<InternalWindowEvent>) super.getEventType();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("InternalWindowEvent [");

    sb.append("source = ").append(getSource());
    sb.append(", target = ").append(getTarget());
    sb.append(", eventType = ").append(getEventType());
    sb.append(", consumed = ").append(isConsumed());

    return sb.append("]").toString();
  }

}
