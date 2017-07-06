package me.onenrico.holoblock.hooker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.utils.MessageUT;

public class PlaceholderAPIHook extends EZPlaceholderHook {

	public PlaceholderAPIHook(Plugin plugin) {
		super(plugin, "holoblock");
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public String onPlaceholderRequest(Player player, String pc) {
		if(pc.startsWith("owned:")) {
			pc = pc.split(":")[1];
			return ""+Datamanager.getDB().getOwned(pc);
		}
		else if(pc.startsWith("maxowned:")) {
			pc = pc.split(":")[1];
			return ""+ConfigPlugin.getMaxOwned(
					Bukkit.getOfflinePlayer(pc), 
					Bukkit.getWorlds().get(0));
		}
		else if(pc.startsWith("maxowned")) {
			return ""+ConfigPlugin.getMaxOwned();
		}
		else if(pc.startsWith("maxline:")) {
			pc = pc.split(":")[1];
			return ""+ConfigPlugin.getMaxLine(
					Bukkit.getOfflinePlayer(pc), 
					Bukkit.getWorlds().get(0));
		}
		else if(pc.startsWith("maxline")) {
			return ""+ConfigPlugin.getMaxLine();
		}
		else if(pc.startsWith("maxmember:")) {
			pc = pc.split(":")[1];
			return ""+ConfigPlugin.getMaxMember(
					Bukkit.getOfflinePlayer(pc), 
					Bukkit.getWorlds().get(0));
		}
		else if(pc.startsWith("maxmember")) {
			return ""+ConfigPlugin.getMaxMember();
		}
		else if(pc.startsWith("isPlaceholder:")) {
			pc = pc.split(":")[1];
			return ""+ConfigPlugin.isAllowPlaceholder(
					Bukkit.getOfflinePlayer(pc), 
					Bukkit.getWorlds().get(0));
		}
		else if(pc.startsWith("isColor:")) {
			pc = pc.split(":")[1];
			return ""+ConfigPlugin.isAllowColor(
					Bukkit.getOfflinePlayer(pc), 
					Bukkit.getWorlds().get(0));
		}
		else if(pc.startsWith("isCustomSkin:")) {
			pc = pc.split(":")[1];
			return ""+ConfigPlugin.isAllowCustomSkin(
					Bukkit.getOfflinePlayer(pc), 
					Bukkit.getWorlds().get(0));
		}
		else if(pc.startsWith("isItemLine:")) {
			pc = pc.split(":")[1];
			return ""+ConfigPlugin.isAllowItemLine(
					Bukkit.getOfflinePlayer(pc), 
					Bukkit.getWorlds().get(0));
		}
		return "";
	}

}
