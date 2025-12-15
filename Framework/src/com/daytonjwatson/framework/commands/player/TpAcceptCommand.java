package com.daytonjwatson.framework.commands.player;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.data.TeleportRequest;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class TpAcceptCommand extends BaseCommand {
    public TpAcceptCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        Player player = (Player) sender;
        TeleportRequest request = playerData.getTpaRequest(player);
        if (request == null) {
            messages.sendMessage(player, "tpa-none");
            return true;
        }
        Player from = Bukkit.getPlayer(request.getFrom());
        if (from != null) {
            if (request.isTeleportHere()) {
                playerData.setLastLocation(player, player.getLocation());
                player.teleport(from.getLocation());
            } else {
                playerData.setLastLocation(from, from.getLocation());
                from.teleport(player.getLocation());
            }
            messages.sendMessage(from, "tpa-accepted", "player", player.getName());
            messages.sendMessage(player, "tpa-accepted", "player", from.getName());
        }
        playerData.clearTpaRequest(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            return Optional.ofNullable(playerData.getTpaRequest(player))
                    .map(request -> Bukkit.getPlayer(request.getFrom()))
                    .map(target -> List.of(target.getName()))
                    .orElseGet(List::of);
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
