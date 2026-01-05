package com.daytonjwatson.framework.settings;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
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
    private static final List<MobPreferenceOption> MOB_PREFERENCE_OPTIONS = List.of(
            new MobPreferenceOption(EntityType.ZOMBIE, "Zombies", Material.ROTTEN_FLESH),
            new MobPreferenceOption(EntityType.SKELETON, "Skeletons", Material.BONE),
            new MobPreferenceOption(EntityType.CREEPER, "Creepers", Material.CREEPER_HEAD),
            new MobPreferenceOption(EntityType.SPIDER, "Spiders", Material.SPIDER_EYE),
            new MobPreferenceOption(EntityType.CAVE_SPIDER, "Cave Spiders", Material.FERMENTED_SPIDER_EYE),
            new MobPreferenceOption(EntityType.ENDERMAN, "Endermen", Material.ENDER_PEARL),
            new MobPreferenceOption(EntityType.WITCH, "Witches", Material.POTION),
            new MobPreferenceOption(EntityType.SLIME, "Slimes", Material.SLIME_BALL),
            new MobPreferenceOption(EntityType.MAGMA_CUBE, "Magma Cubes", Material.MAGMA_CREAM),
            new MobPreferenceOption(EntityType.PHANTOM, "Phantoms", Material.PHANTOM_MEMBRANE),
            new MobPreferenceOption(EntityType.DROWNED, "Drowned", Material.TRIDENT),
            new MobPreferenceOption(EntityType.HUSK, "Husks", Material.SAND),
            new MobPreferenceOption(EntityType.STRAY, "Strays", Material.ARROW),
            new MobPreferenceOption(EntityType.PILLAGER, "Pillagers", Material.CROSSBOW),
            new MobPreferenceOption(EntityType.VINDICATOR, "Vindicators", Material.IRON_AXE),
            new MobPreferenceOption(EntityType.EVOKER, "Evokers", Material.TOTEM_OF_UNDYING),
            new MobPreferenceOption(EntityType.VEX, "Vexes", Material.IRON_SWORD),
            new MobPreferenceOption(EntityType.RAVAGER, "Ravagers", Material.SADDLE),
            new MobPreferenceOption(EntityType.WITHER_SKELETON, "Wither Skeletons", Material.WITHER_SKELETON_SKULL),
            new MobPreferenceOption(EntityType.ZOMBIFIED_PIGLIN, "Zombified Piglins", Material.GOLD_NUGGET),
            new MobPreferenceOption(EntityType.PIGLIN, "Piglins", Material.GOLD_INGOT),
            new MobPreferenceOption(EntityType.PIGLIN_BRUTE, "Piglin Brutes", Material.GOLDEN_AXE),
            new MobPreferenceOption(EntityType.HOGLIN, "Hoglins", Material.PORKCHOP),
            new MobPreferenceOption(EntityType.ZOGLIN, "Zoglins", Material.COOKED_PORKCHOP),
            new MobPreferenceOption(EntityType.GHAST, "Ghasts", Material.GHAST_TEAR),
            new MobPreferenceOption(EntityType.BLAZE, "Blazes", Material.BLAZE_ROD),
            new MobPreferenceOption(EntityType.GUARDIAN, "Guardians", Material.PRISMARINE_SHARD),
            new MobPreferenceOption(EntityType.ELDER_GUARDIAN, "Elder Guardians", Material.PRISMARINE_CRYSTALS),
            new MobPreferenceOption(EntityType.SILVERFISH, "Silverfish", Material.SILVERFISH_SPAWN_EGG),
            new MobPreferenceOption(EntityType.ENDERMITE, "Endermites", Material.CHORUS_FRUIT),
            new MobPreferenceOption(EntityType.SHULKER, "Shulkers", Material.SHULKER_SHELL),
            new MobPreferenceOption(EntityType.WARDEN, "Wardens", Material.SCULK),
            new MobPreferenceOption(EntityType.VILLAGER, "Villagers", Material.EMERALD),
            new MobPreferenceOption(EntityType.IRON_GOLEM, "Iron Golems", Material.IRON_BLOCK),
            new MobPreferenceOption(EntityType.SNOW_GOLEM, "Snow Golems", Material.SNOW_BLOCK),
            new MobPreferenceOption(EntityType.WOLF, "Wolves", Material.BONE),
            new MobPreferenceOption(EntityType.CAT, "Cats", Material.COD),
            new MobPreferenceOption(EntityType.FOX, "Foxes", Material.SWEET_BERRIES),
            new MobPreferenceOption(EntityType.HORSE, "Horses", Material.HAY_BLOCK),
            new MobPreferenceOption(EntityType.DONKEY, "Donkeys", Material.CHEST),
            new MobPreferenceOption(EntityType.CAMEL, "Camels", Material.CACTUS),
            new MobPreferenceOption(EntityType.COW, "Cows", Material.LEATHER),
            new MobPreferenceOption(EntityType.SHEEP, "Sheep", Material.WHITE_WOOL),
            new MobPreferenceOption(EntityType.PIG, "Pigs", Material.PORKCHOP),
            new MobPreferenceOption(EntityType.CHICKEN, "Chickens", Material.EGG),
            new MobPreferenceOption(EntityType.RABBIT, "Rabbits", Material.RABBIT_FOOT)
    );
    private final StorageManager storage;
    private final MessageHandler messages;
    private final boolean defaultAutoPickup;
    private final boolean defaultProtectTools;
    private final boolean defaultBlockCreeperExplosions;
    private final boolean defaultBlockMobSpawns;
    private final boolean defaultNoFallDamage;
    private final Map<EntityType, Boolean> defaultMobSpawnPreferences;
    private final int toolProtectionThreshold;
    private final double creeperShieldRadius;
    private final double mobSpawnBlockRadius;
    private final String menuTitle;
    private final String mobMenuTitle;

    public PlayerSettingsManager(FrameworkPlugin plugin, StorageManager storage, MessageHandler messages) {
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
        this.mobMenuTitle = ChatColor.translateAlternateColorCodes('&', config.getString("player-settings.mob-gui-title", "&aMob Spawn Control"));
        this.defaultMobSpawnPreferences = loadDefaultMobPreferences(config);
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
        return new PlayerSettings(defaultAutoPickup, defaultProtectTools, defaultBlockCreeperExplosions, defaultBlockMobSpawns, defaultNoFallDamage, new EnumMap<>(defaultMobSpawnPreferences));
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

    public boolean isMobMenu(Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof MobSpawnMenuHolder;
    }

    public Inventory createMobMenu(Player player) {
        MobSpawnMenuHolder holder = new MobSpawnMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, 54, mobMenuTitle);
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
        int slot = 0;
        for (MobPreferenceOption option : MOB_PREFERENCE_OPTIONS) {
            holder.bind(slot, option);
            inventory.setItem(slot, buildMobPreferenceItem(settings, option));
            slot++;
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Back to Settings");
            back.setItemMeta(backMeta);
        }
        holder.setBackSlot(53);
        inventory.setItem(53, back);
        return inventory;
    }

    public void handleMenuClick(Player player, Inventory inventory, int slot, ClickType clickType, boolean shiftClick) {
        if (!(inventory.getHolder() instanceof SettingsMenuHolder holder)) {
            return;
        }

        SettingButton button = holder.getButton(slot);
        if (button == null) {
            return;
        }

        if (button.getType() == SettingType.BLOCK_MOB_SPAWNS && shiftClick) {
            player.openInventory(createMobMenu(player));
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

    public void handleMobMenuClick(Player player, Inventory inventory, int slot) {
        if (!(inventory.getHolder() instanceof MobSpawnMenuHolder holder)) {
            return;
        }

        if (slot == holder.getBackSlot()) {
            player.openInventory(createMenu(player));
            return;
        }

        MobPreferenceOption option = holder.getOption(slot);
        if (option == null) {
            return;
        }

        PlayerSettings settings = getSettings(player);
        boolean blocked = settings.isMobSpawnBlocked(option.entityType());
        PlayerSettings updated = settings.withMobSpawnPreference(option.entityType(), !blocked);
        saveSettings(player.getUniqueId(), updated);
        refreshMobMenu(inventory, holder, updated);
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

        List<String> lore = new ArrayList<>();
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
                lore.add(ChatColor.DARK_GRAY + "Shift-click to pick mobs.");
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

    private ItemStack buildMobPreferenceItem(PlayerSettings settings, MobPreferenceOption option) {
        ItemStack item = new ItemStack(option.displayMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        boolean blocked = settings.isMobSpawnBlocked(option.entityType());
        meta.setDisplayName((blocked ? ChatColor.RED : ChatColor.GREEN) + option.displayName());

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Toggle spawning for this mob type.");
        lore.add("");
        lore.add((blocked ? ChatColor.RED : ChatColor.GREEN) + "Currently: " + (blocked ? "Blocked" : "Allowed"));
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

    private static class MobSpawnMenuHolder implements InventoryHolder {
        private final Map<Integer, MobPreferenceOption> options = new HashMap<>();
        private Inventory inventory;
        private int backSlot = -1;

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        public void bind(int slot, MobPreferenceOption option) {
            options.put(slot, option);
        }

        public MobPreferenceOption getOption(int slot) {
            return options.get(slot);
        }

        public Map<Integer, MobPreferenceOption> getOptions() {
            return options;
        }

        public int getBackSlot() {
            return backSlot;
        }

        public void setBackSlot(int backSlot) {
            this.backSlot = backSlot;
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

    private record MobPreferenceOption(EntityType entityType, String displayName, Material displayMaterial) {
    }

    public enum SettingType {
        AUTO_PICKUP,
        PROTECT_TOOLS,
        BLOCK_CREEPER_EXPLOSIONS,
        BLOCK_MOB_SPAWNS,
        NO_FALL_DAMAGE
    }

    private Map<EntityType, Boolean> loadDefaultMobPreferences(FileConfiguration config) {
        Map<EntityType, Boolean> defaults = new EnumMap<>(EntityType.class);
        String basePath = "player-settings.block-mob-spawns.default-preferences";
        for (MobPreferenceOption option : MOB_PREFERENCE_OPTIONS) {
            String key = basePath + "." + option.entityType().name().toLowerCase(Locale.ROOT);
            defaults.put(option.entityType(), config.getBoolean(key, true));
        }
        return defaults;
    }

    private void refreshMobMenu(Inventory inventory, MobSpawnMenuHolder holder, PlayerSettings settings) {
        for (Map.Entry<Integer, MobPreferenceOption> entry : holder.getOptions().entrySet()) {
            inventory.setItem(entry.getKey(), buildMobPreferenceItem(settings, entry.getValue()));
        }
    }
}
