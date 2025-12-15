package com.daytonjwatson.framework.commands;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MsgCommand extends BaseCommand {
    public MsgCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (args.length < 2) {
            messages.sendMessage(sender, "msg-usage");
            return true;
        }
        Player from = (Player) sender;
        Player to = Bukkit.getPlayer(args[0]);
        if (to == null) {
            messages.sendMessage(sender, "player-not-found");
            return true;
        }
        if (playerData.isIgnoring(to, from)) {
            messages.sendMessage(from, "msg-ignored", "player", to.getName());
            return true;
        }
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        from.sendMessage(messages.getMessage("msg-format-sender").replace("%player%", to.getName()).replace("%message%", message));
        to.sendMessage(messages.getMessage("msg-format-target").replace("%player%", from.getName()).replace("%message%", message));
        playerData.setReplyTarget(from, to);
        return true;
    }
}
