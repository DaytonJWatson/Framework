package com.daytonjwatson.framework.autocrop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class AutoCropListener implements Listener {
    private final AutoCropManager manager;

    public AutoCropListener(AutoCropManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        AutoCropManager.AutoCropResponse response = manager.handleBlockBreak(player, event.getBlock(), player.getInventory().getItemInMainHand());
        if (response.shouldCancelEvent()) {
            event.setCancelled(true);
        }
        if (response.shouldPreventDrops()) {
            event.setDropItems(false);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!manager.isMenu(topInventory)) {
            return;
        }

        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != null && topInventory.equals(event.getClickedInventory())) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                manager.handleMenuClick(player, topInventory, event.getRawSlot());
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
        if (manager.isMenu(topInventory)) {
            event.setCancelled(true);
        }
    }
}
