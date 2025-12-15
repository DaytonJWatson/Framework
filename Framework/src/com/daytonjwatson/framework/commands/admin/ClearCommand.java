package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ClearCommand extends BaseCommand {
    public ClearCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.clear")) {
            return true;
        }

        if (args.length == 0 && !requirePlayer(sender)) {
            return true;
        }

        org.bukkit.entity.Player target;
        if (args.length > 0) {
            target = org.bukkit.Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                messages.sendMessage(sender, "player-not-found");
                return true;
            }
        } else {
            target = (org.bukkit.entity.Player) sender;
        }

        target.getInventory().clear();
        target.getInventory().setArmorContents(new org.bukkit.inventory.ItemStack[0]);
        target.getInventory().setExtraContents(new org.bukkit.inventory.ItemStack[0]);
        target.getInventory().setItemInOffHand(null);
        target.updateInventory();

        if (target.equals(sender)) {
            messages.sendMessage(target, "clear-self");
        } else {
            messages.sendMessage(sender, "clear-other", "player", target.getName());
            messages.sendMessage(target, "clear-notify", "player", sender.getName());
        }
        return true;
    }
}
