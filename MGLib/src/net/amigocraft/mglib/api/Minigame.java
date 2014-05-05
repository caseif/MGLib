package net.amigocraft.mglib.api;

import java.util.HashMap;
import java.util.List;

import net.amigocraft.mglib.LobbyManager;
import net.amigocraft.mglib.Main;
import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.RollbackManager;
import net.amigocraft.mglib.exception.ArenaExistsException;
import net.amigocraft.mglib.exception.ArenaNotExistsException;
import net.amigocraft.mglib.exception.InvalidLocationException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

/**
 * The primary API class. Contains necessary methods to create a minigame plugin from the library.
 * <br><br>
 * Building against this version of the library is <i>highly discouraged</i>. This is a development build,
 * and as such, is very prone to change. Methods may be in this version that will disappear in
 * the next release, and existing methods may be temporarily refactored.
 * @author Maxim Roncacé
 * @version 0.1.0
 * @since 0.1.0
 */
public class Minigame {

	private static HashMap<String, Minigame> registeredInstances = new HashMap<String, Minigame>();

	private JavaPlugin plugin;

	private HashMap<String, Round> rounds = new HashMap<String, Round>();

	private ConfigManager configManager;
	private RollbackManager rbManager;
	private LobbyManager lobbyManager;

	private HashMap<String, ArenaFactory> arenaFactories = new HashMap<String, ArenaFactory>();

	private Minigame(JavaPlugin plugin){
		if (!registeredInstances.containsKey(plugin.getName())){
			this.plugin = plugin;
			registeredInstances.put(plugin.getName(), this); // list this Minigame instance for use in other parts of the API
			Main.log.info(plugin + " has successfully hooked into MGLib!");
		}
		else
			throw new IllegalArgumentException(plugin + " attempted to hook into MGLib while an instance of the API was already " +
					"registered. This is a bug, and should be reported this to the author of the hooking plugin.");
		configManager = new ConfigManager(plugin.getName());
		rbManager = new RollbackManager(plugin); // register rollback manager
		rbManager.checkRollbacks(); // roll back any arenas which were left un-rolled back
		lobbyManager = new LobbyManager(plugin.getName());
		lobbyManager.loadSigns();
		Bukkit.getScheduler().runTask(plugin, new Runnable(){
			public void run(){
				lobbyManager.reset();
			}
		});
		Main.registerWorlds(plugin); // registers worlds containing arenas for use with the event listener
	}

	/**
	 * Registers a plugin with the MGLib API.
	 * @return This object may be used for most API methods, with the exception of some pertaining exclusively to players or rounds.
	 * @param plugin An instance of your plugin (can be substituted with this if called from your main class).
	 * @since 0.1.0
	 */
	public static Minigame registerPlugin(JavaPlugin plugin){
		return new Minigame(plugin);
	}

	/**
	 * Retrieves the {@link JavaPlugin} associated with this {@link Minigame} instance.
	 * @return The {@link JavaPlugin} associated with this {@link Minigame} instance.
	 */
	public JavaPlugin getPlugin(){
		return plugin;
	}

	/**
	 * Retrieves a {@link List list} of all registered {@link Minigame minigame} instances.
	 * @return a {@link List list} of all registered {@link Minigame minigame} instances.
	 * @since 0.1.0
	 */
	public static List<Minigame> getMinigameInstances(){
		return Lists.newArrayList(registeredInstances.values());
	}

	/**
	 * Finds the instance of the MGLib API associated with a given plugin
	 * @param plugin The name of the plugin to search for
	 * @return The instance of the MGLib API (Minigame.class) associated with the given plugin
	 * @since 0.1.0
	 */
	public static Minigame getMinigameInstance(String plugin){
		return registeredInstances.get(plugin);
	}

	/**
	 * Finds the instance of the MGLib API associated with a given plugin
	 * @param plugin The plugin to search for
	 * @return The instance of the MGLib API (Minigame.class) associated with the given plugin
	 * @since 0.1.0
	 */
	public static Minigame getMinigameInstance(JavaPlugin plugin){
		return getMinigameInstance(plugin.getName());
	}

