package net.amigocraft.mglib.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.amigocraft.mglib.MGLib;
import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.Stage;
import net.amigocraft.mglib.event.MinigameRoundEndEvent;
import net.amigocraft.mglib.event.MinigameRoundPrepareEvent;
import net.amigocraft.mglib.event.MinigameRoundStartEvent;
import net.amigocraft.mglib.event.PlayerJoinMinigameRoundEvent;
import net.amigocraft.mglib.event.PlayerLeaveMinigameRoundEvent;
import net.amigocraft.mglib.exception.ArenaNotExistsException;
import net.amigocraft.mglib.exception.PlayerNotPresentException;
import net.amigocraft.mglib.exception.PlayerOfflineException;

/**
 * Represents a round within a minigame.
 * @author Maxim Roncacé
 * @since 0.1
 */
public class Round {

	private int prepareTime;
	private int roundTime;

	private String plugin;
	private int time = 0;
	private Stage stage;

	private String world;
	private String arena;
	private List<Location> spawns = new ArrayList<Location>();
	private Location minBound;
	private Location maxBound;

	private HashMap<String, MGPlayer> players = new HashMap<String, MGPlayer>();

	private int timerHandle = -1;

	/**
	 * Creates a new {@link Round} with the given parameters.
	 * <br><br>
	 * Please use {@link Minigame#createRound(String, boolean, int, int) Minigame#createRound()} unless you
	 * understand the implications of using this constructor.
	 * @param plugin The plugin which this round should be associated with.
	 * @param arena The name of the arena in which this round takes place in.
	 * @param discrete Whether this round will take place in a discrete world (uses a defined arena when false).
	 * @param preparationTime The round's total preparation time. Use -1 for no limit, or 0 for no preparation phase.
	 * @param roundTime The round's total playing time. Use -1 for no limit.
	 * @throws ArenaNotExistsException if the specified arena does not exist.
	 */
	public Round(String plugin, String arena, boolean discrete, int preparationTime, int roundTime)
			throws ArenaNotExistsException {
		YamlConfiguration y = MGUtil.loadArenaYaml(plugin);
		if (!y.contains(arena))
			throw new ArenaNotExistsException();
		ConfigurationSection cs = y.getConfigurationSection(arena); // make the code easier to read
		world = cs.getString("world"); // get the name of the world of the arena
		World w = Bukkit.getWorld(world); // convert it to a Bukkit world
		if (w == null) // but what if world is kill?
			throw new IllegalArgumentException("World " + world + " cannot be loaded!"); // then round is kill
		for (String k : cs.getConfigurationSection("spawns").getKeys(false)){ // load spawns into round object
			Location l = new Location(w, cs.getDouble("spawns." + k + ".x"),
					cs.getDouble("spawns." + k + ".y"),
					cs.getDouble("spawns." + k + ".z"));
			if (cs.isSet(k + ".pitch"))
				l.setPitch((float)cs.getDouble(cs.getCurrentPath() + ".spawns." + k + ".pitch"));
			if (cs.isSet(k + ".yaw"))
				l.setYaw((float)cs.getDouble(cs.getCurrentPath() + ".spawns." + k + ".yaw"));
			spawns.add(l); // register spawn
		}
		if (cs.getBoolean("boundaries")){ // check if arena has boundaries defined
			minBound = new Location(w, cs.getDouble("minX"), cs.getDouble("minY"), cs.getDouble("minZ"));
			maxBound = new Location(w, cs.getDouble("maxX"), cs.getDouble("maxY"), cs.getDouble("maxZ"));
		}
		else {
			minBound = null;
			maxBound = null;
		}
		this.plugin = plugin; // set globals
		this.arena = arena;
		stage = Stage.WAITING; // default to waiting stage
		Minigame.getMinigameInstance(plugin).getRounds().put(arena, this); // register round with minigame instance
	}

