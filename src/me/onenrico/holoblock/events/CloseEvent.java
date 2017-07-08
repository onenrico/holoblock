package me.onenrico.holoblock.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.gui.AdminHologramMenu;
import me.onenrico.holoblock.gui.MainMenu;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.utils.InventoryUT;

public class CloseEvent implements Listener {
	public static HashMap<Player, String> mainMenuPlayers = new HashMap<>();
	public static List<Player> adminPlayers = new ArrayList<>();

	@EventHandler
	public void close(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (MainMenu.animation.contains(player)) {
			MainMenu.animation.remove(player);
		}
		InventoryUT.checkSteal(player);
		if (mainMenuPlayers.containsKey(player)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					SoundManager.playSound(player, "UI_BUTTON_CLICK");
					if (mainMenuPlayers.containsKey(player)) {
						MainMenu.open(player, mainMenuPlayers.get(player));
						mainMenuPlayers.remove(player);
					}
				}
			}.runTaskLater(Core.getThis(), 1);
		}
		if (adminPlayers.contains(player)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					SoundManager.playSound(player, "UI_BUTTON_CLICK");
					if (adminPlayers.contains(player)) {
						AdminHologramMenu.open(player, 1);
						while (adminPlayers.contains(player)) {
							adminPlayers.remove(player);
						}
					}
				}
			}.runTaskLater(Core.getThis(), 1);
		}
	}

}
