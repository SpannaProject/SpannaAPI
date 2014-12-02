package org.spanna.component.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.spanna.Server;
import org.spanna.Warning;
import org.spanna.Warning.WarningState;
import org.spanna.configuration.serialization.ConfigurationSerializable;
import org.spanna.configuration.serialization.ConfigurationSerialization;
import org.spanna.event.Event;
import org.spanna.event.EventException;
import org.spanna.event.EventHandler;
import org.spanna.event.Listener;
import org.spanna.event.server.PluginDisableEvent;
import org.spanna.event.server.PluginEnableEvent;
import org.spanna.component.AuthorNagException;
import org.spanna.component.EventExecutor;
import org.spanna.component.InvalidDescriptionException;
import org.spanna.component.InvalidPluginException;
import org.spanna.component.Component;
import org.spanna.component.ComponentDescriptionFile;
import org.spanna.component.ComponentLoader;
import org.spanna.component.RegisteredListener;
import org.spanna.component.TimedRegisteredListener;
import org.spanna.component.UnknownDependencyException;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Represents a Java component loader, allowing components in the form of .jar
 */
public final class JavaComponentLoader implements ComponentLoader {
    final Server server;
    private final Pattern[] fileFilters = new Pattern[] { Pattern.compile("\\.jar$"), };
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private final Map<String, ComponentClassLoader> loaders = new LinkedHashMap<String, ComponentClassLoader>();

    /**
     * This class was not meant to be constructed explicitly
     */
    @Deprecated
    public JavaComponentLoader(Server instance) {
        Validate.notNull(instance, "[ERROR] Server cannot be null");
        server = instance;
    }

    public Component loadComponent(final File file) throws InvalidComponentException {
        Validate.notNull(file, "[ERROR] File cannot be null");

        if (!file.exists()) {
            throw new InvalidComponentException(new FileNotFoundException(file.getPath() + " does not exist"));
        }

        final ComponentDescriptionFile description;
        try {
            description = getComponentDescription(file);
        } catch (InvalidDescriptionException ex) {
            throw new InvalidComponentException(ex);
        }

        final File parentFile = file.getParentFile();
        final File dataFolder = new File(parentFile, description.getName());
        @SuppressWarnings("deprecation")
        final File oldDataFolder = new File(parentFile, description.getRawName());

        // Found old data folder
        if (dataFolder.equals(oldDataFolder)) {
            // They are equal -- nothing needs to be done!
        } else if (dataFolder.isDirectory() && oldDataFolder.isDirectory()) {
            server.getLogger().warning(String.format(
                "[SERVER] While loading %s (%s) found old-data folder: `%s' next to the new one `%s'",
                description.getFullName(),
                file,
                oldDataFolder,
                dataFolder
            ));
        } else if (oldDataFolder.isDirectory() && !dataFolder.exists()) {
            if (!oldDataFolder.renameTo(dataFolder)) {
                throw new InvalidComponentException("[ERROR] Unable to rename old data folder: `" + oldDataFolder + "' to: `" + dataFolder + "'");
            }
            server.getLogger().log(Level.INFO, String.format(
                "[SERVER] While loading %s (%s) renamed data folder: `%s' to `%s'",
                description.getFullName(),
                file,
                oldDataFolder,
                dataFolder
            ));
        }

        if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidComponentException(String.format(
                "[ERROR] Projected datafolder: `%s' for %s (%s) exists and is not a directory",
                dataFolder,
                description.getFullName(),
                file
            ));
        }

        for (final String componentName : description.getDepend()) {
            if (loaders == null) {
                throw new UnknownDependencyException(componentName);
            }
            ComponentClassLoader current = loaders.get(componentName);

            if (current == null) {
                throw new UnknownDependencyException(componentName);
            }
        }

        final ComponentClassLoader loader;
        try {
            loader = new ComponentClassLoader(this, getClass().getClassLoader(), description, dataFolder, file);
        } catch (InvalidComponentException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new InvalidComponentException(ex);
        }

        loaders.put(description.getName(), loader);

