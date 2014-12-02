package org.spanna.command.defaults;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.GameMode;
import org.spanna.command.Command;
import org.spanna.command.CommandSender;
import org.spanna.util.StringUtil;

import com.google.common.collect.ImmutableList;

public class DefaultGameModeCommand extends VanillaCommand {
    private static final List<String> GAMEMODE_NAMES = ImmutableList.of("adventure", "creative", "survival");

    public DefaultGameModeCommand() {
        super("defaultgamemode");
        this.description = "Set the default gamemode";
        this.usageMessage = "/defaultgamemode <mode>";
        this.setPermission("spanna.command.defaultgamemode");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length == 0) {
            sender.sendMessage("[ERROR] Usage: " + usageMessage);
            return false;
        }

        String modeArg = args[0];
        int value = -1;

        try {
            value = Integer.parseInt(modeArg);
        } catch (NumberFormatException ex) {}

        GameMode mode = GameMode.getByValue(value);

        if (mode == null) {
            if (modeArg.equalsIgnoreCase("creative") || modeArg.equalsIgnoreCase("c")) {
                mode = GameMode.CREATIVE;
            } else if (modeArg.equalsIgnoreCase("adventure") || modeArg.equalsIgnoreCase("a")) {
                mode = GameMode.ADVENTURE;
            } else {
                mode = GameMode.SURVIVAL;
            }
        }

        Spanna.getServer().setDefaultGameMode(mode);
        Command.broadcastCommandMessage(sender, "[SERVER] Default game mode set to " + mode.toString().toLowerCase());

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], GAMEMODE_NAMES, new ArrayList<String>(GAMEMODE_NAMES.size()));
        }

        return ImmutableList.of();
    }
}
