package com.daytonjwatson.framework.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.autocrop.AutoCropSettings;
import com.daytonjwatson.framework.settings.PlayerSettings;

public class StorageManager {
    private final FrameworkPlugin plugin;
    private final File homesFile;
    private final FileConfiguration homesConfig;
    private final File warpsFile;
    private final FileConfiguration warpsConfig;
    private final File bansFile;
    private final FileConfiguration bansConfig;
    private final File warnsFile;
    private final FileConfiguration warnsConfig;
    private final File mutesFile;
    private final FileConfiguration mutesConfig;
    private final File autoCropFile;
    private final FileConfiguration autoCropConfig;
    private final File playerSettingsFile;
    private final FileConfiguration playerSettingsConfig;

    public StorageManager(FrameworkPlugin plugin) {
        this.plugin = plugin;
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        bansFile = new File(plugin.getDataFolder(), "bans.yml");
        warnsFile = new File(plugin.getDataFolder(), "warns.yml");
        mutesFile = new File(plugin.getDataFolder(), "mutes.yml");
        autoCropFile = new File(plugin.getDataFolder(), "autocrop.yml");
        playerSettingsFile = new File(plugin.getDataFolder(), "playersettings.yml");
        if (!homesFile.exists()) plugin.saveResource("homes.yml", false);
        if (!warpsFile.exists()) plugin.saveResource("warps.yml", false);
        if (!bansFile.exists()) plugin.saveResource("bans.yml", false);
        if (!warnsFile.exists()) plugin.saveResource("warns.yml", false);
        if (!mutesFile.exists()) plugin.saveResource("mutes.yml", false);
        if (!autoCropFile.exists()) {
            try {
                autoCropFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create autocrop.yml: " + e.getMessage());
            }
        }
        if (!playerSettingsFile.exists()) {
            try {
                playerSettingsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create playersettings.yml: " + e.getMessage());
            }
        }
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
        bansConfig = YamlConfiguration.loadConfiguration(bansFile);
        warnsConfig = YamlConfiguration.loadConfiguration(warnsFile);
        mutesConfig = YamlConfiguration.loadConfiguration(mutesFile);
        autoCropConfig = YamlConfiguration.loadConfiguration(autoCropFile);
        playerSettingsConfig = YamlConfiguration.loadConfiguration(playerSettingsFile);
    }

