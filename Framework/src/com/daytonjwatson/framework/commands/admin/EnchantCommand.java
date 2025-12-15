package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EnchantCommand extends BaseCommand {
    public EnchantCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePermission(sender, "framework.enchant")) {
            return true;
        }
        if (!requirePlayer(sender)) {
            return true;
        }

        if (args.length < 1) {
            messages.sendMessage(sender, "enchant-usage");
            return true;
        }

        org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
        org.bukkit.inventory.ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            messages.sendMessage(player, "enchant-no-item");
            return true;
        }

        org.bukkit.enchantments.Enchantment enchantment = org.bukkit.enchantments.Enchantment.getByName(args[0].toUpperCase());
        if (enchantment == null) {
            enchantment = org.bukkit.enchantments.Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(args[0].toLowerCase()));
        }
        if (enchantment == null) {
            messages.sendMessage(player, "enchant-invalid");
            return true;
        }

        int level = 1;
        if (args.length > 1) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                messages.sendMessage(player, "enchant-invalid-level");
                return true;
            }
            if (level <= 0) {
                messages.sendMessage(player, "enchant-invalid-level");
                return true;
            }
        }

        item.addUnsafeEnchantment(enchantment, level);
        player.sendMessage(messages.getMessage("enchant-success")
                .replace("%enchantment%", enchantment.getKey().getKey())
                .replace("%level%", String.valueOf(level)));
        return true;
    }
}
