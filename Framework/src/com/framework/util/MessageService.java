package com.framework.util;

import com.framework.FrameworkPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessageService {
    private final FrameworkPlugin plugin;
    private final FileConfiguration config;

    public MessageService(FrameworkPlugin plugin, File file) {
        this.plugin = plugin;
        this.config = plugin.loadYaml("messages.yml");
    }

    public void sendMessage(CommandSender sender, String path, String... replacements) {
        String message = config.getString(path, path);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
