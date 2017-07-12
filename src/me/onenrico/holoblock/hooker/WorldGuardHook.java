package me.onenrico.holoblock.hooker;

import org.bukkit.Location;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.onenrico.holoblock.main.Core;

public class WorldGuardHook {
	Core instance;

	public WorldGuardHook(Core instance) {
		this.instance = instance;
		getWorldGuard();
	}

	public WorldGuardPlugin wg = null;

	public WorldGuardPlugin getWorldGuard() {
		if (wg == null) {
			wg = (WorldGuardPlugin) Core.getThis().getServer().getPluginManager().getPlugin("WorldGuard");
		}
		return wg;
	}

	public String isAllowBuild(Location loc) {
		RegionManager manager = wg.getRegionManager(loc.getWorld());
		Vector ve = new Vector().setX(loc.getX()).setY(loc.getY()).setZ(loc.getZ());
		ApplicableRegionSet regions = manager.getApplicableRegions(ve);
		for (ProtectedRegion rg : regions) {
			if (rg.getFlag(DefaultFlag.MYCELIUM_SPREAD) == null) {
				if (Core.getThis().configplugin.getBool("worldguard-override", false)) {
					return "false";
				} else {
					return "null";
				}
			}
			if (rg.getFlag(DefaultFlag.MYCELIUM_SPREAD).equals(true)) {
				return "true";
			}
		}
		return "false";
	}
}
