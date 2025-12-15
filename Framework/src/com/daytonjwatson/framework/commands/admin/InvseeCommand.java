package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InvseeCommand extends BaseCommand {
    public InvseeCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.invsee")) {
            return true;
        }
        if (!requirePlayer(sender)) {
            return true;
        }

        if (args.length < 1) {
            messages.sendMessage(sender, "invsee-usage");
            return true;
        }

        org.bukkit.entity.Player viewer = (org.bukkit.entity.Player) sender;
        org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            messages.sendMessage(sender, "player-not-found");
            return true;
        }

        viewer.openInventory(target.getInventory());
        viewer.sendMessage(messages.getMessage("invsee-open").replace("%player%", target.getName()));
        return true;
    }
}
