package org.spanna.command;

import org.spanna.block.Block;

public interface BlockCommandSender extends CommandSender {

    /**
     * Returns the block this command sender belongs to
     *
     * @return Block for the command sender
     */
    public Block getBlock();
}
