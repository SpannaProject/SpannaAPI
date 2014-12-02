package org.spanna.command.defaults;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.spanna.ChatColor;
import org.spanna.command.BlockCommandSender;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.spanna.Spanna;
import org.spanna.World;
import org.spanna.entity.HumanEntity;

public class GameRuleCommand extends VanillaCommand {
    private static final List<String> GAMERULE_STATES = ImmutableList.of("true", "false");

    public GameRuleCommand() {
        super("gamerule");
        this.description = "Sets the server's game rules";
        this.usageMessage = "/gamerule <rule name> <value> OR /gamerule <rule name>";
        this.setPermission("spanna.command.gamerule");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length > 0) {
            String rule = args[0];
            World world = getGameWorld(sender);

            if (world.isGameRule(rule)) {
                if (args.length > 1) {
                    String value = args[1];

                    world.setGameRuleValue(rule, value);
                    Command.broadcastCommandMessage(sender, "[SERVER] Game rule " + rule + " has been set to: " + value);
                } else {
                    String value = world.getGameRuleValue(rule);
                    sender.sendMessage(rule + " = " + value);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "[ERROR] No game rule called " + rule + " is available");
            }

            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            sender.sendMessage("Rules: " + this.createString(getGameWorld(sender).getGameRules(), 0, ", "));

            return true;
        }
    }

    private World getGameWorld(CommandSender sender) {
        if (sender instanceof HumanEntity) {
            World world = ((HumanEntity) sender).getWorld();
            if (world != null) {
                return world;
            }
        } else if (sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getWorld();
        }

        return Spanna.getWorlds().get(0);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList(getGameWorld(sender).getGameRules()), new ArrayList<String>());
        }

        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], GAMERULE_STATES, new ArrayList<String>(GAMERULE_STATES.size()));
        }

        return ImmutableList.of();
    }
}
