package org.spanna.command.defaults;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.util.StringUtil;

import com.google.common.collect.ImmutableList;

public class PardonIpCommand extends VanillaCommand {
    public PardonIpCommand() {
        super("pardon-ip");
        this.description = "Allows the specified IP address to use this server";
        this.usageMessage = "/pardon-ip <address>";
        this.setPermission("spanna.command.unban.ip");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length != 1)  {
            sender.sendMessage(ChatColor.RED + "[ERROR] Usage: " + usageMessage);
            return false;
        }

        if (BanIpCommand.ipValidity.matcher(args[0]).matches()) {
            Spanna.unbanIP(args[0]);
            Command.broadcastCommandMessage(sender, "[SERVER] Pardoned ip " + args[0]);
        } else {
            sender.sendMessage("[ERROR] Invalid IP Address!");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Spanna.getIPBans(), new ArrayList<String>());
        }
        return ImmutableList.of();
    }
}
