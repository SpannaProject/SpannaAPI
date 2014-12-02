package org.spanna.command.defaults;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.World;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;

import com.google.common.collect.ImmutableList;

public class SaveOffCommand extends VanillaCommand {
    public SaveOffCommand() {
        super("save-off");
        this.description = "Disables server autosaving";
        this.usageMessage = "/save-off";
        this.setPermission("spanna.command.save.disable");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        for (World world : Spanna.getWorlds()) {
            world.setAutoSave(false);
        }

        Command.broadcastCommandMessage(sender, "[AUTO-SAVE] Disabled level saving..");
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
