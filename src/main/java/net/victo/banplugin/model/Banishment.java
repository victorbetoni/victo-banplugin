package net.victo.banplugin.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class Banishment {

    private String player;
    private String reason;
    private LocalDateTime issuance;
    private LocalDateTime expiration;

    public Banishment(String player, String reason, LocalDateTime issuance, LocalDateTime expiration) {
        this.player = player;
        this.reason = reason;
        this.issuance = issuance;
        this.expiration = expiration;
    }

    public String getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getIssuance() {
        return issuance;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public boolean expired() {
        return LocalDateTime.now().isAfter(expiration);
    }
}
