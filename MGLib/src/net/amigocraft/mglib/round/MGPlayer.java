package net.amigocraft.mglib.round;

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
	private String arena;
	private boolean dead;

	/**
	 * Creates a new MGPlayer instance.
	 * @param name The username of the player.
	 * @param arena The arena of the player (this argument is subject to change).
	 * @since 0.1
	 */
	public MGPlayer(String name, String arena){
		this.name = name;
		this.arena = arena;
	}
	
	/**
	 * Gets the minigame plugin associated with this {@link MGPlayer}.
	 * @return The minigame plugin associated with this {@link MGPlayer}.
	 * @since 0.1
	 */
	public String getPlugin(){
		return plugin;
	}
	
	/**
	 * Gets the MGLib API instance registered by the minigame plugin associated with this {@link MGPlayer}.
	 * @return The MGLib API instance registered by the minigame plugin associated with this {@link MGPlayer}.
	 * @since 0.1
	 */
	public Minigame getMinigame(){
		return Minigame.getMinigameInstance(plugin);
	}

	/**
	 * Gets the username of this {@link MGPlayer}.
	 * @return The username of this {@link MGPlayer}.
	 * @since 0.1
	 */
	public String getName(){
		return name;
	}

	/**
	 * Gets the world associated with this {@link MGPlayer}.
	 * @return The world associated with this {@link MGPlayer}.
	 * @since 0.1
	 */
	public String getArena(){
		return arena;
	}

	/**
	 * Gets whether this player is "dead" in the minigame.
	 * @return Whether this player is "dead" in the minigame (can return true even if {@link Player#isDead()} returns
	 * false).
	 * @since 0.1
	 */
	public boolean isDead(){
		return dead;
	}
	
	/**
	 * Gets the {@link Round} associated with this player.
	 * @return The {@link Round} associated with this player.
	 * @since 0.1
	 */
	public Round getRound(){
		return Minigame.getMinigameInstance(plugin).getRound(arena);
	}
	
	/**
	 * Gets the username of this {@link MGPlayer}.
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

	public boolean equals(Object p){
		MGPlayer t = (MGPlayer)p;
		return name.equals(t.getName()) && arena.equals(t.getArena()) && dead == t.isDead();
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + name.hashCode() + arena.hashCode() + Boolean.valueOf(dead).hashCode() + 41);
	}

}
