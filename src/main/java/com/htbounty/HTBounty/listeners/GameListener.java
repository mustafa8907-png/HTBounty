package com.htbounty.HTBounty.listeners;

import com.htbounty.HTBounty.HTBounty;
import com.htbounty.HTBounty.gui.BountyGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GameListener implements Listener {
    private final HTBounty plugin;
    public GameListener(HTBounty plugin) { this.plugin = plugin; }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player vic = e.getEntity();
        Player kil = vic.getKiller();
        if (kil == null || kil.equals(vic)) return;

        if (plugin.getBountyManager().hasBounty(vic.getUniqueId())) {
            double amt = plugin.getBountyManager().getAmount(vic.getUniqueId());
            HTBounty.getEconomy().depositPlayer(kil, amt);
            plugin.getBountyManager().remove(vic.getUniqueId());
            Bukkit.broadcastMessage(plugin.getLang("claimed.chat").replace("%killer%", kil.getName()).replace("%amount%", String.valueOf(amt)));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("Bounty Listesi")) {
            e.setCancelled(true); // GUI'den eşya alınamaz, yer değiştirilemez.
            
            if (e.getCurrentItem() == null) return;
            Player p = (Player) e.getWhoClicked();
            
            if (e.getSlot() == 26) new BountyGUI(plugin).open(p, 1); // Basit sayfa geçiş örneği
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTitle().contains("Bounty Listesi")) e.setCancelled(true);
    }
}

