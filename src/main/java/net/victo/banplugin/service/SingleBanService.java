package net.victo.banplugin.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.victo.banplugin.database.Queries;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.model.Banishment;

import java.util.*;

public enum SingleBanService implements IBanService {
    INSTANCE;

    private Multimap<UUID, Banishment> cachedBanishments = ArrayListMultimap.create();


    @Override
    public Multimap<UUID, Banishment> getCache() {
        return cachedBanishments;
    }

    @Override
    public List<Banishment> getBanishments(UUID player) {
        if(!cachedBanishments.containsKey(player)) {
            return Collections.emptyList();
        }
        List<Banishment> banishments = Queries.getBanishments(player);
        cachedBanishments.putAll(player, banishments);
        return banishments;
    }

    @Override
    public boolean hasActiveBan(UUID player) {
        return getBanishments(player).stream().anyMatch(ban -> !ban.ended());
    }

}
