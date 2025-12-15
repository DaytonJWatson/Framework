package com.daytonjwatson.framework.api;

import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
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

    public void deleteHome(Player player, String name) {
        storageManager.deleteHome(player, name);
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

    public void deleteWarp(String name) {
        storageManager.deleteWarp(name);
    }

    public void addWarning(String playerName, String reason, String issuedBy) {
        storageManager.addWarning(playerName, reason, issuedBy);
    }

    public Map<Integer, Map<String, String>> getWarnings(String playerName) {
        return storageManager.getWarnings(playerName);
    }

    public void clearWarnings(String playerName) {
        storageManager.clearWarnings(playerName);
    }

    public void addBan(String playerName, String reason, String issuedBy, long durationMillis) {
        storageManager.addBan(playerName, reason, issuedBy, durationMillis);
    }

    public boolean isBanned(String playerName) {
        return storageManager.isBanned(playerName);
    }

    public void removeBan(String playerName) {
        storageManager.removeBan(playerName);
    }

    public long getBanExpiry(String playerName) {
        return storageManager.getBanExpiry(playerName);
    }

    public Set<String> getBannedPlayers() {
        return storageManager.getBannedPlayers();
    }

    public String getBanReason(String playerName) {
        return storageManager.getBanReason(playerName);
    }

    public void addMute(String playerName, String reason, String issuedBy, long durationMillis) {
        storageManager.addMute(playerName, reason, issuedBy, durationMillis);
    }

    public boolean isMuted(String playerName) {
        return storageManager.isMuted(playerName);
    }

    public void removeMute(String playerName) {
        storageManager.removeMute(playerName);
    }

    public long getMuteExpiry(String playerName) {
        return storageManager.getMuteExpiry(playerName);
    }

    public String getMuteReason(String playerName) {
        return storageManager.getMuteReason(playerName);
    }

    public Set<String> getMutedPlayers() {
        return storageManager.getMutedPlayers();
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
