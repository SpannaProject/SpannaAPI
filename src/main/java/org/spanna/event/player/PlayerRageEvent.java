package org.spanna.event.player;

import org.spanna.entity.Player;
import org.spanna.event.Cancellable;
import org.spanna.event.HandlerList;

/**
 * Called when a player ragequits from the server
 */
public class PlayerRageEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String leaveMessage;
    private String rageReason;
    private Boolean cancel;

    public PlayerRageEvent(final Player playerRaged, final String rageReason, final String leaveMessage) {
        super(playerRaged);
        this.rageReason = rageReason;
        this.leaveMessage = leaveMessage;
        this.cancel = false;
    }

    /**
     * Gets the reason why the player is getting kicked
     *
     * @return string rage reason
     */
    public String getReason() {
        return rageReason;
    }

    /**
     * Gets the leave message send to all online players
     *
     * @return string rage reason
     */
    public String getLeaveMessage() {
        return leaveMessage;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Sets the reason why the player is getting kicked
     *
     * @param rageReason kick reason
     */
    public void setReason(String rageReason) {
        this.rageReason = rageReason;
    }

    /**
     * Sets the leave message send to all online players
     *
     * @param leaveMessage leave message
     */
    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
