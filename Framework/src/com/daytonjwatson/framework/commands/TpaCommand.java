package com.daytonjwatson.framework.commands;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.data.TeleportRequest;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand extends BaseCommand {
    public TpaCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (args.length == 0) {
            messages.sendMessage(sender, "tpa-usage");
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
        playerData.addTpaRequest(requester, target);
        messages.sendMessage(requester, "tpa-sent", "player", target.getName());
        messages.sendMessage(target, "tpa-received", "player", requester.getName());
        return true;
    }
}
