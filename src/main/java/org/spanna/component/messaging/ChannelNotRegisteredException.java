package org.spanna.component.messaging;

/**
 * Thrown if a Component attempts to send a message on an unregistered channel.
 */
@SuppressWarnings("serial")
public class ChannelNotRegisteredException extends RuntimeException {
    public ChannelNotRegisteredException() {
        this("[ERROR] Attempted to send a component message through an unregistered channel.");
    }

    public ChannelNotRegisteredException(String channel) {
        super("[ERROR] Attempted to send a component message through the unregistered channel `" + channel + "'.");
    }
}
