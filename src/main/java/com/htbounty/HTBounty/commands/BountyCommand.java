package com.htbounty.HTBounty.commands;

import com.htbounty.HTBounty.HTBounty;
import com.htbounty.HTBounty.gui.BountyGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BountyCommand implements CommandExecutor {

    private final HTBounty plugin;

    public BountyCommand(HTBounty plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLang("only-players"));
            return true;
        }

        Player player = (Player) sender;

        // /bounty -> GUI Açar
        if (args.length == 0) {
            if (!player.hasPermission("htbounty.use")) {
                player.sendMessage(plugin.getLang("no-permission"));
                return true;
            }
            new BountyGUI(plugin).open(player, 0);
            return true;
        }

        // /bounty add <player> <amount>
        if (args[0].equalsIgnoreCase("add")) {
            if (!player.hasPermission("htbounty.add")) {
                player.sendMessage(plugin.getLang("no-permission"));
                return true;
            }

            if (args.length < 3) {
                player.sendMessage(plugin.getLang("usage"));
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            
            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(plugin.getLang("self-bounty"));
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getLang("invalid-amount"));
                return true;
            }

            double min = plugin.getConfig().getDouble("settings.min-bounty");
            if (amount < min) {
                player.sendMessage(plugin.getLang("min-amount").replace("%amount%", String.valueOf(min)));
                return true;
            }

            if (HTBounty.getEconomy().getBalance(player) < amount) {
                player.sendMessage(plugin.getLang("not-enough-money"));
                return true;
            }

            // İşlemler
            HTBounty.getEconomy().withdrawPlayer(player, amount);
            double tax = plugin.getConfig().getDouble("settings.tax-rate");
            double finalAmount = amount - (amount * tax);

            plugin.getBountyManager().addBounty(target.getUniqueId(), finalAmount, player.getUniqueId());

            player.sendMessage(plugin.getLang("bounty-set")
                    .replace("%player%", target.getName() != null ? target.getName() : args[1])
                    .replace("%amount%", String.valueOf(finalAmount)));

            broadcastBounty(player, target.getName() != null ? target.getName() : args[1], finalAmount);
            return true;
        }

        player.sendMessage(plugin.getLang("usage"));
        return true;
    }

    private void broadcastBounty(Player setter, String targetName, double amount) {
        String title = plugin.getRawLang("broadcast.title");
        String subtitle = plugin.getRawLang("broadcast.subtitle")
                .replace("%target%", targetName)
                .replace("%amount%", String.valueOf(amount));
        String actionBar = plugin.getRawLang("broadcast.actionbar")
                .replace("%setter%", setter.getName())
                .replace("%target%", targetName)
                .replace("%amount%", String.valueOf(amount));
        String chatMsg = plugin.getLang("broadcast.chat")
                .replace("%setter%", setter.getName())
                .replace("%target%", targetName)
                .replace("%amount%", String.valueOf(amount));

        Sound sound;
        try {
            sound = Sound.valueOf(plugin.getConfig().getString("effects.bounty-set-sound"));
        } catch (Exception e) {
            sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(chatMsg);
            p.sendTitle(title, subtitle, 10, 70, 20);
            plugin.sendActionBar(p, actionBar);
            p.playSound(p.getLocation(), sound, 1f, 1f);
        }
    }
                                                  }
