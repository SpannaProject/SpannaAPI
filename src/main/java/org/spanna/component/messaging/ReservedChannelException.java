package org.spanna.component.messaging;

/**
 * Thrown if a component attempts to register for a reserved channel (such as
 * "REGISTER")
 */
@SuppressWarnings("serial")
public class ReservedChannelException extends RuntimeException {
    public ReservedChannelException() {
        this("[ERROR] Attempted to register for a reserved channel name.");
    }

    public ReservedChannelException(String name) {
        super("[ERROR] Attempted to register for a reserved channel name ('" + name + "')");
    }
}
