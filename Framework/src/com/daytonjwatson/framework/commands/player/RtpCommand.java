package com.daytonjwatson.framework.commands.player;

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

import java.util.Random;

public class RtpCommand extends BaseCommand {
    private final Random random = new Random();

    public RtpCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        Player player = (Player) sender;
        messages.sendMessage(player, "rtp-start");
        int radius = plugin.getConfig().getInt("rtp.radius", 5000);
        World world = player.getWorld();
        Location target = null;
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(radius * 2) - radius;
            int z = random.nextInt(radius * 2) - radius;
            int y = world.getHighestBlockYAt(x, z) + 1;
            Block block = world.getBlockAt(x, y - 1, z);
            if (block.getType() != Material.WATER && block.getType() != Material.LAVA) {
                target = new Location(world, x + 0.5, y, z + 0.5);
                break;
            }
        }
        if (target == null) {
            messages.sendMessage(player, "rtp-failed");
            return true;
        }
        playerData.setLastLocation(player, player.getLocation());
        playerData.initiateTeleport(player, target, () -> messages.sendMessage(player, "rtp-success"));
        return true;
    }
}
