package com.daytonjwatson.framework.utils;

import com.daytonjwatson.framework.FrameworkPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class MessageHandler {

    private final FrameworkPlugin plugin;
    private final FileConfiguration messages;

    public MessageHandler(FrameworkPlugin plugin) {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(), "messages.yml");
        this.messages = YamlConfiguration.loadConfiguration(file);
    }

    public String getMessage(String key) {
        String raw = messages.getString(key, "&cMissing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public List<String> getMessageList(String key) {
        List<String> list = messages.getStringList(key);
        return list.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).toList();
    }

    public void sendMessage(CommandSender sender, String key) {
        sender.sendMessage(getMessage(key));
    }

    public void sendMessage(CommandSender sender, String key, String replacementKey, String replacementValue) {
        sender.sendMessage(getMessage(key).replace("%" + replacementKey + "%", replacementValue));
    }
}
