package org.spanna.command.defaults;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.spanna.Spanna;
import org.spanna.command.CommandSender;
import org.spanna.entity.Player;
import org.spanna.event.entity.EntityDamageEvent;

import com.google.common.collect.ImmutableList;

public class KillCommand extends VanillaCommand {
    public KillCommand() {
        super("kill");
        this.description = "Commits suicide, only usable as a player";
        this.usageMessage = "/kill";
        this.setPermission("spanna.command.kill");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (sender instanceof Player) {
            Player player = (Player) sender;

            EntityDamageEvent ede = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.SUICIDE, 1000);
            Spanna.getComponentManager().callEvent(ede);
            if (ede.isCancelled()) return true;

            ede.getEntity().setLastDamageCause(ede);
            player.setHealth(0);
            sender.sendMessage("[SERVER] Ouch. Try harder next time!");
        } else {
            sender.sendMessage("[ERROR] You can only perform this command as a player!");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        return ImmutableList.of();
    }
}
