package me.onenrico.holoblock.events;

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
import me.onenrico.holoblock.gui.EditLineMenu;
import me.onenrico.holoblock.gui.EditMemberMenu;
import me.onenrico.holoblock.gui.ItemLineMenu;
import me.onenrico.holoblock.gui.MoveLineMenu;
import me.onenrico.holoblock.gui.RemoveLineMenu;
import me.onenrico.holoblock.gui.RemoveMemberMenu;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.object.MenuItem;
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
		if (action.contains("EditLineMenu:")) {
			action = action.split(":")[1];
			EditLineMenu.open(player, action, 1);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			CloseEvent.mainMenuPlayers.put(player, action);
			return;
		}
		if (action.contains("EditMemberMenu:")) {
			action = action.split(":")[1];
			EditMemberMenu.open(player, action, 1);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			CloseEvent.mainMenuPlayers.put(player, action);
			return;
		} else if (action.contains("ItemLineMenu:")) {
			String loc = action.split(":")[1].split("<<")[0];
			int line = MathUT.strInt(action.split(":")[1].split("<<")[1]);
			CloseEvent.mainMenuPlayers.remove(player);
			ItemLineMenu.open(player, loc, 1, line);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			return;
		} else if (action.contains("OpenPage:")) {
			String loc = action.split(":")[1];
			int page = MathUT.strInt(action.split(":")[2]);
			String cache = CloseEvent.mainMenuPlayers.get(player);
			CloseEvent.mainMenuPlayers.remove(player);
			EditLineMenu.open(player, loc, page);
			CloseEvent.mainMenuPlayers.put(player, cache);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			return;
		} else if (action.contains("OpenPageItemLine:")) {
			String loc = action.split(":")[1];
			int page = MathUT.strInt(action.split(":")[2]);
			int line = MathUT.strInt(action.split(":")[3]);
			ItemLineMenu.open(player, loc, page, line);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			return;
		} else if (action.contains("OpenPageMember:")) {
			String loc = action.split(":")[1];
			int page = MathUT.strInt(action.split(":")[2]);
			String cache = CloseEvent.mainMenuPlayers.get(player);
			CloseEvent.mainMenuPlayers.remove(player);
			EditMemberMenu.open(player, loc, page);
			CloseEvent.mainMenuPlayers.put(player, cache);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			return;
		} else if (action.contains("OpenPagePlayer:")) {
			String loc = action.split(":")[1];
			int page = MathUT.strInt(action.split(":")[2]);
			AddMemberMenu.open(player, loc, page);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			return;
		} else if (action.contains("OpenPageMove:")) {
			String loc = action.split(":")[1];
			int page = MathUT.strInt(action.split(":")[2]);
			int line = MathUT.strInt(action.split(":")[3]);
			MoveLineMenu.open(player, loc, page, line);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			return;
		} else if (action.contains("MoveLineMenu:")) {
			int line = MathUT.strInt(action.split(":")[2]);
			action = action.split(":")[1];
			CloseEvent.mainMenuPlayers.remove(player);
			int page = (int) Math.ceil((line + 1) / 45.0);
			MoveLineMenu.open(player, action, page, line);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			return;
		} else if (action.contains("RemoveLineMenu:")) {
			action = action.split(":")[1];
			String rawloc = action.split("<<")[0];
			int line = MathUT.strInt(action.split("<<")[1]);
			CloseEvent.mainMenuPlayers.remove(player);
			RemoveLineMenu.open(player, rawloc, line);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			return;
		} else if (action.contains("RemoveMemberMenu:")) {
			action = action.split(":")[1];
			String rawloc = action.split("<<")[0];
			String member = action.split("<<")[1];
			CloseEvent.mainMenuPlayers.remove(player);
			RemoveMemberMenu.open(player, rawloc, member);
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			return;
		} else if (action.contains("EditLine:")) {
			action = action.split(":")[1];
			MetaUT.setMetaData(player, "EditLine:", action);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);

			List<String> json = JsonUT.btnGenerate(ConfigPlugin.locale.getValue("editing_line"), player, "cancel", true,
					ItemUT.createLore("&7&m--------------------%n%%n%" + "&6Click To &cCancel"
							+ "%n%%n%&7&m--------------------"),
					true, "run", "cancel");
			int index = 0;
			for (String j : json) {
				json.set(index, Locales.pluginPrefix + "<br>" + j);
				index++;
			}
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));

			String cache = CloseEvent.mainMenuPlayers.get(player);
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
			CloseEvent.mainMenuPlayers.put(player, cache);
		} else if (action.contains("AddLine:")) {
			action = action.split(":")[1];
			MetaUT.setMetaData(player, "AddLine:", action);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);
			List<String> json = JsonUT.btnGenerate(ConfigPlugin.locale.getValue("adding_line"), player, "cancel", true,
					ItemUT.createLore("&7&m--------------------%n%%n%" + "&6Click To &cCancel"
							+ "%n%%n%&7&m--------------------"),
					true, "run", "cancel");
			int index = 0;
			for (String j : json) {
				json.set(index, Locales.pluginPrefix + "<br>" + j);
				index++;
			}
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));

			String cache = CloseEvent.mainMenuPlayers.get(player);
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
			CloseEvent.mainMenuPlayers.put(player, cache);
		} else if (action.contains("AddMember:")) {
			action = action.split(":")[1];
			HoloData data = Datamanager.getDataByLoc(action);
			int count = data.getMembers().size();
			if (count >= ConfigPlugin.getMaxMember(player, player.getWorld())) {
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("exceeded_member"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}
			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			CloseEvent.mainMenuPlayers.remove(player);
			AddMemberMenu.open(player, action, 1);
		} else if (action.contains("AddLineMenu:")) {
			action = action.split(":")[1];
			HoloData data = Datamanager.getDataByLoc(action);
			int count = data.getLines().size();
			if (count >= ConfigPlugin.getMaxLine(player, player.getWorld())) {
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("exceeded_line"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;
			}

			SoundManager.playSound(player, "UI_BUTTON_CLICK");
			CloseEvent.mainMenuPlayers.remove(player);
			AddLineMenu.open(player, action);
		} else if (action.contains("PlusMember:")) {
			action = action.split(":")[1];
			String rawloc = action.split("<<")[0];
			String member = action.split("<<")[1];
			HoloData data = Datamanager.getDataByLoc(rawloc);
			data.addMember(member);
			data.saveHolo(new BukkitRunnable() {
				@Override
				public void run() {
					SoundManager.playSound(player, "BLOCK_ANVIL_USE");
					PlaceholderUT pu = new PlaceholderUT();
					pu.add("member", member);
					MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("add_member")));
					EditMemberMenu.open(player, rawloc, 1);
					CloseEvent.mainMenuPlayers.put(player, rawloc);
				}
			});
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
		} else if (action.contains("MinusMember:")) {
			action = action.split(":")[1];
			String rawloc = action.split("<<")[0];
			String member = action.split("<<")[1];
			HoloData data = Datamanager.getDataByLoc(rawloc);
			data.removeMember(member);
			data.saveHolo(new BukkitRunnable() {
				@Override
				public void run() {
					SoundManager.playSound(player, "BLOCK_ANVIL_USE");
					PlaceholderUT pu = new PlaceholderUT();
					pu.add("member", member);
					MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("remove_member")));
					EditMemberMenu.open(player, rawloc, 1);
					CloseEvent.mainMenuPlayers.put(player, rawloc);
				}
			});
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
		} else if (action.contains("RemoveLine:")) {
			action = action.split(":")[1];
			String rawloc = action.split("<<")[0];
			int line = MathUT.strInt(action.split("<<")[1]);
			HoloData data = Datamanager.getDataByLoc(rawloc);
			data.removeLine(line);
			data.saveHolo(new BukkitRunnable() {
				@Override
				public void run() {
					SoundManager.playSound(player, "BLOCK_ANVIL_USE");
					PlaceholderUT pu = new PlaceholderUT();
					pu.add("line", "" + (line + 1));
					MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("remove_line")));
					EditLineMenu.open(player, rawloc, 1);
					CloseEvent.mainMenuPlayers.put(player, rawloc);
				}
			});
			CloseEvent.mainMenuPlayers.remove(player);
			player.closeInventory();
		} else if (action.contains("MoveLine:")) {
			action = action.split(":")[1];
			String rawloc = action.split("<<")[0];
			int line = MathUT.strInt(action.split("<<")[1]);
			int line2 = MathUT.strInt(action.split("<<")[2]);
			HoloData data = Datamanager.getDataByLoc(rawloc);
			String strline = data.getLines().get(line);
			String strline2 = data.getLines().get(line2);

			data.setLine(line, strline2);
			data.setLine(line2, strline);
			data.saveHolo(new BukkitRunnable() {
				@Override
				public void run() {
					SoundManager.playSound(player, "BLOCK_ANVIL_USE");
					PlaceholderUT pu = new PlaceholderUT();
					pu.add("line", "" + (line + 1));
					pu.add("lineto", "" + (line2 + 1));
					MessageUT.plmessage(player, pu.t(ConfigPlugin.locale.getValue("move_line")));
					EditLineMenu.open(player, rawloc, 1);
					CloseEvent.mainMenuPlayers.put(player, rawloc);
				}
			});
			player.closeInventory();
		} else if (action.contains("ItemLine<i>")) {
			action = action.split("<i>")[1];
			String rawloc = action.split("<<")[0];
			double cost = Double.parseDouble(action.split("<<")[1]);
			if (!EconomyUT.has(player, cost)) {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("insufficient_money"));
				return;
			}
			if (!PermissionUT.check(player, "holoblock.itemline")) {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				return;

			}
			String material = action.split("<<")[2];
			int line = Integer.valueOf(action.split("<<")[3]);
			HoloData temp = Datamanager.getDataByLoc(rawloc);
			player.closeInventory();
			if (temp != null) {
				PlaceholderUT pu = new PlaceholderUT();
				pu.add("line", "" + (line + 1));
				pu.add("msg", "" + "Icon:" + material);
				temp.setLine(line, "$ItemStack:" + material);
				temp.saveHolo(new BukkitRunnable() {
					@Override
					public void run() {
						EditLineMenu.open(player, rawloc, 1);
						SoundManager.playSound(player, "UI_BUTTON_CLICK");
						List<String> msgs = pu.t(ConfigPlugin.locale.getValue("add_line"));
						MessageUT.plmessage(player, msgs);
					}
				});
				EconomyUT.subtractBal(player, cost);
			}
		} else if (action.contains("EditOffSet:")) {
			action = action.split(":")[1];
			MetaUT.setMetaData(player, "EditOffSet:", action);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);

			List<String> json = JsonUT.btnGenerate(ConfigPlugin.locale.getValue("editing_offset"), player, "cancel",
					true, ItemUT.createLore("&7&m--------------------%n%%n%" + "&6Click To &cCancel"
							+ "%n%%n%&7&m--------------------"),
					true, "run", "cancel");
			int index = 0;
			for (String j : json) {
				json.set(index, Locales.pluginPrefix + "<br>" + j);
				index++;
			}
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));

			player.closeInventory();
		} else if (action.contains("EditSkin:")) {
			action = action.split(":")[1];
			HoloData data = Datamanager.getDataByLoc(action);
			if (!data.isAllowCustomSkin()) {
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("not_permitted"));
				return;
			}
			MetaUT.setMetaData(player, "EditSkin:", action);
			SoundManager.playSound(player, "BLOCK_PISTON_EXTEND", 4f, 4f);

			List<String> json = JsonUT.btnGenerate(ConfigPlugin.locale.getValue("editing_skin"), player, "cancel", true,
					ItemUT.createLore("&7&m--------------------%n%%n%" + "&6Click To &cCancel"
							+ "%n%%n%&7&m--------------------"),
					true, "run", "cancel");
			int index = 0;
			for (String j : json) {
				json.set(index, Locales.pluginPrefix + "<br>" + j);
				index++;
			}
			JsonUT.multiSend(player, JsonUT.rawToJsons(json));

			player.closeInventory();
		}
	}

}
