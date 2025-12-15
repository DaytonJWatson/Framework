package com.daytonjwatson.framework.commands.player;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class RealnameCommand extends BaseCommand {
    public RealnameCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messages.sendMessage(sender, "realname-usage");
            return true;
        }

        String lookup = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', args[0]));
        for (Player online : Bukkit.getOnlinePlayers()) {
            String display = ChatColor.stripColor(online.getDisplayName());
            if (display.equalsIgnoreCase(lookup) || online.getName().equalsIgnoreCase(lookup)) {
                messages.sendMessage(sender, "realname-result", "player", online.getName());
                return true;
            }
        }
        messages.sendMessage(sender, "realname-not-found");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getDisplayName)
                    .map(ChatColor::stripColor)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