	/**
	 * Gets the name of the minigame plugin associated with this {@link Round}.
	 * @return The name of the minigame plugin associated with this {@link Round}. 
	 * @since 0.1
	 */
	public String getPlugin(){
		return plugin;
	}

	/**
	 * Gets the instance of the MGLib API registered by the plugin associated with this {@link Round}.
	 * @return The instance of the MGLib API registered by the plugin associated with this {@link Round}.
	 * @since 0.1
	 */
	public Minigame getMinigame(){
		return Minigame.getMinigameInstance(plugin);
	}

	/**
	 * Gets the name of the arena associated with this {@link Round}.
	 * @return The name of the arena associated with this {@link Round}.
	 * @since 0.1
	 */
	public String getArena(){
		return arena;
	}

	/**
	 * Gets the current {@link Stage} of this {@link Round}.
	 * @return The current {@link Stage} of this {@link Round}.
	 * @since 0.1
	 */
	public Stage getStage(){
		return stage;
	}

	/**
	 * Gets the current time remaining in this {@link Round}.
	 * @return The current time remaining in this {@link Round}.
	 * @since 0.1
	 */
	public int getTime(){
		return time;
	}

	/**
	 * Gets the round's preparation time.
	 * @return The round's preparation time.
	 * @since 0.1
	 */
	public int getPreparationTime(){
		return prepareTime;
	}

	/**
	 * Gets the round's playing time.
	 * @return The round's playing time.
	 * @since 0.1
	 */
	public int getPlayingTime(){
		return roundTime;
	}

	/**
	 * Gets the round's timer's task's handle, or -1 if a timer is not started.
	 * @return The round's timer's task's handle, or -1 if a timer is not started.
	 * @since 0.1
	 */
	public int getTimerHandle(){
		return timerHandle;
	}

	/**
	 * Sets the associated arena of this {@link Round}.
	 * @param arena The arena to associate with this {@link Round}.
	 * @since 0.1
	 */
	public void setArena(String arena){
		this.arena = arena;
	}

	/**
	 * Sets the current stage of this {@link Round}.
	 * @param s The stage to set this {@link Round} to.
	 * @since 0.1
	 */
	public void setStage(Stage s){
		stage = s;
	}

	/**
	 * Sets the remaining time of this {@link Round}.
	 * @param t The time to set this {@link Round} to.
	 * @since 0.1
	 */
	public void setTime(int t){
		time = t;
	}

	/**
	 * Sets the round's preparation time.
	 * @param t The number of seconds to set the preparation time to. Use -1 for no limit, or 0 for
	 * no preparation phase.
	 * @since 0.1
	 */
	public void setPreparationTime(int t){
		prepareTime = t;
	}

	/**
	 * Sets the round's playing time.
	 * @param t The number of seconds to set the preparation time to. Use -1 for no limit.
	 * @since 0.1
	 */
	public void setPlayingTime(int t){
		roundTime = t;
	}

	/**
	 * Decrements the time remaining in the round by 1.
	 * <br><br>
	 * Please do not call this method from your plugin unless you understand the implications. Let MGLib handle
	 * the timer.
	 * @since 0.1
	 */
	public void tickDown(){
		time -= 1;
	}

	/**
	 * Subtracts <b>t</b> seconds from the remaining time in the round.
	 * @param t The number of seconds to subtract.
	 * @since 0.1
	 */
	public void subtractTime(int t){
		time -= t;
	}

	/**
	 * Adds <b>t</b> seconds from the remaining time in the round.
	 * @param t The number of seconds to add.
	 * @since 0.1
	 */
	public void addTime(int t){
		time += t;
	}

	/**
	 * Destroys this {@link Round}.
	 * <br><br>
	 * Please do not call this method from your plugin unless you understand the implications.
	 * @since 0.1
	 */
	public void destroy(){
		Minigame.getMinigameInstance(plugin).getRounds().remove(this);
	}

