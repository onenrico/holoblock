package me.onenrico.holoblock.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class PlaceholderUT {
	private HashMap<String, String> acuan;

	public PlaceholderUT(HashMap<String, String> acuan) {
		this.acuan = acuan;
	}

	public PlaceholderUT() {
		acuan = new HashMap<>();
	}

	public HashMap<String, String> getAcuan() {
		return acuan;
	}

	public void setAcuan(HashMap<String, String> acuan) {
		this.acuan = acuan;
	}

	public void remove(String data) {
		if (acuan.containsKey(data)) {
			acuan.remove(data);
		}
	}

	public void add(String placeholder, String data) {
		acuan.put(placeholder, data);
	}

	public List<String> t(List<String> data) {
		List<String> result = new ArrayList<>();
		for (String b : data) {
			String temp = t(b);
			result.add(MessageUT.t(temp));
		}
		return result;
	}

	public List<String> tf(List<String> data) {
		List<String> result = new ArrayList<>();
		for (String b : data) {
			String temp = b;
			for (String a : acuan.keySet()) {
				if (temp.contains(a)) {
					temp = temp.replace(a, acuan.get(a));
				}
			}
			result.add(MessageUT.t(temp));
		}
		return result;
	}

	public String t(String data) {
		for (String a : acuan.keySet()) {
			if (data.contains("{" + a + "}")) {
				data = data.replace("{" + a + "}", acuan.get(a));
			}
		}
		return MessageUT.t(data);
	}

	public ItemStack t(ItemStack item) {
		item = ItemUT.createItem(item.getType(), t(ItemUT.getName(item)), t(ItemUT.getLore(item)), item.getAmount(),
				item.getDurability());
		return item;
	}
}
