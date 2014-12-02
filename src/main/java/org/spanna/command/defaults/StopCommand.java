package org.spanna.command.defaults;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import org.spanna.Spanna;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.entity.Player;

import com.google.common.collect.ImmutableList;

public class StopCommand extends VanillaCommand {
    public StopCommand() {
        super("stop");
        this.description = "Stops the server with optional reason";
        this.usageMessage = "/stop [reason]";
        this.setPermission("spanna.command.stop");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        Command.broadcastCommandMessage(sender, "[SERVER] Stopping the server..");
        Spanna.shutdown();

        String reason = this.createString(args, 0);
        if (StringUtils.isNotEmpty(reason)) {
            for (Player player : Spanna.getOnlinePlayers()) {
                player.kickPlayer(reason);
            }
        }

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
