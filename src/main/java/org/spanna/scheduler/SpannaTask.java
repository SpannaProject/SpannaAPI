package org.spanna.scheduler;

import org.spanna.component.Component;

/**
 * Represents a task being executed by the scheduler
 */
public interface SpannaTask {

    /**
     * Returns the taskId for the task.
     *
     * @return Task id number
     */
    public int getTaskId();

    /**
     * Returns the Component that owns this task.
     *
     * @return The Component that owns the task
     */
    public Component getOwner();

    /**
     * Returns true if the Task is a sync task.
     *
     * @return true if the task is run by main thread
     */
    public boolean isSync();

    /**
     * Will attempt to cancel this task.
     */
    public void cancel();
}
