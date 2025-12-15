package com.daytonjwatson.framework.listeners;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import com.daytonjwatson.framework.utils.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Iterator;

public class PlayerActivityListener implements Listener {
    private final MessageHandler messages;
    private final PlayerDataManager playerData;
    private final StorageManager storage;

    public PlayerActivityListener(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        this.messages = messages;
        this.playerData = playerData;
        this.storage = storage;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(messages.getMessage("join").replace("%player%", player.getName()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(messages.getMessage("quit").replace("%player%", player.getName()));
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
    }
}
