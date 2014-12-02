package org.spanna.event.painting;

import org.spanna.Warning;
import org.spanna.block.Block;
import org.spanna.block.BlockFace;
import org.spanna.entity.Painting;
import org.spanna.entity.Player;
import org.spanna.event.Cancellable;
import org.spanna.event.HandlerList;

/**
 * Triggered when a painting is created in the world
 *
 * @deprecated Use {@link org.spanna.event.hanging.HangingPlaceEvent} instead.
 */
@Deprecated
@Warning(reason="This event has been replaced by HangingPlaceEvent")
public class PaintingPlaceEvent extends PaintingEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Player player;
    private final Block block;
    private final BlockFace blockFace;

    public PaintingPlaceEvent(final Painting painting, final Player player, final Block block, final BlockFace blockFace) {
        super(painting);
        this.player = player;
        this.block = block;
        this.blockFace = blockFace;
    }

    /**
     * Returns the player placing the painting
     *
     * @return Entity returns the player placing the painting
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the block that the painting was placed on
     *
     * @return Block returns the block painting placed on
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Returns the face of the block that the painting was placed on
     *
     * @return BlockFace returns the face of the block the painting was placed
     *     on
     */
    public BlockFace getBlockFace() {
        return blockFace;
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
