package org.spanna.component.messaging;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.spanna.entity.Player;
import org.spanna.component.Component;

/**
 * Standard implementation to {@link Messenger}
 */
public class StandardMessenger implements Messenger {
    private final Map<String, Set<ComponentMessageListenerRegistration>> incomingByChannel = new HashMap<String, Set<ComponentMessageListenerRegistration>>();
    private final Map<Component, Set<ComponentMessageListenerRegistration>> incomingByComponent = new HashMap<Component, Set<ComponentMessageListenerRegistration>>();
    private final Map<String, Set<Component>> outgoingByChannel = new HashMap<String, Set<Component>>();
    private final Map<Component, Set<String>> outgoingByComponent = new HashMap<Component, Set<String>>();
    private final Object incomingLock = new Object();
    private final Object outgoingLock = new Object();

    private void addToOutgoing(Component component, String channel) {
        synchronized (outgoingLock) {
            Set<Component> components = outgoingByChannel.get(channel);
            Set<String> channels = outgoingByComponent.get(component);

            if (components == null) {
                components = new HashSet<Component>();
                outgoingByChannel.put(channel, components);
            }

            if (channels == null) {
                channels = new HashSet<String>();
                outgoingByComponent.put(component, channels);
            }

            components.add(component);
            channels.add(channel);
        }
    }

    private void removeFromOutgoing(Component component, String channel) {
        synchronized (outgoingLock) {
            Set<Component> components = outgoingByChannel.get(channel);
            Set<String> channels = outgoingByComponent.get(component);

            if (components != null) {
                components.remove(component);

                if (components.isEmpty()) {
                    outgoingByChannel.remove(channel);
                }
            }

            if (channels != null) {
                channels.remove(channel);

                if (channels.isEmpty()) {
                    outgoingByChannel.remove(channel);
                }
            }
        }
    }

    private void removeFromOutgoing(Component component) {
        synchronized (outgoingLock) {
            Set<String> channels = outgoingByComponent.get(component);

            if (channels != null) {
                String[] toRemove = channels.toArray(new String[0]);

                outgoingByComponent.remove(component);

                for (String channel : toRemove) {
                    removeFromOutgoing(component, channel);
                }
            }
        }
    }

    private void addToIncoming(ComponentMessageListenerRegistration registration) {
        synchronized (incomingLock) {
            Set<ComponentMessageListenerRegistration> registrations = incomingByChannel.get(registration.getChannel());

            if (registrations == null) {
                registrations = new HashSet<ComponentMessageListenerRegistration>();
                incomingByChannel.put(registration.getChannel(), registrations);
            } else {
                if (registrations.contains(registration)) {
                    throw new IllegalArgumentException("[ERROR] This registration already exists");
                }
            }

            registrations.add(registration);

            registrations = incomingByComponent.get(registration.getComponent());

            if (registrations == null) {
                registrations = new HashSet<ComponentMessageListenerRegistration>();
                incomingByComponent.put(registration.getComponent(), registrations);
            } else {
                if (registrations.contains(registration)) {
                    throw new IllegalArgumentException("[ERROR] This registration already exists");
                }
            }

            registrations.add(registration);
        }
    }

    private void removeFromIncoming(ComponentMessageListenerRegistration registration) {
        synchronized (incomingLock) {
            Set<ComponentMessageListenerRegistration> registrations = incomingByChannel.get(registration.getChannel());

            if (registrations != null) {
                registrations.remove(registration);

                if (registrations.isEmpty()) {
                    incomingByChannel.remove(registration.getChannel());
                }
            }

            registrations = incomingByComponent.get(registration.getComponent());

            if (registrations != null) {
                registrations.remove(registration);

                if (registrations.isEmpty()) {
                    incomingByComponent.remove(registration.getComponent());
                }
            }
        }
    }

    private void removeFromIncoming(Component component, String channel) {
        synchronized (incomingLock) {
            Set<ComponentMessageListenerRegistration> registrations = incomingByComponent.get(component);

            if (registrations != null) {
                ComponentMessageListenerRegistration[] toRemove = registrations.toArray(new ComponentMessageListenerRegistration[0]);

                for (ComponentMessageListenerRegistration registration : toRemove) {
                    if (registration.getChannel().equals(channel)) {
                        removeFromIncoming(registration);
                    }
                }
            }
        }
    }

