package org.spanna.component;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.spanna.Server;
import org.spanna.command.Command;
import org.spanna.command.ComponentCommandYamlParser;
import org.spanna.command.SimpleCommandMap;
import org.spanna.event.Event;
import org.spanna.event.EventPriority;
import org.spanna.event.HandlerList;
import org.spanna.event.Listener;
import org.spanna.permissions.Permissible;
import org.spanna.permissions.Permission;
import org.spanna.permissions.PermissionDefault;
import org.spanna.util.FileUtil;

import com.google.common.collect.ImmutableSet;

/**
 * Handles all component management from the Server
 */
public final class SimpleComponentManager implements ComponentManager {
    private final Server server;
    private final Map<Pattern, ComponentLoader> fileAssociations = new HashMap<Pattern, ComponentLoader>();
    private final List<Component> components = new ArrayList<Component>();
    private final Map<String, Component> lookupNames = new HashMap<String, Component>();
    private static File updateDirectory = null;
    private final SimpleCommandMap commandMap;
    private final Map<String, Permission> permissions = new HashMap<String, Permission>();
    private final Map<Boolean, Set<Permission>> defaultPerms = new LinkedHashMap<Boolean, Set<Permission>>();
    private final Map<String, Map<Permissible, Boolean>> permSubs = new HashMap<String, Map<Permissible, Boolean>>();
    private final Map<Boolean, Map<Permissible, Boolean>> defSubs = new HashMap<Boolean, Map<Permissible, Boolean>>();
    private boolean useTimings = false;

    public SimpleComponentManager(Server instance, SimpleCommandMap commandMap) {
        server = instance;
        this.commandMap = commandMap;

        defaultPerms.put(true, new HashSet<Permission>());
        defaultPerms.put(false, new HashSet<Permission>());
    }

    /**
     * Registers the specified component loader.
     *
     * @param loader Class name of the ComponentLoader to register
     * @throws IllegalArgumentException Thrown when the given Class is not a
     *     valid ComponentLoader
     */
    public void registerInterface(Class<? extends ComponentLoader> loader) throws IllegalArgumentException {
        ComponentLoader instance;

        if (ComponentLoader.class.isAssignableFrom(loader)) {
            Constructor<? extends ComponentLoader> constructor;

            try {
                constructor = loader.getConstructor(Server.class);
                instance = constructor.newInstance(server);
            } catch (NoSuchMethodException ex) {
                String className = loader.getName();

                throw new IllegalArgumentException(String.format("Class %s does not have a public %s(Server) constructor", className, className), ex);
            } catch (Exception ex) {
                throw new IllegalArgumentException(String.format("Unexpected exception %s while attempting to construct a new instance of %s", ex.getClass().getName(), loader.getName()), ex);
            }
        } else {
            throw new IllegalArgumentException(String.format("Class %s does not implement interface ComponentLoader", loader.getName()));
        }

        Pattern[] patterns = instance.getComponentFileFilters();

        synchronized (this) {
            for (Pattern pattern : patterns) {
                fileAssociations.put(pattern, instance);
            }
        }
    }

