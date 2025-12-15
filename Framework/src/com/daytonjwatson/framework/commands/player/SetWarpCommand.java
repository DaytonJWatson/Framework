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

import java.util.ArrayList;
import java.util.List;

public class SetWarpCommand extends BaseCommand {
    public SetWarpCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (args.length == 0) {
            messages.sendMessage(sender, "setwarp-usage");
            return true;
        }
        Player player = (Player) sender;
        storage.setWarp(args[0], player.getLocation());
        messages.sendMessage(player, "warp-set", "warp", args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(storage.getWarps());
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
