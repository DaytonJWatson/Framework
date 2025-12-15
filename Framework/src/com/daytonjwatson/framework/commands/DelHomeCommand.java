package com.daytonjwatson.framework.commands;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCommand extends BaseCommand {
    public DelHomeCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        Player player = (Player) sender;
        String name = args.length > 0 ? args[0] : "home";
        if (!storage.getHomes(player).contains(name.toLowerCase())) {
            messages.sendMessage(player, "home-missing", "home", name);
            return true;
        }
        storage.deleteHome(player, name);
        messages.sendMessage(player, "home-deleted", "home", name);
        return true;
    }
}
