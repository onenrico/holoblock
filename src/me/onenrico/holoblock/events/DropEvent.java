package me.onenrico.holoblock.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import me.onenrico.holoblock.utils.InventoryUT;

public class DropEvent implements Listener {
	@EventHandler
	public void drops(PlayerDropItemEvent event) {
		InventoryUT.checkSteal(event.getPlayer());

	}
}
