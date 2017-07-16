package me.onenrico.holoblock.object;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.mojang.authlib.GameProfile;

import me.clip.placeholderapi.PlaceholderAPI;
import me.onenrico.holoblock.api.HoloBlockAPI;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.HoloUT;
import me.onenrico.holoblock.utils.ItemUT;
import me.onenrico.holoblock.utils.MathUT;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.ParticleUT;
import me.onenrico.holoblock.utils.PermissionUT;
import me.onenrico.holoblock.utils.PlayerUT;
import me.onenrico.holoblock.utils.ReflectionUT;

public class HoloData {
	private List<String> members;
	private List<String> lines;
	private String owner;
	private String rawloc;
	private BlockFace rotation;
	private Location realloc;
	private Hologram hologram;
	private Location cloc;
	private String skin;
	private String particlename;
	private BukkitTask particle;
	private BukkitTask potiontask;
	private List<PotionEffect> potioneffect = new ArrayList<>();
	private double offset;
	private boolean allowPlaceholders = false;
	private boolean allowColor = false;
	private boolean allowItemLine = false;
	private boolean allowCustomSkin = false;

	public HoloData(String loc) {
		rawloc = loc;
		realloc = Seriloc.Deserialize(loc);
		owner = Datamanager.getDB().getOwner(loc);
		lines = Datamanager.getDB().getLine(loc);
		members = Datamanager.getDB().getMember(loc);
		skin = Datamanager.getDB().getSkin(loc);
		rotation = Datamanager.getDB().getRotation(loc);
		particlename = Datamanager.getDB().getParticleName(loc);
		if (particlename == null) {
			particlename = "NONE";
		}
		particlename = particlename.toUpperCase();
		if (members == null) {
			members = new ArrayList<>();
		}
		if (lines == null) {
			lines = new ArrayList<>();
		} else {
			int index = 0;
			for (String l : lines) {
				if (l.equalsIgnoreCase("{#n}")) {
					lines.set(index, "");
				}
				index++;
			}
		}
		if (owner == null) {
			owner = "Prepared";
		}
		if (skin == null) {
			skin = Core.getThis().configplugin.getStr("holo.item.head", "SecurityCamera");
		}
		updatePerm();
		updateSkin();
		updateHolo();
		float toffset = (float) (offset * -1) + .1f;
		if (particlename.equalsIgnoreCase("NONE")) {
			particle = ParticleUT.circleParticle(cloc, 0, toffset, toffset, 0f, "SPELL_MOB");
		} else {
			particle = ParticleUT.circleParticle(cloc, 0, toffset, toffset, 0f, particlename);
		}
		potiontask = new BukkitRunnable() {
			@Override
			public void run() {
				Collection<Entity> ens = realloc.getWorld().getNearbyEntities(realloc, 15, 15, 15);
				for (Entity e : ens) {
					if (e instanceof Player) {
						if (members.contains(e) || owner.equals(e.getName())) {
							for (PotionEffect pef : potioneffect) {
								((Player) e).addPotionEffect(pef, true);
							}
						}
					}
				}
			}
		}.runTaskTimer(Core.getThis(), 20, 200);
	}

	@SuppressWarnings("deprecation")
	public void updatePerm() {
		OfflinePlayer ofp = Bukkit.getOfflinePlayer(owner);
		World world = realloc.getWorld();
		allowColor = PermissionUT.has(ofp, "holoblock.use.color", world);
		allowPlaceholders = PermissionUT.has(ofp, "holoblock.use.placeholders", world);
		allowItemLine = PermissionUT.has(ofp, "holoblock.use.itemline", world);
		allowCustomSkin = PermissionUT.has(ofp, "holoblock.use.customskin", world);
	}

	@SuppressWarnings("deprecation")
	public void updateSkin() {
		Block block = realloc.getBlock();
		BlockState state = block.getState();
		if (!(state instanceof Skull)) {
			block.setType(Material.SKULL);
			new BukkitRunnable() {
				@Override
				public void run() {
					updateSkin();
				}
			}.runTaskLater(Core.getThis(), 1);
			return;
		}
		Skull skull = (Skull) state;
		if (rotation == null) {
			rotation = skull.getRotation();
		}
		skull.getData().setData((byte) 1);
		skull.setRotation(rotation);
		skull.setSkullType(SkullType.PLAYER);
		skull.update();
		updateSkinOnly();
	}

	private static Method getWorldHandle;
	private static Method getWorldTileEntity;
	private static Method setGameProfile;
	private static Class<?> blockposition;
	private static Constructor<?> bpc;

