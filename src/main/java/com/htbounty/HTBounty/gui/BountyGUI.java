package com.htbounty.HTBounty.gui;

import com.htbounty.HTBounty.HTBounty;
import com.htbounty.HTBounty.data.BountyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BountyGUI {

    private final HTBounty plugin;
    private final int PAGE_SIZE = 18;
    private final int GUI_SIZE = 27;

    public BountyGUI(HTBounty plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, int page) {
        String title = plugin.getRawLang("gui.title").replace("%page%", String.valueOf(page + 1));
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, title);

        BountyManager manager = plugin.getBountyManager();
        List<Map.Entry<UUID, Double>> entries = new ArrayList<>(manager.getAllBounties().entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, entries.size());

        for (int i = start; i < end; i++) {
            Map.Entry<UUID, Double> entry = entries.get(i);
            OfflinePlayer target = Bukkit.getOfflinePlayer(entry.getKey());

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            
            if (meta != null) {
                meta.setOwningPlayer(target);
                String nameFormat = plugin.getRawLang("gui.item-name")
                        .replace("%player%", target.getName() != null ? target.getName() : "Unknown");
                meta.setDisplayName(nameFormat);

                List<String> lore = new ArrayList<>();
                for (String line : plugin.getConfig().getStringList("gui.item-lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line
                            .replace("%amount%", String.valueOf(entry.getValue()))
                            .replace("%setter%", manager.getSetterName(entry.getKey()))
                    ));
                }
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            inv.setItem(i - start, head);
        }

        if (page > 0) inv.setItem(18, createButton(Material.ARROW, plugin.getRawLang("gui.previous-page")));
        if (end < entries.size()) inv.setItem(26, createButton(Material.ARROW, plugin.getRawLang("gui.next-page")));

        player.openInventory(inv);
    }

    private ItemStack createButton(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return 
          item;
    }
}
