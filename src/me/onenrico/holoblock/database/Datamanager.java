package me.onenrico.holoblock.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.database.sqlite.SQLite;
import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.object.HoloData;
import me.onenrico.holoblock.utils.MessageUT;
import me.onenrico.holoblock.utils.PlaceholderUT;

public class Datamanager {
	private static List<HoloData> LoadedHoloData = new ArrayList<>();
	static Core instance;
	private static SQLite db;

	public Datamanager() {
		instance = Core.getThis();
	}

	public static void reloadData() {
		setup();
		loadHolo();
	}

	public static SQLite getDB() {
		return db;
	}

	public static void savePlaceholders(PlaceholderUT pu) {
		List<String> save = new ArrayList<>();
		for (String key : pu.getAcuan().keySet()) {
			save.add(key + "<#" + pu.getAcuan().get(key));
		}
		ConfigPlugin.getConfig().set("saved", save);
		Core.getThis().saveConfig();
	}

	public static PlaceholderUT getPlaceholders(PlaceholderUT pu) {
		List<String> save = ConfigPlugin.getStrList("saved", new ArrayList<>());
		if (!save.isEmpty()) {
			for (String s : save) {
				String placeholder = s.split("<#")[0];
				String data = s.split("<#")[1];
				pu.add(placeholder, data);
			}
		}
		return pu;
	}

	private static List<BukkitTask> tasks = new ArrayList<>();

	public static void setup() {
		for (BukkitTask task : tasks) {
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
				if(holo.isDeleted()) {
					continue;
				}
				holo.delete();
			}
		}
		db = new SQLite(instance);
		db.load();
		tasks = new ArrayList<>();
		if (LoadedHoloData != null) {
			for (HoloData data : LoadedHoloData) {
				data.destroyHolo();
			}
			LoadedHoloData.clear();
		}

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

	public static void deleteHolo(HoloData data) {
		data.destroy();
		db.deleteHolo(data.getRawloc());
		LoadedHoloData.remove(data);
	}

	public static List<HoloData> getHoloData() {
		return LoadedHoloData;
	}

	public static void addHolo(HoloData newdata) {
		List<HoloData> destroy = new ArrayList<>();
		for (HoloData data : LoadedHoloData) {
			if (data.getRealloc().equals(newdata.getRealloc())) {
				destroy.add(data);
			}
		}
		for (HoloData temp : destroy) {
			deleteHolo(temp);
		}
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
				List<String> databaseHolos = db.getAll();
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
					tasks.add(new BukkitRunnable() {
						int id = num + 1;
						int max = maxasli;
						@Override
						public void run() {
							if (id == times) {
								max = left;
							}
							for(int index = 0;index < max;index++) {
								String temp = databaseHolos.get(index + (maxasli * num));
								LoadedHoloData.add(new HoloData(temp));
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