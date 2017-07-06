package me.onenrico.holoblock.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.Towny;

import me.onenrico.holoblock.api.HoloPlaceEvent;
import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.hooker.TownyHook;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.object.Seriloc;
import me.onenrico.holoblock.utils.FireworkUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.PlaceholderUT;
import me.onenrico.holoblock.utils.PlayerUT;


public class PlaceEvent implements Listener {
	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerPlace(final BlockPlaceEvent event) {
		Player player = event.getPlayer();
		ItemStack hand = PlayerUT.getHand(player);
		Block block = event.getBlock();
		Towny towny= Core.towny;
		if(hand.hasItemMeta()) {
			if(event.isCancelled()) {
				return;
			}
			if(towny != null)
			{
				TownyHook t = new TownyHook(player, block, event, towny);
				if(t.getRetur()) {
					return;
				}
			}
			if(ItemUT.getName(hand).equals(ItemUT.getName(ConfigPlugin.getTool()))) {
				if(ItemUT.getLore(hand).equals(ItemUT.getLore(ConfigPlugin.getTool()))) {
					int maxholo = ConfigPlugin.getMaxOwned(player, block.getWorld());
					int holocount = Datamanager.getDB().getOwned(player.getName());
					PlaceholderUT pu = new PlaceholderUT();
					pu.add("holocount", ""+holocount);
					pu.add("maxholo", ""+maxholo);
					if(holocount >= maxholo) {
						MessageUT.acplmessage(player, pu.t(ConfigPlugin.locale.getValue("exceeded_holo")));
						SoundManager.playSound(player, "ENTITY_BLAZE_DEATH");
						event.setCancelled(true);
						return;
					}
					HoloPlaceEvent placee = new HoloPlaceEvent(player,
							ConfigPlugin.getDefaultLine(),
							block.getLocation(),
							ConfigPlugin.getMaxLine(player, player.getWorld()), 
							holocount, 
							maxholo, 
							true); 
					Bukkit.getPluginManager().callEvent(placee);
					if(placee.isCancelled()) {
						return;
					}
					final Player nplayer = placee.getPlayer();
					Location bloc = placee.getLoc();
					place(nplayer,bloc);
				}
			}
		}
	}

	public static void place(Player player,Location loc) {
		HoloData data = new HoloData(Seriloc.Serialize(loc));
		data.setOwner(player.getName());
		Datamanager.addHolo(
				data);
		int index = 0;
		for(String line : ConfigPlugin.getDefaultLine()) {
			data.setLine(index++, line);
		}
		data.saveHolo(
				new BukkitRunnable() {
					@Override
					public void run() {
						PlaceholderUT pu = new PlaceholderUT();
						int maxholo = ConfigPlugin.getMaxOwned(player, loc.getWorld());
						int holocount = Datamanager.getDB().getOwned(player.getName());
						pu.add("maxholo", ""+maxholo);
						pu.add("holocount", ""+holocount);
						MessageUT.acplmessage(player, pu.t(ConfigPlugin.locale.getValue("add_holo")));
						SoundManager.playSound(player, "BLOCK_ANVIL_PLACE");
						FireworkUT.random(loc, 1);
					}
				});
	}

}
