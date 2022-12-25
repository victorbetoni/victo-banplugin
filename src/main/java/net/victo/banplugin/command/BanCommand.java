package net.victo.banplugin.command;

import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.service.SingleBanService;
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
            sender.sendMessage(ChatColor.RED + "Ban service not available");
            return false;
        }

        IBanService service = optService.get();
        if(args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Specify a player.");
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if(player == null) {
            sender.sendMessage(ChatColor.RED + "The informed player is offline.");
            return false;
        }

        if(service.hasActiveBan(player)) {
            sender.sendMessage(ChatColor.RED + "Player is already banned.");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        String issuer = sender instanceof Player ? sender.getName() : "Console";

        if(args.length == 1) {
            service.ban(player.getName(), issuer, "", now, null);
            sender.sendMessage(ChatColor.GREEN + "Player banned.");
            return false;
        }

        String input = args[1];
        String reason = args.length == 4 ? args[3] : "";

        LocalDateTime expire = null;
        try {
            expire = Utils.plusTime(input, now);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Enter a valid duration.");
            return false;
        }

        service.ban(player.getName(), reason, "", now, expire);

        return true;
    }

}
