package org.spanna.component;

import java.io.File;
import java.util.Set;

import org.spanna.event.Event;
import org.spanna.event.EventPriority;
import org.spanna.event.Listener;
import org.spanna.permissions.Permissible;
import org.spanna.permissions.Permission;

/**
 * Handles all component management from the Server
 */
public interface ComponentManager {

    /**
     * Registers the specified component loader
     *
     * @param loader Class name of the ComponentLoader to register
     * @throws IllegalArgumentException Thrown when the given Class is not a
     *     valid ComponentLoader
     */
    public void registerInterface(Class<? extends ComponentLoader> loader) throws IllegalArgumentException;

    /**
     * Checks if the given component is loaded and returns it when applicable
     * <p>
     * Please note that the name of the component is case-sensitive
     *
     * @param name Name of the component to check
     * @return Component if it exists, otherwise null
     */
    public Component getComponent(String name);

    /**
     * Gets a list of all currently loaded components
     *
     * @return Array of Components
     */
    public Component[] getComponents();

    /**
     * Checks if the given component is enabled or not
     * <p>
     * Please note that the name of the component is case-sensitive.
     *
     * @param name Name of the component to check
     * @return true if the component is enabled, otherwise false
     */
    public boolean isComponentEnabled(String name);

    /**
     * Checks if the given component is enabled or not
     *
     * @param component Component to check
     * @return true if the component is enabled, otherwise false
     */
    public boolean isComponentEnabled(Component component);

    /**
     * Loads the component in the specified file
     * <p>
     * File must be valid according to the current enabled component interfaces
     *
     * @param file File containing the plugin to load
     * @return The component loaded, or null if it was invalid
     * @throws InvalidcomponentException Thrown when the specified file is not a
     *     valid component
     * @throws InvalidDescriptionException Thrown when the specified file
     *     contains an invalid description
     * @throws UnknownDependencyException If a required dependency could not
     *     be resolved
     */
    public Component loadComponent(File file) throws InvalidComponentException, InvalidDescriptionException, UnknownDependencyException;

    /**
     * Loads the components contained within the specified directory
     *
     * @param directory Directory to check for components
     * @return A list of all components loaded
     */
    public Component[] loadComponents(File directory);

    /**
     * Disables all the loaded components
     */
    public void disableComponents();

    /**
     * Disables and removes all components
     */
    public void clearComponents();

    /**
     * Calls an event with the given details
     *
     * @param event Event details
     * @throws IllegalStateException Thrown when an asynchronous event is
     *     fired from synchronous code.
     *     <p>
     *     <i>Note: This is best-effort basis, and should not be used to test
     *     synchronized state. This is an indicator for flawed flow logic.</i>
     */
    public void callEvent(Event event) throws IllegalStateException;

    /**
     * Registers all the events in the given listener class
     *
     * @param listener Listener to register
     * @param component Component to register
     */
    public void registerEvents(Listener listener, Component component);

    /**
     * Registers the specified executor to the given event class
     *
     * @param event Event type to register
     * @param listener Listener to register
     * @param priority Priority to register this event at
     * @param executor EventExecutor to register
     * @param component Component to register
     */
    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Component component);

    /**
     * Registers the specified executor to the given event class
     *
     * @param event Event type to register
     * @param listener Listener to register
     * @param priority Priority to register this event at
     * @param executor EventExecutor to register
     * @param component Component to register
     * @param ignoreCancelled Whether to pass cancelled events or not
     */
    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Component component, boolean ignoreCancelled);

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

    /**
     * Gets a {@link Permission} from its fully qualified name
     *
     * @param name Name of the permission
     * @return Permission, or null if none
     */
    public Permission getPermission(String name);

    /**
     * Adds a {@link Permission} to this component manager.
     * <p>
     * If a permission is already defined with the given name of the new
     * permission, an exception will be thrown.
     *
     * @param perm Permission to add
     * @throws IllegalArgumentException Thrown when a permission with the same
     *     name already exists
     */
    public void addPermission(Permission perm);

    /**
     * Removes a {@link Permission} registration from this component manager.
     * <p>
     * If the specified permission does not exist in this component manager,
     * nothing will happen.
     * <p>
     * Removing a permission registration will <b>not</b> remove the
     * permission from any {@link Permissible}s that have it.
     *
     * @param perm Permission to remove
     */
    public void removePermission(Permission perm);

    /**
     * Removes a {@link Permission} registration from this component manager.
     * <p>
     * If the specified permission does not exist in this component manager,
     * nothing will happen.
     * <p>
     * Removing a permission registration will <b>not</b> remove the
     * permission from any {@link Permissible}s that have it.
     *
     * @param name Permission to remove
     */
    public void removePermission(String name);

    /**
     * Gets the default permissions for the given op status
     *
     * @param op Which set of default permissions to get
     * @return The default permissions
     */
    public Set<Permission> getDefaultPermissions(boolean op);

    /**
     * Recalculates the defaults for the given {@link Permission}.
     * <p>
     * This will have no effect if the specified permission is not registered
     * here.
     *
     * @param perm Permission to recalculate
     */
    public void recalculatePermissionDefaults(Permission perm);

    /**
     * Subscribes the given Permissible for information about the requested
     * Permission, by name.
     * <p>
     * If the specified Permission changes in any form, the Permissible will
     * be asked to recalculate.
     *
     * @param permission Permission to subscribe to
     * @param permissible Permissible subscribing
     */
    public void subscribeToPermission(String permission, Permissible permissible);

    /**
     * Unsubscribes the given Permissible for information about the requested
     * Permission, by name.
     *
     * @param permission Permission to unsubscribe from
     * @param permissible Permissible subscribing
     */
    public void unsubscribeFromPermission(String permission, Permissible permissible);

    /**
     * Gets a set containing all subscribed {@link Permissible}s to the given
     * permission, by name
     *
     * @param permission Permission to query for
     * @return Set containing all subscribed permissions
     */
    public Set<Permissible> getPermissionSubscriptions(String permission);

    /**
     * Subscribes to the given Default permissions by operator status
     * <p>
     * If the specified defaults change in any form, the Permissible will be
     * asked to recalculate.
     *
     * @param op Default list to subscribe to
     * @param permissible Permissible subscribing
     */
    public void subscribeToDefaultPerms(boolean op, Permissible permissible);

    /**
     * Unsubscribes from the given Default permissions by operator status
     *
     * @param op Default list to unsubscribe from
     * @param permissible Permissible subscribing
     */
    public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible);

    /**
     * Gets a set containing all subscribed {@link Permissible}s to the given
     * default list, by op status
     *
     * @param op Default list to query for
     * @return Set containing all subscribed permissions
     */
    public Set<Permissible> getDefaultPermSubscriptions(boolean op);

    /**
     * Gets a set of all registered permissions.
     * <p>
     * This set is a copy and will not be modified live.
     *
     * @return Set containing all current registered permissions
     */
    public Set<Permission> getPermissions();

    /**
     * Returns whether or not timing code should be used for event calls
     *
     * @return True if event timings are to be used
     */
    public boolean useTimings();
}
