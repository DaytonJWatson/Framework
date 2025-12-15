package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class EnderseeCommand extends BaseCommand {
    public EnderseeCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.endersee")) {
            return true;
        }
        if (!requirePlayer(sender)) {
            return true;
        }

        org.bukkit.entity.Player viewer = (org.bukkit.entity.Player) sender;
        org.bukkit.entity.Player target;

        if (args.length == 0) {
            target = viewer;
        } else {
            target = org.bukkit.Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                messages.sendMessage(sender, "player-not-found");
                return true;
            }
        }

        viewer.openInventory(target.getEnderChest());
        viewer.sendMessage(messages.getMessage("endersee-open").replace("%player%", target.getName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }
}
