package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.nuker.NukerManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NukerCommand extends BaseCommand {

    private final NukerManager nukerManager;
    private static final String SIZE_SUBCOMMAND = "size";

    public NukerCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages, NukerManager nukerManager) {
        super(plugin, api, storage, playerData, messages);
        this.nukerManager = nukerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        if (!requirePermission(sender, "framework.nuker")) {
            return true;
        }

        Player player = (Player) sender;
        if (args.length >= 2 && SIZE_SUBCOMMAND.equalsIgnoreCase(args[0])) {
            int radius;
            try {
                radius = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                messages.sendMessage(player, "nuker-radius-invalid");
                return true;
            }
            nukerManager.setRadius(player, radius);
            return true;
        }

        if (args.length == 1 && SIZE_SUBCOMMAND.equalsIgnoreCase(args[0])) {
            messages.sendMessage(player, "nuker-radius-usage");
            return true;
        }

        nukerManager.toggle(player);
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!requirePermission(sender, "framework.nuker")) {
            return java.util.Collections.emptyList();
        }

        if (args.length == 1) {
            return java.util.List.of(SIZE_SUBCOMMAND);
        }

        if (args.length == 2 && SIZE_SUBCOMMAND.equalsIgnoreCase(args[0])) {
            return java.util.List.of("1", "2", "3", "4", "5");
        }

        return java.util.Collections.emptyList();
    }
}
