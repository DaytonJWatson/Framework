package com.daytonjwatson.framework.listeners;

import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RulerListener implements Listener {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private final MessageHandler messages;
    private final Map<UUID, Location> firstPoints = new HashMap<>();

    public RulerListener(MessageHandler messages) {
        this.messages = messages;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRulerUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.WOODEN_SHOVEL) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Location clicked = event.getClickedBlock().getLocation();

        if (action == Action.LEFT_CLICK_BLOCK) {
            firstPoints.put(uuid, clicked);
            player.sendMessage(messages.getMessage("ruler-first-point")
                    .replace("%x%", String.valueOf(clicked.getBlockX()))
                    .replace("%y%", String.valueOf(clicked.getBlockY()))
                    .replace("%z%", String.valueOf(clicked.getBlockZ())));
            return;
        }

        Location first = firstPoints.get(uuid);
        if (first == null) {
            messages.sendMessage(player, "ruler-no-first");
            return;
        }

        if (!first.getWorld().equals(clicked.getWorld())) {
            messages.sendMessage(player, "ruler-world-mismatch");
            return;
        }

        Location firstCenter = center(first);
        Location secondCenter = center(clicked);

        double distance = firstCenter.distance(secondCenter);

        double centerX = (firstCenter.getX() + secondCenter.getX()) / 2.0d;
        double centerY = (firstCenter.getY() + secondCenter.getY()) / 2.0d;
        double centerZ = (firstCenter.getZ() + secondCenter.getZ()) / 2.0d;

        player.sendMessage(messages.getMessage("ruler-measurement")
                .replace("%distance%", DECIMAL_FORMAT.format(distance))
                .replace("%centerx%", DECIMAL_FORMAT.format(centerX))
                .replace("%centery%", DECIMAL_FORMAT.format(centerY))
                .replace("%centerz%", DECIMAL_FORMAT.format(centerZ)));
    }

    private Location center(Location location) {
        return new Location(
                location.getWorld(),
                location.getBlockX() + 0.5d,
                location.getBlockY() + 0.5d,
                location.getBlockZ() + 0.5d
        );
    }
}
