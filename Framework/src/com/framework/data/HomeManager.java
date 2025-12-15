package com.framework.data;

import com.framework.FrameworkPlugin;
import com.framework.util.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class HomeManager {
    private final FrameworkPlugin plugin;
    private final FileConfiguration config;

    public HomeManager(FrameworkPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.loadYaml("homes.yml");
    }

    public Set<String> getHomeOwners() {
        return config.getKeys(false);
    }

    public Set<String> getHomes(UUID player) {
        return config.getConfigurationSection(player.toString()) != null ? config.getConfigurationSection(player.toString()).getKeys(false) : new HashSet<>();
    }

    public boolean hasHome(UUID player, String name) {
        return config.contains(player.toString() + "." + name.toLowerCase(Locale.ROOT));
    }

    public Location getHome(UUID player, String name) {
        String path = player.toString() + "." + name.toLowerCase(Locale.ROOT);
        return config.contains(path) ? LocationSerializer.fromString(config.getString(path)) : null;
    }

    public void setHome(UUID player, String name, Location location) {
        config.set(player.toString() + "." + name.toLowerCase(Locale.ROOT), LocationSerializer.toString(location));
        save();
    }

    public void deleteHome(UUID player, String name) {
        config.set(player.toString() + "." + name.toLowerCase(Locale.ROOT), null);
        save();
    }

    public void save() {
        plugin.saveYaml("homes.yml", config);
    }
}
