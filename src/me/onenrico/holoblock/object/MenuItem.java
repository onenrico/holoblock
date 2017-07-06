package me.onenrico.holoblock.object;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.inventory.Inventory;

import me.onenrico.holoblock.events.ClickEvent;

public class MenuItem {
	private List<String> rightclickAction;
	private List<String> leftclickAction;
	private List<String> shiftleftclickAction;
	private List<String> shiftrightclickAction;
	private List<String> clickAction;
	private List<String> middleclickAction;
	private Inventory inventory;
	private int slot;
	public MenuItem(Inventory inv, int slots) {
		inventory = inv;
		slot = slots;
		refresh();
	} 
	public void refresh() {
		Set<MenuItem> cache = ClickEvent.MenuItems.get(inventory);
		if(cache == null) {
			cache = new HashSet<>();
		}
		if(!cache.contains(this)) {
			cache.add(this);
		}
		ClickEvent.MenuItems.put(inventory, cache);
	}
	public MenuItem addRightClick(String command) {
		if(rightclickAction == null) {
			rightclickAction = new ArrayList<>();
		}
		rightclickAction.add(command);
		return this;
	}
	public MenuItem addLeftClick(String command) {
		if(leftclickAction == null) {
			leftclickAction = new ArrayList<>();
		}
		leftclickAction.add(command);
		return this;
	}
	public MenuItem addShiftLeftClick(String command) {
		if(shiftleftclickAction == null) {
			shiftleftclickAction = new ArrayList<>();
		}
		shiftleftclickAction.add(command);
		return this;
	}
	public MenuItem addShiftRightClick(String command) {
		if(shiftrightclickAction == null) {
			shiftrightclickAction = new ArrayList<>();
		}
		shiftrightclickAction.add(command);
		return this;
	}
	public MenuItem addClick(String command) {
		if(clickAction == null) {
			clickAction = new ArrayList<>();
		}
		clickAction.add(command);
		return this;
	}
	public MenuItem addMiddleClick(String command) {
		if(middleclickAction == null) {
			middleclickAction = new ArrayList<>();
		}
		middleclickAction.add(command);
		return this;
	}
	public List<String> getRightclickAction() {
		return rightclickAction;
	}
	public List<String> getLeftclickAction() {
		return leftclickAction;
	}
	public List<String> getShiftleftclickAction() {
		return shiftleftclickAction;
	}
	public List<String> getShiftrightclickAction() {
		return shiftrightclickAction;
	}
	public List<String> getClickAction() {
		return clickAction;
	}
	public List<String> getMiddleclickAction() {
		return middleclickAction;
	}
	public Inventory getInventory() {
		return inventory;
	}
	public int getSlot() {
		return slot;
	}
}

