package org.spanna.command.defaults;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.World;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;

import com.google.common.collect.ImmutableList;

public class SaveCommand extends VanillaCommand {
    public SaveCommand() {
        super("save-all");
        this.description = "Saves the server to disk/hardrive";
        this.usageMessage = "/save-all";
        this.setPermission("spanna.command.save.perform");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        Command.broadcastCommandMessage(sender, "[SERVER] Forcing SPANNA save..");

        Spanna.savePlayers();

        for (World world : Spanna.getWorlds()) {
            world.save();
        }

        Command.broadcastCommandMessage(sender, "[SERVER] SPANNA Save complete.");

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
