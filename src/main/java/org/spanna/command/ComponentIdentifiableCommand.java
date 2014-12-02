package org.spanna.command;

import org.spanna.component.Component;

/**
 * This interface is used by the help system to group commands into
 * sub-indexes based on the {@link Component} they are a part of. Custom command
 * implementations will need to implement this interface to have a sub-index
 * automatically generated on the component's behalf.
 */
public interface ComponentIdentifiableCommand {

    /**
     * Gets the owner of this ComponentIdentifiableCommand.
     *
     * @return Component that owns this ComponentIdentifiableCommand.
     */
    public Component getComponent();
}
