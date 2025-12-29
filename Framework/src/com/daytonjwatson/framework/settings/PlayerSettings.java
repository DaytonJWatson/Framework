package com.daytonjwatson.framework.settings;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.bukkit.entity.EntityType;

public class PlayerSettings {
    private final boolean autoPickup;
    private final boolean protectTools;
    private final boolean blockCreeperExplosions;
    private final boolean blockMobSpawns;
    private final boolean noFallDamage;
    private final Map<EntityType, Boolean> mobSpawnPreferences;

    public PlayerSettings(boolean autoPickup, boolean protectTools, boolean blockCreeperExplosions, boolean blockMobSpawns, boolean noFallDamage, Map<EntityType, Boolean> mobSpawnPreferences) {
        this.autoPickup = autoPickup;
        this.protectTools = protectTools;
        this.blockCreeperExplosions = blockCreeperExplosions;
        this.blockMobSpawns = blockMobSpawns;
        this.noFallDamage = noFallDamage;
        this.mobSpawnPreferences = Collections.unmodifiableMap(new EnumMap<>(mobSpawnPreferences));
    }

    public boolean isAutoPickup() {
        return autoPickup;
    }

    public boolean isProtectTools() {
        return protectTools;
    }

    public boolean isBlockCreeperExplosions() {
        return blockCreeperExplosions;
    }

    public boolean isBlockMobSpawns() {
        return blockMobSpawns;
    }

    public boolean isNoFallDamage() {
        return noFallDamage;
    }

    public Map<EntityType, Boolean> getMobSpawnPreferences() {
        return mobSpawnPreferences;
    }

    public boolean isMobSpawnBlocked(EntityType entityType) {
        Boolean blocked = mobSpawnPreferences.get(entityType);
        return blocked == null || blocked;
    }

    public PlayerSettings withAutoPickup(boolean enabled) {
        return new PlayerSettings(enabled, protectTools, blockCreeperExplosions, blockMobSpawns, noFallDamage, mobSpawnPreferences);
    }

    public PlayerSettings withProtectTools(boolean enabled) {
        return new PlayerSettings(autoPickup, enabled, blockCreeperExplosions, blockMobSpawns, noFallDamage, mobSpawnPreferences);
    }

    public PlayerSettings withBlockCreeperExplosions(boolean enabled) {
        return new PlayerSettings(autoPickup, protectTools, enabled, blockMobSpawns, noFallDamage, mobSpawnPreferences);
    }

    public PlayerSettings withBlockMobSpawns(boolean enabled) {
        return new PlayerSettings(autoPickup, protectTools, blockCreeperExplosions, enabled, noFallDamage, mobSpawnPreferences);
    }

    public PlayerSettings withMobSpawnPreference(EntityType entityType, boolean blocked) {
        Map<EntityType, Boolean> updated = new EnumMap<>(mobSpawnPreferences);
        updated.put(entityType, blocked);
        return new PlayerSettings(autoPickup, protectTools, blockCreeperExplosions, blockMobSpawns, noFallDamage, updated);
    }

    public PlayerSettings withNoFallDamage(boolean enabled) {
        return new PlayerSettings(autoPickup, protectTools, blockCreeperExplosions, blockMobSpawns, enabled, mobSpawnPreferences);
    }
}
