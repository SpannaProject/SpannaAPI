package org.spanna.command.defaults;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.command.CommandSender;
import org.spanna.entity.Player;

import com.google.common.collect.ImmutableList;

public class ListCommand extends VanillaCommand {
    public ListCommand() {
        super("list");
        this.description = "Lists all online players";
        this.usageMessage = "/list";
        this.setPermission("spanna.command.list");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        StringBuilder online = new StringBuilder();

        final Collection<? extends Player> players = Spanna.getOnlinePlayers();

        for (Player player : players) {
            // If a player is hidden from the sender don't show them in the list
            if (sender instanceof Player && !((Player) sender).canSee(player))
                continue;

            if (online.length() > 0) {
                online.append(", ");
            }

            online.append(player.getDisplayName());
        }

        sender.sendMessage("[SERVER] There are " + players.size() + "/" + Spanna.getMaxPlayers() + " players online:\n" + online.toString());

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        return ImmutableList.of();
    }
}
