package com.daytonjwatson.framework.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;

public class PlayerSettingsManager {
    private final FrameworkPlugin plugin;
    private final StorageManager storage;
    private final MessageHandler messages;
    private final boolean defaultAutoPickup;
    private final boolean defaultProtectTools;
    private final boolean defaultBlockCreeperExplosions;
    private final boolean defaultBlockMobSpawns;
    private final boolean defaultNoFallDamage;
    private final int toolProtectionThreshold;
    private final double creeperShieldRadius;
    private final double mobSpawnBlockRadius;
    private final String menuTitle;

    public PlayerSettingsManager(FrameworkPlugin plugin, StorageManager storage, MessageHandler messages) {
        this.plugin = plugin;
        this.storage = storage;
        this.messages = messages;

        FileConfiguration config = plugin.getConfig();
        this.defaultAutoPickup = config.getBoolean("player-settings.auto-pickup", true);
        this.defaultProtectTools = config.getBoolean("player-settings.protect-tools.enabled", true);
        this.defaultBlockCreeperExplosions = config.getBoolean("player-settings.block-creeper-explosions.enabled", false);
        this.defaultBlockMobSpawns = config.getBoolean("player-settings.block-mob-spawns.enabled", false);
        this.defaultNoFallDamage = config.getBoolean("player-settings.no-fall-damage", false);
        this.toolProtectionThreshold = Math.max(1, config.getInt("player-settings.protect-tools.warn-threshold", 3));
        this.creeperShieldRadius = Math.max(1.0d, config.getDouble("player-settings.block-creeper-explosions.radius", 6.0d));
        this.mobSpawnBlockRadius = Math.max(1.0d, config.getDouble("player-settings.block-mob-spawns.radius", 16.0d));
        this.menuTitle = ChatColor.translateAlternateColorCodes('&', config.getString("player-settings.gui-title", "&aPlayer Settings"));
    }

    public PlayerSettings getSettings(Player player) {
        return storage.loadPlayerSettings(player.getUniqueId(), getDefaultSettings());
    }

    public void saveSettings(UUID playerId, PlayerSettings settings) {
        storage.savePlayerSettings(playerId, settings);
    }

    public double getCreeperShieldRadius() {
        return creeperShieldRadius;
    }

    public double getMobSpawnBlockRadius() {
        return mobSpawnBlockRadius;
    }

    public int getToolProtectionThreshold() {
        return toolProtectionThreshold;
    }

    private PlayerSettings getDefaultSettings() {
        return new PlayerSettings(defaultAutoPickup, defaultProtectTools, defaultBlockCreeperExplosions, defaultBlockMobSpawns, defaultNoFallDamage);
    }

