package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class GiveCommand extends BaseCommand {
    public GiveCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.give")) {
            return true;
        }

        if (args.length < 2) {
            messages.sendMessage(sender, "give-usage");
            return true;
        }

        org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            messages.sendMessage(sender, "player-not-found");
            return true;
        }

        org.bukkit.Material material = org.bukkit.Material.matchMaterial(args[1]);
        if (material == null) {
            messages.sendMessage(sender, "give-invalid-item");
            return true;
        }

        int amount = 1;
        if (args.length > 2) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                messages.sendMessage(sender, "give-invalid-amount");
                return true;
            }
        }

        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(material, amount);
        java.util.Map<Integer, org.bukkit.inventory.ItemStack> leftovers = target.getInventory().addItem(item);
        leftovers.values().forEach(remaining -> target.getWorld().dropItemNaturally(target.getLocation(), remaining));

        String itemName = material.toString().toLowerCase();
        sender.sendMessage(messages.getMessage("give-success")
                .replace("%player%", target.getName())
                .replace("%item%", itemName)
                .replace("%amount%", String.valueOf(amount)));

        if (!target.equals(sender)) {
            target.sendMessage(messages.getMessage("give-received")
                    .replace("%player%", sender.getName())
                    .replace("%item%", itemName)
                    .replace("%amount%", String.valueOf(amount)));
        }
        return true;
    }
}
