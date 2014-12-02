package org.spanna.scheduler;

import org.spanna.Spanna;
import org.spanna.component.Component;

/**
 * This class is provided as an easy way to handle scheduling tasks.
 */
public abstract class SpannaRunnable implements Runnable {
    private int taskId = -1;

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized void cancel() throws IllegalStateException {
        Spanna.getScheduler().cancelTask(getTaskId());
    }

    public synchronized SpannaTask runTask(Component component) throws IllegalArgumentException, IllegalStateException {
        checkState();
        return setupId(Spanna.getScheduler().runTask(component, (Runnable) this));
    }


     */
    public synchronized SpannaTask runTaskAsynchronously(Component component) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Spanna.getScheduler().runTaskAsynchronously(component, (Runnable) this));
    }

   
     */
    public synchronized SpannaTask runTaskLater(Component component, long delay) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Spanna.getScheduler().runTaskLater(component, (Runnable) this, delay));
    }

     */
    public synchronized SpannaTask runTaskLaterAsynchronously(Component component, long delay) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Spanna.getScheduler().runTaskLaterAsynchronously(component, (Runnable) this, delay));
    }

     
    public synchronized SpannaTask runTaskTimer(Component component, long delay, long period) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Spanna.getScheduler().runTaskTimer(component, (Runnable) this, delay, period));
    }


    public synchronized SpannaTask runTaskTimerAsynchronously(Component component, long delay, long period) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Spanna.getScheduler().runTaskTimerAsynchronously(component, (Runnable) this, delay, period));
    }

    public synchronized int getTaskId() throws IllegalStateException {
        final int id = taskId;
        if (id == -1) {
            throw new IllegalStateException("[ERROR] Not scheduled yet");
        }
        return id;
    }

    private void checkState() {
        if (taskId != -1) {
            throw new IllegalStateException("[ERROR] Already scheduled as " + taskId);
        }
    }

    private SpannaTask setupId(final SpannaTask task) {
        this.taskId = task.getTaskId();
        return task;
    }
}
