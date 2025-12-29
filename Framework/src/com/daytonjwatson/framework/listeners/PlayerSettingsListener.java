package com.daytonjwatson.framework.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
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

import com.daytonjwatson.framework.settings.PlayerSettings;
import com.daytonjwatson.framework.settings.PlayerSettingsManager;
import com.daytonjwatson.framework.utils.MessageHandler;

public class PlayerSettingsListener implements Listener {
    private final PlayerSettingsManager settingsManager;
    private final MessageHandler messages;
    private final Map<UUID, Long> toolWarnings = new HashMap<>();
    private static final long WARNING_COOLDOWN_MILLIS = 2000L;

    public PlayerSettingsListener(PlayerSettingsManager settingsManager, MessageHandler messages) {
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
        for (Player player : spawn.getWorld().getPlayers()) {
            PlayerSettings settings = settingsManager.getSettings(player);
            if (!settings.isBlockMobSpawns()) {
                continue;
            }
            if (spawn.distanceSquared(player.getLocation()) <= settingsManager.getMobSpawnBlockRadius() * settingsManager.getMobSpawnBlockRadius()) {
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
        if (!settingsManager.isMenu(topInventory)) {
            return;
        }

        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != null && topInventory.equals(event.getClickedInventory())) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                settingsManager.handleMenuClick(player, topInventory, event.getRawSlot(), event.getClick());
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
        if (settingsManager.isMenu(topInventory)) {
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
}
