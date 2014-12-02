package org.spanna.component;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The ComponentLogger class is a modified {@link Logger} that prepends all
 * logging calls with the name of the component doing the logging. The API for
 * ComponentLogger is exactly the same as {@link Logger}.
 *
 * @see Logger
 */
public class ComponentLogger extends Logger {
    private String pluginName;

    /**
     * Creates a new ComponentLogger that extracts the name from a component.
     *
     * @param context A reference to the component.
     */
    public ComponentLogger(Component context) {
        super(context.getClass().getCanonicalName(), null);
        String prefix = context.getDescription().getPrefix();
        componentName = prefix != null ? new StringBuilder().append("[").append(prefix).append("] ").toString() : "[" + context.getDescription().getName() + "] ";
        setParent(context.getServer().getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(componentName + logRecord.getMessage());
        super.log(logRecord);
    }

}
