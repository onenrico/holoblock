package me.onenrico.holoblock.object;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.MessageUT;

public class MoreHolo {
	private Location loc;
	private Entity entity;
	private World world;
	private String name;
	private int line;
	private holoType type;
	public static String identifier = "&k&b&k&r";
	public MoreHolo(String name,Location loc,holoType type) {
		this.name = name;
		this.loc = loc;
		this.world = loc.getWorld();
		this.line = 1;
		this.type = type;
		build();
	}
	public Entity build() {
		ArmorStand holo = null;
		loc.getChunk().load();
		if(entity == null) {
			holo = (ArmorStand) world.spawnEntity(
					loc.clone().add(0, 10, 0), EntityType.ARMOR_STAND);
			holo.setVisible(false);
			holo.setCustomName(MessageUT.t(identifier+name));
			holo.setCustomNameVisible(true);
			holo.setGravity(false);
			holo.setSmall(true);
			holo.setInvulnerable(true);
			if(!Core.nmsver.startsWith("v1_8")) {
				holo.setCollidable(false);
			}
			entity = holo;
			new BukkitRunnable() {
				@Override
				public void run() {
					getEntity().teleport(loc);
				}
			}.runTaskLater(Core.getThis(), 2);
			return holo;
		}else {
			holo = (ArmorStand) entity;
			holo.setCustomName(MessageUT.t(identifier+name));
			holo.teleport(loc);
		}
		return holo;
	}
	public boolean destroy() {
		boolean ret = false;
		if(entity != null) {
			if(!entity.isDead()) {
				entity.remove();
				ret = true;
			}
		}
		return ret;
	}
	public static enum holoType {
		ITEM,TEXT
	}
	public Entity getEntity() {
		return entity;
	}
	public Location getLoc() {
		return loc;
	}
	public World getWorld() {
		return world;
	}
	public String getName() {
		return name;
	}
	public int getLine() {
		return line;
	}
	public holoType getType() {
		return type;
	}
	public void setLoc(Location loc) {
		this.loc = loc;
		this.world = loc.getWorld();
		build();
	}
	public void setName(String name) {
		this.name = name;
		build();
	}
	public void setLine(int line) {
		this.line = line;
		build();
	}
	public void setType(holoType type) {
		this.type = type;
		build();
	}
}
