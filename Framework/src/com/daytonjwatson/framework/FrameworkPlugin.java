package com.daytonjwatson.framework;

import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.CommandRegistrar;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.listeners.PlayerActivityListener;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FrameworkPlugin extends JavaPlugin {

    public static FrameworkPlugin instance;
    private MessageHandler messageHandler;
    private StorageManager storageManager;
    private PlayerDataManager playerDataManager;
    private FrameworkAPI api;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        this.messageHandler = new MessageHandler(this);
        this.storageManager = new StorageManager(this);
        this.playerDataManager = new PlayerDataManager(this, messageHandler);
        this.api = new FrameworkAPI(storageManager, messageHandler, playerDataManager);

        new CommandRegistrar(this, api, storageManager, playerDataManager, messageHandler).registerCommands();
        Bukkit.getPluginManager().registerEvents(new PlayerActivityListener(this, api, storageManager, playerDataManager, messageHandler), this);
    }

    @Override
    public void onDisable() {
        storageManager.saveAll();
        playerDataManager.saveAll();
    }

    public static FrameworkPlugin getInstance() {
        return instance;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public FrameworkAPI getFrameworkAPI() {
        return api;
    }

    public FileConfiguration getConfiguration() {
        return getConfig();
    }
}
