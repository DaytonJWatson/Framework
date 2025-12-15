package com.daytonjwatson.framework.commands.player;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Map;

public class HelpCommand extends BaseCommand {
    public HelpCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(messages.getMessage("help-header"));

        boolean hasCommands = false;
        for (Map.Entry<String, Map<String, Object>> entry : plugin.getDescription().getCommands().entrySet()) {
            String commandName = entry.getKey();
            PluginCommand pluginCommand = plugin.getCommand(commandName);
            if (pluginCommand == null) continue;
            if (!pluginCommand.testPermissionSilent(sender)) continue;

            hasCommands = true;

            String description = pluginCommand.getDescription();
            if (description == null || description.isEmpty()) {
                description = messages.getMessage("help-no-description");
            }

            sender.sendMessage(messages.getMessage("help-format")
                    .replace("%command%", commandName)
                    .replace("%description%", description));
        }

        if (hasCommands) {
            sender.sendMessage(messages.getMessage("help-footer"));
        } else {
            sender.sendMessage(messages.getMessage("help-none"));
        }
        return true;
    }
}