    /**
     * Loads the components contained within the specified directory
     *
     * @param directory Directory to check for components
     * @return A list of all components loaded
     */
    public Component[] loadComponents(File directory) {
        Validate.notNull(directory, "Directory cannot be null");
        Validate.isTrue(directory.isDirectory(), "Directory must be a directory");

        List<Component> result = new ArrayList<Component>();
        Set<Pattern> filters = fileAssociations.keySet();

        if (!(server.getUpdateFolder().equals(""))) {
            updateDirectory = new File(directory, server.getUpdateFolder());
        }

        Map<String, File> components = new HashMap<String, File>();
        Set<String> loadedComponents = new HashSet<String>();
        Map<String, Collection<String>> dependencies = new HashMap<String, Collection<String>>();
        Map<String, Collection<String>> softDependencies = new HashMap<String, Collection<String>>();

        // This is where it figures out all possible components!
        for (File file : directory.listFiles()) {
            ComponentLoader loader = null;
            for (Pattern filter : filters) {
                Matcher match = filter.matcher(file.getName());
                if (match.find()) {
                    loader = fileAssociations.get(filter);
                }
            }

            if (loader == null) continue;

            ComponentDescriptionFile description = null;
            try {
                description = loader.getComponentDescription(file);
                String name = description.getName();
                if (name.equalsIgnoreCase("spanna") || name.equalsIgnoreCase("minecraft") || name.equalsIgnoreCase("mojang")) {
                    server.getLogger().log(Level.SEVERE, "[ERROR] Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': Restricted Name");
                    continue;
                } else if (description.rawName.indexOf(' ') != -1) {
                    server.getLogger().warning(String.format(
                        "[!] Component `%s' uses the space-character (0x20) in its name `%s' - this is discouraged!",
                        description.getFullName(),
                        description.rawName
                        ));
                }
            } catch (InvalidDescriptionException ex) {
                server.getLogger().log(Level.SEVERE, "[ERROR] Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                continue;
            }

            File replacedFile = components.put(description.getName(), file);
            if (replacedFile != null) {
                server.getLogger().severe(String.format(
                    "[!] Ambiguous component name `%s' for files `%s' and `%s' in `%s'",
                    description.getName(),
                    file.getPath(),
                    replacedFile.getPath(),
                    directory.getPath()
                    ));
            }

            Collection<String> softDependencySet = description.getSoftDepend();
            if (softDependencySet != null && !softDependencySet.isEmpty()) {
                if (softDependencies.containsKey(description.getName())) {
                    // Duplicates do not matter, they will be removed together if applicable
                    softDependencies.get(description.getName()).addAll(softDependencySet);
                } else {
                    softDependencies.put(description.getName(), new LinkedList<String>(softDependencySet));
                }
            }

            Collection<String> dependencySet = description.getDepend();
            if (dependencySet != null && !dependencySet.isEmpty()) {
                dependencies.put(description.getName(), new LinkedList<String>(dependencySet));
            }

            Collection<String> loadBeforeSet = description.getLoadBefore();
            if (loadBeforeSet != null && !loadBeforeSet.isEmpty()) {
                for (String loadBeforeTarget : loadBeforeSet) {
                    if (softDependencies.containsKey(loadBeforeTarget)) {
                        softDependencies.get(loadBeforeTarget).add(description.getName());
                    } else {
                        // softDependencies is never iterated, so 'ghost' components aren't an issue
                        Collection<String> shortSoftDependency = new LinkedList<String>();
                        shortSoftDependency.add(description.getName());
                        softDependencies.put(loadBeforeTarget, shortSoftDependency);
                    }
                }
            }
        }

        while (!components.isEmpty()) {
            boolean missingDependency = true;
            Iterator<String> componentIterator = components.keySet().iterator();

            while (componentIterator.hasNext()) {
                String component = componentIterator.next();

                if (dependencies.containsKey(component)) {
                    Iterator<String> dependencyIterator = dependencies.get(component).iterator();

                    while (dependencyIterator.hasNext()) {
                        String dependency = dependencyIterator.next();

                        // Dependency loaded
                        if (loadedComponents.contains(dependency)) {
                            dependencyIterator.remove();

                        // We have a dependency not found
                        } else if (!components.containsKey(dependency)) {
                            missingDependency = false;
                            File file = components.get(component);
                            componentIterator.remove();
                            softDependencies.remove(component);
                            dependencies.remove(component);

                            server.getLogger().log(
                                Level.SEVERE,
                                "[ERROR] Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'",
                                new UnknownDependencyException(dependency));
                            break;
                        }
                    }

                    if (dependencies.containsKey(component) && dependencies.get(component).isEmpty()) {
                        dependencies.remove(component);
                    }
                }
                if (softDependencies.containsKey(component)) {
                    Iterator<String> softDependencyIterator = softDependencies.get(component).iterator();

                    while (softDependencyIterator.hasNext()) {
                        String softDependency = softDependencyIterator.next();

                        // Soft depend is no longer around
                        if (!components.containsKey(softDependency)) {
                            softDependencyIterator.remove();
                        }
                    }

                    if (softDependencies.get(component).isEmpty()) {
                        softDependencies.remove(component);
                    }
                }
                if (!(dependencies.containsKey(component) || softDependencies.containsKey(component)) && components.containsKey(component)) {
                    // We're clear to load, no more soft or hard dependencies left
                    File file = components.get(component);
                    componentIterator.remove();
                    missingDependency = false;

                    try {
                        result.add(loadComponent(file));
                        loadedComponents.add(component);
                        continue;
                    } catch (InvalidComponentException ex) {
                        server.getLogger().log(Level.SEVERE, "[ERROR] Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                    }
                }
            }

            if (missingDependency) {
                // We now iterate over components until something loads
                // This loop will ignore soft dependencies
            	componentIterator = components.keySet().iterator();

                while (componentIterator.hasNext()) {
                    String component = componentIterator.next();

                    if (!dependencies.containsKey(component)) {
                        softDependencies.remove(component);
                        missingDependency = false;
                        File file = components.get(component);
                        componentIterator.remove();

                        try {
                            result.add(loadComponent(file));
                            loadedComponents.add(component);
                            break;
                        } catch (InvalidComponentException ex) {
                            server.getLogger().log(Level.SEVERE, "[ERROR] Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                        }
                    }
                }
                // We have no components left without a depend
                if (missingDependency) {
                    softDependencies.clear();
                    dependencies.clear();
                    Iterator<File> failedComponentIterator = components.values().iterator();

                    while (failedComponentIterator.hasNext()) {
                        File file = failedComponentIterator.next();
                        failedComponentIterator.remove();
                        server.getLogger().log(Level.SEVERE, "[ERROR] Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': circular dependency detected!");
                    }
                }
            }
        }

        return result.toArray(new Component[result.size()]);
    }

    /**
     * Loads the component in the specified file
     * <p>
     * File must be valid according to the current enabled component interfaces
     *
     * @param file File containing the component to load
     * @return The component loaded, or null if it was invalid
     * @throws InvalidComponentException Thrown when the specified file is not a
     *     valid component
     * @throws UnknownDependencyException If a required dependency could not
     *     be found
     */
    public synchronized Component loadComponent(File file) throws InvalidComponentException, UnknownDependencyException {
        Validate.notNull(file, "File cannot be null");

        checkUpdate(file);

        Set<Pattern> filters = fileAssociations.keySet();
        Component result = null;

        for (Pattern filter : filters) {
            String name = file.getName();
            Matcher match = filter.matcher(name);

            if (match.find()) {
                ComponentLoader loader = fileAssociations.get(filter);

                result = loader.loadComponent(file);
            }
        }

        if (result != null) {
        	components.add(result);
            lookupNames.put(result.getDescription().getName(), result);
        }

        return result;
    }

    private void checkUpdate(File file) {
        if (updateDirectory == null || !updateDirectory.isDirectory()) {
            return;
        }

        File updateFile = new File(updateDirectory, file.getName());
        if (updateFile.isFile() && FileUtil.copy(updateFile, file)) {
            updateFile.delete();
        }
    }

    /**
     * Checks if the given component is loaded and returns it when applicable
     * <p>
     * Please note that the name of the component is case-sensitive
     *
     * @param name Name of the component to check
     * @return Component if it exists, otherwise null
     */
    public synchronized Component getComponent(String name) {
        return lookupNames.get(name.replace(' ', '_'));
    }

    public synchronized Component[] getComponents() {
        return components.toArray(new Component[0]);
    }

    /**
     * Checks if the given component is enabled or not
     * <p>
     * Please note that the name of the component is case-sensitive.
     *
     * @param name Name of the component to check
     * @return true if the component is enabled, otherwise false
     */
    public boolean isComponentEnabled(String name) {
        Component component = getComponent(name);

        return isComponentEnabled(component);
    }

    /**
     * Checks if the given component is enabled or not
     *
     * @param component Component to check
     * @return true if the component is enabled, otherwise false
     */
    public boolean isComponentEnabled(Component component) {
        if ((component != null) && (components.contains(component))) {
            return component.isEnabled();
        } else {
            return false;
        }
    }

    public void enableComponent(final Component component) {
        if (!component.isEnabled()) {
            List<Command> componentCommands = ComponentCommandYamlParser.parse(component);

            if (!componentCommands.isEmpty()) {
                commandMap.registerAll(component.getDescription().getName(), componentCommands);
            }

            try {
            	component.getComponentLoader().enableComponent(component);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the component loader) while enabling " + component.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            HandlerList.bakeAll();
        }
    }

    public void disableComponents() {
        Component[] components = getComponents();
        for (int i = components.length - 1; i >= 0; i--) {
            disableComponent(components[i]);
        }
    }

    public void disableComponent(final Component component) {
        if (component.isEnabled()) {
            try {
            	component.getComponentLoader().disableComponent(component);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the component loader) while disabling " + component.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try {
                server.getScheduler().cancelTasks(component);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the component loader) while cancelling tasks for " + component.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try {
                server.getServicesManager().unregisterAll(component);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the component loader) while unregistering services for " + component.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try {
                HandlerList.unregisterAll(component);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the component loader) while unregistering events for " + component.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try {
                server.getMessenger().unregisterIncomingComponentChannel(component);
                server.getMessenger().unregisterOutgoingComponentChannel(component);
            } catch(Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the component loader) while unregistering component channels for " + component.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
        }
    }

    public void clearComponents() {
        synchronized (this) {
            disableComponents();
            components.clear();
            lookupNames.clear();
            HandlerList.unregisterAll();
            fileAssociations.clear();
            permissions.clear();
            defaultPerms.get(true).clear();
            defaultPerms.get(false).clear();
        }
    }

    /**
     * Calls an event with the given details.
     * <p>
     * This method only synchronizes when the event is not asynchronous.
     *
     * @param event Event details
     */
    public void callEvent(Event event) {
        if (event.isAsynchronous()) {
            if (Thread.holdsLock(this)) {
                throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from inside synchronized code.");
            }
            if (server.isPrimaryThread()) {
                throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from primary server thread.");
            }
            fireEvent(event);
        } else {
            synchronized (this) {
                fireEvent(event);
            }
        }
    }

    private void fireEvent(Event event) {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();

        for (RegisteredListener registration : listeners) {
            if (!registration.getComponent().isEnabled()) {
                continue;
            }

            try {
                registration.callEvent(event);
            } catch (AuthorNagException ex) {
                Component component = registration.getComponent();

                if (component.isNaggable()) {
                	component.setNaggable(false);

                    server.getLogger().log(Level.SEVERE, String.format(
                            "Nag author(s): '%s' of '%s' about the following: %s",
                            component.getDescription().getAuthors(),
                            component.getDescription().getFullName(),
                            ex.getMessage()
                            ));
                }
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "[ERROR] Could not pass event " + event.getEventName() + " to " + registration.getcomponent().getDescription().getFullName(), ex);
            }
        }
    }

