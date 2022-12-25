package net.victo.banplugin.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.victo.banplugin.database.Queries;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.model.BanAction;
import net.victo.banplugin.model.Banishment;
import net.victo.banplugin.model.Unban;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SingleBanService implements IBanService {

    private Multimap<String, BanAction> cache = ArrayListMultimap.create();


    @Override
    public Multimap<String, BanAction> getCache() {
        return cache;
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
        if (cache.containsKey(player)) {
            return (List<BanAction>) cache.get(player);
        }
        List<BanAction> banishments = Queries.FIND_BANS.apply(player);
        cache.putAll(player, banishments);
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
        Banishment banishment = new Banishment(player, issuer, issued, reason, expire);
        Queries.STORE_BAN.accept(banishment);
        this.cache.put(player, banishment);
    }

    @Override
    public void unban(String player, String issuer, LocalDateTime issued) {
        Unban unban = new Unban(player, issuer, issued);
        Queries.STORE_UNBAN.accept(unban);
        this.cache.put(player, unban);
    }

}
