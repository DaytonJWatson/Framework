package com.daytonjwatson.framework.utils;

import java.util.Collections;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class InventoryUtil {

    private InventoryUtil() {
    }

    public static Map<Integer, ItemStack> addItemRespectingOffhand(Player player, ItemStack stack) {
        if (player == null || stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) {
            return Collections.emptyMap();
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack toAdd = stack.clone();
        ItemStack offhand = inventory.getItemInOffHand();

        if (canStack(offhand, toAdd)) {
            int space = toAdd.getMaxStackSize() - offhand.getAmount();
            if (space > 0) {
                int move = Math.min(space, toAdd.getAmount());
                offhand.setAmount(offhand.getAmount() + move);
                inventory.setItemInOffHand(offhand);
                toAdd.setAmount(toAdd.getAmount() - move);
            }
        }

        if (toAdd.getAmount() <= 0) {
            return Collections.emptyMap();
        }

        return inventory.addItem(toAdd);
    }

    private static boolean canStack(ItemStack offhand, ItemStack stack) {
        return offhand != null && offhand.getType() != Material.AIR && offhand.isSimilar(stack)
                && offhand.getAmount() < offhand.getMaxStackSize();
    }
}