	/**
	 * Retrieves a hashmap containing all rounds associated with the instance which registered this API instance.
	 * @return A hashmap containing all rounds associated with the instance which registered this API instance.
	 * @since 0.1.0
	 */
	public HashMap<String, Round> getRounds(){
		return rounds;
	}

	/**
	 * Retrieves a list containing all rounds associated with the instance which registered this API instance.
	 * @return A list containing all rounds associated with the instance which registered this API instance.
	 * @since 0.1.0
	 */
	public List<Round> getRoundList(){
		return Lists.newArrayList(rounds.values());
	}

	/**
	 * Creates and stores a new round with the given parameters.
	 * @param arena The name of the arena to create the round in.
	 * @return The created round.
	 * @throws ArenaNotExistsException if the given arena does not exist.
	 * @since 0.1.0
	 */
	public Round createRound(String arena) throws ArenaNotExistsException {
		Round r = new Round(plugin.getName(), arena); // create the Round object
		r.setStage(Stage.WAITING); // default to waiting stage
		rounds.put(arena, r); // register arena with MGLib
		return r; // give the calling plugin the Round object
	}

	/**
	 * Gets the instance of the round associated with the given world.
	 * @param name The name of the round to retrieve.
	 * @return The instance of the round associated with the given world.
	 * @since 0.1.0
	 */
	public Round getRound(String name){
		return rounds.get(name);
	}

	/**
	 * Creates an arena for use with MGLib.
	 * @param name The name of the arena (used to identify it).
	 * @param spawn The initial spawn point of the arena (more may be added later).
	 * @param corner1 A corner of the arena.
	 * @param corner2 The corner of the arena opposite <b>corner1</b>.
	 * @return the new arena's {@link ArenaFactory}.
	 * @throws InvalidLocationException if the given locations are not in the same world.
	 * @throws ArenaExistsException if an arena of the same name already exists.
	 * @since 0.1.0
	 */
	public ArenaFactory createArena(String name, Location spawn, Location corner1, Location corner2)
			throws InvalidLocationException, ArenaExistsException {

		double minX = Double.NaN;
		double minY = Double.NaN;
		double minZ = Double.NaN;
		double maxX = Double.NaN;
		double maxY = Double.NaN;
		double maxZ = Double.NaN;
		double x1 = Double.NaN;
		double y1 = Double.NaN;
		double z1 = Double.NaN;
		double x2 = Double.NaN;;
		double y2 = Double.NaN;
		double z2 = Double.NaN;

		if (corner1 != null && corner2 != null){
			if (spawn.getWorld().getName() != corner1.getWorld().getName()) // spawn's in a different world than the first corner
				throw new InvalidLocationException();
			if (spawn.getWorld().getName() != corner2.getWorld().getName()) // spawn's in a different world than the second corner
				throw new InvalidLocationException();

			x1 = corner1.getX();
			y1 = corner1.getY();
			z1 = corner1.getZ();
			x2 = corner2.getX();
			y2 = corner2.getY();
			z2 = corner2.getZ();

			// this whole bit just determines which coords are the maxes and mins
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
		}

		ArenaFactory a = ArenaFactory.createArenaFactory(plugin.getName(), name, spawn.getWorld().getName()).addSpawn(spawn);
		if (minX == minX)
			a.setMinBound(minX, minY, minZ).setMaxBound(maxX, maxY, maxZ);
		return a;
	}

