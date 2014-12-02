package org.spanna.command.defaults;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.World;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.util.StringUtil;

import com.google.common.collect.ImmutableList;

public class TimeCommand extends VanillaCommand {
    private static final List<String> TABCOMPLETE_ADD_SET = ImmutableList.of("add", "set");
    private static final List<String> TABCOMPLETE_DAY_NIGHT = ImmutableList.of("day", "night");

    public TimeCommand() {
        super("time");
        this.description = "Changes the time on each world";
        this.usageMessage = "/time set <value>\n/time add <value>";
        this.setPermission("spanna.command.time.add;spanna.command.time.set");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "[ERROR] Incorrect usage. Correct usage:\n" + usageMessage);
            return false;
        }

        int value;

        if (args[0].equals("set")) {
            if (!sender.hasPermission("spanna.command.time.set")) {
                sender.sendMessage(ChatColor.RED + "[ERROR] We're sorry but you do not have permission to use or execute that command. Please contact the server administrators for help.");
                return true;
            }

            if (args[1].equals("day")) {
                value = 0;
            } else if (args[1].equals("night")) {
                value = 12500;
            } else {
                value = getInteger(sender, args[1], 0);
            }

            for (World world : Spanna.getWorlds()) {
                world.setTime(value);
            }

            Command.broadcastCommandMessage(sender, "Set time to " + value);
        } else if (args[0].equals("add")) {
            if (!sender.hasPermission("spanna.command.time.add")) {
                sender.sendMessage(ChatColor.RED + "[ERROR] W're sorry but you do not have permission to use or execute that command. Please contact the server administrators for help.");
                return true;
            }

            value = getInteger(sender, args[1], 0);

            for (World world : Spanna.getWorlds()) {
                world.setFullTime(world.getFullTime() + value);
            }

            Command.broadcastCommandMessage(sender, "Added " + value + " to time");
        } else {
            sender.sendMessage("[ERROR] Unknown method. Usage: " + usageMessage);
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], TABCOMPLETE_ADD_SET, new ArrayList<String>(TABCOMPLETE_ADD_SET.size()));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_DAY_NIGHT, new ArrayList<String>(TABCOMPLETE_DAY_NIGHT.size()));
        }
        return ImmutableList.of();
    }
}
