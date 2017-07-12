package me.onenrico.holoblock.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.onenrico.holoblock.config.DatabaseConfig;
import me.onenrico.holoblock.database.sql.Database;
import me.onenrico.holoblock.database.sql.MySQL;
import me.onenrico.holoblock.database.sql.SQLite;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.utils.MessageUT;

public class Datamanager {
	private static List<HoloData> LoadedHoloData = new ArrayList<>();
	public static Set<String> loadedOwner = new HashSet<>();
	static Core instance;
	private static Database db;

	public Datamanager() {
		instance = Core.getThis();
	}

	public void reloadData() {
		setup();
	}

	public static Database getDB() {
		return db;
	}

	private static List<BukkitTask> databaseload = new ArrayList<>();

	public void setup() {
		for (BukkitTask task : databaseload) {
			task.cancel();
		}
		if (db != null) {
			if (db.connection != null) {
				try {
					if (!db.connection.isClosed()) {
						db.connection.close();
					}
					db = null;
				} catch (SQLException e) {
				}
			}
		}
		if (!HologramsAPI.getHolograms(Core.getThis()).isEmpty()) {
			for (Hologram holo : HologramsAPI.getHolograms(Core.getThis())) {
				if (holo.isDeleted()) {
					continue;
				}
				holo.delete();
			}
		}
		databaseload = new ArrayList<>();
		if (LoadedHoloData != null) {
			for (HoloData data : LoadedHoloData) {
				data.destroyHolo();
			}
			LoadedHoloData.clear();
		}
		if (loadedOwner != null) {
			loadedOwner.clear();
		}
		DatabaseConfig databaseconfig = Core.getThis().databaseconfig;
		String type = databaseconfig.getStr("database.type", "sqlite");
		if (type.equalsIgnoreCase("mysql")) {
			db = getMySQL();
		} else {
			db = getSQLite();
		}
		db.load();

	}

	public static SQLite getSQLite() {
		return new SQLite(instance);
	}

	public static MySQL getMySQL() {
		DatabaseConfig databaseconfig = Core.getThis().databaseconfig;
		String hostname = databaseconfig.getStr("database.hostname", "localhost");
		String port = databaseconfig.getStr("database.port", "3306");
		String database = databaseconfig.getStr("database.database", "database");
		String user = databaseconfig.getStr("database.user", "localhost");
		String password = databaseconfig.getStr("database.password", "localhost");
		return new MySQL(instance, hostname, port, database, user, password);
	}

	public static void unloadHolo() {
		for (HoloData data : LoadedHoloData) {
			data.destroyHolo();
		}
	}

	public static HoloData getDataByLoc(String rawloc) {
		for (HoloData data : LoadedHoloData) {
			if (data.getRawloc().equals(rawloc)) {
				return data;
			}
		}
		return null;
	}

	public static void deleteHolo(HoloData data, BukkitRunnable callback) {
		new BukkitRunnable() {
			@Override
			public void run() {
				String owner = data.getOwner();
				if (db.getOwned(owner) < 2) {
					loadedOwner.remove(owner);
				}
				data.destroy();
				db.deleteHolo(data.getRawloc(), callback);
				LoadedHoloData.remove(data);
			}

		}.runTask(Core.getThis());
	}

	public static List<HoloData> getHoloData() {
		return LoadedHoloData;
	}

	public static void addHolo(HoloData newdata) {
		String owner = newdata.getOwner();
		loadedOwner.add(owner);
		LoadedHoloData.add(newdata);
	}

	private static long last;

	public static void count(int j) {
		long hasil = System.currentTimeMillis() - last;
		double r = hasil / 1000.0;
		MessageUT.cmessage("&f<&bHoloBlock&f> &b" + j + " &eHologram Loaded");
		MessageUT.cmessage("&f<&bHoloBlock&f> Load Database Completed in &a" + r + "s");
	}

	public static void loadHolo() {
		last = System.currentTimeMillis();
		MessageUT.cmessage("&f<&bHoloBlock&f> Start Load Database");
		new BukkitRunnable() {
			@Override
			public void run() {
				List<String> databaseHolos = db.getLocation();
				if (databaseHolos == null) {
					return;
				}
				int maxasli = 1000;
				int count = databaseHolos.size();
				int times = (int) Math.ceil((double) count / (double) maxasli);
				int left = count % maxasli;
				if (count == 0) {
					MessageUT.cmessage("&f<&bHoloBlock&f> No Hologram Found");
					return;
				}
				for (int x = 0; x < times; x++) {
					int num = x;
					databaseload.add(new BukkitRunnable() {
						int id = num + 1;
						int max = maxasli;

						@Override
						public void run() {
							if (id == times) {
								max = left;
							}
							for (int index = 0; index < max; index++) {
								String temp = databaseHolos.get(index + (maxasli * num));
								HoloData data = new HoloData(temp);
								LoadedHoloData.add(data);
								loadedOwner.add(data.getOwner());
							}
							if (id + 1 == times && times > 1) {
								count(count);
							} else if (times < 2) {
								count(count);
							}
							cancel();
							return;
						}

					}.runTaskLater(Core.getThis(), 0));
				}
			}

		}.runTaskLater(Core.getThis(), 0);
	}
}