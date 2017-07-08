package me.onenrico.holoblock.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.utils.InventoryUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class RemoveMemberMenu {

	private static ItemStack YesItem;
	private static ItemStack NoItem;

	private static void setup() {
		YesItem = setupItem("YesItem");
		NoItem = setupItem("NoItem");
	}

	private static ItemStack setupItem(String name) {
		String prefix = "RemoveMemberMenu." + name + ".";
		ItemStack result = ItemUT.getItem(Core.getThis().guiconfig.getStr(prefix + "Material", "STONE").toUpperCase());
		ItemUT.changeDisplayName(result,
				Core.getThis().guiconfig.getStr(prefix + "Displayname", "&6" + name + " &fName &cNot Configured !"));
		ItemUT.changeLore(result, Core.getThis().guiconfig.getStrList(prefix + "Description",
				ItemUT.createLore("&6" + name + " &fDescription &cNot Configured !")));
		return result;
	}

	public static void open(Player player, String rawloc, String member) {
		setup();
		HoloData data = Datamanager.getDataByLoc(rawloc);
		if (!data.getOwner().equals(player.getName())) {
			MessageUT.plmessage(player, ConfigPlugin.locale.getValue("not_permitted"));
			SoundManager.playSound(player, "BLOCK_NOTE_PLING");
			return;
		}
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("member", "" + member);
		String title = pu.t(Core.getThis().guiconfig.getStr("RemoveMemberMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		YesItem = pu.t(YesItem);
		NoItem = pu.t(NoItem);
		for (int r = 0; r < 6; r++) {
			for (int x = 0; x < 4; x++) {
				if (r == 0) {
					InventoryUT.setItem(inv, x, YesItem).addClick("MinusMember:" + rawloc + "<<" + member);
				} else {
					int y = x + (r * 9);
					InventoryUT.setItem(inv, y, YesItem).addClick("MinusMember:" + rawloc + "<<" + member);
				}
			}
			for (int x = 5; x < 9; x++) {
				if (r == 0) {
					InventoryUT.setItem(inv, x, NoItem).addClick("EditMemberMenu:" + rawloc);
				} else {
					int y = x + (r * 9);
					InventoryUT.setItem(inv, y, NoItem).addClick("EditMemberMenu:" + rawloc);
				}
			}
		}
		player.openInventory(inv);
	}
}