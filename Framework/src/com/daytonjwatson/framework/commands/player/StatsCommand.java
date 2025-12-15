package com.daytonjwatson.framework.commands.player;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.commands.BaseCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class StatsCommand extends BaseCommand {
    public StatsCommand(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        super(plugin, api, storage, playerData, messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messages.sendMessage(sender, "stats-usage");
            return true;
        }

        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        boolean share = removeShareFlag(arguments);

        ParseResult parseResult = parseArguments(sender, arguments, true);
        if (!parseResult.success()) {
            return true;
        }

        Player target = parseResult.target();
        Statistic statistic = parseResult.statistic();
        String context = parseResult.context();

        int value = getStatisticValue(sender, target, statistic, parseResult.subValue());
        if (value == Integer.MIN_VALUE) {
            return true;
        }

        String statName = formatStatisticName(statistic, context);
        String message = messages.getMessage("stats-format")
                .replace("%player%", target.getName())
                .replace("%statistic%", statName)
                .replace("%value%", String.valueOf(value));

        if (share) {
            Bukkit.broadcastMessage(messages.getMessage("stats-share-format")
                    .replace("%player%", sender.getName())
                    .replace("%target%", target.getName())
                    .replace("%statistic%", statName)
                    .replace("%value%", String.valueOf(value)));
        } else {
            sender.sendMessage(message);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();
        String lastArg = args[args.length - 1];

        if (args.length == 1) {
            completions.addAll(collectPlayerNames(lastArg));
            completions.addAll(collectStatisticNames(lastArg));
            completions.addAll(collectShareFlag(lastArg));
            return completions;
        }

        if ("share".regionMatches(true, 0, lastArg, 0, lastArg.length())) {
            completions.addAll(collectShareFlag(lastArg));
            return completions;
        }

        ParseResult parseResult = parseArguments(sender, Arrays.asList(args).subList(0, args.length - 1), false);
        Statistic statistic = parseResult.statistic();
        if (statistic == null) {
            completions.addAll(collectPlayerNames(lastArg));
            completions.addAll(collectStatisticNames(lastArg));
            return completions;
        }

        switch (statistic.getType()) {
            case ENTITY -> completions.addAll(collectEntityTypes(lastArg));
            case BLOCK, ITEM -> completions.addAll(collectMaterialTypes(lastArg));
            default -> completions.addAll(collectPlayerNames(lastArg));
        }

        return completions;
    }

    private boolean removeShareFlag(List<String> arguments) {
        if (arguments.isEmpty()) {
            return false;
        }
        String last = arguments.get(arguments.size() - 1);
        if (last.equalsIgnoreCase("share")) {
            arguments.remove(arguments.size() - 1);
            return true;
        }
        return false;
    }

    private ParseResult parseArguments(CommandSender sender, List<String> arguments, boolean notify) {
        if (arguments.isEmpty()) {
            if (notify) {
                messages.sendMessage(sender, "stats-usage");
            }
            return ParseResult.failed();
        }

        String first = arguments.get(0);
        Player potentialPlayer = Bukkit.getPlayer(first);
        Player target;
        String statisticName;
        String subValue = null;

        if (potentialPlayer != null && arguments.size() >= 2) {
            target = potentialPlayer;
            statisticName = arguments.get(1);
            if (arguments.size() >= 3) {
                subValue = arguments.get(2);
            }
        } else {
            target = sender instanceof Player player ? player : null;
            statisticName = first;
            if (arguments.size() >= 2) {
                subValue = arguments.get(1);
            }
        }

        if (target == null) {
            if (notify) {
                messages.sendMessage(sender, "only-players");
            }
            return ParseResult.failed();
        }

        Statistic statistic = parseStatistic(statisticName);
        if (statistic == null) {
            if (notify) {
                messages.sendMessage(sender, "stats-invalid-statistic");
            }
            return ParseResult.failed();
        }

        return new ParseResult(target, statistic, subValue);
    }

    private Statistic parseStatistic(String name) {
        try {
            return Statistic.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private int getStatisticValue(CommandSender sender, Player target, Statistic statistic, String subValue) {
        try {
            Type type = statistic.getType();
            return switch (type) {
                case UNTYPED -> target.getStatistic(statistic);
                case ENTITY -> target.getStatistic(statistic, parseEntityType(sender, subValue));
                case BLOCK, ITEM -> target.getStatistic(statistic, parseMaterial(sender, subValue));
            };
        } catch (IllegalArgumentException ex) {
            messages.sendMessage(sender, "stats-invalid-context");
            return Integer.MIN_VALUE;
        }
    }

    private EntityType parseEntityType(CommandSender sender, String name) {
        if (name == null) {
            messages.sendMessage(sender, "stats-context-required");
            throw new IllegalArgumentException("Missing entity context");
        }
        EntityType type = EntityType.fromName(name.toUpperCase(Locale.ROOT));
        if (type == null) {
            messages.sendMessage(sender, "stats-invalid-entity");
            throw new IllegalArgumentException("Invalid entity type");
        }
        return type;
    }

    private Material parseMaterial(CommandSender sender, String name) {
        if (name == null) {
            messages.sendMessage(sender, "stats-context-required");
            throw new IllegalArgumentException("Missing material context");
        }
        Material material = Material.matchMaterial(name);
        if (material == null) {
            messages.sendMessage(sender, "stats-invalid-material");
            throw new IllegalArgumentException("Invalid material type");
        }
        return material;
    }

    private String formatStatisticName(Statistic statistic, String context) {
        String base = statistic.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        if (context == null || context.isEmpty()) {
            return base;
        }
        return base + " (" + context + ")";
    }

    private List<String> collectPlayerNames(String prefix) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    private List<String> collectStatisticNames(String prefix) {
        return Arrays.stream(Statistic.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .filter(name -> name.startsWith(prefix.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    private List<String> collectMaterialTypes(String prefix) {
        return Arrays.stream(Material.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .filter(name -> name.startsWith(prefix.toLowerCase(Locale.ROOT)))
                .limit(30)
                .collect(Collectors.toList());
    }

    private List<String> collectEntityTypes(String prefix) {
        return Arrays.stream(EntityType.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .filter(name -> name.startsWith(prefix.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    private List<String> collectShareFlag(String prefix) {
        if ("share".startsWith(prefix.toLowerCase(Locale.ROOT))) {
            return Collections.singletonList("share");
        }
        return Collections.emptyList();
    }

    private record ParseResult(Player target, Statistic statistic, String subValue) {
        ParseResult(Player target, Statistic statistic, String subValue) {
            this.target = target;
            this.statistic = statistic;
            this.subValue = subValue;
        }

        boolean success() {
            return target != null && statistic != null;
        }

        String context() {
            return subValue;
        }

        static ParseResult failed() {
            return new ParseResult(null, null, null);
        }
    }
}
