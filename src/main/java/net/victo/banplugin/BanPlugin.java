package net.victo.banplugin;

import net.threader.lib.service.ServiceManager;
import net.threader.lib.sql.acessor.MySQLDBAcessor;
import net.victo.banplugin.command.BanCommand;
import net.victo.banplugin.command.HistoryCommand;
import net.victo.banplugin.command.UnbanCommand;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.listener.PlayerJoinListener;
import net.victo.banplugin.model.Unban;
import net.victo.banplugin.service.SingleBanService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BanPlugin extends JavaPlugin {

    private static BanPlugin instance;
    private ServiceManager serviceManager;

    private MySQLDBAcessor acessor;

    @Override
    public void onEnable() {
        instance = this;

        this.acessor = new MySQLDBAcessor("localhost:3306/bans");
        this.acessor.connect();

        this.serviceManager = new ServiceManager();
        this.serviceManager.getRegistry().register(IBanService.class, new SingleBanService());

        // Could use a cache system which downloads only needed (and when needed) data , but it would
        // be too much over-engineering.
        this.serviceManager.getService(IBanService.class).get().download();

        this.getCommand("unban").setExecutor(new UnbanCommand());
        this.getCommand("ban").setExecutor(new BanCommand());
        this.getCommand("history").setExecutor(new HistoryCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public static BanPlugin instance() {
        return instance;
    }

    public MySQLDBAcessor getDatabase() {
        return acessor;
    }
}
