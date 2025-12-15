package com.daytonjwatson.framework.commands.player;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand extends BaseCommand {
    public SetHomeCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        Player player = (Player) sender;
        String name = args.length > 0 ? args[0] : "home";
        int limit = plugin.getConfig().getInt("homes.limit", 3);
        if (!storage.getHomes(player).contains(name.toLowerCase()) && storage.getHomes(player).size() >= limit) {
            messages.sendMessage(player, "home-limit");
            return true;
        }
        storage.setHome(player, name, player.getLocation());
        messages.sendMessage(player, "home-set", "home", name);
        return true;
    }
}
