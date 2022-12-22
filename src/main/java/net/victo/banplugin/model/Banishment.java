package net.victo.banplugin.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class Banishment {

    private UUID player;
    private String reason;
    private LocalDateTime start;
    private LocalDateTime ending;

    public Banishment(UUID player, String reason, LocalDateTime start, LocalDateTime ending) {
        this.player = player;
        this.reason = reason;
        this.start = start;
        this.ending = ending;
    }

    public UUID getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnding() {
        return ending;
    }

    public boolean ended() {
        return LocalDateTime.now().isAfter(ending);
    }
}
