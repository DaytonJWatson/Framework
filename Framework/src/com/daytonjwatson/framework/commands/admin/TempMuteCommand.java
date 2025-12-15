package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import com.daytonjwatson.framework.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TempMuteCommand extends BaseCommand {
    public TempMuteCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.tempmute")) {
            return true;
        }

        if (args.length < 2) {
            messages.sendMessage(sender, "tempmute-usage");
            return true;
        }

        String targetName = args[0];
        long duration = TimeUtil.parseDuration(args[1]);
        if (duration <= 0) {
            messages.sendMessage(sender, "invalid-duration");
            return true;
        }

        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : messages.getMessage("mute-default-reason");
        storage.addMute(targetName, reason, sender.getName(), duration);

        Player target = Bukkit.getPlayerExact(targetName);
        if (target != null) {
            target.sendMessage(messages.getMessage("tempmute-notify")
                    .replace("%reason%", reason)
                    .replace("%time%", TimeUtil.formatDuration(duration)));
        }

        sender.sendMessage(messages.getMessage("tempmute-success")
                .replace("%player%", targetName)
                .replace("%reason%", reason)
                .replace("%time%", TimeUtil.formatDuration(duration)));
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
        return Collections.emptyList();
    }
}
