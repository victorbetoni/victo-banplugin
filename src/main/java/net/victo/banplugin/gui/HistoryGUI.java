package net.victo.banplugin.gui;

import net.threader.lib.gui.GUIItem;
import net.threader.lib.gui.InventoryGUI;
import net.victo.banplugin.model.BanAction;
import net.victo.banplugin.model.Banishment;
import net.victo.banplugin.service.SingleBanService;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryGUI {

    private int index;
    private Player holder;

    private List<BanAction> history;

    public HistoryGUI(Player holder) {
        this.holder = holder;
    }

    public InventoryGUI build() {
        List<BanAction> history = SingleBanService.INSTANCE.getHistory(holder.getName());
        return null;
    }
}
