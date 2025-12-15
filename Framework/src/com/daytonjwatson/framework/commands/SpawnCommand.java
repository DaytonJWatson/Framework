package com.daytonjwatson.framework.commands;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseCommand {
    public SpawnCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        Player player = (Player) sender;
        World world = Bukkit.getWorld(plugin.getConfig().getString("spawn.world", player.getWorld().getName()));
        double x = plugin.getConfig().getDouble("spawn.x", player.getWorld().getSpawnLocation().getX());
        double y = plugin.getConfig().getDouble("spawn.y", player.getWorld().getSpawnLocation().getY());
        double z = plugin.getConfig().getDouble("spawn.z", player.getWorld().getSpawnLocation().getZ());
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw", 0f);
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch", 0f);
        Location loc = new Location(world, x, y, z, yaw, pitch);
        playerData.setLastLocation(player, player.getLocation());
        player.teleport(loc);
        messages.sendMessage(player, "teleported-spawn");
        return true;
    }
}
