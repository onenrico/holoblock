package me.onenrico.holoblock.config;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.ConfigUT;

public class DatabaseConfig extends ConfigUT implements ConfigSet {
	public FileConfiguration config = null;
	private final File file;

	public DatabaseConfig(JavaPlugin plugin, String filen) {

		file = new File(plugin.getDataFolder(), filen + ".yml");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
		if (!file.exists()) {
			Core.getThis().saveResource(filen + ".yml", false);
		}
		reload();
	}

	public void reload() {
		try {
			config = YamlConfiguration.loadConfiguration(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

}
