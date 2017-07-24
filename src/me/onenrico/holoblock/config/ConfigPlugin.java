package me.onenrico.holoblock.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.ConfigUT;

public class ConfigPlugin extends ConfigUT implements ConfigSet {
	public static Core instance;
	public static Boolean changed = false;
	private static File file;
	
	public FileConfiguration defaultconfig = null;
	public File defaultfile = null;
	public InputStream is = null;
	
	public ConfigPlugin() {
		instance = Core.getThis();
		config = instance.getConfig();
		String filen = "config";
		file = new File(instance.getDataFolder(), filen + ".yml");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
		if (!file.exists()) {
			Core.getThis().saveResource(filen + ".yml", false);
			try {
				config = YamlConfiguration.loadConfiguration(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			instance.reloadConfig();
		}
		is = Core.getThis().getResource("config.yml");
		defaultfile = new File(Core.getThis().getDataFolder(), 
				"config.yml.temp");
		try {
			if(defaultfile.exists()) {
				defaultfile.delete();
				defaultfile.createNewFile();
			}
			FileUtils.copyInputStreamToFile(is, defaultfile);
			defaultconfig = 
					YamlConfiguration.loadConfiguration(defaultfile);
			defaultfile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static Locales locale;

	public static FileConfiguration config;

	@Override
	public FileConfiguration getConfig() {
		return config;
	}

	@Override
	public List<String> getStrList(String path, List<String> def) {
		return getStrList(path, def, config, defaultconfig, file);
	}

	@Override
	public List<String> getStrList(String path) {
		return getStrList(path, config);
	}

	@Override
	public String getStr(String path, String def) {
		return getStr(path, def, config, defaultconfig, file);
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
		return getInt(path, def, config,file);
	}

	@Override
	public Boolean getBool(String path, boolean def) {
		return getBool(path, def, config,file);
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
		locale = new Locales(Core.getThis(), getStr("locales", "EN").toUpperCase());
	}
}
