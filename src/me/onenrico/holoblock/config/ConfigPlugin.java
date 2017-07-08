package me.onenrico.holoblock.config;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.ConfigUT;

public class ConfigPlugin extends ConfigUT implements ConfigSet {
	public static Core instance;
	public static Boolean changed = false;

	public ConfigPlugin() {
		instance = Core.getThis();
		config = instance.getConfig();
	}

	public static Locales locale;

	public static FileConfiguration config;

	@Override
	public FileConfiguration getConfig() {
		return config;
	}

	@Override
	public List<String> getStrList(String path, List<String> def) {
		return getStrList(path, def, config);
	}

	@Override
	public List<String> getStrList(String path) {
		return getStrList(path, config);
	}

	@Override
	public String getStr(String path, String def) {
		return getStr(path, def, config);
	}

	@Override
	public String getStr(String path) {
		return getStr(path, config);
	}

	@Override
	public double getDouble(String path, double v) {
		return getDouble(path, v, config);
	}

	@Override
	public int getInt(String path, int def) {
		return getInt(path, def, config);
	}

	@Override
	public Boolean getBool(String path, boolean def) {
		return getBool(path, def, config);
	}

	@Override
	public Boolean getBool(String path) {
		return getBool(path, config);
	}

	public void reloadSetting() {
		instance.saveDefaultConfig();
		instance.reloadConfig();
		setupSetting();
		instance.datamanager.reloadData();
	}

	public void setupSetting() {
		config = instance.getConfig();
		locale = new Locales(Core.getThis(), getStr("locales","EN").toUpperCase());
	}
}
