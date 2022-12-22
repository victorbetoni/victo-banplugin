package net.victo.banplugin.domain;

import com.google.common.collect.Multimap;
import net.victo.banplugin.model.Banishment;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface IBanService {
    Multimap<UUID, Banishment> getCache();
    List<Banishment> getBanishments(UUID player);
    boolean hasActiveBan(UUID player);
}
