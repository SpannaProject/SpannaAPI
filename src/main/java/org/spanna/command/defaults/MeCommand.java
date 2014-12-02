package org.spanna.command.defaults;

import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.command.CommandSender;

public class MeCommand extends VanillaCommand {
    public MeCommand() {
        super("me");
        this.description = "Performs the specified action in chat";
        this.usageMessage = "/me <action>";
        this.setPermission("spanna.command.me");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 1)  {
            sender.sendMessage(ChatColor.RED + "[ERROR] Usage: " + usageMessage);
            return false;
        }

        StringBuilder message = new StringBuilder();
        message.append(sender.getName());

        for (String arg : args) {
            message.append(" ");
            message.append(arg);
        }

        Spanna.broadcastMessage("* " + message.toString());

        return true;
    }
}
