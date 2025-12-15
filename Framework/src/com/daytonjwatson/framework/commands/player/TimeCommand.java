package com.daytonjwatson.framework.commands.player;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

import java.util.List;
import java.util.stream.Collectors;

public class TimeCommand extends BaseCommand {
    public TimeCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messages.sendMessage(sender, "time-usage");
            return true;
        }

        World world;
        if (sender instanceof Player player) {
            world = player.getWorld();
        } else {
            world = Bukkit.getWorlds().get(0);
        }

        long time;
        switch (args[0].toLowerCase()) {
            case "day":
                time = 1000L;
                break;
            case "night":
                time = 13000L;
                break;
            default:
                try {
                    time = Long.parseLong(args[0]);
                } catch (NumberFormatException e) {
                    messages.sendMessage(sender, "time-invalid");
                    return true;
                }
        }

        world.setTime(time);
        messages.sendMessage(sender, "time-set", "time", args[0].toLowerCase());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("day", "night", "0", "6000", "18000").stream()
                    .filter(opt -> opt.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
