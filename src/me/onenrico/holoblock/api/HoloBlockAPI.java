package me.onenrico.holoblock.api;

import org.bukkit.Bukkit;

import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.actionbar.ActionBar;
import me.onenrico.holoblock.nms.particle.ParticleManager;

public class HoloBlockAPI {
	public HoloBlockAPI() {
		Core.nmsver = Bukkit.getServer().getClass().getPackage().getName();
		Core.nmsver = Core.nmsver.substring(Core.nmsver.lastIndexOf(".") + 1);
		ActionBar.setup();
		ParticleManager.setup();
	}
}
