package com.daytonjwatson.framework;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.daytonjwatson.framework.config.Config;
import com.daytonjwatson.framework.listeners.PlayerJoinListener;

public class FrameworkPlugin extends JavaPlugin {
	
	public static FrameworkPlugin instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		Config.setup();
		
		loadCommands();
		loadListeners();
	}
	
	private void loadCommands() {
		
	}
	
	private void loadListeners() {
		PluginManager pm = this.getServer().getPluginManager();
		
		pm.registerEvents(new PlayerJoinListener(), this);
	}
	
}