	/**
	 * Retrieves a list of {@link MGPlayer}s in this {@link Round}.
	 * @return A list of {@link MGPlayer}s in this {@link Round}.
	 * @since 0.1
	 */
	public List<MGPlayer> getPlayerList(){
		return Lists.newArrayList(players.values());
	}

	/**
	 * Retrieves a hashmap of {@link MGPlayer}s in this {@link Round}.
	 * @return A hashmap of {@link MGPlayer}s in this {@link Round}, with their name as a key.
	 * @since 0.1
	 */
	public HashMap<String, MGPlayer> getPlayers(){
		return players;
	}

	/**
	 * Begin the round and start its timer. If the round's current stage is {@link Stage#PREPARING}, it will
	 * be set to {@link Stage#PLAYING} and the timer will be reset when it reaches 0. Otherwise, its stage
	 * will be set to {@link Stage#PREPARING} and it will begins its preparation stage.
	 * <br><br>
	 * After it finishes its preparation, it will begin as it would if this method were called again (don't
	 * actually call it again though, or you'll trigger an exception).
	 * @throws IllegalStateException if the stage is already {@link Stage#PLAYING}.
	 * @since 0.1
	 */
	public void startRound(){
		if (stage != Stage.PLAYING){ // make sure the round isn't already started
			final Round r = this;
			r.setTime(r.getPreparationTime()); // reset time
			r.setStage(Stage.PREPARING); // set stage to preparing
			//TODO: Skip straight to playing if necessary and check this method for bugs
			Bukkit.getPluginManager().callEvent(new MinigameRoundPrepareEvent(r)); // call an event for anyone who cares
			if (time != -1){ // I'm pretty sure this is wrong, but I'm also pretty tired
				timerHandle = Bukkit.getScheduler().runTaskTimer(MGLib.plugin, new Runnable(){
					public void run(){
						r.tickDown(); // tick timer down by one
						if (r.getTime() <= 0){ // timer ran out
							if (r.getStage() == Stage.PREPARING){ // if we're still preparing...
								r.setStage(Stage.PLAYING); // ...set stage to playing
								r.setTime(r.getPlayingTime()); // reset timer
								Bukkit.getPluginManager().callEvent(new MinigameRoundStartEvent(r));
							}
							else // we're playing and the round just ended
								endRound(true);
						}
					}
				}, 0L, 20L).getTaskId(); // iterates once per second
			}
		}
		else
			throw new IllegalStateException(Bukkit.getPluginManager().getPlugin(plugin) +
					" attempted to start a round which had already been started.");
	}

	/**
	 * Ends the round and resets its timer. The stage will also be set to {@link Stage#WAITING}.
	 * @throws IllegalStateException if the timer has not been started.
	 * @since 0.1
	 */
	public void endRound(boolean timeUp){
		setTime(-1);
		if (timerHandle != -1)
			Bukkit.getScheduler().cancelTask(timerHandle); // cancel the round's timer task
		stage = Stage.WAITING; // set stage back to waiting
		timerHandle = -1; // reset timer handle since the task no longer exists
		for (MGPlayer mp : getPlayerList()){ // iterate and remove players
			try {
				removePlayer(mp.getName());
			}
			catch (PlayerOfflineException ex){}
		}
		Bukkit.getPluginManager().callEvent(new MinigameRoundEndEvent(this, timeUp));
		getMinigame().getRollbackManager().rollback(getArena()); // roll back arena
	}

	/**
	 * Ends the round and resets its timer. The stage will also be set to {@link Stage#WAITING}.
	 * @throws IllegalStateException if the timer has not been started.
	 * @since 0.1
	 */
	public void endRound(){
		endRound(false);
	}

	/**
	 * Retrieves the location representing the minimum boundary on all three axes of the arena this round takes place in.
	 * @return the location representing the minimum boundary on all three axes of the arena this round takes place in, or
	 * null if the arena does not have boundaries.
	 * @since 0.1
	 */
	public Location getMinimumBoundary(){
		return minBound;
	}

