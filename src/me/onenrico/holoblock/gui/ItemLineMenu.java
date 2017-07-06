package me.onenrico.holoblock.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.locale.Locales;
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
		String prefix = "ItemLineMenu."+name+".";
		ItemStack result = ItemUT.getItem(ConfigPlugin.getStr(prefix+"Material", "STONE").toUpperCase());
		ItemUT.changeDisplayName(result, ConfigPlugin.getStr(prefix+"Displayname", 
				"&6"+name+" &fName &cNot Configured !"));
		ItemUT.changeLore(result, ConfigPlugin.getStrList(prefix+"Description",
				ItemUT.createLore("&6"+name+" &fDescription &cNot Configured !")));
		return result;
	}
	public static void open(Player player,String rawloc,int page,int line) {
		setup();
		HoloData data = Datamanager.getDataByLoc(rawloc);
		if(!data.isAllowItemLine()) {
			SoundManager.playSound(player, "BLOCK_NOTE_PLING");
			MessageUT.plmessage(player, Locales.get("not_permitted"));
			return;
		}
		List<String> itemlines = ConfigPlugin.getStrList("LineItems", new ArrayList<>());
		int current = itemlines.size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = MathUT.clamp(maxpage, 1);
		PlaceholderUT pu = new PlaceholderUT();
		pu.add("page", ""+page);
		pu.add("nextpage", ""+(page + 1));
		pu.add("prevpage", ""+(page - 1));
		pu.add("maxpage", ""+maxpage);
		pu.add("player", ""+player.getName());
		pu.add("owner", ""+data.getOwner());
		String title = pu.t(ConfigPlugin.getStr("ItemLineMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		PrevPageItem = pu.t(PrevPageItem);
		NextPageItem = pu.t(NextPageItem);
		if(page > 1) {
			InventoryUT.setItem(inv, 45, PrevPageItem)
			.addClick("OpenPageItemLine:"+rawloc+":"+(page - 1)+":"+line); 
			int multiplier = 45 * (page - 1);
			current = current - multiplier;
			for(int x = 0;x < MathUT.clamp(current, 0, 45);x++) {
				int newx = (x+multiplier);
				String d = MessageUT.d(itemlines.get(newx));
				ItemStack ite = ItemUT.getItem(d.split("<")[0]);
				double cost = Double.parseDouble(d.split("<")[1]);
				String material = ""+ite.getType();
				pu.add("material", ""+ite.getType());
				pu.add("cost", ""+cost);
				ItemStack itemline = ItemLine.clone();
				itemline = pu.t(itemline);
				InventoryUT.setItem(inv, x, itemline)
				.addClick("ItemLine<i>"+rawloc+"<<"+cost+"<<"+material+"<<"+line);
			}
		}else {
			for(int x = 0;x < MathUT.clamp(current, 0, 45);x++) {
				String d = MessageUT.d(itemlines.get(x));
				ItemStack ite = ItemUT.getItem(d.split(">")[0]);
				double cost = Double.parseDouble(d.split(">")[1]);
				String material = d.split(">")[0];
				pu.add("material", ""+ite.getType());
				pu.add("cost", ""+cost);
				ItemStack itemline = ItemLine.clone();
				itemline.setType(ite.getType());
				itemline = ItemUT.changeData(itemline, ite.getDurability());
				itemline = pu.t(itemline);
				InventoryUT.setItem(inv, x, itemline)
				.addClick("ItemLine<i>"+rawloc+"<<"+cost+"<<"+material+"<<"+line);
			}
		}
		if(maxpage > 1) {
			if(page + 1 <= maxpage) {
				InventoryUT.setItem(inv, 53, NextPageItem)
				.addClick("OpenPageItemLine:"+rawloc+":"+(page + 1)+":"+line); 
			}
		}
		for(int x = 0;x<7;x++) {
			InventoryUT.setItem(inv, x + 46, CancelItem)
			.addClick("EditLineMenu:"+rawloc);
		}
		player.openInventory(inv);
	}
}
