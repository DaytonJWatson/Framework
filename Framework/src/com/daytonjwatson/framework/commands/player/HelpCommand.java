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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HelpCommand extends BaseCommand {
    public HelpCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final int commandsPerPage = 8;
        List<PluginCommand> availableCommands = new ArrayList<>();

        for (String commandName : plugin.getDescription().getCommands().keySet()) {
            PluginCommand pluginCommand = plugin.getCommand(commandName);
            if (pluginCommand == null) continue;
            if (!pluginCommand.testPermissionSilent(sender)) continue;

            availableCommands.add(pluginCommand);
        }

        if (availableCommands.isEmpty()) {
            sender.sendMessage(messages.getMessage("help-none"));
            return true;
        }

        availableCommands.sort(Comparator.comparing(PluginCommand::getName));

        int totalPages = (int) Math.ceil((double) availableCommands.size() / commandsPerPage);
        int page = 1;

        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(messages.getMessage("help-invalid-page").replace("%pages%", String.valueOf(totalPages)));
                return true;
            }
        }

        if (page < 1 || page > totalPages) {
            sender.sendMessage(messages.getMessage("help-invalid-page").replace("%pages%", String.valueOf(totalPages)));
            return true;
        }

        int startIndex = (page - 1) * commandsPerPage;
        int endIndex = Math.min(startIndex + commandsPerPage, availableCommands.size());

        sender.sendMessage(messages.getMessage("help-header")
                .replace("%page%", String.valueOf(page))
                .replace("%pages%", String.valueOf(totalPages)));

        for (int i = startIndex; i < endIndex; i++) {
            PluginCommand pluginCommand = availableCommands.get(i);
            String description = pluginCommand.getDescription();
            if (description == null || description.isEmpty()) {
                description = messages.getMessage("help-no-description");
            }

            sender.sendMessage(messages.getMessage("help-format")
                    .replace("%command%", pluginCommand.getName())
                    .replace("%description%", description));
        }

        sender.sendMessage(messages.getMessage("help-footer")
                .replace("%start%", String.valueOf(startIndex + 1))
                .replace("%end%", String.valueOf(endIndex))
                .replace("%total%", String.valueOf(availableCommands.size()))
                .replace("%page%", String.valueOf(page))
                .replace("%pages%", String.valueOf(totalPages)));
        return true;
    }
}
