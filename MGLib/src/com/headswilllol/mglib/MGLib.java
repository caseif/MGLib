package com.headswilllol.mglib;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class MGLib extends JavaPlugin {
	
	public static List<String> approved = new ArrayList<String>();

	public static MGLib plugin;

	public static Logger log;

	public void onEnable(){
		plugin = this;
		log = getLogger();
		initialize();
		if (this.getDescription().getVersion().contains("dev"))
			log.warning("You are running a development build of MGLib. As such, plugins using the library may not " +
					"work correctly.");
		log.info(this + " is now ready!");
	}

	public void onDisable(){
		plugin = null;
		log.info(this + " has been disabled!");
		log = null;
	}
	
	private static void initialize(){
		approved.add("0.1-dev1");
	}

}
