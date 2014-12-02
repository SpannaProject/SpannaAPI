package org.spanna.command.defaults;

import java.util.Arrays;

import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;

public class ReloadCommand extends SpannaCommand {
    public ReloadCommand(String name) {
        super(name);
        this.description = "Reloads the server configuration and components";
        this.usageMessage = "/reload";
        this.setPermission("spanna.command.reload");
        this.setAliases(Arrays.asList("rl"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        Spanna.reload();
        Command.broadcastCommandMessage(sender, ChatColor.GREEN + "[SERVER] Reload completed successfully!");

        return true;
    }
}