    public Inventory createMenu(Player player) {
        SettingsMenuHolder holder = new SettingsMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, 9, menuTitle);
        holder.setInventory(inventory);

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }

        PlayerSettings settings = getSettings(player);
        int slot = 1;
        slot = addSettingItem(inventory, holder, slot, new SettingButton(SettingType.AUTO_PICKUP, "Auto Pickup", Material.HOPPER), settings);
        slot = addSettingItem(inventory, holder, slot, new SettingButton(SettingType.PROTECT_TOOLS, "Protect Tools", Material.SHIELD), settings);
        slot = addSettingItem(inventory, holder, slot, new SettingButton(SettingType.BLOCK_CREEPER_EXPLOSIONS, "Creeper Shield", Material.CREEPER_HEAD), settings);
        slot = addSettingItem(inventory, holder, slot, new SettingButton(SettingType.BLOCK_MOB_SPAWNS, "Stop Mob Spawns", Material.ZOMBIE_HEAD), settings);
        addSettingItem(inventory, holder, slot, new SettingButton(SettingType.NO_FALL_DAMAGE, "No Fall Damage", Material.FEATHER), settings);
        return inventory;
    }

    public boolean isMenu(Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof SettingsMenuHolder;
    }

    public void handleMenuClick(Player player, Inventory inventory, int slot, ClickType clickType) {
        if (!(inventory.getHolder() instanceof SettingsMenuHolder holder)) {
            return;
        }

        SettingButton button = holder.getButton(slot);
        if (button == null) {
            return;
        }

        PlayerSettings settings = getSettings(player);
        PlayerSettings updated = switch (button.getType()) {
            case AUTO_PICKUP -> settings.withAutoPickup(!settings.isAutoPickup());
            case PROTECT_TOOLS -> settings.withProtectTools(!settings.isProtectTools());
            case BLOCK_CREEPER_EXPLOSIONS -> settings.withBlockCreeperExplosions(!settings.isBlockCreeperExplosions());
            case BLOCK_MOB_SPAWNS -> settings.withBlockMobSpawns(!settings.isBlockMobSpawns());
            case NO_FALL_DAMAGE -> settings.withNoFallDamage(!settings.isNoFallDamage());
        };

        saveSettings(player.getUniqueId(), updated);
        refreshItems(inventory, holder, updated);
        sendFeedback(player, button.getType(), updated);
    }

    private int addSettingItem(Inventory inventory, SettingsMenuHolder holder, int slot, SettingButton button, PlayerSettings settings) {
        holder.bind(slot, button);
        inventory.setItem(slot, buildSettingItem(settings, button));
        return slot + 1;
    }

    private void refreshItems(Inventory inventory, SettingsMenuHolder holder, PlayerSettings settings) {
        for (Map.Entry<Integer, SettingButton> entry : holder.getButtons().entrySet()) {
            inventory.setItem(entry.getKey(), buildSettingItem(settings, entry.getValue()));
        }
    }

    private ItemStack buildSettingItem(PlayerSettings settings, SettingButton button) {
        ItemStack item = new ItemStack(button.getDisplayMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        boolean enabled = isEnabled(settings, button.getType());
        meta.setDisplayName((enabled ? ChatColor.GREEN : ChatColor.RED) + button.getDisplayName());

        java.util.List<String> lore = new java.util.ArrayList<>();
        switch (button.getType()) {
            case AUTO_PICKUP -> {
                lore.add(ChatColor.GRAY + "Send block drops directly to");
                lore.add(ChatColor.GRAY + "your inventory.");
            }
            case PROTECT_TOOLS -> {
                lore.add(ChatColor.GRAY + "Prevent using tools when they");
                lore.add(ChatColor.GRAY + "are about to break.");
                lore.add(ChatColor.DARK_GRAY + "Warn at " + toolProtectionThreshold + " durability left.");
            }
            case BLOCK_CREEPER_EXPLOSIONS -> {
                lore.add(ChatColor.GRAY + "Cancel creeper explosions");
                lore.add(ChatColor.GRAY + "near you within " + creeperShieldRadius + " blocks.");
            }
            case BLOCK_MOB_SPAWNS -> {
                lore.add(ChatColor.GRAY + "Stop mobs from spawning");
                lore.add(ChatColor.GRAY + "around you within " + mobSpawnBlockRadius + " blocks.");
            }
            case NO_FALL_DAMAGE -> {
                lore.add(ChatColor.GRAY + "Negate fall damage.");
            }
        }
        lore.add("");
        lore.add((enabled ? ChatColor.GREEN : ChatColor.RED) + "Currently: " + (enabled ? "Enabled" : "Disabled"));
        lore.add(ChatColor.YELLOW + "Click to toggle.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private boolean isEnabled(PlayerSettings settings, SettingType type) {
        return switch (type) {
            case AUTO_PICKUP -> settings.isAutoPickup();
            case PROTECT_TOOLS -> settings.isProtectTools();
            case BLOCK_CREEPER_EXPLOSIONS -> settings.isBlockCreeperExplosions();
            case BLOCK_MOB_SPAWNS -> settings.isBlockMobSpawns();
            case NO_FALL_DAMAGE -> settings.isNoFallDamage();
        };
    }

    private void sendFeedback(Player player, SettingType type, PlayerSettings settings) {
        String state = isEnabled(settings, type) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled";
        String messageKey;
        String replacement = "";
        switch (type) {
            case AUTO_PICKUP -> messageKey = "player-settings-auto-pickup";
            case PROTECT_TOOLS -> messageKey = "player-settings-protect-tools";
            case BLOCK_CREEPER_EXPLOSIONS -> {
                messageKey = "player-settings-creeper-shield";
                replacement = String.valueOf(creeperShieldRadius);
            }
            case BLOCK_MOB_SPAWNS -> {
                messageKey = "player-settings-block-mob-spawns";
                replacement = String.valueOf(mobSpawnBlockRadius);
            }
            case NO_FALL_DAMAGE -> messageKey = "player-settings-no-fall-damage";
            default -> messageKey = "player-settings-updated";
        }
        String message = messages.getMessage(messageKey)
                .replace("%state%", state)
                .replace("%radius%", replacement);
        player.sendMessage(message);
    }

    private static class SettingsMenuHolder implements InventoryHolder {
        private final Map<Integer, SettingButton> buttons = new HashMap<>();
        private Inventory inventory;

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        public void bind(int slot, SettingButton button) {
            buttons.put(slot, button);
        }

        public SettingButton getButton(int slot) {
            return buttons.get(slot);
        }

        public Map<Integer, SettingButton> getButtons() {
            return buttons;
        }
    }

    private record SettingButton(SettingType type, String displayName, Material displayMaterial) {
        public SettingType getType() {
            return type;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getDisplayMaterial() {
            return displayMaterial;
        }
    }

    public enum SettingType {
        AUTO_PICKUP,
        PROTECT_TOOLS,
        BLOCK_CREEPER_EXPLOSIONS,
        BLOCK_MOB_SPAWNS,
        NO_FALL_DAMAGE
    }
}
