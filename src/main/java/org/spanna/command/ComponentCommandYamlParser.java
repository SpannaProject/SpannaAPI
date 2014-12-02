package org.spanna.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spanna.spanna;
import org.spanna.component.Component;

public class ComponentCommandYamlParser {

    public static List<Command> parse(Component component) {
        List<Command> componentCmds = new ArrayList<Command>();

        Map<String, Map<String, Object>> map = component.getDescription().getCommands();

        if (map == null) {
            return componentCmds;
        }

        for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
            if (entry.getKey().contains(":")) {
                Spanna.getServer().getLogger().severe("[ERROR] Could not load command " + entry.getKey() + " for component " + component.getName() + ": Illegal Characters");
                continue;
            }
            Command newCmd = new ComponentCommand(entry.getKey(), component);
            Object description = entry.getValue().get("description");
            Object usage = entry.getValue().get("usage");
            Object aliases = entry.getValue().get("aliases");
            Object permission = entry.getValue().get("permission");
            Object permissionMessage = entry.getValue().get("permission-message");

            if (description != null) {
                newCmd.setDescription(description.toString());
            }

            if (usage != null) {
                newCmd.setUsage(usage.toString());
            }

            if (aliases != null) {
                List<String> aliasList = new ArrayList<String>();

                if (aliases instanceof List) {
                    for (Object o : (List<?>) aliases) {
                        if (o.toString().contains(":")) {
                            Spanna.getServer().getLogger().severe("[ERROR] Could not load alias " + o.toString() + " for component " + component.getName() + ": Illegal Characters");
                            continue;
                        }
                        aliasList.add(o.toString());
                    }
                } else {
                    if (aliases.toString().contains(":")) {
                        Spanna.getServer().getLogger().severe("[ERROR] Could not load alias " + aliases.toString() + " for component " + component.getName() + ": Illegal Characters");
                    } else {
                        aliasList.add(aliases.toString());
                    }
                }

                newCmd.setAliases(aliasList);
            }

            if (permission != null) {
                newCmd.setPermission(permission.toString());
            }

            if (permissionMessage != null) {
                newCmd.setPermissionMessage(permissionMessage.toString());
            }

            componentCmds.add(newCmd);
        }
        return componentCmds;
    }
}
