package com.framework.data;

import com.framework.FrameworkPlugin;
import com.framework.util.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class WarpManager {
    private final FrameworkPlugin plugin;
    private final FileConfiguration config;

    public WarpManager(FrameworkPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.loadYaml("warps.yml");
    }

    public Set<String> getAllWarps() {
        return new HashSet<>(config.getKeys(false));
    }

    public Location getWarp(String name) {
        return config.contains(name.toLowerCase(Locale.ROOT)) ? LocationSerializer.fromString(config.getString(name.toLowerCase(Locale.ROOT))) : null;
    }

    public void setWarp(String name, Location location) {
        config.set(name.toLowerCase(Locale.ROOT), LocationSerializer.toString(location));
        save();
    }

    public void removeWarp(String name) {
        config.set(name.toLowerCase(Locale.ROOT), null);
        save();
    }

    public void save() { plugin.saveYaml("warps.yml", config);}    
}
