package org.spanna.component;

/**
 * Represents the order in which a component should be initialized and enabled
 */
public enum ComponentLoadOrder {

    /**
     * Indicates that the component will be loaded at startup
     */
    STARTUP,
    /**
     * Indicates that the component will be loaded after the first/default world
     * was created
     */
    POSTWORLD
}
