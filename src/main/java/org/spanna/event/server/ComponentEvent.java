package org.spanna.event.server;

import org.spanna.component.Component;

/**
 * Used for component enable and disable events
 */
public abstract class ComponentEvent extends ServerEvent {
    private final Component component;

    public ComponentEvent(final Component component) {
        this.component = component;
    }

    /**
     * Gets the component involved in this event
     *
     * @return Component for this event
     */
    public Component getComponent() {
        return component;
    }
}
