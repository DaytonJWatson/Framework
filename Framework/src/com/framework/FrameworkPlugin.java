package com.framework;

import com.framework.command.CommandRouter;
import com.framework.data.HomeManager;
import com.framework.data.PlayerDataManager;
import com.framework.data.WarpManager;
import com.framework.listener.PlayerEventListener;
import com.framework.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class FrameworkPlugin extends JavaPlugin {
    private MessageService messages;
    private WarpManager warpManager;
    private HomeManager homeManager;
    private PlayerDataManager playerDataManager;
    private CommandRouter commandRouter;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        messages = new MessageService(this, new File(getDataFolder(), "messages.yml"));
        warpManager = new WarpManager(this);
        homeManager = new HomeManager(this);
        playerDataManager = new PlayerDataManager(this);
        commandRouter = new CommandRouter(this);
        registerCommands();
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(this), this);
        getLogger().info("Framework enabled with " + warpManager.getAllWarps().size() + " warps and homes for " + homeManager.getHomeOwners().size() + " players.");
    }

    private void registerCommands() {
        String[] commands = new String[]{
                "help", "list", "motd", "rules", "spawn", "setspawn", "warp", "setwarp", "warps", "home", "sethome", "delhome", "homes",
                "tpa", "tpaccept", "tpdeny", "back", "suicide", "afk", "msg", "reply", "ignore", "seen", "nickname", "realname",
                "rtp", "trash", "enderchest", "workbench", "stats", "playtime", "ban", "tempban", "unban", "mute", "tempmute", "kick",
                "warn", "warnings", "time", "weather", "gamemode", "tp", "tpall", "fly", "god", "heal", "feed", "vanish", "invsee",
                "endersee", "clear", "give", "enchant", "broadcast"
        };
        for (String command : commands) {
            if (getCommand(command) != null) {
                getCommand(command).setExecutor(commandRouter);
                getCommand(command).setTabCompleter(commandRouter);
            } else {
                getLogger().warning("Command not defined in plugin.yml: " + command);
            }
        }
    }

    @Override
    public void onDisable() {
        warpManager.save();
        homeManager.save();
        playerDataManager.save();
    }

    public MessageService getMessages() {
        return messages;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public FileConfiguration loadYaml(String name) {
        File file = new File(getDataFolder(), name);
        if (!file.exists()) {
            saveResource(name, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveYaml(String name, FileConfiguration config) {
        File file = new File(getDataFolder(), name);
        try {
            config.save(file);
        } catch (IOException e) {
            getLogger().severe("Unable to save " + name + ": " + e.getMessage());
        }
    }
}
