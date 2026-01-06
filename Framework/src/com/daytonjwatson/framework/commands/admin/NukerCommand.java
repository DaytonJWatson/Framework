package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.nuker.NukerManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NukerCommand extends BaseCommand {

    private final NukerManager nukerManager;

    public NukerCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages, NukerManager nukerManager) {
        super(plugin, api, storage, playerData, messages);
        this.nukerManager = nukerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        if (!requirePermission(sender, "framework.nuker")) {
            return true;
        }

        Player player = (Player) sender;
        nukerManager.toggle(player);
        return true;
    }
}
