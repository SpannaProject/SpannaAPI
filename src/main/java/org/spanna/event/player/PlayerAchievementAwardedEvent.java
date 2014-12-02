package org.spanna.event.player;

import org.spanna.Achievement;
import org.spanna.entity.Player;
import org.spanna.event.Cancellable;
import org.spanna.event.HandlerList;

/**
 * Called when a player earns an achievement.
 */
public class PlayerAchievementAwardedEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Achievement achievement;
    private boolean isCancelled = false;

    public PlayerAchievementAwardedEvent(Player player, Achievement achievement) {
        super(player);
        this.achievement = achievement;
    }

    /**
     * Gets the Achievement being awarded.
     *
     * @return the achievement being awarded
     */
    public Achievement getAchievement() {
        return achievement;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