	@SuppressWarnings("deprecation")
	public void updateSkinOnly() {
		Block block = realloc.getBlock();
		BlockState state = block.getState();
		Skull skull = (Skull) state;
		if (skin.startsWith("$CustomSkin:")) {
			String nskin = skin.replace("$CustomSkin:", "");
			CustomSkin cs = new CustomSkin(nskin);
			potioneffect.clear();
			for (String pefstr : cs.getPotioneffects()) {
				try {
					String pefe = pefstr.split(":")[0];
					int mod = MathUT.strInt(pefstr.split(":")[1]);
					PotionEffectType peft = PotionEffectType.getByName(pefe.toUpperCase());
					potioneffect.add(new PotionEffect(peft, 240, mod));
				} catch (Exception ex) {
					MessageUT.cmessage("Holoblock Potion Effect: " + pefstr + " is Invalid");
					continue;
				}
			}
			String type = cs.getType();
			GameProfile gp = null;
			if (type.equalsIgnoreCase("name")) {
				skull.setOwner(cs.getData());
				skull.update(true);
				return;
			} else if (type.equalsIgnoreCase("url")) {
				gp = PlayerUT.Skull.getProfile(cs.getData(), false);
			} else {
				gp = PlayerUT.Skull.getProfile(cs.getData(), true);
			}
			try {
				if (bpc == null) {
					blockposition = ReflectionUT.getNMSClass("BlockPosition");
					bpc = blockposition.getConstructor(int.class, int.class, int.class);
					getWorldHandle = ReflectionUT.getCraftBukkitClass("CraftWorld").getMethod("getHandle");
					getWorldTileEntity = ReflectionUT.getNMSClass("WorldServer").getMethod("getTileEntity",
							blockposition);
					setGameProfile = ReflectionUT.getNMSClass("TileEntitySkull").getMethod("setGameProfile",
							GameProfile.class);
				}
				Object bp = bpc.newInstance(block.getX(), block.getY(), block.getZ());
				Object world = getWorldHandle.invoke(skull.getWorld());
				Object tileSkull = getWorldTileEntity.invoke(world, bp);
				setGameProfile.invoke(tileSkull, gp);
				block.getState().update(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// TileEntitySkull skullTile = (TileEntitySkull)
			// ((CraftWorld) block.getWorld()).getHandle()
			// .getTileEntity(new BlockPosition(block.getX(), block.getY(),
			// block.getZ()));
			// skullTile.setGameProfile(gp);
			// block.getState().update(true);
			return;
		} else {
			skull.setOwner(skin);
		}
		skull.update(true);
	}

	public void update() {
		updateLines();
		updateHolo();
	}

	public void updateLines() {
		if (lines == null) {
			lines = new ArrayList<>();
		}
		if (!lines.isEmpty()) {
			lines = new ArrayList<>();
		}
		for (int x = 0; x < hologram.size(); x++) {
			if (hologram == null) {
				if (hologram.isDeleted()) {
					return;
				}
			}
			Object line = hologram.getLine(x);
			if (line instanceof ItemLine) {
				ItemLine item = (ItemLine) line;
				String stack = "" + item.getItemStack().getType();
				if (item.getItemStack().getDurability() > 0) {
					stack += ":" + item.getItemStack().getDurability();
				}
				lines.add("$ItemStack:" + stack);
			} else if (line instanceof TextLine) {
				TextLine text = (TextLine) line;
				String ltext = text.getText();
				ltext = ltext.replace("{refresh:fastest}", "");
				lines.add(ltext);
			}
		}
	}

	public void updateHolo() {
		destroyHolo();
		offset = Datamanager.getDB().getOffSet(getRawloc());
		if (offset == -690) {
			offset = HoloBlockAPI.getDefaultOffset();
		}
		cloc = Seriloc.centered(realloc.clone()).add(0, offset, 0);
		hologram = HoloUT.createHologram(cloc, allowPlaceholders);
		int index = 0;
		if (lines.isEmpty()) {
			return;
		}
		Boolean color = allowColor;
		for (String line : lines) {
			if (hologram == null) {
				if (hologram.isDeleted()) {
					break;
				}
			}
			if (line.contains("$ItemStack:")) {
				line = line.replace("$ItemStack:", "");
				ItemStack item = ItemUT.getItem(line);
				HoloUT.setLine(hologram, index, item);
			} else {
				if (Core.papi != null) {
					line = line.replace("{refresh:fastest}", "");
					if (isAllowPlaceholders()) {
						if (PlaceholderAPI.containsBracketPlaceholders(line)) {
							line = "{refresh:fastest}" + line;
						}
					}
				}
				if (color) {
					HoloUT.setLine(hologram, index, MessageUT.t(line));
				} else {
					HoloUT.setLine(hologram, index, MessageUT.u(line));
				}
			}
			index++;
		}
	}

	public void addMember(String member) {
		members.add(member);
	}

	public void removeMember(String member) {
		members.remove(member);
	}

	public void setLine(int line, String data) {
		if (hologram == null) {
			if (hologram.isDeleted()) {
				return;
			}
		}
		if (data.contains("$ItemStack:")) {
			data = data.replace("$ItemStack:", "");
			ItemStack item = ItemUT.getItem(data);
			HoloUT.setLine(hologram, line, item);
		} else {
			if (Core.papi != null) {
				data = data.replace("{refresh:fastest}", "");
				if (isAllowPlaceholders()) {
					if (PlaceholderAPI.containsBracketPlaceholders(data)) {
						data = "{refresh:fastest}" + data;
					}
				}
			}
			Boolean color = allowColor;
			if (color) {
				HoloUT.setLine(hologram, line, MessageUT.t(data));
			} else {
				HoloUT.setLine(hologram, line, MessageUT.u(data));
			}
		}
	}

	public void insertLine(int line, String data) {
		if (hologram == null) {
			if (hologram.isDeleted()) {
				return;
			}
		}
		if (data.contains("$ItemStack:")) {
			data = data.replace("$ItemStack:", "");
			ItemStack item = ItemUT.getItem(data);
			HoloUT.insertLine(hologram, line, item);
		} else {
			if (Core.papi != null) {
				data = data.replace("{refresh:fastest}", "");
				if (isAllowPlaceholders()) {
					if (PlaceholderAPI.containsBracketPlaceholders(data)) {
						data = "{refresh:fastest}" + data;
					}
				}
			}
			Boolean color = allowColor;
			if (color) {
				HoloUT.insertLine(hologram, line, MessageUT.t(data));
			} else {
				HoloUT.insertLine(hologram, line, MessageUT.u(data));
			}
		}
	}

	public void removeLine(int line) {
		if (hologram == null) {
			if (hologram.isDeleted()) {
				return;
			}
		}
		try {
			HoloUT.removeLine(hologram, line);
		} catch (Exception ex) {

		}

	}

	public void destroyHolo() {
		if (particle != null) {
			particle.cancel();
		}
		if (hologram == null) {
			return;
		}
		if (hologram.isDeleted()) {
			return;
		}
		hologram.delete();
	}

	public void destroy() {
		destroyHolo();
		realloc.getBlock().setType(Material.AIR, true);
		realloc.getBlock().getState().update(true);
		potiontask.cancel();
	}

	public void saveHolo(BukkitRunnable callback) {
		new BukkitRunnable() {
			String rawlines = "";
			String rawmembers = "";

			@Override
			public void run() {
				updateLines();
				for (int x = 0; x < hologram.size(); x++) {
					Object line = hologram.getLine(x);
					if (line instanceof ItemLine) {
						ItemLine item = (ItemLine) line;
						String stack = "" + item.getItemStack().getType();
						if (item.getItemStack().getDurability() > 0) {
							stack += ":" + item.getItemStack().getDurability();
						}
						rawlines += "$ItemStack:" + stack;

					} else if (line instanceof TextLine) {
						TextLine text = (TextLine) line;
						String teks = text.getText();
						if (teks.isEmpty()) {
							teks = "{#n}";
						}
						rawlines += teks.replace("{refresh:fastest}", "");
					}
					if (hologram.size() - x > 1) {
						rawlines += "<#";
					}
				}
				if (!members.isEmpty()) {
					for (int x = 0; x < members.size(); x++) {
						rawmembers += members.get(x);
						if (members.size() - x > 1) {
							rawmembers += "<#";
						}
					}
				}
				Datamanager.getDB().setHolo(owner, rawloc, rawlines, rawmembers, offset, skin, rotation, "NONE",
						callback);
			}
		}.runTaskLater(Core.getThis(), 3);
	}

	public List<String> getMembers() {
		return members;
	}

	public List<String> getLines() {
		return lines;
	}

	public String getOwner() {
		return owner;
	}

	public String getRawloc() {
		return rawloc;
	}

	public Location getRealloc() {
		return realloc;
	}

	public Hologram getHologram() {
		return hologram;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	public void setOwner(String owner) {
		this.owner = owner;
		updatePerm();
		hologram.setAllowPlaceholders(allowPlaceholders);
	}

	public void setRawloc(String rawloc) {
		this.rawloc = rawloc;
	}

	public void setRealloc(Location realloc) {
		this.realloc = realloc;
	}

	public void setHologram(Hologram hologram) {
		this.hologram = hologram;
	}

	public Location getCloc() {
		return cloc;
	}

	public double getOffset() {
		return offset;
	}

	public void setCloc(Location cloc) {
		this.cloc = cloc;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
		updateSkinOnly();
	}

	public BlockFace getRotation() {
		return rotation;
	}

	public void setRotation(BlockFace rotation) {
		this.rotation = rotation;
	}

	public boolean isAllowPlaceholders() {
		return allowPlaceholders;
	}

	public boolean isAllowColor() {
		return allowColor;
	}

	public boolean isAllowItemLine() {
		return allowItemLine;
	}

	public boolean isAllowCustomSkin() {
		return allowCustomSkin;
	}

	public String getParticlename() {
		return particlename;
	}
}
