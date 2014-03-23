package net.amigocraft.mglib.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.amigocraft.mglib.event.PlayerLeaveMinigameRoundEvent;
import net.amigocraft.mglib.exception.PlayerNotPresentException;
import net.amigocraft.mglib.exception.PlayerOfflineException;

/**
 * Represents a player participating in a minigame.
 * @since 0.1
 */
public class MGPlayer {

	private String plugin;
	private String name;
	private String arena;
	private boolean dead;

	/**
	 * Creates a new MGPlayer instance.
	 * @param plugin The plugin to associate the MGPlayer with.
	 * @param name The username of the player.
	 * @param arena The arena of the player (this argument is subject to change).
	 * @since 0.1
	 */
	public MGPlayer(String plugin, String name, String arena){
		this.plugin = plugin;
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
	 * Gets the arena associated with this {@link MGPlayer}.
	 * @return The arena associated with this {@link MGPlayer}.
	 * @since 0.1
	 */
	public String getArena(){
		return arena;
	}
	
	/**
	 * Sets the arena of this {@link MGPlayer}. Please do not call this method unless you understand the implications of doing so.
	 * @since 0.1
	 */
	public void setArena(String arena){
		this.arena = arena;
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
	 * Changes the alive status of this {@link MGPlayer}.
	 * @param dead Whether the player is "dead."
	 * @since 0.1
	 */
	public void setDead(boolean dead){
		this.dead = dead;
	}
	
	/**
	 * Adds this {@link MGPlayer} to the given {@link Round round}.
	 * @param round The name of the round to add the player to.
	 * @throws PlayerOfflineException if the player is not online.
	 * @since 0.1
	 */
	public void addToRound(String round) throws PlayerOfflineException {
		Minigame.getMinigameInstance(plugin).getRound(round).addPlayer(name);
	}
	
	/**
	 * Removes this {@link MGPlayer} from the round they are currently in.
	 * @throws PlayerOfflineException if the given player is not online.
	 * @throws PlayerNotPresentException if the given player is not in a round.
	 * @since 0.1
	 */
	public void removeFromRound(Location location) throws PlayerOfflineException, PlayerNotPresentException {
		Player p = Bukkit.getPlayer(name);
		if (p == null) // check that the specified player is online
			throw new PlayerOfflineException();
		Round round = null;
		for (Round r : Minigame.getMinigameInstance(plugin).getRoundList()) // reuse the old MGPlayer if it exists
			if (r.getPlayers().containsKey(name)){
				round = r;
				r.getPlayers().remove(name);
				r.getPlayers().get(name).setArena(arena);
				break;
			}
		if (round == null)
			throw new PlayerNotPresentException();
		setArena(null);
		setDead(false); // make sure they're not dead when they join a new round
		round.getPlayers().remove(name);
		reset(location);
		Bukkit.getPluginManager().callEvent(new PlayerLeaveMinigameRoundEvent(round, this));
	}
	
	/**
	 * Removes this {@link MGPlayer} from the round they are currently in.
	 * @throws PlayerOfflineException if the player is not online.
	 * @throws PlayerNotPresentException if the player is not in a round.
	 * @since 0.1
	 */
	public void removeFromRound() throws PlayerOfflineException, PlayerNotPresentException{
		removeFromRound(Minigame.getMinigameInstance(plugin).getExitLocation());
	}
	
	/**
	 * Resets the {@link Player Bukkit player} after they've left a round.
	 * @param location The location to teleport the player to, or null to skip teleportation.
	 * @throws PlayerOfflineException if the player is not online.
	 * @since 0.1
	 */
	public void reset(Location location) throws PlayerOfflineException {
		MGPlayer.resetPlayer(name, location);
	}
	
	/**
	 * Resets the {@link Player Bukkit player} after they've left a round.
	 * @throws PlayerOfflineException if the player is not online.
	 * @since 0.1
	 */
	public void reset() throws PlayerOfflineException {
		MGPlayer.resetPlayer(name, Minigame.getMinigameInstance(plugin).getExitLocation());
	}
	
	/**
	 * Resets the given {@link Player Bukkit player} after they've left a round.
	 * @param player The player to reset.
	 * @param location The location to teleport the player to, or null to skip teleportation.
	 * @throws PlayerOfflineException if the given player is not online.
	 * @since 0.1
	 */
	public static void resetPlayer(String player, Location location) throws PlayerOfflineException {
		Player p = Bukkit.getPlayer(player);
		if (p == null) // check that the specified player is online
			throw new PlayerOfflineException();
		if (location != null)
			p.teleport(location); // teleport the player
	}

	public boolean equals(Object p){
		MGPlayer t = (MGPlayer)p;
		return name.equals(t.getName()) && arena.equals(t.getArena()) && dead == t.isDead();
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + name.hashCode() + arena.hashCode() + Boolean.valueOf(dead).hashCode() + 41);
	}

}
