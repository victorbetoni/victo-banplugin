package net.victo.banplugin.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.victo.banplugin.database.Queries;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.model.Banishment;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.*;

public enum SingleBanService implements IBanService {
    INSTANCE;

    private Multimap<String, Banishment> cachedBanishments = ArrayListMultimap.create();


    @Override
    public Multimap<String, Banishment> getCache() {
        return cachedBanishments;
    }

    @Override
    public List<Banishment> getBanishments(String player) {
        if(!cachedBanishments.containsKey(player)) {
            return Collections.emptyList();
        }
        List<Banishment> banishments = Queries.getBanishments(player);
        cachedBanishments.putAll(player, banishments);
        return banishments;
    }

    @Override
    public boolean hasActiveBan(Player player) {
        return hasActiveBan(player.getName());
    }

    @Override
    public boolean hasActiveBan(String player) {
        return getBanishments(player).stream().anyMatch(ban -> !ban.expired());
    }

    @Override
    public void banPlayer(String nickname, String reason, LocalDateTime issued, LocalDateTime expire) {
        Queries.banPlayer(nickname, reason, issued, expire);
    }

}
