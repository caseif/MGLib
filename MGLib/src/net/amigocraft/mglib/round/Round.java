package net.amigocraft.mglib.round;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import net.amigocraft.mglib.MGLib;
import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.Minigame;
import net.amigocraft.mglib.event.MinigameRoundEndEvent;
import net.amigocraft.mglib.event.MinigameRoundPrepareEvent;
import net.amigocraft.mglib.event.MinigameRoundStartEvent;

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
	private String name;

	private String world;
	private List<Location> spawns;
	private Location minBound;
	private Location maxBound;

	private int timerHandle = -1;

	/**
	 * Creates a new {@link Round} with the given parameters.
	 * <br><br>
	 * Please use {@link Minigame#createRound(String, boolean, int, int) Minigame#createRound()} unless you
	 * understand the implications of using this constructor.
	 * @param plugin The plugin which this round should be associated with.
	 * @param name The name of the arena in which this round takes place in.
	 * @param discrete Whether this round will take place in a discrete world (uses a defined arena when false).
	 * @param preparationTime The round's total preparation time. Use -1 for no limit, or 0 for no preparation phase.
	 * @param roundTime The round's total playing time. Use -1 for no limit.
	 * @throws IllegalArgumentException if the specified arena does not exist or the world of the specified arena does not exist
	 * @throws IOException if an exception occurs while loading the arenas.yml file from disk
	 * @throws InvalidConfigurationException if an exception occurs while loading the configuration from arenas.yml
	 * @since 0.1
	 */
	public Round(String plugin, String name, boolean discrete, int preparationTime, int roundTime)
			throws IllegalArgumentException, IOException, InvalidConfigurationException {
		YamlConfiguration y = MGUtil.loadArenaYaml(plugin);
		if (!y.contains(name))
			throw new IllegalArgumentException("Error occurred while creating round for " +
					Minigame.getMinigameInstance(plugin).getPlugin() + ": specified arena does not exist");
		ConfigurationSection cs = y.getConfigurationSection(name);
		World w = Bukkit.getWorld(cs.getString("world"));
		if (w == null)
			throw new IllegalArgumentException("Error occurred while creating round for " +
					Minigame.getMinigameInstance(plugin).getPlugin() + ": world of the specified arena does not exist");
		for (String k : cs.getConfigurationSection("spawns").getKeys(false)){
			spawns.add(new Location(w, cs.getDouble(k + ".x"), cs.getDouble(k + ".y"), cs.getDouble(k + ".z")));
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
		this.name = name;
		stage = Stage.WAITING;
		Minigame.getMinigameInstance(plugin).getRounds().add(this);
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
	public String getName(){
		return name;
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
	 * @param name The arena to associate with this {@link Round}.
	 * @since 0.1
	 */
	public void setName(String name){
		this.name = name;
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
	 * Gets a list of {@link MGPlayer}s in this {@link Round}.
	 * @return A list of {@link MGPlayer}s in this {@link Round}.
	 * @since 0.1
	 */
	public List<MGPlayer> getPlayers(){
		List<MGPlayer> temp = new ArrayList<MGPlayer>();
		List<MGPlayer> p = new ArrayList<MGPlayer>();
		for (MGPlayer t : MGPlayer.players)
			if (t.getWorld().equals(name))
				temp.add(t);
		for (MGPlayer t : temp)
			p.add(t);
		return p;
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
		Bukkit.getPluginManager().callEvent(new MinigameRoundEndEvent(this, timeUp));
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
	 * Retrieves the world in which this round takes place.
	 * @return the world in which this round takes place.
	 * @since 0.1
	 */
	public String getWorld(){
		return world;
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

	public boolean equals(Object p){
		Round r = (Round)p;
		return name.equals(r.getName());
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + name.hashCode() + 41);
	}
}
