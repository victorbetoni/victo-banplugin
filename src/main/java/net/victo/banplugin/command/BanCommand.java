package net.victo.banplugin.command;

import net.threader.lib.text.Text;
import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.service.SingleBanService;
import net.victo.banplugin.util.Message;
import net.victo.banplugin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class BanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Optional<IBanService> optService = BanPlugin.instance().getServiceManager().getService(IBanService.class);

        if(!optService.isPresent()) {
            sender.sendMessage(Message.Util.of("not_available", BanPlugin.instance()));
            return false;
        }

        IBanService service = optService.get();
        if(args.length < 1) {
            sender.sendMessage(Message.Util.of("specify_player", BanPlugin.instance()));
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if(player == null) {
            new Message.Builder().fromConfig("player_offline", BanPlugin.instance())
                    .addVariable("player", args[0]).build().send(sender);
            return false;
        }

        if(service.hasActiveBan(player)) {
            new Message.Builder().fromConfig("already_banned", BanPlugin.instance())
                    .addVariable("player", player.getName()).build().send(sender);
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        String issuer = sender instanceof Player ? sender.getName() : "Console";

        if(args.length == 1) {
            service.ban(player.getName(), issuer, "", now, null);
            new Message.Builder().fromConfig("player_banned", BanPlugin.instance())
                    .addVariable("player", player.getName()).build().send(sender);
            return false;
        }

        String input = args[1];
        String reason = args.length == 4 ? args[3] : "";

        LocalDateTime expire = now;
        try {
            Map<ChronoUnit, Long> units = Utils.parseDuration(input);
            expire = expire.plusMonths(units.get(ChronoUnit.MONTHS));
            expire = expire.plusWeeks(units.get(ChronoUnit.WEEKS));
            expire = expire.plusDays(units.get(ChronoUnit.DAYS));
            expire = expire.plusHours(units.get(ChronoUnit.HOURS));
            expire = expire.plusMinutes(units.get(ChronoUnit.MINUTES));
            expire = expire.plusSeconds(units.get(ChronoUnit.SECONDS));
        } catch (Exception ex) {
            sender.sendMessage(Message.Util.of("invalid_duration", BanPlugin.instance()));
            return false;
        }

        service.ban(player.getName(), reason, "", now, expire);

        return true;
    }

}
