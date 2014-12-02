package org.spanna.command.defaults;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanns;
import org.spanna.World;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;

import com.google.common.collect.ImmutableList;

public class SaveOnCommand extends VanillaCommand {
    public SaveOnCommand() {
        super("save-on");
        this.description = "Enables server autosaving";
        this.usageMessage = "/save-on";
        this.setPermission("spanna.command.save.enable");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        for (World world : Spanna.getWorlds()) {
            world.setAutoSave(true);
        }

        Command.broadcastCommandMessage(sender, "[AUTO-SAVE] Enabled level saving...");
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
