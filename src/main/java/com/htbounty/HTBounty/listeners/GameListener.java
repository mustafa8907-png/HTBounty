package com.htbounty.HTBounty.listeners;

import com.htbounty.HTBounty.HTBounty;
import com.htbounty.HTBounty.gui.BountyGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GameListener implements Listener {

    private final HTBounty plugin;

    public GameListener(HTBounty plugin) {
        this.plugin = plugin;
    }

    // Ödül Kazanma
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();

        if (killer == null || killer.equals(victim)) return;

        if (plugin.getBountyManager().hasBounty(victim.getUniqueId())) {
            double amount = plugin.getBountyManager().getBounty(victim.getUniqueId());

            HTBounty.getEconomy().depositPlayer(killer, amount);
            plugin.getBountyManager().removeBounty(victim.getUniqueId());

            String msg = plugin.getLang("bounty-claimed")
                    .replace("%killer%", killer.getName())
                    .replace("%victim%", victim.getName())
                    .replace("%amount%", String.valueOf(amount));
            plugin.getServer().broadcastMessage(msg);
        }
    }

    // GUI Tıklamaları
    @EventHandler
    public void onGuiClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        
        String title = e.getView().getTitle();
        String configTitle = plugin.getRawLang("gui.title").split("%")[0]; // "Bounty Listesi" kısmını al

        if (!title.startsWith(configTitle)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        Player player = (Player) e.getWhoClicked();
        
        // Sayfa numarasını başlıktan çekme
        int currentPage = 0;
        try {
            String[] split = title.split(" ");
            currentPage = Integer.parseInt(split[split.length - 1]) - 1;
        } catch (Exception ignored) {}

        // Navigasyon
        if (e.getSlot() == 26 && e.getCurrentItem().getType() == Material.ARROW) {
            new BountyGUI(plugin).open(player, currentPage + 1);
        } else if (e.getSlot() == 18 && e.getCurrentItem().getType() == Material.ARROW) {
            new BountyGUI(plugin).open(player, currentPage - 1);
   
          }
    }
  }
