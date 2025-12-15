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

public class WeatherCommand extends BaseCommand {
    public WeatherCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messages.sendMessage(sender, "weather-usage");
            return true;
        }

        World world;
        if (sender instanceof Player player) {
            world = player.getWorld();
        } else {
            world = Bukkit.getWorlds().get(0);
        }

        String type = args[0].toLowerCase();
        int duration = 6000;
        if (args.length > 1) {
            try {
                duration = Integer.parseInt(args[1]) * 20;
            } catch (NumberFormatException e) {
                messages.sendMessage(sender, "weather-usage");
                return true;
            }
        }

        switch (type) {
            case "clear":
                world.setStorm(false);
                world.setThundering(false);
                world.setWeatherDuration(duration);
                break;
            case "rain":
                world.setStorm(true);
                world.setThundering(false);
                world.setWeatherDuration(duration);
                break;
            case "thunder":
                world.setStorm(true);
                world.setThundering(true);
                world.setThunderDuration(duration);
                break;
            default:
                messages.sendMessage(sender, "weather-usage");
                return true;
        }

        messages.sendMessage(sender, "weather-set", "weather", type);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("clear", "rain", "thunder").stream()
                    .filter(opt -> opt.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return List.of("60", "120", "300");
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
