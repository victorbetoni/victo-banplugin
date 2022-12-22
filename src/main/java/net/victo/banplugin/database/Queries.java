package net.victo.banplugin.database;

import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.model.Banishment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Queries {

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static List<Banishment> getBanishments(UUID player) {
        List<Banishment> banishments = new ArrayList<>();
        try (PreparedStatement statement = BanPlugin.instance().getDatabase().connection().prepareStatement(
                "SELECT * FROM banishments")) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                banishments.add(new Banishment(
                        UUID.fromString(result.getString("player_id")),
                        result.getString("reason"),
                        LocalDateTime.parse(result.getString("starts_in"), DEFAULT_DATE_FORMATTER),
                        LocalDateTime.parse(result.getString("ends_in"), DEFAULT_DATE_FORMATTER)
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return banishments;
    }

}
