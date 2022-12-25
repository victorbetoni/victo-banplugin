package net.victo.banplugin.command;

import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.domain.IBanService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.util.Optional;

public class UnbanCommand implements CommandExecutor {
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

        String target = args[0];

        if(!service.hasActiveBan(target)) {
            sender.sendMessage(ChatColor.RED + "This player doesnt have any active ban.");
            return false;
        }

        service.unban(target, sender.getName(), LocalDateTime.now());
        sender.sendMessage(ChatColor.GREEN + "Player unbanned.");
        return true;
    }
}
