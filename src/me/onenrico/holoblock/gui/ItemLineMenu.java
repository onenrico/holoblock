package me.onenrico.holoblock.gui;

import java.util.ArrayList;
import java.util.List;

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
import me.onenrico.holoblock.utils.MathUT;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class ItemLineMenu {
	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack ItemLine;
	private static ItemStack CancelItem;

	private static void setup() {
		PrevPageItem = setupItem("PrevPageItem");
		NextPageItem = setupItem("NextPageItem");
		ItemLine = setupItem("ItemLine");
		CancelItem = setupItem("CancelItem");
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
	public static void open(Player player, String rawloc, int page, int line) {
		if(first) {
			setup();
		}
		HoloData data = Datamanager.getDataByLoc(rawloc);
		if (!data.isAllowItemLine()) {
			SoundManager.playSound(player, "BLOCK_NOTE_PLING");
			MessageUT.plmessage(player, ConfigPlugin.locale.getValue("not_permitted"));
			return;
		}
		List<String> itemlines = Core.getThis().configplugin.getStrList("LineItems", new ArrayList<>());
		int current = itemlines.size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = MathUT.clamp(maxpage, 1);
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("page", "" + page);
		pu.add("nextpage", "" + (page + 1));
		pu.add("prevpage", "" + (page - 1));
		pu.add("maxpage", "" + maxpage);
		pu.add("player", "" + player.getName());
		pu.add("owner", "" + data.getOwner());
		String title = pu.t(Core.getThis().guiconfig.getStr("ItemLineMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		ItemStack tempPrevPageItem = pu.t(PrevPageItem.clone());
		ItemStack tempNextPageItem = pu.t(NextPageItem.clone());
		ItemStack tempCancelItem = pu.t(CancelItem.clone());
		if (page > 1) {
			InventoryUT.setItem(inv, 45, tempPrevPageItem)
					.addClick("OpenPageItemLine:" + rawloc + ":" + (page - 1) + ":" + line);
			int multiplier = 45 * (page - 1);
			current = current - multiplier;
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				int newx = (x + multiplier);
				String d = MessageUT.d(itemlines.get(newx));
				ItemStack ite = ItemUT.getItem(d.split(">")[0]);
				double cost = Double.parseDouble(d.split(">")[1]);
				String material = d.split(">")[0];
				pu.add("material", "" + ite.getType());
				pu.add("cost", "" + cost);
				ItemStack itemline = ItemLine.clone();
				itemline.setType(ite.getType());
				itemline = ItemUT.changeData(itemline, ite.getDurability());
				itemline = pu.t(itemline);
				InventoryUT.setItem(inv, x, itemline)
						.addClick("ItemLine<i>" + rawloc + "<<" + cost + "<<" + material + "<<" + line);
			}
		} else {
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				String d = MessageUT.d(itemlines.get(x));
				ItemStack ite = ItemUT.getItem(d.split(">")[0]);
				double cost = Double.parseDouble(d.split(">")[1]);
				String material = d.split(">")[0];
				pu.add("material", "" + ite.getType());
				pu.add("cost", "" + cost);
				ItemStack itemline = ItemLine.clone();
				itemline.setType(ite.getType());
				itemline = ItemUT.changeData(itemline, ite.getDurability());
				itemline = pu.t(itemline);
				InventoryUT.setItem(inv, x, itemline)
						.addClick("ItemLine<i>" + rawloc + "<<" + cost + "<<" + material + "<<" + line);
			}
		}
		if (maxpage > 1) {
			if (page + 1 <= maxpage) {
				InventoryUT.setItem(inv, 53, tempNextPageItem)
						.addClick("OpenPageItemLine:" + rawloc + ":" + (page + 1) + ":" + line);
			}
		}
		for (int x = 0; x < 5; x++) {
			InventoryUT.setItem(inv, x + 47, tempCancelItem).addClick("EditLineMenu:" + rawloc);
		}
		player.openInventory(inv);
	}
}
