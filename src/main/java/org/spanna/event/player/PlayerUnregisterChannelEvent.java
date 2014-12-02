package org.spanna.event.player;

import org.spanna.entity.Player;

/**
 * This is called immediately after a player unregisters for a component channel.
 */
public class PlayerUnregisterChannelEvent extends PlayerChannelEvent {

    public PlayerUnregisterChannelEvent(final Player player, final String channel) {
        super(player, channel);
    }
}
