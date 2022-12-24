package net.victo.banplugin.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.victo.banplugin.database.Queries;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.model.BanAction;
import net.victo.banplugin.model.Banishment;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public enum SingleBanService implements IBanService {
    INSTANCE;

    private Multimap<String, BanAction> cachedBanishments = ArrayListMultimap.create();


    @Override
    public Multimap<String, BanAction> getCache() {
        return cachedBanishments;
    }

    @Override
    public List<BanAction> getHistory(String player) {
        if (!cachedBanishments.containsKey(player)) {
            return Collections.emptyList();
        }
        List<BanAction> banishments = Queries.getHistory(player);
        cachedBanishments.putAll(player, banishments);
        return banishments;
    }

    @Override
    public boolean hasActiveBan(Player player) {
        return hasActiveBan(player.getName());
    }

    @Override
    public boolean hasActiveBan(String player) {
        return getHistory(player).stream()
                .flatMap(action -> action instanceof Banishment ? Stream.of((Banishment) action) : Stream.empty())
                .anyMatch(ban -> !ban.expired());
    }

    @Override
    public void ban(String nickname, String issuer, String reason, LocalDateTime issued, LocalDateTime expire) {
        Queries.ban(nickname, issuer, reason, issued, expire);
    }

    @Override
    public void unban(String nickname, String issuer, LocalDateTime issued) {
        Queries.unban(nickname, issuer, issued);
    }

}
