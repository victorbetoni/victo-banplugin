package net.victo.banplugin.domain;

import com.google.common.collect.Multimap;
import net.victo.banplugin.model.BanAction;
import net.victo.banplugin.model.Banishment;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.*;

public interface IBanService {
    Multimap<String, BanAction> getCache();
    List<BanAction> getHistory(String player);
    boolean hasActiveBan(Player player);
    boolean hasActiveBan(String player);
    Optional<Banishment> getLatestIssuedActiveBan(String player);
    void ban(String nickname, String issuer, String reason, LocalDateTime issued, LocalDateTime expire);
    void unban(String nickname, String issuer, LocalDateTime issued);

}
