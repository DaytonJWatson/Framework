package com.daytonjwatson.framework.commands.player;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.autocrop.AutoCropManager;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoCropCommand extends BaseCommand {
    private final AutoCropManager autoCropManager;

    public AutoCropCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages, AutoCropManager autoCropManager) {
        super(plugin, api, storage, playerData, messages);
        this.autoCropManager = autoCropManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!requirePermission(sender, "framework.autocrop")) return true;
        if (!autoCropManager.isFeatureEnabled()) {
            messages.sendMessage(sender, "autocrop-disabled");
            return true;
        }

        Player player = (Player) sender;
        player.openInventory(autoCropManager.createMenu(player));
        return true;
    }
}
