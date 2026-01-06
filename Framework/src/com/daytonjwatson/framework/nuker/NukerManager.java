package com.daytonjwatson.framework.nuker;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class NukerManager {

    private static final int DEFAULT_RADIUS = 1;
    private static final int MIN_RADIUS = 1;
    private static final int MAX_RADIUS = 5;
    private static final int TASK_INTERVAL_TICKS = 2;
    private final FrameworkPlugin plugin;
    private final MessageHandler messages;
    private final Set<UUID> activeNukers = ConcurrentHashMap.newKeySet();
    private final ConcurrentMap<UUID, Long> lastToolWarnings = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Integer> playerRadii = new ConcurrentHashMap<>();
    private static final long TOOL_WARNING_COOLDOWN_MILLIS = 2000L;
    private final Set<Material> unbreakable = Set.of(
            Material.BEDROCK,
            Material.BARRIER,
            Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK,
            Material.END_PORTAL,
            Material.END_PORTAL_FRAME,
            Material.END_GATEWAY,
            Material.STRUCTURE_BLOCK,
            Material.JIGSAW
    );

    public NukerManager(FrameworkPlugin plugin, MessageHandler messages) {
        this.plugin = plugin;
        this.messages = messages;
        startTask();
    }

    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();
        if (activeNukers.contains(uuid)) {
            activeNukers.remove(uuid);
            messages.sendMessage(player, "nuker-disabled");
            return false;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool == null || tool.getType().isAir()) {
            messages.sendMessage(player, "nuker-no-tool");
            return false;
        }

        activeNukers.add(uuid);
        messages.sendMessage(player, "nuker-enabled");
        return true;
    }

    public boolean setRadius(Player player, int radius) {
        if (radius < MIN_RADIUS || radius > MAX_RADIUS) {
            messages.sendMessage(player, "nuker-radius-invalid");
            return false;
        }
        playerRadii.put(player.getUniqueId(), radius);
        messages.sendMessage(player, "nuker-radius-set", "radius", String.valueOf(radius));
        return true;
    }

    public void disable(Player player) {
        if (activeNukers.remove(player.getUniqueId())) {
            messages.sendMessage(player, "nuker-disabled");
        }
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, TASK_INTERVAL_TICKS, TASK_INTERVAL_TICKS);
    }

    private void tick() {
        for (UUID uuid : activeNukers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                activeNukers.remove(uuid);
                continue;
            }

            ItemStack tool = player.getInventory().getItemInMainHand();
            if (tool == null || tool.getType().isAir()) {
                warnTool(player);
                continue;
            }

            breakNearby(player);
        }
    }

    private void warnTool(Player player) {
        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();
        Long last = lastToolWarnings.get(uuid);
        if (last == null || now - last >= TOOL_WARNING_COOLDOWN_MILLIS) {
            messages.sendMessage(player, "nuker-no-tool");
            lastToolWarnings.put(uuid, now);
        }
    }

    private void breakNearby(Player player) {
        Location location = player.getLocation();
        World world = player.getWorld();
        int centerX = location.getBlockX();
        int centerY = location.getBlockY();
        int centerZ = location.getBlockZ();
        int radius = playerRadii.getOrDefault(player.getUniqueId(), DEFAULT_RADIUS);

        int minY = Math.max(world.getMinHeight(), centerY);
        int maxY = Math.min(world.getMaxHeight() - 1, centerY + (radius * 2));

        Stream<Block> targets = BlockStreamHelper.cube(world, centerX, centerY, centerZ, radius, minY, maxY);
        targets.forEach(block -> {
            if (shouldSkip(block, centerX, centerY, centerZ)) {
                return;
            }
            player.breakBlock(block);
        });
    }

    private boolean shouldSkip(Block block, int centerX, int centerY, int centerZ) {
        Material type = block.getType();
        if (type.isAir() || unbreakable.contains(type)) {
            return true;
        }
        return block.getX() == centerX && block.getY() == centerY && block.getZ() == centerZ;
    }
}
