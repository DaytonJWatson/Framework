package com.daytonjwatson.framework.commands;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand extends BaseCommand {
    public HomeCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        Player player = (Player) sender;
        String name = args.length > 0 ? args[0] : "home";
        Location home = storage.getHome(player, name);
        if (home == null) {
            messages.sendMessage(player, "home-missing", "home", name);
            return true;
        }
        playerData.setLastLocation(player, player.getLocation());
        player.teleport(home);
        messages.sendMessage(player, "home-teleport", "home", name);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player player && args.length == 1) {
            return new ArrayList<>(storage.getHomes(player));
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
