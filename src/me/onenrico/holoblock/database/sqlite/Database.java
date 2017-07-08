package me.onenrico.holoblock.database.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockFace;

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

	public void initialize() {
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table);
			ResultSet rs = ps.executeQuery();
			close(ps, rs);
		} catch (SQLException ex) {
			MessageUT.debug("A: " + ex);
		}
	}

	public List<String> getAll() {
		List<String> result = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + table + ";");
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("Location"));
			}
			return result;
		} catch (SQLException ex) {
			MessageUT.debug("B: " + ex);
			return result;
		} finally {
			close(ps, rs);
		}
	}

	public List<String> getLine(String location) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE Location='" + location + "';");
			rs = ps.executeQuery();
			List<String> result = new ArrayList<>();
			if (rs.next()) {
				for (String r : rs.getString("Lines").split("<#")) {
					if (!r.isEmpty()) {
						result.add(r);
					}
				}
				return result;
			}
		} catch (SQLException ex) {
			MessageUT.debug("B: " + ex);
		} finally {
			close(ps, rs);
		}
		return null;
	}

	public String getOwner(String location) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE Location='" + location + "';");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("Owner");
			}
		} catch (SQLException ex) {
			MessageUT.debug("B: " + ex);
		} finally {
			close(ps, rs);
		}
		return null;
	}

	public int getOwned(String name) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE Owner='" + name + "';");
			rs = ps.executeQuery();
			int result = 0;
			while (rs.next()) {
				result++;
			}
			return result;
		} catch (SQLException ex) {
			MessageUT.debug("B: " + ex);
		} finally {
			close(ps, rs);
		}
		return 0;
	}

	public double getOffSet(String loc) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE Location='" + loc + "';");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble("Offset");
			}
		} catch (SQLException ex) {
			MessageUT.debug("B: " + ex);
		} finally {
			close(ps, rs);
		}
		return -690;
	}

	public List<String> getHoloFrom(String name) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE Owner='" + name + "';");
			rs = ps.executeQuery();
			List<String> result = new ArrayList<>();
			while (rs.next()) {
				result.add(rs.getString("Location"));
			}
			return result;
		} catch (SQLException ex) {
			MessageUT.debug("B: " + ex);
		} finally {
			close(ps, rs);
		}
		return null;
	}

	public List<String> getMember(String location) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE Location='" + location + "';");
			rs = ps.executeQuery();
			List<String> result = new ArrayList<>();
			if (rs.next()) {
				for (String r : rs.getString("Members").split("<#")) {
					if (!r.isEmpty()) {
						result.add(r);
					}
				}
				return result;
			}
		} catch (SQLException ex) {
			MessageUT.debug("B: " + ex);
		} finally {
			close(ps, rs);
		}
		return null;
	}

	public String getSkin(String location) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE Location='" + location + "';");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("skin");
			}
		} catch (SQLException ex) {
			MessageUT.debug("B: " + ex);
		} finally {
			close(ps, rs);
		}
		return null;
	}

	public BlockFace getRotation(String location) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE Location='" + location + "';");
			rs = ps.executeQuery();
			if (rs.next()) {
				return BlockFace.valueOf(rs.getString("rotation"));
			}
		} catch (SQLException ex) {
			MessageUT.debug("B: " + ex);
		} finally {
			close(ps, rs);
		}
		return null;
	}
	public String getParticleName(String location) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE Location='" + location + "';");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("particle");
			}
		} catch (SQLException ex) {
			MessageUT.debug("B: " + ex);
		} finally {
			close(ps, rs);
		}
		return null;
	}

	public void setHolo(String player, 
			String location, 
			String rawline, String members, double offset, String skin,
			BlockFace rotation,
			String particle) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("REPLACE INTO " + table
					+ "(Owner,Location,Lines,Members,Offset,Skin,Rotation,Particle) "
					+ "VALUES(?,?,?,?,?,?,?,?)");
			ps.setString(1, player);
			ps.setString(2, location);
			ps.setString(3, rawline);
			ps.setString(4, members);
			ps.setDouble(5, offset);
			ps.setString(6, skin);
			ps.setString(7, rotation.toString());
			ps.setString(8, particle);
			ps.executeUpdate();
			return;
		} catch (SQLException ex) {
			MessageUT.debug("D: " + ex);
		} finally {
			close(ps, null);
		}
		return;
	}

	public void deleteHolo(String rawLoc) {
		;
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("DELETE FROM " + table + " WHERE Location='" + rawLoc + "'");
			ps.executeUpdate();
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
