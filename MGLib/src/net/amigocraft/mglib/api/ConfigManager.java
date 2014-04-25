package net.amigocraft.mglib.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Stores default variables for rounds for convenience purposes.
 * @since 0.1
 */
public class ConfigManager {

	private String plugin;

	private Location exitLocation;
	private int maxPlayers = 32;
	private String signId;
	private int roundPrepareTime = 90;
	private int roundPlayTime = 300;
	
	/**
	 * Creates a config manager for the given plugin.
	 * @param plugin the plugin to associate this config manager with.
	 * @since 0.1
	 */
	public ConfigManager(String plugin){
		this.plugin = plugin;
		this.exitLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
		this.signId = "[" + plugin + "]";
	}

	/**
	 * Retrieves the {@link Minigame} associated with this config manager.
	 * @return the {@link Minigame} associated with this config manager.
	 * @since 0.1
	 */
	public Minigame getMinigame(){
		return Minigame.getMinigameInstance(plugin);
	}

	/**
	 * Retrieves the name of the plugin associated with this config manager.
	 * return the name of the plugin associated with this config manager.
	 * @since 0.1
	 */
	public String getPlugin(){
		return plugin;
	}

	/**
	 * Retrives the default exit location for players upon round end.
	 * @return the default exit location for players upon round end.
	 * @since 0.1
	 */
	public Location getDefaultExitLocation(){
		return exitLocation;
	}

	/**
	 * Sets this default exit location for players upon a {@link Round round} ending.
	 * @param exitLocation this default exit location for players upon a {@link Round round} ending.
	 * @since 0.1
	 */
	public void setDefaultExitLocation(Location exitLocation){
		this.exitLocation = exitLocation;
	}

	/**
	 * Retrieves the default maximum number of players allowed in a {@link Round round} at one time.
	 * @return the default maximum number of players allowed in a {@link Round round} at one time.
	 * @since 0.1
	 */
	public int getMaxPlayers(){
		return maxPlayers;
	}

	/**
	 * Sets the default maximum number of players allowed in a {@link Round round} at one time.
	 * @param maxPlayers the default maximum number of players allowed in a {@link Round round} at one time.
	 * @since 0.1
	 */
	public void setMaxPlayers(int maxPlayers){
		this.maxPlayers = maxPlayers;
	}

	/**
	 * Retrieves the associated plugin {@link LobbySign lobby sign} identifier, used to recognize lobby signs.
	 * @return the associated plugin {@link LobbySign lobby sign} identifier, used to recognize lobby signs.
	 * @since 0.1
	 */
	public String getSignId(){
		return signId;
	}

	/**
	 * Sets the associated plugin {@link LobbySign lobby sign} identifier, used to recognize lobby signs.
	 * @param signId the associated plugin {@link LobbySign lobby sign} identifier, used to recognize lobby signs.
	 * @since 0.1
	 */
	public void setSignId(String signId){
		this.signId = signId;
	}

	/**
	 * Sets the default time allotted to a round's {@link Stage#PREPARING preparation} period.
	 * @return the default time allotted to a round's {@link Stage#PREPARING preparation} period.
	 * @since 0.1
	 */
	public int getDefaultPreparationTime(){
		return roundPrepareTime;
	}

	/**
	 * Sets the default time allotted to a round's {@link Stage#PREPARING preparation} period.
	 * Set to a value less than or equal to zero to skip the preparation period.
	 * @param preparationTime the default time allotted to a round's {@link Stage#PREPARING preparation} period.
	 * @since 0.1
	 */
	public void setDefaultPreparationTime(int preparationTime){
		this.roundPrepareTime = preparationTime;
	}

	/**
	 * Retrieves the default time allotted to a round's {@link Stage#PLAYING playing} period.
	 * @return the default time allotted to a round's {@link Stage#PLAYING playing} period.
	 * @since 0.1
	 */
	public int getDefaultPlayingTime(){
		return roundPlayTime;
	}
	
	/**
	 * Sets the default time allotted to a round's {@link Stage#PLAYING playing} period.
	 * Set to a value less than or equal to zero for an indefinite (infinite) time limit.
	 * @param playingTime the default time allotted to a round's {@link Stage#PLAYING playing} period.
	 * @since 0.1
	 */
	public void setDefaultPlayingTime(int playingTime){
		this.roundPlayTime = playingTime;
	}
	
	//TODO: More lobby sign configuration
	//TODO: And white/blacklisted actions
	
}
