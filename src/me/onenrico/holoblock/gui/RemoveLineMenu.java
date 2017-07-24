package me.onenrico.holoblock.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
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
		first = false;
		String prefix = "AddLineMenu." + name + ".";
		ItemStack result = ItemUT.getItem(Core.getThis().guiconfig.getStr(prefix + "Material", "STONE").toUpperCase());
		ItemUT.changeDisplayName(result,
				Core.getThis().guiconfig.getStr(prefix + "Displayname", "&6" + name + " &fName &cNot Configured !"));
		ItemUT.changeLore(result, Core.getThis().guiconfig.getStrList(prefix + "Description",
				ItemUT.createLore("&6" + name + " &fDescription &cNot Configured !")));
		return result;
	}
	private static Boolean first = true;
	
	public static void open(Player player, String rawloc, int line) {
		if(first) {
			setup();
		}
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("line", "" + line);
		String title = pu.t(Core.getThis().guiconfig.getStr("RemoveLineMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		ItemStack tempYesItem = pu.t(YesItem.clone());
		ItemStack tempNoItem = pu.t(NoItem.clone());
		for (int r = 0; r < 6; r++) {
			for (int x = 0; x < 4; x++) {
				if (r == 0) {
					InventoryUT.setItem(inv, x, tempYesItem).addClick("RemoveLine:" + rawloc + "<<" + line);
				} else {
					int y = x + (r * 9);
					InventoryUT.setItem(inv, y, tempYesItem).addClick("RemoveLine:" + rawloc + "<<" + line);
				}
			}
			for (int x = 5; x < 9; x++) {
				if (r == 0) {
					InventoryUT.setItem(inv, x, tempNoItem).addClick("EditLineMenu:" + rawloc);
				} else {
					int y = x + (r * 9);
					InventoryUT.setItem(inv, y, tempNoItem).addClick("EditLineMenu:" + rawloc);
				}
			}
		}
		player.openInventory(inv);
	}
}