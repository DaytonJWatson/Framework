package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class GamemodeCommand extends BaseCommand {
    public GamemodeCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.gamemode")) {
            return true;
        }

        if (args.length < 1) {
            messages.sendMessage(sender, "gamemode-usage");
            return true;
        }

        org.bukkit.GameMode mode = parseGameMode(args[0]);
        if (mode == null) {
            messages.sendMessage(sender, "gamemode-invalid");
            return true;
        }

        org.bukkit.entity.Player target;
        if (args.length > 1) {
            target = org.bukkit.Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                messages.sendMessage(sender, "player-not-found");
                return true;
            }
        } else {
            if (!requirePlayer(sender)) {
                return true;
            }
            target = (org.bukkit.entity.Player) sender;
        }

        target.setGameMode(mode);
        String modeName = mode.name().toLowerCase();
        if (target.equals(sender)) {
            messages.sendMessage(target, "gamemode-set-self", "mode", modeName);
        } else {
            sender.sendMessage(messages.getMessage("gamemode-set-other")
                    .replace("%player%", target.getName())
                    .replace("%mode%", modeName));
            messages.sendMessage(target, "gamemode-set-target", "mode", modeName);
        }
        return true;
    }

    private org.bukkit.GameMode parseGameMode(String input) {
        switch (input.toLowerCase()) {
            case "0":
            case "s":
            case "survival":
                return org.bukkit.GameMode.SURVIVAL;
            case "1":
            case "c":
            case "creative":
                return org.bukkit.GameMode.CREATIVE;
            case "2":
            case "a":
            case "adventure":
                return org.bukkit.GameMode.ADVENTURE;
            case "3":
            case "sp":
            case "spectator":
                return org.bukkit.GameMode.SPECTATOR;
            default:
                try {
                    return org.bukkit.GameMode.valueOf(input.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    return null;
                }
        }
    }
}
