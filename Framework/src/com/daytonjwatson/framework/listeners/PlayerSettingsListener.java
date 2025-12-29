package com.daytonjwatson.framework.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.settings.PlayerSettings;
import com.daytonjwatson.framework.settings.PlayerSettingsManager;
import com.daytonjwatson.framework.utils.MessageHandler;

public class PlayerSettingsListener implements Listener {
    private final FrameworkPlugin plugin;
    private final PlayerSettingsManager settingsManager;
    private final MessageHandler messages;
    private final Map<UUID, Long> toolWarnings = new HashMap<>();
    private static final long WARNING_COOLDOWN_MILLIS = 2000L;
    private static final double AUTO_PICKUP_RADIUS = 3.0d;
    private static final int AUTO_PICKUP_ATTEMPTS = 4;
    private static final long AUTO_PICKUP_INTERVAL_TICKS = 2L;

    public PlayerSettingsListener(FrameworkPlugin plugin, PlayerSettingsManager settingsManager, MessageHandler messages) {
        this.plugin = plugin;
        this.settingsManager = settingsManager;
        this.messages = messages;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDrop(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerSettings settings = settingsManager.getSettings(player);
        if (!settings.isAutoPickup()) {
            return;
        }

        Location dropLocation = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        Map<Integer, ItemStack> leftovers = new HashMap<>();
        event.setCancelled(true);
        event.getItems().forEach(item -> {
            Map<Integer, ItemStack> remain = player.getInventory().addItem(item.getItemStack());
            if (!remain.isEmpty()) {
                leftovers.putAll(remain);
            }
            item.remove();
        });

        if (!leftovers.isEmpty() && dropLocation.getWorld() != null) {
            leftovers.values().forEach(stack -> dropLocation.getWorld().dropItemNaturally(dropLocation, stack));
        }

        collectNearbyDrops(player, dropLocation);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerSettings settings = settingsManager.getSettings(player);
        if (!settings.isProtectTools()) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool == null || tool.getType().getMaxDurability() <= 0) {
            return;
        }

        if (!(tool.getItemMeta() instanceof Damageable damageable)) {
            return;
        }

        int max = tool.getType().getMaxDurability();
        int remaining = max - damageable.getDamage();
        if (remaining <= settingsManager.getToolProtectionThreshold()) {
            event.setCancelled(true);
            sendToolWarning(player, remaining);
        }

        if (settings.isAutoPickup()) {
            collectNearbyDrops(player, event.getBlock().getLocation().add(0.5, 0.5, 0.5));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        double radius = settingsManager.getCreeperShieldRadius();
        boolean cancel = false;
        for (Player nearby : creeper.getWorld().getPlayers()) {
            if (nearby.getLocation().distanceSquared(creeper.getLocation()) > radius * radius) {
                continue;
            }
            PlayerSettings settings = settingsManager.getSettings(nearby);
            if (settings.isBlockCreeperExplosions()) {
                cancel = true;
                nearby.sendMessage(messages.getMessage("player-settings-creeper-shield-active"));
            }
        }

        if (cancel) {
            event.blockList().clear();
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (shouldBypassSpawnReason(event.getSpawnReason())) {
            return;
        }

        Location spawn = event.getLocation();
        double radiusSquared = settingsManager.getMobSpawnBlockRadius() * settingsManager.getMobSpawnBlockRadius();
        for (Player player : spawn.getWorld().getPlayers()) {
            PlayerSettings settings = settingsManager.getSettings(player);
            if (!settings.isBlockMobSpawns() || !settings.isMobSpawnBlocked(event.getEntityType())) {
                continue;
            }
            if (spawn.distanceSquared(player.getLocation()) <= radiusSquared) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER || event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerSettings settings = settingsManager.getSettings(player);
        if (settings.isNoFallDamage() && player.hasPermission("framework.nofall")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        boolean settingsMenu = settingsManager.isMenu(topInventory);
        boolean mobMenu = settingsManager.isMobMenu(topInventory);
        if (!settingsMenu && !mobMenu) {
            return;
        }

        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != null && topInventory.equals(event.getClickedInventory())) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                if (settingsMenu) {
                    settingsManager.handleMenuClick(player, topInventory, event.getRawSlot(), event.getClick(), event.isShiftClick());
                } else {
                    settingsManager.handleMobMenuClick(player, topInventory, event.getRawSlot());
                }
            }
            return;
        }

        if (event.isShiftClick()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (settingsManager.isMenu(topInventory) || settingsManager.isMobMenu(topInventory)) {
            event.setCancelled(true);
        }
    }

    private void sendToolWarning(Player player, int remaining) {
        long now = System.currentTimeMillis();
        Long last = toolWarnings.get(player.getUniqueId());
        if (last != null && (now - last) < WARNING_COOLDOWN_MILLIS) {
            return;
        }
        toolWarnings.put(player.getUniqueId(), now);
        messages.sendMessage(player, "player-settings-tool-protect", "durability", String.valueOf(remaining));
    }

    private boolean shouldBypassSpawnReason(CreatureSpawnEvent.SpawnReason reason) {
        return switch (reason) {
            case SPAWNER, SPAWNER_EGG, BREEDING, EGG, DISPENSE_EGG, BUILD_IRONGOLEM, BUILD_SNOWMAN, BUILD_WITHER, CUSTOM, CURED -> true;
            default -> false;
        };
    }

    private void collectNearbyDrops(Player player, Location location) {
        if (location.getWorld() == null) {
            return;
        }
        runPickupScan(player, location);
        for (int i = 1; i < AUTO_PICKUP_ATTEMPTS; i++) {
            long delay = AUTO_PICKUP_INTERVAL_TICKS * i;
            Bukkit.getScheduler().runTaskLater(plugin, () -> runPickupScan(player, location), delay);
        }
    }

    private void runPickupScan(Player player, Location location) {
        if (!player.isOnline() || location.getWorld() == null) {
            return;
        }

        location.getWorld().getNearbyEntities(location, AUTO_PICKUP_RADIUS, AUTO_PICKUP_RADIUS, AUTO_PICKUP_RADIUS, entity -> entity instanceof Item)
                .forEach(entity -> {
                    Item item = (Item) entity;
                    if (item.isDead() || item.getItemStack().getAmount() <= 0) {
                        return;
                    }
                    Map<Integer, ItemStack> remaining = player.getInventory().addItem(item.getItemStack());
                    if (remaining.isEmpty()) {
                        item.remove();
                        return;
                    }

                    int totalRemaining = remaining.values().stream().mapToInt(ItemStack::getAmount).sum();
                    if (totalRemaining <= 0) {
                        item.remove();
                        return;
                    }

                    ItemStack first = item.getItemStack().clone();
                    first.setAmount(totalRemaining);
                    item.setItemStack(first);
                });
    }
}
