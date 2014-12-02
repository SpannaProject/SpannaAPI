package org.spanna.event.player;

import org.spanna.entity.Player;

/**
 * This is called immediately after a player registers for a component channel.
 */
public class PlayerRegisterChannelEvent extends PlayerChannelEvent {

    public PlayerRegisterChannelEvent(final Player player, final String channel) {
        super(player, channel);
    }
}
