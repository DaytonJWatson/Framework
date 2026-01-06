package com.daytonjwatson.framework.listeners;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.data.PlayerDataManager;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PillarListener implements Listener {
    private final PlayerDataManager playerDataManager;
    private final FrameworkPlugin plugin;

    public PillarListener(FrameworkPlugin plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!playerDataManager.isPillarEnabled(player)) {
            return;
        }

        Block placed = event.getBlockPlaced();
        World world = placed.getWorld();
        int maxHeight = world.getMaxHeight();
        int startY = placed.getY() + 1;
        if (startY >= maxHeight) {
            return;
        }

        Block stopper = findStopperBlock(world, placed.getX(), startY, placed.getZ(), maxHeight);
        if (stopper == null) {
            return;
        }

        int blocksToPlace = stopper.getY() - startY;
        if (blocksToPlace <= 0) {
            return;
        }

        BlockData data = placed.getBlockData().clone();

        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack cost = new ItemStack(data.getMaterial(), blocksToPlace);
            if (!player.getInventory().containsAtLeast(cost, blocksToPlace)) {
                player.sendMessage(plugin.getMessageHandler().getMessage("pillar-not-enough-blocks"));
                return;
            }
            player.getInventory().removeItem(cost);
        }

        for (int y = startY; y < stopper.getY(); y++) {
            Block target = world.getBlockAt(placed.getX(), y, placed.getZ());
            target.setBlockData(data, false);
        }
    }

    private Block findStopperBlock(World world, int x, int startY, int z, int maxHeight) {
        for (int y = startY; y < maxHeight; y++) {
            Block block = world.getBlockAt(x, y, z);
            if (!block.isEmpty()) {
                return block;
            }
        }
        return null;
    }
}
