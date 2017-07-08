package me.onenrico.holoblock.hooker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.onenrico.holoblock.api.HoloBlockAPI;
import me.onenrico.holoblock.database.Datamanager;

public class PlaceholderAPIHook extends EZPlaceholderHook {

	public PlaceholderAPIHook(Plugin plugin) {
		super(plugin, "holoblock");

	}

	@SuppressWarnings("deprecation")
	@Override
	public String onPlaceholderRequest(Player player, String pc) {
		if (pc.startsWith("owned:")) {
			pc = pc.split(":")[1];
			return "" + Datamanager.getDB().getOwned(pc);
		} else if (pc.startsWith("maxowned:")) {
			pc = pc.split(":")[1];
			return "" + HoloBlockAPI.getMaxOwned(Bukkit.getOfflinePlayer(pc), Bukkit.getWorlds().get(0));
		} else if (pc.startsWith("maxowned")) {
			return "" + HoloBlockAPI.getMaxOwned();
		} else if (pc.startsWith("maxline:")) {
			pc = pc.split(":")[1];
			return "" + HoloBlockAPI.getMaxLine(Bukkit.getOfflinePlayer(pc), Bukkit.getWorlds().get(0));
		} else if (pc.startsWith("maxline")) {
			return "" + HoloBlockAPI.getMaxLine();
		} else if (pc.startsWith("maxmember:")) {
			pc = pc.split(":")[1];
			return "" + HoloBlockAPI.getMaxMember(Bukkit.getOfflinePlayer(pc), Bukkit.getWorlds().get(0));
		} else if (pc.startsWith("maxmember")) {
			return "" + HoloBlockAPI.getMaxMember();
		} else if (pc.startsWith("isPlaceholder:")) {
			pc = pc.split(":")[1];
			return "" + HoloBlockAPI.isAllowPlaceholder(Bukkit.getOfflinePlayer(pc), Bukkit.getWorlds().get(0));
		} else if (pc.startsWith("isColor:")) {
			pc = pc.split(":")[1];
			return "" + HoloBlockAPI.isAllowColor(Bukkit.getOfflinePlayer(pc), Bukkit.getWorlds().get(0));
		} else if (pc.startsWith("isCustomSkin:")) {
			pc = pc.split(":")[1];
			return "" + HoloBlockAPI.isAllowCustomSkin(Bukkit.getOfflinePlayer(pc), Bukkit.getWorlds().get(0));
		} else if (pc.startsWith("isItemLine:")) {
			pc = pc.split(":")[1];
			return "" + HoloBlockAPI.isAllowItemLine(Bukkit.getOfflinePlayer(pc), Bukkit.getWorlds().get(0));
		}
		return "";
	}

}
