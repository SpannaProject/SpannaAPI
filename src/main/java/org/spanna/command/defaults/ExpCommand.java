package org.spanna.command.defaults;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.entity.Player;

import com.google.common.collect.ImmutableList;

public class ExpCommand extends VanillaCommand {
    public ExpCommand() {
        super("xp");
        this.description = "Gives the specified player a certain amount of experience. Specify <amount>L to give levels instead, with a negative amount resulting in taking levels.";
        this.usageMessage = "/xp <amount> [player] OR /xp <amount>L [player]";
        this.setPermission("spanna.command.xp");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length > 0) {
            String inputAmount = args[0];
            Player player = null;

            boolean isLevel = inputAmount.endsWith("l") || inputAmount.endsWith("L");
            if (isLevel && inputAmount.length() > 1) {
                inputAmount = inputAmount.substring(0, inputAmount.length() - 1);
            }

            int amount = getInteger(sender, inputAmount, Integer.MIN_VALUE, Integer.MAX_VALUE);
            boolean isTaking = amount < 0;

            if (isTaking) {
                amount *= -1;
            }

            if (args.length > 1) {
                player = Spanna.getPlayer(args[1]);
            } else if (sender instanceof Player) {
                player = (Player) sender;
            }

            if (player != null) {
                if (isLevel) {
                    if (isTaking) {
                        player.giveExpLevels(-amount);
                        Command.broadcastCommandMessage(sender, "[SERVER] Taken " + amount + " level(s) from " + player.getName());
                    } else {
                        player.giveExpLevels(amount);
                        Command.broadcastCommandMessage(sender, "[SERVER] Given " + amount + " level(s) to " + player.getName());
                    }
                } else {
                    if (isTaking) {
                        sender.sendMessage(ChatColor.RED + "[ERROR] Taking experience can only be done by levels, cannot give players negative experience points");
                        return false;
                    } else {
                        player.giveExp(amount);
                        Command.broadcastCommandMessage(sender, "[SERVER] Given " + amount + " experience to " + player.getName());
                    }
                }
            } else {
                sender.sendMessage("[ERROR] Can't find player, was one provided?\n" + ChatColor.RED + "Usage: " + usageMessage);
                return false;
            }

            return true;
        }

        sender.sendMessage(ChatColor.RED + "[ERROR] Usage: " + usageMessage);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 2) {
            return super.tabComplete(sender, alias, args);
        }
        return ImmutableList.of();
    }
}
