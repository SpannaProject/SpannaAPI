package org.spanna.help;

import org.spanna.ChatColor;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.apache.commons.lang.StringUtils;
import org.spanna.command.ConsoleCommandSender;
import org.spanna.command.ComponentCommand;
import org.spanna.command.defaults.VanillaCommand;
import org.spanna.help.HelpTopic;

/**
 * Lacking an alternative, the help system will create instances of
 * GenericCommandHelpTopic for each command in the server's CommandMap. You
 * can use this class as a base class for custom help topics, or as an example
 * for how to write your own.
 */
public class GenericCommandHelpTopic extends HelpTopic {

    protected Command command;

    public GenericCommandHelpTopic(Command command) {
        this.command = command;

        if (command.getLabel().startsWith("/")) {
            name = command.getLabel();
        } else {
            name = "/" + command.getLabel();
        }

        // The short text is the first line of the description
        int i = command.getDescription().indexOf("\n");
        if (i > 1) {
            shortText = command.getDescription().substring(0, i - 1);
        } else {
            shortText = command.getDescription();
        }

        // Build full text
        StringBuffer sb = new StringBuffer();

        sb.append(ChatColor.RED);
        sb.append("Description: ");
        sb.append(ChatColor.WHITE);
        sb.append(command.getDescription());

        sb.append("\n");

        sb.append(ChatColor.RED);
        sb.append("Usage: ");
        sb.append(ChatColor.WHITE);
        sb.append(command.getUsage().replace("<command>", name.substring(1)));

        if (command.getAliases().size() > 0) {
            sb.append("\n");
            sb.append(ChatColor.RED);
            sb.append("Aliases: ");
            sb.append(ChatColor.WHITE);
            sb.append(ChatColor.WHITE + StringUtils.join(command.getAliases(), ", "));
        }
        fullText = sb.toString();
    }

    public boolean canSee(CommandSender sender) {
        if (!command.isRegistered() && !(command instanceof VanillaCommand)) {
            // Unregistered commands should not show up in the help (ignore VanillaCommands)
            return false;
        }

        if (sender instanceof ConsoleCommandSender) {
            return true;
        }

        if (amendedPermission != null) {
            return sender.hasPermission(amendedPermission);
        } else {
            return command.testPermissionSilent(sender);
        }
    }
}
