package org.spanna.event.inventory;

import org.spanna.block.Block;
import org.spanna.event.Cancellable;
import org.spanna.event.HandlerList;
import org.spanna.event.block.BlockEvent;
import org.spanna.inventory.ItemStack;

/**
 * Called when an ItemStack is successfully burned as fuel in a furnace.
 */
public class FurnaceBurnEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final ItemStack fuel;
    private int burnTime;
    private boolean cancelled;
    private boolean burning;

    public FurnaceBurnEvent(final Block furnace, final ItemStack fuel, final int burnTime) {
        super(furnace);
        this.fuel = fuel;
        this.burnTime = burnTime;
        this.cancelled = false;
        this.burning = true;
    }

    /**
     * Gets the block for the furnace involved in this event
     *
     * @return the block of the furnace
     * @deprecated In favour of {@link #getBlock()}.
     */
    @Deprecated
    public Block getFurnace() {
        return getBlock();
    }

    /**
     * Gets the fuel ItemStack for this event
     *
     * @return the fuel ItemStack
     */
    public ItemStack getFuel() {
        return fuel;
    }

    /**
     * Gets the burn time for this fuel
     *
     * @return the burn time for this fuel
     */
    public int getBurnTime() {
        return burnTime;
    }

    /**
     * Sets the burn time for this fuel
     *
     * @param burnTime the burn time for this fuel
     */
    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    /**
     * Gets whether the furnace's fuel is burning or not.
     *
     * @return whether the furnace's fuel is burning or not.
     */
    public boolean isBurning() {
        return this.burning;
    }

    /**
     * Sets whether the furnace's fuel is burning or not.
     *
     * @param burning true if the furnace's fuel is burning
     */
    public void setBurning(boolean burning) {
        this.burning = burning;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
