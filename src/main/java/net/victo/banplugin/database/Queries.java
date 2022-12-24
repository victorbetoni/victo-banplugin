package net.victo.banplugin.database;

import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.model.BanAction;
import net.victo.banplugin.model.Banishment;
import net.victo.banplugin.model.Unban;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Queries {

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static List<BanAction> getHistory(String player) {
        List<BanAction> history = new ArrayList<>();
        try (PreparedStatement statement = BanPlugin.instance().getDatabase().getConnection().prepareStatement(
                "SELECT * FROM ban_log WHERE player = ? ORDER BY issued_on")) {

            statement.setString(1, player);

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String target = result.getString("player");
                String issuer = result.getString("issuer");
                LocalDateTime issuedOn = LocalDateTime.parse(result.getString("issued_on"), DEFAULT_DATE_FORMATTER);

                switch (result.getString("action")) {
                    case "ban" -> history.add(new Banishment(
                            target,
                            issuer, issuedOn,
                            result.getString("reason"),
                            LocalDateTime.parse(result.getString("expire_on"), DEFAULT_DATE_FORMATTER)));
                    case "unban" -> history.add(new Unban(target, issuer, issuedOn));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return history;
    }

    public static void ban(String player, String issuer, String reason, LocalDateTime start, LocalDateTime end) {
        try (PreparedStatement statement = BanPlugin.instance().getDatabase().getConnection().prepareStatement(
                "INSERT INTO ban_log VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, player);
            statement.setString(2, issuer);
            statement.setString(3, reason);
            statement.setString(4, "ban");
            statement.setString(5, start.format(DEFAULT_DATE_FORMATTER));
            statement.setString(6, end.format(DEFAULT_DATE_FORMATTER));
            statement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void unban(String player, String issuer, LocalDateTime issuedOn) {
        try (PreparedStatement statement = BanPlugin.instance().getDatabase().getConnection().prepareStatement(
                "INSERT INTO ban_log (player, issuer, action, issued_on) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, player);
            statement.setString(2, issuer);
            statement.setString(3, "unban");
            statement.setString(4, issuedOn.format(DEFAULT_DATE_FORMATTER));
            statement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
