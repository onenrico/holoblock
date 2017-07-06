package me.onenrico.holoblock.config;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.PermissionUT;
import me.onenrico.holoblock.utils.PlayerUT;

public class ConfigPlugin {
	public static Core instance;
	public static FileConfiguration config;
	public static Boolean changed = false;

	public ConfigPlugin() {
		instance = Core.getThis();
		config = instance.getConfig();
	}
	public static Locales locale;
	@SuppressWarnings("deprecation")
	public static ItemStack getTool() {
		String name = getStr("holo.item.head", "&cNot Configured");
		String display = getStr("holo.item.displayname", "&cNot Configured");
		List<String> lore = getStrList("holo.item.description",ItemUT.createLore("&cNot Configured"));
		ItemStack result = PlayerUT.getHead(name);
		result = ItemUT.changeDisplayName(result, display);
		result = ItemUT.changeLore(result, lore);
		result.getItemMeta().spigot().setUnbreakable(true);
		return result;
	}
	public static FileConfiguration getConfig() {
		return config;
	} 
	public static void reloadSetting() {
		instance.saveDefaultConfig();
		instance.reloadConfig();
		setupSetting();
		Datamanager.reloadData();
	}
	public static void setupSetting() {
		config = instance.getConfig();
		locale = new Locales(Core.getThis(), "EN");
	}
	public static double getDefaultOffset() {
		return getDouble("holo.default_offset",2);
	}
	public static List<String> getDefaultLine() {
		return getStrList("holo.default_line",ItemUT.createLore("&cNot Configured"));
	}
	public static String getStr(String path, String def) {
		def = MessageUT.t(def);
		if (config.get(path) == null) {
			config.set(path, MessageUT.u(def));
			Core.getThis().saveConfig();
			return def;
		}
		return config.getString(path, def);
	}

	public static List<String> getStrList(String path) {
		return config.getStringList(path);
	}

	public static List<String> getStrList(String path, List<String> def) {
		if (config.getStringList(path).isEmpty()) {
			config.set(path, def);
			Core.getThis().saveConfig();
			return def;
		}
		return config.getStringList(path);
	}

	public static int getInt(String path, int def) {
		if (config.get(path) == null) {
			config.set(path, def);
			Core.getThis().saveConfig();
			return def;
		}
		return config.getInt(path, def);
	}

	public static String getStr(String path) {
		return MessageUT.t(config.getString(path));
	}

	public static int getInt(String path) {
		return config.getInt(path);
	}
	public static double getDouble(String path,double value) {
		return config.getDouble(path,value);
	}

	public static Boolean getBool(String path) {
		return config.getBoolean(path);
	}

	public static Boolean getBool(String path, Boolean def) {
		return config.getBoolean(path, def);
	}
	public static int getMaxOwned() {
		return ConfigPlugin.getInt("holo.max_owned", 5);
	}
	public static int getMaxMember() {
		return ConfigPlugin.getInt("holo.max_member", 5);
	}
	public static int getMaxLine() {
		return ConfigPlugin.getInt("holo.max_line", 100);
	}
	public static int getMaxText() {
		return ConfigPlugin.getInt("holo.max_text", 40);
	}
	public static int getMaxOwned(OfflinePlayer offlineplayer,World world) {
		int max = getMaxOwned();
		for(int x = max;x>0;x--) {
			if(PermissionUT.has(offlineplayer, "holoblock.maxowned."+x, world)) {
				return x;
			}
		}
		return 0;
	}
	public static int getMaxLine(OfflinePlayer offlineplayer,World world) {
		int max = getMaxLine();
		for(int x = max;x>0;x--) {
			if(PermissionUT.has(offlineplayer, "holoblock.maxline."+x, world)) {
				return x;
			}
		}
		return 0;
	}
	public static int getMaxMember(OfflinePlayer offlineplayer,World world) {
		int max = getMaxMember();
		for(int x = max;x>0;x--) {
			if(PermissionUT.has(offlineplayer, "holoblock.maxmember."+x, world)) {
				return x;
			}
		}
		return 0;
	}
}
