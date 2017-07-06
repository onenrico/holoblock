package me.onenrico.holoblock.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.gui.EditLineMenu;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MathUT;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.MetaUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class ChatEvent implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (MetaUT.isThere(player, "EditLine:")) {
			String msg = event.getMessage();
			if (msg.equalsIgnoreCase("cancel")) {
				CloseEvent.mainMenuPlayers.remove(player);
				player.removeMetadata("EditLine:", Core.getThis());
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("edit_canceled"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				event.setCancelled(true);
				return;
			}
			msg = msg.replace("$ItemStack:", "");
			int length = ConfigPlugin.getMaxText();
			if (msg.length() > length) {
				msg = msg.substring(0, length - 1);
			}
			String data = MetaUT.getMetadata(player, "EditLine:").asString();
			player.removeMetadata("EditLine:", Core.getThis());
			String[] datas = data.split("<<");
			String rawloc = datas[0].replace("<r>", "<>");
			String line = datas[1];
			HoloData temp = Datamanager.getDataByLoc(rawloc);
			if (temp != null) {
				PlaceholderUT pu = new PlaceholderUT();
				pu.add("line", "" + (MathUT.strInt(line) + 1));
				pu.add("msg", "" + msg);
				temp.removeLine(MathUT.strInt(line));
				temp.setLine(MathUT.strInt(line), msg);
				temp.saveHolo(new BukkitRunnable() {
					@Override
					public void run() {
						EditLineMenu.open(player, rawloc, 1);
					}
				});
				SoundManager.playSound(player, "BLOCK_ANVIL_USE");
				List<String> le = pu.t(ConfigPlugin.locale.getValue("edit_line"));
				MessageUT.plmessage(player, le);
				event.setCancelled(true);
			}
		} else if (MetaUT.isThere(player, "AddLine:")) {
			String msg = event.getMessage();
			if (msg.equalsIgnoreCase("cancel")) {
				CloseEvent.mainMenuPlayers.remove(player);
				player.removeMetadata("AddLine:", Core.getThis());
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("edit_canceled"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				event.setCancelled(true);
				return;
			}
			msg = msg.replace("$ItemStack:", "");
			int length = ConfigPlugin.getMaxText();
			if (msg.length() > length) {
				msg = msg.substring(0, length - 1);
			}
			String data = MetaUT.getMetadata(player, "AddLine:").asString();
			player.removeMetadata("AddLine:", Core.getThis());
			String[] datas = data.split("<<");
			String rawloc = datas[0].replace("<r>", "<>");
			String line = datas[1];
			HoloData temp = Datamanager.getDataByLoc(rawloc);
			if (temp != null) {
				PlaceholderUT pu = new PlaceholderUT();
				pu.add("line", "" + (line + 1));
				pu.add("msg", "" + msg);
				temp.setLine(MathUT.strInt(line), msg);
				temp.saveHolo(new BukkitRunnable() {
					@Override
					public void run() {
						EditLineMenu.open(player, rawloc, 1);
					}
				});
				SoundManager.playSound(player, "BLOCK_ANVIL_USE");
				List<String> msgs = pu.t(ConfigPlugin.locale.getValue("add_line"));
				MessageUT.plmessage(player, msgs);
				event.setCancelled(true);
			}
		} else if (MetaUT.isThere(player, "EditOffSet:")) {
			String msg = event.getMessage();

			if (msg.equalsIgnoreCase("cancel")) {
				CloseEvent.mainMenuPlayers.remove(player);
				player.removeMetadata("EditOffSet:", Core.getThis());
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("edit_canceled"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				event.setCancelled(true);
				return;
			}
			Double num = 0d;
			try {
				num = Double.parseDouble(msg);
				if (num > 5) {
					num = 5d;
				}
				if (num < -5) {
					num = -5d;
				}
			} catch (Exception ex) {
				MessageUT.plmessage(player, ItemUT.createLore("&cInput Must Number!"), true);
				return;
			}
			String data = MetaUT.getMetadata(player, "EditOffSet:").asString();
			player.removeMetadata("EditOffSet:", Core.getThis());
			String rawloc = data;
			rawloc = rawloc.replace("<r>", "<>");
			HoloData temp = Datamanager.getDataByLoc(rawloc);
			if (temp != null) {
				PlaceholderUT pu = new PlaceholderUT();
				pu.add("offset", "" + num);
				temp.setOffset(num);
				temp.saveHolo(new BukkitRunnable() {
					@Override
					public void run() {
						temp.updateHolo();
						SoundManager.playSound(player, "BLOCK_ANVIL_USE");
					}
				});
				List<String> msgs = pu.t(ConfigPlugin.locale.getValue("edit_offset"));
				MessageUT.plmessage(player, msgs);
				event.setCancelled(true);
			}
		} else if (MetaUT.isThere(player, "EditSkin:")) {
			String msg = event.getMessage().split(" ")[0];
			int length = ConfigPlugin.getMaxText();
			if (msg.length() > length) {
				msg = msg.substring(0, length - 1);
			}
			if (msg.equalsIgnoreCase("cancel")) {
				CloseEvent.mainMenuPlayers.remove(player);
				player.removeMetadata("EditSkin:", Core.getThis());
				MessageUT.plmessage(player, ConfigPlugin.locale.getValue("edit_canceled"));
				SoundManager.playSound(player, "BLOCK_NOTE_PLING");
				event.setCancelled(true);
				return;
			}
			String data = MetaUT.getMetadata(player, "EditSkin:").asString();
			player.removeMetadata("EditSkin:", Core.getThis());
			String rawloc = data.replace("<r>", "<>");
			HoloData temp = Datamanager.getDataByLoc(rawloc);
			if (temp != null) {
				PlaceholderUT pu = new PlaceholderUT();
				pu.add("skin", "" + msg);
				temp.setSkin(msg);
				temp.saveHolo(new BukkitRunnable() {
					@Override
					public void run() {
						SoundManager.playSound(player, "BLOCK_ANVIL_USE");
					}
				});
				List<String> msgs = pu.t(ConfigPlugin.locale.getValue("edit_skin"));
				MessageUT.plmessage(player, msgs);
				event.setCancelled(true);
			}
		}
	}
}
