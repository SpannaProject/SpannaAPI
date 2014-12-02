package org.spanna.event.painting;

import org.spanna.Warning;
import org.spanna.entity.Entity;
import org.spanna.entity.Painting;

/**
 * Triggered when a painting is removed by an entity
 *
 * @deprecated Use {@link org.spanna.event.hanging.HangingBreakByEntityEvent}
 *     instead.
 */
@Deprecated
@Warning(reason="This event has been replaced by HangingBreakByEntityEvent")
public class PaintingBreakByEntityEvent extends PaintingBreakEvent {
    private final Entity remover;

    public PaintingBreakByEntityEvent(final Painting painting, final Entity remover) {
        super(painting, RemoveCause.ENTITY);
        this.remover = remover;
    }

    /**
     * Gets the entity that removed the painting
     *
     * @return the entity that removed the painting.
     */
    public Entity getRemover() {
        return remover;
    }
}
