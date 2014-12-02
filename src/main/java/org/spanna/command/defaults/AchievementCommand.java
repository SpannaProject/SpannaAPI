package org.spanna.command.defaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Achievement;
import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.Statistic;
import org.spanna.Material;
import org.spanna.Statistic.Type;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.entity.EntityType;
import org.spanna.entity.Player;
import org.spanna.event.player.PlayerAchievementAwardedEvent;
import org.spanna.event.player.PlayerStatisticIncrementEvent;

import com.google.common.collect.ImmutableList;

public class AchievementCommand extends VanillaCommand {
    public AchievementCommand() {
        super("achievement");
        this.description = "Gives the specified player an achievement or changes a statistic value. Use '*' to give all achievements.";
        this.usageMessage = "/achievement give <stat_name> [player]";
        this.setPermission("spanna.command.achievement");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "[ERROR] Usage: " + usageMessage);
            return false;
        }

        if (!args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(ChatColor.RED + "[ERROR] Usage: " + usageMessage);
            return false;
        }

        String statisticString = args[1];
        Player player = null;

        if (args.length > 2) {
            player = Spanna.getPlayer(args[1]);
        } else if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (player == null) {
            sender.sendMessage("[ERROR] You must specify which player you wish to perform this action on.");
            return true;
        }

        if (statisticString.equals("*")) {
            for (Achievement achievement : Achievement.values()) {
                if (player.hasAchievement(achievement)) {
                    continue;
                }
                PlayerAchievementAwardedEvent event = new PlayerAchievementAwardedEvent(player, achievement);
                Spanna.getServer().getComponentManager().callEvent(event);
                if (!event.isCancelled()) {
                    player.awardAchievement(achievement);
                }
            }
            Command.broadcastCommandMessage(sender, String.format("[SERVER] Successfully given all achievements to %s", player.getName()));
            return true;
        }

        Achievement achievement = Spanna.getUnsafe().getAchievementFromInternalName(statisticString);
        Statistic statistic = Spanna.getUnsafe().getStatisticFromInternalName(statisticString);

        if (achievement != null) {
            if (player.hasAchievement(achievement)) {
                sender.sendMessage(String.format("[ERROR] %s already has achievement %s", player.getName(), statisticString));
                return true;
            }

            PlayerAchievementAwardedEvent event = new PlayerAchievementAwardedEvent(player, achievement);
            Spanna.getServer().getComponentManager().callEvent(event);
            if (event.isCancelled()) {
                sender.sendMessage(String.format("[ERROR] Unable to award %s the achievement %s", player.getName(), statisticString));
                return true;
            }
            player.awardAchievement(achievement);
                
            Command.broadcastCommandMessage(sender, String.format("[SERVER] Successfully given %s the stat %s", player.getName(), statisticString));
            return true;
        }

        if (statistic == null) {
            sender.sendMessage(String.format("[ERROR] Unknown achievement or statistic '%s'", statisticString));
            return true;
        }

        if (statistic.getType() == Type.UNTYPED) {
            PlayerStatisticIncrementEvent event = new PlayerStatisticIncrementEvent(player, statistic, player.getStatistic(statistic), player.getStatistic(statistic) + 1);
            Spanna.getServer().getComponentManager().callEvent(event);
            if (event.isCancelled()) {
                sender.sendMessage(String.format("[ERROR] Unable to increment %s for %s", statisticString, player.getName()));
                return true;
            }
            player.incrementStatistic(statistic);
            Command.broadcastCommandMessage(sender, String.format("[SERVER] Successfully given %s the stat %s", player.getName(), statisticString));
            return true;
        }

        if (statistic.getType() == Type.ENTITY) {
            EntityType entityType = EntityType.fromName(statisticString.substring(statisticString.lastIndexOf(".") + 1));

            if (entityType == null) {
                sender.sendMessage(String.format("[ERROR] Unknown achievement or statistic '%s'", statisticString));
                return true;
            }

            PlayerStatisticIncrementEvent event = new PlayerStatisticIncrementEvent(player, statistic, player.getStatistic(statistic), player.getStatistic(statistic) + 1, entityType);
            Spanna.getServer().getComponentManager().callEvent(event);
            if (event.isCancelled()) {
                sender.sendMessage(String.format("[ERROR] Unable to increment %s for %s", statisticString, player.getName()));
                return true;
            }

            try {
                player.incrementStatistic(statistic, entityType);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(String.format("[ERROR] Unknown achievement or statistic '%s'", statisticString));
                return true;
            }
        } else {
            int id;
            try {
                id = getInteger(sender, statisticString.substring(statisticString.lastIndexOf(".") + 1), 0, Integer.MAX_VALUE, true);
            } catch (NumberFormatException e) {
                sender.sendMessage(e.getMessage());
                return true;
            }

            Material material = Material.getMaterial(id);

            if (material == null) {
                sender.sendMessage(String.format("[ERROR] Unknown achievement or statistic '%s'", statisticString));
                return true;
            }

            PlayerStatisticIncrementEvent event = new PlayerStatisticIncrementEvent(player, statistic, player.getStatistic(statistic), player.getStatistic(statistic) + 1, material);
            Spanna.getServer().getComponentManager().callEvent(event);
            if (event.isCancelled()) {
                sender.sendMessage(String.format("[ERROR] Unable to increment %s for %s", statisticString, player.getName()));
                return true;
            }

            try {
                player.incrementStatistic(statistic, material);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(String.format("[ERROR] Unknown achievement or statistic '%s'", statisticString));
                return true;
            }
        }

        Command.broadcastCommandMessage(sender, String.format("[SERVER] Successfully given %s the stat %s", player.getName(), statisticString));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return Arrays.asList("give");
        }

        if (args.length == 2) {
            return Spanna.getUnsafe().tabCompleteInternalStatisticOrAchievementName(args[1], new ArrayList<String>());
        }

        if (args.length == 3) {
            return super.tabComplete(sender, alias, args);
        }
        return ImmutableList.of();
    }
}
