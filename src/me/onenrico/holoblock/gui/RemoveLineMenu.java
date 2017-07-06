package me.onenrico.holoblock.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.utils.InventoryUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class RemoveLineMenu {

	private static ItemStack YesItem;
	private static ItemStack NoItem;

	private static void setup() {
		YesItem = setupItem("YesItem");
		NoItem = setupItem("NoItem");
	}

	private static ItemStack setupItem(String name) {
		String prefix = "RemoveLineMenu." + name + ".";
		ItemStack result = ItemUT.getItem(ConfigPlugin.getStr(prefix + "Material", "STONE").toUpperCase());
		ItemUT.changeDisplayName(result,
				ConfigPlugin.getStr(prefix + "Displayname", "&6" + name + " &fName &cNot Configured !"));
		ItemUT.changeLore(result, ConfigPlugin.getStrList(prefix + "Description",
				ItemUT.createLore("&6" + name + " &fDescription &cNot Configured !")));
		return result;
	}

	public static void open(Player player, String rawloc, int line) {
		setup();
		PlaceholderUT pu = new PlaceholderUT();
		pu.add("line", "" + line);
		String title = pu.t(ConfigPlugin.getStr("RemoveLineMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		YesItem = pu.t(YesItem);
		NoItem = pu.t(NoItem);
		for (int r = 0; r < 6; r++) {
			for (int x = 0; x < 4; x++) {
				if (r == 0) {
					InventoryUT.setItem(inv, x, YesItem).addClick("RemoveLine:" + rawloc + "<<" + line);
				} else {
					int y = x + (r * 9);
					InventoryUT.setItem(inv, y, YesItem).addClick("RemoveLine:" + rawloc + "<<" + line);
				}
			}
			for (int x = 5; x < 9; x++) {
				if (r == 0) {
					InventoryUT.setItem(inv, x, NoItem).addClick("EditLineMenu:" + rawloc);
				} else {
					int y = x + (r * 9);
					InventoryUT.setItem(inv, y, NoItem).addClick("EditLineMenu:" + rawloc);
				}
			}
		}
		player.openInventory(inv);
	}
}