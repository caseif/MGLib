package net.amigocraft.mglib.api;

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
import net.amigocraft.mglib.RollbackManager;
import net.amigocraft.mglib.Stage;
import net.amigocraft.mglib.event.MinigameRoundEndEvent;
import net.amigocraft.mglib.event.MinigameRoundPrepareEvent;
import net.amigocraft.mglib.event.MinigameRoundStartEvent;
import net.amigocraft.mglib.event.PlayerJoinMinigameRoundEvent;
import net.amigocraft.mglib.event.PlayerLeaveMinigameRoundEvent;

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
	private List<Location> spawns;
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
	 * @throws IllegalArgumentException if the specified arena does not exist or the world of the specified arena does not exist.
	 * @since 0.1
	 */
	public Round(String plugin, String arena, boolean discrete, int preparationTime, int roundTime)
			throws IllegalArgumentException {
		YamlConfiguration y = MGUtil.loadArenaYaml(plugin);
		if (!y.contains(arena))
			throw new IllegalArgumentException("Error occurred while creating round for " +
					Minigame.getMinigameInstance(plugin).getPlugin() + ": specified arena does not exist");
		ConfigurationSection cs = y.getConfigurationSection(arena);
		world = cs.getString(world);
		World w = Bukkit.getWorld(world);
		if (w == null)
			throw new IllegalArgumentException("Error occurred while creating round for " +
					Minigame.getMinigameInstance(plugin).getPlugin() + ": world of the specified arena does not exist");
		for (String k : cs.getConfigurationSection("spawns").getKeys(false)){
			Location l = new Location(w, cs.getDouble(k + ".x"), cs.getDouble(k + ".y"), cs.getDouble(k + ".z"));
			l.setPitch((float)cs.getDouble(k + ".pitch"));
			l.setYaw((float)cs.getDouble(k + ".yaw"));
		}
		if (cs.getBoolean("boundaries")){
			minBound = new Location(w, cs.getDouble("minX"), cs.getDouble("minY"), cs.getDouble("minZ"));
			minBound = new Location(w, cs.getDouble("maxX"), cs.getDouble("maxY"), cs.getDouble("maxZ"));
		}
		else {
			minBound = null;
			maxBound = null;
		}
		this.plugin = plugin;
		this.arena = arena;
		stage = Stage.WAITING;
		Minigame.getMinigameInstance(plugin).getRounds().put(arena, this);
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
		if (stage != Stage.PLAYING){
			final Round r = this;
			r.setTime(r.getPreparationTime());
			r.setStage(Stage.PREPARING);
			Bukkit.getPluginManager().callEvent(new MinigameRoundPrepareEvent(r));
			if (time != -1){
				timerHandle = Bukkit.getScheduler().runTaskTimer(MGLib.plugin, new Runnable(){
					public void run(){
						r.tickDown();
						if (r.getTime() <= 0){
							if (r.getStage() == Stage.PREPARING){
								r.setStage(Stage.PLAYING);
								r.setTime(r.getPlayingTime());
								Bukkit.getPluginManager().callEvent(new MinigameRoundStartEvent(r));
							}
							else {
								endRound(true);
							}
						}
					}
				}, 0L, 20L).getTaskId();
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
			Bukkit.getScheduler().cancelTask(timerHandle);
		stage = Stage.WAITING;
		timerHandle = -1;
		for (MGPlayer mp : getPlayerList())
			removePlayer(mp.getName());
		Bukkit.getPluginManager().callEvent(new MinigameRoundEndEvent(this, timeUp));
		RollbackManager.rollback(getArena());
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
		for (MGPlayer p : getPlayerList())
			if (p.getName().equals(player))
				return p;
		return null;
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
	 * @throws IllegalArgumentException if the given player is not online
	 * @since 0.1
	 */
	public void addPlayer(String name) throws IllegalArgumentException{
		Player p = Bukkit.getPlayer(name);
		if (p == null) // check that the specified player is online
			throw new IllegalArgumentException("\"" + name + "\" is not presently online");
		MGPlayer mp = null;
		for (Round r : Minigame.getMinigameInstance(plugin).getRoundList()) // reuse the old MGPlayer if it exists
			if (r.players.containsKey(name)){
				mp = r.players.get(name);
				r.players.remove(name);
				r.players.get(name).setArena(arena);
				break;
			}
		if (mp == null)
			mp = new MGPlayer(plugin, arena); // otherwise make a new one
		mp.setDead(false); // make sure they're not dead the second they join
		players.put(name, mp);
		Location spawn = spawns.get(new Random(spawns.size()).nextInt()); // pick a random spawn
		p.teleport(spawn); // teleport the player to it
		Bukkit.getPluginManager().callEvent(new PlayerJoinMinigameRoundEvent(this, mp));
	}
	
	/**
	 * Removes a given player from this {@link Round round} and teleports them to the given location.
	 * @param name The player to remove from this {@link Round round).
	 * @param location The location to teleport the player to.
	 * @throws IllegalArgumentException if the given player is not online, or if they are not in this round.
	 * @since 0.1
	 */
	public void removePlayer(String name, Location location) throws IllegalArgumentException {
		Player p = Bukkit.getPlayer(name);
		if (p == null) // check that the specified player is online
			throw new IllegalArgumentException("\"" + name + "\" is not presently online");
		MGPlayer mp = players.get(name);
		if (mp == null)
			throw new IllegalArgumentException("\"" + name + "\" is not in the specified round");
		mp.setArena(null);
		mp.setDead(false); // make sure they're not dead when they join a new round
		players.remove(name);
		mp.reset(location);
		Bukkit.getPluginManager().callEvent(new PlayerLeaveMinigameRoundEvent(this, mp));
	}
	
	/**
	 * Removes a given player from this {@link Round round} and teleports them to the main world's spawn.
	 * @param name The player to remove from this {@link Round round}.
	 * @since 0.1
	 */
	public void removePlayer(String name){
		removePlayer(name, Minigame.getMinigameInstance(plugin).getExitLocation());
	}

	public boolean equals(Object p){
		Round r = (Round)p;
		return arena.equals(r.getArena());
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + arena.hashCode() + 41);
	}
}
