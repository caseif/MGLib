package net.amigocraft.mglib.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.amigocraft.mglib.MGLib;
import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.RollbackManager;
import net.amigocraft.mglib.Stage;
import net.amigocraft.mglib.exception.ArenaExistsException;
import net.amigocraft.mglib.exception.ArenaNotExistsException;
import net.amigocraft.mglib.exception.InvalidLocationException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
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
 * @version 0.1-dev16
 * @since 0.1
 */
public class Minigame {

	private static HashMap<String, Minigame> registeredInstances = new HashMap<String, Minigame>();

	private JavaPlugin plugin;

	private HashMap<String, Round> rounds = new HashMap<String, Round>();

	private Location exitLocation = null;

	private RollbackManager rbManager = null;

	/**
	 * Creates a new instance of the MGLib API. This object may be used for all API methods
	 * @param plugin An instance of your plugin.
	 * @param approvedVersions The approved versions of MGLib for your plugin.
	 * @since 0.1
	 */
	public Minigame(JavaPlugin plugin, List<String> approvedVersions){
		if (!registeredInstances.containsKey(plugin.getName())){
			this.plugin = plugin;
			this.exitLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
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
				MGLib.log.info("Type /mglib v " + plugin.getName() + " to see a list of compatible MGLib versions");
				//TODO: Actually implement this ^
			}
			if (dev)
				MGLib.log.warning(plugin + " was tested only against development version(s) of MGLib. " +
						"As such, it may not be fully compatible with the installed instance of the library. Please " +
						"notify the developer of " + plugin.getName() + " so he/she may take appropriate action.");
			registeredInstances.put(plugin.getName(), this);
			MGLib.log.info(plugin + " has successfully hooked into MGLib!");
		}
		else
			throw new IllegalArgumentException(plugin + " attempted to hook into MGLib while an instance of the API was already " +
					"registered. Please report this to the plugin author.");
		rbManager = new RollbackManager(plugin);
		rbManager.checkRollbacks();
		MGLib.registerWorlds(plugin);
	}

	/**
	 * Creates a new instance of the MGLib API. This object may be used for all API methods
	 * @param plugin An instance of your plugin.
	 * @param approvedVersion The approved version of MGLib for your plugin.\
	 * @since 0.1
	 */
	@SuppressWarnings("serial")
	public Minigame(JavaPlugin plugin, final String approvedVersion){
		this(plugin, new ArrayList<String>(){{add(approvedVersion);}});
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
				if (version.contains("dev"))
					if (Integer.parseInt(version.split("dev")[1]) <= MGLib.lastDev)
						return true;
					else
						return false;
				else
					return true;

		return false;
	}

	/**
	 * Retrieves a {@link List list} of all registered {@link Minigame minigame} instances.
	 * @return a {@link List list} of all registered {@link Minigame minigame} instances.
	 * @since 0.1
	 */
	public static List<Minigame> getMinigameInstances(){
		return Lists.newArrayList(registeredInstances.values());
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
	 * Retrieves a hashmap containing all rounds associated with the instance which registered this API instance.
	 * @return A hashmap containing all rounds associated with the instance which registered this API instance.
	 * @since 0.1
	 */
	public HashMap<String, Round> getRounds(){
		return rounds;
	}

	/**
	 * Retrieves a list containing all rounds associated with the instance which registered this API instance.
	 * @return A list containing all rounds associated with the instance which registered this API instance.
	 * @since 0.1
	 */
	public List<Round> getRoundList(){
		return Lists.newArrayList(rounds.values());
	}

	/**
	 * Creates and stores a new round with the given parameters.
	 * @param arena The name of the arena to create the round in.
	 * @param preparationTime The time (in seconds) the round should be kept in the preparation stage for)
	 * @param roundTime The time (in seconds) the round should last for. Set to 0 for no limit.
	 * @return The created round.
	 * @throws ArenaNotExistsException if the given arena does not exist.
	 * @since 0.1
	 */
	public Round createRound(String arena, boolean discrete, int preparationTime, int roundTime) throws ArenaNotExistsException {
		Round r = new Round(plugin.getName(), arena, discrete, preparationTime, roundTime);
		r.setStage(Stage.WAITING);
		rounds.put(arena, r);
		return r;
	}

	/**
	 * Gets the instance of the round associated with the given world.
	 * @param name The name of the round to retrieve.
	 * @return The instance of the round associated with the given world.
	 * @since 0.1
	 */
	public Round getRound(String name){
		for (Round r : rounds.values()){
			if (r.getArena().equals(name))
				return r;
		}
		return null;
	}

	/**
	 * Creates an arena for use with MGLib.
	 * @param name The name of the arena (used to identify it).
	 * @param spawn The initial spawn point of the arena (more may be added later).
	 * @param corner1 A corner of the arena.
	 * @param corner2 The corner of the arena opposite <b>corner1</b>.
	 * @throws InvalidLocationException if the given locations are not in the same world.
	 * @throws ArenaExistsException if an arena of the same name already exists.
	 * @since 0.1
	 */
	public void createArena(String name, Location spawn, Location corner1, Location corner2)
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
			if (spawn.getWorld().getName() != corner1.getWorld().getName())
				throw new InvalidLocationException();
			if (spawn.getWorld().getName() != corner2.getWorld().getName())
				throw new InvalidLocationException();

			x1 = corner1.getX();
			y1 = corner1.getY();
			z1 = corner1.getZ();
			x2 = corner2.getX();
			y2 = corner2.getY();
			z2 = corner2.getZ();

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

		YamlConfiguration y = MGUtil.loadArenaYaml(plugin.getName());
		if (y != null){
			if (y.contains(name))
				throw new ArenaExistsException();
			y.createSection(name);
			ConfigurationSection c = y.getConfigurationSection(name);
			c.set("world", spawn.getWorld().getName());
			c.set("spawns.0.x", spawn.getX());
			c.set("spawns.0.y", spawn.getY());
			c.set("spawns.0.z", spawn.getZ());
			c.set("spawns.0.pitch", spawn.getPitch());
			c.set("spawns.0.yaw", spawn.getYaw());
			if (corner1 != null){
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
			MGUtil.saveArenaYaml(plugin.getName(), y);
			MGLib.registerWorlds(plugin);
		}
	}

	/**
	 * Creates an arena for use with MGLib.
	 * @param name The name of the arena (used to identify it).
	 * @param spawn The initial spawn point of the arena (more may be added later).
	 * @throws ArenaExistsException if an arena of the same name already exists.
	 * @since 0.1
	 */
	public void createArena(String name, Location spawn) throws ArenaExistsException {
		try {
			createArena(name, spawn, null, null);
		}
		catch (InvalidLocationException ex){
			MGLib.log.severe("How the HELL did you get this to throw an exception?");
			MGLib.log.severe("Like, seriously, it should never be possible for this code to be triggered. " +
					"You SERIOUSLY screwed something up.");
			MGLib.log.severe("And hello to the person reading the library's source, " +
					"since that's the only place this is ever going to be read. Now get back to work.");
		}
	}

	/**
	 * Removes an arena from the plugin's config, effectively deleting it.
	 * @param name The arena to delete.
	 * @throws ArenaNotExistsException if an arena by the specified name does not exist.
	 * @since 0.1
	 */
	public void deleteArena(String name) throws ArenaNotExistsException {
		for (Round r : getRoundList())
			if (r.getArena().equals(name))
				r.endRound();
		YamlConfiguration y = MGUtil.loadArenaYaml(plugin.getName());
		if (!y.contains(name))
			throw new ArenaNotExistsException();
		y.set(name, null);
		Round r = Minigame.getMinigameInstance(plugin).getRound(name);
		if (r != null){
			r.endRound();
			r.destroy();
		}
	}

	/**
	 * Returns the {@link MGPlayer} associated with the given username.
	 * @param player The username to search for.
	 * @return The {@link MGPlayer} associated with the given username, or <b>null</b> if none is found.
	 * @since 0.1
	 */
	public MGPlayer getMGPlayer(String player){
		for (Round r : rounds.values())
			for (MGPlayer p : r.getPlayerList())
				if (p.getName().equals(player))
					return p;
		return null;
	}

	/**
	 * Convenience method for checking if an {@link MGPlayer} is associated with the given username.
	 * <br><br>
	 * This method simply checks if {@link Minigame#getMGPlayer(String) Minigame#getMGPlayer(p)} is <b>null</b>.
	 * @param p The username to search for.
	 * @return Whether an associated {@link MGPlayer} was found.
	 * @since 0.1
	 */
	public boolean isPlayer(String p){
		if (getMGPlayer(p) != null)
			return true;
		return false;
	}

	/**
	 * Adds a spawn to the given arena with the given coordinates, pitch, and yaw.
	 * @param arena The arena to add the new spawn to.
	 * @param x The x-coordinate of the new spawn.
	 * @param y The y-coordinate of the new spawn.
	 * @param z The z-coordinate of the new spawn.
	 * @param pitch The pitch (x- and z-rotation) of the new spawn.
	 * @param yaw The yaw (y-rotation) of the new spawn.
	 * @since 0.1
	 */
	public void addSpawn(String arena, double x, double y, double z, float pitch, float yaw){
		if (rounds.containsKey(arena)){
			Round r = rounds.get(arena);
			Location l = new Location(Bukkit.getWorld(r.getWorld()), x, y, z);
			l.setPitch(pitch);
			l.setYaw(yaw);
			r.getSpawns().add(l);
		}
		YamlConfiguration yc = MGUtil.loadArenaYaml(plugin.getName());
		int min;
		for (min = 0; min > 0; min++)
			if (yc.get("spawns." + min) != null)
				break;
		yc.set("spawns." + min + ".x", x);
		yc.set("spawns." + min + ".y", y);
		yc.set("spawns." + min + ".z", z);
		yc.set("spawns." + min + ".pitch", pitch);
		yc.set("spawns." + min + ".yaw", yaw);
		MGUtil.saveArenaYaml(plugin.getName(), yc);
	}

	/**
	 * Adds a spawn to the given arena with the given coordinates.
	 * @param arena The arena to add the new spawn to.
	 * @param x The x-coordinate of the new spawn.
	 * @param y The y-coordinate of the new spawn.
	 * @param z The z-coordinate of the new spawn.
	 * @since 0.1
	 */
	public void addSpawn(String arena, double x, double y, double z){
		addSpawn(arena, x, y, z, 90f, 0f);
	}

	/**
	 * Adds a spawn to the given arena with the given {@link Location}.
	 * @param arena The arena to add the new spawn to.
	 * @param l The location of the new spawn.
	 * @param saveOrientation Whether to save the {@link Location}'s pitch and yaw to the spawn.
	 * @since 0.1
	 */
	public void addSpawn(String arena, Location l, boolean saveOrientation){
		if (saveOrientation)
			addSpawn(arena, l.getX(), l.getY(), l.getZ(), l.getPitch(), l.getYaw());
		else
			addSpawn(arena, l.getX(), l.getY(), l.getZ());
	}

	/**
	 * Deletes a spawn from the given arena at the given coordinates.
	 * @param arena The arena to delete the spawn from.
	 * @param x The x-coordinate of the spawn to delete.
	 * @param y The y-coordinate of the spawn to delete.
	 * @param z The z-coordinate of the spawn to delete.
	 * @since 0.1
	 */
	public void deleteSpawn(String arena, double x, double y, double z){
		if (rounds.containsKey(arena)){
			Round r = rounds.get(arena);
			for (Location l : r.getSpawns())
				if (l.getX() == x && l.getY() == y && l.getZ() == z)
					r.getSpawns().remove(l);
		}
		YamlConfiguration yc = MGUtil.loadArenaYaml(plugin.getName());
		ConfigurationSection spawns = yc.getConfigurationSection("spawns");
		for (String k : spawns.getKeys(false))
			if (yc.getDouble(k + ".x") == x && yc.getDouble(k + ".y") == y && yc.getDouble(k + ".z") == z)
				yc.set(k, null);
		MGUtil.saveArenaYaml(plugin.getName(), yc);
	}

	/**
	 * Deletes a spawn from the given arena at the given {@link Location}.
	 * @param l The {@link Location} of the spawn to delete.
	 * @since 0.1
	 */
	public void deleteSpawn(String arena, Location l){
		deleteSpawn(arena, l.getX(), l.getY(), l.getZ());
	}

	/**
	 * Retrieves the {@link Location location} to teleport players to upon exiting a {@link Round round}.
	 * @return the {@link Location location} to teleport players to upon exiting a {@link Round round}.
	 * @since 0.1
	 */
	public Location getExitLocation(){
		return exitLocation;
	}

	/**
	 * Sets the {@link Location location} to teleport players to upon exiting a {@link Round round}.
	 * @param location The location to teleport players to upon exiting a {@link Round round}.
	 * @since 0.1
	 */
	public void setExitLocation(Location location){
		exitLocation = location;
	}

	/**
	 * Retrieves this minigame's rollback manager
	 * @return this minigame's rollback manager
	 * @since 0.1
	 */
	public RollbackManager getRollbackManager(){
		return rbManager;
	}

	/**
	 * Unsets all static variables in this class. <b>Please do not call this from your plugin unless you want to ruin
	 * everything for everyone.</b>
	 * @since 0.1
	 */
	public static void uninitialize(){
		registeredInstances.clear();
		registeredInstances = null;
	}

}
