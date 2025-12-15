package com.daytonjwatson.framework.commands.player;

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

import java.util.stream.Collectors;

public class TpahereCommand extends BaseCommand {
    public TpahereCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (args.length == 0) {
            messages.sendMessage(sender, "tpahere-usage");
            return true;
        }

        Player requester = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            messages.sendMessage(sender, "player-not-found");
            return true;
        }
        if (target.equals(requester)) {
            messages.sendMessage(sender, "tpa-self");
            return true;
        }

        playerData.addTpaRequest(requester, target, true);
        messages.sendMessage(requester, "tpahere-sent", "player", target.getName());
        messages.sendMessage(target, "tpahere-received", "player", requester.getName());
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
