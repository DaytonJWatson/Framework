package com.daytonjwatson.framework.commands.admin;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NicknameCommand extends BaseCommand {
    public NicknameCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && !(sender instanceof Player)) {
            messages.sendMessage(sender, "nickname-usage");
            return true;
        }

        Player target;
        String nickname;

        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                messages.sendMessage(sender, "player-not-found");
                return true;
            }
            nickname = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        } else {
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, "nickname-usage");
                return true;
            }
            target = player;
            nickname = args.length == 1 ? args[0] : target.getName();
        }

        if (nickname.equalsIgnoreCase("reset")) {
            target.setDisplayName(target.getName());
            target.setPlayerListName(target.getName());
            messages.sendMessage(sender, target.equals(sender) ? "nickname-reset" : "nickname-reset-other", "player", target.getName());
            if (!target.equals(sender)) {
                messages.sendMessage(target, "nickname-reset-notify", "player", sender.getName());
            }
            return true;
        }

        String colored = ChatColor.translateAlternateColorCodes('&', nickname);
        String displayName = colored + ChatColor.RESET;
        target.setDisplayName(displayName);
        target.setPlayerListName(colored);

        messages.sendMessage(sender, target.equals(sender) ? "nickname-set" : "nickname-set-other", "player", target.getName());
        if (!target.equals(sender)) {
            messages.sendMessage(target, "nickname-set-notify", "player", sender.getName());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("reset");
            suggestions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList()));
            return suggestions;
        }
        if (args.length == 2) {
            return List.of("&aNickname", "&bwith&ccolors");
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
