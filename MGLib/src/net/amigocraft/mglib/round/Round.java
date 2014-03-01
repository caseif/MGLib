package net.amigocraft.mglib.round;

import java.util.ArrayList;
import java.util.List;

import net.amigocraft.mglib.Minigame;

/**
 * Represents a round within a minigame.
 * @author Maxim Roncacé
 * @since 0.1
 */
public class Round {

	private String plugin;
	private int time = 0;
	private Stage stage;
	private String world;

	/**
	 * Creates a new {@link Round} with the given parameters.
	 * <br><br>
	 * Please use {@link Minigame#createRound(String, int)} unless you understand the implications of using this
	 * constructor.
	 * @param plugin
	 * @param world
	 * @since 0.1
	 */
	public Round(String plugin, String world){
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

	public boolean equals(Object p){
		Round r = (Round)p;
		return world.equals(r.getWorld());
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + world.hashCode() + 41);
	}
}
