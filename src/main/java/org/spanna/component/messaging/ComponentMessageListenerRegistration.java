package org.spanna.component.messaging;

import org.spanna.component.Component;

/**
 * Contains information about a {@link Component}s registration to a component
 * channel.
 */
public final class ComponentMessageListenerRegistration {
    private final Messenger messenger;
    private final Component component;
    private final String channel;
    private final ComponentMessageListener listener;

    public ComponentMessageListenerRegistration(Messenger messenger, Component component, String channel, ComponentMessageListener listener) {
        if (messenger == null) {
            throw new IllegalArgumentException("[ERROR] Messenger cannot be null!");
        }
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null!");
        }
        if (channel == null) {
            throw new IllegalArgumentException("[ERROR] Channel cannot be null!");
        }
        if (listener == null) {
            throw new IllegalArgumentException("[ERROR] Listener cannot be null!");
        }

        this.messenger = messenger;
        this.component = component;
        this.channel = channel;
        this.listener = listener;
    }

    /**
     * Gets the component channel that this registration is about.
     *
     * @return Component channel.
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Gets the registered listener described by this registration.
     *
     * @return Registered listener.
     */
    public ComponentMessageListener getListener() {
        return listener;
    }

    /**
     * Gets the component that this registration is for.
     *
     * @return Registered component.
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Checks if this registration is still valid.
     *
     * @return True if this registration is still valid, otherwise false.
     */
    public boolean isValid() {
        return messenger.isRegistrationValid(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ComponentMessageListenerRegistration other = (ComponentMessageListenerRegistration) obj;
        if (this.messenger != other.messenger && (this.messenger == null || !this.messenger.equals(other.messenger))) {
            return false;
        }
        if (this.component != other.component && (this.component == null || !this.component.equals(other.component))) {
            return false;
        }
        if ((this.channel == null) ? (other.channel != null) : !this.channel.equals(other.channel)) {
            return false;
        }
        if (this.listener != other.listener && (this.listener == null || !this.listener.equals(other.listener))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.messenger != null ? this.messenger.hashCode() : 0);
        hash = 53 * hash + (this.component != null ? this.component.hashCode() : 0);
        hash = 53 * hash + (this.channel != null ? this.channel.hashCode() : 0);
        hash = 53 * hash + (this.listener != null ? this.listener.hashCode() : 0);
        return hash;
    }
}
