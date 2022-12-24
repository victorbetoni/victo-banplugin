package net.victo.banplugin.model;

import java.time.LocalDateTime;

public abstract class BanAction {
    protected String player;
    protected String issuer;
    protected LocalDateTime issuedOn;

    public BanAction(String player, String issuer, LocalDateTime issuedOn) {
        this.player = player;
        this.issuer = issuer;
        this.issuedOn = issuedOn;
    }

    public String getPlayer() {
        return player;
    }

    public String getIssuer() {
        return issuer;
    }

    public LocalDateTime getIssuedOn() {
        return issuedOn;
    }
}
