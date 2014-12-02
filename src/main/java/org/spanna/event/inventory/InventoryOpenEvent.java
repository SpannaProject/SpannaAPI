package org.spanna.event.inventory;

import org.spanna.inventory.InventoryView;
import org.spanna.entity.HumanEntity;
import org.spanna.event.Cancellable;
import org.spanna.event.HandlerList;

/**
 * Represents a player related inventory event
 */
public class InventoryOpenEvent extends InventoryEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public InventoryOpenEvent(InventoryView transaction) {
        super(transaction);
        this.cancelled = false;
    }

    /**
     * Returns the player involved in this event
     *
     * @return Player who is involved in this event
     */
    public final HumanEntity getPlayer() {
        return transaction.getPlayer();
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other components.
     * <p>
     * If an inventory open event is cancelled, the inventory screen will not
     * show.
     *
     * @return true if this event is cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other components.
     * <p>
     * If an inventory open event is cancelled, the inventory screen will not
     * show.
     *
     * @param cancel true if you wish to cancel this event
     */
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
