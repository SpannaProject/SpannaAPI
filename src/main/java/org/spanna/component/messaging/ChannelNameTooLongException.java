package org.spanna.component.messaging;

/**
 * Thrown if a Component Channel is too long.
 */
@SuppressWarnings("serial")
public class ChannelNameTooLongException extends RuntimeException {
    public ChannelNameTooLongException() {
        super("[ERROR] Attempted to send a Component Message to a channel that was too large. The maximum length a channel may be is " + Messenger.MAX_CHANNEL_SIZE + " chars.");
    }

    public ChannelNameTooLongException(String channel) {
        super("[ERROR] Attempted to send a Component Message to a channel that was too large. The maximum length a channel may be is " + Messenger.MAX_CHANNEL_SIZE + " chars (attempted " + channel.length() + " - '" + channel + ".");
    }
}
