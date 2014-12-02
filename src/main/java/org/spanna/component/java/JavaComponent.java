package org.spanna.component.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;
import org.spanna.Server;
import org.spanna.Warning.WarningState;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.command.PluginCommand;
import org.spanna.configuration.InvalidConfigurationException;
import org.spanna.configuration.file.FileConfiguration;
import org.spanna.configuration.file.YamlConfiguration;
import org.spanna.generator.ChunkGenerator;
import org.spanna.component.AuthorNagException;
import org.spanna.component.ComponentAwareness;
import org.spanna.component.ComponentBase;
import org.spanna.component.ComponentDescriptionFile;
import org.spanna.component.ComponentLoader;
import org.spanna.component.ComponentLogger;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

/**
 * Represents a Java component
 */
public abstract class JavaComponent extends ComponentBase {
    private boolean isEnabled = false;
    private ComponentLoader loader = null;
    private Server server = null;
    private File file = null;
    private ComponentDescriptionFile description = null;
    private File dataFolder = null;
    private ClassLoader classLoader = null;
    private boolean naggable = true;
    private EbeanServer ebean = null;
    private FileConfiguration newConfig = null;
    private File configFile = null;
    private ComponentLogger logger = null;

    public JavaComponent() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof ComponentClassLoader)) {
            throw new IllegalStateException("[ERROR] JavaPlugin requires " + ComponentClassLoader.class.getName());
        }
        ((ComponentClassLoader) classLoader).initialize(this);
    }

    /**
     * @deprecated This method is intended for unit testing purposes when the
     *     other {@linkplain #JavaComponent(JavaComponentLoader,
     *     ComponentDescriptionFile, File, File) constructor} cannot be used.
     *     <p>
     *     Its existence may be temporary.
     */
    @Deprecated
    protected JavaComponent(final ComponentLoader loader, final Server server, final ComponentDescriptionFile description, final File dataFolder, final File file) {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader instanceof ComponentClassLoader) {
            throw new IllegalStateException("[ERROR] Cannot use initialization constructor at runtime");
        }
        init(loader, server, description, dataFolder, file, classLoader);
    }

    protected JavaComponent(final JavaComponentLoader loader, final ComponentDescriptionFile description, final File dataFolder, final File file) {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader instanceof ComponentClassLoader) {
            throw new IllegalStateException("[ERROR] Cannot use initialization constructor at runtime");
        }
        init(loader, loader.server, description, dataFolder, file, classLoader);
    }

    /**
     * Returns the folder that the component data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder.
     */
    @Override
    public final File getDataFolder() {
        return dataFolder;
    }

    /**
     * Gets the associated ComponentLoader responsible for this component
     *
     * @return ComponentLoader that controls this component
     */
    @Override
    public final ComponentLoader getComponentLoader() {
        return loader;
    }

    /**
     * Returns the Server instance currently running this component
     *
     * @return Server running this component
     */
    @Override
    public final Server getServer() {
        return server;
    }

    /**
     * Returns a value indicating whether or not this component is currently
     * enabled
     *
     * @return true if this component is enabled, otherwise false
     */
    @Override
    public final boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Returns the file which contains this component
     *
     * @return File containing this component
     */
    protected File getFile() {
        return file;
    }

    /**
     * Returns the component.yaml file containing the details for this component
     *
     * @return Contents of the component.yaml file
     */
    @Override
    public final ComponentDescriptionFile getDescription() {
        return description;
    }

    @Override
    public FileConfiguration getConfig() {
        if (newConfig == null) {
            reloadConfig();
        }
        return newConfig;
    }

    /**
     * Provides a reader for a text file located inside the jar. The behavior
     * of this method adheres to {@link ComponentAwareness.Flags#UTF8}, or if not
     * defined, uses UTF8 if {@link FileConfiguration#UTF8_OVERRIDE} is
     * specified, or system default otherwise.
     *
     * @param file the filename of the resource to load
     * @return null if {@link #getResource(String)} returns null
     * @throws IllegalArgumentException if file is null
     * @see ClassLoader#getResourceAsStream(String)
     */
    @SuppressWarnings("deprecation")
    protected final Reader getTextResource(String file) {
        final InputStream in = getResource(file);

        return in == null ? null : new InputStreamReader(in, isStrictlyUTF8() || FileConfiguration.UTF8_OVERRIDE ? Charsets.UTF_8 : Charset.defaultCharset());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void reloadConfig() {
        newConfig = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = getResource("config.yml");
        if (defConfigStream == null) {
            return;
        }

        final YamlConfiguration defConfig;
        if (isStrictlyUTF8() || FileConfiguration.UTF8_OVERRIDE) {
            defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));
        } else {
            final byte[] contents;
            defConfig = new YamlConfiguration();
            try {
                contents = ByteStreams.toByteArray(defConfigStream);
            } catch (final IOException e) {
                getLogger().log(Level.SEVERE, "[ERROR] Unexpected failure reading config.yml", e);
                return;
            }

            final String text = new String(contents, Charset.defaultCharset());
            if (!text.equals(new String(contents, Charsets.UTF_8))) {
                getLogger().warning("[ERROR] Default system encoding may have misread config.yml from component jar");
            }

            try {
                defConfig.loadFromString(text);
            } catch (final InvalidConfigurationException e) {
                getLogger().log(Level.SEVERE, "[ERROR] Cannot load configuration from jar", e);
            }
        }

        newConfig.setDefaults(defConfig);
    }

    private boolean isStrictlyUTF8() {
        return getDescription().getAwareness().contains(ComponentAwareness.Flags.UTF8);
    }

    @Override
    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "[ERROR] Could not save config to " + configFile, ex);
        }
    }

    @Override
    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("[ERROR] ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("[ERROR] The embedded resource '" + resourcePath + "' cannot be found in " + file);
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                logger.log(Level.WARNING, "[ERROR] Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "[ERROR] Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Override
    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("[ERROR] Filename cannot be null");
        }

        try {
            URL url = getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Returns the ClassLoader which holds this component
     *
     * @return ClassLoader holding this component
     */
    protected final ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the enabled state of this component
     *
     * @param enabled true if enabled, otherwise false
     */
    protected final void setEnabled(final boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;

            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    /**
     * @deprecated This method is legacy and will be removed - it must be
     *     replaced by the specially provided constructor(s).
     */
    @Deprecated
    protected final void initialize(ComponentLoader loader, Server server, ComponentDescriptionFile description, File dataFolder, File file, ClassLoader classLoader) {
        if (server.getWarningState() == WarningState.OFF) {
            return;
        }
        getLogger().log(Level.WARNING, "[ERROR] " + getClass().getName() + " is already initialized", server.getWarningState() == WarningState.DEFAULT ? null : new AuthorNagException("[SERVER] Explicit initialization"));
    }

    final void init(ComponentLoader loader, Server server, ComponentDescriptionFile description, File dataFolder, File file, ClassLoader classLoader) {
        this.loader = loader;
        this.server = server;
        this.file = file;
        this.description = description;
        this.dataFolder = dataFolder;
        this.classLoader = classLoader;
        this.configFile = new File(dataFolder, "config.yml");
        this.logger = new PluginLogger(this);

        if (description.isDatabaseEnabled()) {
            ServerConfig db = new ServerConfig();

            db.setDefaultServer(false);
            db.setRegister(false);
            db.setClasses(getDatabaseClasses());
            db.setName(description.getName());
            server.configureDbConfig(db);

            DataSourceConfig ds = db.getDataSourceConfig();

            ds.setUrl(replaceDatabaseString(ds.getUrl()));
            dataFolder.mkdirs();

            ClassLoader previous = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader(classLoader);
            ebean = EbeanServerFactory.create(db);
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    /**
     * Provides a list of all classes that should be persisted in the database
     *
     * @return List of Classes that are Ebeans
     */
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<Class<?>>();
    }

    private String replaceDatabaseString(String input) {
        input = input.replaceAll("\\{DIR\\}", dataFolder.getPath().replaceAll("\\\\", "/") + "/");
        input = input.replaceAll("\\{NAME\\}", description.getName().replaceAll("[^\\w_-]", ""));
        return input;
    }

    /**
     * Gets the initialization status of this component
     *
     * @return true if this component is initialized, otherwise false
     * @deprecated This method cannot return false, as {@link
     *     JavaComponent} is now initialized in the constructor.
     */
    @Deprecated
    public final boolean isInitialized() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    /**
     * Gets the command with the given name, specific to this component. Commands
     * need to be registered in the {@link PluginDescriptionFile#getCommands()
     * ComponentDescriptionFile} to exist at runtime.
     *
     * @param name name or alias of the command
     * @return the component command if found, otherwise null
     */
    public ComponentCommand getCommand(String name) {
        String alias = name.toLowerCase();
        ComponentCommand command = getServer().getComponentCommand(alias);

        if (command == null || command.getComponent() != this) {
            command = getServer().getComponentCommand(description.getName().toLowerCase() + ":" + alias);
        }

        if (command != null && command.getComponent() == this) {
            return command;
        } else {
            return null;
        }
    }

    @Override
    public void onLoad() {}

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return null;
    }

    @Override
    public final boolean isNaggable() {
        return naggable;
    }

    @Override
    public final void setNaggable(boolean canNag) {
        this.naggable = canNag;
    }

    @Override
    public EbeanServer getDatabase() {
        return ebean;
    }

    protected void installDDL() {
        SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
        DdlGenerator gen = serv.getDdlGenerator();

        gen.runScript(false, gen.generateCreateDdl());
    }

    protected void removeDDL() {
        SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
        DdlGenerator gen = serv.getDdlGenerator();

        gen.runScript(true, gen.generateDropDdl());
    }

    @Override
    public final Logger getLogger() {
        return logger;
    }

    @Override
    public String toString() {
        return description.getFullName();
    }

    /**
     * This method provides fast access to the plugin that has {@link
     * #getProvidingComponent(Class) provided} the given component class, which is
     * usually the component that implemented it.
     * <p>
     * An exception to this would be if component's jar that contained the class
     * does not extend the class, where the intended component would have
     * resided in a different jar / classloader.
     *
     * @param clazz the class desired
     * @return the component that provides and implements said class
     * @throws IllegalArgumentException if clazz is null
     * @throws IllegalArgumentException if clazz does not extend {@link
     *     JavaComponent}
     * @throws IllegalStateException if clazz was not provided by a component,
     *     for example, if called with
     *     <code>JavaComponent.getComponent(JavaComponent.class)</code>
     * @throws IllegalStateException if called from the static initializer for
     *     given JavaComponent
     * @throws ClassCastException if component that provided the class does not
     *     extend the class
     */
    public static <T extends JavaComponent> T getComponent(Class<T> clazz) {
        Validate.notNull(clazz, "[ERROR] Null class cannot have a component");
        if (!JavaComponent.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("[ERROR] " + clazz + " does not extend " + JavaComponent.class);
        }
        final ClassLoader cl = clazz.getClassLoader();
        if (!(cl instanceof ComponentClassLoader)) {
            throw new IllegalArgumentException("[ERROR] " + clazz + " is not initialized by " + ComponentClassLoader.class);
        }
        JavaComponent component = ((ComponentClassLoader) cl).component;
        if (component == null) {
            throw new IllegalStateException("[ERROR] Cannot get component for " + clazz + " from a static initializer");
        }
        return clazz.cast(component);
    }

    /**
     * This method provides fast access to the component that has provided the
     * given class.
     *
     * @throws IllegalArgumentException if the class is not provided by a
     *     JavaComponent
     * @throws IllegalArgumentException if class is null
     * @throws IllegalStateException if called from the static initializer for
     *     given JavaComponent
     */
    public static JavaComponent getProvidingComponent(Class<?> clazz) {
        Validate.notNull(clazz, "[ERROR] Null class cannot have a component");
        final ClassLoader cl = clazz.getClassLoader();
        if (!(cl instanceof ComponentClassLoader)) {
            throw new IllegalArgumentException(clazz + " is not provided by " + ComponentClassLoader.class);
        }
        JavaComponent component = ((ComponentClassLoader) cl).component;
        if (component == null) {
            throw new IllegalStateException("[ERROR] Cannot get component for " + clazz + " from a static initializer");
        }
        return component;
    }
}
