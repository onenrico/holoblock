package me.onenrico.holoblock.events;

import java.util.List;

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

import me.onenrico.holoblock.api.HoloBlockAPI;
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
import me.onenrico.holoblock.utils.PermissionUT;
import me.onenrico.holoblock.utils.PlaceholderUT;
import me.onenrico.holoblock.utils.PlayerUT;

public class PlaceEvent implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerPlace(final BlockPlaceEvent event) {
		Player player = event.getPlayer();
		ItemStack hand = PlayerUT.getHand(player);
		Block block = event.getBlock();
		Towny towny = Core.towny;
		if (hand.hasItemMeta()) {
			if(hand.getItemMeta().hasDisplayName()) {
				if(hand.getItemMeta().hasLore()) {
					if (ItemUT.getName(hand).equals(ItemUT.getName(Core.getAPI().getHoloItem()))) {
						if (ItemUT.getLore(hand).equals(ItemUT.getLore(Core.getAPI().getHoloItem()))) {
							if (Core.getThis().w_hook.wg != null) {
								Boolean override = Core.getThis().configplugin.getBool("worldguard-override", false);
								if (Core.getThis().w_hook.isAllowBuild(block.getLocation()).equals("true")) {
									event.setCancelled(false);
								} else if (Core.getThis().w_hook.isAllowBuild(block.getLocation()).equals("null")) {
									if (override) {
										MessageUT.plmessage(player, ConfigPlugin.locale.getValue("not_permitted"));
										SoundManager.playSound(player, "BLOCK_NOTE_PLING");
										event.setCancelled(true);
									}
								} else {
									if (override) {
										MessageUT.plmessage(player, ConfigPlugin.locale.getValue("not_permitted"));
										SoundManager.playSound(player, "BLOCK_NOTE_PLING");
										event.setCancelled(true);
									}
								}
							}
							if (!PermissionUT.has(player, "holoblock.place." + block.getLocation().getWorld().getName(),
									player.getWorld())) {
								List<String> noperm = ConfigPlugin.locale.getValue("no_permission");
								PlaceholderUT pu = new PlaceholderUT();
								pu.add("perm", "holoblock.place." + player.getWorld().getName());
								MessageUT.plmessage(player, pu.t(noperm));
								event.setCancelled(true);
								SoundManager.playSound(player, "BLOCK_NOTE_PLING");
								return;
							}
							if (event.isCancelled()) {
								MessageUT.plmessage(player, ConfigPlugin.locale.getValue("not_permitted"));
								SoundManager.playSound(player, "BLOCK_NOTE_PLING");
								return;
							}
							if (towny != null) {
								TownyHook t = new TownyHook(player, block, event, towny);
								if (t.getRetur()) {
									MessageUT.plmessage(player, ConfigPlugin.locale.getValue("not_permitted"));
									SoundManager.playSound(player, "BLOCK_NOTE_PLING");
									event.setCancelled(true);
									return;
								}
							}
							int maxholo = HoloBlockAPI.getMaxOwned(player, block.getWorld());
							int holocount = Datamanager.getDB().getOwned(player.getName());
							PlaceholderUT pu = new PlaceholderUT();
							pu.add("holocount", "" + holocount);
							pu.add("maxholo", "" + maxholo);
							if (holocount >= maxholo) {
								MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("exceeded_holo")));
								SoundManager.playSound(player, "ENTITY_BLAZE_DEATH");
								event.setCancelled(true);
								return;
							}
							HoloPlaceEvent placee = new HoloPlaceEvent(player, HoloBlockAPI.getDefaultLine(),
									block.getLocation(), HoloBlockAPI.getMaxLine(player, player.getWorld()), holocount, maxholo,
									true);
							Bukkit.getPluginManager().callEvent(placee);
							if (placee.isCancelled()) {
								return;
							}
							final Player nplayer = placee.getPlayer();
							Location bloc = placee.getLoc();
							place(nplayer, bloc);
						}
					}
				}
			}
		}
	}

	public static void place(Player player, Location loc) {
		HoloData data = new HoloData(Seriloc.Serialize(loc));
		data.setOwner(player.getName());
		Datamanager.addHolo(data);
		int index = 0;
		for (String line : HoloBlockAPI.getDefaultLine()) {
			data.setLine(index++, line);
		}
		data.saveHolo(new BukkitRunnable() {
			@Override
			public void run() {
				PlaceholderUT pu = new PlaceholderUT();
				int maxholo = HoloBlockAPI.getMaxOwned(player, loc.getWorld());
				int holocount = Datamanager.getDB().getOwned(player.getName());
				pu.add("maxholo", "" + maxholo);
				pu.add("holocount", "" + holocount);
				MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("add_holo")));
				SoundManager.playSound(player, "BLOCK_ANVIL_PLACE");
				FireworkUT.random(loc, 1);
			}
		});
	}

}
