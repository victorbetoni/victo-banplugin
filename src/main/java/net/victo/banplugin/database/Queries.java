package net.victo.banplugin.database;

import com.google.common.collect.Multimap;
import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.model.BanAction;
import net.victo.banplugin.model.Banishment;
import net.victo.banplugin.model.Unban;
import net.victo.banplugin.util.Utils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Queries {

    public static Consumer<Multimap<String, BanAction>> DOWNLOAD_BANS = (cache) -> {
        try {
            Statement st = BanPlugin.instance().getDatabase().getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM ban_log");
            while (rs.next()) {
                UUID uid = UUID.fromString(rs.getString("id"));
                String target = rs.getString("player");
                String issuer = rs.getString("issuer");
                LocalDateTime issuedOn = LocalDateTime.parse(rs.getString("issued_on"), Utils.DEFAULT_DATE_FORMATTER);

                switch (rs.getString("action")) {
                    case "ban":
                        cache.put(target, new Banishment(
                                uid,
                                target,
                                issuer, issuedOn,
                                rs.getString("reason"),
                                LocalDateTime.parse(rs.getString("expire_on"), Utils.DEFAULT_DATE_FORMATTER)));
                        break;
                    case "unban":
                        cache.put(target, new Unban(uid, target, issuer, issuedOn));
                        break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    };

    public static Function<String, List<BanAction>> FIND_BANS = (player) -> {
        List<BanAction> history = new ArrayList<>();
        try (PreparedStatement statement = BanPlugin.instance().getDatabase().getConnection().prepareStatement(
                "SELECT * FROM ban_log WHERE player = ? ORDER BY issued_on")) {

            statement.setString(1, player);

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                UUID uid = UUID.fromString(result.getString("id"));
                String target = result.getString("player");
                String issuer = result.getString("issuer");
                LocalDateTime issuedOn = LocalDateTime.parse(result.getString("issued_on"), Utils.DEFAULT_DATE_FORMATTER);

                switch (result.getString("action")) {
                    case "ban":
                        history.add(new Banishment(
                                uid,
                                target,
                                issuer, issuedOn,
                                result.getString("reason"),
                                LocalDateTime.parse(result.getString("expire_on"), Utils.DEFAULT_DATE_FORMATTER)));
                        break;
                    case "unban":
                        history.add(new Unban(uid, target, issuer, issuedOn));
                        break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return history;
    };

    public static Consumer<Banishment> STORE_BAN = (banishment) -> {
        try (PreparedStatement statement = BanPlugin.instance().getDatabase().getConnection().prepareStatement(
                "INSERT INTO ban_log VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, banishment.getPlayer());
            statement.setString(2, banishment.getIssuer());
            statement.setString(3, banishment.getReason());
            statement.setString(4, "ban");
            statement.setString(5, banishment.getIssuedOn().format(Utils.DEFAULT_DATE_FORMATTER));
            if(banishment.getExpiration() == null) {
                statement.setString(6, null);
            } else {
                statement.setString(6, banishment.getExpiration().format(Utils.DEFAULT_DATE_FORMATTER));
            }
            statement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    };

    public static Consumer<Unban> STORE_UNBAN = (unban) -> {
        try (PreparedStatement statement = BanPlugin.instance().getDatabase().getConnection().prepareStatement(
                "INSERT INTO ban_log (player, issuer, action, issued_on) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, unban.getPlayer());
            statement.setString(2, unban.getIssuer());
            statement.setString(3, "unban");
            statement.setString(4, unban.getIssuedOn().format(Utils.DEFAULT_DATE_FORMATTER));
            statement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    };

    public static BiConsumer<UUID, LocalDateTime> UPDATE_BAN_EXPIRATION = (id, date) -> {
        try (PreparedStatement statement = BanPlugin.instance().getDatabase().getConnection().prepareStatement(
                "UPDATE ban_log SET expire_on = ? WHERE id = ?")) {
            statement.setString(1, date.format(Utils.DEFAULT_DATE_FORMATTER));
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    };

}
