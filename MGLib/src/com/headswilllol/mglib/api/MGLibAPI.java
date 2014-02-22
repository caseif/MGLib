package com.headswilllol.mglib.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.headswilllol.mglib.MGLib;

public class MGLibAPI {

	@SuppressWarnings("unused")
	private JavaPlugin plugin;

	/**
	 * Creates a new instance of the MGLib API. This object may be used for all API methods
	 * @param plugin An instance of your plugin.
	 * @param minimumVersion The approved version of MGLib for your plugin.
	 */
	public MGLibAPI(JavaPlugin plugin, String approvedVersion){
		List<String> list = new ArrayList<String>();
		list.add(approvedVersion);
		new MGLibAPI(plugin, list);
	}

	/**
	 * Creates a new instance of the MGLib API. This object may be used for all API methods
	 * @param plugin An instance of your plugin.
	 * @param minimumVersion The approved versions of MGLib for your plugin.
	 */
	public MGLibAPI(JavaPlugin plugin, List<String> approvedVersions){
		this.plugin = plugin;
		boolean dev = true;
		List<String> compatibleVersions = new ArrayList<String>();
		for (String v : approvedVersions){
			if (isCompatible(v)){
				compatibleVersions.add(v);
				if (!v.contains("dev"))
					dev = false;
			}
		}
		if (compatibleVersions.size() == 0){
			MGLib.log.warning(plugin + " was built for a newer version of MGLib. As such, it is likely that " +
		"it wlil not work correctly.");
		}
		if (dev)
			MGLib.log.warning(plugin + " was tested only against development version(s) of MGLib. " +
				"As such, it may not be fully compatible with the installed instance of the library. Please " +
				"notify the developer of " + plugin.getName() + " so he/she may take appropriate action.");
	}

	private boolean isCompatible(String version){
		if (MGLib.approved.contains(version))
			return true;
		return false;
	}

}
