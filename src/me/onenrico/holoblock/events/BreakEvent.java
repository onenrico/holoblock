package me.onenrico.holoblock.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.object.Seriloc;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.ParticleUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class BreakEvent implements Listener {

	@EventHandler
	public void playerBreak(final BlockBreakEvent event) {
		if (event.isCancelled()) {
			event.setCancelled(true);
			return;
		}
		Player player = event.getPlayer();
		Location loc = event.getBlock().getLocation();
		if (loc.getBlock().getType().equals(Material.SKULL)) {
			String rawloc = Seriloc.Serialize(loc);
			HoloData data = Datamanager.getDataByLoc(rawloc);
			if (data == null) {
				return;
			}
			String playern = data.getOwner();
			if (playern == null) {
				return;
			}
			List<String> msg = ConfigPlugin.locale.getValue("not_permitted");
			List<String> msg2 = ConfigPlugin.locale.getValue("remove_holo");
			loc = Seriloc.centered(loc);
			if (!player.hasPermission("holoblock.admin")) {
				if (!player.getName().equals(playern)) {
					MessageUT.plmessage(player, msg, true);
					ParticleUT.send(player, "CLOUD", loc, 0.05f, 0.5f, 0.05f, 0.08f, 25, true);
					SoundManager.playSound(player, "ENTITY_BLAZE_DEATH", loc);
					event.setCancelled(true);
					return;
				}
			}
			event.getBlock().getDrops().clear();
			loc.getWorld().dropItemNaturally(loc.add(0, .5d, 0), ConfigPlugin.getTool());
			ParticleUT.send(player, "FLAME", loc, 0.01f, 1f, 0.01f, 0.08f, 25, true);
			SoundManager.playSound(player, "BLOCK_ANVIL_USE", loc);
			Datamanager.deleteHolo(Datamanager.getDataByLoc(rawloc));
			int maxholo = ConfigPlugin.getMaxOwned(player, loc.getWorld());
			int holocount = Datamanager.getDB().getOwned(player.getName());
			PlaceholderUT pu = new PlaceholderUT();
			pu.add("holocount", "" + holocount);
			pu.add("maxholo", "" + maxholo);
			MessageUT.plmessage(player, pu.t(msg2));
		}
	}
}
