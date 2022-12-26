package net.victo.banplugin.command;

import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.util.Message;
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
            sender.sendMessage(Message.Util.of("not_available", BanPlugin.instance()));
            return false;
        }

        IBanService service = optService.get();

        if(args.length < 1) {
            sender.sendMessage(Message.Util.of("specify_player", BanPlugin.instance()));
            return false;
        }


        String target = args[0];

        if(!service.hasActiveBan(target)) {
            new Message.Builder().fromConfig("no_bans", BanPlugin.instance())
                    .addVariable("player", args[0]).build().send(sender);
            return false;
        }

        service.unban(target, sender.getName(), LocalDateTime.now());
        new Message.Builder().fromConfig("player_unbanned", BanPlugin.instance())
                .addVariable("player", args[0]).build().send(sender);
        return true;
    }
}
