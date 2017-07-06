package me.onenrico.holoblock.locale;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MessageUT;

public class Locales extends YamlConfiguration {
	private static HashMap<String, List<String>> map = new HashMap<>();

	public Locales(JavaPlugin plugin, String locale) {
		file = new File(plugin.getDataFolder(), "lang_" + locale + ".yml");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
		if (!file.exists()) {
			Core.getThis().saveResource("lang_EN.yml", false);
		}
		reload();
		setup();
	}

	public void setup() {
		ConfigurationSection cs = ConfigPlugin.getConfig().getConfigurationSection("messages");
		if (cs != null) {
			Set<String> keys = cs.getKeys(false);
			if (keys != null) {
				MessageUT.debug("Converting...");
				for (String key : keys) {
					MessageUT.debug("Convert: " + key);
					set("messages." + key, getStrList("messages." + key, new ArrayList<>()));
					ConfigPlugin.getConfig().set("messages." + key, null);
				}
				ConfigPlugin.getConfig().set("messages", null);
				Core.getThis().saveConfig();
				save();
				MessageUT.debug("Success...");
			}
		}
		Set<String> nkeys = getConfigurationSection("messages").getKeys(false);
		for (String key : nkeys) {
			map.put(key, getStringList("messages." + key));
		}
		pluginPrefix = ConfigPlugin.getStr("pluginPrefix", "&cNot Configured");
	}

	public List<String> getValue(String msg) {
		if (map.get(msg) == null) {
			set("messages." + msg, ItemUT.createLore("&cNot Configured"));
			map.put(msg, ItemUT.createLore("&cCheck Lang File &7[&fmessages." + msg + "&7]"));
			save();
		}
		return map.get(msg);
	}

	private List<String> getStrList(String path, List<String> def) {
		return ConfigPlugin.getStrList(path, def);
	}

	public static String pluginPrefix;

	private final File file;

	public void reload() {
		try {
			load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
