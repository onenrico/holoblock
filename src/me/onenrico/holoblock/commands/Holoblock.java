package me.onenrico.holoblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.onenrico.holoblock.api.HoloBlockAPI;
import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.gui.AdminHologramMenu;
import me.onenrico.holoblock.gui.ManageHologramMenu;
import me.onenrico.holoblock.gui.ShopMenu;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.utils.MathUT;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.PermissionUT;

public class Holoblock implements CommandExecutor {
	public boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			return true;
		}
		return false;
	}

	public static String leftheader = "&8&l[&b&m----&8&m-------&r ";
	public static String value = "{value}";
	public static String rightheader = " &8&m-------&b&m----&8&l]";

	public void help(Player player) {
		if (player != null) {
			MessageUT.pmessage(player, leftheader + "&bHolo Block Command" + rightheader);
			MessageUT.pmessage(player, " ");
			MessageUT.pmessage(player, "&6/holoblock give &f[<name>] [<amount>] &e Get The Holo Head Item");
			MessageUT.pmessage(player, "&6/holoblock Admin &eOpen Admin GUI ");
			MessageUT.pmessage(player, "&6/holoblock Manage &e Manage Your Hologram");
			MessageUT.pmessage(player, "&6/holoblock reload &e Reload the Plugin");
			MessageUT.pmessage(player, " ");
			MessageUT.pmessage(player, leftheader + "&bHolo Block Command" + rightheader);
		} else {
			MessageUT.cmessage(leftheader + "&bOnly Allow For Player" + rightheader);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("holoblock")) {
			Player player = null;
			try {
				player = me.onenrico.holoblock.utils.PlayerUT.getPlayer(sender);
			} catch (Exception ex) {
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					if (isPlayer(sender)) {
						if (PermissionUT.check(player, "holoblock.reload")) {
							MessageUT.plmessage(player, ConfigPlugin.locale.getValue("config_reload"));
						} else {
							return true;
						}
					}
					Core.getThis().configplugin.reloadSetting();
					return true;
				}
				if (isPlayer(sender)) {
					if (args[0].equalsIgnoreCase("give")) {
						if (PermissionUT.check(player, "holoblock.give")) {
							MessageUT.plmessage(player, ConfigPlugin.locale.getValue("item_get"));
							player.getInventory().addItem(Core.getAPI().getHoloItem());
							return true;
						} else {
							SoundManager.playSound(player, "BLOCK_NOTE_PLING");
							return true;
						}
					}
					if (args[0].equalsIgnoreCase("admin")) {
						if (PermissionUT.check(player, "holoblock.admin")) {
							AdminHologramMenu.open(player, 1);
							return true;
						} else {
							SoundManager.playSound(player, "BLOCK_NOTE_PLING");
							return true;
						}
					}
					if (args[0].equalsIgnoreCase("manage")) {
						if (PermissionUT.check(player, "holoblock.remote")) {
							ManageHologramMenu.open(player, player.getName(), 1);
							return true;
						} else {
							SoundManager.playSound(player, "BLOCK_NOTE_PLING");
							return true;
						}
					}
					if (args[0].equalsIgnoreCase("shop")) {
						if (PermissionUT.check(player, "holoblock.shop")) {
							ShopMenu.open(player);
							return true;
						} else {
							SoundManager.playSound(player, "BLOCK_NOTE_PLING");
							return true;
						}
					}
				} else {
					return true;
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("give")) {
					if (isPlayer(sender)) {
						if (PermissionUT.check(player, "holoblock.give.other")) {
							Player target = Bukkit.getPlayer(args[1]);
							if (target == null) {
								MessageUT.plmessage(player, "Player " + args[1] + " Not Found");
								return true;
							}
							HoloBlockAPI.give(player, target);
						} else {
							return true;
						}
					} else {
						Player target = Bukkit.getPlayer(args[1]);
						if (target == null) {
							MessageUT.cmessage("Player " + args[1] + " Not Found");
							return true;
						}
						HoloBlockAPI.give(null, target);
					}
					return true;
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("give")) {
					if (isPlayer(sender)) {
						if (PermissionUT.check(player, "holoblock.give.other")) {
							Player target = Bukkit.getPlayer(args[1]);
							if (target == null) {
								MessageUT.plmessage(player, "Player " + args[1] + " Not Found");
								return true;
							}
							int count = MathUT.strInt(args[2]);
							HoloBlockAPI.give((Player) sender, target, count);
						} else {
							return true;
						}
					} else {
						Player target = Bukkit.getPlayer(args[1]);
						if (target == null) {
							MessageUT.cmessage("Player " + args[1] + " Not Found");
							return true;
						}
						int count = MathUT.strInt(args[2]);
						HoloBlockAPI.give(null, target, count);
					}
					return true;
				}
			}
			if (isPlayer(sender)) {
				help(player);
			}
		}
		return true;
	}
}
