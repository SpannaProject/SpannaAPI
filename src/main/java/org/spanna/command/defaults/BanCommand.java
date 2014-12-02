package org.spanna.command.defaults;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.spanna.BanList;
import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.entity.Player;

import com.google.common.collect.ImmutableList;

public class BanCommand extends VanillaCommand {
    public BanCommand() {
        super("ban");
        this.description = "Prevents the specified player from using this server";
        this.usageMessage = "/ban <player> [reason ...]";
        this.setPermission("spanna.command.ban.player");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length == 0)  {
            sender.sendMessage(ChatColor.RED + "[ERROR] Usage: " + usageMessage);
            return false;
        }

        String reason = args.length > 0 ? StringUtils.join(args, ' ', 1, args.length) : null;
        Spanna.getBanList(BanList.Type.NAME).addBan(args[0], reason, null, sender.getName());

        Player player = Spanna.getPlayer(args[0]);
        if (player != null) {
            player.kickPlayer("Banned by an administrator!");
        }

        Command.broadcastCommandMessage(sender, "[SERVER] Banned player " + args[0]);
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
