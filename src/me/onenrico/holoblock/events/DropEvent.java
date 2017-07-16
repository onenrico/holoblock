package me.onenrico.holoblock.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import me.onenrico.holoblock.utils.InventoryUT;

public class DropEvent implements Listener {
	@EventHandler
	public void drops(PlayerDropItemEvent event) {
		InventoryUT.checkSteal(event.getPlayer());

	}
	// @EventHandler
	// public void arrow(EntityDamageByEntityEvent e) {
	// if(e.getCause().equals(DamageCause.PROJECTILE)) {
	// PotionEffect pef = new PotionEffect(PotionEffectType.SLOW, 200, 10);
	// StarEffect ef = new StarEffect(Core.em);
	// Damageable en = (Damageable) e.getEntity();
	// ef.particle = ParticleEffect.SPELL_MOB;
	// ef.particleCount = 1;
	// ef.updateDirections = true;
	// ef.setEntity(en);
	// ef.start();
	// new BukkitRunnable() {
	// @Override
	// public void run() {
	// if(en.isDead()) {
	// en.getWorld().createExplosion(en.getLocation(), 2);
	// ef.cancel();
	// this.cancel();
	// return;
	// }
	// en.damage(2);
	// en.setGravity(false);
	// en.setVelocity(new Vector(0,0.1d,0));
	// }
	// }.runTaskTimer(Core.getThis(), 1, 20);
	// }
	// }
}
