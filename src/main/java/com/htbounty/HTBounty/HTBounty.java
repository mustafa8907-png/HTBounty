package com.htbounty.HTBounty;

import com.htbounty.HTBounty.commands.BountyCommand;
import com.htbounty.HTBounty.data.BountyManager;
import com.htbounty.HTBounty.listeners.GameListener;
import net.milkbowl.vault.economy.Economy;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class HTBounty extends JavaPlugin {
    private static HTBounty instance;
    private static Economy econ = null;
    private BountyManager bountyManager;
    private FileConfiguration langConfig;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadLang();
        if (!setupEconomy()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.bountyManager = new BountyManager(this);
        getCommand("bounty").setExecutor(new BountyCommand(this));
        getServer().getPluginManager().registerEvents(new GameListener(this), this);

        // Her 10 dakikada bir süresi dolanları temizle
        Bukkit.getScheduler().runTaskTimer(this, () -> bountyManager.cleanExpiredBounties(), 20L * 60, 20L * 60 * 10);
    }

    @Override
    public void onDisable() { if(bountyManager != null) bountyManager.saveData(); }

    private void loadLang() {
        File f = new File(getDataFolder(), "lang/tr.yml");
        if (!f.exists()) saveResource("lang/tr.yml", false);
        langConfig = YamlConfiguration.loadConfiguration(f);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) econ = rsp.getProvider();
        return econ != null;
    }

    public static HTBounty getInstance() { return instance; }
    public static Economy getEconomy() { return econ; }
    public BountyManager getBountyManager() { return bountyManager; }
    public String getLang(String k) { return ChatColor.translateAlternateColorCodes('&', langConfig.getString("prefix", "") + langConfig.getString(k, k)); }
    public String getRawLang(String k) { return ChatColor.translateAlternateColorCodes('&', langConfig.getString(k, k)); }
    public void sendActionBar(Player p, String m) { p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', m)
      )); }
    }
