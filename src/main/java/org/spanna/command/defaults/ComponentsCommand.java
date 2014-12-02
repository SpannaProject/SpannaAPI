package org.spanna.command.defaults;

import java.util.Arrays;

import org.spanna.Spanna;
import org.spanna.ChatColor;
import org.spanna.command.CommandSender;
import org.spanna.component.Component;

public class ComponentsCommand extends SpannaCommand {
    public ComponentsCommand(String name) {
        super(name);
        this.description = "Gets a list of the components running on the server";
        this.usageMessage = "/components";
        this.setPermission("spanna.command.components");
        this.setAliases(Arrays.asList("pl"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        sender.sendMessage("Components " + getComponentList());
        return true;
    }

    private String getComponentList() {
        StringBuilder componentList = new StringBuilder();
        Component[] components = Spanna.getComponentManager().getComponents();

        for (Component component : components) {
            if (componentList.length() > 0) {
                componentList.append(ChatColor.BLUE);
                componentList.append(", ");
            }

            componentList.append(component.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
            componentList.append(component.getDescription().getName());
        }

        return "(" + components.length + "): " + componentList.toString();
    }
}
