package net.victo.banplugin;

import net.threader.lib.service.ServiceManager;
import net.threader.lib.sql.ScriptExecutor;
import net.threader.lib.sql.acessor.MySQLDBAcessor;
import net.victo.banplugin.command.BanCommand;
import net.victo.banplugin.command.HistoryCommand;
import net.victo.banplugin.command.UnbanCommand;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.listener.PlayerBannedListener;
import net.victo.banplugin.listener.PlayerJoinListener;
import net.victo.banplugin.service.SingleBanService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BanPlugin extends JavaPlugin {

    private static BanPlugin instance;
    private ServiceManager serviceManager;

    private MySQLDBAcessor acessor;

    public static BanPlugin instance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        String dbUrl = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&useSSL=false",
                this.getConfig().get("database.host"),
                this.getConfig().get("database.port"),
                this.getConfig().get("database.db"),
                this.getConfig().get("database.username"),
                this.getConfig().get("database.password"));

        this.acessor = new MySQLDBAcessor(this, dbUrl);
        this.acessor.connect();

        ScriptExecutor executor = new ScriptExecutor(
                new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/sql/setup.sql"))),
                this.acessor.getConnection());
        executor.execute();


        this.serviceManager = new ServiceManager();
        this.serviceManager.getRegistry().register(IBanService.class, new SingleBanService());

        // Could use a cache system which downloads only needed (and when needed) data , but it would
        // be too much over-engineering.
        this.serviceManager.getService(IBanService.class).get().download();

        this.getCommand("unban").setExecutor(new UnbanCommand());
        this.getCommand("ban").setExecutor(new BanCommand());
        this.getCommand("history").setExecutor(new HistoryCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerBannedListener(), this);
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public MySQLDBAcessor getDatabase() {
        return acessor;
    }
}
