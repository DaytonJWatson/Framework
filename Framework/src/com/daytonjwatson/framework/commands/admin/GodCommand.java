package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class GodCommand extends BaseCommand {
    public GodCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.god")) {
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

        boolean newState = !playerData.isGod(target);
        playerData.setGodMode(target, newState);
        target.setInvulnerable(newState);

        if (target.equals(sender)) {
            messages.sendMessage(target, newState ? "god-enabled" : "god-disabled");
        } else {
            messages.sendMessage(sender, newState ? "god-enabled-other" : "god-disabled-other", "player", target.getName());
            messages.sendMessage(target, newState ? "god-notify-enabled" : "god-notify-disabled", "player", sender.getName());
        }
        return true;
    }
}
