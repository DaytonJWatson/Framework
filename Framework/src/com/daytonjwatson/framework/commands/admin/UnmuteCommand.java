package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UnmuteCommand extends BaseCommand {
    public UnmuteCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.unmute")) {
            return true;
        }

        if (args.length < 1) {
            messages.sendMessage(sender, "unmute-usage");
            return true;
        }

        String targetName = args[0];
        if (!storage.isMuted(targetName)) {
            messages.sendMessage(sender, "unmute-not-muted");
            return true;
        }

        storage.removeMute(targetName);
        messages.sendMessage(sender, "unmute-success", "player", targetName);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return Collections.singletonList(messages.getMessage("unmute-usage"));
        }
        if (args.length == 1) {
            List<String> names = new ArrayList<>(storage.getMutedPlayers());
            return names.stream()
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
