package net.victo.banplugin.model;

import java.time.LocalDateTime;

public class Unban extends BanAction {
    public Unban(String player, String issuer, LocalDateTime issuedOn) {
        super(player, issuer, issuedOn);
    }
}
