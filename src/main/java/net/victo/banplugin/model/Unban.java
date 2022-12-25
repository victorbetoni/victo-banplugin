package net.victo.banplugin.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Unban extends BanAction {
    public Unban(UUID uuid, String player, String issuer, LocalDateTime issuedOn) {
        super(uuid, player, issuer, issuedOn);
    }
}
