package org.spanna.conversations;

import org.spanna.ChatColor;
import org.spanna.command.CommandSender;
import org.spanna.component.Component;

/**
 * ComponentNameConversationPrefix is a {@link ConversationPrefix} implementation
 * that displays the component name in front of conversation output.
 */
public class ComponentNameConversationPrefix implements ConversationPrefix {
    
    protected String separator;
    protected ChatColor prefixColor;
    protected Component component;
    
    private String cachedPrefix;
    
    public ComponentNameConversationPrefix(Component component) {
        this(component, " > ", ChatColor.PURPLE);
    }
    
    public ComponentNameConversationPrefix(Component component, String separator, ChatColor prefixColor) {
        this.separator = separator;
        this.prefixColor = prefixColor;
        this.component = component;

        cachedPrefix = prefixColor + component.getDescription().getName() + separator + ChatColor.WHITE;
    }

    /**
     * Prepends each conversation message with the component name.
     *
     * @param context Context information about the conversation.
     * @return An empty string.
     */
    public String getPrefix(ConversationContext context) {
        return cachedPrefix;
    }
}
