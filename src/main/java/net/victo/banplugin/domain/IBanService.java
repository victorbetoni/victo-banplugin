package net.victo.banplugin.domain;

import com.google.common.collect.Multimap;
import net.victo.banplugin.model.Banishment;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.*;

public interface IBanService {
    Multimap<String, Banishment> getCache();
    List<Banishment> getBanishments(String player);
    boolean hasActiveBan(Player player);
    boolean hasActiveBan(String player);
    void banPlayer(String nickname, String reason, LocalDateTime issued, LocalDateTime expire);

}
