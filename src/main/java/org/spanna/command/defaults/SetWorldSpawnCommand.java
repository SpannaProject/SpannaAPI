package org.spanna.command.defaults;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.Location;
import org.spanna.World;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.entity.Player;

import java.util.List;

public class SetWorldSpawnCommand extends VanillaCommand {

    public SetWorldSpawnCommand() {
        super("setworldspawn");
        this.description = "Sets a worlds's spawn point. If no coordinates are specified, the player's coordinates will be used.";
        this.usageMessage = "/setworldspawn OR /setworldspawn <x> <y> <z>";
        this.setPermission("spanna.command.setworldspawn");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        Player player = null;
        World world;
        if (sender instanceof Player) {
            player = (Player) sender;
            world = player.getWorld();
        } else {
            world = Spanna.getWorlds().get(0);
        }

        final int x, y, z;

        if (args.length == 0) {
            if (player == null) {
                sender.sendMessage("[ERROR] You can only perform this command as a player");
                return true;
            }

            Location location = player.getLocation();

            x = location.getBlockX();
            y = location.getBlockY();
            z = location.getBlockZ();
        } else if (args.length == 3) {
            try {
                x = getInteger(sender, args[0], MIN_COORD, MAX_COORD, true);
                y = getInteger(sender, args[1], 0, world.getMaxHeight(), true);
                z = getInteger(sender, args[2], MIN_COORD, MAX_COORD, true);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ex.getMessage());
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "[ERROR] Usage: " + usageMessage);
            return false;
        }

        world.setSpawnLocation(x, y, z);

        Command.broadcastCommandMessage(sender, "[SERVER] Set world " + world.getName() + "'s spawnpoint to (" + x + ", " + y + ", " + z + ")");
        return true;

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        return ImmutableList.of();
    }
}
