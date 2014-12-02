package org.spanna.component;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import org.spanna.Server;
import org.spanna.command.TabExecutor;
import org.spanna.configuration.file.FileConfiguration;
import org.spanna.generator.ChunkGenerator;

import com.avaje.ebean.EbeanServer;

/**
 * Represents a Component
 * <p>
 * The use of {@link ComponentBase} is recommended for actual Implementation
 */
public interface Component extends TabExecutor {
    /**
     * Returns the folder that the component data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder
     */
    public File getDataFolder();

    /**
     * Returns the component.yaml file containing the details for this component
     *
     * @return Contents of the component.yaml file
     */
    public ComponentDescriptionFile getDescription();

    /**
     * Gets a {@link FileConfiguration} for this component, read through
     * "config.yml"
     * <p>
     * If there is a default config.yml embedded in this component, it will be
     * provided as a default for this Configuration.
     *
     * @return Component configuration
     */
    public FileConfiguration getConfig();

    /**
     * Gets an embedded resource in this component
     *
     * @param filename Filename of the resource
     * @return File if found, otherwise null
     */
    public InputStream getResource(String filename);

    /**
     * Saves the {@link FileConfiguration} retrievable by {@link #getConfig()}.
     */
    public void saveConfig();

    /**
     * Saves the raw contents of the default config.yml file to the location
     * retrievable by {@link #getConfig()}. If there is no default config.yml
     * embedded in the plugin, an empty config.yml file is saved. This should
     * fail silently if the config.yml already exists.
     */
    public void saveDefaultConfig();

    /**
     * Saves the raw contents of any resource embedded with a component's .jar
     * file assuming it can be found using {@link #getResource(String)}.
     * <p>
     * The resource is saved into the component's data folder using the same
     * hierarchy as the .jar file (subdirectories are preserved).
     *
     * @param resourcePath the embedded resource path to look for within the
     *     component's .jar file. (No preceding slash).
     * @param replace if true, the embedded resource will overwrite the
     *     contents of an existing file.
     * @throws IllegalArgumentException if the resource path is null, empty,
     *     or points to a nonexistent resource.
     */
    public void saveResource(String resourcePath, boolean replace);

    /**
     * Discards any data in {@link #getConfig()} and reloads from disk.
     */
    public void reloadConfig();

    /**
     * Gets the associated ComponentLoader responsible for this component
     *
     * @return ComponentLoader that controls this plugin
     */
    public ComponentLoader getComponentLoader();

    /**
     * Returns the Server instance currently running this component
     *
     * @return Server running this component
     */
    public Server getServer();

    /**
     * Returns a value indicating whether or not this component is currently
     * enabled
     *
     * @return true if this component is enabled, otherwise false
     */
    public boolean isEnabled();

    /**
     * Called when this component is disabled
     */
    public void onDisable();

    /**
     * Called after a component is loaded but before it has been enabled.
     * <p>
     * When mulitple components are loaded, the onLoad() for all components is
     * called before any onEnable() is called.
     */
    public void onLoad();

    /**
     * Called when this component is enabled
     */
    public void onEnable();

    /**
     * Simple boolean if we can still nag to the logs about things
     *
     * @return boolean whether we can nag
     */
    public boolean isNaggable();

    /**
     * Set naggable state
     *
     * @param canNag is this component still naggable?
     */
    public void setNaggable(boolean canNag);

    /**
     * Gets the {@link EbeanServer} tied to this component. This will only be
     * available if enabled in the {@link
     * ComponentDescriptionFile#isDatabaseEnabled()}
     * <p>
     * <i>For more information on the use of <a href="http://www.avaje.org/">
     * Avaje Ebeans ORM</a>, see <a
     * href="http://www.avaje.org/ebean/documentation.html">Avaje Ebeans
     * Documentation</a></i>
     * <p>
     * <i>For an example using Ebeans ORM, see <a
     * href="https://github.com/Spanna/
     *
     * @return ebean server instance or null if not enabled
     */
    public EbeanServer getDatabase();

    /**
     * Gets a {@link ChunkGenerator} for use in a default world, as specified
     * in the server configuration
     *
     * @param worldName Name of the world that this will be applied to
     * @param id Unique ID, if any, that was specified to indicate which
     *     generator was requested
     * @return ChunkGenerator for use in the default world generation
     */
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id);

    /**
     * Returns the component logger associated with this server's logger. The
     * returned logger automatically tags all log messages with the component's
     * name.
     *
     * @return Logger associated with this component
     */
    public Logger getLogger();

    /**
     * Returns the name of the component.
     * <p>
     * This should return the bare name of the component and should be used for
     * comparison.
     *
     * @return name of the component
     */
    public String getName();
}
