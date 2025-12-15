package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class TpallCommand extends BaseCommand {
    public TpallCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.tpall")) {
            return true;
        }
        if (!requirePlayer(sender)) {
            return true;
        }

        org.bukkit.entity.Player initiator = (org.bukkit.entity.Player) sender;
        org.bukkit.Location destination = initiator.getLocation();

        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (!player.equals(initiator)) {
                player.teleport(destination);
                player.sendMessage(messages.getMessage("tpall-notify").replace("%player%", initiator.getName()));
            }
        }

        messages.sendMessage(initiator, "tpall-success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
