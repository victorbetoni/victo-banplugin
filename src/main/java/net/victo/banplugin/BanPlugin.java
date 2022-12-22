package net.victo.banplugin;

import net.victo.banplugin.database.MySQLConnector;
import org.bukkit.plugin.java.JavaPlugin;

public class BanPlugin extends JavaPlugin {

    private static BanPlugin instance;

    private MySQLConnector connector;

    @Override
    public void onEnable() {
        instance = this;

        this.connector = new MySQLConnector.Builder().url("localhost:3306/bans").plugin(this).build();
        this.connector.connect();

    }

    public static BanPlugin instance() {
        return instance;
    }

    public MySQLConnector getDatabase() {
        return connector;
    }
}
