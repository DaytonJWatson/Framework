package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VanishCommand extends BaseCommand {
    public VanishCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.vanish")) {
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

        boolean newState = !playerData.isVanished(target);
        playerData.setVanished(target, newState);

        for (org.bukkit.entity.Player other : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (newState) {
                other.hidePlayer(plugin, target);
            } else {
                other.showPlayer(plugin, target);
            }
        }

        if (newState) {
            target.setCollidable(false);
        } else {
            target.setCollidable(true);
        }

        if (target.equals(sender)) {
            messages.sendMessage(target, newState ? "vanish-enabled" : "vanish-disabled");
        } else {
            messages.sendMessage(sender, newState ? "vanish-enabled-other" : "vanish-disabled-other", "player", target.getName());
            messages.sendMessage(target, newState ? "vanish-notify-enabled" : "vanish-notify-disabled", "player", sender.getName());
        }
        return true;
    }
}
