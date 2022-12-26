package net.victo.banplugin.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.victo.banplugin.api.event.PlayerBannedEvent;
import net.victo.banplugin.database.Queries;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.model.BanAction;
import net.victo.banplugin.model.Banishment;
import net.victo.banplugin.model.Unban;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class SingleBanService implements IBanService {

    private Multimap<String, BanAction> bans = ArrayListMultimap.create();


    @Override
    public Multimap<String, BanAction> getBans() {
        return bans;
    }

    /*
    * First checks if the cache contains the player's history, if not, searches it in
    * the database and store it in the cache.
    *
    * since not all bans are in memory at first, this method needs to be thread safe,
    * so a callback is required in case the history needs to be fetched in the database
    *
    * @param player - The player to be queried
    * @param whenReady - What to do then the history is ready
     * */
    @Override
    public List<BanAction> getHistory(String player) {
        if (bans.containsKey(player)) {
            return (List<BanAction>) bans.get(player);
        }
        List<BanAction> banishments = Queries.FIND_BANS.apply(player);
        bans.putAll(player, banishments);
        return banishments;
    }

    /*
    * Checks if the player has any bans that have not yet expired.
    * */
    @Override
    public boolean hasActiveBan(String player) {
        return this.getHistory(player).stream()
                .flatMap(action -> action instanceof Banishment ? Stream.of((Banishment) action) : Stream.empty())
                .anyMatch(ban -> !ban.expired());
    }

    @Override
    public boolean hasActiveBan(Player player) {
        return hasActiveBan(player.getName());
    }

    /*
    * Return the Player latest issued ban and wrap it in a Optional object, in case the player
    * doest have any bans.
    * */
    @Override
    public Optional<Banishment> getLatestIssuedActiveBan(String player) {
        return getHistory(player).stream()
                .flatMap(action -> action instanceof Banishment ? Stream.of((Banishment) action) : Stream.empty()).
                max(Comparator.comparing(BanAction::getIssuedOn));
    }

    @Override
    public void ban(String player, String issuer, String reason, LocalDateTime issued, LocalDateTime expire) {
        Banishment banishment = new Banishment(UUID.randomUUID(), player, issuer, issued, reason, expire);

        Bukkit.getPluginManager().callEvent(new PlayerBannedEvent(banishment));

        Queries.STORE_BAN.accept(banishment);
        this.bans.put(player, banishment);
    }

    /*
    * Create a new Unban action and sets the latest ban as expired.
    *
    * @param player - The player to be unbanned
    * @param issuer - The player who issued the unban
    * @param issued - The time the unban was issued.
    * */
    @Override
    public void unban(String player, String issuer, LocalDateTime issued) {
        this.getLatestIssuedActiveBan(player).ifPresent(ban -> {
            if(!ban.expired()) {
                Unban unban = new Unban(UUID.randomUUID(), player, issuer, issued);
                Queries.UPDATE_BAN_EXPIRATION.accept(ban.getId(), issued);
                Queries.STORE_UNBAN.accept(unban);
                this.bans.put(player, unban);
            }
        });
    }

    /*
    * Download all the data in the database and store it in the multimap.
    * */
    @Override
    public void download() {
        Queries.DOWNLOAD_BANS.accept(this.bans);
    }

}
