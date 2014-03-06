package net.amigocraft.mglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.amigocraft.mglib.round.Round;
import net.amigocraft.mglib.round.Stage;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The primary API class. Contains necessary methods to create a minigame plugin from the library.
 * <br><br>
 * Building against this version of the library is <i>highly discouraged</i>. This is a development build,
 * and as such, is very prone to change. Methods may be in this version that will disappear in
 * the next release, and existing methods may be temporarily refactored.
 * @author Maxim Roncacé
 * @version 0.1-dev8
 * @since 0.1
 */
public class Minigame {

	private static HashMap<String, Minigame> registeredInstances = new HashMap<String, Minigame>();

	private JavaPlugin plugin;

	private List<Round> rounds = new ArrayList<Round>();

	/**
	 * Creates a new instance of the MGLib API. This object may be used for all API methods
	 * @param plugin An instance of your plugin.
	 * @param approvedVersion The approved version of MGLib for your plugin.\
	 * @since 0.1
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
	 * @param approvedVersions The approved versions of MGLib for your plugin.
	 * @since 0.1
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
			MGLib.log.warning(plugin + " was built for a newer or incompatible version of MGLib. As such, it is " +
					"likely that it wlil not work correctly.");
			MGLib.log.info("Type /mglib v" + plugin.getName() + " to see a list of compatible MGLib versions");
			//TODO: Actually implement this ^
		}
		if (dev)
			MGLib.log.warning(plugin + " was tested only against development version(s) of MGLib. " +
					"As such, it may not be fully compatible with the installed instance of the library. Please " +
					"notify the developer of " + plugin.getName() + " so he/she may take appropriate action.");
	}
	
	/**
	 * Retrieves the {@link JavaPlugin} associated with this {@link Minigame} instance.
	 * @return The {@link JavaPlugin} associated with this {@link Minigame} instance.
	 */
	public JavaPlugin getPlugin(){
		return plugin;
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
	 * @since 0.1
	 */
	public static Minigame getMinigameInstance(String plugin){
		return registeredInstances.get(plugin);
	}

	/**
	 * Finds the instance of the MGLib API associated with a given plugin
	 * @param plugin The plugin to search for
	 * @return The instance of the MGLib API (Minigame.class) associated with the given plugin
	 * @since 0.1
	 */
	public static Minigame getMinigameInstance(JavaPlugin plugin){
		return getMinigameInstance(plugin.getName());
	}

	/**
	 * Gets a list containing all rounds associated with the instance which registered this API instance.
	 * @return A list containing all rounds associated with the instance which registered this API instance.
	 * @since 0.1
	 */
	public List<Round> getRounds(){
		return rounds;
	}

	/**
	 * Creates and stores a new round with the given parameters.
	 * @param name The name of the world to create the round in.
	 * @param preparationTime The time (in seconds) the round should be kept in the preparation stage for)
	 * @param roundTime The time (in seconds) the round should last for. Set to 0 for no limit.
	 * @return The created round.
	 * @throws IllegalArgumentException if the specified arena cannot be loaded (due to it or its world being nonexistent)
	 * @throws IOException if an exception occurs while loading the arenas.yml file from disk
	 * @throws InvalidConfigurationException if an exception occurs while loading the configuration from arenas.yml
	 * @since 0.1
	 */
	public Round createRound(String name, boolean discrete, int preparationTime, int roundTime)
			throws IllegalArgumentException, IOException, InvalidConfigurationException{
		Round r = new Round(plugin.getName(), name, discrete, preparationTime, roundTime);
		r.setStage(Stage.WAITING);
		rounds.add(r);
		return r;
	}

	/**
	 * Gets the instance of the round associated with the given world.
	 * @param name The name of the round to retrieve.
	 * @return The instance of the round associated with the given world.
	 * @since 0.1
	 */
	public Round getRound(String name){
		for (Round r : rounds){
			if (r.getName().equals(name))
				return r;
		}
		return null;
	}

	/**
	 * Creates an arena for use with MGLib.
	 * @param name The name of the arena (used to identify it).
	 * @param spawn The initial spawn point of the arena.
	 * @param corner1 A corner of the arena.
	 * @param corner2 The corner of the arena opposite <b>corner1</b>.
	 * @throws IllegalArgumentException if the given locations are not in the same world, or if
	 * an arena of the same name already exists.
	 * @throws IOException if the filesystem calls throw one.
	 * @throws InvalidConfigurationException if the loaded YAML file is invalid.
	 * <br><br>
	 * It is recommended that you use {@link String#contains(CharSequence) String#contains()} in the event of
	 * an IllegalArgumentException to determine and handle the issue rather than just printing the stack trace
	 * (it scares users).
	 * <br><br>
	 * Example:
	 * <pre>
	 * {@code if (ex.getMessage().contains("exist")){
	 * 	// arena exists; handle appropriately
	 *  }
	 * </pre>
	 * @since 0.1
	 */
	public void createArena(String name, Location spawn, Location corner1, Location corner2)
			throws IllegalArgumentException, IOException, InvalidConfigurationException {

		if (spawn.getWorld().getName() != corner1.getWorld().getName())
			throw new IllegalArgumentException("Given locations are not in the same world");
		if (spawn.getWorld().getName() != corner2.getWorld().getName())
			throw new IllegalArgumentException("Given locations are not in the same world");

		double x1 = corner1.getX();
		double y1 = corner1.getY();
		double z1 = corner1.getZ();
		double x2 = corner2.getX();
		double y2 = corner2.getY();
		double z2 = corner2.getZ();

		double minX;
		double minY;
		double minZ;
		double maxX;
		double maxY;
		double maxZ;

		if (x1 < x2){
			minX = x1;
			maxX = x2;
		}
		else {
			minX = x2;
			maxX = x1;
		}
		if (y1 < y2){
			minY = y1;
			maxY = y2;
		}
		else {
			minY = y2;
			maxY = y1;
		}
		if (z1 < z2){
			minZ = z1;
			maxZ = z2;
		}
		else {
			minZ = z2;
			maxZ = z1;
		}
		
		YamlConfiguration y = MGUtil.loadArenaYaml(plugin.getName());
		if (y.contains(name))
			throw new IllegalArgumentException("An arena named \"" + name + "\" already exists");
		ConfigurationSection c = y.getConfigurationSection(name);
		c.set("world", spawn.getWorld());
		c.set("spawns.0.x", spawn.getX());
		c.set("spawns.0.y", spawn.getY());
		c.set("spawns.0.z", spawn.getZ());
		if (minX != Double.NaN){
			c.set("boundaries", true);
			c.set("minX", minX);
			c.set("minY", minY);
			c.set("minZ", minZ);
			c.set("maxX", maxX);
			c.set("maxY", maxY);
			c.set("maxZ", maxZ);
		}
		else
			c.set("boundaries", false);

	}

	/**
	 * Removes an arena from the plugin's config, effectively deleting it.
	 * @param name The arena to delete.
	 * @throws IllegalArgumentException if an arena by the specified name does not exist.
	 * @throws IOException if the filesystem calls throw one.
	 * @throws InvalidConfigurationException if the loaded YAML file is invalid.
	 * <br><br>
	 * It is recommended that you use {@link String#contains(CharSequence) String#contains()} in the event of
	 * an IllegalArgumentException to determine and handle the issue rather than just printing the stack trace
	 * (it scares users).
	 * <br><br>
	 * Example:
	 * <pre>
	 * {@code if (ex.getMessage().contains("exist")){
	 * 	// arena does not exist; handle appropriately
	 *  }
	 * </pre>
	 * @since 0.1
	 */
	public void deleteArena(String name) throws IllegalArgumentException, IOException, InvalidConfigurationException {
		//TODO: Remove players in arena
		YamlConfiguration y = MGUtil.loadArenaYaml(plugin.getName());
		if (!y.contains(name))
			throw new IllegalArgumentException("An arena by the name \"" + name + "\" does not exist");
		y.set(name, null);
	}

}
