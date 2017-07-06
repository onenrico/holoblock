package me.onenrico.holoblock.utils;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.ItemUT;

public class PlayerUT {
	
	public static Collection<? extends Player> getOnlinePlayers(){
		return Bukkit.getServer().getOnlinePlayers();
	}
	public static Player getPlayer(String name) {
		return getPlayer(name,false);
	}
	public static Player getPlayer(String name,Boolean exact) {
		if(exact) {
			return Bukkit.getPlayerExact(name);
		}else {
			return Bukkit.getPlayer(name);
		}
	}
	public static Player getPlayer(UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}
	public static Player getPlayer(Object object) {
		Player player = (Player) object;
		return player;
	}
	public static Boolean isOnline(Player player) {
		return isOnline(player.getName());
	}
	public static Boolean isOnline(String name) {
		if(Bukkit.getPlayer(name) == null) {
			return false;
		}
		return true;
	}
	public static ItemStack getHead(String player) {
		ItemStack item = ItemUT.createItem(Material.SKULL_ITEM,(short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(player);
		item.setItemMeta(meta);
		return item;
	}
	@SuppressWarnings("deprecation")
	public static void setHand(Player player,ItemStack item) {
		boolean oldmethod = false;
		for(int x = 1;x<4;x++) {
			if (Core.nmsver.equalsIgnoreCase("v1_8_R"+x)){
				oldmethod = true;
			}
		}
		if(oldmethod) {
			player.getInventory().setItemInHand(item);
			return;
		}else {
			player.getInventory().setItemInMainHand(item);
			return;
		}
	}
	@SuppressWarnings("deprecation")
	public static ItemStack getHand(Player player) {
		boolean oldmethod = false;
		for(int x = 1;x<4;x++) {
			if (Core.nmsver.equalsIgnoreCase("v1_8_R"+x)){
				oldmethod = true;
			}
		}
		if(oldmethod) {
			return player.getItemInHand();
		}else {
			return player.getInventory().getItemInMainHand();
		}
	}
}
