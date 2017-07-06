package me.onenrico.holoblock.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HoloPlaceEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Location loc;
	private List<String> defaultlines;
	private int maxline;
	private int currentowned;
	private int maxowned;
	private boolean placeholder;
	private boolean cancelled = false;

	public HoloPlaceEvent(Player player, List<String> defaultlines, Location loc, int maxline, int currentowned,
			int maxowned, boolean placeholder) {
		this.player = player;
		this.defaultlines = defaultlines;
		this.loc = loc;
		this.maxline = maxline;
		this.currentowned = currentowned;
		this.maxowned = maxowned;
		this.placeholder = placeholder;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public Location getLoc() {
		return loc;
	}

	public List<String> getDefaultlines() {
		return defaultlines;
	}

	public int getMaxline() {
		return maxline;
	}

	public int getCurrentowned() {
		return currentowned;
	}

	public int getMaxowned() {
		return maxowned;
	}

	public boolean isPlaceholder() {
		return placeholder;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
