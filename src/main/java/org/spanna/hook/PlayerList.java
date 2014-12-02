package org.spanna.hook;

import org.spanna.entity.RTPlayer;
import org.spanna.reflect.ReflectiveClass;
import org.spanna.Spanna;
import org.spanna.event.player.PlayerJoinEvent;

public class PlayerList {

    private PlayerList() {
    }
    
    /**
     * @return The join message to be used.
     */
    public static String onJoin(Object nmsPlayer) {
        ReflectiveClass entityPlayer = ReflectiveClass.get("EntityPlayer");
        ReflectiveClass entityHuman = ReflectiveClass.get("EntityHuman");
        
        // Get their player object
        Player player = (Player) entityHuman.getRawField("spannaPlayer", nmsPlayer);
        
        // Create the event and call it
        PlayerJoinEvent event = new PlayerJoinEvent(player, "\u00A7d" + player.getName() + " has joined the game.");
        Spanna.getServer().getComponentManager().callEvent(event);
        
        return event.getJoinMessage();
    }
}
