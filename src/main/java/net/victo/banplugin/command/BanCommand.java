package net.victo.banplugin.command;

import net.victo.banplugin.service.SingleBanService;
import net.victo.banplugin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class BanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Specify a player.");
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if(player == null) {
            sender.sendMessage(ChatColor.RED + "The informed player is offline.");
            return false;
        }

        if(SingleBanService.INSTANCE.hasActiveBan(player)) {
            sender.sendMessage(ChatColor.RED + "Player is already banned.");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if(args.length == 1) {
            SingleBanService.INSTANCE.banPlayer(player.getName(), "", now, null);
            sender.sendMessage(ChatColor.GREEN + "Player banned.");
            return false;
        }

        String input = args[1];
        String reason = args.length == 4 ? args[3] : "";

        SingleBanService.INSTANCE.banPlayer(player.getName(), reason, now, Utils.plusTime(input, now));

        return true;
    }

}
