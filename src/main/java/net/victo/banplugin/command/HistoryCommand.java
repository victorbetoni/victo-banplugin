package net.victo.banplugin.command;

import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.gui.HistoryGUI;
import net.victo.banplugin.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class HistoryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Optional<IBanService> optService = BanPlugin.instance().getServiceManager().getService(IBanService.class);

        if(!(sender instanceof Player)) {
            sender.sendMessage(Message.Util.of("player_cmd", BanPlugin.instance()));
            return false;
        }

        if(!optService.isPresent()) {
            sender.sendMessage(Message.Util.of("not_available", BanPlugin.instance()));
            return false;
        }

        IBanService service = optService.get();

        String target = null;

        if(args.length < 1) {
            target = sender.getName();
        }

        target = args.length > 1 && Bukkit.getPlayer(args[0]) != null
                ? Bukkit.getPlayer(args[0]).getName()
                : args[0];

        if(service.getHistory(target).isEmpty()) {
            new Message.Builder().fromConfig("no_bans", BanPlugin.instance())
                    .addVariable("player", target).build().send(sender);
            return false;
        }

        new HistoryGUI((Player) sender, target).reopen();
        return true;
    }
}
