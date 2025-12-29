package com.daytonjwatson.framework.commands.player;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.settings.PlayerSettingsManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerSettingsCommand extends BaseCommand {
    private final PlayerSettingsManager settingsManager;

    public PlayerSettingsCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages, PlayerSettingsManager settingsManager) {
        super(plugin, api, storage, playerData, messages);
        this.settingsManager = settingsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!requirePermission(sender, "framework.playersettings")) return true;

        Player player = (Player) sender;
        player.openInventory(settingsManager.createMenu(player));
        return true;
    }
}