	/**
	 * Retrieves the location representing the maximum boundary on all three axes of the arena this round takes place in.
	 * @return the location representing the maximum boundary on all three axes of the arena this round takes place in, or
	 * null if the arena does not have boundaries.
	 * @since 0.1
	 */
	public Location getMaximumBoundary(){
		return maxBound;
	}

	/**
	 * Retrieves a list of possible spawns for this round's arena.
	 * @return a list of possible spawns for this round's arena.
	 * @since 0.1
	 */
	public List<Location> getSpawns(){
		return spawns;
	}

	/**
	 * Returns the {@link MGPlayer} in this round associated with the given username.
	 * @param player The username to search for.
	 * @return The {@link MGPlayer} in this round associated with the given username, or <b>null</b> if none is found.
	 * @since 0.1
	 */
	public MGPlayer getMGPlayer(String player){
		return players.get(player);
	}

	/**
	 * Retrieves the world of this arena.
	 * @return The name of the world containing this arena.
	 * @since 0.1
	 */
	public String getWorld(){
		return world;
	}

	/**
	 * Adds a player by the given name to this {@link Round round}.
	 * @param name The player to add to this {@link Round round}.
	 * @throws PlayerOfflineException if the player is not online
	 * @since 0.1
	 */
	public void addPlayer(String name) throws PlayerOfflineException {
		Player p = Bukkit.getPlayer(name);
		if (p == null) // check that the specified player is online
			throw new PlayerOfflineException();
		MGPlayer mp = null;
		for (Round r : Minigame.getMinigameInstance(plugin).getRoundList()) // reuse the old MGPlayer if it exists
			if (r.players.containsKey(name)){
				mp = r.players.get(name);
				r.players.remove(name);
				r.players.get(name).setArena(arena);
				break;
			}
		if (mp == null)
			mp = new MGPlayer(plugin, name, arena); // otherwise make a new one
		mp.setDead(false); // make sure they're not dead the second they join
		players.put(name, mp); // register player with round object
		Location spawn = spawns.get(new Random().nextInt(spawns.size())); // pick a random spawn
		p.teleport(spawn); // teleport the player to it
		Bukkit.getPluginManager().callEvent(new PlayerJoinMinigameRoundEvent(this, mp));
	}

	/**
	 * Removes a given player from this {@link Round round} and teleports them to the given location.
	 * @param name The player to remove from this {@link Round round).
	 * @param location The location to teleport the player to.
	 * @throws PlayerOfflineException if the player is not online.
	 * @throws PlayerNotPresentException if the player are not in this round.
	 * @since 0.1
	 */
	public void removePlayer(String name, Location location) throws PlayerOfflineException, PlayerNotPresentException {
		Player p = Bukkit.getPlayer(name);
		MGPlayer mp = players.get(name);
		if (mp == null)
			throw new PlayerNotPresentException();
		if (p != null){
			mp.setArena(null); // they're not in an arena anymore
			mp.setDead(false); // make sure they're not dead when they join a new round
			players.remove(name); // remove player from round
			mp.reset(location); // reset the object and send the player to the exit point
		}
		Bukkit.getPluginManager().callEvent(new PlayerLeaveMinigameRoundEvent(this, mp));
	}

	/**
	 * Removes a given player from this {@link Round round} and teleports them to the main world's spawn.
	 * @param name The player to remove from this {@link Round round}. 
	 * @since 0.1
	 */
	public void removePlayer(String name) throws PlayerOfflineException {
		try {
			removePlayer(name, Minigame.getMinigameInstance(plugin).getExitLocation());
		}
		catch (PlayerOfflineException ex){}
		catch (PlayerNotPresentException e2){} // neither of these can happen, and if they do, we have bigger problems to worry about
	}

	public boolean equals(Object p){
		Round r = (Round)p;
		return arena.equals(r.getArena());
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + arena.hashCode() + 41);
	}
}
