package com.htbounty.HTBounty.gui;

import com.htbounty.HTBounty.HTBounty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.*;

public class BountyGUI {
    private final HTBounty plugin;
    public BountyGUI(HTBounty plugin) { this.plugin = plugin; }

    public void open(Player p, int page) {
        Inventory inv = Bukkit.createInventory(null, 27, plugin.getRawLang("gui.title").replace("%page%", "1"));
        
        int slot = 0;
        for (UUID id : plugin.getBountyManager().getAll().keySet()) {
            if (slot > 17) break;
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta m = (SkullMeta) head.getItemMeta();
            m.setOwningPlayer(Bukkit.getOfflinePlayer(id));
            m.setDisplayName(plugin.getRawLang("gui.item-name").replace("%player%", Bukkit.getOfflinePlayer(id).getName()));
            
            List<String> lore = new ArrayList<>();
            for(String s : plugin.getConfig().getStringList("gui.item-lore")) {
                lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', s
                    .replace("%amount%", String.valueOf(plugin.getBountyManager().getAmount(id)))
                    .replace("%setter%", plugin.getBountyManager().getSetterName(id))
                    .replace("%expiry%", plugin.getBountyManager().getRemainingTime(id))));
            }
            m.setLore(lore);
            head.setItemMeta(m);
            inv.setItem(slot++, head);
        }
        p.openInventory(inv);
    }
    
}
