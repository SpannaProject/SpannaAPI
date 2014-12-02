package org.spanna.component.messaging;

/**
 * Thrown if a Component Message is sent that is too large to be sent.
 */
@SuppressWarnings("serial")
public class MessageTooLargeException extends RuntimeException {
    public MessageTooLargeException() {
        this("[ERROR] Attempted to send a component message that was too large. The maximum length a component message may be is " + Messenger.MAX_MESSAGE_SIZE + " bytes.");
    }

    public MessageTooLargeException(byte[] message) {
        this(message.length);
    }

    public MessageTooLargeException(int length) {
        this("[ERROR] Attempted to send a component message that was too large. The maximum length a component message may be is " + Messenger.MAX_MESSAGE_SIZE + " bytes (tried to send one that is " + length + " bytes long).");
    }

    public MessageTooLargeException(String msg) {
        super(msg);
    }
}
