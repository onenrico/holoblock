package me.onenrico.holoblock.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import me.onenrico.holoblock.locale.Locales;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class JsonUT {
	public static List<HashMap<String, String>> rawToJson(String text) {
		List<HashMap<String, String>> end = new ArrayList<>();
		String[] texts = text.split("<br>");
		for (String c : texts) {
			String[] splitter = c.split(jsonsplit);
			if (splitter.length > 1) {
				end.add(jsonParse(splitter[0], splitter[1]));
			} else {
				end.add(jsonParse(splitter[0], ""));
			}
		}
		return end;
	}

	public static List<List<HashMap<String, String>>> rawToJsons(List<String> text) {
		List<List<HashMap<String, String>>> end = new ArrayList<>();
		for (String j : text) {
			end.add(rawToJson(j));
		}
		return end;
	}

	public static String jsonsplit = ("<cmd>");

	public static HashMap<String, String> jsonParse(String text, String cmd) {
		HashMap<String, String> end = new HashMap<>();
		end.put(text, cmd);
		return end;
	}

	public static List<String> btnGenerate(List<String> json, String btn, boolean click,
			String clicktype, String clickvalue) {
		return btnGenerate(json, btn, false, null, click, clicktype, clickvalue);
	}

	public static List<String> btnGenerate(List<String> json, String btn, boolean hover,
			List<String> hovertext) {
		return btnGenerate(json, btn, hover, hovertext, false, null, null);
	}

	public static List<String> btnGenerate(List<String> json, String btn, Boolean hover,
			List<String> hovertext, boolean click, String clicktype, String clickvalue) {
		PlaceholderUT pu = new PlaceholderUT();
		List<String> newjson = new ArrayList<>();
		for (String j : json) {
			j = MessageUT.t(j);
			if (j.contains("<np>")) {
				j = j.replace("<np>", "");
			} else {
				j = Locales.pluginPrefix + "<br>" + j;
			}
			if (j.contains("<center>")) {
				j = j.replace("<center>", "");
				j = MessageUT.centered(j);
			}
			newjson.add(j);
		}
		String cmd = "<br>&6" + btn + "<cmd>";
		if (hover) {
			cmd += "#H:$TEXT:";
			for (int x = 0; x < hovertext.size(); x++) {
				cmd += hovertext.get(x);
				if (x + 1 >= hovertext.size()) {
					break;
				}
				cmd += "\n";
			}
		}
		if (click) {
			if (hover) {
				cmd += "<and>";
			}
			cmd += "#C:$" + clicktype.toUpperCase() + ":" + clickvalue;
		}
		cmd += "<br>";
		pu.getAcuan().put(btn, cmd);
		return pu.t(newjson);
	}

	public static void multiSend(Player player, List<List<HashMap<String, String>>> json) {
		for (List<HashMap<String, String>> j : json) {
			send(player, j);
		}
	}

	public static void send(Player player, List<HashMap<String, String>> json) {
		List<TextComponent> textlist = new ArrayList<>();
		for (HashMap<String, String> map : json) {
			Set<String> key = map.keySet();
			for (String text : key) {
				String cache = map.get(text);
				text = MessageUT.t(text);
				TextComponent single = new TextComponent(text);
				String[] temp = cache.split("<and>");
				for (String eventstr : temp) {
					if (eventstr.contains("#C:")) {
						eventstr = eventstr.replace("#C:", "");
						if (eventstr.contains("$RUN:")) {
							eventstr = eventstr.replace("$RUN:", "");
							String cmd = eventstr;
							single.setClickEvent(new ClickEvent(Action.RUN_COMMAND, cmd));
						}
						if (eventstr.contains("$SUGGEST:")) {
							eventstr = eventstr.replace("$SUGGEST:", "");
							String cmd = eventstr;
							single.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, cmd));
						}
						if (eventstr.contains("$URL:")) {
							eventstr = eventstr.replace("$URL:", "");
							String cmd = eventstr;
							single.setClickEvent(new ClickEvent(Action.OPEN_URL, cmd));
						}
					}
					if (eventstr.contains("#H:")) {
						eventstr = eventstr.replace("#H:", "");
						if (eventstr.contains("$TEXT:")) {
							eventstr = eventstr.replace("$TEXT:", "");
							String msg = MessageUT.t(eventstr);
							single.setHoverEvent(
									new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(msg).create()));
						}
					}
				}
				textlist.add(single);
			}
		}
		TextComponent rs = new TextComponent("");
		for (TextComponent tc : textlist) {
			tc.setText(tc.getText() + " ");
			rs.addExtra(tc);
		}
		player.spigot().sendMessage(rs);
	}
}
