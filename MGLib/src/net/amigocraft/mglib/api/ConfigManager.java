package net.amigocraft.mglib.api;

import java.util.HashMap;

import net.amigocraft.mglib.api.Stage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	private boolean joinRiP = false;
	private boolean joinRwP = false;
	private HashMap<String, ChatColor> lobbyColors = new HashMap<String, ChatColor>();
	private HashMap<String, Boolean> actions = new HashMap<String, Boolean>();
	
	/**
	 * Creates a config manager for the given plugin.
	 * @param plugin the plugin to associate this config manager with.
	 * @since 0.1
	 */
	public ConfigManager(String plugin){
		this.plugin = plugin;
		this.exitLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
		this.signId = "[" + plugin + "]";
		
		lobbyColors.put("arena", ChatColor.DARK_RED);
		lobbyColors.put("waiting", ChatColor.GRAY);
		lobbyColors.put("preparing", ChatColor.RED);
		lobbyColors.put("playing", ChatColor.DARK_PURPLE);
		lobbyColors.put("resetting", ChatColor.GRAY);
		lobbyColors.put("time", ChatColor.GREEN);
		lobbyColors.put("time-warning", ChatColor.RED);
		lobbyColors.put("time-infinite", ChatColor.GREEN);
		lobbyColors.put("player-count", ChatColor.GREEN);
		lobbyColors.put("player-count-full", ChatColor.RED);
		
		actions.put("teleport", true);
		actions.put("block-place", false);
		actions.put("block-break", false);
		actions.put("block-burn", false);
		actions.put("block-fade", false);
		actions.put("block-grow", false);
		actions.put("block-ignite", false);
		actions.put("block-flow", false);
		actions.put("block-physics", false);
		actions.put("block-piston", true);
		actions.put("block-spread", false);
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
	 * @return the name of the plugin associated with this config manager.
	 * @since 0.1
	 */
	public String getPlugin(){
		return plugin;
	}

	/**
	 * Retrives the default exit location for players upon round end (default: the spawn point of the main world).
	 * @return the default exit location for players upon round end.
	 * @since 0.1
	 */
	public Location getDefaultExitLocation(){
		return exitLocation;
	}

	/**
	 * Sets this default exit location for players upon a {@link Round round} ending (default: the spawn point of the main world).
	 * @param exitLocation this default exit location for players upon a {@link Round round} ending.
	 * @since 0.1
	 */
	public void setDefaultExitLocation(Location exitLocation){
		this.exitLocation = exitLocation;
	}

	/**
	 * Retrieves the default maximum number of players allowed in a {@link Round round} at one time (default: 32).
	 * @return the default maximum number of players allowed in a {@link Round round} at one time.
	 * @since 0.1
	 */
	public int getMaxPlayers(){
		return maxPlayers;
	}

	/**
	 * Sets the default maximum number of players allowed in a {@link Round round} at one time (default: 32).
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
	 * Sets the default time allotted to a round's {@link Stage#PREPARING preparation} period (default: 90).
	 * @return the default time allotted to a round's {@link Stage#PREPARING preparation} period.
	 * @since 0.1
	 */
	public int getDefaultPreparationTime(){
		return roundPrepareTime;
	}

	/**
	 * Sets the default time allotted to a round's {@link Stage#PREPARING preparation} period (default: 90).
	 * Set to a value less than or equal to zero to skip the preparation period.
	 * @param preparationTime the default time allotted to a round's {@link Stage#PREPARING preparation} period.
	 * @since 0.1
	 */
	public void setDefaultPreparationTime(int preparationTime){
		this.roundPrepareTime = preparationTime;
	}

	/**
	 * Retrieves the default time allotted to a round's {@link Stage#PLAYING playing} period (default: 300).
	 * @return the default time allotted to a round's {@link Stage#PLAYING playing} period.
	 * @since 0.1
	 */
	public int getDefaultPlayingTime(){
		return roundPlayTime;
	}
	
	/**
	 * Sets the default time allotted to a round's {@link Stage#PLAYING playing} period (default: 300).
	 * Set to a value less than or equal to zero for an indefinite (infinite) time limit.
	 * @param playingTime the default time allotted to a round's {@link Stage#PLAYING playing} period.
	 * @since 0.1
	 */
	public void setDefaultPlayingTime(int playingTime){
		this.roundPlayTime = playingTime;
	}
	
	/**
	 * Retrieves whether players are allowed to join a round which {@link Stage#PLAYING has already started} (default: true).
	 * @return whether players are allowed to join a round which {@link Stage#PLAYING has already started.}
	 */
	public boolean getAllowJoinRoundInProgress(){
		return joinRiP;
	}
	
	/**
	 * Sets whether players are allowed to join a round which {@link Stage#PLAYING has already started} (default: true).
	 * @param allow whether players are allowed to join a round which {@link Stage#PLAYING has already started.}
	 */
	public void setAllowJoinRoundInProgress(boolean allow){
		this.joinRiP = allow;
	}
	
	/**
	 * Retrieves whether players are allowed to join a round which {@link Stage#PREPARING is in its preparation stage} (default: true).
	 * @return whether players are allowed to join a round which {@link Stage#PREPARING is in its preparation stage.}
	 */
	public boolean getAllowJoinRoundWhilePreparing(){
		return joinRwP;
	}
	
	/**
	 * Sets whether players are allowed to join a round which {@link Stage#PREPARING is in its preparation stage} (default: true).
	 * @param allow whether players are allowed to join a round which {@link Stage#PREPARING is in its preparation stage}.
	 */
	public void setAllowJoinRoundWhilePreparing(boolean allow){
		this.joinRwP = allow;
	}

	/**
	 * Retrieves the color of the top line (the arena name) of a lobby sign.
	 * @return the color of the top line (the arena name) of a lobby sign.
	 * @since 0.1
	 */
	public ChatColor getLobbyArenaColor(){
		return lobbyColors.get("arena");
	}
	
	/**
	 * Sets the color of the top line (the arena name) of a lobby sign.
	 * @param color the new color of the top line of a lobby sign.
	 * @since 0.1
	 */
	public void setLobbyArenaColor(ChatColor color){
		lobbyColors.put("arena", color);
	}
	
	/**
	 * Retrieves the color of a lobby sign's {@link Stage#WAITING WAITING} status.
	 * @return the color of a lobby sign's {@link Stage#WAITING WAITING} status.
	 * @since 0.1
	 */
	public ChatColor getLobbyWaitingColor(){
		return lobbyColors.get("waiting");
	}
	
	/**
	 * Sets the color of a lobby sign's {@link Stage#WAITING WAITING} status.
	 * @param color the new color of a lobby sign's {@link Stage#WAITING WAITING} status.
	 * @since 0.1
	 */
	public void setLobbyWaitingColor(ChatColor color){
		lobbyColors.put("waiting", color);
	}
	
	/**
	 * Retrieves the color of a lobby sign's {@link Stage#PREPARING PREPARING} status.
	 * @return the color of a lobby sign's {@link Stage#PREPARING PREPARING} status.
	 * @since 0.1
	 */
	public ChatColor getLobbyPreparingColor(){
		return lobbyColors.get("preparing");
	}
	
	/**
	 * Sets the color of a lobby sign's {@link Stage#PREPARING PREPARING} status.
	 * @param color the new color of a lobby sign's {@link Stage#PREPARING PREPARING} status.
	 * @since 0.1
	 */
	public void setLobbyPreparingColor(ChatColor color){
		lobbyColors.put("preparing", color);
	}
	
	/**
	 * Retrieves the color of a lobby sign's {@link Stage#PLAYING PLAYING} status.
	 * @return the color of a lobby sign's {@link Stage#PLAYING PLAYING} status.
	 * @since 0.1
	 */
	public ChatColor getLobbyPlayingColor(){
		return lobbyColors.get("playing");
	}
	
	/**
	 * Sets the color of a lobby sign's {@link Stage#PLAYING PLAYING} status.
	 * @param color the new color of a lobby sign's {@link Stage#PLAYING PLAYING} status.
	 * @since 0.1
	 */
	public void setLobbyPlayingColor(ChatColor color){
		lobbyColors.put("playing", color);
	}
	
	/**
	 * Retrieves the color of a lobby sign's {@link Stage#RESETTING RESETTING} status.
	 * @return the color of a lobby sign's {@link Stage#RESETTING RESETTING} status.
	 * @since 0.1
	 */
	public ChatColor getLobbyResettingColor(){
		return lobbyColors.get("resetting");
	}
	
	/**
	 * Sets the color of a lobby sign's {@link Stage#RESETTING RESETTING} status.
	 * @param color the new color of a lobby sign's {@link Stage#RESETTING RESETTING} status.
	 * @since 0.1
	 */
	public void setLobbyResettingColor(ChatColor color){
		lobbyColors.put("resetting", color);
	}
	
	/**
	 * Retrieves the color of a lobby sign's timer when remaining time is greater than 60 seconds.
	 * @return the color of a lobby sign's timer when remaining time is greater than 60 seconds.
	 * @since 0.1
	 */
	public ChatColor getLobbyTimeColor(){
		return lobbyColors.get("time");
	}
	
	/**
	 * Sets the color of a lobby sign's timer when remaining time is greater than 60 seconds.
	 * @param color the new color of a lobby sign's timer when remaining time is greater than 60 seconds.
	 * @since 0.1
	 */
	public void setLobbyTimeColor(ChatColor color){
		lobbyColors.put("time", color);
	}
	
	/**
	 * Retrieves the color of a lobby sign's timer when remaining time is less than 60 seconds.
	 * @return the color of a lobby sign's timer when remaining time is less than 60 seconds.
	 * @since 0.1
	 */
	public ChatColor getLobbyTimeWarningColor(){
		return lobbyColors.get("time-warning");
	}
	
	/**
	 * Sets the color of a lobby sign's timer when remaining time is less than 60 seconds.
	 * @param color the new color of a lobby sign's timer when remaining time is less than 60 seconds.
	 * @since 0.1
	 */
	public void setLobbyTimeWarningColor(ChatColor color){
		lobbyColors.put("time-warning", color);
	}
	
	/**
	 * Retrieves the color of a lobby sign's timer when remaining time is infinite.
	 * @return the color of a lobby sign's timer when remaining time is infinite.
	 * @since 0.1
	 */
	public ChatColor getLobbyTimeInfiniteColor(){
		return lobbyColors.get("time-infinite");
	}
	
	/**
	 * Sets the color of a lobby sign's timer when remaining time is infinite.
	 * @param color the new color of a lobby sign's timer when remaining time is infinite.
	 * @since 0.1
	 */
	public void setLobbyTimeInfiniteColor(ChatColor color){
		lobbyColors.put("time-infinite", color);
	}
	
	/**
	 * Retrieves the color of a lobby sign's player count when the round is not full.
	 * @return the color of a lobby sign's player count when the round is not full.
	 * @since 0.1
	 */
	public ChatColor getLobbyPlayerCountColor(){
		return lobbyColors.get("player-count");
	}
	
	/**
	 * Sets the color of a lobby sign's player count when the round is not full.
	 * @param color the new color of a lobby sign's player count when the round is not full.
	 * @since 0.1
	 */
	public void setLobbyPlayerCountColor(ChatColor color){
		lobbyColors.put("player-count", color);
	}
	
	/**
	 * Retrieves the color of a lobby sign's player count when the round is full.
	 * @return the color of a lobby sign's player count when the round is full.
	 * @since 0.1
	 */
	public ChatColor getLobbyPlayerCountFullColor(){
		return lobbyColors.get("player-count-full");
	}
	
	/**
	 * Sets the color of a lobby sign's player count when the round is full.
	 * @param color the new color of a lobby sign's player count when the round is full.
	 * @since 0.1
	 */
	public void setLobbyPlayerCountFullColor(ChatColor color){
		lobbyColors.put("player-count-full", color);
	}
	
	/**
	 * Retrieves a copy of the hashmap containing all color attributes for lobby signs associated with this manager's plugin.
	 * @return a copy of the hashmap containing all color attributes for lobby signs associated with this manager's plugin.
	 * @since 0.1
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ChatColor> getLobbyColorAttributes(){
		return (HashMap<String, ChatColor>)lobbyColors.clone();
	}
	
	/**
	 * Retrieves a specific color attribute for lobby signs associated with this manager's plugin.
	 * @param key the attribute to fetch the color for.
	 * @return a specific color attribute for lobby signs associated with this manager's plugin,
	 * or null if it does not exist.
	 * @since 0.1
	 */
	public ChatColor getLobbyColorAttribute(String key){
		return lobbyColors.get(key);
	}
	
	/**
	 * Sets a specific color attribute for lobby signs associated with this manager's plugin.
	 * If the provided key is not used in the library, this method will have no effect.
	 * @param key the key of the color attribute (e.g. arena, player-count, time).
	 * @param color the color to associate with aforementioned key.
	 * @since 0.1
	 */
	public void setLobbyColor(String key, ChatColor color){
		lobbyColors.put(key, color);
	}

	/**
	 * Retrieves whether teleportation (via ender pearl, command, or another plugin) is permitted for players in a {@link Round round}. (Default: true)
	 * @return whether teleportation is permitted for players in a {@link Round round}.
	 * @since 0.1
	 */
	public boolean isTeleportationAllowed(){
		return actions.get("teleport");
	}
	
	/**
	 * Sets whether teleportation (via ender pearl, command, or another plugin) is permitted for players in a {@link Round round}. (Default: true)
	 * @param allowed whether teleportation is permitted for players in a {@link Round round}.
	 * @since 0.1
	 */
	public void setTeleportationAllowed(boolean allowed){
		actions.put("teleport", allowed);
	}

	/**
	 * Retrieves whether block placing is permitted for players in a {@link Round round}. (Default: false)
	 * @return whether block placing is permitted for players in a {@link Round round}.
	 * @since 0.1
	 */
	public boolean isBlockPlaceAllowed(){
		return actions.get("block-place");
	}
	
	/**
	 * Sets whether block placing is permitted for players in a {@link Round round}. (Default: false)
	 * @param allowed whether block placing is permitted for players in a {@link Round round}.
	 * @since 0.1
	 */
	public void setBlockPlaceAllowed(boolean allowed){
		actions.put("block-place", allowed);
	}

	/**
	 * Retrieves whether block breaking is permitted for players in a {@link Round round}. (Default: false)
	 * @return whether block breaking is permitted for players in a {@link Round round}.
	 * @since 0.1
	 */
	public boolean isBlockBreakAllowed(){
		return actions.get("block-break");
	}
	
	/**
	 * Sets whether block breaking is permitted for players in a {@link Round round}. (Default: false)
	 * @param allowed whether block breaking is permitted for players in a {@link Round round}.
	 * @since 0.1
	 */
	public void setBlockBreakAllowed(boolean allowed){
		actions.put("block-break", allowed);
	}

	/**
	 * Retrieves whether block burning is permitted in worlds containing one or more arenas. (Default: false)
	 * @return whether block burning is permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public boolean isBlockBurnAllowed(){
		return actions.get("block-burn");
	}
	
	/**
	 * Sets whether block burning is permitted in worlds containing one or more arenas. (Default: false)
	 * @param allowed whether block burning is permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public void setBlockBurningAllowed(boolean allowed){
		actions.put("block-burn", allowed);
	}

	/**
	 * Retrieves whether block fading (e.g. leaves decaying) is permitted in worlds containing one or more arenas. (Default: false)
	 * @return whether block fading (e.g. leaves decaying) is permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public boolean isBlockFadeAllowed(){
		return actions.get("block-fade");
	}
	
	/**
	 * Sets whether block fading (e.g. leaves decaying) is permitted in worlds containing one or more arenas. (Default: false)
	 * @param allowed whether block fading (e.g. leaves decaying) is permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public void setBlockFadeAllowed(boolean allowed){
		actions.put("block-fade", allowed);
	}

	/**
	 * Retrieves whether block growing (e.g. reeds growing) is permitted in worlds containing one or more arenas. (Default: false)
	 * @return whether block growing (e.g. reeds growing) is permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public boolean isBlockGrowAllowed(){
		return actions.get("block-grow");
	}
	
	/**
	 * Sets whether block growing (e.g. reeds growing) is permitted in worlds containing one or more arenas. (Default: false)
	 * @param allowed whether block growing (e.g. reeds growing) is permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public void setBlockGrowAllowed(boolean allowed){
		actions.put("block-grow", allowed);
	}

	/**
	 * Retrieves whether block ignition is permitted in worlds containing one or more arenas. (Default: false)
	 * @return whether block ignition is permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public boolean isBlockIgniteAllowed(){
		return actions.get("block-ignite");
	}
	
	/**
	 * Sets whether block ignition is permitted in worlds containing one or more arenas. (Default: false)
	 * @param allowed whether block ignition is permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public void setBlockIgniteAllowed(boolean allowed){
		actions.put("block-ignite", allowed);
	}

	/**
	 * Retrieves whether block flowing is permitted in worlds containing one or more arenas. (Default: true)
	 * @return whether block flowing is permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public boolean isBlockFlowAllowed(){
		return actions.get("block-flow");
	}
	
	/**
	 * Sets whether block flowing is permitted in worlds containing one or more arenas. (Default: true)
	 * @param allowed whether block flowing is permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public void setBlockFlowAllowed(boolean allowed){
		actions.put("block-flow", allowed);
	}

	/**
	 * Retrieves whether block physics are permitted in worlds containing one or more arenas. (Default: false)
	 * @return whether block physics are permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public boolean isBlockPhysicsAllowed(){
		return actions.get("block-physics");
	}
	
	/**
	 * Sets whether block ignition are physics in worlds containing one or more arenas. (Default: false)
	 * @param allowed whether block physics are permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public void setBlockPhyiscsAllowed(boolean allowed){
		actions.put("block-physics", allowed);
	}

	/**
	 * Retrieves whether pistons are permitted in worlds containing one or more arenas. (Default: true)
	 * @return whether pistons are permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public boolean isBlockPistonAllowed(){
		return actions.get("block-piston");
	}
	
	/**
	 * Sets whether pistons are permitted in worlds containing one or more arenas. (Default: true)
	 * @param allowed whether pistons are permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public void setBlockPistonAllowed(boolean allowed){
		actions.put("block-piston", allowed);
	}

	/**
	 * Retrieves whether block spreading (e.g. grass spreding to dirt blocks) are permitted in worlds containing one or more arenas. (Default: false)
	 * @return whether block spreading (e.g. grass spreding to dirt blocks) are permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public boolean isBlockSpreadAllowed(){
		return actions.get("block-spread");
	}
	
	/**
	 * Sets whether block spreading (e.g. grass spreding to dirt blocks) are permitted in worlds containing one or more arenas. (Default: false)
	 * @param allowed whether block spreading (e.g. grass spreding to dirt blocks) are permitted in worlds containing one or more arenas.
	 * @since 0.1
	 */
	public void setBlockSpreadAllowed(boolean allowed){
		actions.put("block-spread", allowed);
	}
	
	
	//TODO: And white/blacklisted actions
	
}
