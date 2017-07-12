package me.onenrico.holoblock.locale;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class Locales extends YamlConfiguration {
	private static HashMap<String, List<String>> map = new HashMap<>();
	private static HashMap<String, String> map2 = new HashMap<>();
	public static PlaceholderUT pub = null;

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
		ConfigurationSection cs = Core.getThis().configplugin.getConfig().getConfigurationSection("messages");
		if (cs != null) {
			Set<String> keys = cs.getKeys(false);
			if (keys != null) {
				MessageUT.debug("Converting...");
				for (String key : keys) {
					MessageUT.debug("Convert: " + key);
					set("messages." + key, getStrList("messages." + key, new ArrayList<>()));
					Core.getThis().configplugin.getConfig().set("messages." + key, null);
				}
				Core.getThis().configplugin.getConfig().set("messages", null);
				Core.getThis().saveConfig();
				save();
				MessageUT.debug("Success...");
			}
		}
		Set<String> nkeys = getConfigurationSection("messages").getKeys(false);
		if (nkeys != null) {
			for (String key : nkeys) {
				map.put(key, getStringList("messages." + key));
			}
		}
		Set<String> nkeys2 = getConfigurationSection("custom-placeholder").getKeys(false);
		if (nkeys2 != null) {
			for (String key : nkeys2) {
				map2.put(key, getString("custom-placeholder." + key));
			}
		}
		pub = new PlaceholderUT(getPlaceholder());
		pluginPrefix = Core.getThis().configplugin.getStr("pluginPrefix", "&cNot Configured");
	}

	@SuppressWarnings("deprecation")
	public List<String> getValue(String msg) {
		if (map.get(msg) == null) {
			InputStream is = Core.getThis().getResource("lang_EN.yml");
			File file = new File(Core.getThis().getDataFolder(), "lang.temp");
			try {
				FileUtils.copyInputStreamToFile(is, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileConfiguration defaultc = YamlConfiguration.loadConfiguration(file);
			List<String> mmsg = defaultc.getStringList("messages." + msg);
			set("messages." + msg, mmsg);
			map.put(msg, mmsg);
			save();
			file.delete();
		}
		return map.get(msg);
	}

	public static HashMap<String, String> getPlaceholder() {
		return map2;
	}

	private List<String> getStrList(String path, List<String> def) {
		return Core.getThis().configplugin.getStrList(path, def);
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
