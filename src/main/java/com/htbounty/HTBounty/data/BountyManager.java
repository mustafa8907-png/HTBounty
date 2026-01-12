package com.htbounty.HTBounty.data;

import com.htbounty.HTBounty.HTBounty;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BountyManager {

    private final HTBounty plugin;
    private final Map<UUID, Double> bounties = new HashMap<>();
    private final Map<UUID, UUID> setters = new HashMap<>();
    private final File file;
    private final YamlConfiguration config;

    public BountyManager(HTBounty plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        loadData();
    }

    public void addBounty(UUID target, double amount, UUID setter) {
        double current = bounties.getOrDefault(target, 0.0);
        bounties.put(target, current + amount);
        setters.put(target, setter);
    }

    public boolean hasBounty(UUID target) {
        return bounties.containsKey(target);
    }

    public double getBounty(UUID target) {
        return bounties.getOrDefault(target, 0.0);
    }

    public void removeBounty(UUID target) {
        bounties.remove(target);
        setters.remove(target);
    }

    public String getSetterName(UUID target) {
        if (!setters.containsKey(target)) return "Bilinmiyor";
        return Bukkit.getOfflinePlayer(setters.get(target)).getName();
    }

    public Map<UUID, Double> getAllBounties() {
        return bounties;
    }

    private void loadData() {
        if (!config.contains("bounties")) return;
        for (String key : config.getConfigurationSection("bounties").getKeys(false)) {
            UUID target = UUID.fromString(key);
            double amount = config.getDouble("bounties." + key + ".amount");
            String setterStr = config.getString("bounties." + key + ".setter");
            bounties.put(target, amount);
            if (setterStr != null) setters.put(target, UUID.fromString(setterStr));
        }
    }

    public void saveData() {
        config.set("bounties", null);
        for (Map.Entry<UUID, Double> entry : bounties.entrySet()) {
            config.set("bounties." + entry.getKey() + ".amount", entry.getValue());
            if (setters.containsKey(entry.getKey())) {
                config.set("bounties." + entry.getKey() + ".setter", setters.get(entry.getKey()).toString());
            }
        }
        try { config.save(file); } catch (IOException e) { e.printStackTrace(

        ); }
    }
}
