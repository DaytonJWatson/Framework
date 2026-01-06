package com.daytonjwatson.framework.listeners;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import com.daytonjwatson.framework.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Iterator;

public class PlayerActivityListener implements Listener {
    private final FrameworkPlugin plugin;
    private final MessageHandler messages;
    private final PlayerDataManager playerData;
    private final StorageManager storage;

    public PlayerActivityListener(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        this.plugin = plugin;
        this.messages = messages;
        this.playerData = playerData;
        this.storage = storage;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        if (!storage.isBanned(playerName)) {
            return;
        }

        long expiry = storage.getBanExpiry(playerName);
        String reason = storage.getBanReason(playerName);
        if (reason == null || reason.isEmpty()) {
            reason = messages.getMessage("ban-default-reason");
        }

        if (expiry > 0) {
            String remaining = TimeUtil.formatDuration(expiry - System.currentTimeMillis());
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, messages.getMessage("tempban-kick-message")
                    .replace("%reason%", reason)
                    .replace("%time%", remaining));
        } else {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, messages.getMessage("ban-kick-message")
                    .replace("%reason%", reason));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String joinMessage = plugin.getConfig().getString("player-messages.join", messages.getMessage("join"));
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', joinMessage).replace("%player%", player.getName()));
        playerData.applyFlightState(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String quitMessage = plugin.getConfig().getString("player-messages.quit", messages.getMessage("quit"));
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', quitMessage).replace("%player%", player.getName()));
        playerData.setLastLocation(player, player.getLocation());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (storage.isMuted(event.getPlayer().getName())) {
            event.setCancelled(true);
            long expiry = storage.getMuteExpiry(event.getPlayer().getName());
            String messageKey = expiry > 0 ? "mute-active-temp" : "mute-active";
            String remaining = expiry > 0 ? TimeUtil.formatDuration(expiry - System.currentTimeMillis()) : "";
            event.getPlayer().sendMessage(messages.getMessage(messageKey).replace("%time%", remaining));
            return;
        }

        Iterator<Player> iterator = event.getRecipients().iterator();
        while (iterator.hasNext()) {
            Player recipient = iterator.next();
            if (playerData.isIgnoring(recipient, event.getPlayer())) {
                iterator.remove();
            }
        }

        String format = plugin.getConfig().getString("chat.format", "&7%player% &8» &r%message%");
        format = ChatColor.translateAlternateColorCodes('&', format)
                .replace("%displayname%", "%1$s")
                .replace("%player%", "%1$s")
                .replace("%message%", "%2$s");
        event.setFormat(format);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String respawnPreference = plugin.getConfig().getString("death.respawn", "spawn");

        if ("bed".equalsIgnoreCase(respawnPreference)) {
            @SuppressWarnings("deprecation")
			Location bedSpawn = player.getBedSpawnLocation();
            if (bedSpawn != null) {
                event.setRespawnLocation(bedSpawn);
                return;
            }
        }

        World world = Bukkit.getWorld(plugin.getConfig().getString("spawn.world", player.getWorld().getName()));
        if (world == null) {
            world = player.getWorld();
        }
        double x = plugin.getConfig().getDouble("spawn.x", player.getWorld().getSpawnLocation().getX());
        double y = plugin.getConfig().getDouble("spawn.y", player.getWorld().getSpawnLocation().getY());
        double z = plugin.getConfig().getDouble("spawn.z", player.getWorld().getSpawnLocation().getZ());
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw", 0f);
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch", 0f);
        Location spawnLocation = new Location(world, x, y, z, yaw, pitch);
        event.setRespawnLocation(spawnLocation);
        playerData.applyFlightState(player);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        playerData.applyFlightState(event.getPlayer());
    }
}