    public void registerEvents(Listener listener, Component component) {
        if (!component.isEnabled()) {
            throw new IllegalComponentAccessException("[ERROR] Component attempted to register " + listener + " while not enabled");
        }

        for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : component.getComponentLoader().createRegisteredListeners(listener, component).entrySet()) {
            getEventListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
        }

    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Component component) {
        registerEvent(event, listener, priority, executor, component, false);
    }

    /**
     * Registers the given event to the specified listener using a directly
     * passed EventExecutor
     *
     * @param event Event class to register
     * @param listener PlayerListener to register
     * @param priority Priority of this event
     * @param executor EventExecutor to register
     * @param component Component to register
     * @param ignoreCancelled Do not call executor if event was already
     *     cancelled
     */
    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Component component, boolean ignoreCancelled) {
        Validate.notNull(listener, "Listener cannot be null");
        Validate.notNull(priority, "Priority cannot be null");
        Validate.notNull(executor, "Executor cannot be null");
        Validate.notNull(component, "Component cannot be null");

        if (!component.isEnabled()) {
            throw new IllegalComponentAccessException("[ERROR] Component attempted to register " + event + " while not enabled");
        }

        if (useTimings) {
            getEventListeners(event).register(new TimedRegisteredListener(listener, executor, priority, component, ignoreCancelled));
        } else {
            getEventListeners(event).register(new RegisteredListener(listener, executor, priority, component, ignoreCancelled));
        }
    }

    private HandlerList getEventListeners(Class<? extends Event> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalComponentAccessException(e.toString());
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalComponentAccessException("[ERROR] Unable to find handler list for event " + clazz.getName());
            }
        }
    }

    public Permission getPermission(String name) {
        return permissions.get(name.toLowerCase());
    }

    public void addPermission(Permission perm) {
        String name = perm.getName().toLowerCase();

        if (permissions.containsKey(name)) {
            throw new IllegalArgumentException("[ERROR] The permission " + name + " is already defined!");
        }

        permissions.put(name, perm);
        calculatePermissionDefault(perm);
    }

    public Set<Permission> getDefaultPermissions(boolean op) {
        return ImmutableSet.copyOf(defaultPerms.get(op));
    }

    public void removePermission(Permission perm) {
        removePermission(perm.getName());
    }

    public void removePermission(String name) {
        permissions.remove(name.toLowerCase());
    }

    public void recalculatePermissionDefaults(Permission perm) {
        if (permissions.containsValue(perm)) {
            defaultPerms.get(true).remove(perm);
            defaultPerms.get(false).remove(perm);

            calculatePermissionDefault(perm);
        }
    }

    private void calculatePermissionDefault(Permission perm) {
        if ((perm.getDefault() == PermissionDefault.OP) || (perm.getDefault() == PermissionDefault.TRUE)) {
            defaultPerms.get(true).add(perm);
            dirtyPermissibles(true);
        }
        if ((perm.getDefault() == PermissionDefault.NOT_OP) || (perm.getDefault() == PermissionDefault.TRUE)) {
            defaultPerms.get(false).add(perm);
            dirtyPermissibles(false);
        }
    }

    private void dirtyPermissibles(boolean op) {
        Set<Permissible> permissibles = getDefaultPermSubscriptions(op);

        for (Permissible p : permissibles) {
            p.recalculatePermissions();
        }
    }

    public void subscribeToPermission(String permission, Permissible permissible) {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = permSubs.get(name);

        if (map == null) {
            map = new WeakHashMap<Permissible, Boolean>();
            permSubs.put(name, map);
        }

        map.put(permissible, true);
    }

    public void unsubscribeFromPermission(String permission, Permissible permissible) {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = permSubs.get(name);

        if (map != null) {
            map.remove(permissible);

            if (map.isEmpty()) {
                permSubs.remove(name);
            }
        }
    }

    public Set<Permissible> getPermissionSubscriptions(String permission) {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = permSubs.get(name);

        if (map == null) {
            return ImmutableSet.of();
        } else {
            return ImmutableSet.copyOf(map.keySet());
        }
    }

    public void subscribeToDefaultPerms(boolean op, Permissible permissible) {
        Map<Permissible, Boolean> map = defSubs.get(op);

        if (map == null) {
            map = new WeakHashMap<Permissible, Boolean>();
            defSubs.put(op, map);
        }

        map.put(permissible, true);
    }

    public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {
        Map<Permissible, Boolean> map = defSubs.get(op);

        if (map != null) {
            map.remove(permissible);

            if (map.isEmpty()) {
                defSubs.remove(op);
            }
        }
    }

    public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
        Map<Permissible, Boolean> map = defSubs.get(op);

        if (map == null) {
            return ImmutableSet.of();
        } else {
            return ImmutableSet.copyOf(map.keySet());
        }
    }

    public Set<Permission> getPermissions() {
        return new HashSet<Permission>(permissions.values());
    }

    public boolean useTimings() {
        return useTimings;
    }

    /**
     * Sets whether or not per event timing code should be used
     *
     * @param use True if per event timing code should be used
     */
    public void useTimings(boolean use) {
        useTimings = use;
    }
}
