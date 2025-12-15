package com.framework.command;

import com.framework.FrameworkPlugin;
import com.framework.data.HomeManager;
import com.framework.data.PlayerDataManager;
import com.framework.data.WarpManager;
import com.framework.util.LocationSerializer;
import com.framework.util.MessageService;
import org.bukkit.*;
import org.bukkit.ban.BanList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class CommandRouter implements CommandExecutor, TabCompleter {
    private final FrameworkPlugin plugin;
    private final MessageService messages;
    private final WarpManager warpManager;
    private final HomeManager homeManager;
    private final PlayerDataManager playerData;
    private final Map<String, FrameworkCommand> commandMap = new HashMap<>();

    public CommandRouter(FrameworkPlugin plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
        this.warpManager = plugin.getWarpManager();
        this.homeManager = plugin.getHomeManager();
        this.playerData = plugin.getPlayerDataManager();
        register();
    }

    private void register() {
        commandMap.put("help", new SimpleMessageCommand("framework.help", "/help [command]", sender -> messages.sendMessage(sender, "help")));
        commandMap.put("list", new ListCommand());
        commandMap.put("motd", new SimpleMessageCommand("framework.motd", "/motd", sender -> messages.sendMessage(sender, "motd")));
        commandMap.put("rules", new SimpleMessageCommand("framework.rules", "/rules", sender -> messages.sendMessage(sender, "rules")));
        commandMap.put("spawn", new SpawnCommand());
        commandMap.put("setspawn", new SetSpawnCommand());
        commandMap.put("warp", new WarpCommand());
        commandMap.put("setwarp", new SetWarpCommand());
        commandMap.put("warps", new WarpsCommand());
        commandMap.put("home", new HomeCommand());
        commandMap.put("sethome", new SetHomeCommand());
        commandMap.put("delhome", new DelHomeCommand());
        commandMap.put("homes", new HomesCommand());
        commandMap.put("tpa", new TpaCommand());
        commandMap.put("tpaccept", new TpAcceptCommand());
        commandMap.put("tpdeny", new TpDenyCommand());
        commandMap.put("back", new BackCommand());
        commandMap.put("suicide", new SuicideCommand());
        commandMap.put("afk", new AfkCommand());
        commandMap.put("msg", new MsgCommand());
        commandMap.put("reply", new ReplyCommand());
        commandMap.put("ignore", new IgnoreCommand());
        commandMap.put("seen", new SeenCommand());
        commandMap.put("nickname", new NickCommand());
        commandMap.put("realname", new RealNameCommand());
        commandMap.put("rtp", new RandomTeleportCommand());
        commandMap.put("trash", new TrashCommand());
        commandMap.put("enderchest", new EnderChestCommand());
        commandMap.put("workbench", new WorkbenchCommand());
        commandMap.put("stats", new StatsCommand());
        commandMap.put("playtime", new PlaytimeCommand());
        commandMap.put("ban", new BanCommand());
        commandMap.put("tempban", new TempBanCommand());
        commandMap.put("unban", new UnbanCommand());
        commandMap.put("mute", new MuteCommand());
        commandMap.put("tempmute", new TempMuteCommand());
        commandMap.put("kick", new KickCommand());
        commandMap.put("warn", new WarnCommand());
        commandMap.put("warnings", new WarningsCommand());
        commandMap.put("time", new TimeCommand());
        commandMap.put("weather", new WeatherCommand());
        commandMap.put("gamemode", new GamemodeCommand());
        commandMap.put("tp", new TeleportCommand());
        commandMap.put("tpall", new TpAllCommand());
        commandMap.put("fly", new FlyCommand());
        commandMap.put("god", new GodCommand());
        commandMap.put("heal", new HealCommand());
        commandMap.put("feed", new FeedCommand());
        commandMap.put("vanish", new VanishCommand());
        commandMap.put("invsee", new InvSeeCommand());
        commandMap.put("endersee", new EnderSeeCommand());
        commandMap.put("clear", new ClearCommand());
        commandMap.put("give", new GiveCommand());
        commandMap.put("enchant", new EnchantCommand());
        commandMap.put("broadcast", new BroadcastCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FrameworkCommand handler = commandMap.get(command.getName().toLowerCase(Locale.ROOT));
        if (handler == null) {
            return false;
        }
        String perm = handler.getPermission();
        if (perm != null && !perm.isEmpty() && !sender.hasPermission(perm)) {
            messages.sendMessage(sender, "no-permission");
            return true;
        }
        boolean result = handler.execute(sender, label, args);
        if (!result) {
            String usage = handler.getUsage();
            if (usage != null && !usage.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Usage: " + usage);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        FrameworkCommand handler = commandMap.get(command.getName().toLowerCase(Locale.ROOT));
        if (handler == null) {
            return Collections.emptyList();
        }
        return handler.tabComplete(sender, args);
    }

    private abstract class PlayerCommand implements FrameworkCommand {
        private final String permission;
        private final String usage;

        PlayerCommand(String permission, String usage) {
            this.permission = permission;
            this.usage = usage;
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player)) {
                messages.sendMessage(sender, "players-only");
                return true;
            }
            return run((Player) sender, args);
        }

        public abstract boolean run(Player player, String[] args);

        @Override
        public String getPermission() {
            return permission;
        }

        @Override
        public String getUsage() {
            return usage;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            return Collections.emptyList();
        }
    }

    private class SimpleMessageCommand implements FrameworkCommand {
        private final String permission;
        private final String usage;
        private final java.util.function.Consumer<CommandSender> action;

        SimpleMessageCommand(String permission, String usage, java.util.function.Consumer<CommandSender> action) {
            this.permission = permission;
            this.usage = usage;
            this.action = action;
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            action.accept(sender);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            return Collections.emptyList();
        }

        @Override
        public String getPermission() {
            return permission;
        }

        @Override
        public String getUsage() {
            return usage;
        }
    }

    private class ListCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            String players = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.joining(", "));
            messages.sendMessage(sender, "list", "%online%", String.valueOf(Bukkit.getOnlinePlayers().size()), "%max%", String.valueOf(Bukkit.getMaxPlayers()), "%players%", players);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Collections.emptyList();}

        @Override
        public String getPermission() {return "framework.list";}

        @Override
        public String getUsage() {return "/list";}
    }

    private class SpawnCommand extends PlayerCommand {
        SpawnCommand() {super("framework.spawn", "/spawn");}

        @Override
        public boolean run(Player player, String[] args) {
            Location spawn = player.getWorld().getSpawnLocation();
            playerData.recordBack(player);
            player.teleport(spawn);
            messages.sendMessage(player, "spawn");
            return true;
        }
    }

    private class SetSpawnCommand extends PlayerCommand {
        SetSpawnCommand() {super("framework.setspawn", "/setspawn");}

        @Override
        public boolean run(Player player, String[] args) {
            player.getWorld().setSpawnLocation(player.getLocation());
            messages.sendMessage(player, "setspawn");
            return true;
        }
    }

    private class WarpCommand extends PlayerCommand {
        WarpCommand() {super("framework.warp", "/warp <name>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 1) return false;
            Location loc = warpManager.getWarp(args[0]);
            if (loc == null) {
                messages.sendMessage(player, "warp-missing", "%name%", args[0]);
                return true;
            }
            playerData.recordBack(player);
            player.teleport(loc);
            messages.sendMessage(player, "warp", "%name%", args[0]);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) return new ArrayList<>(warpManager.getAllWarps());
            return Collections.emptyList();
        }
    }

    private class SetWarpCommand extends PlayerCommand {
        SetWarpCommand() {super("framework.setwarp", "/setwarp <name>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 1) return false;
            warpManager.setWarp(args[0], player.getLocation());
            messages.sendMessage(player, "setwarp", "%name%", args[0]);
            return true;
        }
    }

    private class WarpsCommand extends PlayerCommand {
        WarpsCommand() {super("framework.warps", "/warps");}

        @Override
        public boolean run(Player player, String[] args) {
            messages.sendMessage(player, "warps", "%list%", String.join(", ", warpManager.getAllWarps()));
            return true;
        }
    }

    private class HomeCommand extends PlayerCommand {
        HomeCommand() {super("framework.home", "/home [name]");}

        @Override
        public boolean run(Player player, String[] args) {
            String home = args.length > 0 ? args[0] : "home";
            Location loc = homeManager.getHome(player.getUniqueId(), home);
            if (loc == null) {
                messages.sendMessage(player, "home-missing", "%name%", home);
                return true;
            }
            playerData.recordBack(player);
            player.teleport(loc);
            messages.sendMessage(player, "home", "%name%", home);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (sender instanceof Player && args.length == 1) {
                return new ArrayList<>(homeManager.getHomes(((Player) sender).getUniqueId()));
            }
            return Collections.emptyList();
        }
    }

    private class SetHomeCommand extends PlayerCommand {
        SetHomeCommand() {super("framework.sethome", "/sethome [name]");}

        @Override
        public boolean run(Player player, String[] args) {
            String name = args.length > 0 ? args[0] : "home";
            int maxHomes = plugin.getConfig().getInt("limits.homes", 3);
            if (!homeManager.hasHome(player.getUniqueId(), name) && homeManager.getHomes(player.getUniqueId()).size() >= maxHomes) {
                messages.sendMessage(player, "home-limit", "%limit%", String.valueOf(maxHomes));
                return true;
            }
            homeManager.setHome(player.getUniqueId(), name, player.getLocation());
            messages.sendMessage(player, "sethome", "%name%", name);
            return true;
        }
    }

    private class DelHomeCommand extends PlayerCommand {
        DelHomeCommand() {super("framework.delhome", "/delhome <name>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 1) return false;
            if (!homeManager.hasHome(player.getUniqueId(), args[0])) {
                messages.sendMessage(player, "home-missing", "%name%", args[0]);
                return true;
            }
            homeManager.deleteHome(player.getUniqueId(), args[0]);
            messages.sendMessage(player, "delhome", "%name%", args[0]);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (sender instanceof Player && args.length == 1) {
                return new ArrayList<>(homeManager.getHomes(((Player) sender).getUniqueId()));
            }
            return Collections.emptyList();
        }
    }

    private class HomesCommand extends PlayerCommand {
        HomesCommand() {super("framework.homes", "/homes");}

        @Override
        public boolean run(Player player, String[] args) {
            messages.sendMessage(player, "homes", "%list%", String.join(", ", homeManager.getHomes(player.getUniqueId())));
            return true;
        }
    }

    private class TpaCommand extends PlayerCommand {
        TpaCommand() {super("framework.tpa", "/tpa <player>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 1) return false;
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null || !target.isOnline()) {
                messages.sendMessage(player, "player-not-found");
                return true;
            }
            playerData.requestTeleport(player, target);
            messages.sendMessage(player, "tpa-sent", "%target%", target.getName());
            messages.sendMessage(target, "tpa-received", "%player%", player.getName());
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }

    private class TpAcceptCommand extends PlayerCommand {
        TpAcceptCommand() {super("framework.tpa", "/tpaccept");}

        @Override
        public boolean run(Player player, String[] args) {
            Player requester = playerData.consumeTeleportRequest(player);
            if (requester == null) {
                messages.sendMessage(player, "tpa-none");
                return true;
            }
            playerData.recordBack(requester);
            requester.teleport(player.getLocation());
            messages.sendMessage(player, "tpa-accepted", "%player%", requester.getName());
            messages.sendMessage(requester, "tpa-accepted", "%player%", player.getName());
            return true;
        }
    }

    private class TpDenyCommand extends PlayerCommand {
        TpDenyCommand() {super("framework.tpa", "/tpdeny");}

        @Override
        public boolean run(Player player, String[] args) {
            Player requester = playerData.consumeTeleportRequest(player);
            if (requester == null) {
                messages.sendMessage(player, "tpa-none");
                return true;
            }
            messages.sendMessage(player, "tpa-denied", "%player%", requester.getName());
            messages.sendMessage(requester, "tpa-denied", "%player%", player.getName());
            return true;
        }
    }

    private class BackCommand extends PlayerCommand {
        BackCommand() {super("framework.back", "/back");}

        @Override
        public boolean run(Player player, String[] args) {
            Location back = playerData.getBackLocation(player);
            if (back == null) {
                messages.sendMessage(player, "back-missing");
                return true;
            }
            player.teleport(back);
            messages.sendMessage(player, "back");
            return true;
        }
    }

    private class SuicideCommand extends PlayerCommand {
        SuicideCommand() {super("framework.suicide", "/suicide");}

        @Override
        public boolean run(Player player, String[] args) {
            player.setHealth(0);
            messages.sendMessage(player, "suicide");
            return true;
        }
    }

    private class AfkCommand extends PlayerCommand {
        AfkCommand() {super("framework.afk", "/afk");}

        @Override
        public boolean run(Player player, String[] args) {
            boolean now = !playerData.isAfk(player.getUniqueId());
            playerData.setAfk(player.getUniqueId(), now);
            messages.sendMessage(player, now ? "afk-enable" : "afk-disable");
            return true;
        }
    }

    private class MsgCommand extends PlayerCommand {
        MsgCommand() {super("framework.msg", "/msg <player> <message>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 2) return false;
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null || !target.isOnline()) {
                messages.sendMessage(player, "player-not-found");
                return true;
            }
            if (playerData.isIgnored(target.getUniqueId(), player.getUniqueId())) {
                messages.sendMessage(player, "ignored");
                return true;
            }
            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            messages.sendMessage(player, "msg-sent", "%player%", target.getName(), "%message%", message);
            messages.sendMessage(target, "msg-received", "%player%", player.getName(), "%message%", message);
            playerData.setLastMessaged(player.getUniqueId(), target.getUniqueId());
            playerData.setLastMessaged(target.getUniqueId(), player.getUniqueId());
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }

    private class ReplyCommand extends PlayerCommand {
        ReplyCommand() {super("framework.msg", "/reply <message>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 1) return false;
            UUID last = playerData.getLastMessaged(player.getUniqueId());
            if (last == null) {
                messages.sendMessage(player, "reply-none");
                return true;
            }
            Player target = Bukkit.getPlayer(last);
            if (target == null) {
                messages.sendMessage(player, "player-not-found");
                return true;
            }
            String message = String.join(" ", args);
            messages.sendMessage(player, "msg-sent", "%player%", target.getName(), "%message%", message);
            messages.sendMessage(target, "msg-received", "%player%", player.getName(), "%message%", message);
            return true;
        }
    }

    private class IgnoreCommand extends PlayerCommand {
        IgnoreCommand() {super("framework.ignore", "/ignore <player>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 1) return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            playerData.toggleIgnore(player.getUniqueId(), target.getUniqueId());
            boolean ignored = playerData.isIgnored(player.getUniqueId(), target.getUniqueId());
            messages.sendMessage(player, ignored ? "ignore-add" : "ignore-remove", "%player%", target.getName());
            return true;
        }
    }

    private class SeenCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length < 1) return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            Instant last = playerData.getLastSeen(target.getUniqueId());
            if (last == null) {
                messages.sendMessage(sender, "never-seen", "%player%", target.getName());
            } else {
                Duration duration = Duration.between(last, Instant.now());
                messages.sendMessage(sender, "seen", "%player%", target.getName(), "%time%", formatDuration(duration));
            }
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Collections.emptyList();}

        @Override
        public String getPermission() {return "framework.seen";}

        @Override
        public String getUsage() {return "/seen <player>";}
    }

    private class NickCommand extends PlayerCommand {
        NickCommand() {super("framework.nickname", "/nickname <name>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 1) return false;
            playerData.setNickname(player.getUniqueId(), args[0]);
            player.setDisplayName(args[0]);
            messages.sendMessage(player, "nickname", "%name%", args[0]);
            return true;
        }
    }

    private class RealNameCommand extends FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length < 1) return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            String real = playerData.getRealName(target.getUniqueId(), target.getName());
            messages.sendMessage(sender, "realname", "%name%", real);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Collections.emptyList();}

        @Override
        public String getPermission() {return "framework.nickname";}

        @Override
        public String getUsage() {return "/realname <player>";}
    }

    private class RandomTeleportCommand extends PlayerCommand {
        RandomTeleportCommand() {super("framework.rtp", "/rtp");}

        @Override
        public boolean run(Player player, String[] args) {
            int radius = plugin.getConfig().getInt("teleport.random-radius", 5000);
            World world = player.getWorld();
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                int x = random.nextInt(radius * 2) - radius;
                int z = random.nextInt(radius * 2) - radius;
                int y = world.getHighestBlockYAt(x, z) + 1;
                Location loc = new Location(world, x + 0.5, y, z + 0.5);
                playerData.recordBack(player);
                player.teleport(loc);
                messages.sendMessage(player, "rtp", "%x%", String.valueOf(x), "%z%", String.valueOf(z));
                return true;
            }
            messages.sendMessage(player, "rtp-failed");
            return true;
        }
    }

    private class TrashCommand extends PlayerCommand {
        TrashCommand() {super("framework.trash", "/trash");}

        @Override
        public boolean run(Player player, String[] args) {
            Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Trash");
            player.openInventory(inv);
            messages.sendMessage(player, "trash");
            return true;
        }
    }

    private class EnderChestCommand extends PlayerCommand {
        EnderChestCommand() {super("framework.enderchest", "/enderchest [player]");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length == 0) {
                player.openInventory(player.getEnderChest());
                messages.sendMessage(player, "enderchest");
            } else if (player.hasPermission("framework.enderchest.others")) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {messages.sendMessage(player, "player-not-found"); return true;}
                player.openInventory(target.getEnderChest());
                messages.sendMessage(player, "enderchest-other", "%player%", target.getName());
            } else {
                messages.sendMessage(player, "no-permission");
            }
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1 && sender.hasPermission("framework.enderchest.others")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }

    private class WorkbenchCommand extends PlayerCommand {
        WorkbenchCommand() {super("framework.workbench", "/workbench");}

        @Override
        public boolean run(Player player, String[] args) {
            player.openWorkbench(null, true);
            messages.sendMessage(player, "workbench");
            return true;
        }
    }

    private class StatsCommand extends PlayerCommand {
        StatsCommand() {super("framework.stats", "/stats");}

        @Override
        public boolean run(Player player, String[] args) {
            messages.sendMessage(player, "stats", "%kills%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS)), "%deaths%", String.valueOf(player.getStatistic(Statistic.DEATHS)));
            return true;
        }
    }

    private class PlaytimeCommand extends PlayerCommand {
        PlaytimeCommand() {super("framework.playtime", "/playtime");}

        @Override
        public boolean run(Player player, String[] args) {
            Duration playtime = playerData.getPlaytime(player.getUniqueId());
            messages.sendMessage(player, "playtime", "%time%", formatDuration(playtime));
            return true;
        }
    }

    private class BanCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length < 2) return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), reason, null, sender.getName());
            messages.sendMessage(sender, "ban", "%player%", target.getName(), "%reason%", reason);
            if (target.isOnline()) ((Player) target).kickPlayer(reason);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Collections.emptyList();}

        @Override
        public String getPermission() {return "framework.ban";}

        @Override
        public String getUsage() {return "/ban <player> <reason>";}
    }

    private class TempBanCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length < 3) return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            long seconds = parseDuration(args[1]);
            String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            Date expires = Date.from(Instant.now().plusSeconds(seconds));
            Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), reason, expires, sender.getName());
            messages.sendMessage(sender, "tempban", "%player%", target.getName(), "%reason%", reason, "%time%", args[1]);
            if (target.isOnline()) ((Player) target).kickPlayer(reason);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Collections.emptyList();}

        @Override
        public String getPermission() {return "framework.ban";}

        @Override
        public String getUsage() {return "/tempban <player> <time> <reason>";}
    }

    private class UnbanCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length < 1) return false;
            Bukkit.getBanList(BanList.Type.NAME).pardon(args[0]);
            messages.sendMessage(sender, "unban", "%player%", args[0]);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Collections.emptyList();}

        @Override
        public String getPermission() {return "framework.ban";}

        @Override
        public String getUsage() {return "/unban <player>";}
    }

    private class MuteCommand extends PlayerCommand {
        MuteCommand() {super("framework.mute", "/mute <player>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 1) return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            playerData.setMute(target.getUniqueId(), true, null);
            messages.sendMessage(player, "mute", "%player%", target.getName());
            return true;
        }
    }

    private class TempMuteCommand extends PlayerCommand {
        TempMuteCommand() {super("framework.mute", "/tempmute <player> <time>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 2) return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            playerData.setMute(target.getUniqueId(), true, Instant.now().plusSeconds(parseDuration(args[1])));
            messages.sendMessage(player, "mute", "%player%", target.getName());
            return true;
        }
    }

    private class KickCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length < 2) return false;
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {messages.sendMessage(sender, "player-not-found"); return true;}
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            target.kickPlayer(reason);
            messages.sendMessage(sender, "kick", "%player%", target.getName(), "%reason%", reason);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Collections.emptyList();}

        @Override
        public String getPermission() {return "framework.kick";}

        @Override
        public String getUsage() {return "/kick <player> <reason>";}
    }

    private class WarnCommand extends PlayerCommand {
        WarnCommand() {super("framework.warn", "/warn <player> <reason>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 2) return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            playerData.addWarning(target.getUniqueId(), reason, player.getName());
            messages.sendMessage(player, "warn", "%player%", target.getName(), "%reason%", reason);
            if (target.isOnline()) messages.sendMessage((Player) target, "warn-received", "%reason%", reason);
            return true;
        }
    }

    private class WarningsCommand extends FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length < 1) return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            List<String> warnings = playerData.getWarnings(target.getUniqueId());
            messages.sendMessage(sender, "warnings", "%player%", target.getName(), "%list%", String.join(" | ", warnings));
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Collections.emptyList();}

        @Override
        public String getPermission() {return "framework.warn";}

        @Override
        public String getUsage() {return "/warnings <player>";}
    }

    private class TimeCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            if (args.length < 1) return false;
            player.getWorld().setTime(parseTime(args[0]));
            messages.sendMessage(sender, "time", "%time%", args[0]);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Arrays.asList("day", "night", "noon", "midnight");}

        @Override
        public String getPermission() {return "framework.time";}

        @Override
        public String getUsage() {return "/time <value>";}
    }

    private class WeatherCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            if (args.length < 1) return false;
            String type = args[0].toLowerCase(Locale.ROOT);
            player.getWorld().setStorm(type.contains("rain"));
            player.getWorld().setThundering(type.contains("thunder"));
            messages.sendMessage(sender, "weather", "%weather%", type);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Arrays.asList("clear", "rain", "thunder");}

        @Override
        public String getPermission() {return "framework.weather";}

        @Override
        public String getUsage() {return "/weather <clear|rain|thunder>";}
    }

    private class GamemodeCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length < 1) return false;
            GameMode mode = parseGamemode(args[0]);
            if (mode == null) return false;
            if (args.length == 1 && sender instanceof Player) {
                ((Player) sender).setGameMode(mode);
                messages.sendMessage(sender, "gamemode", "%mode%", mode.name());
            } else if (args.length == 2) {
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {messages.sendMessage(sender, "player-not-found"); return true;}
                target.setGameMode(mode);
                messages.sendMessage(sender, "gamemode-other", "%player%", target.getName(), "%mode%", mode.name());
            } else return false;
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Arrays.asList("survival", "creative", "adventure", "spectator");}

        @Override
        public String getPermission() {return "framework.gamemode";}

        @Override
        public String getUsage() {return "/gamemode <mode> [player]";}
    }

    private class TeleportCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            if (args.length < 1) return false;
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {messages.sendMessage(sender, "player-not-found"); return true;}
            playerData.recordBack(player);
            player.teleport(target.getLocation());
            messages.sendMessage(sender, "tp", "%player%", target.getName());
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());}

        @Override
        public String getPermission() {return "framework.tp";}

        @Override
        public String getUsage() {return "/tp <player>";}
    }

    private class TpAllCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.equals(player)) continue;
                online.teleport(player.getLocation());
            }
            messages.sendMessage(sender, "tpall");
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Collections.emptyList();}

        @Override
        public String getPermission() {return "framework.tp";}

        @Override
        public String getUsage() {return "/tpall";}
    }

    private class FlyCommand extends PlayerCommand {
        FlyCommand() {super("framework.fly", "/fly");}

        @Override
        public boolean run(Player player, String[] args) {
            boolean enable = !player.getAllowFlight();
            player.setAllowFlight(enable);
            player.setFlying(enable);
            messages.sendMessage(player, enable ? "fly-enable" : "fly-disable");
            return true;
        }
    }

    private class GodCommand extends PlayerCommand {
        GodCommand() {super("framework.god", "/god");}

        @Override
        public boolean run(Player player, String[] args) {
            boolean god = !player.isInvulnerable();
            player.setInvulnerable(god);
            messages.sendMessage(player, god ? "god-enable" : "god-disable");
            return true;
        }
    }

    private class HealCommand extends PlayerCommand {
        HealCommand() {super("framework.heal", "/heal [player]");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length == 0) {
                heal(player);
                messages.sendMessage(player, "heal");
            } else if (player.hasPermission("framework.heal.others")) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {messages.sendMessage(player, "player-not-found"); return true;}
                heal(target);
                messages.sendMessage(player, "heal-other", "%player%", target.getName());
            } else {
                messages.sendMessage(player, "no-permission");
            }
            return true;
        }

        private void heal(Player target) {
            target.setHealth(target.getMaxHealth());
            target.setFoodLevel(20);
            target.setFireTicks(0);
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1 && sender.hasPermission("framework.heal.others")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }

    private class FeedCommand extends HealCommand {
        FeedCommand() {super();}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length == 0) {
                player.setFoodLevel(20);
                messages.sendMessage(player, "feed");
            } else if (player.hasPermission("framework.feed.others")) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {messages.sendMessage(player, "player-not-found"); return true;}
                target.setFoodLevel(20);
                messages.sendMessage(player, "feed-other", "%player%", target.getName());
            } else messages.sendMessage(player, "no-permission");
            return true;
        }

        @Override
        public String getPermission() {return "framework.feed";}

        @Override
        public String getUsage() {return "/feed [player]";}
    }

    private class VanishCommand extends PlayerCommand {
        VanishCommand() {super("framework.vanish", "/vanish");}

        @Override
        public boolean run(Player player, String[] args) {
            boolean vanished = !playerData.isVanished(player.getUniqueId());
            playerData.setVanished(player.getUniqueId(), vanished);
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (vanished) {
                    other.hidePlayer(plugin, player);
                } else {
                    other.showPlayer(plugin, player);
                }
            }
            messages.sendMessage(player, vanished ? "vanish-enable" : "vanish-disable");
            return true;
        }
    }

    private class InvSeeCommand extends PlayerCommand {
        InvSeeCommand() {super("framework.invsee", "/invsee <player>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 1) return false;
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {messages.sendMessage(player, "player-not-found"); return true;}
            player.openInventory(target.getInventory());
            messages.sendMessage(player, "invsee", "%player%", target.getName());
            return true;
        }
    }

    private class EnderSeeCommand extends PlayerCommand {
        EnderSeeCommand() {super("framework.endersee", "/endersee <player>");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length < 1) return false;
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {messages.sendMessage(player, "player-not-found"); return true;}
            player.openInventory(target.getEnderChest());
            messages.sendMessage(player, "endersee", "%player%", target.getName());
            return true;
        }
    }

    private class ClearCommand extends PlayerCommand {
        ClearCommand() {super("framework.clear", "/clear [player]");}

        @Override
        public boolean run(Player player, String[] args) {
            if (args.length == 0) {
                player.getInventory().clear();
                messages.sendMessage(player, "clear");
            } else if (player.hasPermission("framework.clear.others")) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {messages.sendMessage(player, "player-not-found"); return true;}
                target.getInventory().clear();
                messages.sendMessage(player, "clear-other", "%player%", target.getName());
            } else messages.sendMessage(player, "no-permission");
            return true;
        }
    }

    private class GiveCommand extends FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length < 2) return false;
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {messages.sendMessage(sender, "player-not-found"); return true;}
            Material material = Material.matchMaterial(args[1]);
            if (material == null) return false;
            int amount = args.length > 2 ? Integer.parseInt(args[2]) : material.getMaxStackSize();
            target.getInventory().addItem(new ItemStack(material, amount));
            messages.sendMessage(sender, "give", "%player%", target.getName(), "%item%", material.name(), "%amount%", String.valueOf(amount));
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            if (args.length == 2) return Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList());
            return Collections.emptyList();
        }

        @Override
        public String getPermission() {return "framework.give";}

        @Override
        public String getUsage() {return "/give <player> <item> [amount]";}
    }

    private class EnchantCommand extends FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player)) return false;
            if (args.length < 1) return false;
            Player player = (Player) sender;
            Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase(Locale.ROOT));
            if (enchantment == null) return false;
            int level = args.length > 1 ? Integer.parseInt(args[1]) : enchantment.getMaxLevel();
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta) {
                ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, level, true);
            } else {
                item.addUnsafeEnchantment(enchantment, level);
            }
            player.getInventory().setItemInMainHand(item);
            messages.sendMessage(player, "enchant", "%enchant%", enchantment.getName(), "%level%", String.valueOf(level));
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Arrays.stream(Enchantment.values()).map(Enchantment::getName).collect(Collectors.toList());}

        @Override
        public String getPermission() {return "framework.enchant";}

        @Override
        public String getUsage() {return "/enchant <name> [level]";}
    }

    private class BroadcastCommand implements FrameworkCommand {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length < 1) return false;
            String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
            Bukkit.broadcastMessage(message);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {return Collections.emptyList();}

        @Override
        public String getPermission() {return "framework.broadcast";}

        @Override
        public String getUsage() {return "/broadcast <message>";}
    }

    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return hours + "h " + minutes + "m " + secs + "s";
    }

    private long parseDuration(String input) {
        try {
            if (input.endsWith("s")) return Long.parseLong(input.replace("s", ""));
            if (input.endsWith("m")) return Long.parseLong(input.replace("m", "")) * 60;
            if (input.endsWith("h")) return Long.parseLong(input.replace("h", "")) * 3600;
            if (input.endsWith("d")) return Long.parseLong(input.replace("d", "")) * 86400;
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            return 60;
        }
    }

    private long parseTime(String value) {
        switch (value.toLowerCase(Locale.ROOT)) {
            case "day":
                return 1000;
            case "night":
                return 13000;
            case "noon":
                return 6000;
            case "midnight":
                return 18000;
            default:
                try {return Long.parseLong(value);} catch (NumberFormatException e) {return 0;}
        }
    }

    private GameMode parseGamemode(String input) {
        try {
            int id = Integer.parseInt(input);
            switch (id) {
                case 0: return GameMode.SURVIVAL;
                case 1: return GameMode.CREATIVE;
                case 2: return GameMode.ADVENTURE;
                case 3: return GameMode.SPECTATOR;
            }
        } catch (NumberFormatException ignored) {}
        for (GameMode gm : GameMode.values()) {
            if (gm.name().equalsIgnoreCase(input)) return gm;
        }
        return null;
    }
}
