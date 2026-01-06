package com.daytonjwatson.framework.commands;

import com.daytonjwatson.framework.FrameworkPlugin;
import com.daytonjwatson.framework.api.FrameworkAPI;
import com.daytonjwatson.framework.autocrop.AutoCropManager;
import com.daytonjwatson.framework.commands.admin.BanCommand;
import com.daytonjwatson.framework.commands.admin.BroadcastCommand;
import com.daytonjwatson.framework.commands.admin.ClearCommand;
import com.daytonjwatson.framework.commands.admin.EnchantCommand;
import com.daytonjwatson.framework.commands.admin.EnderseeCommand;
import com.daytonjwatson.framework.commands.admin.FeedCommand;
import com.daytonjwatson.framework.commands.admin.FlyCommand;
import com.daytonjwatson.framework.commands.admin.GamemodeCommand;
import com.daytonjwatson.framework.commands.admin.GiveCommand;
import com.daytonjwatson.framework.commands.admin.GodCommand;
import com.daytonjwatson.framework.commands.admin.HealCommand;
import com.daytonjwatson.framework.commands.admin.InvseeCommand;
import com.daytonjwatson.framework.commands.admin.KickCommand;
import com.daytonjwatson.framework.commands.admin.MuteCommand;
import com.daytonjwatson.framework.commands.admin.NicknameCommand;
import com.daytonjwatson.framework.commands.admin.SetSpawnCommand;
import com.daytonjwatson.framework.commands.admin.SetWarpCommand;
import com.daytonjwatson.framework.commands.admin.TempBanCommand;
import com.daytonjwatson.framework.commands.admin.TempMuteCommand;
import com.daytonjwatson.framework.commands.admin.TpCommand;
import com.daytonjwatson.framework.commands.admin.TphereCommand;
import com.daytonjwatson.framework.commands.admin.TpallCommand;
import com.daytonjwatson.framework.commands.admin.UnbanCommand;
import com.daytonjwatson.framework.commands.admin.UnmuteCommand;
import com.daytonjwatson.framework.commands.admin.VanishCommand;
import com.daytonjwatson.framework.commands.admin.WarnCommand;
import com.daytonjwatson.framework.commands.admin.WarningsCommand;
import com.daytonjwatson.framework.commands.player.BackCommand;
import com.daytonjwatson.framework.commands.player.AutoCropCommand;
import com.daytonjwatson.framework.commands.player.DelHomeCommand;
import com.daytonjwatson.framework.commands.player.EnderChestCommand;
import com.daytonjwatson.framework.commands.player.HelpCommand;
import com.daytonjwatson.framework.commands.player.HomeCommand;
import com.daytonjwatson.framework.commands.player.HomesCommand;
import com.daytonjwatson.framework.commands.player.IgnoreCommand;
import com.daytonjwatson.framework.commands.player.ListCommand;
import com.daytonjwatson.framework.commands.player.MotdCommand;
import com.daytonjwatson.framework.commands.player.MsgCommand;
import com.daytonjwatson.framework.commands.player.PlaytimeCommand;
import com.daytonjwatson.framework.commands.player.PlayerSettingsCommand;
import com.daytonjwatson.framework.commands.player.PillarCommand;
import com.daytonjwatson.framework.commands.player.RealnameCommand;
import com.daytonjwatson.framework.commands.player.ReplyCommand;
import com.daytonjwatson.framework.commands.player.RtpCommand;
import com.daytonjwatson.framework.commands.player.RulesCommand;
import com.daytonjwatson.framework.commands.player.SetHomeCommand;
import com.daytonjwatson.framework.commands.player.SpawnCommand;
import com.daytonjwatson.framework.commands.player.StatsCommand;
import com.daytonjwatson.framework.commands.player.TimeCommand;
import com.daytonjwatson.framework.commands.player.TpAcceptCommand;
import com.daytonjwatson.framework.commands.player.TpDenyCommand;
import com.daytonjwatson.framework.commands.player.TpahereCommand;
import com.daytonjwatson.framework.commands.player.TpaCommand;
import com.daytonjwatson.framework.commands.player.TrashCommand;
import com.daytonjwatson.framework.commands.player.WarpCommand;
import com.daytonjwatson.framework.commands.player.WarpsCommand;
import com.daytonjwatson.framework.commands.player.WeatherCommand;
import com.daytonjwatson.framework.commands.player.WorkbenchCommand;
import com.daytonjwatson.framework.data.PlayerDataManager;
import com.daytonjwatson.framework.data.StorageManager;
import com.daytonjwatson.framework.settings.PlayerSettingsManager;
import com.daytonjwatson.framework.utils.MessageHandler;

