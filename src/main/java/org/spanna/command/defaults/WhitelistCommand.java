package org.spanna.command.defaults;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

public class WhitelistCommand extends VanillaCommand {
    private static final List<String> WHITELIST_SUBCOMMANDS = ImmutableList.of("add", "remove", "on", "off", "list", "reload");

    public WhitelistCommand() {
        super("whitelist");
        this.description = "Manages the list of players allowed to use this server";
        this.usageMessage = "/whitelist (add|remove) <player>\n/whitelist (on|off|list|reload)";
        this.setPermission("spanna.command.whitelist.reload;spanna.command.whitelist.enable;spanna.command.whitelist.disable;spanna.command.whitelist.list;spanna.command.whitelist.add;spanna.command.whitelist.remove");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (badPerm(sender, "reload")) return true;

                Spanna.reloadWhitelist();
                Command.broadcastCommandMessage(sender, "[SERVER] Reloaded white-list");
                return true;
            } else if (args[0].equalsIgnoreCase("on")) {
                if (badPerm(sender, "enable")) return true;

                Spanna.setWhitelist(true);
                Command.broadcastCommandMessage(sender, "[SERVER] Turned on white-listing");
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                if (badPerm(sender, "disable")) return true;

                Spanna.setWhitelist(false);
                Command.broadcastCommandMessage(sender, "[SERVER] Turned off white-listing");
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (badPerm(sender, "list")) return true;

                StringBuilder result = new StringBuilder();

                for (OfflinePlayer player : Spanna.getWhitelistedPlayers()) {
                    if (result.length() > 0) {
                        result.append(", ");
                    }

                    result.append(player.getName());
                }

                sender.sendMessage("[SERVER] White-listed players: " + result.toString());
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (badPerm(sender, "add")) return true;

                Spanna.getOfflinePlayer(args[1]).setWhitelisted(true);

                Command.broadcastCommandMessage(sender, "[SERVER] Added " + args[1] + " to white-list");
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (badPerm(sender, "remove")) return true;

                Spanna.getOfflinePlayer(args[1]).setWhitelisted(false);

                Command.broadcastCommandMessage(sender, "[SERVER] Removed " + args[1] + " from white-list");
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Correct command usage:\n" + usageMessage);
        return false;
    }

    private boolean badPerm(CommandSender sender, String perm) {
        if (!sender.hasPermission("spanna.command.whitelist." + perm)) {
            sender.sendMessage(ChatColor.RED + "[SERVER] We're sorry but you do not have permission to use or execute that command. Please contact the server administrators for help.");
            return true;
        }

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], WHITELIST_SUBCOMMANDS, new ArrayList<String>(WHITELIST_SUBCOMMANDS.size()));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                List<String> completions = new ArrayList<String>();
                for (OfflinePlayer player : Spanna.getOfflinePlayers()) {
                    String name = player.getName();
                    if (StringUtil.startsWithIgnoreCase(name, args[1]) && !player.isWhitelisted()) {
                        completions.add(name);
                    }
                }
                return completions;
            } else if (args[0].equalsIgnoreCase("remove")) {
                List<String> completions = new ArrayList<String>();
                for (OfflinePlayer player : Spanna.getWhitelistedPlayers()) {
                    String name = player.getName();
                    if (StringUtil.startsWithIgnoreCase(name, args[1])) {
                        completions.add(name);
                    }
                }
                return completions;
            }
        }
        return ImmutableList.of();
    }
}
