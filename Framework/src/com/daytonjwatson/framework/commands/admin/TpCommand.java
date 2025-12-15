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

public class TpCommand extends BaseCommand {
    public TpCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.tp")) return true;
        if (args.length == 0) {
            messages.sendMessage(sender, "tp-usage");
            return true;
        }

        if (args.length == 1) {
            if (!requirePlayer(sender)) return true;
            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                messages.sendMessage(sender, "player-not-found");
                return true;
            }
            playerData.setLastLocation(player, player.getLocation());
            player.teleport(target.getLocation());
            messages.sendMessage(player, "tp-success", "player", target.getName());
            return true;
        }

        if (args.length == 2) {
            Player from = Bukkit.getPlayer(args[0]);
            Player to = Bukkit.getPlayer(args[1]);
            if (from == null || to == null) {
                messages.sendMessage(sender, "player-not-found");
                return true;
            }
            playerData.setLastLocation(from, from.getLocation());
            from.teleport(to.getLocation());
            messages.sendMessage(sender, "tp-success-other", "player", from.getName());
            messages.sendMessage(from, "tp-success", "player", to.getName());
            return true;
        }

        messages.sendMessage(sender, "tp-usage");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 || args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
