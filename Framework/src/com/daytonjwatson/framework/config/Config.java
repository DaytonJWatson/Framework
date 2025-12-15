package com.daytonjwatson.framework.config;

import com.daytonjwatson.framework.FrameworkPlugin;

public class Config {
	
	public static void setup() {
		create();
	}
	
	private static void create() {
		FrameworkPlugin.instance.getConfig().options().copyDefaults();
		FrameworkPlugin.instance.saveDefaultConfig();
	}
	
	public static void save() {
		FrameworkPlugin.instance.saveConfig();
	}
	
	public static void reload() {
		FrameworkPlugin.instance.reloadConfig();
	}	
}
