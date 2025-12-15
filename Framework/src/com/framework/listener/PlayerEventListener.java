package com.framework.listener;

import com.framework.FrameworkPlugin;
import com.framework.data.PlayerDataManager;
import com.framework.util.MessageService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {
    private final PlayerDataManager playerData;
    private final MessageService messages;

    public PlayerEventListener(FrameworkPlugin plugin) {
        this.playerData = plugin.getPlayerDataManager();
        this.messages = plugin.getMessages();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerData.recordJoin(event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerData.recordQuit(event);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        playerData.recordBack(event.getEntity());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (playerData.isMuted(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            messages.sendMessage(event.getPlayer(), "muted");
        }
        if (playerData.isAfk(event.getPlayer().getUniqueId())) {
            playerData.setAfk(event.getPlayer().getUniqueId(), false);
            messages.sendMessage(event.getPlayer(), "afk-disable");
        }
    }
}
