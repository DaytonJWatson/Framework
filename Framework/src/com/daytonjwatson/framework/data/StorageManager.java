package com.daytonjwatson.framework.data;

import com.daytonjwatson.framework.FrameworkPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StorageManager {
    private final FrameworkPlugin plugin;
    private final File homesFile;
    private final FileConfiguration homesConfig;
    private final File warpsFile;
    private final FileConfiguration warpsConfig;
    private final File warningsFile;
    private final FileConfiguration warningsConfig;

    public StorageManager(FrameworkPlugin plugin) {
        this.plugin = plugin;
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        warningsFile = new File(plugin.getDataFolder(), "warnings.yml");
        if (!homesFile.exists()) plugin.saveResource("homes.yml", false);
        if (!warpsFile.exists()) plugin.saveResource("warps.yml", false);
        if (!warningsFile.exists()) plugin.saveResource("warnings.yml", false);
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
        warningsConfig = YamlConfiguration.loadConfiguration(warningsFile);
    }

    public void saveAll() {
        try {
            homesConfig.save(homesFile);
            warpsConfig.save(warpsFile);
            warningsConfig.save(warningsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save data: " + e.getMessage());
        }
    }

    public void setHome(Player player, String name, Location location) {
        homesConfig.set(player.getUniqueId() + "." + name.toLowerCase(), location);
        saveAll();
    }

    public Location getHome(Player player, String name) {
        return homesConfig.getLocation(player.getUniqueId() + "." + name.toLowerCase());
    }

    public Set<String> getHomes(Player player) {
        return homesConfig.getConfigurationSection(player.getUniqueId()) != null ?
                homesConfig.getConfigurationSection(player.getUniqueId()).getKeys(false) : java.util.Collections.emptySet();
    }

    public void deleteHome(Player player, String name) {
        homesConfig.set(player.getUniqueId() + "." + name.toLowerCase(), null);
        saveAll();
    }

    public void setWarp(String name, Location location) {
        warpsConfig.set(name.toLowerCase(), location);
        saveAll();
    }

    public Location getWarp(String name) {
        return warpsConfig.getLocation(name.toLowerCase());
    }

    public Set<String> getWarps() {
        return warpsConfig.getKeys(false);
    }

    public void deleteWarp(String name) {
        warpsConfig.set(name.toLowerCase(), null);
        saveAll();
    }

    public void addWarning(String playerName, String reason, String issuedBy) {
        int count = warningsConfig.getInt(playerName + ".count", 0) + 1;
        warningsConfig.set(playerName + ".count", count);
        warningsConfig.set(playerName + ".warnings." + count + ".reason", reason);
        warningsConfig.set(playerName + ".warnings." + count + ".by", issuedBy);
        saveAll();
    }

    public Map<Integer, Map<String, String>> getWarnings(String playerName) {
        Map<Integer, Map<String, String>> warnings = new HashMap<>();
        if (warningsConfig.getConfigurationSection(playerName + ".warnings") != null) {
            for (String key : warningsConfig.getConfigurationSection(playerName + ".warnings").getKeys(false)) {
                int index = Integer.parseInt(key);
                Map<String, String> warn = new HashMap<>();
                warn.put("reason", warningsConfig.getString(playerName + ".warnings." + key + ".reason"));
                warn.put("by", warningsConfig.getString(playerName + ".warnings." + key + ".by"));
                warnings.put(index, warn);
            }
        }
        return warnings;
    }

    public void clearWarnings(String playerName) {
        warningsConfig.set(playerName, null);
        saveAll();
    }
}
