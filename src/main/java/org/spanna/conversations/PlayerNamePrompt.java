package org.spanna.conversations;

import org.spanna.entity.Player;
import org.spanna.component.Component;

/**
 * PlayerNamePrompt is the base class for any prompt that requires the player
 * to enter another player's name.
 */
public abstract class PlayerNamePrompt extends ValidatingPrompt{
    private Component component;

    public PlayerNamePrompt(Component component) {
        super();
        this.component = component;
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        return component.getServer().getPlayer(input) != null;
        
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String input) {
        return acceptValidatedInput(context, component.getServer().getPlayer(input));
    }

    /**
     * Override this method to perform some action with the user's player name
     * response.
     *
     * @param context Context information about the conversation.
     * @param input The user's player name response.
     * @return The next {@link Prompt} in the prompt graph.
     */
    protected abstract Prompt acceptValidatedInput(ConversationContext context, Player input);
}
