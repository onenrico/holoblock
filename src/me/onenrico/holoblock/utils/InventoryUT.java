package me.onenrico.holoblock.utils;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.onenrico.holoblock.object.MenuItem;

public class InventoryUT {
	public static final String steal = MessageUT.t("&r&a&f&r&a&b&r&f");
	public static MenuItem setItem(Inventory inv, int slot, ItemStack item, Boolean Steal) {
		if (Steal) {
			inv.setItem(slot, item);
		} else {
			List<String> Lore = item.getItemMeta().getLore();
			if (Lore == null) {
				Lore = ItemUT.createLore(steal);
			} else {
				Lore.set(0, steal + Lore.get(0));
			}
			item = ItemUT.changeLore(item, Lore);
			inv.setItem(slot, item);
		}
		return new MenuItem(inv, slot);
	}

	public static Inventory createInventory(int Row, String title) {
		return Bukkit.createInventory(null, Row * 9, MessageUT.t(title));
	}

	public static MenuItem setItem(Inventory inv, int slot, ItemStack item) {
		return setItem(inv, slot, item, false);
	}

	public static MenuItem getMenuItem(Inventory inv, int slot) {
		return new MenuItem(inv, slot);
	}

	public static void checkSteal(Player player) { 
		Inventory inv = player.getInventory();
		ItemStack[] inven = inv.getContents();
		if (inven.length < 1) {
			return;
		}
		for (ItemStack i : inven) {
			if (i != null) {
				if (i.hasItemMeta()) {
					ItemMeta meta = i.getItemMeta(); 
					if(meta.hasLore()) { 
						List<String> lore = meta.getLore();
						if (lore.get(0).contains(MessageUT.t(steal))) {
							inv.remove(i);
						}
					}
				}
			}
		}
	}
}

