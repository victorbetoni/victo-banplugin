package net.victo.banplugin;

import net.threader.lib.sql.acessor.MySQLDBAcessor;
import net.victo.banplugin.command.BanCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class BanPlugin extends JavaPlugin {

    private static BanPlugin instance;

    private MySQLDBAcessor acessor;

    @Override
    public void onEnable() {
        instance = this;

        this.acessor = new MySQLDBAcessor("localhost:3306/bans");
        this.acessor.connect();;

        this.getCommand("ban").setExecutor(new BanCommand());

    }

    public static BanPlugin instance() {
        return instance;
    }

    public MySQLDBAcessor getDatabase() {
        return acessor;
    }
}
