package org.spanna.component.messaging;

import java.util.Set;
import org.spanna.entity.Player;
import org.spanna.component.Component;

/**
 * A class responsible for managing the registrations of component channels and
 * their listeners.
 */
public interface Messenger {

    /**
     * Represents the largest size that an individual Component Message may be.
     */
    public static final int MAX_MESSAGE_SIZE = 32766;

    /**
     * Represents the largest size that a Component Channel may be.
     */
    public static final int MAX_CHANNEL_SIZE = 16;

    /**
     * Checks if the specified channel is a reserved name.
     *
     * @param channel Channel name to check.
     * @return True if the channel is reserved, otherwise false.
     * @throws IllegalArgumentException Thrown if channel is null.
     */
    public boolean isReservedChannel(String channel);

    /**
     * Registers the specific component to the requested outgoing component channel,
     * allowing it to send messages through that channel to any clients.
     *
     * @param component Component that wishes to send messages through the channel.
     * @param channel Channel to register.
     * @throws IllegalArgumentException Thrown if component or channel is null.
     */
    public void registerOutgoingComponentChannel(Component component, String channel);

    /**
     * Unregisters the specific component from the requested outgoing component
     * channel, no longer allowing it to send messages through that channel to
     * any clients.
     *
     * @param component Component that no longer wishes to send messages through the
     *     channel.
     * @param channel Channel to unregister.
     * @throws IllegalArgumentException Thrown if component or channel is null.
     */
    public void unregisterOutgoingComponentChannel(Component component, String channel);

    /**
     * Unregisters the specific component from all outgoing component channels, no
     * longer allowing it to send any component messages.
     *
     * @param component Component that no longer wishes to send component messages.
     * @throws IllegalArgumentException Thrown if plugin is null.
     */
    public void unregisterOutgoingComponentChannel(Component component);

    /**
     * Registers the specific compponent for listening on the requested incoming
     * component channel, allowing it to act upon any component messages.
     *
     * @param component Component that wishes to register to this channel.
     * @param channel Channel to register.
     * @param listener Listener to receive messages on.
     * @return The resulting registration that was made as a result of this
     *     method.
     * @throws IllegalArgumentException Thrown if component, channel or listener
     *     is null, or the listener is already registered for this channel.
     */
    public ComponentMessageListenerRegistration registerIncomingComponentChannel(Component component, String channel, ComponentMessageListener listener);

    /**
     * Unregisters the specific component's listener from listening on the
     * requested incoming component channel, no longer allowing it to act upon
     * any component messages.
     *
     * @param component Component that wishes to unregister from this channel.
     * @param channel Channel to unregister.
     * @param listener Listener to stop receiving messages on.
     * @throws IllegalArgumentException Thrown if component, channel or listener
     *     is null.
     */
    public void unregisterIncomingComponentChannel(Component component, String channel, ComponentMessageListener listener);

    /**
     * Unregisters the specific plugin from listening on the requested
     * incoming component channel, no longer allowing it to act upon any component
     * messages.
     *
     * @param component Component that wishes to unregister from this channel.
     * @param channel Channel to unregister.
     * @throws IllegalArgumentException Thrown if component or channel is null.
     */
    public void unregisterIncomingComponentChannel(Component component, String channel);

    /**
     * Unregisters the specific component from listening on all component channels
     * through all listeners.
     *
     * @param component Component that wishes to unregister from this channel.
     * @throws IllegalArgumentException Thrown if component is null.
     */
    public void unregisterIncomingComponentChannel(Component component);

    /**
     * Gets a set containing all the outgoing component channels.
     *
     * @return List of all registered outgoing component channels.
     */
    public Set<String> getOutgoingChannels();

    /**
     * Gets a set containing all the outgoing component channels that the
     * specified component is registered to.
     *
     * @param component Component to retrieve channels for.
     * @return List of all registered outgoing component channels that a component
     *     is registered to.
     * @throws IllegalArgumentException Thrown if component is null.
     */
    public Set<String> getOutgoingChannels(Component component);

    /**
     * Gets a set containing all the incoming component channels.
     *
     * @return List of all registered incoming component channels.
     */
    public Set<String> getIncomingChannels();

    /**
     * Gets a set containing all the incoming component channels that the
     * specified component is registered for.
     *
     * @param component Component to retrieve channels for.
     * @return List of all registered incoming component channels that the component
     *     is registered for.
     * @throws IllegalArgumentException Thrown if component is null.
     */
    public Set<String> getIncomingChannels(Component component);

    /**
     * Gets a set containing all the incoming component channel registrations
     * that the specified component has.
     *
     * @param component Component to retrieve registrations for.
     * @return List of all registrations that the component has.
     * @throws IllegalArgumentException Thrown if component is null.
     */
    public Set<ComponentMessageListenerRegistration> getIncomingChannelRegistrations(Component component);

    /**
     * Gets a set containing all the incoming component channel registrations
     * that are on the requested channel.
     *
     * @param channel Channel to retrieve registrations for.
     * @return List of all registrations that are on the channel.
     * @throws IllegalArgumentException Thrown if channel is null.
     */
    public Set<ComponentMessageListenerRegistration> getIncomingChannelRegistrations(String channel);

    /**
     * Gets a set containing all the incoming plugin channel registrations
     * that the specified component has on the requested channel.
     *
     * @param component Component to retrieve registrations for.
     * @param channel Channel to filter registrations by.
     * @return List of all registrations that the component has.
     * @throws IllegalArgumentException Thrown if component or channel is null.
     */
    public Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(Component component, String channel);

    /**
     * Checks if the specified component message listener registration is valid.
     * <p>
     * A registration is considered valid if it has not be unregistered and
     * that the component is still enabled.
     *
     * @param registration Registration to check.
     * @return True if the registration is valid, otherwise false.
     */
    public boolean isRegistrationValid(ComponentMessageListenerRegistration registration);

    /**
     * Checks if the specified component has registered to receive incoming
     * messages through the requested channel.
     *
     * @param component Component to check registration for.
     * @param channel Channel to test for.
     * @return True if the channel is registered, else false.
     */
    public boolean isIncomingChannelRegistered(Component component, String channel);

    /**
     * Checks if the specified component has registered to send outgoing messages
     * through the requested channel.
     *
     * @param Component component to check registration for.
     * @param channel Channel to test for.
     * @return True if the channel is registered, else false.
     */
    public boolean isOutgoingChannelRegistered(Component component, String channel);

    /**
     * Dispatches the specified incoming message to any registered listeners.
     *
     * @param source Source of the message.
     * @param channel Channel that the message was sent by.
     * @param message Raw payload of the message.
     */
    public void dispatchIncomingMessage(Player source, String channel, byte[] message);
}
