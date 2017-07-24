package me.onenrico.holoblock.database.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.utils.MessageUT;

public class MySQL extends Database {
	public static String dbname;
	public String hostname = "";
	public String port = "";
	public String database = "";
	public String user = "";
	public String password = "";

	public MySQL(Core instance, String hostname, String port, String database, String user, String password) {
		super(instance);
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		dbname = "database";
		HashMap<String, String> map = new HashMap<>();
		map.put("Location", "varchar(255)");
		map.put("Owner", "TEXT");
		map.put("Lines", "TEXT CHARACTER SET utf8mb4");
		map.put("Members", "TEXT");
		map.put("Offset", "double");
		map.put("Skin", "TEXT");
		map.put("Rotation", "TEXT");
		map.put("Particle", "TEXT");
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
		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database, user,
						password);

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return connection;
		} catch (SQLException ex) {
			MessageUT.debug("G: MySQL exception on initialize");
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
