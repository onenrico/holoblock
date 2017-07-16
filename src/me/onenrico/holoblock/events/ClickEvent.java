package me.onenrico.holoblock.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.api.HoloBlockAPI;
import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.gui.AddLineMenu;
import me.onenrico.holoblock.gui.AddMemberMenu;
import me.onenrico.holoblock.gui.AdminHologramMenu;
import me.onenrico.holoblock.gui.EditLineMenu;
import me.onenrico.holoblock.gui.EditMemberMenu;
import me.onenrico.holoblock.gui.ItemLineMenu;
import me.onenrico.holoblock.gui.MainMenu;
import me.onenrico.holoblock.gui.ManageHologramMenu;
import me.onenrico.holoblock.gui.MoveLineMenu;
import me.onenrico.holoblock.gui.RemoveLineMenu;
import me.onenrico.holoblock.gui.RemoveMemberMenu;
import me.onenrico.holoblock.gui.SkinMenu;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.object.MenuItem;
import me.onenrico.holoblock.object.Seriloc;
import me.onenrico.holoblock.utils.EconomyUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.JsonUT;
import me.onenrico.holoblock.utils.MathUT;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.MetaUT;
import me.onenrico.holoblock.utils.ParticleUT;
import me.onenrico.holoblock.utils.PermissionUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class ClickEvent implements Listener {
	public static HashMap<Inventory, Set<MenuItem>> MenuItems = new HashMap<>();

	@EventHandler
	public void ItemClick(InventoryClickEvent event) {
		try {
			event.getWhoClicked().getOpenInventory().getTopInventory();
		} catch (Exception ex) {
			return;
		}
		if (event.getSlotType().equals(SlotType.OUTSIDE)) {
			return;
		}
		if (!MenuItems.containsKey(event.getClickedInventory())) {
			return;
		}
		if (event.getClickedInventory().getTitle().contains("Manage Lore")) {
			return;
		}
		Player player = (Player) event.getWhoClicked();
		Set<MenuItem> menuitemlist = MenuItems.get(event.getClickedInventory());
		event.setCancelled(true);
		for (MenuItem mi : menuitemlist) {
			if (mi.getSlot() == event.getSlot()) {
				List<String> Click = mi.getClickAction();
				List<String> Left_Click = mi.getLeftclickAction();
				List<String> Right_Click = mi.getRightclickAction();
				List<String> Shift_Right_Click = mi.getShiftrightclickAction();
				List<String> Shift_Left_Click = mi.getShiftleftclickAction();
				List<String> Middle_Click = mi.getMiddleclickAction();
				boolean dn = true;
				ClickType click = event.getClick();
				if (click.equals(ClickType.LEFT)) {
					if (!click.isShiftClick()) {
						if (dn) {
							if (Click != null) {
								for (String cmd : Click) {
									actionHandle(cmd, player);
								}
							}
							dn = false;
						}
						if (Left_Click != null) {
							for (String cmd : Left_Click) {
								actionHandle(cmd, player);
							}
						}
					}
				} else if (click.equals(ClickType.RIGHT)) {
					if (!click.isShiftClick()) {
						if (dn) {
							if (Click != null) {
								for (String cmd : Click) {
									actionHandle(cmd, player);
								}
							}
							dn = false;
						}
						if (Right_Click != null) {
							for (String cmd : Right_Click) {
								actionHandle(cmd, player);
							}
						}
					}
				} else if (click.equals(ClickType.SHIFT_RIGHT)) {
					if (Shift_Right_Click != null) {
						for (String cmd : Shift_Right_Click) {
							actionHandle(cmd, player);
						}
					}
				} else if (click.equals(ClickType.SHIFT_LEFT)) {
					if (Shift_Left_Click != null) {
						for (String cmd : Shift_Left_Click) {
							actionHandle(cmd, player);
						}
					}
				} else if (click.equals(ClickType.MIDDLE)) {
					if (Middle_Click != null) {
						for (String cmd : Middle_Click) {
							actionHandle(cmd, player);
						}
					}
				}
			}
		}
	}

	public void actionHandle(String action, Player player) {
		String prefix = action.split(":")[0];
		String data = "";
		if (prefix.contains("<i>")) {
			prefix = prefix.split("<i>")[0];
		} else {
			data = action.split(":")[1];
		}
		HoloData hdata = null;
		String loc, member, strline, strline2, last, material = "";
		int page, line, line2, count = 0;
		double cost = 0;
		List<String> json = new ArrayList<>();
		List<String> hoverl = ItemUT.createLore(
				"&7&m--------------------%n%%n%" + "&r     &fClick To &c&lCancel" + "%n%%n%&7&m--------------------");
		PlaceholderUT pu = Locales.pub;
		switch (prefix) {
		case "Teleport":
			if (!PermissionUT.check(player, "holoblock.remote.teleport")) {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}
			if (CloseEvent.adminPlayers.contains(player)) {
				CloseEvent.adminPlayers.remove(player);
			}
			player.closeInventory();
			Location tloc = Seriloc.centered(Seriloc.Deserialize(data));
			Block up = tloc.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP);
			tloc.add(0, 2, 0);
			if (!up.getType().equals(Material.AIR)) {
				tloc.setY(tloc.getWorld().getHighestBlockYAt(tloc));
			}
			player.teleport(tloc, TeleportCause.PLUGIN);
			SoundManager.playSound(player, "ENTITY_ENDERMEN_TELEPORT");
			ParticleUT.send(player, "SPELL_WITCH", tloc, 0.1f, 200, true);
			ParticleUT.send(player, "FIREWORKS_SPARK", tloc, 0.1f, 200, true);
			ParticleUT.send(player, "SPELL_MOB", tloc, 0.1f, 200, true);
			break;
		case "Buy":
			double costb = Double.parseDouble(data);
			if (EconomyUT.has(player, costb)) {
				SoundManager.playSound(player, "BLOCK_CHEST_OPEN");
				HoloBlockAPI.give(null, player);
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("success_buy"));
				EconomyUT.subtractBal(player, costb);
				player.closeInventory();
			} else {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("insufficient_money"));
			}
			break;
		case "Refresh":
			hdata = Datamanager.getDataByLoc(data);
			hdata.updatePerm();
			hdata.updateSkinOnly();
			hdata.updateHolo();
			break;
		case "MainMenu":
			if (!PermissionUT.check(player, "holoblock.remote.manage")) {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}
			CloseEvent.adminPlayers.remove(player);
			CloseEvent.mainMenuPlayers.remove(player);
			MainMenu.open(player, data);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "EditLineMenu":
			EditLineMenu.open(player, data, 1);
			CloseEvent.mainMenuPlayers.put(player, data);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "EditMemberMenu":
			EditMemberMenu.open(player, data, 1);
			CloseEvent.mainMenuPlayers.put(player, data);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "ManageHologramMenu":
			ManageHologramMenu.open(player, data, 1);
			CloseEvent.adminPlayers.add(player);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "ItemLineMenu":
			loc = data.split("<<")[0];
			line = MathUT.strInt(data.split("<<")[1]);
			CloseEvent.mainMenuPlayers.remove(player);
			ItemLineMenu.open(player, loc, 1, line);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "OpenPage":
			loc = action.split(":")[1];
			page = MathUT.strInt(action.split(":")[2]);
			CloseEvent.mainMenuPlayers.remove(player);
			EditLineMenu.open(player, loc, page);
			CloseEvent.mainMenuPlayers.put(player, loc);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "OpenPageAdmin":
			page = MathUT.strInt(action.split(":")[1]);
			CloseEvent.adminPlayers.remove(player);
			AdminHologramMenu.open(player, page);
			CloseEvent.adminPlayers.add(player);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "OpenPageHolo":
			page = MathUT.strInt(action.split(":")[1]);
			Boolean admin = false;
			if (CloseEvent.adminPlayers.contains(player)) {
				CloseEvent.adminPlayers.remove(player);
				admin = true;
			}
			AdminHologramMenu.open(player, page);
			if (admin) {
				CloseEvent.adminPlayers.add(player);
			}
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "OpenPageItemLine":
			loc = action.split(":")[1];
			page = MathUT.strInt(action.split(":")[2]);
			line = MathUT.strInt(action.split(":")[3]);
			ItemLineMenu.open(player, loc, page, line);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "OpenPageSkin":
			loc = action.split(":")[1];
			page = MathUT.strInt(action.split(":")[2]);
			SkinMenu.open(player, loc, page);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "OpenPageMember":
			loc = action.split(":")[1];
			page = MathUT.strInt(action.split(":")[2]);
			CloseEvent.mainMenuPlayers.remove(player);
			EditMemberMenu.open(player, loc, page);
			CloseEvent.mainMenuPlayers.put(player, loc);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "OpenPagePlayer":
			loc = action.split(":")[1];
			page = MathUT.strInt(action.split(":")[2]);
			AddMemberMenu.open(player, loc, page);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "OpenPageMove":
			loc = action.split(":")[1];
			page = MathUT.strInt(action.split(":")[2]);
			line = MathUT.strInt(action.split(":")[3]);
			MoveLineMenu.open(player, loc, page, line);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "SkinMenu":
			CloseEvent.mainMenuPlayers.remove(player);
			SkinMenu.open(player, data, 1);
			CloseEvent.mainMenuPlayers.put(player, data);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "MoveLineMenu":
			line = MathUT.strInt(action.split(":")[2]);
			page = (int) Math.ceil((line + 1) / 45.0);
			CloseEvent.mainMenuPlayers.remove(player);
			MoveLineMenu.open(player, data, page, line);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "RemoveLineMenu":
			loc = data.split("<<")[0];
			line = MathUT.strInt(data.split("<<")[1]);
			CloseEvent.mainMenuPlayers.remove(player);
			RemoveLineMenu.open(player, loc, line);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "RemoveMemberMenu":
			loc = data.split("<<")[0];
			member = data.split("<<")[1];
			CloseEvent.mainMenuPlayers.remove(player);
			RemoveMemberMenu.open(player, loc, member);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "EditLine":
			MetaUT.setMetaData(player, "EditLine:", data);
			hdata = Datamanager.getDataByLoc(data.split("<<")[0]);
			line = MathUT.strInt(data.split("<<")[1]);
			last = hdata.getLines().get(line);
			json = JsonUT.btnGenerate(ConfigPlugin.locale.getValue("editing_line"), "edit", Locales.pub.t("{edit}"),
					true, ItemUT.createLore(last), true, "suggest", last);
			json = JsonUT.btnGenerate(json, "cancel", "&8<&cCancel&8>", true, hoverl, true, "run", "cancel");
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
			CloseEvent.mainMenuPlayers.put(player, data.split("<<")[0]);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);
			break;
		case "EditOffSet":
			action = action.split(":")[1];
			MetaUT.setMetaData(player, "EditOffSet:", action);
			hdata = Datamanager.getDataByLoc(action);
			last = "" + hdata.getOffset();
			json = JsonUT.btnGenerate(ConfigPlugin.locale.getValue("editing_offset"), "edit", Locales.pub.t("{edit}"),
					true, ItemUT.createLore(last), true, "suggest", MessageUT.u(last));
			json = JsonUT.btnGenerate(json, "cancel", "&8<&cCancel&8>", true, hoverl, true, "run", "cancel");
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));
			player.closeInventory();
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);
			break;
		case "EditSkin":
			action = action.split(":")[1];
			hdata = Datamanager.getDataByLoc(action);
			if (!hdata.isAllowCustomSkin()) {
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("not_permitted"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}
			last = hdata.getSkin();
			MetaUT.setMetaData(player, "EditSkin:", action);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);
			json = JsonUT.btnGenerate(ConfigPlugin.locale.getValue("editing_skin"), "edit", Locales.pub.t("{edit}"),
					true, ItemUT.createLore(last), true, "suggest", MessageUT.u(last));
			json = JsonUT.btnGenerate(json, "cancel", "&8<&cCancel&8>", true, hoverl, true, "run", "cancel");
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));
			player.closeInventory();
			break;
		case "AddLine":
			MetaUT.setMetaData(player, "AddLine:", data);
			json = JsonUT.btnGenerate(ConfigPlugin.locale.getValue("adding_line"), "cancel", "&8<&cCancel&8>", true,
					hoverl, true, "run", "cancel");
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
			CloseEvent.mainMenuPlayers.put(player, data.split("<<")[0]);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);
			break;
		case "AddMember":
			hdata = Datamanager.getDataByLoc(data);
			count = hdata.getMembers().size();
			if (count >= HoloBlockAPI.getMaxMember(player, player.getWorld())) {
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("exceeded_member"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				break;
			}
			CloseEvent.mainMenuPlayers.remove(player);
			AddMemberMenu.open(player, data, 1);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "AddLineMenu":
			hdata = Datamanager.getDataByLoc(data);
			count = hdata.getLines().size();
			if (count >= HoloBlockAPI.getMaxLine(player, player.getWorld())) {
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("exceeded_line"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				break;
			}
			CloseEvent.mainMenuPlayers.remove(player);
			AddLineMenu.open(player, data);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			break;
		case "PlusMember":
			loc = data.split("<<")[0];
			member = data.split("<<")[1];
			hdata = Datamanager.getDataByLoc(loc);
			hdata.addMember(member);
			hdata.saveHolo(new BukkitRunnable() {
				@Override
				public void run() {
					pu.add("member", member);
					MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("add_member")));
					EditMemberMenu.open(player, loc, 1);
					CloseEvent.mainMenuPlayers.put(player, loc);
					SoundManager.playSound(player, "BLOCK_ANVIL_USE");
				}
			});
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
			break;
		case "MinusMember":
			loc = data.split("<<")[0];
			member = data.split("<<")[1];
			hdata = Datamanager.getDataByLoc(loc);
			hdata.removeMember(member);
			hdata.saveHolo(new BukkitRunnable() {
				@Override
				public void run() {
					pu.add("member", member);
					MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("remove_member")));
					EditMemberMenu.open(player, loc, 1);
					CloseEvent.mainMenuPlayers.put(player, loc);
					SoundManager.playSound(player, "BLOCK_ANVIL_USE");
				}
			});
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
			break;
		case "RemoveLine":
			loc = data.split("<<")[0];
			line = MathUT.strInt(data.split("<<")[1]);
			hdata = Datamanager.getDataByLoc(loc);
			hdata.removeLine(line);
			hdata.saveHolo(new BukkitRunnable() {
				@Override
				public void run() {
					pu.add("line", "" + (line + 1));
					MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("remove_line")));
					EditLineMenu.open(player, loc, 1);
					CloseEvent.mainMenuPlayers.put(player, loc);
					SoundManager.playSound(player, "BLOCK_ANVIL_USE");
				}
			});
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
			break;
		case "ReplaceLine":
			loc = data.split("<<")[0];
			line = MathUT.strInt(data.split("<<")[1]);
			line2 = MathUT.strInt(data.split("<<")[2]);
			hdata = Datamanager.getDataByLoc(loc);
			strline = hdata.getLines().get(line);
			strline2 = hdata.getLines().get(line2);
			hdata.removeLine(line);
			hdata.insertLine(line, strline2);
			HoloData scope = hdata;
			new BukkitRunnable() {
				@Override
				public void run() {
					scope.removeLine(line2);
					scope.insertLine(line2, strline);
					scope.saveHolo(new BukkitRunnable() {
						@Override
						public void run() {
							pu.add("line", "" + (line + 1));
							pu.add("lineto", "" + (line2 + 1));
							MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("move_line")));
							EditLineMenu.open(player, loc, 1);
							CloseEvent.mainMenuPlayers.put(player, loc);
							SoundManager.playSound(player, "BLOCK_ANVIL_USE");
						}
					});
					player.closeInventory();
				}

			}.runTaskLater(Core.getThis(), 3);
			break;
		case "MoveLine":
			loc = data.split("<<")[0];
			line = MathUT.strInt(data.split("<<")[1]);
			line2 = MathUT.strInt(data.split("<<")[2]);
			hdata = Datamanager.getDataByLoc(loc);
			strline = hdata.getLines().get(line);
			hdata.removeLine(line);
			hdata.insertLine(line2, strline);
			HoloData nscope = hdata;
			new BukkitRunnable() {
				@Override
				public void run() {
					nscope.saveHolo(new BukkitRunnable() {
						@Override
						public void run() {
							pu.add("line", "" + (line + 1));
							pu.add("lineto", "" + (line2 + 1));
							MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("move_line")));
							EditLineMenu.open(player, loc, 1);
							CloseEvent.mainMenuPlayers.put(player, loc);
							SoundManager.playSound(player, "BLOCK_ANVIL_USE");
						}
					});
					player.closeInventory();
				}

			}.runTaskLater(Core.getThis(), 3);
			break;
		case "CustomSkin":
			loc = data.split("<<")[0];
			cost = Double.parseDouble(data.split("<<")[1]);
			if (!EconomyUT.has(player, cost)) {
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("insufficient_money"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}
			if (!PermissionUT.check(player, "holoblock.use.customskin")) {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;

			}
			String name = data.split("<<")[2];
			HoloData temp = Datamanager.getDataByLoc(loc);
			player.closeInventory();
			if (temp != null) {
				temp.setSkin("$CustomSkin:" + name);
				temp.saveHolo(new BukkitRunnable() {
					@Override
					public void run() {
						pu.add("skin", "&7<&fCustom:&e" + name + "&7>");
						List<String> msgs = pu.t(ConfigPlugin.locale.getValue("edit_skin"));
						MessageUT.plmessage(player, msgs);
						SoundManager.playSound(player, "BLOCK_ANVIL_USE");
					}
				});
				EconomyUT.subtractBal(player, cost);
			}
			break;
		case "ItemLine":
			action = action.split("<i>")[1];
			loc = action.split("<<")[0];
			cost = Double.parseDouble(action.split("<<")[1]);
			if (!EconomyUT.has(player, cost)) {
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("insufficient_money"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}
			if (!PermissionUT.check(player, "holoblock.use.itemline")) {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;

			}
			material = action.split("<<")[2];
			line = Integer.valueOf(action.split("<<")[3]);
			HoloData temp2 = Datamanager.getDataByLoc(loc);
			player.closeInventory();
			if (temp2 != null) {
				pu.add("line", "" + (line + 1));
				pu.add("msg", "" + "Icon:" + material);
				temp2.setLine(line, "$ItemStack:" + material);
				temp2.saveHolo(new BukkitRunnable() {
					@Override
					public void run() {
						EditLineMenu.open(player, loc, 1);
						List<String> msgs = pu.t(ConfigPlugin.locale.getValue("add_line"));
						MessageUT.plmessage(player, msgs);
						SoundManager.playSound(player, "BLOCK_ANVIL_USE");
					}
				});
				EconomyUT.subtractBal(player, cost);
			}
			break;
		}
	}

}
