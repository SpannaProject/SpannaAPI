package org.spanna.component.messaging;

import org.spanna.entity.Player;

/**
 * A listener for a specific Component Channel, which will receive notifications
 * of messages sent from a client.
 */
public interface ComponentMessageListener {

    /**
     * A method that will be thrown when a ComponentMessageSource sends a component
     * message on a registered channel.
     *
     * @param channel Channel that the message was sent through.
     * @param player Source of the message.
     * @param message The raw message that was sent.
     */
    public void onComponentMessageReceived(String channel, Player player, byte[] message);
}
