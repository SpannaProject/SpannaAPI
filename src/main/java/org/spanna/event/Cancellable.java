package org.spanna.event;

public interface Cancellable {

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other components.
     *
     * @return true if this event is cancelled
     */
    public boolean isCancelled();

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other components.
     *
     * @param cancel true if you wish to cancel this event
     */
    public void setCancelled(boolean cancel);
}
