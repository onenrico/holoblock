package me.onenrico.holoblock.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.onenrico.holoblock.utils.PlayerUT;

public class JoinEvent implements Listener {
	@EventHandler
	public void join(PlayerJoinEvent event) {
		if (PlayerUT.getOnlinePlayers().size() == 1) {
			// Core.getThis().configplugin.reloadSetting();
		}
	}
}
