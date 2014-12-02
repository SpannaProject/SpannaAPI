package org.spanna.command.defaults;

import java.util.List;

import org.spanna.command.Command;

public abstract class SpannaCommand extends Command {
    protected SpannaCommand(String name) {
        super(name);
    }

    protected SpannaCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }
}
