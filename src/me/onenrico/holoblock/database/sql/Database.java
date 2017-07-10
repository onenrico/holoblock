package me.onenrico.holoblock.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.MessageUT;

public abstract class Database {

	public Connection connection;
	Core plugin;
	public String table = "HoloData";

	public Database(Core instance) {
		plugin = instance;
	}

	public abstract Connection getSQLConnection();

	public abstract void load();

	public HashMap<String, List<String>> datacache = new HashMap<>();

	String[] columns = { 
		"Owner", "Lines", "Members", "Offset", "Skin", "Rotation", "Particle" };

	public void initialize(BukkitRunnable callback) {
		reloadData(callback);
	}

	public void reloadData() {
		reloadData(null);
	}

	public void reloadData(BukkitRunnable callback) {
		if (!datacache.isEmpty()) {
			datacache.clear();
		}
		new BukkitRunnable() {
			PreparedStatement ps = null;
			ResultSet rs = null;

			@Override
			public void run() {
				try {
					ps = connection.prepareStatement("SELECT * FROM " + table);
					rs = ps.executeQuery();
					List<String> result = null;
					while (rs.next()) {
						result = new ArrayList<>();
						for (String column : columns) {
							result.add(rs.getString(column));
						}
						datacache.put(rs.getString("Location"), result);
					}
					if (callback != null) {
						callback.run();
					}
				} catch (SQLException ex) {
					MessageUT.debug("A: " + ex);
				} finally {
					close(ps, rs);
				}
			}

		}.runTaskAsynchronously(Core.getThis());
	}

	public List<String> getLocation() {
		List<String> result = new ArrayList<String>(datacache.keySet());
		return result;
	}

	public List<String> getLine(String location) {
		if(datacache.get(location) == null) {
			return null;
		}
		return Arrays.asList(datacache.get(location).get(1).split("<#"));
	}

	public String getOwner(String location) {
		if(datacache.get(location) == null) {
			return null;
		}
		return datacache.get(location).get(0);
	}

	public int getOwned(String name) {
		return getHoloFrom(name).size();
	}

	public double getOffSet(String location) {
		if(datacache.get(location) == null) {
			return -690;
		}
		return Double.parseDouble(datacache.get(location).get(3));
	}

	public List<String> getHoloFrom(String name) {
		List<String> result = new ArrayList<>();
		for (String location : datacache.keySet()) {
			if (getOwner(location).equals(name)) {
				result.add(location);
			}
		}
		return result;
	}

	public List<String> getMember(String location) {
		if(datacache.get(location) == null) {
			return null;
		}
		return Arrays.asList(datacache.get(location).get(2).split("<#"));
	}

	public String getSkin(String location) {
		if(datacache.get(location) == null) {
			return null;
		}
		return datacache.get(location).get(4);
	}

	public BlockFace getRotation(String location) {
		if(datacache.get(location) == null) {
			return null;
		}
		return BlockFace.valueOf(datacache.get(location).get(5));
	}

	public String getParticleName(String location) {
		if(datacache.get(location) == null) {
			return null;
		}
		return datacache.get(location).get(6);
	}

	public void setHolo(String player, 
			String location, 
			String rawline, String members, double offset, String skin,
			BlockFace rotation, String particle, BukkitRunnable callback) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("REPLACE INTO " + table
					+ "(Owner,Location,Lines,Members,Offset,Skin,Rotation,Particle) " + "VALUES(?,?,?,?,?,?,?,?)");
			ps.setString(1, player);
			ps.setString(2, location);
			ps.setString(3, rawline);
			ps.setString(4, members);
			ps.setDouble(5, offset);
			ps.setString(6, skin);
			ps.setString(7, rotation.toString());
			ps.setString(8, particle);
			ps.executeUpdate();
			List<String> result = new ArrayList<>();
			result.add(player);
			result.add(rawline);
			result.add(members);
			result.add("" + offset);
			result.add(skin);
			result.add(rotation.toString());
			result.add(particle);
			datacache.put(location, result);
			if (callback != null) {
				callback.run();
			}
			return;
		} catch (SQLException ex) {
			MessageUT.debug("D: " + ex);
		} finally {
			close(ps, null);
		}
		return;
	}

	public void deleteHolo(String location, BukkitRunnable callback) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("DELETE FROM " + table + " WHERE Location='" + location + "'");
			ps.executeUpdate();
			datacache.remove(location);
			if (callback != null) {
				callback.run();
			}
			return;
		} catch (SQLException ex) {
			MessageUT.debug("D: " + ex);
		} finally {
			close(ps, null);
		}
		return;
	}

	private void close(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException ex) {
			MessageUT.debug("F: " + ex);
		}
	}
}
