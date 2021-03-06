package org.spanna.event.server;

import org.spanna.event.HandlerList;
import org.spanna.component.Component;

/**
 * Called when a component is disabled.
 */
public class ComponentDisableEvent extends ComponentEvent {
    private static final HandlerList handlers = new HandlerList();

    public ComponentDisableEvent(final Component component) {
        super(component);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
