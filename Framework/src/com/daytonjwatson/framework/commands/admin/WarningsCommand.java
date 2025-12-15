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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WarningsCommand extends BaseCommand {
    public WarningsCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.warnings")) {
            return true;
        }

        if (args.length < 1) {
            messages.sendMessage(sender, "warnings-usage");
            return true;
        }

        String targetName = args[0];
        Map<Integer, Map<String, String>> warnings = storage.getWarnings(targetName);
        if (warnings.isEmpty()) {
            sender.sendMessage(messages.getMessage("warnings-none").replace("%player%", targetName));
            return true;
        }

        sender.sendMessage(messages.getMessage("warnings-header").replace("%player%", targetName));
        warnings.keySet().stream().sorted(Comparator.naturalOrder()).forEach(index -> {
            Map<String, String> warn = warnings.get(index);
            sender.sendMessage(messages.getMessage("warnings-entry")
                    .replace("%index%", String.valueOf(index))
                    .replace("%reason%", warn.getOrDefault("reason", ""))
                    .replace("%by%", warn.getOrDefault("by", "")));
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(player -> player.getName())
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
