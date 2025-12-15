package com.daytonjwatson.framework.commands;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {
    protected final FrameworkPlugin plugin;
    protected final FrameworkAPI api;
    protected final StorageManager storage;
    protected final PlayerDataManager playerData;
    protected final MessageHandler messages;

    protected BaseCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        this.plugin = plugin;
        this.api = api;
        this.storage = storage;
        this.playerData = playerData;
        this.messages = messages;
    }

    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    protected boolean requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            messages.sendMessage(sender, "only-players");
            return false;
        }
        return true;
    }

    protected boolean requirePermission(CommandSender sender, String permission) {
        if (permission == null || permission.isEmpty() || sender.hasPermission(permission)) {
            return true;
        }
        messages.sendMessage(sender, "no-permission");
        return false;
    }
}
