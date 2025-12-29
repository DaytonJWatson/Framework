package com.daytonjwatson.framework.settings;

public class PlayerSettings {
    private final boolean autoPickup;
    private final boolean protectTools;
    private final boolean blockCreeperExplosions;
    private final boolean blockMobSpawns;
    private final boolean noFallDamage;

    public PlayerSettings(boolean autoPickup, boolean protectTools, boolean blockCreeperExplosions, boolean blockMobSpawns, boolean noFallDamage) {
        this.autoPickup = autoPickup;
        this.protectTools = protectTools;
        this.blockCreeperExplosions = blockCreeperExplosions;
        this.blockMobSpawns = blockMobSpawns;
        this.noFallDamage = noFallDamage;
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

    public PlayerSettings withAutoPickup(boolean enabled) {
        return new PlayerSettings(enabled, protectTools, blockCreeperExplosions, blockMobSpawns, noFallDamage);
    }

    public PlayerSettings withProtectTools(boolean enabled) {
        return new PlayerSettings(autoPickup, enabled, blockCreeperExplosions, blockMobSpawns, noFallDamage);
    }

    public PlayerSettings withBlockCreeperExplosions(boolean enabled) {
        return new PlayerSettings(autoPickup, protectTools, enabled, blockMobSpawns, noFallDamage);
    }

    public PlayerSettings withBlockMobSpawns(boolean enabled) {
        return new PlayerSettings(autoPickup, protectTools, blockCreeperExplosions, enabled, noFallDamage);
    }

    public PlayerSettings withNoFallDamage(boolean enabled) {
        return new PlayerSettings(autoPickup, protectTools, blockCreeperExplosions, blockMobSpawns, enabled);
    }
}
