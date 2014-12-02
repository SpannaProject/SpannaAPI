package org.spanna.event.block;

import org.spanna.block.Block;
import org.spanna.block.BlockState;
import org.spanna.entity.Entity;

/**
 * Called when a block is formed by entities.
 * <p>
 * Examples:
 * <ul>
 * <li>Snow formed by a {@link org.bukkit.entity.Snowman}.
 * </ul>
 */
public class EntityBlockFormEvent extends BlockFormEvent {
    private final Entity entity;

    public EntityBlockFormEvent(final Entity entity, final Block block, final BlockState blockstate) {
        super(block, blockstate);

        this.entity = entity;
    }

    /**
     * Get the entity that formed the block.
     *
     * @return Entity involved in event
     */
    public Entity getEntity() {
        return entity;
    }
}