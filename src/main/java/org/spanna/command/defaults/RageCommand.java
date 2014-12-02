package org.spanna.command.defaults;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.entity.Player;
import org.spanna.command.defaults.KickCommand;

import com.google.common.collect.ImmutableList;

public class RageCommand extends VanillaCommand {
    public RageCommand() {
        super("ragequit");
        this.description = "Used generally when a player ragequits from the game.";
        this.usageMessage = "/ragequit";
        this.setPermission("spanna.command.rage");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        
        if (sender instanceof Player) {
        Player player = (Player) sender;

            player.kickPlayer("Ragequited from the game!");
            Command.broadcastCommandMessage(sender, "[SERVER] Player " + player.getName() + "has ragequit from the game.";
            
        } else {
        	sender.sendMessage("[ERROR] You can only perform this command as a player!");
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
