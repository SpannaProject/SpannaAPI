package org.spanna.component;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.spanna.event.Event;
import org.spanna.event.Listener;

/**
 * Represents a component loader, which handles direct access to specific types
 * of components.
 */
public interface ComponentLoader {

    /**
     * Loads the component contained in the specified file
     *
     * @param file File to attempt to load
     * @return Component that was contained in the specified file, or null if
     *     unsuccessful
     * @throws InvalidComponentException Thrown when the specified file is not a
     *     component
     * @throws UnknownDependencyException If a required dependency could not
     *     be found
     */
    public Component loadComponent(File file) throws InvalidComponentException, UnknownDependencyException;

    /**
     * Loads a ComponentDescriptionFile from the specified file
     *
     * @param file File to attempt to load from
     * @return A new ComponentDescriptionFile loaded from the component.yml in the
     *     specified file
     * @throws InvalidDescriptionException If the component description file
     *     could not be created
     */
    public ComponentDescriptionFile getComponentDescription(File file) throws InvalidDescriptionException;

    /**
     * Returns a list of all filename filters expected by this ComponentLoader
     *
     * @return The filters
     */
    public Pattern[] getComponentFileFilters();

    /**
     * Creates and returns registered listeners for the event classes used in
     * this listener
     *
     * @param listener The object that will handle the eventual call back
     * @param component The component to use when creating registered listeners
     * @return The registered listeners.
     */
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Component component);

    /**
     * Enables the specified component
     * <p>
     * Attempting to enable a component that is already enabled will have no
     * effect
     *
     * @param component Component to enable
     */
    public void enableComponent(Component component);

    /**
     * Disables the specified component
     * <p>
     * Attempting to disable a component that is not enabled will have no effect
     *
     * @param component Component to disable
     */
    public void disableComponent(Component component);
}
