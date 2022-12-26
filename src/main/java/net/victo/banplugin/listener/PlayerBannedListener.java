package net.victo.banplugin.listener;

import net.victo.banplugin.api.event.PlayerBannedEvent;
import net.victo.banplugin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerBannedListener implements Listener {

    @EventHandler
    public void handleBan(PlayerBannedEvent event) {

        Player target = Bukkit.getPlayer(event.getBanishment().getPlayer());

        if(target != null) {
            target.kickPlayer(Utils.getBanMessage(event.getBanishment()));
        }
    }

}
