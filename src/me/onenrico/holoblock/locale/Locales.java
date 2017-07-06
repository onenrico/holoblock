package me.onenrico.holoblock.locale;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.utils.ItemUT;

public class Locales {
	private static HashMap <String,List<String>> map = new HashMap<>();
	public static void setup() {
		Set<String> keys = ConfigPlugin.getConfig().getConfigurationSection("messages").getKeys(false);
		for(String key : keys) {
			 map.put(key, getStrList("messages."+key, ItemUT.createLore("&cNot Configured")));
		}
		pluginPrefix = ConfigPlugin.getStr("pluginPrefix","&cNot Configured");
	}
	public static List<String> get(String msg){
		return map.getOrDefault(msg, ItemUT.createLore("&cNot Configured"));
	}
	private static List<String> getStrList(String path,List<String> def){
		return ConfigPlugin.getStrList(path, def);
	}
	public static String pluginPrefix;
}
