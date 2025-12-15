package com.framework.data;

import com.framework.FrameworkPlugin;
import com.framework.util.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class PlayerDataManager {
    private final FrameworkPlugin plugin;
    private final Map<UUID, Instant> lastSeen = new HashMap<>();
    private final Map<UUID, UUID> lastMessaged = new HashMap<>();
    private final Map<UUID, Set<UUID>> ignored = new HashMap<>();
    private final Map<UUID, Location> back = new HashMap<>();
    private final Map<UUID, Location> pendingTeleports = new HashMap<>();
    private final Map<UUID, UUID> teleportRequests = new HashMap<>();
    private final Map<UUID, Boolean> afk = new HashMap<>();
    private final Map<UUID, Instant> muteExpiry = new HashMap<>();
    private final Map<UUID, List<String>> warnings = new HashMap<>();
    private final Map<UUID, String> nicknames = new HashMap<>();
    private final Map<UUID, Instant> playtimeStart = new HashMap<>();
    private final Map<UUID, Duration> playtime = new HashMap<>();
    private final Map<UUID, Boolean> vanish = new HashMap<>();
    private final Map<UUID, Boolean> god = new HashMap<>();

    public PlayerDataManager(FrameworkPlugin plugin) {
        this.plugin = plugin;
    }

    public void recordJoin(PlayerJoinEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        lastSeen.put(id, Instant.now());
        playtimeStart.put(id, Instant.now());
    }

    public void recordQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        lastSeen.put(id, Instant.now());
        accumulatePlaytime(id);
    }

    public Instant getLastSeen(UUID id) {return lastSeen.get(id);}    

    public void setLastMessaged(UUID sender, UUID target) {lastMessaged.put(sender, target);}    

    public UUID getLastMessaged(UUID player) {return lastMessaged.get(player);}    

    public boolean isIgnored(UUID owner, UUID target) {return ignored.getOrDefault(owner, Collections.emptySet()).contains(target);}    

    public void toggleIgnore(UUID owner, UUID target) {
        ignored.computeIfAbsent(owner, u -> new HashSet<>());
        if (!ignored.get(owner).add(target)) ignored.get(owner).remove(target);
    }

    public void recordBack(Player player) {back.put(player.getUniqueId(), player.getLocation());}

    public Location getBackLocation(Player player) {return back.get(player.getUniqueId());}

    public void requestTeleport(Player from, Player to) {teleportRequests.put(to.getUniqueId(), from.getUniqueId());}

    public Player consumeTeleportRequest(Player target) {
        UUID requesterId = teleportRequests.remove(target.getUniqueId());
        return requesterId == null ? null : Bukkit.getPlayer(requesterId);
    }

    public boolean isAfk(UUID player) {return afk.getOrDefault(player, false);}    

    public void setAfk(UUID player, boolean value) {afk.put(player, value);}    

    public void setMute(UUID player, boolean value, Instant until) { if (value && until != null) muteExpiry.put(player, until); else if (!value) muteExpiry.remove(player);}    

    public boolean isMuted(UUID player) {Instant until = muteExpiry.get(player); return until != null && Instant.now().isBefore(until);}    

    public void addWarning(UUID player, String reason, String issuer) {warnings.computeIfAbsent(player, u -> new ArrayList<>()).add(reason + " by " + issuer);}    

    public List<String> getWarnings(UUID player) {return warnings.getOrDefault(player, Collections.emptyList());}

    public void setNickname(UUID player, String nick) {nicknames.put(player, nick);}    

    public String getRealName(UUID player, String fallback) {return nicknames.getOrDefault(player, fallback);}    

    public void setLastLocation(UUID player, Location location) {back.put(player, location);}    

    public Duration getPlaytime(UUID player) {accumulatePlaytime(player); return playtime.getOrDefault(player, Duration.ZERO);}    

    private void accumulatePlaytime(UUID id) {Instant start = playtimeStart.remove(id); if (start != null) playtime.merge(id, Duration.between(start, Instant.now()), Duration::plus);}    

    public void setVanished(UUID id, boolean value) {vanish.put(id, value);}    

    public boolean isVanished(UUID id) {return vanish.getOrDefault(id, false);}    

    public void setGod(UUID id, boolean value) {god.put(id, value);}    

    public boolean isGod(UUID id) {return god.getOrDefault(id, false);}    

    public void save() { /* placeholder for future persistence */ }
}
