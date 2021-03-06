package me.onenrico.holoblock.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.utils.InventoryUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class ShopMenu {
	private static ItemStack HoloItem;

	private static void setup() {
		HoloItem = setupItem("HoloItem");
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

	public static void open(Player player) {
		if(first) {
			setup();
		}
		SoundManager.playSound(player, "ENTITY_PLAYER_BURP");
		String title = Core.getThis().guiconfig.getStr("ShopMenu.Title", "&1&lHolo Shop");
		Inventory inv = InventoryUT.createInventory(3, title);
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("itemname", Core.getThis().configplugin.getStr("holo.item.displayname", "&cNot Configured"));
		double cost = Core.getThis().configplugin.getDouble("holo.item.cost", 1000);
		pu.add("cost", "" + cost);
		ItemStack tempHoloItem = pu.t(HoloItem.clone());
		InventoryUT.setItem(inv, 13, tempHoloItem).addClick("Buy:" + cost);
		player.openInventory(inv);
	}
}
