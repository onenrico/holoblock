package me.onenrico.holoblock.utils;

import java.io.StringReader;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemSerializer {
	private static FileConfiguration cache = new YamlConfiguration();

	public static String serialize(ItemStack item) {
		cache.set("item", item);
		String a = cache.saveToString();
		String data = a.replace("\n", "<!>");
		return data;
	}

	public static ItemStack deserialize(String seritem) {
		String seri = seritem.replace("<!>", "\n");
		FileConfiguration loaded = YamlConfiguration.loadConfiguration(new StringReader(seri));
		return loaded.getItemStack("item");
	}

	public static String serialize(Inventory inv) {
		StringBuilder result = new StringBuilder();
		for (int slot = 0; slot < inv.getSize(); slot++) {
			ItemStack item = inv.getItem(slot);
			if (item != null) {
				result.append("<#" + slot + "$:" + serialize(item));
			}
		}
		return result.toString();
	}

	public static void deserialize(String arg, Inventory inv) {
		inv.clear();
		if (arg == null) {
			return;
		}
		String[] list = arg.split("<#");
		if (list == null || list.length < 1) {
			return;
		}
		for (String l : list) {
			try {
				int slot = MathUT.strInt(l);
				String rawitem = l.replace(slot + "$:", "");
				if (rawitem.equals("air")) {
					inv.setItem(slot, ItemUT.createItem(Material.AIR));
				} else {
					ItemStack item = deserialize(rawitem);
					inv.setItem(slot, item);
				}
			} catch (Exception ex) {
				// MessageUT.cmessage("Error: String Item Corrupted");
				// MessageUT.cmessage(l);
			}
		}
	}
}