    public void saveAll() {
        try {
            homesConfig.save(homesFile);
            warpsConfig.save(warpsFile);
            bansConfig.save(bansFile);
            warnsConfig.save(warnsFile);
            mutesConfig.save(mutesFile);
            autoCropConfig.save(autoCropFile);
            playerSettingsConfig.save(playerSettingsFile);
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
        String playerId = player.getUniqueId().toString();
        return homesConfig.getConfigurationSection(playerId) != null ?
                homesConfig.getConfigurationSection(playerId).getKeys(false) : java.util.Collections.emptySet();
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
        String key = playerName.toLowerCase();
        int count = warnsConfig.getInt(key + ".count", 0) + 1;
        warnsConfig.set(key + ".count", count);
        warnsConfig.set(key + ".warnings." + count + ".reason", reason);
        warnsConfig.set(key + ".warnings." + count + ".by", issuedBy);
        saveAll();
    }

    public Map<Integer, Map<String, String>> getWarnings(String playerName) {
        Map<Integer, Map<String, String>> warnings = new HashMap<>();
        String key = playerName.toLowerCase();
        if (warnsConfig.getConfigurationSection(key + ".warnings") != null) {
            for (String warnKey : warnsConfig.getConfigurationSection(key + ".warnings").getKeys(false)) {
                int index = Integer.parseInt(warnKey);
                Map<String, String> warn = new HashMap<>();
                warn.put("reason", warnsConfig.getString(key + ".warnings." + warnKey + ".reason"));
                warn.put("by", warnsConfig.getString(key + ".warnings." + warnKey + ".by"));
                warnings.put(index, warn);
            }
        }
        return warnings;
    }

    public void clearWarnings(String playerName) {
        warnsConfig.set(playerName.toLowerCase(), null);
        saveAll();
    }

    public void addBan(String playerName, String reason, String issuedBy, long durationMillis) {
        String key = playerName.toLowerCase();
        long expires = durationMillis > 0 ? System.currentTimeMillis() + durationMillis : -1;
        bansConfig.set(key + ".reason", reason);
        bansConfig.set(key + ".by", issuedBy);
        bansConfig.set(key + ".expires", expires);
        saveAll();
    }

    public boolean isBanned(String playerName) {
        String key = playerName.toLowerCase();
        if (!bansConfig.contains(key)) return false;
        long expires = bansConfig.getLong(key + ".expires", -1);
        if (expires > 0 && System.currentTimeMillis() > expires) {
            removeBan(playerName);
            return false;
        }
        return true;
    }

    public void removeBan(String playerName) {
        bansConfig.set(playerName.toLowerCase(), null);
        saveAll();
    }

    public long getBanExpiry(String playerName) {
        return bansConfig.getLong(playerName.toLowerCase() + ".expires", -1);
    }

    public Set<String> getBannedPlayers() {
        Set<String> names = new java.util.HashSet<>(bansConfig.getKeys(false));
        names.removeIf(name -> !isBanned(name));
        return names;
    }

    public String getBanReason(String playerName) {
        return bansConfig.getString(playerName.toLowerCase() + ".reason", "");
    }

    public void addMute(String playerName, String reason, String issuedBy, long durationMillis) {
        String key = playerName.toLowerCase();
        long expires = durationMillis > 0 ? System.currentTimeMillis() + durationMillis : -1;
        mutesConfig.set(key + ".reason", reason);
        mutesConfig.set(key + ".by", issuedBy);
        mutesConfig.set(key + ".expires", expires);
        saveAll();
    }

    public boolean isMuted(String playerName) {
        String key = playerName.toLowerCase();
        if (!mutesConfig.contains(key)) return false;
        long expires = mutesConfig.getLong(key + ".expires", -1);
        if (expires > 0 && System.currentTimeMillis() > expires) {
            removeMute(playerName);
            return false;
        }
        return true;
    }

    public void removeMute(String playerName) {
        mutesConfig.set(playerName.toLowerCase(), null);
        saveAll();
    }

    public long getMuteExpiry(String playerName) {
        return mutesConfig.getLong(playerName.toLowerCase() + ".expires", -1);
    }

    public String getMuteReason(String playerName) {
        return mutesConfig.getString(playerName.toLowerCase() + ".reason", "");
    }

    public Set<String> getMutedPlayers() {
        Set<String> names = new java.util.HashSet<>(mutesConfig.getKeys(false));
        names.removeIf(name -> !isMuted(name));
        return names;
    }

    public boolean isAutoCropEnabled(UUID playerId, String cropKey, boolean defaultValue) {
        String path = playerId.toString();
        if (!autoCropConfig.contains(path)) {
            return defaultValue;
        }
        List<String> enabled = autoCropConfig.getStringList(path + ".crops");
        if (enabled.isEmpty() && autoCropConfig.isList(path)) {
            enabled = autoCropConfig.getStringList(path);
        }
        return enabled.contains(cropKey.toLowerCase());
    }

    public void setAutoCropEnabled(UUID playerId, String cropKey, boolean enabled) {
        String path = playerId.toString();
        List<String> enabledCrops = new ArrayList<>(autoCropConfig.getStringList(path + ".crops"));
        if (enabledCrops.isEmpty() && autoCropConfig.isList(path)) {
            enabledCrops = new ArrayList<>(autoCropConfig.getStringList(path));
        }
        if (enabled) {
            if (!enabledCrops.contains(cropKey.toLowerCase())) {
                enabledCrops.add(cropKey.toLowerCase());
            }
        } else {
            enabledCrops.removeIf(entry -> entry.equalsIgnoreCase(cropKey));
        }
        autoCropConfig.set(path + ".crops", enabledCrops);
        saveAutoCrop();
    }

    public AutoCropSettings getAutoCropSettings(UUID playerId, AutoCropSettings defaultSettings) {
        String path = playerId.toString() + ".settings";
        boolean blockImmature = autoCropConfig.getBoolean(path + ".block-immature", defaultSettings.isBlockImmatureBreaks());
        boolean protectionEnabled = autoCropConfig.contains(path + ".break-protection.enabled") ?
                autoCropConfig.getBoolean(path + ".break-protection.enabled") :
                defaultSettings.isBreakProtectionToggle();
        int protectionSeconds = autoCropConfig.getInt(path + ".break-protection.seconds", defaultSettings.getBreakProtectionSeconds());
        return new AutoCropSettings(blockImmature, protectionEnabled, protectionSeconds);
    }

    public void setAutoCropSettings(UUID playerId, AutoCropSettings settings) {
        String path = playerId.toString() + ".settings";
        autoCropConfig.set(path + ".block-immature", settings.isBlockImmatureBreaks());
        autoCropConfig.set(path + ".break-protection.enabled", settings.isBreakProtectionToggle());
        autoCropConfig.set(path + ".break-protection.seconds", settings.getBreakProtectionSeconds());
        saveAutoCrop();
    }

    private void saveAutoCrop() {
        try {
            autoCropConfig.save(autoCropFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save autocrop data: " + e.getMessage());
        }
    }

    public PlayerSettings loadPlayerSettings(UUID playerId, PlayerSettings defaults) {
        String path = playerId.toString();
        boolean autoPickup = playerSettingsConfig.getBoolean(path + ".auto-pickup", defaults.isAutoPickup());
        boolean protectTools = playerSettingsConfig.getBoolean(path + ".protect-tools", defaults.isProtectTools());
        boolean creeperShield = playerSettingsConfig.getBoolean(path + ".block-creeper-explosions", defaults.isBlockCreeperExplosions());
        boolean blockMobSpawns = playerSettingsConfig.getBoolean(path + ".block-mob-spawns", defaults.isBlockMobSpawns());
        boolean noFallDamage = playerSettingsConfig.getBoolean(path + ".no-fall-damage", defaults.isNoFallDamage());
        return new PlayerSettings(autoPickup, protectTools, creeperShield, blockMobSpawns, noFallDamage);
    }

    public void savePlayerSettings(UUID playerId, PlayerSettings settings) {
        String path = playerId.toString();
        playerSettingsConfig.set(path + ".auto-pickup", settings.isAutoPickup());
        playerSettingsConfig.set(path + ".protect-tools", settings.isProtectTools());
        playerSettingsConfig.set(path + ".block-creeper-explosions", settings.isBlockCreeperExplosions());
        playerSettingsConfig.set(path + ".block-mob-spawns", settings.isBlockMobSpawns());
        playerSettingsConfig.set(path + ".no-fall-damage", settings.isNoFallDamage());
        savePlayerSettingsFile();
    }

    private void savePlayerSettingsFile() {
        try {
            playerSettingsConfig.save(playerSettingsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save player settings: " + e.getMessage());
        }
    }
}
