package org.spanna.command.defaults;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.entity.Player;

import com.google.common.collect.ImmutableList;

public class KickCommand extends VanillaCommand {
    public KickCommand() {
        super("kick");
        this.description = "Removes the specified player from the server";
        this.usageMessage = "/kick <player> [reason ...]";
        this.setPermission("spanna.command.kick");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 1 || args[0].length() == 0) {
            sender.sendMessage(ChatColor.RED + "[ERROR] Usage: " + usageMessage);
            return false;
        }

        Player player = Spanna.getPlayerExact(args[0]);

        if (player != null) {
            String reason = "Kicked by an operator on the server. If you believe this is an issue, please contact the server staff immediately!";

            if (args.length > 1) {
                reason = createString(args, 1);
            }

            player.kickPlayer(reason);
            Command.broadcastCommandMessage(sender, "[SERVER] Kicked player " + player.getName() + ". With reason:\n" + reason);
        } else {
            sender.sendMessage("[ERROR] " + args[0] + " not found.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length >= 1) {
            return super.tabComplete(sender, alias, args);
        }
        return ImmutableList.of();
    }
}
