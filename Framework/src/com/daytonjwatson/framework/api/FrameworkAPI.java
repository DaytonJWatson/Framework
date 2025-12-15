package com.daytonjwatson.framework.api;

import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class FrameworkAPI {
    private final StorageManager storageManager;
    private final MessageHandler messageHandler;
    private final PlayerDataManager playerDataManager;

    public FrameworkAPI(StorageManager storageManager, MessageHandler messageHandler, PlayerDataManager playerDataManager) {
        this.storageManager = storageManager;
        this.messageHandler = messageHandler;
        this.playerDataManager = playerDataManager;
    }

    public void setHome(Player player, String name, Location location) {
        storageManager.setHome(player, name, location);
    }

    public Location getHome(Player player, String name) {
        return storageManager.getHome(player, name);
    }

    public Set<String> getHomes(Player player) {
        return storageManager.getHomes(player);
    }

    public void setWarp(String name, Location location) {
        storageManager.setWarp(name, location);
    }

    public Location getWarp(String name) {
        return storageManager.getWarp(name);
    }

    public Set<String> getWarps() {
        return storageManager.getWarps();
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
