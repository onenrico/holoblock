package me.onenrico.holoblock.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.gui.MainMenu;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.object.Seriloc;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.ParticleUT;
import me.onenrico.holoblock.utils.PermissionUT;

public class InteractEvent implements Listener {
	private List<Player> interacted = new ArrayList<>();
	@EventHandler
	public void playerInteract(final PlayerInteractEvent event) {
		if(interacted.contains(event.getPlayer())) {
			interacted.remove(event.getPlayer());
			return;
		}
		interacted.add(event.getPlayer());
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Player player = event.getPlayer();
			Block block = event.getClickedBlock();
			Location loc = block.getLocation();
			String rawloc = Seriloc.Serialize(loc);
			HoloData data = Datamanager.getDataByLoc(rawloc);
			if(data == null) {
				return;
			}
			String playern = data.getOwner();
			if(playern == null) {
				return;
			}
			loc = Seriloc.centered(loc);
			if(!PermissionUT.has(player, "holoblock.admin")) {
				if(!player.getName().equals(playern)) {
					Boolean pass = false;
					for(String member : data.getMembers()) {
						if(player.getName().equals(member)) {
							pass = true;
						}
					}
					if(!pass) {
						MessageUT.plmessage(player, Locales.get("not_permitted"), true);
						ParticleUT.send(player, "CLOUD", loc, 0.05f, 0.5f, 0.05f, 0.08f, 25, false);
						SoundManager.playSound(player, "ENTITY_BLAZE_DEATH",loc);
						event.setCancelled(true);
						return;
					}
				}
			}
			MainMenu.open(player, rawloc);
			event.setCancelled(true);
			new BukkitRunnable() {
				@Override
				public void run() {
					if(interacted.contains(event.getPlayer())) {
						interacted.remove(event.getPlayer());
					}
				}

			}.runTaskLater(Core.getThis(), 2);
		}
	}
}
