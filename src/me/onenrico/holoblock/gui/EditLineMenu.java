package me.onenrico.holoblock.gui;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.onenrico.holoblock.api.HoloBlockAPI;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.object.Seriloc;
import me.onenrico.holoblock.utils.InventoryUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MathUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class EditLineMenu {

	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack AddLineItem;
	private static ItemStack LineItem;

	private static void setup() {
		PrevPageItem = setupItem("PrevPageItem");
		NextPageItem = setupItem("NextPageItem");
		AddLineItem = setupItem("AddLineItem");
		LineItem = setupItem("LineItem");
	}

	private static ItemStack setupItem(String name) {
		String prefix = "EditLineMenu." + name + ".";
		ItemStack result = ItemUT.getItem(Core.getThis().guiconfig.getStr(prefix + "Material", "STONE").toUpperCase());
		ItemUT.changeDisplayName(result,
				Core.getThis().guiconfig.getStr(prefix + "Displayname", "&6" + name + " &fName &cNot Configured !"));
		ItemUT.changeLore(result, Core.getThis().guiconfig.getStrList(prefix + "Description",
				ItemUT.createLore("&6" + name + " &fDescription &cNot Configured !")));
		return result;
	}

	public static void open(Player player, String rawloc, int page) {
		setup();
		int max = HoloBlockAPI.getMaxLine(player, Seriloc.Deserialize(rawloc).getWorld());
		HoloData data = Datamanager.getDataByLoc(rawloc);
		List<String> lines = data.getLines();
		int current = lines.size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = MathUT.clamp(maxpage, 1);
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("page", "" + page);
		pu.add("nextpage", "" + (page + 1));
		pu.add("prevpage", "" + (page - 1));
		pu.add("maxpage", "" + maxpage);
		pu.add("player", "" + player.getName());
		pu.add("owner", "" + data.getOwner());
		pu.add("lines", "" + current);
		pu.add("maxlines", "" + max);
		String title = pu.t(Core.getThis().guiconfig.getStr("EditLineMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		PrevPageItem = pu.t(PrevPageItem);
		NextPageItem = pu.t(NextPageItem);
		AddLineItem = pu.t(AddLineItem);
		if (page > 1) {
			InventoryUT.setItem(inv, 45, PrevPageItem).addClick("OpenPage:" + rawloc + ":" + (page - 1));
			int multiplier = 45 * (page - 1);
			current = current - multiplier;
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				int newx = (x + multiplier);
				pu.add("line", "0" + (newx + 1));
				String content = "&r" + lines.get(newx);
				if (content.contains("$ItemStack:")) {
					content = content.replace("$ItemStack:", "&rIcon:&6");
				}
				pu.add("content", content);
				ItemStack line = LineItem.clone();
				line = pu.t(line);
				line.setAmount(x + 1);
				InventoryUT.setItem(inv, x, line).addLeftClick("EditLine:" + rawloc + "<<" + (newx))
						.addShiftRightClick("RemoveLineMenu:" + rawloc + "<<" + (newx))
						.addRightClick("MoveLineMenu:" + rawloc + ":" + (newx));
			}
		} else {
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				pu.add("line", "0" + (x + 1));
				String content = "&r" + lines.get(x);
				if (content.contains("$ItemStack:")) {
					content = content.replace("$ItemStack:", "&rIcon:&6");
				}
				pu.add("content", content);
				ItemStack line = LineItem.clone();
				line = pu.t(line);
				line.setAmount(x + 1);
				InventoryUT.setItem(inv, x, line).addLeftClick("EditLine:" + rawloc + "<<" + (x))
						.addShiftRightClick("RemoveLineMenu:" + rawloc + "<<" + (x))
						.addRightClick("MoveLineMenu:" + rawloc + ":" + (x));
			}
		}
		if (maxpage > 1) {
			if (page + 1 <= maxpage) {
				InventoryUT.setItem(inv, 53, NextPageItem).addClick("OpenPage:" + rawloc + ":" + (page + 1));
			}
		}
		for (int x = 0; x < 5; x++) {
			InventoryUT.setItem(inv, x + 47, AddLineItem).addClick("AddLineMenu:" + rawloc);
		}
		player.openInventory(inv);
	}
}