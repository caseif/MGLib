package net.amigocraft.mglib.round;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import net.amigocraft.mglib.Minigame;

/**
 * Represents a player participating in a minigame.
 * @author Maxim Roncacé
 * @since 0.1
 */
public class MGPlayer {

	private String plugin;
	private String name;
	private String world;
	private boolean dead;
	/**
	 * A list containing all {@link MGPlayer}s in all registered MGLib API instances.
	 * <br><br>
	 * Please do not modify this list from your plugin.
	 * @since 0.1
	 */
	public static ArrayList<MGPlayer> players = new ArrayList<MGPlayer>();

	/**
	 * Creates a new MGPlayer instance.
	 * @param name The username of the player.
	 * @param world The world of the player (this argument is subject to change).
	 * @since 0.1
	 */
	public MGPlayer(String name, String world){
		this.name = name;
		this.world = world;
		players.add(this);
	}
	
	/**
	 * @return The minigame plugin associated with this {@link MGPlayer}.
	 * @since 0.1
	 */
	public String getPlugin(){
		return plugin;
	}
	
	/**
	 * @return The MGLib API instance registered by the minigame plugin associated with this {@link MGPlayer}.
	 * @since 0.1
	 */
	public Minigame getMinigame(){
		return Minigame.getMinigameInstance(plugin);
	}

	/**
	 * @return The username of this {@link MGPlayer}.
	 * @since 0.1
	 */
	public String getName(){
		return name;
	}

	/**
	 * @return The world associated with this {@link MGPlayer}.
	 * @since 0.1
	 */
	public String getWorld(){
		return world;
	}

	/**
	 * @return Whether this player is "dead" in the minigame (can return true even if {@link Player#isDead()} returns
	 * false).
	 * @since 0.1
	 */
	public boolean isDead(){
		return dead;
	}
	
	/**
	 * @return The {@link Round} associated with this player.
	 * @since 0.1
	 */
	public Round getRound(){
		return Minigame.getMinigameInstance(plugin).getRound(world);
	}
	
	/**
	 * @param name The username of this {@link MGPlayer}.
	 * @since 0.1
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * Changes the alive status of this {@link MGPlayer}.
	 * @param dead Whether the player is "dead."
	 * @since 0.1
	 */
	public void setDead(boolean dead){
		this.dead = dead;
	}
	
	/**
	 * Returns the {@link MGPlayer} associated with the given username.
	 * @param player The username to search for.
	 * @return The {@link MGPlayer} associated with the given username, or <b>null</b> if none is found.
	 * @since 0.1
	 */
	public static MGPlayer getMGPlayer(String player){
		for (MGPlayer p : players){
			if (p.getName().equals(player))
				return p;
		}
		return null;
	}

	/**
	 * Destroys this {@link MGPlayer} instance.
	 * @since 0.1
	 */
	public void destroy(){
		players.remove(this);
	}

	/**
	 * Convenience method for checking if an {@link MGPlayer} is associated with the given username.
	 * <br><br>
	 * This method simply checks if {@link MGPlayer#getMGPlayer(String) MGPlayer#getMGPlayer(p)} is <b>null</b>.
	 * @param p The username to search for.
	 * @return Whether an associated {@link MGPlayer} was found.
	 * @since 0.1
	 */
	public static boolean isPlayer(String p){
		if (getMGPlayer(p) != null)
			return true;
		return false;
	}

	public boolean equals(Object p){
		MGPlayer t = (MGPlayer)p;
		return name.equals(t.getName()) && world.equals(t.getWorld());
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + name.hashCode() + world.hashCode() + 41);
	}

}