    private void removeFromIncoming(Component component) {
        synchronized (incomingLock) {
            Set<ComponentMessageListenerRegistration> registrations = incomingByComponent.get(component);

            if (registrations != null) {
                ComponentMessageListenerRegistration[] toRemove = registrations.toArray(new ComponentMessageListenerRegistration[0]);

                incomingByComponent.remove(component);

                for (ComponentMessageListenerRegistration registration : toRemove) {
                    removeFromIncoming(registration);
                }
            }
        }
    }

    public boolean isReservedChannel(String channel) {
        validateChannel(channel);

        return channel.equals("REGISTER") || channel.equals("UNREGISTER");
    }

    public void registerOutgoingComponentChannel(Component component, String channel) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }
        validateChannel(channel);
        if (isReservedChannel(channel)) {
            throw new ReservedChannelException(channel);
        }

        addToOutgoing(component, channel);
    }

    public void unregisterOutgoingComponentChannel(Component component, String channel) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }
        validateChannel(channel);

        removeFromOutgoing(component, channel);
    }

    public void unregisterOutgoingComponentChannel(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }

        removeFromOutgoing(component);
    }

    public ComponentMessageListenerRegistration registerIncomingComponentChannel(Component component, String channel, ComponentMessageListener listener) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }
        validateChannel(channel);
        if (isReservedChannel(channel)) {
            throw new ReservedChannelException(channel);
        }
        if (listener == null) {
            throw new IllegalArgumentException("[ERROR] Listener cannot be null");
        }

        ComponentMessageListenerRegistration result = new ComponentMessageListenerRegistration(this, component, channel, listener);

        addToIncoming(result);

        return result;
    }

    public void unregisterIncomingComponentChannel(Component component, String channel, ComponentMessageListener listener) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("[ERROR] Listener cannot be null");
        }
        validateChannel(channel);

        removeFromIncoming(new ComponentMessageListenerRegistration(this, component, channel, listener));
    }

    public void unregisterIncomingComponentChannel(Component component, String channel) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }
        validateChannel(channel);

        removeFromIncoming(component, channel);
    }

    public void unregisterIncomingComponentChannel(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }

        removeFromIncoming(component);
    }

    public Set<String> getOutgoingChannels() {
        synchronized (outgoingLock) {
            Set<String> keys = outgoingByChannel.keySet();
            return ImmutableSet.copyOf(keys);
        }
    }

    public Set<String> getOutgoingChannels(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }

        synchronized (outgoingLock) {
            Set<String> channels = outgoingByComponent.get(component);

            if (channels != null) {
                return ImmutableSet.copyOf(channels);
            } else {
                return ImmutableSet.of();
            }
        }
    }

    public Set<String> getIncomingChannels() {
        synchronized (incomingLock) {
            Set<String> keys = incomingByChannel.keySet();
            return ImmutableSet.copyOf(keys);
        }
    }

    public Set<String> getIncomingChannels(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }

        synchronized (incomingLock) {
            Set<ComponentMessageListenerRegistration> registrations = incomingByComponent.get(component);

            if (registrations != null) {
                Builder<String> builder = ImmutableSet.builder();

                for (ComponentMessageListenerRegistration registration : registrations) {
                    builder.add(registration.getChannel());
                }

                return builder.build();
            } else {
                return ImmutableSet.of();
            }
        }
    }

    public Set<ComponentMessageListenerRegistration> getIncomingChannelRegistrations(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }

        synchronized (incomingLock) {
            Set<ComponentMessageListenerRegistration> registrations = incomingByComponent.get(component);

            if (registrations != null) {
                return ImmutableSet.copyOf(registrations);
            } else {
                return ImmutableSet.of();
            }
        }
    }

    public Set<ComponentMessageListenerRegistration> getIncomingChannelRegistrations(String channel) {
        validateChannel(channel);

        synchronized (incomingLock) {
            Set<ComponentMessageListenerRegistration> registrations = incomingByChannel.get(channel);

            if (registrations != null) {
                return ImmutableSet.copyOf(registrations);
            } else {
                return ImmutableSet.of();
            }
        }
    }

    public Set<ComponentMessageListenerRegistration> getIncomingChannelRegistrations(Component component, String channel) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }
        validateChannel(channel);

        synchronized (incomingLock) {
            Set<ComponentMessageListenerRegistration> registrations = incomingByComponent.get(component);

            if (registrations != null) {
                Builder<ComponentMessageListenerRegistration> builder = ImmutableSet.builder();

                for (ComponentMessageListenerRegistration registration : registrations) {
                    if (registration.getChannel().equals(channel)) {
                        builder.add(registration);
                    }
                }

                return builder.build();
            } else {
                return ImmutableSet.of();
            }
        }
    }

    public boolean isRegistrationValid(ComponentMessageListenerRegistration registration) {
        if (registration == null) {
            throw new IllegalArgumentException("[ERROR] Registration cannot be null");
        }

        synchronized (incomingLock) {
            Set<ComponentMessageListenerRegistration> registrations = incomingByComponent.get(registration.getComponent());

            if (registrations != null) {
                return registrations.contains(registration);
            }

            return false;
        }
    }

    public boolean isIncomingChannelRegistered(Component component, String channel) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }
        validateChannel(channel);

        synchronized (incomingLock) {
            Set<ComponentMessageListenerRegistration> registrations = incomingByComponent.get(component);

            if (registrations != null) {
                for (ComponentMessageListenerRegistration registration : registrations) {
                    if (registration.getChannel().equals(channel)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean isOutgoingChannelRegistered(Component component, String channel) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        }
        validateChannel(channel);

        synchronized (outgoingLock) {
            Set<String> channels = outgoingByComponent.get(component);

            if (channels != null) {
                return channels.contains(channel);
            }

            return false;
        }
    }

    public void dispatchIncomingMessage(Player source, String channel, byte[] message) {
        if (source == null) {
            throw new IllegalArgumentException("[ERROR] Player source cannot be null");
        }
        if (message == null) {
            throw new IllegalArgumentException("[ERROR] Message cannot be null");
        }
        validateChannel(channel);

        Set<ComponentMessageListenerRegistration> registrations = getIncomingChannelRegistrations(channel);

        for (ComponentMessageListenerRegistration registration : registrations) {
            registration.getListener().onComponentMessageReceived(channel, source, message);
        }
    }

    /**
     * Validates a Component Channel name.
     *
     * @param channel Channel name to validate.
     */
    public static void validateChannel(String channel) {
        if (channel == null) {
            throw new IllegalArgumentException("[ERROR] Channel cannot be null");
        }
        if (channel.length() > Messenger.MAX_CHANNEL_SIZE) {
            throw new ChannelNameTooLongException(channel);
        }
    }

    /**
     * Validates the input of a Component Message, ensuring the arguments are all
     * valid.
     *
     * @param messenger Messenger to use for validation.
     * @param source Source component of the Message.
     * @param channel Component Channel to send the message by.
     * @param message Raw message payload to send.
     * @throws IllegalArgumentException Thrown if the source plugin is
     *     disabled.
     * @throws IllegalArgumentException Thrown if source, channel or message
     *     is null.
     * @throws MessageTooLargeException Thrown if the message is too big.
     * @throws ChannelNameTooLongException Thrown if the channel name is too
     *     long.
     * @throws ChannelNotRegisteredException Thrown if the channel is not
     *     registered for this component.
     */
    public static void validateComponentMessage(Messenger messenger, Component source, String channel, byte[] message) {
        if (messenger == null) {
            throw new IllegalArgumentException("[ERROR] Messenger cannot be null");
        }
        if (source == null) {
            throw new IllegalArgumentException("[ERROR] Component source cannot be null");
        }
        if (!source.isEnabled()) {
            throw new IllegalArgumentException("[ERROR] Component must be enabled to send messages");
        }
        if (message == null) {
            throw new IllegalArgumentException("[ERROR] Message cannot be null");
        }
        if (!messenger.isOutgoingChannelRegistered(source, channel)) {
            throw new ChannelNotRegisteredException(channel);
        }
        if (message.length > Messenger.MAX_MESSAGE_SIZE) {
            throw new MessageTooLargeException(message);
        }
        validateChannel(channel);
    }
}
