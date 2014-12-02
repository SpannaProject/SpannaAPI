package org.spanna.component;

/**
 * Thrown when a component attempts to interact with the server when it is not
 * enabled
 */
@SuppressWarnings("serial")
public class IllegalComponentAccessException extends RuntimeException {

    /**
     * Creates a new instance of <code>IllegalComponentAccessException</code>
     * without detail message.
     */
    public IllegalComponentAccessException() {}

    /**
     * Constructs an instance of <code>IllegalComponentAccessException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public IllegalComponentAccessException(String msg) {
        super(msg);
    }
}
