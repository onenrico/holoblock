package me.onenrico.holoblock.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.utils.InventoryUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class AddLineMenu {
	private static ItemStack ItemLine;
	private static ItemStack TextLine;
	private static ItemStack CancelItem;

	private static void setup() {
		ItemLine = setupItem("ItemLine");
		TextLine = setupItem("TextLine");
		CancelItem = setupItem("CancelItem");
	}

	private static ItemStack setupItem(String name) {
		String prefix = "AddLineMenu." + name + ".";
		ItemStack result = ItemUT.getItem(Core.getThis().guiconfig.getStr(prefix + "Material", "STONE").toUpperCase());
		ItemUT.changeDisplayName(result,
				Core.getThis().guiconfig.getStr(prefix + "Displayname", "&6" + name + " &fName &cNot Configured !"));
		ItemUT.changeLore(result, Core.getThis().guiconfig.getStrList(prefix + "Description",
				ItemUT.createLore("&6" + name + " &fDescription &cNot Configured !")));
		return result;
	}

	public static void open(Player player, String rawloc) {
		setup();
		HoloData data = Datamanager.getDataByLoc(rawloc);
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("player", "" + player.getName());
		pu.add("owner", "" + data.getOwner());
		CancelItem = pu.t(CancelItem);
		TextLine = pu.t(TextLine);
		ItemLine = pu.t(ItemLine);
		String title = pu.t(Core.getThis().guiconfig.getStr("AddLineMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(2, title);
		InventoryUT.setItem(inv, 2, TextLine).addClick("AddLine:" + rawloc + "<<" + (data.getLines().size()));
		InventoryUT.setItem(inv, 6, ItemLine).addClick("ItemLineMenu:" + rawloc + "<<" + (data.getLines().size()));
		InventoryUT.setItem(inv, 13, CancelItem).addClick("EditLineMenu:" + rawloc);
		player.openInventory(inv);
	}
}
