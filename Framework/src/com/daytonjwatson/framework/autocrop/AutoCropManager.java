package com.daytonjwatson.framework.autocrop;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AutoCropManager {
    private final FrameworkPlugin plugin;
    private final StorageManager storage;
    private final MessageHandler messages;
    private final boolean enabled;
    private final boolean respectFortune;
    private final boolean requireReplantItem;
    private final int replantDelay;
    private final long breakProtectionMillis;
    private final String menuTitle;
    private final Map<String, CropType> crops = new LinkedHashMap<>();
    private final Map<String, ProtectedCrop> protectedCrops = new HashMap<>();

    public AutoCropManager(FrameworkPlugin plugin, StorageManager storage, MessageHandler messages) {
        this.plugin = plugin;
        this.storage = storage;
        this.messages = messages;

        FileConfiguration config = plugin.getConfig();
        this.enabled = config.getBoolean("autocrop.enabled", true);
        this.respectFortune = config.getBoolean("autocrop.respect-fortune", true);
        this.requireReplantItem = config.getBoolean("autocrop.require-replant-item", true);
        this.replantDelay = Math.max(0, config.getInt("autocrop.replant-delay-ticks", 2));
        this.breakProtectionMillis = Math.max(0, config.getLong("autocrop.break-protection-seconds", 0L)) * 1000L;
        this.menuTitle = ChatColor.translateAlternateColorCodes('&', config.getString("autocrop.gui-title", "&aAuto Crop Settings"));

        crops.put("wheat", new CropType("wheat", Material.WHEAT, Material.WHEAT_SEEDS, Material.WHEAT_SEEDS, Material.FARMLAND));
        crops.put("carrots", new CropType("carrots", Material.CARROTS, Material.CARROT, Material.CARROT, Material.FARMLAND));
        crops.put("potatoes", new CropType("potatoes", Material.POTATOES, Material.POTATO, Material.POTATO, Material.FARMLAND));
        crops.put("beetroot", new CropType("beetroot", Material.BEETROOTS, Material.BEETROOT_SEEDS, Material.BEETROOT_SEEDS, Material.FARMLAND));
        crops.put("nether_wart", new CropType("nether_wart", Material.NETHER_WART, Material.NETHER_WART, Material.NETHER_WART, Material.SOUL_SAND));
    }

    public boolean isFeatureEnabled() {
        return enabled;
    }

    public Inventory createMenu(Player player) {
        int size = ((crops.size() + 8) / 9) * 9;
        AutoCropMenuHolder holder = new AutoCropMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, size, menuTitle);
        holder.setInventory(inventory);

        int slot = 0;
        for (CropType crop : crops.values()) {
            holder.bind(slot, crop);
            inventory.setItem(slot, buildMenuItem(player, crop));
            slot++;
        }

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        for (int i = slot; i < size; i++) {
            inventory.setItem(i, filler);
        }
        return inventory;
    }

    public boolean isMenu(Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof AutoCropMenuHolder;
    }

    public void handleMenuClick(Player player, Inventory inventory, int slot) {
        if (!(inventory.getHolder() instanceof AutoCropMenuHolder holder)) {
            return;
        }
        CropType crop = holder.getCrop(slot);
        if (crop == null) {
            return;
        }
        boolean newState = toggleCrop(player, crop);
        inventory.setItem(slot, buildMenuItem(player, crop));
        String key = newState ? "autocrop-toggle-enabled" : "autocrop-toggle-disabled";
        messages.sendMessage(player, key, "crop", crop.getDisplayName());
    }

    public AutoCropResponse handleBlockBreak(Player player, Block block, ItemStack tool) {
        if (!enabled) {
            return AutoCropResponse.none();
        }

        CropType crop = crops.values().stream()
                .filter(entry -> entry.getBlockMaterial() == block.getType())
                .findFirst()
                .orElse(null);

        if (crop == null) {
            return AutoCropResponse.none();
        }

        AutoCropResponse protectionCheck = enforceProtection(player, block.getLocation(), crop);
        if (protectionCheck.shouldCancelEvent()) {
            return protectionCheck;
        }

        if (!isCropEnabled(player, crop) || !isMature(block)) {
            return AutoCropResponse.none();
        }

        if (!hasRequiredSoil(block, crop)) {
            return AutoCropResponse.none();
        }

        List<ItemStack> drops = new ArrayList<>();
        if (respectFortune) {
            drops.addAll(block.getDrops(tool, player));
        } else {
            drops.addAll(block.getDrops());
        }

        boolean canReplant = !requireReplantItem || consumeReplantCost(player, crop, drops);
        dropItems(block.getLocation(), drops);

        if (!canReplant) {
            messages.sendMessage(player, "autocrop-missing-seed", "crop", crop.getDisplayName());
            return AutoCropResponse.preventDropsOnly();
        }

        Location replantLocation = block.getLocation();
        Bukkit.getScheduler().runTaskLater(plugin, () -> replantCrop(replantLocation, crop), replantDelay);
        return AutoCropResponse.preventDropsOnly();
    }

    private boolean isMature(Block block) {
        if (!(block.getBlockData() instanceof Ageable ageable)) {
            return false;
        }
        return ageable.getAge() >= ageable.getMaximumAge();
    }

    private boolean hasRequiredSoil(Block block, CropType crop) {
        Block below = block.getRelative(BlockFace.DOWN);
        return below.getType() == crop.getRequiredSoil();
    }

    private boolean consumeReplantCost(Player player, CropType crop, List<ItemStack> drops) {
        Iterator<ItemStack> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ItemStack drop = iterator.next();
            if (drop.getType() == crop.getReplantMaterial()) {
                int remaining = drop.getAmount() - 1;
                if (remaining <= 0) {
                    iterator.remove();
                } else {
                    drop.setAmount(remaining);
                }
                return true;
            }
        }

        ItemStack cost = new ItemStack(crop.getReplantMaterial(), 1);
        if (player.getInventory().containsAtLeast(cost, 1)) {
            player.getInventory().removeItem(cost);
            return true;
        }
        return false;
    }

    private void dropItems(Location location, List<ItemStack> drops) {
        if (location.getWorld() == null) {
            return;
        }
        for (ItemStack drop : drops) {
            if (drop.getType() == Material.AIR || drop.getAmount() <= 0) {
                continue;
            }
            location.getWorld().dropItemNaturally(location, drop);
        }
    }

    private void replantCrop(Location location, CropType crop) {
        Block target = location.getBlock();
        if (target.getType() != Material.AIR && target.getType() != crop.getBlockMaterial()) {
            return;
        }

        if (!hasRequiredSoil(target, crop)) {
            return;
        }

        target.setType(crop.getBlockMaterial(), false);
        if (target.getBlockData() instanceof Ageable ageable) {
            ageable.setAge(0);
            target.setBlockData(ageable, false);
        }

        if (breakProtectionMillis > 0) {
            String key = key(location);
            protectedCrops.put(key, new ProtectedCrop(System.currentTimeMillis() + breakProtectionMillis, crop.getDisplayName()));
        }
    }

    private boolean isCropEnabled(Player player, CropType crop) {
        FileConfiguration config = plugin.getConfig();
        boolean defaultEnabled = config.getBoolean("autocrop.crops." + crop.getKey(), true);
        return storage.isAutoCropEnabled(player.getUniqueId(), crop.getKey(), defaultEnabled);
    }

    private boolean toggleCrop(Player player, CropType crop) {
        boolean enabledState = !isCropEnabled(player, crop);
        storage.setAutoCropEnabled(player.getUniqueId(), crop.getKey(), enabledState);
        return enabledState;
    }

    private ItemStack buildMenuItem(Player player, CropType crop) {
        boolean enabledState = isCropEnabled(player, crop);
        ItemStack item = new ItemStack(crop.getDisplayMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String stateColor = enabledState ? ChatColor.GREEN.toString() : ChatColor.RED.toString();
            meta.setDisplayName(stateColor + crop.getDisplayName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Toggle automatic replanting for");
            lore.add(ChatColor.GRAY + crop.getDisplayName() + ".");
            lore.add("");
            lore.add((enabledState ? ChatColor.GREEN : ChatColor.RED) + "Currently: " + (enabledState ? "Enabled" : "Disabled"));
            lore.add(ChatColor.YELLOW + "Click to toggle.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private AutoCropResponse enforceProtection(Player player, Location location, CropType crop) {
        if (breakProtectionMillis <= 0) {
            return AutoCropResponse.none();
        }

        cleanupProtection();
        String key = key(location);
        ProtectedCrop protection = protectedCrops.get(key);
        if (protection == null) {
            return AutoCropResponse.none();
        }

        long remaining = protection.expiry - System.currentTimeMillis();
        if (remaining <= 0) {
            protectedCrops.remove(key);
            return AutoCropResponse.none();
        }

        long seconds = Math.max(1L, (long) Math.ceil(remaining / 1000.0));
        String cropName = protection.cropName != null ? protection.cropName : crop.getDisplayName();
        String message = messages.getMessage("autocrop-protected")
                .replace("%time%", String.valueOf(seconds))
                .replace("%crop%", cropName);
        player.sendMessage(message);
        return AutoCropResponse.cancelBreak();
    }

    private void cleanupProtection() {
        if (protectedCrops.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        protectedCrops.entrySet().removeIf(entry -> entry.getValue().expiry <= now);
    }

    private String key(Location location) {
        if (location.getWorld() == null) {
            return "unknown:" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
        }
        return location.getWorld().getUID() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    public static class AutoCropResponse {
        private final boolean handled;
        private final boolean cancelEvent;
        private final boolean preventDrops;

        private AutoCropResponse(boolean handled, boolean cancelEvent, boolean preventDrops) {
            this.handled = handled;
            this.cancelEvent = cancelEvent;
            this.preventDrops = preventDrops;
        }

        public static AutoCropResponse none() {
            return new AutoCropResponse(false, false, false);
        }

        public static AutoCropResponse cancelBreak() {
            return new AutoCropResponse(true, true, true);
        }

        public static AutoCropResponse preventDropsOnly() {
            return new AutoCropResponse(true, false, true);
        }

        public boolean isHandled() {
            return handled;
        }

        public boolean shouldCancelEvent() {
            return cancelEvent;
        }

        public boolean shouldPreventDrops() {
            return preventDrops;
        }
    }

    private static class ProtectedCrop {
        private final long expiry;
        private final String cropName;

        private ProtectedCrop(long expiry, String cropName) {
            this.expiry = expiry;
            this.cropName = cropName;
        }
    }

    private static class CropType {
        private final String key;
        private final Material blockMaterial;
        private final Material replantMaterial;
        private final Material displayMaterial;
        private final Material requiredSoil;

        public CropType(String key, Material blockMaterial, Material replantMaterial, Material displayMaterial, Material requiredSoil) {
            this.key = key.toLowerCase(Locale.ROOT);
            this.blockMaterial = blockMaterial;
            this.replantMaterial = replantMaterial;
            this.displayMaterial = displayMaterial;
            this.requiredSoil = requiredSoil;
        }

        public String getKey() {
            return key;
        }

        public Material getBlockMaterial() {
            return blockMaterial;
        }

        public Material getReplantMaterial() {
            return replantMaterial;
        }

        public Material getDisplayMaterial() {
            return displayMaterial;
        }

        public Material getRequiredSoil() {
            return requiredSoil;
        }

        public String getDisplayName() {
            String[] parts = key.split("_");
            StringBuilder name = new StringBuilder();
            for (String part : parts) {
                if (name.length() > 0) {
                    name.append(" ");
                }
                name.append(part.substring(0, 1).toUpperCase(Locale.ROOT)).append(part.substring(1));
            }
            return name.toString();
        }
    }

    private static class AutoCropMenuHolder implements InventoryHolder {
        private final Map<Integer, CropType> slotMap = new HashMap<>();
        private Inventory inventory;

        public void bind(int slot, CropType crop) {
            slotMap.put(slot, crop);
        }

        public CropType getCrop(int slot) {
            return slotMap.get(slot);
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
