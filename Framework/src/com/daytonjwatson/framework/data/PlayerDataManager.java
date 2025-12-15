package com.daytonjwatson.framework.data;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.utils.TimeUtil;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private final Map<UUID, Location> lastLocation = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> replyTargets = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> ignores = new ConcurrentHashMap<>();
    private final Map<UUID, TeleportRequest> tpaRequests = new ConcurrentHashMap<>();
    private final Set<UUID> vanished = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<UUID> godMode = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<UUID> flying = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public PlayerDataManager(FrameworkPlugin plugin, StorageManager storageManager) {
    }

    public void setLastLocation(Player player, Location location) {
        lastLocation.put(player.getUniqueId(), location);
    }

    public Location getLastLocation(Player player) {
        return lastLocation.get(player.getUniqueId());
    }

    public void setReplyTarget(Player sender, Player target) {
        replyTargets.put(sender.getUniqueId(), target.getUniqueId());
        replyTargets.put(target.getUniqueId(), sender.getUniqueId());
    }

    public UUID getReplyTarget(Player sender) {
        return replyTargets.get(sender.getUniqueId());
    }

    public void toggleIgnore(Player player, Player target) {
        ignores.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        if (!ignores.get(player.getUniqueId()).add(target.getUniqueId())) {
            ignores.get(player.getUniqueId()).remove(target.getUniqueId());
        }
    }

    public boolean isIgnoring(Player player, Player target) {
        return ignores.getOrDefault(player.getUniqueId(), Collections.emptySet()).contains(target.getUniqueId());
    }

    public void addTpaRequest(Player from, Player to, boolean teleportHere) {
        tpaRequests.put(to.getUniqueId(), new TeleportRequest(from.getUniqueId(), to.getUniqueId(), System.currentTimeMillis(), teleportHere));
    }

    public TeleportRequest getTpaRequest(Player to) {
        return tpaRequests.get(to.getUniqueId());
    }

    public void clearTpaRequest(Player to) {
        tpaRequests.remove(to.getUniqueId());
    }

    public void setVanished(Player player, boolean state) {
        if (state) {
            vanished.add(player.getUniqueId());
        } else {
            vanished.remove(player.getUniqueId());
        }
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public void setGodMode(Player player, boolean state) {
        if (state) godMode.add(player.getUniqueId()); else godMode.remove(player.getUniqueId());
    }

    public boolean isGod(Player player) {
        return godMode.contains(player.getUniqueId());
    }

    public void setFlying(Player player, boolean state) {
        if (state) flying.add(player.getUniqueId()); else flying.remove(player.getUniqueId());
    }

    public boolean isFlying(Player player) {
        return flying.contains(player.getUniqueId());
    }

    public String getPlaytime(OfflinePlayer player) {
        long ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long seconds = ticks / 20;
        return TimeUtil.formatDuration(seconds * 1000);
    }

    public void saveAll() {
        // placeholder for future persistence of runtime data
    }
}
