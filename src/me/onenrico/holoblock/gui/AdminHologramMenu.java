package me.onenrico.holoblock.gui;

import java.util.ArrayList;
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
import me.onenrico.holoblock.utils.InventoryUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MathUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class AdminHologramMenu {

	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack PlayerItem;

	private static void setup() {
		PrevPageItem = setupItem("PrevPageItem");
		NextPageItem = setupItem("NextPageItem");
		PlayerItem = setupItem("PlayerItem");
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
	public static void open(Player player, int page) {
		if(first) {
			setup();
		}
		List<String> members = new ArrayList<>();
		members.addAll(Datamanager.loadedOwner);
		int current = members.size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = MathUT.clamp(maxpage, 1);
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("page", "" + page);
		pu.add("nextpage", "" + (page + 1));
		pu.add("prevpage", "" + (page - 1));
		pu.add("maxpage", "" + maxpage);
		pu.add("player", "" + player.getName());
		String title = pu.t(Core.getThis().guiconfig.getStr("AdminHologramMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		ItemStack tempPrevPageItem = pu.t(PrevPageItem.clone());
		ItemStack tempNextPageItem = pu.t(NextPageItem.clone());
		World world = Bukkit.getWorlds().get(0);
		if (page > 1) {
			InventoryUT.setItem(inv, 45, tempPrevPageItem).addClick("OpenPageAdmin:" + (page - 1));
			int multiplier = 45 * (page - 1);
			current = current - multiplier;
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				int newx = (x + multiplier);
				String membername = members.get(newx);
				OfflinePlayer ofc = Bukkit.getOfflinePlayer(membername);
				pu.add("playername", membername);
				pu.add("owned", "" + Datamanager.getDB().getOwned(membername));
				pu.add("maxowned", "" + HoloBlockAPI.getMaxOwned(ofc, world));
				pu.add("maxlines", "" + HoloBlockAPI.getMaxLine(ofc, world));
				pu.add("maxmembers", "" + HoloBlockAPI.getMaxMember(ofc, world));
				pu.add("placeholder", "" + HoloBlockAPI.isAllowPlaceholder(ofc, world));
				pu.add("color", "" + HoloBlockAPI.isAllowColor(ofc, world));
				pu.add("customskin", "" + HoloBlockAPI.isAllowCustomSkin(ofc, world));
				pu.add("itemline", "" + HoloBlockAPI.isAllowItemLine(ofc, world));
				ItemStack member = PlayerItem.clone();
				member = pu.t(member);
				if (member.getItemMeta() instanceof SkullMeta) {
					SkullMeta meta = (SkullMeta) member.getItemMeta();
					meta.setOwner(membername);
					member.setItemMeta(meta);
				}
				InventoryUT.setItem(inv, x, member).addClick("ManageHologramMenu:" + (membername));
			}
		} else {
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				String membername = members.get(x);
				OfflinePlayer ofc = Bukkit.getOfflinePlayer(membername);
				pu.add("playername", membername);
				pu.add("owned", "" + Datamanager.getDB().getOwned(membername));
				pu.add("maxowned", "" + HoloBlockAPI.getMaxOwned(ofc, world));
				pu.add("maxlines", "" + HoloBlockAPI.getMaxLine(ofc, world));
				pu.add("maxmembers", "" + HoloBlockAPI.getMaxMember(ofc, world));
				pu.add("placeholder", "" + HoloBlockAPI.isAllowPlaceholder(ofc, world));
				pu.add("color", "" + HoloBlockAPI.isAllowColor(ofc, world));
				pu.add("customskin", "" + HoloBlockAPI.isAllowCustomSkin(ofc, world));
				pu.add("itemline", "" + HoloBlockAPI.isAllowItemLine(ofc, world));
				ItemStack member = PlayerItem.clone();
				member = pu.t(member);
				if (member.getItemMeta() instanceof SkullMeta) {
					SkullMeta meta = (SkullMeta) member.getItemMeta();
					meta.setOwner(membername);
					member.setItemMeta(meta);
				}
				InventoryUT.setItem(inv, x, member).addClick("ManageHologramMenu:" + (membername));
			}
		}
		if (maxpage > 1) {
			if (page + 1 <= maxpage) {
				InventoryUT.setItem(inv, 53, tempNextPageItem).addClick("OpenPageAdmin:" + (page + 1));
			}
		}
		player.openInventory(inv);
	}
}