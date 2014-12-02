package org.spanna.event.painting;

import org.spanna.Warning;
import org.spanna.entity.Painting;
import org.spanna.event.Event;

/**
 * Represents a painting-related event.
 *
 * @deprecated Use {@link org.spanna.event.hanging.HangingEvent} instead.
 */
@Deprecated
@Warning(reason="This event has been replaced by HangingEvent")
public abstract class PaintingEvent extends Event {
    protected Painting painting;

    protected PaintingEvent(final Painting painting) {
        this.painting = painting;
    }

    /**
     * Gets the painting involved in this event.
     *
     * @return the painting
     */
    public Painting getPainting() {
        return painting;
    }
}
