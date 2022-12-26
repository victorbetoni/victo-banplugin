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
import java.util.Optional;

public class BanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Optional<IBanService> optService = BanPlugin.instance().getServiceManager().getService(IBanService.class);

        if(optService.isEmpty()) {
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

        LocalDateTime expire = null;
        try {
            expire = Utils.plusTime(input, now);
        } catch (NumberFormatException ex) {
            sender.sendMessage(Message.Util.of("invalid_duration", BanPlugin.instance()));
            return false;
        }

        service.ban(player.getName(), reason, "", now, expire);

        return true;
    }

}
