package net.victo.banplugin.listener;

import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.model.Banishment;
import net.victo.banplugin.util.Message;
import net.victo.banplugin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleJoin(PlayerJoinEvent event) {
        Optional<IBanService> optService = BanPlugin.instance().getServiceManager().getService(IBanService.class);

        optService.ifPresent(service -> {
            if (service.hasActiveBan(event.getPlayer())) {
                Banishment ban = service.getLatestIssuedActiveBan(event.getPlayer().getName()).get();

                event.getPlayer().kickPlayer(Utils.getBanMessage(ban));
            }
        });

    }
}
