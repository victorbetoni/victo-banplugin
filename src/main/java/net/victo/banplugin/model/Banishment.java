package net.victo.banplugin.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class Banishment extends BanAction {

    private String reason;
    private LocalDateTime expiration;

    public Banishment(String player, String issuer, LocalDateTime issuedOn, String reason, LocalDateTime expiration) {
        super(player, issuer, issuedOn);
        this.reason = reason;
        this.expiration = expiration;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public boolean expired() {
        return LocalDateTime.now().isAfter(expiration);
    }
}
