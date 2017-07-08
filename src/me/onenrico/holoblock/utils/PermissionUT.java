package me.onenrico.holoblock.utils;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.main.Core;

public class PermissionUT {
	public static boolean check(Player player, String perm) {
		if (has(player, perm)) {
			return true;
		} else {
			PlaceholderUT pu = new PlaceholderUT();
			pu.add("perm", perm);
			List<String> msg = pu.t(ConfigPlugin.locale.getValue("no_permission"));
			MessageUT.plmessage(player, msg, true);
			return false;
		}
	}

	public static boolean has(Player player, String cperm) {
		if (player.hasPermission(cperm)) {
			return true;
		}
		return false;
	}

	public static boolean has(OfflinePlayer offlineplayer, String perm, World world) {
		if (Core.v_permission.playerHas(world.getName(), offlineplayer, perm)) {
			return true;
		}
		return false;
	}
}
