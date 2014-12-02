package org.spanna.component.messaging;

import java.util.Set;
import org.spanna.component.Component;

/**
 * Represents a possible recipient for a Component Message.
 */
public interface ComponentMessageRecipient {
    /**
     * Sends this recipient a Component Message on the specified outgoing
     * channel.
     * <p>
     * The message may not be larger than {@link Messenger#MAX_MESSAGE_SIZE}
     * bytes, and the component must be registered to send messages on the
     * specified channel.
     *
     * @param source The component that sent this message.
     * @param channel The channel to send this message on.
     * @param message The raw message to send.
     * @throws IllegalArgumentException Thrown if the source component is
     *     disabled.
     * @throws IllegalArgumentException Thrown if source, channel or message
     *     is null.
     * @throws MessageTooLargeException Thrown if the message is too big.
     * @throws ChannelNotRegisteredException Thrown if the channel is not
     *     registered for this component.
     */
    public void sendComponentMessage(Component source, String channel, byte[] message);

    /**
     * Gets a set containing all the Component Channels that this client is
     * listening on.
     *
     * @return Set containing all the channels that this client may accept.
     */
    public Set<String> getListeningComponentChannels();
}
