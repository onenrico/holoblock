package me.onenrico.holoblock.events;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.gui.MainMenu;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.utils.InventoryUT;

public class CloseEvent implements Listener {
	public static HashMap<Player,String> mainMenuPlayers = new HashMap<>();
//	@EventHandler
//	public void command(PlayerCommandPreprocessEvent e) {
//		if(pu == null) {
//			pu = new PlaceholderUT();
//		}
//		pu = Datamanager.getPlaceholders(pu);
//		if(e.getMessage().contains("/setmaterial ")) {
//			e.setMessage(e.getMessage().replace("setmaterial", "mi material"));
//			return;
//		}
//		if(e.getMessage().contains("/setdata ")) {
//			e.setMessage(e.getMessage().replace("setdata", "mi data"));
//			return;
//		}
//		if(e.getMessage().contains("/setamount ")) {
//			e.setMessage(e.getMessage().replace("setamount", "mi amount"));
//			return;
//		}
//		if(e.getMessage().contains("/saveitem ")) {
//			e.setMessage(e.getMessage().replace("saveitem", "mi save"));
//			return;
//		}
//		if(e.getMessage().contains("/padd ")) {
//			e.setCancelled(true);
//			if(e.getMessage().equals("/padd list")) {
//				Inventory inv = InventoryUT.createInventory(6, "Placeholder List");
//				for(int x = 0;x < pu.getAcuan().keySet().size();x++) {
//					InventoryUT.setItem(inv, x, ItemUT.createItem(Material.PAPER, 
//							"&8[&f"+pu.getAcuan().keySet().toArray()[x]+"&8]", 
//							ItemUT.createLore(pu.getAcuan().get(pu.getAcuan().keySet().toArray()[x]))));
//				}
//				e.getPlayer().openInventory(inv);
//				return;
//			}else {
//				String cmd = e.getMessage().replaceFirst("/padd ", "");
//				String[] data = cmd.split(" ");
//				String msg = "";
//				String placeholder = data[0];
//				if(data.length > 2) {
//					int index = 0;
//					for(String dat : data) {
//						if(index++ > 0) {
//							msg += dat;
//							if(index < data.length) {
//								msg += " ";
//							}
//						}
//					}
//				}else {
//					msg = data[1];
//				}
//				pu.add(placeholder, msg);
//				MessageUT.plmessage(e.getPlayer(), ItemUT.createLore("&aSuccessfully Set !%n%"
//						+ "&8[&f"+placeholder+"&8] &7into"+"%n%&r"+msg));
//				Datamanager.savePlaceholders(pu);
//			}
//		}
//		else {
//			e.setMessage(pu.t(e.getMessage()));
//		}
//		if(e.getMessage().contains("/managelore")) {
//			Inventory inv = InventoryUT.createInventory(6, "&1Manage Lore");
//			ItemStack hand = PlayerUT.getHand(e.getPlayer());
//			if(hand != null) {
//				List<String> lore = ItemUT.getLore(hand);
//				if(lore != null) {
//					for(int x = 0;x < lore.size();x++) {
//						InventoryUT.setItem(inv, x, ItemUT.createItem(Material.PAPER, 
//								"&8[&fLine &b"+(x+1)+"&8]", 
//								ItemUT.createLore(lore.get(x).replace(InventoryUT.steal, ""))),true);
//					}
//				}
//			}
//			e.getPlayer().openInventory(inv);
//			e.setCancelled(true);
//			return;
//		}
//	}
	@EventHandler
	public void close(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if(MainMenu.animation.contains(player)) {
			MainMenu.animation.remove(player);
		}
		InventoryUT.checkSteal(player);
//		if(event.getInventory().getTitle().contains("Manage Lore")) {
//			List<String> lore = new ArrayList<>();
//			int last = -1;
//			for(int x = 0;
//				x < event.getInventory().getSize();
//				x++) {
//				if(event.getInventory().getItem(x) == null) {
//					lore.add("&r");
//				}else {
//					lore.add(ItemUT.getLore(event.getInventory().getItem(x)).get(0).replace(InventoryUT.steal, ""));
//					last = x;
//				}
//			}
//			if(last == -1) {
//				return;
//			}
//			if(last + 1 < lore.size()) {
//				List<String> newl = new ArrayList<>();
//				for(int x = 0;x<=last;x++) {
//					newl.add(lore.get(x));
//				}
//				lore = newl;
//			}
//			ItemUT.changeLore(PlayerUT.getHand(player), lore);
//		}
		if(mainMenuPlayers.containsKey(player)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					SoundManager.playSound(player, "UI_BUTTON_CLICK");
					MainMenu.open(player, mainMenuPlayers.get(player));
					mainMenuPlayers.remove(player);
				}
			}.runTaskLater(Core.getThis(),1);
		}
	}

}
