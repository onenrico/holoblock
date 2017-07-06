package me.onenrico.holoblock.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.PermissionUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class Holoblock implements CommandExecutor {
	public boolean isPlayer(CommandSender sender) {
		if(sender instanceof Player) {
			return true;
		}
		return false;
	}
	public static String leftheader = "&8&l[&b&m----&8&m-------&r ";
	public static String value = "{value}";
	public static String rightheader = " &8&m-------&b&m----&8&l]";
	public void help(Player player) {
		if(player != null) {
			MessageUT.pmessage(player, leftheader + "&bHolo Block Command" + rightheader);
			MessageUT.pmessage(player, " ");
			MessageUT.pmessage(player, "&6/holoblock give &f[<name>] &e Get The Holo Head Item");
			MessageUT.pmessage(player, " ");
			MessageUT.pmessage(player, leftheader + "&bHolo Block Command" + rightheader);
		}else {
			MessageUT.cmessage(leftheader + "&bHolo Block Command" + rightheader);
			MessageUT.cmessage(" ");
			MessageUT.cmessage("&6/holoblock give &f[<name>] &e Get The Holo Head Item");
			MessageUT.cmessage(" ");
			MessageUT.cmessage(leftheader + "&bHolo Block Command" + rightheader);
		}
	}
	public void give(Player player,Player target) {
		PlaceholderUT pu = new PlaceholderUT();
		pu.add("target", target.getName());
		List<String> msg = pu.t(ConfigPlugin.locale.getValue("item.give"));
		if(player == null) {
			MessageUT.cmessage(msg);
		}else{
			MessageUT.plmessage(player, msg);
		}
		target.getInventory().addItem(ConfigPlugin.getTool());
		return;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("holoblock")) {
			Player player = null;
			try {
				player = me.onenrico.holoblock.utils.PlayerUT.getPlayer(sender);
			}catch(Exception ex) {
			}
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload")) {
					if(isPlayer(sender)) {
						if(PermissionUT.check(player,"holoblock.reload")) {
							MessageUT.plmessage(player, ConfigPlugin.locale.getValue("config_reload"));
						}else {
							return true;
						}
					}
					ConfigPlugin.reloadSetting();
					return true;
				}
				if(isPlayer(sender)) {
					if(args[0].equalsIgnoreCase("give")) {
						if(player.hasPermission("holoblock.give")) {
							MessageUT.plmessage(player, ConfigPlugin.locale.getValue("item_get"));
							player.getInventory().addItem(ConfigPlugin.getTool());
							return true;
						}
					}
				}else {
					return true;
				}
			}
			else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("give")) {
					if(isPlayer(sender)) {
						if(PermissionUT.check(player,"holoblock.give.other")) {
							Player target = Bukkit.getPlayer(args[1]);
							if(target == null) {
								MessageUT.plmessage(player, "Player " + args[1] + " Not Found");
								return true;
							}
							give(player,target);
						}else {
							return true;
						}
					}else {
						Player target = Bukkit.getPlayer(args[1]);
						if(target == null) {
							MessageUT.cmessage("Player " + args[1] + " Not Found");
							return true;
						}
						give(null,target);
					}
					return true;
				}
			}
			if(isPlayer(sender)) {
				help(player);
			}
		}
		return true;
	}
}
