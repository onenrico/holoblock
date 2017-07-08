package me.onenrico.holoblock.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.onenrico.holoblock.api.HoloBlockAPI;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.object.Seriloc;
import me.onenrico.holoblock.utils.InventoryUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MathUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class EditMemberMenu {

	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack AddMemberItem;
	private static ItemStack MemberItem;

	private static void setup() {
		PrevPageItem = setupItem("PrevPageItem");
		NextPageItem = setupItem("NextPageItem");
		AddMemberItem = setupItem("AddMemberItem");
		MemberItem = setupItem("MemberItem");
	}

	private static ItemStack setupItem(String name) {
		String prefix = "EditMemberMenu." + name + ".";
		ItemStack result = ItemUT.getItem(Core.getThis().guiconfig.getStr(prefix + "Material", "STONE").toUpperCase());
		ItemUT.changeDisplayName(result,
				Core.getThis().guiconfig.getStr(prefix + "Displayname", "&6" + name + " &fName &cNot Configured !"));
		ItemUT.changeLore(result, Core.getThis().guiconfig.getStrList(prefix + "Description",
				ItemUT.createLore("&6" + name + " &fDescription &cNot Configured !")));
		return result;
	}

	public static void open(Player player, String rawloc, int page) {
		setup();
		int max = HoloBlockAPI.getMaxMember(player, Seriloc.Deserialize(rawloc).getWorld());
		HoloData data = Datamanager.getDataByLoc(rawloc);
		List<String> temp = data.getMembers();
		List<String> members = new ArrayList<>();
		for (String a : temp) {
			if (!a.isEmpty()) {
				members.add(a);
			}
		}
		temp = null;
		int current = members.size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = MathUT.clamp(maxpage, 1);
		PlaceholderUT pu = new PlaceholderUT();
		pu.add("page", "" + page);
		pu.add("nextpage", "" + (page + 1));
		pu.add("prevpage", "" + (page - 1));
		pu.add("maxpage", "" + maxpage);
		pu.add("player", "" + player.getName());
		pu.add("owner", "" + data.getOwner());
		pu.add("members", "" + current);
		pu.add("maxmembers", "" + max);
		String title = pu.t(Core.getThis().guiconfig.getStr("EditMemberMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		PrevPageItem = pu.t(PrevPageItem);
		NextPageItem = pu.t(NextPageItem);
		AddMemberItem = pu.t(AddMemberItem);
		if (page > 1) {
			InventoryUT.setItem(inv, 45, PrevPageItem).addClick("OpenPageMember:" + rawloc + ":" + (page - 1));
			int multiplier = 45 * (page - 1);
			current = current - multiplier;
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				int newx = (x + multiplier);
				String membername = members.get(newx);
				pu.add("membername", membername);
				ItemStack member = MemberItem.clone();
				member = pu.t(member);
				if (member.getItemMeta() instanceof SkullMeta) {
					SkullMeta meta = (SkullMeta) member.getItemMeta();
					meta.setOwner(membername);
					member.setItemMeta(meta);
				}
				InventoryUT.setItem(inv, x, member)
						.addShiftRightClick("RemoveMemberMenu:" + rawloc + "<<" + (membername));
			}
		} else {
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				String membername = members.get(x);
				pu.add("membername", membername);
				ItemStack member = MemberItem.clone();
				member = pu.t(member);
				if (member.getItemMeta() instanceof SkullMeta) {
					SkullMeta meta = (SkullMeta) member.getItemMeta();
					meta.setOwner(membername);
					member.setItemMeta(meta);
				}
				InventoryUT.setItem(inv, x, member)
						.addShiftRightClick("RemoveMemberMenu:" + rawloc + "<<" + (membername));
			}
		}
		if (maxpage > 1) {
			if (page + 1 <= maxpage) {
				InventoryUT.setItem(inv, 53, NextPageItem).addClick("OpenPageMember:" + rawloc + ":" + (page + 1));
			}
		}
		for (int x = 0; x < 5; x++) {
			InventoryUT.setItem(inv, x + 47, AddMemberItem).addClick("AddMember:" + rawloc);
		}
		player.openInventory(inv);
	}
}