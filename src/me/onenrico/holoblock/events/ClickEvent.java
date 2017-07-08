package me.onenrico.holoblock.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

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
		String data = action.split(":")[1];
		if(prefix.contains("<i>")) {
			prefix = prefix.split("<i>")[0];
		}
		HoloData hdata = null;
		String loc,member,strline,strline2 = "";
		int page,line,line2,count = 0;
		List<String> json = new ArrayList<>();
		List<String> hoverl = 
				ItemUT.createLore(
						"&7&m--------------------%n%%n%" 
								+ "&r      &fClick To &cCancel"
								+ "%n%%n%&7&m--------------------");
		PlaceholderUT pu = Locales.pub;
		switch(prefix) {
		case "Teleport":
			if(!PermissionUT.check(player, "holoblock.remote.teleport")) {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}
			if(CloseEvent.adminPlayers.contains(player)) {
				CloseEvent.adminPlayers.remove(player);
			}
			player.closeInventory();
			player.teleport(Seriloc.Deserialize(data));
			break;
		case "Refresh":
			hdata = Datamanager.getDataByLoc(data);
			hdata.updatePerm();
			hdata.updateSkinOnly();
			hdata.updateHolo();
			break;
		case "MainMenu":
			if(!PermissionUT.check(player, "holoblock.remote.manage")) {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}
			CloseEvent.adminPlayers.remove(player);
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
			if(CloseEvent.adminPlayers.contains(player)) {
				CloseEvent.adminPlayers.remove(player);
				admin = true;
			}
			AdminHologramMenu.open(player, page);
			if(admin) {
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
			json = JsonUT.btnGenerate(
					ConfigPlugin.locale.getValue("editing_line"),
					"cancel", 
					true,
					hoverl,
					true, "run", "cancel");
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
			CloseEvent.mainMenuPlayers.put(player, data.split("<<")[0]);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);
			break;
		case "EditOffSet":
			action = action.split(":")[1];
			MetaUT.setMetaData(player, "EditOffSet:", action);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);
			json = JsonUT.btnGenerate(
					ConfigPlugin.locale.getValue("editing_offset"),
					"cancel", 
					true,
					hoverl,
					true, "run", "cancel");
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));
			player.closeInventory();
			break;
		case "EditSkin":
			action = action.split(":")[1];
			hdata = Datamanager.getDataByLoc(action);
			if (!hdata.isAllowCustomSkin()) {
				MessageUT.plmessage(player, 
						ConfigPlugin.locale.getValue("not_permitted"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}
			MetaUT.setMetaData(player, "EditSkin:", action);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);
			json = JsonUT.btnGenerate(
					ConfigPlugin.locale.getValue("editing_skin"),
					"cancel", 
					true,
					hoverl,
					true, "run", "cancel");
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));
			player.closeInventory();
			break;
		case "AddLine":
			MetaUT.setMetaData(player, "AddLine:", data);
			json = JsonUT.btnGenerate(
					ConfigPlugin.locale.getValue("adding_line"),
					"cancel", 
					true,
					hoverl,
					true, "run", "cancel");
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
			MessageUT.debug("Data:" +data);
			CloseEvent.mainMenuPlayers.put(player, data.split("<<")[0]);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);
			break;
		case "AddMember":
			hdata = Datamanager.getDataByLoc(data);
			count = hdata.getMembers().size();
			if (count >= ConfigPlugin.getMaxMember(player, player.getWorld())) {
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
			if (count >= ConfigPlugin.getMaxLine(player, player.getWorld())) {
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
					MessageUT.plmessage(player, 
							pu.t(ConfigPlugin.locale.getValue("add_member")));
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
					MessageUT.plmessage(player, 
							pu.t(ConfigPlugin.locale.getValue("remove_member")));
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
					MessageUT.plmessage(player, 
							pu.t(ConfigPlugin.locale.getValue("remove_line")));
					EditLineMenu.open(player, loc, 1);
					CloseEvent.mainMenuPlayers.put(player, loc);
					SoundManager.playSound(player, "BLOCK_ANVIL_USE");
				}
			});
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
			break;
		case "MoveLine":
			loc = data.split("<<")[0];
			line = MathUT.strInt(data.split("<<")[1]);
			line2 = MathUT.strInt(data.split("<<")[2]);
			hdata = Datamanager.getDataByLoc(loc);
			strline = hdata.getLines().get(line);
			strline2 = hdata.getLines().get(line2);
			hdata.removeLine(line);
			hdata.insertLine(line,strline2);
			HoloData scope = hdata;
			new BukkitRunnable() {
				@Override
				public void run() {
					scope.removeLine(line2);
					scope.insertLine(line2,strline);
					scope.saveHolo(new BukkitRunnable() {
						@Override
						public void run() {
							pu.add("line", "" + (line + 1));
							pu.add("lineto", "" + (line2 + 1));
							MessageUT.plmessage(player, 
									pu.t(ConfigPlugin.locale.getValue("move_line")));
							EditLineMenu.open(player, loc, 1);
							CloseEvent.mainMenuPlayers.put(player, loc);
							SoundManager.playSound(player, "BLOCK_ANVIL_USE");
						}
					});
					player.closeInventory();
				}
				
			}.runTaskLater(Core.getThis(), 3);
			break;
		case "ItemLine":
			action = action.split("<i>")[1];
			loc = action.split("<<")[0];
			double cost = Double.parseDouble(action.split("<<")[1]);
			if (!EconomyUT.has(player, cost)) {
				MessageUT.plmessage(player, 
						ConfigPlugin.locale.getValue("insufficient_money"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}
			if (!PermissionUT.check(player, "holoblock.itemline")) {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;

			}
			String material = action.split("<<")[2];
			line = Integer.valueOf(action.split("<<")[3]);
			HoloData temp = Datamanager.getDataByLoc(loc);
			player.closeInventory();
			if (temp != null) {
				pu.add("line", "" + (line + 1));
				pu.add("msg", "" + "Icon:" + material);
				temp.setLine(line, "$ItemStack:" + material);
				temp.saveHolo(new BukkitRunnable() {
					@Override
					public void run() {
						EditLineMenu.open(player, loc, 1);
						List<String> msgs = 
								pu.t(ConfigPlugin.locale.getValue("add_line"));
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
