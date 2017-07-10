package me.onenrico.holoblock.database.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.MessageUT;

public class SQLite extends Database {
	public static String dbname;

	public SQLite(Core instance) {
		super(instance);
		dbname = "database";
		HashMap<String, String> map = new HashMap<>();
		map.put("Location", "varchar(255)");
		map.put("Owner", "blob");
		map.put("Lines", "blob");
		map.put("Members", "blob");
		map.put("Offset", "double");
		map.put("Skin", "blob");
		map.put("Rotation", "blob");
		map.put("Particle", "blob");
		SQLiteCreateTokensTable = generateToken("Location", map);
	}

	public String generateToken(String indexer, HashMap<String, String> map) {
		String result = "CREATE TABLE IF NOT EXISTS " + table + " (";
		int index = 0;
		for (String key : map.keySet()) {
			index++;
			if (index >= map.keySet().size()) {
				result += "`" + key + "` " + map.get(key) + " NOT NULL";
			} else {
				result += "`" + key + "` " + map.get(key) + " NOT NULL,";
			}
		}
		// result += ",PRIMARY KEY (`Key`)";
		// result += ");";
		result += ",PRIMARY KEY (`" + indexer + "`));";
		return result;
	}

	public String SQLiteCreateTokensTable = "";

	@Override
	public Connection getSQLConnection() {
		File dataFolder = new File(Core.getThis().getDataFolder() + "/data/");
		File dataFile = new File(Core.getThis().getDataFolder() + "/data/", dbname + ".db");
		if (!dataFolder.exists()) {
			try {
				dataFolder.mkdir();
				dataFile.createNewFile();
			} catch (IOException e) {
				MessageUT.debug("File write error: " + dbname + ".db");
			}
		}
		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile);
			return connection;
		} catch (SQLException ex) {
			MessageUT.debug("G: SQLite exception on initialize");
		} catch (ClassNotFoundException ex) {
			MessageUT.debug("H: SQLite exception on initialize");
		}
		return null;
	}

	@Override
	public void load() {
		connection = getSQLConnection();
		try {
			Statement s = connection.createStatement();
			s.executeUpdate(SQLiteCreateTokensTable);
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		initialize(new BukkitRunnable() {
			@Override
			public void run() {
				Datamanager.loadHolo();
			}
		});
	}
}
