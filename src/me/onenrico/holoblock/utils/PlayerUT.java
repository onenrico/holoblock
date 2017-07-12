package me.onenrico.holoblock.utils;

import java.util.Collection;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import me.onenrico.holoblock.main.Core;

public class PlayerUT {

	public static Collection<? extends Player> getOnlinePlayers() {
		return Bukkit.getServer().getOnlinePlayers();
	}

	public static Player getPlayer(String name) {
		return getPlayer(name, false);
	}

	public static Player getPlayer(String name, Boolean exact) {
		if (exact) {
			return Bukkit.getPlayerExact(name);
		} else {
			return Bukkit.getPlayer(name);
		}
	}

	public static Player getPlayer(UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}

	public static Player getPlayer(Object object) {
		Player player = (Player) object;
		return player;
	}

	public static Boolean isOnline(Player player) {
		return isOnline(player.getName());
	}

	public static Boolean isOnline(String name) {
		if (Bukkit.getPlayer(name) == null) {
			return false;
		}
		return true;
	}

	public static ItemStack getHead(String player) {
		ItemStack item = ItemUT.createItem(Material.SKULL_ITEM, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(player);
		item.setItemMeta(meta);
		return item;
	}

	@SuppressWarnings("deprecation")
	public static void setHand(Player player, ItemStack item) {
		boolean oldmethod = false;
		for (int x = 1; x < 4; x++) {
			if (Core.nmsver.equalsIgnoreCase("v1_8_R" + x)) {
				oldmethod = true;
			}
		}
		if (oldmethod) {
			player.getInventory().setItemInHand(item);
			return;
		} else {
			player.getInventory().setItemInMainHand(item);
			return;
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getHand(Player player) {
		boolean oldmethod = false;
		for (int x = 1; x < 4; x++) {
			if (Core.nmsver.equalsIgnoreCase("v1_8_R" + x)) {
				oldmethod = true;
			}
		}
		if (oldmethod) {
			return player.getItemInHand();
		} else {
			return player.getInventory().getItemInMainHand();
		}
	}

	public enum Skull {

		ARROW_LEFT("MHF_ArrowLeft"), ARROW_RIGHT("MHF_ArrowRight"), ARROW_UP("MHF_ArrowUp"), ARROW_DOWN(
				"MHF_ArrowDown"), QUESTION("MHF_Question"), EXCLAMATION("MHF_Exclamation"), CAMERA("FHG_Cam"),

		ZOMBIE_PIGMAN("MHF_PigZombie"), PIG("MHF_Pig"), SHEEP("MHF_Sheep"), BLAZE("MHF_Blaze"), CHICKEN(
				"MHF_Chicken"), COW("MHF_Cow"), SLIME("MHF_Slime"), SPIDER("MHF_Spider"), SQUID("MHF_Squid"), VILLAGER(
						"MHF_Villager"), OCELOT("MHF_Ocelot"), HEROBRINE("MHF_Herobrine"), LAVA_SLIME(
								"MHF_LavaSlime"), MOOSHROOM("MHF_MushroomCow"), GOLEM("MHF_Golem"), GHAST(
										"MHF_Ghast"), ENDERMAN("MHF_Enderman"), CAVE_SPIDER("MHF_CaveSpider"),

		CACTUS("MHF_Cactus"), CAKE("MHF_Cake"), CHEST("MHF_Chest"), MELON("MHF_Melon"), LOG("MHF_OakLog"), PUMPKIN(
				"MHF_Pumpkin"), TNT("MHF_TNT"), DYNAMITE("MHF_TNT2");

		private static final Base64 base64 = new Base64();
		private String id;

		private Skull(String id) {
			this.id = id;
		}

		public static ItemStack getCustomSkull(String url) {
			return getCustomSkull(url, false);
		}

		public static ItemStack getCustomSkull(String url, Boolean encoded) {
			GameProfile profile = getProfile(url, encoded);
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			ItemMeta headMeta = head.getItemMeta();
			Class<?> headMetaClass = headMeta.getClass();
			ReflectionUT.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
			head.setItemMeta(headMeta);
			return head;
		}

		public static GameProfile getProfile(String url, Boolean encoded) {
			GameProfile profile = new GameProfile(UUID.randomUUID(), null);
			PropertyMap propertyMap = profile.getProperties();
			if (propertyMap == null) {
				throw new IllegalStateException("Profile doesn't contain a property map");
			}
			if (!encoded) {
				byte[] encodedData = base64.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
				propertyMap.put("textures", new Property("textures", new String(encodedData)));
			} else {
				propertyMap.put("textures", new Property("textures", url));
			}
			return profile;
		}

		/**
		 * Return a skull of a player.
		 *
		 * @param name
		 *            player's name
		 * @return itemstack
		 */
		public static ItemStack getPlayerSkull(String name) {
			ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
			meta.setOwner(name);
			itemStack.setItemMeta(meta);
			return itemStack;
		}

		/**
		 * Return the skull's id.
		 *
		 * @return id
		 */
		public String getId() {
			return id;
		}

		/**
		 * Return the skull of the enum.
		 *
		 * @return itemstack
		 */
		public ItemStack getSkull() {
			ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
			meta.setOwner(id);
			itemStack.setItemMeta(meta);
			return itemStack;
		}

	}
}
