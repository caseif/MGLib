package net.amigocraft.mglib;

import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.api.Round;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ArenaFactory {

	private String plugin;

	private String name;

	private YamlConfiguration yaml = null;

	private int timerHandle = -1;

	/**
	 * Creates a new {@link ArenaFactory arena} object, used to modify an arena's assets.
	 * @param plugin The plugin this arena is owned by.
	 * @param name The name of this arena.
	 * @since 0.1
	 */
	private ArenaFactory(String plugin, String name){
		this.plugin = plugin;
		this.name = name;
	}
	
	public static ArenaFactory createArenaFactory(String plugin, String name){
		ArenaFactory af = Minigame.getMinigameInstance(plugin).getArenaFactory(name);
		if (af == null)
			return new ArenaFactory(plugin, name);
		else
			return af;
	}

	/**
	 * Adds a spawn to the given arena with the given coordinates, pitch, and yaw.
	 * @param x The x-coordinate of the new spawn.
	 * @param y The y-coordinate of the new spawn.
	 * @param z The z-coordinate of the new spawn.
	 * @param pitch The pitch (x- and z-rotation) of the new spawn.
	 * @param yaw The yaw (y-rotation) of the new spawn.
	 * @since 0.1
	 */
	public ArenaFactory addSpawn(double x, double y, double z, float pitch, float yaw){
		if (yaml == null)
			MGUtil.loadArenaYaml(plugin);
		else
			Bukkit.getScheduler().cancelTask(timerHandle);
		Minigame mg = Minigame.getMinigameInstance(plugin);
		if (Minigame.getMinigameInstance(plugin).getRounds().containsKey(name)){ // check if round is taking place in arena
			Round r = mg.getRound(name); // get the round object
			Location l = new Location(Bukkit.getWorld(r.getWorld()), x, y, z); // self-explanatory
			l.setPitch(pitch);
			l.setYaw(yaw);
			r.getSpawns().add(l); // add spawn to the live round
		}
		ConfigurationSection cs = yaml.getConfigurationSection(name);
		int min; // the minimum available spawn number
		for (min = 0; min > 0; min++) // this feels like a bad idea, but I think it should work
			if (cs.get("spawns." + min) == null)
				break;
		cs.set("spawns." + min + ".x", x);
		cs.set("spawns." + min + ".y", y);
		cs.set("spawns." + min + ".z", z);
		cs.set("spawns." + min + ".pitch", pitch);
		cs.set("spawns." + min + ".yaw", yaw);
		timerHandle = Bukkit.getScheduler().runTaskLaterAsynchronously(mg.getPlugin(), new Runnable(){
			public void run(){
				writeChanges();
			}
		}, 1L).getTaskId();
		return this;
	}

	/**
	 * Adds a spawn to the given arena with the given coordinates.
	 * @param x The x-coordinate of the new spawn.
	 * @param y The y-coordinate of the new spawn.
	 * @param z The z-coordinate of the new spawn.
	 * @return This {@link ArenaFactory} object.
	 * @since 0.1
	 */
	public ArenaFactory addSpawn(double x, double y, double z){
		return addSpawn(x, y, z, 90f, 0f);
	}

	/**
	 * Adds a spawn to the given arena with the given {@link Location}.
	 * @param arena The arena to add the new spawn to.
	 * @param l The location of the new spawn.
	 * @param saveOrientation Whether to save the {@link Location}'s pitch and yaw to the spawn (Defaults to false if omitted).
	 * @return This {@link ArenaFactory} object.
	 * @since 0.1
	 */
	public ArenaFactory addSpawn(Location l, boolean saveOrientation){
		if (saveOrientation)
			return addSpawn(l.getX(), l.getY(), l.getZ(), l.getPitch(), l.getYaw());
		return addSpawn(l.getX(), l.getY(), l.getZ());
	}

	/**
	 * Adds a spawn to the given arena with the given {@link Location}.
	 * @param arena The arena to add the new spawn to.
	 * @param l The location of the new spawn.
	 * @return This {@link ArenaFactory} object.
	 * @since 0.1
	 */
	public ArenaFactory addSpawn(Location l){
		return addSpawn(l, false);
	}

	/**
	 * Deletes a spawn from the given arena at the given coordinates.
	 * @param arena The arena to delete the spawn from.
	 * @param x The x-coordinate of the spawn to delete.
	 * @param y The y-coordinate of the spawn to delete.
	 * @param z The z-coordinate of the spawn to delete.
	 * @return This {@link ArenaFactory} object.
	 * @since 0.1
	 */
	public ArenaFactory deleteSpawn(String arena, double x, double y, double z){
		if (yaml == null)
			MGUtil.loadArenaYaml(plugin);
		else
			Bukkit.getScheduler().cancelTask(timerHandle);
		Minigame mg = Minigame.getMinigameInstance(plugin);
		if (mg.getRound(arena) != null){
			Round r = mg.getRound(arena);
			for (Location l : r.getSpawns())
				if (l.getX() == x && l.getY() == y && l.getZ() == z)
					r.getSpawns().remove(l);
		}
		YamlConfiguration yc = MGUtil.loadArenaYaml(plugin);
		ConfigurationSection spawns = yc.getConfigurationSection("spawns"); // make the code easier to read
		for (String k : spawns.getKeys(false))
			if (spawns.getDouble(k + ".x") == x && spawns.getDouble(k + ".y") == y && spawns.getDouble(k + ".z") == z)
				spawns.set(k, null); // delete it from the config
		timerHandle = Bukkit.getScheduler().runTaskLaterAsynchronously(mg.getPlugin(), new Runnable(){
			public void run(){
				writeChanges();
			}
		}, 1L).getTaskId();
		return this;
	}

	/**
	 * Deletes a spawn from the given arena at the given {@link Location}.
	 * @param l The {@link Location} of the spawn to delete.
	 * @return This {@link ArenaFactory} object.
	 * @since 0.1
	 */
	public ArenaFactory deleteSpawn(String arena, Location l){
		return deleteSpawn(arena, l.getX(), l.getY(), l.getZ());
	}

	/**
	 * Sets the minimum boundary of this arena.
	 * @param x The minimum x-value.
	 * @param y The minimum y-value.
	 * @param z The minimum z-value.
	 * @return This {@link ArenaFactory} object.
	 * @since 0.1
	 */
	public ArenaFactory setMinBound(double x, double y, double z){
		if (yaml == null)
			MGUtil.loadArenaYaml(plugin);
		else
			Bukkit.getScheduler().cancelTask(timerHandle);
		Minigame mg = Minigame.getMinigameInstance(plugin);
		if (Minigame.getMinigameInstance(plugin).getRounds().containsKey(name)){ // check if round is taking place in arena
			Round r = mg.getRound(name); // get the round object
			r.setMinBound(x, y, z); // add spawn to the live round
		}
		ConfigurationSection cs = yaml.getConfigurationSection(name);
		cs.set("minX", x);
		cs.set("minY", y);
		cs.set("minZ", z);
		timerHandle = Bukkit.getScheduler().runTaskLaterAsynchronously(mg.getPlugin(), new Runnable(){
			public void run(){
				writeChanges();
			}
		}, 1L).getTaskId();
		return this;
	}

	/**
	 * Sets the maximum boundary of this arena.
	 * @param x The maximum x-value.
	 * @param y The maximum y-value.
	 * @param z The maximum z-value.
	 * @return This {@link ArenaFactory} object.
	 * @since 0.1
	 */
	public ArenaFactory setMaxBound(double x, double y, double z){
		if (yaml == null)
			MGUtil.loadArenaYaml(plugin);
		else
			Bukkit.getScheduler().cancelTask(timerHandle);
		Minigame mg = Minigame.getMinigameInstance(plugin);
		if (Minigame.getMinigameInstance(plugin).getRounds().containsKey(name)){ // check if round is taking place in arena
			Round r = mg.getRound(name); // get the round object
			r.setMaxBound(x, y, z); // add spawn to the live round
		}
		ConfigurationSection cs = yaml.getConfigurationSection(name);
		cs.set("maxX", x);
		cs.set("maxY", y);
		cs.set("maxZ", z);
		timerHandle = Bukkit.getScheduler().runTaskLaterAsynchronously(mg.getPlugin(), new Runnable(){
			public void run(){
				writeChanges();
			}
		}, 1L).getTaskId();
		return this;
	}

	private void writeChanges(){
		MGUtil.saveArenaYaml(plugin, yaml);
		yaml = null;
	}

}
