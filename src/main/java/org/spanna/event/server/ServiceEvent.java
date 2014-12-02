package org.spanna.event.server;

import org.spanna.component.RegisteredServiceProvider;

/**
 * An event relating to a registered service. This is called in a {@link
 * org.spanna.component.ServicesManager}
 */
public abstract class ServiceEvent extends ServerEvent {
    private final RegisteredServiceProvider<?> provider;

    public ServiceEvent(final RegisteredServiceProvider<?> provider) {
        this.provider = provider;
    }

    public RegisteredServiceProvider<?> getProvider() {
        return provider;
    }
}