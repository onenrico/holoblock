package me.onenrico.holoblock.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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

public class AddMemberMenu {
	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack AlreadyMember;
	private static ItemStack PlayerItem;
	private static ItemStack CancelItem;

	private static void setup() {
		PrevPageItem = setupItem("PrevPageItem");
		NextPageItem = setupItem("NextPageItem");
		AlreadyMember = setupItem("AlreadyMember");
		PlayerItem = setupItem("PlayerItem");
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
	public static void open(Player player, String rawloc, int page) {
		if(first) {
			setup();
		}
		HoloData data = Datamanager.getDataByLoc(rawloc);
		if (!data.getOwner().equals(player.getName())) {
			MessageUT.plmessage(player, ConfigPlugin.locale.getValue("not_permitted"));
			SoundManager.playSound(player, "BLOCK_NOTE_PLING");
			return;
		}
		Collection<? extends Player> online = Bukkit.getServer().getOnlinePlayers();
		List<String> players = new ArrayList<>();
		List<String> members = data.getMembers();
		for (Player p : online) {
			players.add(p.getName());
		}
		int current = players.size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = MathUT.clamp(maxpage, 1);
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("page", "" + page);
		pu.add("nextpage", "" + (page + 1));
		pu.add("prevpage", "" + (page - 1));
		pu.add("maxpage", "" + maxpage);
		pu.add("player", "" + player.getName());
		pu.add("owner", "" + data.getOwner());
		pu.add("onlines", "" + current);
		String title = pu.t(Core.getThis().guiconfig.getStr("AddMemberMenu.Title", "Title &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		ItemStack tempPrevPageItem = pu.t(PrevPageItem.clone());
		ItemStack tempNextPageItem = pu.t(NextPageItem.clone());
		ItemStack tempCancelItem = pu.t(CancelItem.clone());
		if (page > 1) {
			InventoryUT.setItem(inv, 45, tempPrevPageItem).addClick("OpenPagePlayer:" + rawloc + ":" + (page - 1));
			int multiplier = 45 * (page - 1);
			current = current - multiplier;
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				int newx = (x + multiplier);
				String playername = MessageUT.d(players.get(newx));
				String owner = MessageUT.d(data.getOwner());
				pu.add("playername", playername);
				if (members.contains(playername) || owner.equals(playername)) {
					ItemStack amember = AlreadyMember.clone();
					amember = pu.t(amember);
					amember = ItemUT.setGlowing(amember, true);
					if (amember.getItemMeta() instanceof SkullMeta) {
						SkullMeta meta = (SkullMeta) amember.getItemMeta();
						meta.setOwner(playername);
						amember.setItemMeta(meta);
					}
					InventoryUT.setItem(inv, x, amember);
				} else {
					ItemStack member = PlayerItem.clone();
					member = pu.t(member);
					if (member.getItemMeta() instanceof SkullMeta) {
						SkullMeta meta = (SkullMeta) member.getItemMeta();
						meta.setOwner(playername);
						member.setItemMeta(meta);
					}
					InventoryUT.setItem(inv, x, member).addClick("PlusMember:" + rawloc + "<<" + (playername));
				}
			}
		} else {
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				String playername = MessageUT.d(players.get(x));
				String owner = MessageUT.d(data.getOwner());
				pu.add("playername", playername);
				if (members.contains(playername) || owner.equals(playername)) {
					ItemStack amember = AlreadyMember.clone();
					amember = pu.t(amember);
					amember = ItemUT.setGlowing(amember, true);
					if (amember.getItemMeta() instanceof SkullMeta) {
						SkullMeta meta = (SkullMeta) amember.getItemMeta();
						meta.setOwner(playername);
						amember.setItemMeta(meta);
					}
					InventoryUT.setItem(inv, x, amember);
				} else {
					ItemStack member = PlayerItem.clone();
					member = pu.t(member);
					if (member.getItemMeta() instanceof SkullMeta) {
						SkullMeta meta = (SkullMeta) member.getItemMeta();
						meta.setOwner(playername);
						member.setItemMeta(meta);
					}
					InventoryUT.setItem(inv, x, member).addClick("PlusMember:" + rawloc + "<<" + (playername));
				}
			}
		}
		if (maxpage > 1) {
			if (page + 1 <= maxpage) {
				InventoryUT.setItem(inv, 53, tempNextPageItem).addClick("OpenPagePlayer:" + rawloc + ":" + (page + 1));
			}
		}
		for (int x = 0; x < 5; x++) {
			InventoryUT.setItem(inv, x + 47, tempCancelItem).addClick("EditMemberMenu:" + rawloc);
		}
		player.openInventory(inv);
	}
}
