package me.onenrico.holoblock.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.onenrico.holoblock.api.HoloBlockAPI;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.utils.InventoryUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MathUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class ManageHologramMenu {

	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack HoloItem;

	private static void setup() {
		PrevPageItem = setupItem("PrevPageItem");
		NextPageItem = setupItem("NextPageItem");
		HoloItem = setupItem("HoloItem");
	}

	private static ItemStack setupItem(String name) {
		String prefix = "ManageHologramMenu." + name + ".";
		ItemStack result = ItemUT.getItem(Core.getThis().guiconfig.getStr(prefix + "Material", "STONE").toUpperCase());
		ItemUT.changeDisplayName(result,
				Core.getThis().guiconfig.getStr(prefix + "Displayname", "&6" + name + " &fName &cNot Configured !"));
		ItemUT.changeLore(result, Core.getThis().guiconfig.getStrList(prefix + "Description",
				ItemUT.createLore("&6" + name + " &fDescription &cNot Configured !")));
		return result;
	}

	@SuppressWarnings("deprecation")
	public static void open(Player player, String target, int page) {
		setup();
		List<String> holos = Datamanager.getDB().getHoloFrom(target);
		int current = holos.size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = MathUT.clamp(maxpage, 1);
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("playername", "" + target);
		pu.add("page", "" + page);
		pu.add("nextpage", "" + (page + 1));
		pu.add("prevpage", "" + (page - 1));
		pu.add("maxpage", "" + maxpage);
		pu.add("player", "" + player.getName());
		String title = pu.t(Core.getThis().guiconfig.getStr("ManageHologramMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		PrevPageItem = pu.t(PrevPageItem);
		NextPageItem = pu.t(NextPageItem);
		World world = Bukkit.getWorlds().get(0);
		if (page > 1) {
			InventoryUT.setItem(inv, 45, PrevPageItem).addClick("OpenPageHolo:" + (page - 1));
			int multiplier = 45 * (page - 1);
			current = current - multiplier;
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				int newx = (x + multiplier);
				String rawloc = holos.get(newx);
				HoloData data = Datamanager.getDataByLoc(rawloc);
				OfflinePlayer ofc = Bukkit.getOfflinePlayer(data.getOwner());
				pu.add("skin", data.getSkin());
				pu.add("members", "" + data.getMembers().size());
				pu.add("lines", "" + data.getLines().size());
				pu.add("maxlines", "" + HoloBlockAPI.getMaxLine(ofc, world));
				pu.add("maxmembers", "" + HoloBlockAPI.getMaxMember(ofc, world));
				pu.add("offset", "" + data.getOffset());
				ItemStack holo = HoloItem.clone();
				holo = pu.t(holo);
				if (holo.getItemMeta() instanceof SkullMeta) {
					SkullMeta meta = (SkullMeta) holo.getItemMeta();
					meta.setOwner(data.getSkin());
					holo.setItemMeta(meta);
				}
				InventoryUT.setItem(inv, x, holo).addLeftClick("MainMenu:" + (rawloc))
						.addRightClick("Teleport:" + (rawloc));
			}
		} else {
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				String rawloc = holos.get(x);
				HoloData data = Datamanager.getDataByLoc(rawloc);
				OfflinePlayer ofc = Bukkit.getOfflinePlayer(data.getOwner());
				pu.add("skin", data.getSkin());
				pu.add("members", "" + data.getMembers().size());
				pu.add("lines", "" + data.getLines().size());
				pu.add("maxlines", "" + HoloBlockAPI.getMaxLine(ofc, world));
				pu.add("maxmembers", "" + HoloBlockAPI.getMaxMember(ofc, world));
				pu.add("offset", "" + data.getOffset());
				ItemStack holo = HoloItem.clone();
				holo = pu.t(holo);
				if (holo.getItemMeta() instanceof SkullMeta) {
					SkullMeta meta = (SkullMeta) holo.getItemMeta();
					meta.setOwner(data.getSkin());
					holo.setItemMeta(meta);
				}
				InventoryUT.setItem(inv, x, holo).addLeftClick("MainMenu:" + (rawloc))
						.addRightClick("Teleport:" + (rawloc));
			}
		}
		if (maxpage > 1) {
			if (page + 1 <= maxpage) {
				InventoryUT.setItem(inv, 53, NextPageItem).addClick("OpenPageHolo:" + (page + 1));
			}
		}
		player.openInventory(inv);
	}
}