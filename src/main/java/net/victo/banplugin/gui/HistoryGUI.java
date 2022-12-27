package net.victo.banplugin.gui;

import net.threader.lib.gui.GUIItem;
import net.threader.lib.gui.InventoryGUI;
import net.threader.lib.util.ItemStackBuilder;
import net.victo.banplugin.BanPlugin;
import net.victo.banplugin.domain.IBanService;
import net.victo.banplugin.model.BanAction;
import net.victo.banplugin.model.Banishment;
import net.victo.banplugin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HistoryGUI {

    private AtomicInteger index = new AtomicInteger(0);
    private List<Page> pages = new ArrayList<>();
    private Player holder;
    private String target;

    public HistoryGUI(Player holder, String target) {
        this.holder = holder;
        this.target = target;
        this.buildPages();
    }

    public HistoryGUI(Player holder, String target, int currentIndex) {
        this.holder = holder;
        this.target = target;
        this.index = new AtomicInteger(currentIndex);
        this.buildPages();
    }

    public AtomicInteger getIndex() {
        return index;
    }

    public List<Page> getPages() {
        return pages;
    }

    /*
     * Build the GUI pages based on the target player ban/unban history
     * */
    public void buildPages() {
        AtomicInteger count = new AtomicInteger(0);
        List<BanAction> currentPageActions = new ArrayList<>();
        for (BanAction action : BanPlugin.instance().getServiceManager().getService(IBanService.class).get().getHistory(target)) {
            if (count.incrementAndGet() <= 45) {
                currentPageActions.add(action);
                continue;
            }
            count.set(1);
            pages.add(new Page(new ArrayList<>(currentPageActions), holder, this));
            currentPageActions = new ArrayList<>();
            currentPageActions.add(action);
        }
        pages.add(new Page(new ArrayList<>(currentPageActions), holder, this));
    }

    /*
     * Increment the current index.
     * If the current page is the last one, the index automatically goes to the first page.
     */
    public void nextPage() {
        if (index.get() + 1 > pages.size() - 1) {
            index.set(0);
            return;
        }
        index.set(index.get() + 1);
    }

    /*
     * Decrement the current index.
     * If the current page is the first one, the index automatically goes to the last page.
     */
    public void previousPage() {
        if (index.get() - 1 < 0) {
            index.set(pages.size() - 1);
        }
        index.set(index.get() - 1);
    }

    /*
     * Get the page corresponding to the current index and opens it.
     * If the player is in the last page, the first page will be opened
     * and the current index set to 0.
     * */
    public void openCurrentPage() {
        if (index.get() > pages.size() - 1) {
            index.set(pages.size() - 1);
            openCurrentPage();
            return;
        }
        pages.get(index.get()).buildInventory().openInventory(holder);
    }

    /*
     * Close the player current inventory and open a new HistoryGUI in the current index.
     * */
    public void reopen() {
        holder.closeInventory();
        new HistoryGUI(holder, target, index.get()).openCurrentPage();
    }

    public Player getHolder() {
        return holder;
    }

    public static class Page {
        private List<BanAction> actions;
        private Player player;
        private HistoryGUI parent;

        public Page(List<BanAction> actions, Player player, HistoryGUI parent) {
            this.actions = actions;
            this.player = player;
            this.parent = parent;
        }

        /*
         * Build and return the player's bans/unbans history GUI
         *
         * */
        public InventoryGUI buildInventory() {
            AtomicInteger index = new AtomicInteger(0);
            GUIItem[] items = new GUIItem[actions.size() + 9];
            actions.forEach(action -> {
                String title = action instanceof Banishment
                        ? ChatColor.RED + "Player banned"
                        : ChatColor.GREEN + "Player unbanned";
                ItemStackBuilder builder = ItemStackBuilder.factory().type(Material.PAINTING);
                builder.title(title);
                builder.lore(
                        "",
                        ChatColor.GRAY + "Player: " + ChatColor.YELLOW + action.getPlayer(),
                        ChatColor.GRAY + "Issued on: " + ChatColor.YELLOW + Utils.DEFAULT_DATE_FORMATTER.format(action.getIssuedOn()),
                        ChatColor.GRAY + "Issued by: " + ChatColor.YELLOW + action.getIssuer(),
                        ""
                );

                if (action instanceof Banishment) {
                    Banishment ban = (Banishment) action;
                    if (!ban.getReason().equals("")) {
                        builder.lore(ChatColor.GRAY + "Reason: " + ChatColor.YELLOW + ban.getReason());
                    }
                    String expiration = ban.getExpiration() != null
                            ? ChatColor.YELLOW + Utils.DEFAULT_DATE_FORMATTER.format(ban.getExpiration())
                            : ChatColor.YELLOW + "Never";
                    builder.lore(ChatColor.GRAY + "Expiration: " + expiration);
                    builder.lore("");
                    if(ban.getExpiration() != null) {
                        String expired = ban.expired() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No";
                        builder.lore(ChatColor.GRAY + "Expired: " + expired);
                    }
                }
                items[index.get()] = new GUIItem(
                        builder.build(),
                        index.getAndIncrement(),
                        (p, e) -> {
                        });
            });

            ItemStack backStack = ItemStackBuilder.factory().type(Material.ARROW)
                    .title(ChatColor.YELLOW + "⮘ Previous").build();
            items[index.getAndIncrement()] = new GUIItem(backStack, 45, (e, p) -> parent.previousPage());

            ItemStack nextStack = ItemStackBuilder.factory().type(Material.ARROW)
                    .title(ChatColor.YELLOW + "Next ⮚").build();
            items[index.getAndIncrement()] = new GUIItem(nextStack, 53, (e, p) -> parent.previousPage());

            ItemStack closeStack = ItemStackBuilder.factory().type(Material.REDSTONE_BLOCK)
                    .title(ChatColor.RED + "Cancel ❌").build();
            items[index.getAndIncrement()] = new GUIItem(closeStack, 49, (event, player) -> player.closeInventory());

            ItemStack foo = ItemStackBuilder.factory().type(Material.STAINED_GLASS_PANE).title("").build();
            items[index.getAndIncrement()] = new GUIItem(foo.clone(), 46, (e, p) -> {});
            items[index.getAndIncrement()] = new GUIItem(foo.clone(), 47, (e, p) -> {});
            items[index.getAndIncrement()] = new GUIItem(foo.clone(), 48, (e, p) -> {});
            items[index.getAndIncrement()] = new GUIItem(foo.clone(), 50, (e, p) -> {});
            items[index.getAndIncrement()] = new GUIItem(foo.clone(), 51, (e, p) -> {});
            items[index.getAndIncrement()] = new GUIItem(foo.clone(), 52, (e, p) -> {});

            return new InventoryGUI(
                    BanPlugin.instance(),
                    parent.getHolder(),
                    parent.getHolder().getName() + "'s History (" + parent.getIndex().get() + "/" + parent.getPages().size() + ")",
                    InventoryGUI.Rows.SIX,
                    items
            );
        }
    }
}
