package org.spanna.conversations;

import org.spanna.component.Component;

import java.util.Map;

/**
 * A ConversationContext provides continuity between nodes in the prompt graph
 * by giving the developer access to the subject of the conversation and a
 * generic map for storing values that are shared between all {@link Prompt}
 * invocations.
 */
public class ConversationContext {
    private Conversable forWhom;
    private Map<Object, Object> sessionData;
    private Component component;

    /**
     * @param component The owning component.
     * @param forWhom The subject of the conversation.
     * @param initialSessionData Any initial values to put in the sessionData
     *     map.
     */
    public ConversationContext(Component component, Conversable forWhom, Map<Object, Object> initialSessionData) {
        this.component = component;
        this.forWhom = forWhom;
        this.sessionData = initialSessionData;
    }

    /**
     * Gets the component that owns this conversation.
     *
     * @return The owning component.
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Gets the subject of the conversation.
     *
     * @return The subject of the conversation.
     */
    public Conversable getForWhom() {
        return forWhom;
    }

    /**
     * Gets session data shared between all {@link Prompt} invocations. Use
     * this as a way to pass data through each Prompt as the conversation
     * develops.
     *
     * @param key The session data key.
     * @return The requested session data.
     */
    public Object getSessionData(Object key) {
        return sessionData.get(key);
    }

    /**
     * Sets session data shared between all {@link Prompt} invocations. Use
     * this as a way to pass data through each prompt as the conversation
     * develops.
     *
     * @param key The session data key.
     * @param value The session data value.
     */
    public void setSessionData(Object key, Object value) {
        sessionData.put(key, value);
    }
}
