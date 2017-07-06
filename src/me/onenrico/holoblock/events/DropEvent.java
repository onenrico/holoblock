package me.onenrico.holoblock.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import me.onenrico.holoblock.utils.InventoryUT;

public class DropEvent implements Listener {
	@EventHandler
	public void drops(PlayerDropItemEvent event) {
		InventoryUT.checkSteal(event.getPlayer());
		// Item drop = event.getItemDrop();
		// if(ItemUT.getName(drop.getItemStack()) != null) {
		// drop.setCustomName(MessageUT.t(ItemUT.getName(drop.getItemStack()) +"
		// &bx"+drop.getItemStack().getAmount()));
		// }else {
		// drop.setCustomName(MessageUT.t(drop.getItemStack().getType() +"
		// &bx"+drop.getItemStack().getAmount()));
		// }
		// drop.setCustomNameVisible(true);
	}
	// @EventHandler
	// public void merge(ItemMergeEvent event) {
	// Item target = event.getTarget();
	// Item victim = event.getEntity();
	// int count = target.getItemStack().getAmount() +
	// victim.getItemStack().getAmount();
	// if(ItemUT.getName(target.getItemStack()) != null) {
	// target.setCustomName(MessageUT.t(ItemUT.getName(target.getItemStack()) +"
	// &bx"+count));
	// }else {
	// target.setCustomName(MessageUT.t(""+target.getItemStack().getType() +"
	// &bx"+count));
	// }
	// target.setCustomNameVisible(true);
	// }
	// @EventHandler
	// public void spawn(ItemSpawnEvent event) {
	// Item target = event.getEntity();
	// int count = target.getItemStack().getAmount();
	// if(ItemUT.getName(target.getItemStack()) != null) {
	// target.setCustomName(MessageUT.t(ItemUT.getName(target.getItemStack()) +"
	// &bx"+count));
	// }else {
	// target.setCustomName(MessageUT.t(""+target.getItemStack().getType() +"
	// &bx"+count));
	// }
	// target.setCustomNameVisible(true);
	// }
}
