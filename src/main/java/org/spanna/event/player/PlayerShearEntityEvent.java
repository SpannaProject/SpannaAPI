package org.spanna.event.player;

import org.spanna.entity.Entity;
import org.spanna.entity.Player;
import org.spanna.event.Cancellable;
import org.spanna.event.HandlerList;

/**
 * Called when a player shears an entity
 */
public class PlayerShearEntityEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private final Entity what;

    public PlayerShearEntityEvent(final Player who, final Entity what) {
        super(who);
        this.cancel = false;
        this.what = what;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Gets the entity the player is shearing
     *
     * @return the entity the player is shearing
     */
    public Entity getEntity() {
        return what;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
