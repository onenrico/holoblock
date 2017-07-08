package me.onenrico.holoblock.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.holoblock.main.Core;
import me.onenrico.holoblock.nms.actionbar.ActionBar;
import me.onenrico.holoblock.nms.particle.ParticleManager;
import me.onenrico.holoblock.utils.MessageUT;

public class HoloBlockAPI {
	public HoloBlockAPI() {
		Core.nmsver = Bukkit.getServer().getClass().getPackage().getName();
		Core.nmsver = Core.nmsver.substring(Core.nmsver.lastIndexOf(".") + 1);
		ActionBar.setup();
		ParticleManager.setup();
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					URL url = new URL("http://web.indomc.pro/plugin/holoblock.txt");
					URLConnection con = url.openConnection();
					InputStream in = con.getInputStream();
					String encoding = con.getContentEncoding(); 
					encoding = encoding == null ? "UTF-8" : encoding;
					String body = IOUtils.toString(in, encoding);
					String[] multi = body.split("\n");
					String name = "";
					String author = "";
					String version = "";
					for(String data : multi) {
						String prefix = data.split(": ")[0];
						String rdata = data.split(": ")[1];
						switch(prefix) {
						case "Name":
							name = rdata.trim();
							break;
						case "Author":
							author = rdata.trim();
							break;
						case "Version":
							version = rdata.trim();
							break;
						}
					}
					PluginDescriptionFile pdf = Core.getThis().getDescription();
					String pname = pdf.getName();
					String pauthor = pdf.getAuthors().get(0);
					String pversion = pdf.getVersion();
					if(pname == "") {
						return;
					}
					if(!pname.equals(name)) {
						MessageUT.cmessage(
						"&f<&bHoloBlock&f> "+
						"&cPlugin Disabled Because Plugin Name Changed !");
						
						Bukkit.getPluginManager().disablePlugin(Core.getThis());
					}
					if(pdf.getAuthors().size() > 1 || 
							!(pauthor.equals(author))) {
						MessageUT.cmessage(
						"&f<&bHoloBlock&f> "+
						"&cPlugin Disabled Because Plugin Author Changed !");

						Bukkit.getPluginManager().disablePlugin(Core.getThis());
					}
					if(!pversion.equals(version)) {
						MessageUT.cmessage(
						"&f<&bHoloBlock&f> "+" &lPlugin Found Update !");
						MessageUT.cmessage(
						"&f<&bHoloBlock&f> "+" &lPlease Update To v"+version);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}.runTaskLaterAsynchronously(Core.getThis(), 0);
	}
}
