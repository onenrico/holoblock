package me.onenrico.holoblock.utils;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.actionbar.ActionBar;
import me.onenrico.holoblock.nms.titlebar.TitleBar;

public class MessageUT {
	public static String t(String colorize) {
		return ChatColor.translateAlternateColorCodes('&', colorize);
	}

	public static String u(String decolorize) {
		return decolorize.replace('§', '&');
	}

	public static String d(String remove) {
		remove = MessageUT.t(remove);
		for (ChatColor color : ChatColor.values()) {
			remove = remove.replaceAll(color.toString(), "");
		}
		return remove;
	}

	public static void pmessage(Player player, List<String> messages) {
		for (String m : messages) {
			pmessage(player, m);
		}
	}

	public static void plmessage(Player player, List<String> messages, Boolean warning) {
		for (String m : messages) {
			plmessage(player, m, warning);
		}
	}

	public static void plmessage(Player player, List<String> messages) {
		plmessage(player, messages, false);
	}



	public static void cmessage(String teks) {
		Core.getThis().getServer().getConsoleSender().sendMessage(t(teks));
	}
	public static void cmessage(List<String> teks) {
		for(String tek : teks) {
			cmessage(MessageUT.t(tek));
		}
	}

	public static void debug(String o) {
		Core.getThis().getServer().getConsoleSender().sendMessage(t("&8[&dDebug&8] &f" + o));
	}

	public static void debug(Player player, String o) {
		pmessage(player, "&8[&dDebug&8] &f" + o);
	}

	// PLAYER MESSAGE
	public static void pmessage(Player player, String teks) {
		pmessage(player, teks, false);
	}

	public static void pmessage(Player player, String teks, Boolean Action) {
		player.sendMessage(t(teks));
		;
		if (Action) {
			acmessage(player, teks);
		}
	}

	public static void plmessage(Player player, String teks) {
		plmessage(player, teks, false);
	}

	public static void plmessage(Player player, String teks, Boolean warning) {
		plmessage(player, teks, warning, false);
	}

	public static void plmessage(Player player, String teks, Boolean warning, Boolean Action) {
		if (warning) {
			pmessage(player, Locales.pluginPrefix + "&c" + teks, Action);
		} else {
			pmessage(player, Locales.pluginPrefix + "&b" + teks, Action);
		}
	}
	// PLAYER MESSAGE

	// ACTIONBAR MESSAGE
	public static void acplmessage(Player player, String teks, Boolean warning) {
		if (warning) {
			acmessage(player, Locales.pluginPrefix + "&c" + teks);
		} else {
			acmessage(player, Locales.pluginPrefix + "&b" + teks);
		}
	}

	public static void acplmessage(Player player, String teks) {
		acplmessage(player, teks, false);
	}

	public static void acplmessage(Player player, List<String> teks) {
		acplmessage(player, teks, false);
	}

	public static void acplmessage(Player player, List<String> messages, Boolean warning) {
		for (String m : messages) {
			acplmessage(player, m, warning);
		}
	}
	public static void acmessage(Player player, String teks) {
		teks = MessageUT.t(teks);
		ActionBar.sendActionBar(player, teks);
	}
	// ACTIONBAR MESSAGE

	// TITLEBAR MESSAGE
	public static void tfullmessage(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
		TitleBar.sendTitle(player, fadein, stay, fadeout, t(title), t(subtitle));
	}

	public static void tfullmessage(Player player, String title, String subtitle) {
		tfullmessage(player, title, subtitle, 20, 60, 20);
	}

	public static void tsubmessage(Player player, String subtitle, int fadein, int stay, int fadeout) {
		tfullmessage(player, null, subtitle, fadein, stay, fadeout);
	}

	public static void tsubmessage(Player player, String subtitle) {
		tfullmessage(player, null, subtitle, 20, 60, 20);
	}

	public static void ttmessage(Player player, String title, int fadein, int stay, int fadeout) {
		tfullmessage(player, title, null, fadein, stay, fadeout);
	}

	public static void ttmessage(Player player, String title) {
		tfullmessage(player, null, title, 20, 60, 20);
	}
	// TITLEBAR MESSAGE
}
