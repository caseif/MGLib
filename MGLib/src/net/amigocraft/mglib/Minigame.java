package net.amigocraft.mglib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public class Minigame {

	private static HashMap<String, Minigame> registeredInstances = new HashMap<String, Minigame>();

	@SuppressWarnings("unused")
	private JavaPlugin plugin;

	/**
	 * Creates a new instance of the MGLib API. This object may be used for all API methods
	 * @param plugin An instance of your plugin.
	 * @param minimumVersion The approved version of MGLib for your plugin.
	 */
	public Minigame(JavaPlugin plugin, String approvedVersion){
		if (!registeredInstances.containsKey(plugin.getName())){
			List<String> list = new ArrayList<String>();
			list.add(approvedVersion);
			registeredInstances.put(plugin.getName(), new Minigame(plugin, list));
			MGLib.log.info(plugin + " has hooked into MGLib!");
		}
		else
			MGLib.log.warning(plugin + " attempted to hook into MGLib while an instance of the API was already " +
					"registered. Please report this to the plugin author.");
	}

	/**
	 * Creates a new instance of the MGLib API. This object may be used for all API methods
	 * @param plugin An instance of your plugin.
	 * @param minimumVersion The approved versions of MGLib for your plugin.
	 */
	public Minigame(JavaPlugin plugin, List<String> approvedVersions){
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
		for (String v : MGLib.approved)
			if (version.contains(v))
				if (version.contains("dev")){
					if (Integer.parseInt(version.split("dev")[1]) <= MGLib.lastDev)
						return true;
				}
				else
					return true;
					
		return false;
	}

	/**
	 * Finds the instance of the MGLib API associated with a given plugin
	 * @param plugin The name of the plugin to search for
	 * @return The instance of the MGLib API (Minigame.class) associated with the given plugin
	 */
	public static Minigame getMinigameInstance(String plugin){
		return registeredInstances.get(plugin);
	}

	/**
	 * Finds the instance of the MGLib API associated with a given plugin
	 * @param plugin The plugin to search for
	 * @return The instance of the MGLib API (Minigame.class) associated with the given plugin
	 */
	public static Minigame getMinigameInstance(JavaPlugin plugin){
		return getMinigameInstance(plugin.getName());
	}

}
