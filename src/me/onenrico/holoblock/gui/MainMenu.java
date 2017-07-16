package me.onenrico.holoblock.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.api.HoloBlockAPI;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.events.CloseEvent;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.object.CustomSkin;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.object.Seriloc;
import me.onenrico.holoblock.utils.InventoryUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class MainMenu {
	private static ItemStack EditLineItem;
	private static ItemStack EditOffSetItem;
	private static ItemStack EditSkinItem;
	private static ItemStack EditMemberItem;
	private static ItemStack EditInfoItem;

	private static void setup() {
		EditInfoItem = setupItem("InfoItem");
		EditLineItem = setupItem("LineItem");
		EditOffSetItem = setupItem("OffSetItem");
		EditSkinItem = setupItem("SkinItem");
		EditMemberItem = setupItem("MemberItem");
	}

	private static ItemStack setupItem(String name) {
		String prefix = "MainMenu." + name + ".";
		ItemStack result = ItemUT.getItem(Core.getThis().guiconfig.getStr(prefix + "Material", "STONE").toUpperCase());
		ItemUT.changeDisplayName(result,
				Core.getThis().guiconfig.getStr(prefix + "Displayname", "&6" + name + " &fName &cNot Configured !"));
		ItemUT.changeLore(result, Core.getThis().guiconfig.getStrList(prefix + "Description",
				ItemUT.createLore("&6" + name + " &fDescription &cNot Configured !")));
		return result;
	}

	public static List<Player> animation = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public static void open(Player player, String rawloc) {
		setup();
		Inventory inv = InventoryUT.createInventory(3,
				Core.getThis().guiconfig.getStr("MainMenu.Title", "&c&lNot Configured !"));
		if (!animation.contains(player)) {
			animation.add(player);
		}

		CloseEvent.mainMenuPlayers.remove(player);
		player.removeMetadata("EditLine:", Core.getThis());
		player.removeMetadata("EditOffset:", Core.getThis());
		player.removeMetadata("EditSkin:", Core.getThis());
		player.removeMetadata("AddLine:", Core.getThis());
		player.removeMetadata("MoveLine:", Core.getThis());

		new BukkitRunnable() {
			int index = 0;
			Boolean reverse = false;
			String item = "DIAMOND";
			Random r = new Random();
			String[] items = { "DIAMOND", "EMERALD", "REDSTONE", "COAL", "GOLD_INGOT", "IRON_INGOT" };
			int data = 0;

			@Override
			public void run() {
				if (!animation.contains(player)) {
					cancel();
					return;
				}
				for (int x = 0; x < inv.getSize(); x++) {
					if (x != 4 && x != 11 && x != 13 && x != 15 && x != 22) {
						if (data > 15) {
							data = 0;
						}
						InventoryUT.setItem(inv, x, ItemUT.changeDisplayName(ItemUT.getItem("160:" + data), "&r"));
						data++;
					}
				}
				if (reverse) {
					if (index <= 0) {
						item = items[r.nextInt(items.length)];
						InventoryUT.setItem(inv, index, ItemUT.changeDisplayName(ItemUT.getItem(item), "&a&l>>>"));
						reverse = false;
					} else {
						if (index != 4 && index != 11 && index != 13 && index != 15 && index != 22) {
							InventoryUT.setItem(inv, index, ItemUT.changeDisplayName(ItemUT.getItem(item), "&b&l<<<"));
						} else {
							item = items[r.nextInt(items.length)];
						}
						index--;
					}
				} else {
					if (index >= inv.getSize()) {
						item = items[r.nextInt(items.length)];
						reverse = true;
						index--;
						InventoryUT.setItem(inv, index, ItemUT.changeDisplayName(ItemUT.getItem(item), "&b&l<<<"));
					} else {
						if (index != 4 && index != 11 && index != 13 && index != 15 && index != 22) {
							InventoryUT.setItem(inv, index, ItemUT.changeDisplayName(ItemUT.getItem(item), "&a&l>>>"));
						} else {
							item = items[r.nextInt(items.length)];
						}
						index++;
					}
				}

			}
		}.runTaskTimer(Core.getThis(), 0, 8);
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		HoloData data = Datamanager.getDataByLoc(rawloc);
		String owner = data.getOwner();
		Location loc = Seriloc.Deserialize(rawloc);
		pu.add("owner", owner);
		List<String> temp = data.getMembers();
		List<String> members = new ArrayList<>();
		for (String a : temp) {
			if (!a.isEmpty()) {
				members.add(a);
			}
		}
		temp = null;
		int current = members.size();
		pu.add("members", "" + current);
		pu.add("lines", "" + data.getLines().size());
		pu.add("maxlines", "" + HoloBlockAPI.getMaxLine(Bukkit.getOfflinePlayer(owner), loc.getWorld()));
		pu.add("maxmembers", "" + HoloBlockAPI.getMaxMember(Bukkit.getOfflinePlayer(owner), loc.getWorld()));
		pu.add("offset", "" + data.getOffset());
		pu.add("color", "" + data.isAllowColor());
		pu.add("placeholder", "" + data.isAllowPlaceholders());
		pu.add("itemline", "" + data.isAllowItemLine());
		pu.add("customskin", "" + data.isAllowCustomSkin());
		String skin = data.getSkin();
		Boolean custom = false;
		if (skin.startsWith("$CustomSkin:")) {
			custom = true;
			skin = skin.replace("$CustomSkin:", "");
		}
		if (custom) {
			pu.add("skin", "&7<&fCustom:&e" + skin + "&7>");
		} else {
			pu.add("skin", "&e" + skin);
		}
		EditInfoItem = pu.t(EditInfoItem);
		EditLineItem = pu.t(EditLineItem);
		EditOffSetItem = pu.t(EditOffSetItem);
		EditSkinItem = pu.t(EditSkinItem);
		EditMemberItem = pu.t(EditMemberItem);
		if (EditInfoItem.getItemMeta() instanceof SkullMeta) {
			SkullMeta meta = (SkullMeta) EditInfoItem.getItemMeta();
			meta.setOwner(owner);
			EditInfoItem.setItemMeta(meta);
		}
		if (EditSkinItem.getItemMeta() instanceof SkullMeta) {
			SkullMeta meta = (SkullMeta) EditSkinItem.getItemMeta();
			if (custom) {
				CustomSkin cs = new CustomSkin(skin);
				ItemStack customskin = null;
				switch (cs.getType().toLowerCase()) {
				case "name":
					meta.setOwner(cs.getData());
					EditSkinItem.setItemMeta(meta);
					break;
				case "url":
					customskin = cs.getSkullitem();
					customskin = ItemUT.changeDisplayName(customskin, pu.t(ItemUT.getName(EditSkinItem)));
					customskin = ItemUT.changeLore(customskin, pu.t(ItemUT.getLore(EditSkinItem)));
					EditSkinItem = customskin;
					break;
				case "encode":
					customskin = cs.getSkullitem();
					customskin = ItemUT.changeDisplayName(customskin, pu.t(ItemUT.getName(EditSkinItem)));
					customskin = ItemUT.changeLore(customskin, pu.t(ItemUT.getLore(EditSkinItem)));
					EditSkinItem = customskin;
					break;
				}
			}
			if (!custom) {
				meta.setOwner(skin);
				EditSkinItem.setItemMeta(meta);
			}
		}
		InventoryUT.setItem(inv, 4, EditInfoItem).addClick("Refresh:" + rawloc);
		InventoryUT.setItem(inv, 11, EditLineItem).addClick("EditLineMenu:" + rawloc);
		InventoryUT.setItem(inv, 13, EditOffSetItem).addClick("EditOffSet:" + rawloc);
		InventoryUT.setItem(inv, 15, EditMemberItem).addClick("EditMemberMenu:" + rawloc);
		InventoryUT.setItem(inv, 22, EditSkinItem).addRightClick("EditSkin:" + rawloc)
				.addLeftClick("SkinMenu:" + rawloc);
		player.openInventory(inv);
	}
}