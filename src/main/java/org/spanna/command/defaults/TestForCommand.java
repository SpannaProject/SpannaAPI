package org.spanna.command.defaults;

import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.command.CommandSender;

public class TestForCommand extends VanillaCommand {
    public TestForCommand() {
        super("testfor");
        this.description = "Tests whether a specifed player is online";
        this.usageMessage = "/testfor <player>";
        this.setPermission("spanna.command.testfor");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 1)  {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        sender.sendMessage(ChatColor.RED + "[ERROR] /testfor is only usable by commandblocks with analog output.");
        return true;
    }
}