	/**
	 * Creates an arena for use with MGLib.
	 * @param name The name of the arena (used to identify it).
	 * @param spawn The initial spawn point of the arena (more may be added later).
	 * @throws ArenaExistsException if an arena of the same name already exists.
	 * @since 0.1.0
	 */
	public void createArena(String name, Location spawn) throws ArenaExistsException {
		try {
			createArena(name, spawn, null, null);
		}
		catch (InvalidLocationException ex){ // this can never be thrown since only one location is passed
			Main.log.severe("How the HELL did you get this to throw an exception?");
			Main.log.severe("Like, seriously, it should never be possible for this code to be triggered. " +
					"You SERIOUSLY screwed something up.");
			Main.log.warning("And hello to the person reading the library's source, " +
					"since that's the only place this is ever going to be read. Now get back to work.");
		}
	}

	/**
	 * Removes an arena from the plugin's config, effectively deleting it.
	 * @param name The arena to delete.
	 * @throws ArenaNotExistsException if an arena by the specified name does not exist.
	 * @since 0.1.0
	 */
	public void deleteArena(String name) throws ArenaNotExistsException {
		YamlConfiguration y = MGUtil.loadArenaYaml(plugin.getName()); // convenience method for loading the YAML file
		if (!y.contains(name)) // arena doesn't exist
			throw new ArenaNotExistsException();
		y.set(name, null); // remove the arena from the arenas.yml file 
		MGUtil.saveArenaYaml(plugin.getName(), y);
		Round r = Minigame.getMinigameInstance(plugin).getRound(name); // get the Round object if it exists
		if (r != null){
			r.end(); // end the round
			r.destroy(); // get rid of the object (or just its assets)
		}
	}

	/**
	 * Returns the {@link MGPlayer} associated with the given username.
	 * @param player The username to search for.
	 * @return The {@link MGPlayer} associated with the given username, or <b>null</b> if none is found.
	 * @since 0.1.0
	 */
	public MGPlayer getMGPlayer(String player){
		for (Round r : rounds.values()) // iterate registered rounds
			if (r.getMGPlayer(player) != null) // check if the player is in the round
				return r.getMGPlayer(player);
		return null;
	}

	/**
	 * Convenience method for checking if an {@link MGPlayer} is associated with the given username.
	 * <br><br>
	 * This method simply checks if {@link Minigame#getMGPlayer(String) Minigame#getMGPlayer(p)} is <b>null</b>.
	 * @param p The username to search for.
	 * @return Whether an associated {@link MGPlayer} was found.
	 * @since 0.1.0
	 */
	public boolean isPlayer(String p){
		if (getMGPlayer(p) != null) // player object exists
			return true;
		return false;
	}

	/**
	 * Retrieves an {@link ArenaFactory} for the arena of the specified name.
	 * @param name the name of the arena to retrieve an {@link ArenaFactory} for.
	 * @return the arena's {@link ArenaFactory}, or null if one does not exist.
	 * @since 0.1.0
	 */
	public ArenaFactory getArenaFactory(String name){
		return arenaFactories.get(name);
	}

	/**
	 * For use within the library <b><i>only</i></b>. Please do not modify the returned map.
	 * @return a map of arena names and their corresponding {@link ArenaFactory ArenaFactories}.
	 */
	public HashMap<String, ArenaFactory> getArenaFactories(){
		return arenaFactories;
	}

	/**
	 * Retrieves this minigame's rollback manager.
	 * @return this minigame's rollback manager.
	 * @since 0.1.0
	 */
	public RollbackManager getRollbackManager(){
		return rbManager;
	}

	/**
	 * Retrieves this minigame's lobby manager.
	 * @return this minigame's lobby manager.
	 * @since 0.1.0
	 */
	public LobbyManager getLobbyManager(){
		return lobbyManager;
	}

	/**
	 * Retrieves this minigame's config manager.
	 * @return this minigame's config manager.
	 * @since 0.1.0
	 */
	public ConfigManager getConfigManager(){
		return configManager;
	}

	/**
	 * Unsets all static variables in this class. <b>Please do not call this from your plugin unless you want to ruin
	 * everything for everyone.</b>
	 * @since 0.1.0
	 */
	public static void uninitialize(){
		registeredInstances.clear(); // unregister all minigame instances
		registeredInstances = null; // why not?
	}

}