public class CommandRegistrar {
    private final FrameworkPlugin plugin;
    private final FrameworkAPI api;
    private final StorageManager storage;
    private final PlayerDataManager playerData;
    private final MessageHandler messages;
    private final AutoCropManager autoCropManager;
    private final PlayerSettingsManager playerSettingsManager;

    public CommandRegistrar(FrameworkPlugin plugin, FrameworkAPI api, StorageManager storage, PlayerDataManager playerData, MessageHandler messages, AutoCropManager autoCropManager, PlayerSettingsManager playerSettingsManager) {
        this.plugin = plugin;
        this.api = api;
        this.storage = storage;
        this.playerData = playerData;
        this.messages = messages;
        this.autoCropManager = autoCropManager;
        this.playerSettingsManager = playerSettingsManager;
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
        register("tpahere", new TpahereCommand(plugin, api, storage, playerData, messages));
        register("tpaccept", new TpAcceptCommand(plugin, api, storage, playerData, messages));
        register("tpdeny", new TpDenyCommand(plugin, api, storage, playerData, messages));
        register("back", new BackCommand(plugin, api, storage, playerData, messages));
        register("msg", new MsgCommand(plugin, api, storage, playerData, messages));
        register("reply", new ReplyCommand(plugin, api, storage, playerData, messages));
        register("ignore", new IgnoreCommand(plugin, api, storage, playerData, messages));
        register("nickname", new NicknameCommand(plugin, api, storage, playerData, messages));
        register("realname", new RealnameCommand(plugin, api, storage, playerData, messages));
        register("rtp", new RtpCommand(plugin, api, storage, playerData, messages));
        register("playersettings", new PlayerSettingsCommand(plugin, api, storage, playerData, messages, playerSettingsManager));
        register("trash", new TrashCommand(plugin, api, storage, playerData, messages));
        register("enderchest", new EnderChestCommand(plugin, api, storage, playerData, messages));
        register("workbench", new WorkbenchCommand(plugin, api, storage, playerData, messages));
        register("stats", new StatsCommand(plugin, api, storage, playerData, messages));
        register("playtime", new PlaytimeCommand(plugin, api, storage, playerData, messages));
        register("pillar", new PillarCommand(plugin, api, storage, playerData, messages));
        register("ban", new BanCommand(plugin, api, storage, playerData, messages));
        register("tempban", new TempBanCommand(plugin, api, storage, playerData, messages));
        register("unban", new UnbanCommand(plugin, api, storage, playerData, messages));
        register("mute", new MuteCommand(plugin, api, storage, playerData, messages));
        register("tempmute", new TempMuteCommand(plugin, api, storage, playerData, messages));
        register("unmute", new UnmuteCommand(plugin, api, storage, playerData, messages));
        register("kick", new KickCommand(plugin, api, storage, playerData, messages));
        register("warn", new WarnCommand(plugin, api, storage, playerData, messages));
        register("warnings", new WarningsCommand(plugin, api, storage, playerData, messages));
        register("time", new TimeCommand(plugin, api, storage, playerData, messages));
        register("weather", new WeatherCommand(plugin, api, storage, playerData, messages));
        register("gamemode", new GamemodeCommand(plugin, api, storage, playerData, messages));
        register("tp", new TpCommand(plugin, api, storage, playerData, messages));
        register("tphere", new TphereCommand(plugin, api, storage, playerData, messages));
        register("tpall", new TpallCommand(plugin, api, storage, playerData, messages));
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
        register("autocrop", new AutoCropCommand(plugin, api, storage, playerData, messages, autoCropManager));
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
