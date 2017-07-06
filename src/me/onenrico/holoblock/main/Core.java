package me.onenrico.holoblock.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.palmergames.bukkit.towny.Towny;

import me.onenrico.holoblock.api.HoloBlockAPI;
import me.onenrico.holoblock.commands.Holoblock;
import me.onenrico.holoblock.config.ConfigPlugin;
import me.onenrico.holoblock.database.Datamanager;
import me.onenrico.holoblock.events.BreakEvent;
import me.onenrico.holoblock.events.ChatEvent;
import me.onenrico.holoblock.events.ClickEvent;
import me.onenrico.holoblock.events.CloseEvent;
import me.onenrico.holoblock.events.DropEvent;
import me.onenrico.holoblock.events.InteractEvent;
import me.onenrico.holoblock.events.PlaceEvent;
import me.onenrico.holoblock.hooker.vaultHook;
import me.onenrico.holoblock.locale.Locales;
import me.onenrico.holoblock.utils.MessageUT;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Core extends JavaPlugin {
	private static Core instance;
	private static HoloBlockAPI holoapi;
	public static Chat v_chat = null;
	public static Economy v_economy = null;
	public static Permission v_permission = null;
	public Datamanager thedata;
	public ConfigPlugin theconfig;
	public vaultHook v_hook;
	public static String nmsver;
	public static Core getThis() {
		return instance;
	}	
	public static HoloBlockAPI getAPI() {
		return holoapi;
	}	


	@Override
	public void onDisable() {
		Datamanager.unloadHolo();
		instance = null;
	}
	@Override
	public void onEnable() {
		instance = this;
		holoapi = new HoloBlockAPI();
		this.getServer().getPluginCommand("HoloBlock").setExecutor(new Holoblock());
		saveDefaultConfig();
		setupConstructor();
		if (!v_hook.setupEconomy()) {
			MessageUT.cmessage(Locales.pluginPrefix + " &cPlease Install Vault and Economy Plugin !");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (!v_hook.setupPermissions()) {
			MessageUT.cmessage(Locales.pluginPrefix + " &cPlease Install Vault and Permission Plugin !");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		setupEvent();
		setupDepedency();
	    Datamanager.loadHolo();
	}

	private void setupEvent() {
		Bukkit.getServer().getPluginManager().registerEvents(new PlaceEvent(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BreakEvent(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ClickEvent(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ChatEvent(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InteractEvent(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CloseEvent(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new DropEvent(), this);
	}
	private void setupConstructor() {
		thedata = new Datamanager();
		theconfig = new ConfigPlugin();
		v_hook = new vaultHook();
		ConfigPlugin.setupSetting();
		Datamanager.setup();
	}
	private Boolean setupHologram() {

		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			return false;
		}
		return true;
	}
	public static Boolean useHolo = true;
	public static HologramsAPI api;
	public static Towny towny;
	private void setupDepedency() {
		towny = (Towny) Bukkit.getPluginManager().getPlugin("Towny");
		useHolo = setupHologram();
		if(useHolo) {
			if(!HologramsAPI.getHolograms(Core.getThis()).isEmpty()) {
				for(Hologram holo : HologramsAPI.getHolograms(Core.getThis())) {
					holo.delete();
				}
			}
		}else {
			MessageUT.cmessage(Locales.pluginPrefix+" &cHolographicDisplay Not Found Disabling Holo Block !");
			this.getServer().getPluginManager().disablePlugin(this);
//			for(World w : Bukkit.getWorlds()) {
//				List<Entity> e = w.getEntities();
//				for(Entity en : e) {
//					if(en instanceof ArmorStand) {
//						String name = en.getCustomName();
//						if(name.startsWith(MessageUT.t(MoreHolo.identifier))) {
//							en.remove();
//						}
//					}
//				}
//			}
		}

	}
}
