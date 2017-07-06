package me.onenrico.holoblock.hooker;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.PlayerCache.TownBlockStatus;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.towny.war.flagwar.TownyWar;
import com.palmergames.bukkit.towny.war.flagwar.TownyWarConfig;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.events.PlaceEvent;
import me.onenrico.holoblock.nms.sound.SoundManager;
import me.onenrico.holoblock.utils.PlayerUT;

public class TownyHook {
	private boolean retur = false;

	public boolean getRetur() {
		return retur;
	}

	@SuppressWarnings("deprecation")
	public TownyHook(Player player, Block block, BlockPlaceEvent event, Towny towny) {
		try {
			ItemStack hand = PlayerUT.getHand(player);
			TownyWorld world = TownyUniverse.getDataSource().getWorld(block.getWorld().getName());
			WorldCoord worldCoord = new WorldCoord(world.getName(), Coord.parseCoord(block));

			boolean bBuild = PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getTypeId(),
					block.getData(), TownyPermission.ActionType.BUILD);
			if (bBuild) {
				if (hand.hasItemMeta()) {
					if (event.isCancelled()) {
						retur = true;
					}
					if (hand.getItemMeta().getDisplayName()
							.equals(ConfigPlugin.getTool().getItemMeta().getDisplayName())) {
						PlaceEvent.place(player, event.getBlock().getLocation());
						SoundManager.playSound(player, "BLOCK_ANVIL_PLACE");
					}
				}
				retur = true;
			}
			PlayerCache cache = towny.getCache(player);
			TownBlockStatus status = cache.getStatus();
			if (((status == TownBlockStatus.ENEMY) && TownyWarConfig.isAllowingAttacks())
					&& (event.getBlock().getType() == TownyWarConfig.getFlagBaseMaterial())) {

				try {
					if (TownyWar.callAttackCellEvent(towny, player, block, worldCoord)) {
						retur = true;
					}
				} catch (TownyException e) {
					TownyMessaging.sendErrorMsg(player, e.getMessage());
				}

				event.setBuild(false);
				event.setCancelled(true);
				retur = true;

			} else if (status == TownBlockStatus.WARZONE) {
				if (!TownyWarConfig.isEditableMaterialInWarZone(block.getType())) {
					event.setBuild(false);
					event.setCancelled(true);
					TownyMessaging.sendErrorMsg(player,
							String.format(TownySettings.getLangString("msg_err_warzone_cannot_edit_material"), "build",
									block.getType().toString().toLowerCase()));

					return;
				}
			} else {
				event.setBuild(false);
				event.setCancelled(true);
				retur = true;
			}

			if ((cache.hasBlockErrMsg()) && (event.isCancelled())) {
				TownyMessaging.sendErrorMsg(player, cache.getBlockErrMsg());
			}

		} catch (NotRegisteredException e1) {
			TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_not_configured"));
			event.setCancelled(true);
			retur = true;
		}
	}
}
