package net.victo.banplugin.database;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySQLConnector {

    private String url;
    private Plugin plugin;
    private Connection connection;

    public MySQLConnector(String url, Plugin plugin) {
        this.url = url;
        this.plugin = plugin;
    }

    public void connect() {
        try {
            plugin.getLogger().log(Level.INFO, "Connecting to the database...");
            connection = DriverManager.getConnection(url);
            plugin.getLogger().log(Level.INFO, "Connection established successfully!");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            plugin.getLogger().log(Level.SEVERE, "Error while establishing database connection: ");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    public Connection connection() {
        try {
            if(connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static class Builder {
        private String url;
        private Plugin plugin;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder plugin(Plugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public MySQLConnector build() {
            return new MySQLConnector(url, plugin);
        }
    }
}
