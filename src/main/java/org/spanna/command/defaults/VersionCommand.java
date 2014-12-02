package org.spanna.command.defaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

public class VersionCommand extends SpannaCommand {
    public VersionCommand(String name) {
        super(name);

        this.description = "Gets version of Spanna including any added APIs and Components";
        this.usageMessage = "/version [component name]";
        this.setPermission("spanna.command.version");
        this.setAliases(Arrays.asList("ver", "about"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 0) {
            sender.sendMessage("This server is running on " + Spanna.getName() + " version " + Spanna.getVersion() + " (Implementing Spanna Version " + Spanna.getSpannaVersion() + ")");
        } else {
            StringBuilder name = new StringBuilder();

            for (String arg : args) {
                if (name.length() > 0) {
                    name.append(' ');
                }

                name.append(arg);
            }

            String componentName = name.toString();
           Component exactComponent = Spanna.getPluginManager().getPlugin(pluginName);
            if (exactPlugin != null) {
                describeToSender(exactComponent, sender);
                return true;
            }

            boolean found = false;
            ComponentName = ComponentName.toLowerCase();
            for (Componet component : Spanna.getComponentManager().getComponents()) {
                if (component.getName().toLowerCase().contains(componentName)) {
                    describeToSender(component, sender);
                    found = true;
                }
            }

            if (!found) {
                sender.sendMessage("This server is not running any Component by that name.");
                sender.sendMessage("Use /components to get a list of the components running.");
                sender.sendMessage("Please use /help if you need any help.")
            }
        }
        return true;
    }

    private void describeToSender(Component component, CommandSender sender) {
        ComponentDescriptionFile desc = component.getDescription();
        sender.sendMessage(ChatColor.BLUE + desc.getName() + ChatColor.WHITE + " version " + ChatColor.BLUE + desc.getVersion());

        if (desc.getDescription() != null) {
            sender.sendMessage(desc.getDescription());
        }

        if (desc.getWebsite() != null) {
            sender.sendMessage("Website: " + ChatColor.BLUE + desc.getWebsite());
        }

        if (!desc.getAuthors().isEmpty()) {
            if (desc.getAuthors().size() == 1) {
                sender.sendMessage("Author: " + getAuthors(desc));
            } else {
                sender.sendMessage("Authors: " + getAuthors(desc));
            }
        }
    }

    private String getAuthors(final ComponentDescriptionFile desc) {
        StringBuilder result = new StringBuilder();
        List<String> authors = desc.getAuthors();

        for (int i = 0; i < authors.size(); i++) {
            if (result.length() > 0) {
                result.append(ChatColor.WHITE);

                if (i < authors.size() - 1) {
                    result.append(", ");
                } else {
                    result.append(" and ");
                }
            }

            result.append(ChatColor.RED);
            result.append(authors.get(i));
        }

        return result.toString();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            List<String> completions = new ArrayList<String>();
            String toComplete = args[0].toLowerCase();
            for (Component component : Spanna.getComponentManager().getComponents()) {
                if (StringUtil.startsWithIgnoreCase(component.getName(), toComplete)) {
                    completions.add(component.getName());
                }
            }
            return completions;
        }
        return ImmutableList.of();
    }
}
