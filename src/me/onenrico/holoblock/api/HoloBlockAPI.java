package me.onenrico.holoblock.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.actionbar.ActionBar;
import me.onenrico.holoblock.nms.particle.ParticleManager;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.PermissionUT;
import me.onenrico.holoblock.utils.PlaceholderUT;
import me.onenrico.holoblock.utils.PlayerUT;

public class HoloBlockAPI {
	private static Core instance;

	public HoloBlockAPI() {
		instance = Core.getThis();
		Core.nmsver = Bukkit.getServer().getClass().getPackage().getName();
		Core.nmsver = Core.nmsver.substring(Core.nmsver.lastIndexOf(".") + 1);
		ActionBar.setup();
		ParticleManager.setup();
		updateCheck();
	}

	public static boolean isAllowCustomSkin(OfflinePlayer ofp, World world) {
		return PermissionUT.has(ofp, "holoblock.use.customskin", world);
	}

	public static boolean isAllowColor(OfflinePlayer ofp, World world) {
		return PermissionUT.has(ofp, "holoblock.use.color", world);
	}

	public static boolean isAllowPlaceholder(OfflinePlayer ofp, World world) {
		return PermissionUT.has(ofp, "holoblock.use.placeholder", world);
	}

	public static boolean isAllowItemLine(OfflinePlayer ofp, World world) {
		return PermissionUT.has(ofp, "holoblock.use.itemline", world);
	}

	public static int getMaxMember(OfflinePlayer offlineplayer, World world) {
		int max = HoloBlockAPI.getMaxMember();
		for (int x = max; x > 0; x--) {
			if (PermissionUT.has(offlineplayer, "holoblock.maxmember." + x, world)) {
				return x;
			}
		}
		return 0;
	}

	public static int getMaxLine(OfflinePlayer offlineplayer, World world) {
		int max = HoloBlockAPI.getMaxLine();
		for (int x = max; x > 0; x--) {
			if (PermissionUT.has(offlineplayer, "holoblock.maxline." + x, world)) {
				return x;
			}
		}
		return 0;
	}

	public static int getMaxOwned(OfflinePlayer offlineplayer, World world) {
		int max = HoloBlockAPI.getMaxOwned();
		for (int x = max; x > 0; x--) {
			if (PermissionUT.has(offlineplayer, "holoblock.maxowned." + x, world)) {
				return x;
			}
		}
		return 0;
	}

	public static int getMaxText() {
		return instance.configplugin.getInt("holo.max_text", 40);
	}

	public static int getMaxLine() {
		return instance.configplugin.getInt("holo.max_line", 100);
	}

	public static int getMaxMember() {
		return instance.configplugin.getInt("holo.max_member", 5);
	}

	public static int getMaxOwned() {
		return instance.configplugin.getInt("holo.max_owned", 5);
	}

	public static List<String> getDefaultLine() {
		return instance.configplugin.getStrList("holo.default_line", ItemUT.createLore("&cNot Configured"));
	}

	public static double getDefaultOffset() {
		return ConfigPlugin.instance.configplugin.getDouble("holo.default_offset", 2);
	}

	@SuppressWarnings("deprecation")
	public ItemStack getHoloItem() {
		String name = instance.configplugin.getStr("holo.item.head", "&cNot Configured");
		String display = instance.configplugin.getStr("holo.item.displayname", "&cNot Configured");
		List<String> lore = instance.configplugin.getStrList("holo.item.description",
				ItemUT.createLore("&cNot Configured"));
		ItemStack result = PlayerUT.getHead(name);
		result = ItemUT.changeDisplayName(result, display);
		result = ItemUT.changeLore(result, lore);
		result.getItemMeta().spigot().setUnbreakable(true);
		return result;
	}

	public void updateCheck() {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					URL url = new URL("http://web.indomc.pro/plugin/holoblock.txt");
					URLConnection con = url.openConnection();
					InputStream in = con.getInputStream();
					String encoding = con.getContentEncoding();
					encoding = encoding == null ? "UTF-8" : encoding;
					String body = IOUtils.toString(in, encoding);
					String[] multi = body.split("\n");
					String name = "";
					String author = "";
					String version = "";
					for (String data : multi) {
						String prefix = data.split(": ")[0];
						String rdata = data.split(": ")[1];
						switch (prefix) {
						case "Name":
							name = rdata.trim();
							break;
						case "Author":
							author = rdata.trim();
							break;
						case "Version":
							version = rdata.trim();
							break;
						}
					}
					PluginDescriptionFile pdf = Core.getThis().getDescription();
					String pname = pdf.getName();
					String pauthor = pdf.getAuthors().get(0);
					String pversion = pdf.getVersion();
					if (pname == "") {
						return;
					}
					if (!pname.equals(name)) {
						MessageUT.cmessage("&f<&bHoloBlock&f> " + "&cPlugin Disabled Because Plugin Name Changed !");

						Bukkit.getPluginManager().disablePlugin(Core.getThis());
					}
					if (pdf.getAuthors().size() > 1 || !(pauthor.equals(author))) {
						MessageUT.cmessage("&f<&bHoloBlock&f> " + "&cPlugin Disabled Because Plugin Author Changed !");

						Bukkit.getPluginManager().disablePlugin(Core.getThis());
					}
					if (!pversion.equals(version)) {
						MessageUT.cmessage("&f<&bHoloBlock&f> " + " &lPlugin Found Update !");
						MessageUT.cmessage("&f<&bHoloBlock&f> " + " &lPlease Update To v" + version);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}.runTaskLaterAsynchronously(Core.getThis(), 0);
	}

	public static void give(Player player, Player target) {
		PlaceholderUT pu = new PlaceholderUT();
		pu.add("target", target.getName());
		pu.add("amount", "" + 1);
		List<String> msg = pu.t(ConfigPlugin.locale.getValue("item_give"));
		if (player == null) {
			MessageUT.cmessage(msg);
		} else {
			MessageUT.plmessage(player, msg);
		}
		List<String> msg2 = pu.t(ConfigPlugin.locale.getValue("item_gived"));
		MessageUT.plmessage(target, msg2);
		target.getInventory().addItem(Core.getAPI().getHoloItem());
		return;
	}

	public static void give(Player player, Player target, int count) {
		PlaceholderUT pu = new PlaceholderUT();
		pu.add("target", target.getName());
		pu.add("amount", "" + count);
		List<String> msg = pu.t(ConfigPlugin.locale.getValue("item_give"));
		if (player == null) {
			MessageUT.cmessage(msg);
		} else {
			MessageUT.plmessage(player, msg);
		}
		List<String> msg2 = pu.t(ConfigPlugin.locale.getValue("item_gived"));
		MessageUT.plmessage(target, msg2);
		if (count > 0) {
			for (int x = 0; x < count; x++) {
				target.getInventory().addItem(Core.getAPI().getHoloItem());
			}
		}
		return;
	}
}
