package me.onenrico.holoblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.particle.ParticleManager;

public class ParticleUT {
	public static Location newloc;

	public static void send(Player player, String effect, Location loc, float xOffset, float yOffset, float zOffset,
			float speed, int amount, Boolean all) {
		if (all) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				ParticleManager.sendParticles(p, effect, loc, xOffset, yOffset, zOffset, speed, amount);
			}
		} else {
			ParticleManager.sendParticles(player, effect, loc, xOffset, yOffset, zOffset, speed, amount);
		}
	}

	public static void send(Player player, String effect, Location loc, float speed, int amount, Boolean all) {
		send(player, effect, loc, 0, 0, 0, speed, amount, all);
	}

	public static void send(Player player, String effect, Location loc, float speed, Boolean all) {
		send(player, effect, loc, speed, 1, all);
	}

	public static void send(Player player, String effect, Location loc, int amount, Boolean all) {
		send(player, effect, loc, 0, amount, all);
	}

	public static void send(Player player, String effect, Location loc, Boolean all) {
		send(player, effect, loc, 0, 0, 0, 0, 1, all);
	}

	public static void send(Player player, String effect, Boolean all) {
		send(player, effect, player.getLocation(), all);
	}

	public static BukkitTask circleParticle(Location loc, float radius, float height, float maxheight, float speed,
			String particle) {
		String particleOne = particle;
		return new BukkitRunnable() {
			World world = loc.getWorld();
			float newheight = height;
			boolean add = true;
			boolean first = true;
			boolean reversed = false;

			@Override
			public void run() {
				if (first) {
					if (height > maxheight) {
						reversed = true;
					}
				}
				if (radius > 0) {
					for (int x = 0; x < 30; x++) {
						Double xcos = Math.cos(x) * radius;
						Double zsin = Math.sin(x) * radius;
						send(null, particleOne.toUpperCase(),
								new Location(world, loc.getX() + xcos, loc.getY() + newheight, loc.getZ() + zsin), -1,
								1, 0, 1, 0, true);
					}
				} else {
					for (int x = 0; x < 5; x++) {
						send(null, particleOne.toUpperCase(),
								new Location(world, loc.getX(), loc.getY() + newheight, loc.getZ()), -1, 1, 0, 1, 0,
								true);
					}
				}
				if (reversed) {
					if (add) {
						newheight -= speed;
						if (newheight < maxheight) {
							add = false;
						}
					} else {
						newheight += speed;
						if (newheight > height) {
							add = true;
						}
					}
				} else {
					if (add) {
						newheight += speed;
						if (newheight > maxheight) {
							add = false;
						}
					} else {
						newheight -= speed;
						if (newheight < height) {
							add = true;
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Core.getThis(), 0, 5);
	}
}
