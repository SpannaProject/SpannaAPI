package org.spanna.component.java;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.spanna.component.InvalidComponentException;
import org.spanna.component.ComponentDescriptionFile;

/**
 * A ClassLoader for components, to allow shared classes across multiple components
 */
final class ComponentClassLoader extends URLClassLoader {
    private final JavaComponentLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private final ComponentDescriptionFile description;
    private final File dataFolder;
    private final File file;
    final JavaComponent component;
    private JavaComponent componentInit;
    private IllegalStateException pluginState;

    ComponentClassLoader(final JavaComponentLoader loader, final ClassLoader parent, final ComponentDescriptionFile description, final File dataFolder, final File file) throws InvalidComponentException, MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, parent);
        Validate.notNull(loader, "[ERROR] Loader cannot be null");

        this.loader = loader;
        this.description = description;
        this.dataFolder = dataFolder;
        this.file = file;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(description.getMain(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new InvalidComponentException("[ERROR] Cannot find main class `" + description.getMain() + "'", ex);
            }

            Class<? extends JavaComponent> pluginClass;
            try {
                componentClass = jarClass.asSubclass(JavaComponent.class);
            } catch (ClassCastException ex) {
                throw new InvalidComponentException("[ERROR] main class `" + description.getMain() + "' does not extend JavaComponent", ex);
            }

            component = ComponentClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new InvalidComponentException("[ERROR] No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new InvalidComponentException("[ERROR] Abnormal component type", ex);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        if (name.startsWith("org.spanna.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    loader.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

    Set<String> getClasses() {
        return classes.keySet();
    }

    synchronized void initialize(JavaComponent javaComponent) {
        Validate.notNull(javaComponent, "[ERROR] Initializing component cannot be null");
        Validate.isTrue(javaComponent.getClass().getClassLoader() == this, "[ERROR] Cannot initialize component outside of this class loader");
        if (this.component != null || this.componentInit != null) {
            throw new IllegalArgumentException("[ERROR] Component already initialized!", componentState);
        }

        componentState = new IllegalStateException("[SERVER] Initial initialization");
        this.componentInit = javaComponent;

        javaComponent.init(loader, loader.server, description, dataFolder, file, this);
    }
}
