package com.htbounty.HTBounty.commands;

import com.htbounty.HTBounty.HTBounty;
import com.htbounty.HTBounty.gui.BountyGUI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class BountyCommand implements CommandExecutor {
    private final HTBounty plugin;
    public BountyCommand(HTBounty plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;

        if (args.length == 0) {
            new BountyGUI(plugin).open(p, 0);
            return true;
        }

        if (args[0].equalsIgnoreCase("add") && args.length >= 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) { p.sendMessage(plugin.getLang("player-not-found")); return true; }
            if (target.equals(p)) { p.sendMessage(plugin.getLang("self-bounty")); return true; }

            double amt;
            try { amt = Double.parseDouble(args[2]); } catch (Exception e) { return true; }
            
            if (HTBounty.getEconomy().getBalance(p) < amt) { p.sendMessage(plugin.getLang("not-enough-money")); return true; }

            HTBounty.getEconomy().withdrawPlayer(p, amt);
            plugin.getBountyManager().addBounty(target.getUniqueId(), amt, p.getUniqueId());
            
            broadcast(p, target.getName(), amt);
            return true;
        }
        return true;
    }

    private void broadcast(Player s, String t, double a) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendTitle(plugin.getRawLang("broadcast.title"), t + ": " + a + "$", 10, 40, 10);
            online.playSound(online.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }

    }
            }
