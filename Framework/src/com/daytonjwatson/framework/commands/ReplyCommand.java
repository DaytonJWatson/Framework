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
import java.util.UUID;

public class ReplyCommand extends BaseCommand {
    public ReplyCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (args.length == 0) {
            messages.sendMessage(sender, "reply-usage");
            return true;
        }
        Player player = (Player) sender;
        UUID targetId = playerData.getReplyTarget(player);
        if (targetId == null) {
            messages.sendMessage(player, "reply-none");
            return true;
        }
        Player target = Bukkit.getPlayer(targetId);
        if (target == null) {
            messages.sendMessage(player, "player-not-found");
            return true;
        }
        if (playerData.isIgnoring(target, player)) {
            messages.sendMessage(player, "msg-ignored", "player", target.getName());
            return true;
        }
        String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        player.sendMessage(messages.getMessage("msg-format-sender").replace("%player%", target.getName()).replace("%message%", message));
        target.sendMessage(messages.getMessage("msg-format-target").replace("%player%", player.getName()).replace("%message%", message));
        playerData.setReplyTarget(player, target);
        return true;
    }
}
