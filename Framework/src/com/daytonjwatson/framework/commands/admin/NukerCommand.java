package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public class NukerCommand extends BaseCommand {

    private static final int NUKER_RADIUS = 1;
    private static final int MIN_HEIGHT_OFFSET = 1;
    private static final int MAX_HEIGHT = 5;
    private static final Set<Material> UNBREAKABLE = EnumSet.of(
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

    public NukerCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        if (!requirePermission(sender, "framework.nuker")) {
            return true;
        }

        Player player = (Player) sender;
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool == null || tool.getType().isAir()) {
            messages.sendMessage(player, "nuker-no-tool");
            return true;
        }

        int broken = breakNearbyBlocks(player);
        if (broken <= 0) {
            messages.sendMessage(player, "nuker-no-blocks");
            return true;
        }

        messages.sendMessage(player, "nuker-success", "count", String.valueOf(broken));
        return true;
    }

    private int breakNearbyBlocks(Player player) {
        Location location = player.getLocation();
        World world = player.getWorld();
        int centerX = location.getBlockX();
        int centerY = location.getBlockY();
        int centerZ = location.getBlockZ();

        int minY = Math.max(world.getMinHeight(), centerY + MIN_HEIGHT_OFFSET);
        int maxY = Math.min(world.getMaxHeight() - 1, centerY + MAX_HEIGHT);

        int broken = 0;
        for (int y = minY; y <= maxY; y++) {
            for (int dx = -NUKER_RADIUS; dx <= NUKER_RADIUS; dx++) {
                for (int dz = -NUKER_RADIUS; dz <= NUKER_RADIUS; dz++) {
                    Block target = world.getBlockAt(centerX + dx, y, centerZ + dz);
                    if (shouldSkip(target)) {
                        continue;
                    }

                    if (player.breakBlock(target)) {
                        broken++;
                    }
                }
            }
        }
        return broken;
    }

    private boolean shouldSkip(Block block) {
        Material type = block.getType();
        return type.isAir() || UNBREAKABLE.contains(type);
    }
}
