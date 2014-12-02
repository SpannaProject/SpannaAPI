package org.spanna.component.messaging;

/**
 * Represents the different directions a component channel may go.
 */
public enum ComponentChannelDirection {

    /**
     * The component channel is being sent to the server from a client.
     */
    INCOMING,

    /**
     * The component channel is being sent to a client from the server.
     */
    OUTGOING
}
