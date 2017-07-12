package me.onenrico.holoblock.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.object.CustomSkin;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.utils.InventoryUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MathUT;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class SkinMenu {
	private static ItemStack PrevPageItem;
	private static ItemStack NextPageItem;
	private static ItemStack SkinLine;
	private static ItemStack ManualSkinLine;
	private static ItemStack CancelItem;
	private static List<CustomSkin> lcs = new ArrayList<>();

	private static void setup() {
		PrevPageItem = setupItem("PrevPageItem");
		NextPageItem = setupItem("NextPageItem");
		SkinLine = setupItem("SkinLine");
		ManualSkinLine = setupItem("ManualSkinLine");
		CancelItem = setupItem("CancelItem");
	}

	private static Boolean first = true;

	private static void setSkinPath(FileConfiguration c, String name, String data, String type, double cost) {
		c.set("CustomSkins." + name + ".cost", cost);
		c.set("CustomSkins." + name + ".type", type);
		c.set("CustomSkins." + name + ".data", data);
	}

	private static void setupSkin() {
		FileConfiguration fc = Core.getThis().configplugin.getConfig();
		ConfigurationSection css = fc.getConfigurationSection("CustomSkins");
		if (css == null) {
			setSkinPath(fc, "ARROW_LEFT", "MHF_ArrowLeft", "name", 1000);
			setSkinPath(fc, "ARROW_RIGHT", "MHF_ArrowRight", "name", 1000);
			setSkinPath(fc, "ARROW_UP", "MHF_ArrowUP", "name", 1000);
			setSkinPath(fc, "ARROW_DOWN", "MHF_ArrowDown", "name", 1000);
			setSkinPath(fc, "QUESTION", "MHF_Question", "name", 1000);
			setSkinPath(fc, "EXCLAMATION", "MHF_Exclamation", "name", 1000);
			setSkinPath(fc, "MACHINE",
					"eyJ0ZXh0dXJlcyI6" + "eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5ta"
							+ "W5lY3JhZnQubmV0L3RleHR1cmUvNmRkZDRhMTJkYTFjYzJjOW"
							+ "Y5ZDZjZDQ5ZmM3NzhlM2ExMWYzNzU3ZGU2ZGQzMTJkNzBhMGQ0Nzg4" + "NTE4OWMwIn19fQ==\\",
					"encode", 1000);
			setSkinPath(fc, "BURGER",
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmVmNjMzNDNkMWZmMWVmY2FkNTQyYTE5MTAyMzdkNTMzYjVlMWQ1NDIxY2EyMzVlNWQxZWZjMDllNzA1NyJ9fX0=",
					"encode", 1000);
			setSkinPath(fc, "SMILE_PUMPKIN",
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTJlYTk2ZjU0MGYxNzFkNmY2ODQ0NjY5YjE3NTNjZjQ4YThmNDE1ZThiYzM3YzhlZThiZjU2MWNhNmUzMWYyOCJ9fX0=",
					"encode", 1000);
			setSkinPath(fc, "BBQ",
					"http://textures.minecraft.net/texture/a86f19bf23d248e662c9c8b7fa15efb8a1f1d5bdacd3b8625a9b59e93ac8a",
					"url", 1000);
			setSkinPath(fc, "KEYPAD",
					"http://textures.minecraft.net/texture/391b5e3a6db068a0331f31e87284e756e1f21a482d0731c2ef8ee6091f0433d",
					"url", 1000);
			setSkinPath(fc, "MASTER_BALL",
					"http://textures.minecraft.net/texture/556d2a1cdda8778ffd833c7a5f539b87f99eb83a5d19599e5fa8e6c966c37c2",
					"url", 1000);
			Core.getThis().saveConfig();
			setupSkin();
		} else {
			Set<String> keys = css.getKeys(false);
			for (String key : keys) {
				String data = fc.getString("CustomSkins." + key + ".data");
				String type = fc.getString("CustomSkins." + key + ".type");
				double cost = fc.getDouble("CustomSkins." + key + ".cost");
				lcs.add(new CustomSkin(key, data, type, cost));
			}
		}
		first = false;
	}

	private static ItemStack setupItem(String name) {
		String prefix = "SkinMenu." + name + ".";
		ItemStack result = ItemUT.getItem(Core.getThis().guiconfig.getStr(prefix + "Material", "STONE").toUpperCase());
		ItemUT.changeDisplayName(result,
				Core.getThis().guiconfig.getStr(prefix + "Displayname", "&6" + name + " &fName &cNot Configured !"));
		ItemUT.changeLore(result, Core.getThis().guiconfig.getStrList(prefix + "Description",
				ItemUT.createLore("&6" + name + " &fDescription &cNot Configured !")));
		return result;
	}

	public static void open(Player player, String rawloc, int page) {
		setup();
		if (first) {
			setupSkin();
		}
		HoloData data = Datamanager.getDataByLoc(rawloc);
		if (!data.isAllowCustomSkin()) {
			SoundManager.playSound(player, "BLOCK_NOTE_PLING");
			MessageUT.plmessage(player, ConfigPlugin.locale.getValue("not_permitted"));
			return;
		}

		int current = lcs.size();
		int maxpage = (int) Math.ceil(current / 45.0);
		maxpage = MathUT.clamp(maxpage, 1);
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		pu.add("page", "" + page);
		pu.add("nextpage", "" + (page + 1));
		pu.add("prevpage", "" + (page - 1));
		pu.add("maxpage", "" + maxpage);
		pu.add("player", "" + player.getName());
		pu.add("owner", "" + data.getOwner());
		String title = pu.t(Core.getThis().guiconfig.getStr("SkinMenu.Title", "Title" + " &cNot Configured !"));
		Inventory inv = InventoryUT.createInventory(6, title);
		PrevPageItem = pu.t(PrevPageItem);
		NextPageItem = pu.t(NextPageItem);
		CancelItem = pu.t(CancelItem);
		if (page > 1) {
			InventoryUT.setItem(inv, 45, PrevPageItem).addClick("OpenPageSkin:" + rawloc + ":" + (page - 1));
			int multiplier = 45 * (page - 1);
			current = current - multiplier;
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				int newx = (x + multiplier);
				CustomSkin cs = lcs.get(newx);
				String name = cs.getName();
				ItemStack ite = cs.getSkullitem();
				double cost = cs.getCost();
				String type = cs.getType();
				pu.add("skinname", name.toUpperCase());
				pu.add("cost", "" + cost);
				pu.add("type", "" + type);
				ItemStack skinline = SkinLine.clone();
				ite = ItemUT.changeDisplayName(ite, pu.t(ItemUT.getName(skinline)));
				ite = ItemUT.changeLore(ite, pu.t(ItemUT.getLore(skinline)));
				InventoryUT.setItem(inv, x, ite).addClick("CustomSkin:" + rawloc + "<<" + cost + "<<" + name);
			}
		} else {
			for (int x = 0; x < MathUT.clamp(current, 0, 45); x++) {
				CustomSkin cs = lcs.get(x);
				String name = cs.getName();
				ItemStack ite = cs.getSkullitem();
				double cost = cs.getCost();
				String type = cs.getType();
				pu.add("skinname", name.toUpperCase());
				pu.add("cost", "" + cost);
				pu.add("type", "" + type);
				ItemStack skinline = SkinLine.clone();
				ite = ItemUT.changeDisplayName(ite, pu.t(ItemUT.getName(skinline)));
				ite = ItemUT.changeLore(ite, pu.t(ItemUT.getLore(skinline)));
				InventoryUT.setItem(inv, x, ite).addClick("CustomSkin:" + rawloc + "<<" + cost + "<<" + name);
			}
		}
		if (maxpage > 1) {
			if (page + 1 <= maxpage) {
				InventoryUT.setItem(inv, 53, NextPageItem).addClick("OpenPageSkin:" + rawloc + ":" + (page + 1));
			}
		}
		for (int x = 0; x < 5; x++) {
			InventoryUT.setItem(inv, x + 47, CancelItem).addClick("MainMenu:" + rawloc);
		}
		player.openInventory(inv);
	}
}
