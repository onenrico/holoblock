package me.onenrico.holoblock.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.PlayerUT;
import me.onenrico.holoblock.utils.PlayerUT.Skull;

public class CustomSkin {
	private ItemStack skullitem;
	private String type;
	private String data;
	private String name;
	private List<String> potioneffects;
	private double cost;

	public CustomSkin(String name, String data, String type, double cost) {
		this.name = name;
		this.data = data;
		this.type = type;
		this.cost = cost;
		potioneffects = new ArrayList<>();
		if (type.equalsIgnoreCase("name")) {
			skullitem = PlayerUT.Skull.getPlayerSkull(data);
		} else if (type.equalsIgnoreCase("url")) {
			skullitem = Skull.getCustomSkull(data);
		} else if (type.equalsIgnoreCase("encode")) {
			skullitem = Skull.getCustomSkull(data, true);
		}
	}

	public CustomSkin(String name) {
		this.name = name;
		potioneffects = Core.getThis().configplugin.getStrList("CustomSkins." + name + ".potions", new ArrayList<>());
		data = Core.getThis().configplugin.getStr("CustomSkins." + name + ".data", "MHF_CHEST");
		type = Core.getThis().configplugin.getStr("CustomSkins." + name + ".type", "name");
		cost = Core.getThis().configplugin.getDouble("CustomSkins." + name + ".cost", 500d);
		if (type.equalsIgnoreCase("name")) {
			skullitem = PlayerUT.Skull.getPlayerSkull(data);
		} else if (type.equalsIgnoreCase("url")) {
			skullitem = Skull.getCustomSkull(data);
		} else if (type.equalsIgnoreCase("encode")) {
			skullitem = Skull.getCustomSkull(data, true);
		}
	}

	public ItemStack getSkullitem() {
		return skullitem;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public double getCost() {
		return cost;
	}

	public List<String> getPotioneffects() {
		return potioneffects;
	}
}
