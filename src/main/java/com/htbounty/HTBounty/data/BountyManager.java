package com.htbounty.HTBounty.data;

import com.htbounty.HTBounty.HTBounty;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BountyManager {
    private final HTBounty plugin;
    private final Map<UUID, Double> amounts = new HashMap<>();
    private final Map<UUID, UUID> setters = new HashMap<>();
    private final Map<UUID, Long> times = new HashMap<>();
    private final File file;
    private final YamlConfiguration config;

    public BountyManager(HTBounty plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        loadData();
    }

    public void addBounty(UUID target, double amount, UUID setter) {
        amounts.put(target, amounts.getOrDefault(target, 0.0) + amount);
        setters.put(target, setter);
        times.put(target, System.currentTimeMillis());
    }

    public void cleanExpiredBounties() {
        long limit = plugin.getConfig().getLong("settings.expiry-time") * 1000L;
        if (limit <= 0) return;
        long now = System.currentTimeMillis();
        amounts.keySet().removeIf(id -> (now - times.getOrDefault(id, 0L)) > limit);
    }

    public String getRemainingTime(UUID id) {
        long limit = plugin.getConfig().getLong("settings.expiry-time") * 1000L;
        long diff = limit - (System.currentTimeMillis() - times.getOrDefault(id, 0L));
        if (diff <= 0) return "Süresi Doldu";
        long hours = diff / 3600000;
        long mins = (diff % 3600000) / 60000;
        return hours + "s " + mins + "dk";
    }

    public boolean hasBounty(UUID id) { return amounts.containsKey(id); }
    public double getAmount(UUID id) { return amounts.getOrDefault(id, 0.0); }
    public void remove(UUID id) { amounts.remove(id); setters.remove(id); times.remove(id); }
    public String getSetterName(UUID id) { return org.bukkit.Bukkit.getOfflinePlayer(setters.getOrDefault(id, id)).getName(); }
    public Map<UUID, Double> getAll() { return amounts; }

    private void loadData() {
        if (!config.contains("data")) return;
        for (String k : config.getConfigurationSection("data").getKeys(false)) {
            UUID id = UUID.fromString(k);
            amounts.put(id, config.getDouble("data." + k + ".a"));
            setters.put(id, UUID.fromString(config.getString("data." + k + ".s")));
            times.put(id, config.getLong("data." + k + ".t"));
        }
    }

    public void saveData() {
        config.set("data", null);
        for (UUID id : amounts.keySet()) {
            config.set("data." + id + ".a", amounts.get(id));
            config.set("data." + id + ".s", setters.get(id).toString());
            config.set("data." + id + ".t", times.get(id));
        }
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
        
    }
}
