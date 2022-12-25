package net.victo.banplugin.model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BanAction {
    protected String player;
    protected String issuer;
    protected LocalDateTime issuedOn;

    protected UUID id;

    public BanAction(UUID uuid, String player, String issuer, LocalDateTime issuedOn) {
        this.player = player;
        this.id = uuid;
        this.issuer = issuer;
        this.issuedOn = issuedOn;
    }

    public UUID getId() {
        return id;
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
