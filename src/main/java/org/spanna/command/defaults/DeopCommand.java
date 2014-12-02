package org.spanna.command.defaults;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.OfflinePlayer;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.entity.Player;
import org.spanna.util.StringUtil;

import com.google.common.collect.ImmutableList;

public class DeopCommand extends VanillaCommand {
    public DeopCommand() {
        super("deop");
        this.description = "Takes the specified player's operator status";
        this.usageMessage = "/deop <player>";
        this.setPermission("spanna.command.op.take");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length != 1 || args[0].length() == 0) {
            sender.sendMessage(ChatColor.RED + "[ERROR] Usage: " + usageMessage);
            return false;
        }

        OfflinePlayer player = Spanna.getOfflinePlayer(args[0]);
        player.setOp(false);

        if (player instanceof Player) {
            ((Player) player).sendMessage(ChatColor.PURPLE + "Sorry, but you are no longer op!");
        }

        Command.broadcastCommandMessage(sender, "[SERVER] De-opped " + args[0]);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            List<String> completions = new ArrayList<String>();
            for (OfflinePlayer player : Spanna.getOperators()) {
                String playerName = player.getName();
                if (StringUtil.startsWithIgnoreCase(playerName, args[0])) {
                    completions.add(playerName);
                }
            }
            return completions;
        }
        return ImmutableList.of();
    }
}
