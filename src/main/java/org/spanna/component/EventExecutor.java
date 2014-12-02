package org.spanna.component;

import org.spanna.event.Event;
import org.spanna.event.EventException;
import org.spanna.event.Listener;

/**
 * Interface which defines the class for event call backs to components
 */
public interface EventExecutor {
    public void execute(Listener listener, Event event) throws EventException;
}
