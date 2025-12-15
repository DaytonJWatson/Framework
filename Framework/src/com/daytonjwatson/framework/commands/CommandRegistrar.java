package com.daytonjwatson.framework.commands;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.utils.MessageHandler;

public class CommandRegistrar {
    private final FrameworkPlugin plugin;
    private final FrameworkAPI api;
    private final StorageManager storage;
    private final PlayerDataManager playerData;
    private final MessageHandler messages;

    public CommandRegistrar(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages) {
        this.plugin = plugin;
        this.api = api;
        this.storage = storage;
        this.playerData = playerData;
        this.messages = messages;
    }

    public void registerCommands() {
        register("help", new HelpCommand(plugin, api, storage, playerData, messages));
        register("list", new ListCommand(plugin, api, storage, playerData, messages));
        register("motd", new MotdCommand(plugin, api, storage, playerData, messages));
        register("rules", new RulesCommand(plugin, api, storage, playerData, messages));
        register("spawn", new SpawnCommand(plugin, api, storage, playerData, messages));
        register("setspawn", new SetSpawnCommand(plugin, api, storage, playerData, messages));
        register("warp", new WarpCommand(plugin, api, storage, playerData, messages));
        register("setwarp", new SetWarpCommand(plugin, api, storage, playerData, messages));
        register("warps", new WarpsCommand(plugin, api, storage, playerData, messages));
        register("home", new HomeCommand(plugin, api, storage, playerData, messages));
        register("sethome", new SetHomeCommand(plugin, api, storage, playerData, messages));
        register("delhome", new DelHomeCommand(plugin, api, storage, playerData, messages));
        register("homes", new HomesCommand(plugin, api, storage, playerData, messages));
        register("tpa", new TpaCommand(plugin, api, storage, playerData, messages));
        register("tpaccept", new TpAcceptCommand(plugin, api, storage, playerData, messages));
        register("tpdeny", new TpDenyCommand(plugin, api, storage, playerData, messages));
        register("back", new BackCommand(plugin, api, storage, playerData, messages));
        register("msg", new MsgCommand(plugin, api, storage, playerData, messages));
        register("reply", new ReplyCommand(plugin, api, storage, playerData, messages));
        register("ignore", new IgnoreCommand(plugin, api, storage, playerData, messages));
        register("nickname", new NicknameCommand(plugin, api, storage, playerData, messages));
        register("realname", new RealnameCommand(plugin, api, storage, playerData, messages));
        register("rtp", new RtpCommand(plugin, api, storage, playerData, messages));
        register("trash", new TrashCommand(plugin, api, storage, playerData, messages));
        register("enderchest", new EnderChestCommand(plugin, api, storage, playerData, messages));
        register("workbench", new WorkbenchCommand(plugin, api, storage, playerData, messages));
        register("stats", new StatsCommand(plugin, api, storage, playerData, messages));
        register("playtime", new PlaytimeCommand(plugin, api, storage, playerData, messages));
        register("ban", new BanCommand(plugin, api, storage, playerData, messages));
        register("tempban", new TempBanCommand(plugin, api, storage, playerData, messages));
        register("unban", new UnbanCommand(plugin, api, storage, playerData, messages));
        register("mute", new MuteCommand(plugin, api, storage, playerData, messages));
        register("tempmute", new TempMuteCommand(plugin, api, storage, playerData, messages));
        register("kick", new KickCommand(plugin, api, storage, playerData, messages));
        register("warn", new WarnCommand(plugin, api, storage, playerData, messages));
        register("warnings", new WarningsCommand(plugin, api, storage, playerData, messages));
        register("time", new TimeCommand(plugin, api, storage, playerData, messages));
        register("weather", new WeatherCommand(plugin, api, storage, playerData, messages));
        register("gamemode", new GamemodeCommand(plugin, api, storage, playerData, messages));
        register("tp", new TeleportCommand(plugin, api, storage, playerData, messages));
        register("tpall", new TpAllCommand(plugin, api, storage, playerData, messages));
        register("fly", new FlyCommand(plugin, api, storage, playerData, messages));
        register("god", new GodCommand(plugin, api, storage, playerData, messages));
        register("heal", new HealCommand(plugin, api, storage, playerData, messages));
        register("feed", new FeedCommand(plugin, api, storage, playerData, messages));
        register("vanish", new VanishCommand(plugin, api, storage, playerData, messages));
        register("invsee", new InvseeCommand(plugin, api, storage, playerData, messages));
        register("endersee", new EnderseeCommand(plugin, api, storage, playerData, messages));
        register("clear", new ClearCommand(plugin, api, storage, playerData, messages));
        register("give", new GiveCommand(plugin, api, storage, playerData, messages));
        register("enchant", new EnchantCommand(plugin, api, storage, playerData, messages));
        register("broadcast", new BroadcastCommand(plugin, api, storage, playerData, messages));
    }

    private void register(String name, BaseCommand executor) {
        if (plugin.getCommand(name) != null) {
            plugin.getCommand(name).setExecutor(executor);
            plugin.getCommand(name).setTabCompleter(executor);
        } else {
            plugin.getLogger().warning("Command not found in plugin.yml: " + name);
        }
    }
}
