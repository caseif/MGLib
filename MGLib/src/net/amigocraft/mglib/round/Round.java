package net.amigocraft.mglib.round;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import net.amigocraft.mglib.MGLib;
import net.amigocraft.mglib.Minigame;

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

	private int timerHandle = -1;

	/**
	 * Creates a new {@link Round} with the given parameters.
	 * <br><br>
	 * Please use {@link Minigame#createRound(String, int)} unless you understand the implications of using this
	 * constructor.
	 * @param plugin The plugin which this round should be associated with.
	 * @param world The world which this round takes place in.
	 * @param preparationTime The round's total preparation time.
	 * @param playingTime The round's total playing time.
	 * @since 0.1
	 */
	public Round(String plugin, String world, int preparationTime, int roundTime){
		this.plugin = plugin;
		this.world = world;
		stage = Stage.WAITING;
		Minigame.getMinigameInstance(plugin).getRounds().add(this);
	}

	/**
	 * @return The name of the minigame plugin associated with this {@link Round}. 
	 * @since 0.1
	 */
	public String getPlugin(){
		return plugin;
	}

	/**
	 * @return The instance of the MGLib API registered by the plugin associated with this {@link Round}.
	 * @since 0.1
	 */
	public Minigame getMinigame(){
		return Minigame.getMinigameInstance(plugin);
	}

	/**
	 * @return The name of the world associated with this {@link Round}.
	 * @since 0.1
	 */
	public String getWorld(){
		return world;
	}

	/**
	 * @return The current {@link Stage} of this {@link Round}.
	 * @since 0.1
	 */
	public Stage getStage(){
		return stage;
	}

	/**
	 * @return The current time remaining in this {@link Round}.
	 * @since 0.1
	 */
	public int getTime(){
		return time;
	}

	/**
	 * @return The round's preparation time.
	 * @since 0.1
	 */
	public int getPreparationTime(){
		return prepareTime;
	}

	/**
	 * @return The round's playing time.
	 * @since 0.1
	 */
	public int getPlayingTime(){
		return roundTime;
	}
	
	/**
	 * @return The round's timer's task's handle, or -1 if a timer is not started.
	 * @since 0.1
	 */
	public int getTimerHandle(){
		return timerHandle;
	}

	/**
	 * Sets the associated world of this {@link Round}.
	 * @param world The world to associate with this {@link Round}.
	 * @since 0.1
	 */
	public void setWorld(String world){
		this.world = world;
	}

	/**
	 * Sets the current stage of this {@link Round}.
	 * @param s The stage to set thsi {@link Round} to.
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
	 * @param t The number of seconds to set the preparation time to.
	 * @since 0.1
	 */
	public void setPreparationTime(int t){
		prepareTime = t;
	}

	/**
	 * Sets the round's playing time.
	 * @param t The number of seconds to set the preparation time to.
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
	 * @return A list of {@link MGPlayer}s in this {@link Round}.
	 * @since 0.1
	 */
	public List<MGPlayer> getPlayers(){
		List<MGPlayer> temp = new ArrayList<MGPlayer>();
		List<MGPlayer> p = new ArrayList<MGPlayer>();
		for (MGPlayer t : MGPlayer.players)
			if (t.getWorld().equals(world))
				temp.add(t);
		for (MGPlayer t : temp)
			p.add(t);
		return p;
	}

	/**
	 * Starts the round's timer. If the round's current stage is {@link Stage#PREPARING}, it will be set to
	 * {@link Stage#PLAYING} and the timer will be reset when it reaches 0.
	 * @throws IllegalStateException if the timer has already been started.
	 * @since 0.1
	 */
	public void startTimer(){
		if (timerHandle == -1){
			final Round r = this;
			if (r.getStage() == Stage.PLAYING)
				r.setTime(r.getPlayingTime());
			else {
				r.setTime(r.getPreparationTime());
				r.setStage(Stage.PREPARING);
			}
			timerHandle = Bukkit.getScheduler().runTaskTimer(MGLib.plugin, new Runnable(){
				public void run(){
					r.tickDown();
					if (r.getTime() <= 0){
						if (r.getStage() == Stage.PREPARING){
							r.setStage(Stage.PLAYING);
							r.setTime(r.getPlayingTime());
						}
						else {
							Bukkit.getScheduler().cancelTask(r.getTimerHandle());
							r.setStage(Stage.WAITING);
							r.setTime(-1);
							//TODO: Fire custom event
						}
					}
				}
			}, 0L, 20L).getTaskId();
		}
		else
			throw new IllegalStateException(Bukkit.getPluginManager().getPlugin(plugin) +
					" attempted to start a timer which " +
					"was already started.");
	}

	/**
	 * Stops and resets the round's timer. The stage will also be set to {@link Stage#WAITING}.
	 * @throws IllegalStateException if the timer has not been started.
	 * @since 0.1
	 */
	public void stopTimer(){
		setTime(-1);
		Bukkit.getScheduler().cancelTask(timerHandle);
		timerHandle = -1;
	}

	public boolean equals(Object p){
		Round r = (Round)p;
		return world.equals(r.getWorld());
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + world.hashCode() + 41);
	}
}
