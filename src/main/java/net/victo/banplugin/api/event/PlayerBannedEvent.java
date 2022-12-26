package net.victo.banplugin.api.event;

import net.victo.banplugin.model.Banishment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.logging.Handler;

public class PlayerBannedEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    private Banishment banishment;

    public PlayerBannedEvent(Banishment banishment) {
        this.banishment = banishment;
    }

    public Banishment getBanishment() {
        return banishment;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
