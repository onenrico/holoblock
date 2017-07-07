package me.onenrico.holoblock.utils;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.object.MoreHolo;
import me.onenrico.holoblock.object.MoreHolo.holoType;

public class HoloUT {
	public static MoreHolo createLegacyHologram(Location loc) {
		MoreHolo holo = new MoreHolo("&b&lTest Halo", loc, holoType.TEXT);
		return holo;
	}

	private static double offset = .24;

	public static Hologram createHologram(Location loc) {
		if (Core.useHolo) {
			Hologram holo = HologramsAPI.createHologram(Core.getThis(), loc);
			holo.setAllowPlaceholders(true);
			return holo;
		} else {
			return null;
		}
	}

	public static Hologram createHologram(Location loc, Boolean placeholder) {
		if (Core.useHolo) {
			Hologram holo = HologramsAPI.createHologram(Core.getThis(), loc);
			holo.setAllowPlaceholders(placeholder);
			return holo;
		} else {
			return null;
		}
	}

	public static TextLine setLine(Hologram holo, int line, String text) {
		try {
			text = MessageUT.t(text);
			String temp = text;
			try {
				Object cline = holo.getLine(line);
				if (cline instanceof TextLine) {
					TextLine tline = (TextLine) holo.getLine(line);
					tline.setText(text);
					return tline;
				} else {
					new BukkitRunnable() {
						@Override
						public void run() {
							insertLine(holo, line, temp);
							holo.removeLine(line + 1);
						}

					}.runTaskLater(Core.getThis(), 2);
					return null;
				}
			} catch (IndexOutOfBoundsException ex) {
				new BukkitRunnable() {
					@Override
					public void run() {
						for (int current = holo.size(); current < line; current++) {
							holo.appendTextLine("");
							holo.teleport(new Location(holo.getWorld(), holo.getX(), holo.getY() + offset, holo.getZ()));
						}
						holo.teleport(new Location(holo.getWorld(), holo.getX(), holo.getY() + offset, holo.getZ()));
						holo.appendTextLine(temp);

					}

				}.runTaskLater(Core.getThis(), 2);
				return null;
			}
		}catch(Exception ex) {return null;}
	}

	public static ItemLine setLine(Hologram holo, int line, ItemStack item) {
		try {
			Object cline = holo.getLine(line);
			if (cline instanceof ItemLine) {
				ItemLine hline = (ItemLine) holo.getLine(line);
				hline.setItemStack(item);
				return hline;
			} else {
				new BukkitRunnable() {
					@Override
					public void run() {
						insertLine(holo, line, item);
						holo.removeLine(line + 1);
					}

				}.runTaskLater(Core.getThis(), 2);
				return null;
			}
		} catch (IndexOutOfBoundsException ex) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for (int current = holo.size(); current < line; current++) {
						holo.appendTextLine("");
						holo.teleport(new Location(holo.getWorld(), holo.getX(), holo.getY() + offset, holo.getZ()));
					}
					holo.appendItemLine(item);
					holo.teleport(new Location(holo.getWorld(), holo.getX(), holo.getY() + offset, holo.getZ()));
				}
			}.runTaskLater(Core.getThis(), 2);
			return null;
		}catch(Exception ex) {
			return null;
		}
	}

	public static void insertLine(Hologram holo, int line, String text) {
		String temp = MessageUT.t(text);
		try {
			holo.insertTextLine(line, temp);
		} catch (IndexOutOfBoundsException ex) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for (int current = holo.size(); current <= line; current++) {
						holo.appendTextLine("");
						holo.teleport(new Location(holo.getWorld(), holo.getX(), holo.getY() + offset, holo.getZ()));
					}
					holo.appendTextLine(temp);
					holo.teleport(new Location(holo.getWorld(), holo.getX(), holo.getY() + offset, holo.getZ()));
				}

			}.runTaskLater(Core.getThis(), 2);
		}
	}

	public static void insertLine(Hologram holo, int line, ItemStack item) {
		try {
			holo.insertItemLine(line, item);
			;
		} catch (IndexOutOfBoundsException ex) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for (int current = holo.size(); current <= line; current++) {
						holo.appendTextLine("");
						holo.teleport(new Location(holo.getWorld(), holo.getX(), holo.getY() + offset, holo.getZ()));
					}
					holo.insertItemLine(line, item);
					holo.teleport(new Location(holo.getWorld(), holo.getX(), holo.getY() + offset, holo.getZ()));
				}
			}.runTaskLater(Core.getThis(), 2);
		}
	}

	public static void removeLine(Hologram holo, int line) {
		try {
			if (holo.size() > line) {
				holo.removeLine(line);
				new BukkitRunnable() {
					@Override
					public void run() {
						holo.teleport(new Location(holo.getWorld(), holo.getX(), holo.getY() - offset, holo.getZ()));
					}

				}.runTaskLater(Core.getThis(), 2);
			}
		} catch (Exception ex) {
		}
	}
}