        return loader.component;
    }

    public ComponentDescriptionFile getComponentDescription(File file) throws InvalidDescriptionException {
        Validate.notNull(file, "[ERROR] File cannot be null");

        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("component.yml");

            if (entry == null) {
                throw new InvalidDescriptionException(new FileNotFoundException("[ERROR] Jar file does not contain component.yml"));
            }

            stream = jar.getInputStream(entry);

            return new ComponentDescriptionFile(stream);

        } catch (IOException ex) {
            throw new InvalidDescriptionException(ex);
        } catch (YAMLException ex) {
            throw new InvalidDescriptionException(ex);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public Pattern[] getComponentFileFilters() {
        return fileFilters.clone();
    }

    Class<?> getClassByName(final String name) {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (String current : loaders.keySet()) {
                ComponentClassLoader loader = loaders.get(current);

                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException cnfe) {}
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }

    void setClass(final String name, final Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);

            if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
                Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
                ConfigurationSerialization.registerClass(serializable);
            }
        }
    }

    private void removeClass(String name) {
        Class<?> clazz = classes.remove(name);

        try {
            if ((clazz != null) && (ConfigurationSerializable.class.isAssignableFrom(clazz))) {
                Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
                ConfigurationSerialization.unregisterClass(serializable);
            }
        } catch (NullPointerException ex) {
            // Boggle!
            // (Native methods throwing NPEs is not fun when you can't stop it before-hand)
        }
    }

    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, final Component component) {
        Validate.notNull(component, "[ERROR] Component can not be null");
        Validate.notNull(listener, "[ERROR] Listener can not be null");

        boolean useTimings = server.getComponentManager().useTimings();
        Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<Class<? extends Event>, Set<RegisteredListener>>();
        Set<Method> methods;
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            methods = new HashSet<Method>(publicMethods.length, Float.MAX_VALUE);
            for (Method method : publicMethods) {
                methods.add(method);
            }
            for (Method method : listener.getClass().getDeclaredMethods()) {
                methods.add(method);
            }
        } catch (NoClassDefFoundError e) {
            component.getLogger().severe("[ERROR] Component " + component.getDescription().getFullName() + " has failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.");
            return ret;
        }

        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                component.getLogger().severe(component.getDescription().getFullName() + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
                continue;
            }
            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            Set<RegisteredListener> eventSet = ret.get(eventClass);
            if (eventSet == null) {
                eventSet = new HashSet<RegisteredListener>();
                ret.put(eventClass, eventSet);
            }

            for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                // This loop checks for extending deprecated events
                if (clazz.getAnnotation(Deprecated.class) != null) {
                    Warning warning = clazz.getAnnotation(Warning.class);
                    WarningState warningState = server.getWarningState();
                    if (!warningState.printFor(warning)) {
                        break;
                    }
                    component.getLogger().log(
                            Level.WARNING,
                            String.format(
                                    "[ERROR] \"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated." +
                                    "[ERROR] \"%s\"; please notify the authors %s.",
                                    component.getDescription().getFullName(),
                                    clazz.getName(),
                                    method.toGenericString(),
                                    (warning != null && warning.reason().length() != 0) ? warning.reason() : "[JavaComponentLoader] Server performance will be affected!",
                                    Arrays.toString(component.getDescription().getAuthors().toArray())),
                            warningState == WarningState.ON ? new AuthorNagException(null) : null);
                    break;
                }
            }

            EventExecutor executor = new EventExecutor() {
                public void execute(Listener listener, Event event) throws EventException {
                    try {
                        if (!eventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }
                        method.invoke(listener, event);
                    } catch (InvocationTargetException ex) {
                        throw new EventException(ex.getCause());
                    } catch (Throwable t) {
                        throw new EventException(t);
                    }
                }
            };
            if (useTimings) {
                eventSet.add(new TimedRegisteredListener(listener, executor, eh.priority(), component, eh.ignoreCancelled()));
            } else {
                eventSet.add(new RegisteredListener(listener, executor, eh.priority(), component, eh.ignoreCancelled()));
            }
        }
        return ret;
    }

    public void enableComponent(final Component component) {
        Validate.isTrue(component instanceof JavaComponent, "[ERROR] Component is not associated with this ComponentLoader");

        if (!component.isEnabled()) {
            component.getLogger().info("[SERVER] [/\] Enabling " + component.getDescription().getFullName());

            JavaComponent jComponent = (JavaComponent) component;

            String componentName = JComponent.getDescription().getName();

            if (!loaders.containsKey(componentName)) {
                loaders.put(componentName, (ComponentClassLoader) JComponent.getClassLoader());
            }

            try {
                jComponent.setEnabled(true);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "[SERVER] An Error occurred while enabling " + component.getDescription().getFullName() + " (Is it up to date? Or is it corrupt?)", ex);
            }

            // Perhaps abort here, rather than continue going, but as it stands,
            // an abort is not possible the way it's currently written
            server.getComponentManager().callEvent(new ComponentEnableEvent(component));
        }
    }

    public void disableComponent(Component component) {
        Validate.isTrue(component instanceof JavaComponent, "[ERROR] Component is not associated with this ComponentLoader");

        if (component.isEnabled()) {
            String message = String.format("[SERVER] [\/] Disabling %s", component.getDescription().getFullName());
            component.getLogger().info(message);

            server.getComponentManager().callEvent(new ComponentDisableEvent(component));

            JavaComponent jComponent = (JavaComponent) component;
            ClassLoader cloader = jComponent.getClassLoader();

            try {
                jComponent.setEnabled(false);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "[SERVER] [-?-] Error occurred while disabling " + component.getDescription().getFullName() + " (Is it up to date? Or is it corrupt?)", ex);
            }

            loaders.remove(jComponent.getDescription().getName());

            if (cloader instanceof ComponentClassLoader) {
                ComponentClassLoader loader = (ComponentClassLoader) cloader;
                Set<String> names = loader.getClasses();

                for (String name : names) {
                    removeClass(name);
                }
            }
        }
    }
}
