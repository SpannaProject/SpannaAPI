package org.spanna.component;

import java.util.Set;

import org.spanna.component.java.JavaComponent;

/**
 * Represents a concept that a component is aware of.
 * <p>
 * The internal representation may be singleton, or be a parameterized
 * instance, but must be immutable.
 */
public interface ComponentAwareness {
    /**
     * Each entry here represents a particular component's awareness. These can
     * be checked by using {@link ComponentDescriptionFile#getAwareness()}.{@link
     * Set#contains(Object) contains(flag)}.
     */
    public enum Flags implements ComponentAwareness {
        /**
         * This specifies that all (text) resources stored in a component's jar
         * use UTF-8 encoding.
         *
         * @see JavaComponent#getTextResource(String)
         */
        UTF8,
        ;
    }
}
