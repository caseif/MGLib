package net.amigocraft.mglib.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.amigocraft.mglib.Main;
import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.event.player.PlayerJoinMinigameRoundEvent;
import net.amigocraft.mglib.event.player.PlayerLeaveMinigameRoundEvent;
import net.amigocraft.mglib.event.round.MinigameRoundEndEvent;
import net.amigocraft.mglib.event.round.MinigameRoundPrepareEvent;
import net.amigocraft.mglib.event.round.MinigameRoundStartEvent;
import net.amigocraft.mglib.event.round.MinigameRoundTickEvent;
import net.amigocraft.mglib.exception.ArenaNotExistsException;
import net.amigocraft.mglib.exception.PlayerNotPresentException;
import net.amigocraft.mglib.exception.PlayerOfflineException;

/**
 * Represents a round within a minigame.
 * @author Maxim Roncacé
 * @since 0.1
 */
public class Round {

	private int maxPlayers;
	private int prepareTime;
	private int roundTime;
	private Location exitLocation;

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
	 * Please use {@link Minigame#createRound(String)} unless you
	 * understand the implications of using this constructor.
	 * @param plugin the plugin which this round should be associated with.
	 * @param arena the name of the arena in which this round takes place in.
	 * @throws ArenaNotExistsException if the specified arena does not exist.
	 */
	public Round(String plugin, String arena) throws ArenaNotExistsException {
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
		ConfigManager cm = Minigame.getMinigameInstance(plugin).getConfigManager();
		this.prepareTime = cm.getDefaultPreparationTime();
		this.roundTime = cm.getDefaultPlayingTime();
		this.maxPlayers = cm.getMaxPlayers();
		this.exitLocation = cm.getDefaultExitLocation();
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
	 * Gets the current time in seconds of this {@link Round}, where 0 represents the first second of it.
	 * @return the current time in seconds of this {@link Round}, where 0 represents the first second of it.
	 * @since 0.1
	 */
	public int getTime(){
		return time;
	}

	/**
	 * Gets the time remaining in this round.
	 * @return the time remaining in this round, or -1 if there is no time limit or if the {@link Stage stage} is not
	 * {@link Stage#PLAYING PLAYING} or {@link Stage#PREPARING PREPARING}
	 * @since 0.1
	 */
	public int getRemainingTime(){
		switch (this.getStage()){
		case PREPARING:
			if (this.getPreparationTime() > 0)
				return this.getPreparationTime() - this.getTime();
			else
				return -1;
		case PLAYING:
			if (this.getPlayingTime() > 0)
				return this.getPlayingTime() - this.getTime();
			else
				return -1;
		default:
			return -1;
		}
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
	public void tick(){
		time += 1;
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
	public void start(){
		if (stage == Stage.WAITING){ // make sure the round isn't already started
			final Round r = this;
			if (r.getPreparationTime() > 0){
				r.setTime(0); // reset time
				r.setStage(Stage.PREPARING); // set stage to preparing
				Bukkit.getPluginManager().callEvent(new MinigameRoundPrepareEvent(r)); // call an event for anyone who cares
			}
			else {
				r.setTime(0); // reset timer
				r.setStage(Stage.PLAYING);
				Bukkit.getPluginManager().callEvent(new MinigameRoundStartEvent(r));
			}
			if (time != -1){ // I'm pretty sure this is wrong, but I'm also pretty tired
				timerHandle = Bukkit.getScheduler().runTaskTimer(Main.plugin, new Runnable(){
					public void run(){
						int oldTime = r.getTime();
						boolean stageChange = false;
						int limit = r.getStage() == Stage.PLAYING ? r.getPlayingTime() : r.getPreparationTime();
						if (r.getTime() >= limit && limit > 0){ // timer reached its limit
							if (r.getStage() == Stage.PREPARING){ // if we're still preparing...
								r.setStage(Stage.PLAYING); // ...set stage to playing
								stageChange = true;
								r.setTime(0); // reset timer
								Bukkit.getPluginManager().callEvent(new MinigameRoundStartEvent(r));
							}
							else { // we're playing and the round just ended
								end(true);
								stageChange = true;
							}
						}
						if (!stageChange)
							r.tick();
						if (r.getStage() == Stage.PLAYING || r.getStage() == Stage.PREPARING)
							Bukkit.getPluginManager().callEvent(new MinigameRoundTickEvent(r, oldTime, stageChange));
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
	 * @param Whether the round was ended due to its timer expiring. This will default to false if omitted.
	 * @throws IllegalStateException if the timer has not been started.
	 * @since 0.1
	 */
	public void end(boolean timeUp){
		setTime(-1);
		if (timerHandle != -1)
			Bukkit.getScheduler().cancelTask(timerHandle); // cancel the round's timer task
		stage = Stage.WAITING; // set stage back to waiting
		timerHandle = -1; // reset timer handle since the task no longer exists
		for (MGPlayer mp : getPlayerList()) // iterate and remove players
			removePlayer(mp.getName());
		Bukkit.getPluginManager().callEvent(new MinigameRoundEndEvent(this, timeUp));
		getMinigame().getRollbackManager().rollback(getArena()); // roll back arena
	}

	/**
	 * Ends the round and resets its timer. The stage will also be set to {@link Stage#WAITING}.
	 * @throws IllegalStateException if the timer has not been started.
	 * @since 0.1
	 */
	public void end(){
		end(false);
	}

	/**
	 * Retrieves the location representing the minimum boundary on all three axes of the arena this round takes place in.
	 * @return the location representing the minimum boundary on all three axes of the arena this round takes place in, or
	 * null if the arena does not have boundaries.
	 * @since 0.1
	 */
	public Location getMinBound(){
		return minBound;
	}

	/**
	 * Retrieves the location representing the maximum boundary on all three axes of the arena this round takes place in.
	 * @return the location representing the maximum boundary on all three axes of the arena this round takes place in, or
	 * null if the arena does not have boundaries.
	 * @since 0.1
	 */
	public Location getMaxBound(){
		return maxBound;
	}

	/**
	 * Sets the minimum boundary on all three axes of this round object.
	 * @param x The minimum x-value.
	 * @param y The minimum y-value.
	 * @param z The minimum z-value.
	 * @since 0.1
	 */
	public void setMinBound(double x, double y, double z){
		this.minBound = new Location(this.minBound.getWorld(), x, y, z);
	}

	/**
	 * Sets the maximum boundary on all three axes of this round object.
	 * @param x The maximum x-value.
	 * @param y The maximum y-value.
	 * @param z The maximum z-value.
	 * @since 0.1
	 */
	public void setMaxBound(double x, double y, double z){
		this.minBound = new Location(this.minBound.getWorld(), x, y, z);
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
		MGPlayer mp = Minigame.getMinigameInstance(plugin).getMGPlayer(name);
		if (mp == null)
			mp = new MGPlayer(plugin, name, arena);
		else if (mp.getArena() == null)
			mp.setArena(arena);
		else {
			throw new IllegalArgumentException("Player " + name + " is already in arena " + mp.getArena());
		}
		mp.setDead(false); // make sure they're not dead the second they join.
		players.put(name, mp); // register player with round object
		Location spawn = spawns.get(new Random().nextInt(spawns.size())); // pick a random spawn
		p.teleport(spawn); // teleport the player to it
		Bukkit.getPluginManager().callEvent(new PlayerJoinMinigameRoundEvent(this, mp));
	}

	/**
	 * Removes a given player from this {@link Round round} and teleports them to the given location.
	 * @param name The player to remove from this {@link Round round}.
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
		PlayerLeaveMinigameRoundEvent event = new PlayerLeaveMinigameRoundEvent(this, mp);
		Bukkit.getPluginManager().callEvent(event);
	}

	/**
	 * Removes a given player from this {@link Round round} and teleports them to the round or plugin's default exit location
	 * (defaults to the main world's spawn point).
	 * @param name The player to remove from this {@link Round round}. 
	 * @since 0.1
	 */
	public void removePlayer(String name){
		try {
			removePlayer(name, Minigame.getMinigameInstance(plugin).getConfigManager().getDefaultExitLocation());
		}
		catch (PlayerOfflineException ex){}
		catch (PlayerNotPresentException e2){} // neither of these can happen, and if they do, we have bigger problems to worry about
	}

	/**
	 * Retrieves the maximum number of players allowed in a round at once.
	 * @return the maximum number of players allowed in a round at once.
	 * @since 0.1
	 */
	public int getMaxPlayers(){
		return maxPlayers;
	}

	/**
	 * Sets the maximum number of players allowed in a round at once.
	 * @param players the maximum number of players allowed in a round at once.
	 * @since 0.1
	 */
	public void setMaxPlayers(int players){
		this.maxPlayers = players;
	}

	/**
	 * Creates a new LobbySign to be managed
	 * @param location The location to create the sign at.
	 * @param type The type of the sign ("status" or "players")
	 * @param index The number of the sign (applicable only for "players" signs)
	 * @throws ArenaNotExistsException  if the specified arena does not exist.
	 * @throws IllegalArgumentException if the specified location does not contain a sign.
	 * @throws IllegalArgumentException if the specified index for a player sign is less than 1.
	 * @since 0.1
	 */
	public void addSign(Location location, LobbyType type, int index) throws ArenaNotExistsException, IllegalArgumentException {
		this.getMinigame().getLobbyManager().add(location, this.getArena(), type, index);
	}

	/**
	 * Updates all lobby signs linked to this round's arena.
	 * @since 0.1
	 */
	public void updateSigns(){
		this.getMinigame().getLobbyManager().update(arena);
	}
	
	/**
	 * Retrieves this round's exit location.
	 * @return this round's exit location.
	 * @since 0.1
	 */
	public Location getExitLocation(){
		return exitLocation;
	}
	
	/**
	 * Sets this round's exit location.
	 * @param location the new exit location for this round.
	 * @since 0.1
	 */
	public void setExitLocation(Location location){
		this.exitLocation = location;
	}

	public boolean equals(Object p){
		Round r = (Round)p;
		return arena.equals(r.getArena());
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + arena.hashCode() + 41);
	}
}
