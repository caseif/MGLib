/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Maxim Roncacé
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.amigocraft.mglib.api;

import net.amigocraft.mglib.MGUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;

/**
 * Stores variables for minigames and defaults for rounds for convenience
 * purposes.
 *
 * @since 0.1.0
 */
public class ConfigManager {

	private String plugin;

	private Location3D exitLocation;
	private int minPlayers = 0;
	private int maxPlayers = 32;
	private String signId;
	private int roundPrepareTime = 90;
	private int roundPlayTime = 300;
	private boolean joinRiP = false;
	private boolean joinRwP = false;
	private boolean pmsAllowed = true;
	private boolean kitsAllowed = true;
	private boolean spectateJoin = false;
	private HashMap<String, Color> lobbyColors = new HashMap<String, Color>();
	private HashMap<String, Boolean> actions = new HashMap<String, Boolean>();
	private Class<? extends MGPlayer> playerClass = MGPlayer.class;
	private Class<? extends Round> roundClass = Round.class;
	private GameMode gameMode = GameMode.SURVIVAL;
	private boolean pvp = true;
	private boolean damage = true;
	private boolean rollback = true;
	private boolean spectatorsOnSigns = true;
	private boolean spectatorFlight = true;
	private boolean teamDamage = true;
	private String locale = "enUS";
	private boolean randomSpawns = true;
	private boolean overrideDeathEvent = false;
	private boolean hunger = false;
	private boolean perRoundChat = true;
	private boolean teamChat = false;
	private boolean mobSpawning = true;
	private boolean targeting = false;
	private boolean spectatorChat = true;
	private boolean vanillaSpectating = true;
	private boolean spectatorsInTabList = true;

	/**
	 * Creates a config manager for the given plugin.
	 *
	 * @param plugin the plugin to associate this config manager with
	 * @since 0.1.0
	 */
	public ConfigManager(String plugin) {
		this.plugin = plugin;
		this.exitLocation = MGUtil.fromBukkitLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
		this.signId = "[" + plugin + "]";

		lobbyColors.put("arena", Color.DARK_RED);
		lobbyColors.put("waiting", Color.DARK_GRAY);
		lobbyColors.put("preparing", Color.RED);
		lobbyColors.put("playing", Color.DARK_PURPLE);
		lobbyColors.put("resetting", Color.DARK_GRAY);
		lobbyColors.put("time", Color.GREEN);
		lobbyColors.put("time-warning", Color.RED);
		lobbyColors.put("time-infinite", Color.GREEN);
		lobbyColors.put("player-count", Color.GREEN);
		lobbyColors.put("player-count-full", Color.RED);

		actions.put("teleport", true);
		actions.put("block-place", false);
		actions.put("block-break", false);
		actions.put("block-burn", false);
		actions.put("block-fade", false);
		actions.put("block-grow", false);
		actions.put("block-ignite", false);
		actions.put("block-flow", false);
		actions.put("block-physics", true);
		actions.put("block-piston", true);
		actions.put("block-spread", false);
		actions.put("entity-explode", false);
		actions.put("hanging-break", false);
		actions.put("item-frame-damage", false);
	}

	/**
	 * Retrieves the {@link Minigame} associated with this config manager.
	 *
	 * @return the {@link Minigame} associated with this config manager
	 * @since 0.1.0
	 */
	public Minigame getMinigame() {
		return Minigame.getMinigameInstance(plugin);
	}

	/**
	 * Retrieves the name of the plugin associated with this config manager.
	 *
	 * @return the name of the plugin associated with this config manager
	 * @since 0.1.0
	 */
	public String getPlugin() {
		return plugin;
	}

	/**
	 * Retrives the default exit location for players upon round end (default:
	 * the spawn point of the main world).
	 *
	 * @return the default exit location for players upon round end
	 * @deprecated Use {@link ConfigManager#getDefaultExitLocation()}
	 * @since 0.1.0
	 */
	//TODO: figure out how to transition to Location3D
	public Location getDefaultExitLocation() {
		return new Location(Bukkit.getWorld(exitLocation.getWorld()), exitLocation.getX(), exitLocation.getY(), exitLocation.getZ());
	}

	/**
	 * Sets this default exit location for players upon a {@link Round round}
	 * ending (default: the spawn point of the main world).
	 *
	 * @param exitLocation this default exit location for players upon a {@link
	 *                     Round round} ending
	 * @since 0.1.0
	 */
	public void setDefaultExitLocation(Location3D exitLocation) {
		this.exitLocation = exitLocation;
	}

	/**
	 * Sets this default exit location for players upon a {@link Round round}
	 * ending (default: the spawn point of the main world).
	 *
	 * @param exitLocation this default exit location for players upon a {@link
	 *                     Round round} ending
	 * @deprecated Use {@link ConfigManager#setDefaultExitLocation(Location3D)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setDefaultExitLocation(Location exitLocation) {
		setDefaultExitLocation(new Location3D(exitLocation.getWorld().getName(), exitLocation.getX(), exitLocation.getY(), exitLocation.getZ()));
	}

	/**
	 * Retrieves the default minimum number of players required to automatically
	 * start a {@link Round round}. (default: 0 (no minimum))
	 *
	 * @return the default minimum number of players required to automatically
	 * start a {@link Round round}
	 * @since 0.2.0
	 */
	public int getMinPlayers() {
		return minPlayers;
	}

	/**
	 * Sets the default minimum number of players required to automatically
	 * start a {@link Round round}. Set to 0 (default) for no autostart.
	 *
	 * @param minPlayers the default minimum number of players required to
	 *                   automatically start a {@link Round round}
	 * @since 0.2.0
	 */
	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	/**
	 * Retrieves the default maximum number of players allowed in a {@link Round
	 * round} at one time (default: 32).
	 *
	 * @return the default maximum number of players allowed in a {@link Round
	 * round} at one time
	 * @since 0.1.0
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * Sets the default maximum number of players allowed in a {@link Round
	 * round} at one time (default: 32). Set to 0 for no limit.
	 *
	 * @param maxPlayers the default maximum number of players allowed in a
	 *                   {@link Round round} at one time
	 * @since 0.1.0
	 */
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	/**
	 * Retrieves the associated plugin {@link LobbySign lobby sign} identifier,
	 * used to recognize lobby signs. (default: your plugin's name in square
	 * brackets)
	 *
	 * @return the associated plugin {@link LobbySign lobby sign} identifier,
	 * used to recognize lobby signs
	 * @since 0.1.0
	 */
	public String getSignId() {
		return signId;
	}

	/**
	 * Sets the associated plugin {@link LobbySign lobby sign} identifier, used
	 * to recognize lobby signs. (default: your plugin's name in square
	 * brackets)
	 *
	 * @param signId the associated plugin {@link LobbySign lobby sign}
	 *               identifier, used to recognize lobby signs
	 * @since 0.1.0
	 */
	public void setSignId(String signId) {
		this.signId = signId;
	}

	/**
	 * Sets the default time allotted to a round's {@link Stage#PREPARING
	 * preparation} period (default: 90).
	 *
	 * @return the default time allotted to a round's {@link Stage#PREPARING
	 * preparation} period
	 * @since 0.1.0
	 */
	public int getDefaultPreparationTime() {
		return roundPrepareTime;
	}

	/**
	 * Sets the default time allotted to a round's {@link Stage#PREPARING
	 * preparation} period (default: 90). Set to a value less than or equal to
	 * zero to skip the preparation period.
	 *
	 * @param preparationTime the default time allotted to a round's {@link
	 *                        Stage#PREPARING preparation} period
	 * @since 0.1.0
	 */
	public void setDefaultPreparationTime(int preparationTime) {
		this.roundPrepareTime = preparationTime;
	}

	/**
	 * Retrieves the default time allotted to a round's {@link Stage#PLAYING
	 * playing} period (default: 300).
	 *
	 * @return the default time allotted to a round's {@link Stage#PLAYING
	 * playing} period
	 * @since 0.1.0
	 */
	public int getDefaultPlayingTime() {
		return roundPlayTime;
	}

	/**
	 * Sets the default time allotted to a round's {@link Stage#PLAYING playing}
	 * period (default: 300). Set to a value less than or equal to zero for an
	 * indefinite (infinite) time limit.
	 *
	 * @param playingTime the default time allotted to a round's {@link
	 *                    Stage#PLAYING playing} period
	 * @since 0.1.0
	 */
	public void setDefaultPlayingTime(int playingTime) {
		this.roundPlayTime = playingTime;
	}

	/**
	 * Retrieves whether players are allowed to join a round which {@link
	 * Stage#PLAYING has already started}. (default: <code>false</code>)
	 *
	 * @return whether players are allowed to join a round which {@link
	 * Stage#PLAYING has already started.}
	 * @since 0.1.0
	 */
	public boolean getAllowJoinRoundInProgress() {
		return joinRiP;
	}

	/**
	 * Sets whether players are allowed to join a round which {@link
	 * Stage#PLAYING has already started}. (default: <code>false</code>)
	 *
	 * @param allow whether players are allowed to join a round which {@link
	 *              Stage#PLAYING has already started.}
	 * @since 0.1.0
	 */
	public void setAllowJoinRoundInProgress(boolean allow) {
		this.joinRiP = allow;
	}

	/**
	 * Retrieves whether players are allowed to join a round which {@link
	 * Stage#PREPARING is in its preparation stage}. (default:
	 * <code>false</code>)
	 *
	 * @return whether players are allowed to join a round which {@link
	 * Stage#PREPARING is in its preparation stage.}
	 * @since 0.1.0
	 */
	public boolean getAllowJoinRoundWhilePreparing() {
		return joinRwP;
	}

	/**
	 * Sets whether players are allowed to join a round which {@link
	 * Stage#PREPARING is in its preparation stage}. (default:
	 * <code>false</code>)
	 *
	 * @param allow whether players are allowed to join a round which {@link
	 *              Stage#PREPARING is in its preparation stage}.
	 * @since 0.1.0
	 */
	public void setAllowJoinRoundWhilePreparing(boolean allow) {
		this.joinRwP = allow;
	}

	/**
	 * Retrieves whether players will be set to spectator mode upon joining
	 * around in progress. (default: <code>false</code>)
	 *
	 * @return whether players will be set to spectator mode upon joining around
	 * in progress
	 * @since 0.1.0
	 */
	public boolean getSpectateOnJoin() {
		return spectateJoin;
	}

	/**
	 * Sets whether players will be set to spectator mode upon joining around in
	 * progress. (default: <code>false</code>) This will have no effect if both
	 * {@link ConfigManager#getAllowJoinRoundWhilePreparing()} and {@link
	 * ConfigManager#getAllowJoinRoundInProgress()} return false.
	 *
	 * @param allowed whether players will be set to spectator mode upon joining
	 *                around in progress
	 * @since 0.1.0
	 */
	public void setSpectateOnJoin(boolean allowed) {
		this.spectateJoin = allowed;
	}

	/**
	 * Retrieves the color of the top line (the arena name) of a lobby sign.
	 *
	 * @return the color of the top line (the arena name) of a lobby sign
	 * @since 0.1.0
	 */
	public ChatColor getLobbyArenaColor() {
		return ChatColor.getByChar(lobbyColors.get("arena").getCode());
	}

	/**
	 * Sets the color of the top line (the arena name) of a lobby sign.
	 *
	 * @param color the new color of the top line of a lobby sign
	 * @since 0.3.1
	 */
	public void setLobbyArenaColor(Color color) {
		lobbyColors.put("arena", color);
	}

	/**
	 * Sets the color of the top line (the arena name) of a lobby sign.
	 *
	 * @param color the new color of the top line of a lobby sign
	 * @deprecated Use {@link ConfigManager#setLobbyArenaColor(Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyArenaColor(ChatColor color) {
		setLobbyArenaColor(Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves the color of a lobby sign's {@link Stage#WAITING WAITING}
	 * status.
	 *
	 * @return the color of a lobby sign's {@link Stage#WAITING WAITING} status
	 * @since 0.1.0
	 */
	public ChatColor getLobbyWaitingColor() {
		return ChatColor.getByChar(lobbyColors.get("waiting").getCode());
	}

	/**
	 * Sets the color of a lobby sign's {@link Stage#WAITING WAITING} status.
	 *
	 * @param color the new color of a lobby sign's {@link Stage#WAITING
	 *              WAITING} status
	 * @since 0.3.1
	 */
	public void setLobbyWaitingColor(Color color) {
		lobbyColors.put("waiting", color);
	}

	/**
	 * Sets the color of a lobby sign's {@link Stage#WAITING WAITING} status.
	 *
	 * @param color the new color of a lobby sign's {@link Stage#WAITING
	 *              WAITING} status
	 * @deprecated Use {@link ConfigManager#setLobbyWaitingColor(Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyWaitingColor(ChatColor color) {
		setLobbyWaitingColor(Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves the color of a lobby sign's {@link Stage#PREPARING PREPARING}
	 * status.
	 *
	 * @return the color of a lobby sign's {@link Stage#PREPARING PREPARING}
	 * status
	 * @since 0.1.0
	 */
	public ChatColor getLobbyPreparingColor() {
		return ChatColor.getByChar(lobbyColors.get("preparing").getCode());
	}

	/**
	 * Sets the color of a lobby sign's {@link Stage#PREPARING PREPARING}
	 * status.
	 *
	 * @param color the new color of a lobby sign's {@link Stage#PREPARING
	 *              PREPARING} status
	 * @since 0.3.1
	 */
	public void setLobbyPreparingColor(Color color) {
		lobbyColors.put("preparing", color);
	}

	/**
	 * Sets the color of a lobby sign's {@link Stage#PREPARING PREPARING}
	 * status.
	 *
	 * @param color the new color of a lobby sign's {@link Stage#PREPARING
	 *              PREPARING} status
	 * @deprecated Use {@link ConfigManager#setLobbyPreparingColor(Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyPreparingColor(ChatColor color) {
		setLobbyPreparingColor(Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves the color of a lobby sign's {@link Stage#PLAYING PLAYING}
	 * status.
	 *
	 * @return the color of a lobby sign's {@link Stage#PLAYING PLAYING} status
	 * @since 0.1.0
	 */
	public ChatColor getLobbyPlayingColor() {
		return ChatColor.getByChar(lobbyColors.get("playing").getCode());
	}

	/**
	 * Sets the color of a lobby sign's {@link Stage#PLAYING PLAYING} status.
	 *
	 * @param color the new color of a lobby sign's {@link Stage#PLAYING
	 *              PLAYING} status
	 * @since 0.3.1
	 */
	public void setLobbyPlayingColor(Color color) {
		lobbyColors.put("playing", color);
	}

	/**
	 * Sets the color of a lobby sign's {@link Stage#PLAYING PLAYING} status.
	 *
	 * @param color the new color of a lobby sign's {@link Stage#PLAYING
	 *              PLAYING} status
	 * @deprecated Use {@link ConfigManager#setLobbyPlayingColor(Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyPlayingColor(ChatColor color) {
		setLobbyPlayingColor(Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves the color of a lobby sign's {@link Stage#RESETTING RESETTING}
	 * status.
	 *
	 * @return the color of a lobby sign's {@link Stage#RESETTING RESETTING}
	 * status
	 * @since 0.1.0
	 */
	public ChatColor getLobbyResettingColor() {
		return ChatColor.getByChar(lobbyColors.get("resetting").getCode());
	}

	/**
	 * Sets the color of a lobby sign's {@link Stage#RESETTING RESETTING}
	 * status.
	 *
	 * @param color the new color of a lobby sign's {@link Stage#RESETTING
	 *              RESETTING} status
	 * @since 0.3.1
	 */
	public void setLobbyResettingColor(Color color) {
		lobbyColors.put("resetting", color);
	}

	/**
	 * Sets the color of a lobby sign's {@link Stage#RESETTING RESETTING}
	 * status.
	 *
	 * @param color the new color of a lobby sign's {@link Stage#RESETTING
	 *              RESETTING} status
	 * @deprecated Use {@link ConfigManager#setLobbyResettingColor(Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyResettingColor(ChatColor color) {
		lobbyColors.put("resetting", Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves the color of a lobby sign's timer when remaining time is
	 * greater than 60 seconds.
	 *
	 * @return the color of a lobby sign's timer when remaining time is greater
	 * than 60 seconds
	 * @since 0.1.0
	 */
	public ChatColor getLobbyTimeColor() {
		return ChatColor.getByChar(lobbyColors.get("time").getCode());
	}

	/**
	 * Sets the color of a lobby sign's timer when remaining time is greater
	 * than 60 seconds.
	 *
	 * @param color the new color of a lobby sign's timer when remaining time is
	 *              greater than 60 seconds
	 * @since 0.3.1
	 */
	public void setLobbyTimeColor(Color color) {
		lobbyColors.put("time", color);
	}

	/**
	 * Sets the color of a lobby sign's timer when remaining time is greater
	 * than 60 seconds.
	 *
	 * @param color the new color of a lobby sign's timer when remaining time is
	 *              greater than 60 seconds
	 * @deprecated Use {@link ConfigManager#setLobbyTimeColor(Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyTimeColor(ChatColor color) {
		lobbyColors.put("time", Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves the color of a lobby sign's timer when remaining time is less
	 * than 60 seconds.
	 *
	 * @return the color of a lobby sign's timer when remaining time is less
	 * than 60 seconds
	 * @since 0.1.0
	 */
	public ChatColor getLobbyTimeWarningColor() {
		return ChatColor.getByChar(lobbyColors.get("time-warning").getCode());
	}

	/**
	 * Sets the color of a lobby sign's timer when remaining time is less than
	 * 60 seconds.
	 *
	 * @param color the new color of a lobby sign's timer when remaining time is
	 *              less than 60 seconds
	 * @since 0.3.1
	 */
	public void setLobbyTimeWarningColor(Color color) {
		lobbyColors.put("time-warning", color);
	}

	/**
	 * Sets the color of a lobby sign's timer when remaining time is less than
	 * 60 seconds.
	 *
	 * @param color the new color of a lobby sign's timer when remaining time is
	 *              less than 60 seconds
	 * @deprecated Use {@link ConfigManager#setLobbyTimeWarningColor(Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyTimeWarningColor(ChatColor color) {
		lobbyColors.put("time-warning", Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves the color of a lobby sign's timer when remaining time is
	 * infinite.
	 *
	 * @return the color of a lobby sign's timer when remaining time is infinite
	 * @since 0.1.0
	 */
	public ChatColor getLobbyTimeInfiniteColor() {
		return ChatColor.getByChar(lobbyColors.get("time-infinite").getCode());
	}

	/**
	 * Sets the color of a lobby sign's timer when remaining time is infinite.
	 *
	 * @param color the new color of a lobby sign's timer when remaining time is
	 *              infinite
	 * @since 0.3.1
	 */
	public void setLobbyTimeInfiniteColor(Color color) {
		lobbyColors.put("time-infinite", color);
	}

	/**
	 * Sets the color of a lobby sign's timer when remaining time is infinite.
	 *
	 * @param color the new color of a lobby sign's timer when remaining time is
	 *              infinite
	 * @deprecated Use {@link ConfigManager#setLobbyTimeInfiniteColor(Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyTimeInfiniteColor(ChatColor color) {
		lobbyColors.put("time-infinite", Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves the color of a lobby sign's player count when the round is not
	 * full.
	 *
	 * @return the color of a lobby sign's player count when the round is not
	 * full
	 * @since 0.1.0
	 */
	public ChatColor getLobbyPlayerCountColor() {
		return ChatColor.getByChar(lobbyColors.get("player-count").getCode());
	}

	/**
	 * Sets the color of a lobby sign's player count when the round is not
	 * full.
	 *
	 * @param color the new color of a lobby sign's player count when the round
	 *              is not full
	 * @since 0.3.1
	 */
	public void setLobbyPlayerCountColor(Color color) {
		lobbyColors.put("player-count", color);
	}

	/**
	 * Sets the color of a lobby sign's player count when the round is not
	 * full.
	 *
	 * @param color the new color of a lobby sign's player count when the round
	 *              is not full
	 * @deprecated Use {@link ConfigManager#setLobbyPlayerCountColor(Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyPlayerCountColor(ChatColor color) {
		lobbyColors.put("player-count", Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves the color of a lobby sign's player count when the round is
	 * full.
	 *
	 * @return the color of a lobby sign's player count when the round is full
	 * @since 0.1.0
	 */
	public ChatColor getLobbyPlayerCountFullColor() {
		return ChatColor.getByChar(lobbyColors.get("player-count-full").getCode());
	}

	/**
	 * Sets the color of a lobby sign's player count when the round is full.
	 *
	 * @param color the new color of a lobby sign's player count when the round
	 *              is full
	 * @since 0.3.1
	 */
	public void setLobbyPlayerCountFullColor(Color color) {
		lobbyColors.put("player-count-full", color);
	}

	/**
	 * Sets the color of a lobby sign's player count when the round is full.
	 *
	 * @param color the new color of a lobby sign's player count when the round
	 *              is full
	 * @deprecated Use {@link ConfigManager#setLobbyPlayerCountFullColor(Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyPlayerCountFullColor(ChatColor color) {
		lobbyColors.put("player-count-full", Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves a copy of the hashmap containing all color attributes for lobby
	 * signs associated with this manager's plugin.
	 *
	 * @return a copy of the hashmap containing all color attributes for lobby
	 * signs associated with this manager's plugin.
	 * @since 0.1.0
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ChatColor> getLobbyColorAttributes() {
		return (HashMap<String, ChatColor>)lobbyColors.clone();
	}

	/**
	 * Retrieves a specific color attribute for lobby signs associated with this
	 * manager's plugin.
	 *
	 * @param key the attribute to fetch the color for
	 * @return a specific color attribute for lobby signs associated with this
	 * manager's plugin, or null if it does not exist.
	 * @since 0.1.0
	 */
	public ChatColor getLobbyColorAttribute(String key) {
		return ChatColor.getByChar(lobbyColors.get(key).getCode());
	}

	/**
	 * Sets a specific color attribute for lobby signs associated with this
	 * manager's plugin. If the provided key is not used in the library, this
	 * method will have no effect.
	 *
	 * @param key   the key of the color attribute (e.g. arena, player-count,
	 *              time)
	 * @param color the color to associate with aforementioned key
	 * @since 0.3.1
	 */
	public void setLobbyColor(String key, Color color) {
		lobbyColors.put(key, color);
	}

	/**
	 * Sets a specific color attribute for lobby signs associated with this
	 * manager's plugin. If the provided key is not used in the library, this
	 * method will have no effect.
	 *
	 * @param key   the key of the color attribute (e.g. arena, player-count,
	 *              time)
	 * @param color the color to associate with aforementioned key
	 * @deprecated Use {@link ConfigManager#setLobbyColor(String, Color)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setLobbyColor(String key, ChatColor color) {
		lobbyColors.put(key, Color.fromCode(color.getChar()));
	}

	/**
	 * Retrieves whether teleportation (via ender pearl, command, or another
	 * plugin) is permitted for players in a {@link Round round}. (default:
	 * true)
	 *
	 * @return whether teleportation is permitted for players in a {@link Round
	 * round}
	 * @since 0.1.0
	 */
	public boolean isTeleportationAllowed() {
		return actions.get("teleport");
	}

	/**
	 * Sets whether teleportation (via ender pearl, command, or another plugin)
	 * is permitted for players in a {@link Round round}. (default:
	 * <code>true</code>)
	 *
	 * @param allowed whether teleportation is permitted for players in a {@link
	 *                Round round}
	 * @since 0.1.0
	 */
	public void setTeleportationAllowed(boolean allowed) {
		actions.put("teleport", allowed);
	}

	/**
	 * Retrieves whether players partaking in a minigame are permitted to send
	 * private messages or use the /pm command. (default: <code>true</code>)
	 * This can help limit communication of volatile information between teams,
	 * such as in <a href="http://dev.bukkit.org/bukkit-plugins/TTT">TTT</a>.
	 *
	 * @return whether players partaking in a minigame are permitted to send
	 * private messages or use the /pm command
	 * @since 0.1.0
	 */
	public boolean arePMsAllowed() {
		return pmsAllowed;
	}

	/**
	 * Sets whether players partaking in a minigame are permitted to send
	 * private messages or use the /pm command. (default: <code>true</code>)
	 *
	 * @param allowed whether players partaking in a minigame are permitted to
	 *                send private messages or use the /pm command.
	 * @since 0.1.0
	 */
	public void setPMsAllowed(boolean allowed) {
		this.pmsAllowed = allowed;
	}

	/**
	 * Retrieves whether players are permitted to use the /kit command while
	 * partaking in a minigame. (default: <code>true</code>)
	 *
	 * @return whether players are permitted to use the /kit command while
	 * partaking in a minigame
	 * @since 0.1.0
	 */
	public boolean areKitsAllowed() {
		return kitsAllowed;
	}

	/**
	 * Sets whether players are permitted to use the /kit command while
	 * partaking in a minigame. (default: <code>true</code>)
	 *
	 * @param allowed whether players are permitted to use the /kit command
	 *                while partaking in a minigame
	 * @since 0.1.0
	 */
	public void setKitsAllowed(boolean allowed) {
		this.kitsAllowed = allowed;
	}

	/**
	 * Retrieves whether block placing is permitted for players in a {@link
	 * Round round}. (default: <code>false</code>)
	 *
	 * @return whether block placing is permitted for players in a {@link Round
	 * round}
	 * @since 0.1.0
	 */
	public boolean isBlockPlaceAllowed() {
		return actions.get("block-place");
	}

	/**
	 * Sets whether block placing is permitted for players in a {@link Round
	 * round}. (default: <code>false</code>)
	 *
	 * @param allowed whether block placing is permitted for players in a {@link
	 *                Round round}
	 * @since 0.1.0
	 */
	public void setBlockPlaceAllowed(boolean allowed) {
		actions.put("block-place", allowed);
	}

	/**
	 * Retrieves whether block breaking is permitted for players in a {@link
	 * Round round}. (default: <code>false</code>)
	 *
	 * @return whether block breaking is permitted for players in a {@link Round
	 * round}
	 * @since 0.1.0
	 */
	public boolean isBlockBreakAllowed() {
		return actions.get("block-break");
	}

	/**
	 * Sets whether block breaking is permitted for players in a {@link Round
	 * round}. (default: <code>false</code>)
	 *
	 * @param allowed whether block breaking is permitted for players in a
	 *                {@link Round round}
	 * @since 0.1.0
	 */
	public void setBlockBreakAllowed(boolean allowed) {
		actions.put("block-break", allowed);
	}

	/**
	 * Retrieves whether hanging entity (e.g. paintings, item frames) breaking
	 * is permitted for players in a {@link Round round}. (default:
	 * <code>false</code>)
	 *
	 * @return whether hanging entity breaking is permitted for players in a
	 * {@link Round round}
	 * @since 0.3.0
	 */
	public boolean isHangingBreakAllowed() {
		return actions.get("hanging-break");
	}

	/**
	 * Sets whether hanging entity (e.g. paintings, item frames) breaking is
	 * permitted for players in a {@link Round round}. (default:
	 * <code>false</code>)
	 *
	 * @param allowed whether hanging entity breaking is permitted for players
	 *                in a {@link Round round}
	 * @since 0.3.0
	 */
	public void setHangingBreakAllowed(boolean allowed) {
		actions.put("hanging-break", allowed);
	}

	/**
	 * Retrieves whether players in a minigame may remove the item from an item
	 * frame by clicking on it. (default: <code>false</code>)
	 *
	 * @return whether players in a minigame may remove the item from an item
	 * frame by clicking on it
	 * @since 0.3.0
	 */
	public boolean isItemFrameDamageAllowed() {
		return actions.get("item-frame-damage");
	}

	/**
	 * Sets whether players in a minigame may remove the item from an item frame
	 * by clicking on it. (default: <code>false</code>)
	 *
	 * @param allowed whether players in a minigame may remove the item from an
	 *                item frame by clicking on it
	 * @since 0.3.0
	 */
	public void setItemFrameDamageAllowed(boolean allowed) {
		actions.put("item-frame-damage", allowed);
	}

	/**
	 * Retrieves whether block burning is permitted in worlds containing one or
	 * more arenas. (default: <code>false</code>)
	 *
	 * @return whether block burning is permitted in worlds containing one or
	 * more arenas
	 * @since 0.1.0
	 */
	public boolean isBlockBurnAllowed() {
		return actions.get("block-burn");
	}

	/**
	 * Sets whether block burning is permitted in worlds containing one or more
	 * arenas. (default: <code>false</code>)
	 *
	 * @param allowed whether block burning is permitted in worlds containing
	 *                one or more arenas
	 * @since 0.3.1
	 */
	public void setBlockBurnAllowed(boolean allowed) {
		actions.put("block-burn", allowed);
	}

	/**
	 * Sets whether block burning is permitted in worlds containing one or more
	 * arenas. (default: <code>false</code>)
	 *
	 * @param allowed whether block burning is permitted in worlds containing
	 *                one or more arenas
	 * @since 0.1.0
	 * @deprecated This method is improperly named, but remains for the sake of
	 * reverse compatibility. You should instead use {@link
	 * ConfigManager#setBlockBurnAllowed(boolean)}.
	 */
	@Deprecated
	public void setBlockBurningAllowed(boolean allowed) {
		this.setBlockBurnAllowed(allowed);
	}

	/**
	 * Retrieves whether block fading (e.g. leaves decaying) is permitted in
	 * worlds containing one or more arenas. (default: <code>false</code>)
	 *
	 * @return whether block fading (e.g. leaves decaying) is permitted in
	 * worlds containing one or more arenas
	 * @since 0.1.0
	 */
	public boolean isBlockFadeAllowed() {
		return actions.get("block-fade");
	}

	/**
	 * Sets whether block fading (e.g. leaves decaying) is permitted in worlds
	 * containing one or more arenas. (default: <code>false</code>)
	 *
	 * @param allowed whether block fading (e.g. leaves decaying) is permitted
	 *                in worlds containing one or more arenas
	 * @since 0.1.0
	 */
	public void setBlockFadeAllowed(boolean allowed) {
		actions.put("block-fade", allowed);
	}

	/**
	 * Retrieves whether block growing (e.g. reeds growing) is permitted in
	 * worlds containing one or more arenas. (default: <code>false</code>)
	 *
	 * @return whether block growing (e.g. reeds growing) is permitted in worlds
	 * containing one or more arenas
	 * @since 0.1.0
	 */
	public boolean isBlockGrowAllowed() {
		return actions.get("block-grow");
	}

	/**
	 * Sets whether block growing (e.g. reeds growing) is permitted in worlds
	 * containing one or more arenas. (default: <code>false</code>)
	 *
	 * @param allowed whether block growing (e.g. reeds growing) is permitted in
	 *                worlds containing one or more arenas
	 * @since 0.1.0
	 */
	public void setBlockGrowAllowed(boolean allowed) {
		actions.put("block-grow", allowed);
	}

	/**
	 * Retrieves whether block ignition is permitted in worlds containing one or
	 * more arenas. (default: <code>false</code>)
	 *
	 * @return whether block ignition is permitted in worlds containing one or
	 * more arenas
	 * @since 0.1.0
	 */
	public boolean isBlockIgniteAllowed() {
		return actions.get("block-ignite");
	}

	/**
	 * Sets whether block ignition is permitted in worlds containing one or more
	 * arenas. (default: <code>false</code>)
	 *
	 * @param allowed whether block ignition is permitted in worlds containing
	 *                one or more arenas
	 * @since 0.1.0
	 */
	public void setBlockIgniteAllowed(boolean allowed) {
		actions.put("block-ignite", allowed);
	}

	/**
	 * Retrieves whether block flowing is permitted in worlds containing one or
	 * more arenas. (default: <code>true</code>)
	 *
	 * @return whether block flowing is permitted in worlds containing one or
	 * more arenas
	 * @since 0.1.0
	 */
	public boolean isBlockFlowAllowed() {
		return actions.get("block-flow");
	}

	/**
	 * Sets whether block flowing is permitted in worlds containing one or more
	 * arenas. (default: <code>true</code>)
	 *
	 * @param allowed whether block flowing is permitted in worlds containing
	 *                one or more arenas
	 * @since 0.1.0
	 */
	public void setBlockFlowAllowed(boolean allowed) {
		actions.put("block-flow", allowed);
	}

	/**
	 * Retrieves whether block physics are permitted in worlds containing one or
	 * more arenas. (default: <code>true</code>)
	 *
	 * @return whether block physics are permitted in worlds containing one or
	 * more arenas
	 * @since 0.3.1
	 */
	public boolean areBlockPhysicsAllowed() {
		return actions.get("block-physics");
	}

	/**
	 * Retrieves whether block physics are permitted in worlds containing one or
	 * more arenas. (default: <code>true</code>)
	 *
	 * @return whether block physics are permitted in worlds containing one or
	 * more arenas
	 * @since 0.1.0
	 * @deprecated This method is improperly named, but remains for the sake of
	 * reverse compatibility. You should instead use {@link
	 * ConfigManager#areBlockPhysicsAllowed()}.
	 */
	@Deprecated
	public boolean isBlockPhysicsAllowed() {
		return this.areBlockPhysicsAllowed();
	}

	/**
	 * Sets whether block physics are permitted in worlds containing one or more
	 * arenas. (default: <code>true</code>)
	 *
	 * @param allowed whether block physics are permitted in worlds containing
	 *                one or more arenas
	 * @since 0.3.0
	 */
	public void setBlockPhysicsAllowed(boolean allowed) {
		actions.put("block-physics", allowed);
	}

	/**
	 * Sets whether block physics are permitted in worlds containing one or more
	 * arenas. (default: <code>true</code>)
	 *
	 * @param allowed whether block physics are permitted in worlds containing
	 *                one or more arenas
	 * @since 0.1.0
	 * @deprecated This method is improperly named, but remains for the sake of
	 * reverse compatibility. You should instead use {@link
	 * ConfigManager#setBlockPhysicsAllowed(boolean)}.
	 */
	@Deprecated
	public void setBlockPhyiscsAllowed(boolean allowed) {
		setBlockPhysicsAllowed(allowed);
	}

	/**
	 * Retrieves whether pistons are permitted in worlds containing one or more
	 * arenas. (default: <code>true</code>)
	 *
	 * @return whether pistons are permitted in worlds containing one or more
	 * arenas
	 * @since 0.1.0
	 */
	public boolean isBlockPistonAllowed() {
		return actions.get("block-piston");
	}

	/**
	 * Sets whether pistons are permitted in worlds containing one or more
	 * arenas. (default: <code>true</code>)
	 *
	 * @param allowed whether pistons are permitted in worlds containing one or
	 *                more arenas
	 * @since 0.1.0
	 */
	public void setBlockPistonAllowed(boolean allowed) {
		actions.put("block-piston", allowed);
	}

	/**
	 * Retrieves whether block spreading (e.g. grass spreding to dirt blocks)
	 * are permitted in worlds containing one or more arenas. (default:
	 * <code>false</code>)
	 *
	 * @return whether block spreading (e.g. grass spreding to dirt blocks) are
	 * permitted in worlds containing one or more arenas.
	 * @since 0.1.0
	 */
	public boolean isBlockSpreadAllowed() {
		return actions.get("block-spread");
	}

	/**
	 * Sets whether block spreading (e.g. grass spreding to dirt blocks) are
	 * permitted in worlds containing one or more arenas. (default:
	 * <code>false</code>)
	 *
	 * @param allowed whether block spreading (e.g. grass spreding to dirt
	 *                blocks) are permitted in worlds containing one or more
	 *                arenas.
	 * @since 0.1.0
	 */
	public void setBlockSpreadAllowed(boolean allowed) {
		actions.put("block-spread", allowed);
	}

	/**
	 * Retrieves whether entity explosions are permitted in worlds containing
	 * one or more arenas. (default: <code>false</code>)
	 *
	 * @return whether entity explosions are permitted in worlds containing one
	 * or more arenas
	 * @since 0.3.1
	 */
	public boolean areEntityExplosionsAllowed() {
		return actions.get("entity-explode");
	}

	/**
	 * Retrieves whether entity explosions are permitted in worlds containing
	 * one or more arenas. (default: <code>false</code>)
	 *
	 * @return whether entity explosions are permitted in worlds containing one
	 * or more arenas
	 * @since 0.3.0
	 * @deprecated This method is improperly named, but remains for the sake of
	 * reverse compatibility. You should instead use {@link
	 * ConfigManager#areEntityExplosionsAllowed()}.
	 */
	@Deprecated
	public boolean isEntityExplosionsAllowed() {
		return this.areEntityExplosionsAllowed();
	}

	/**
	 * Sets whether entity explosions are permitted in worlds containing one or
	 * more arenas. (default: <code>false</code>)
	 *
	 * @param allowed whether entity explosions are permitted in worlds
	 *                containing one or more arenas
	 * @since 0.3.0
	 */
	public void setEntityExplosionsAllowed(boolean allowed) {
		actions.put("entity-explode", allowed);
	}

	/**
	 * Retrieves the {@link Class class} object used to store information about
	 * players in minigame rounds. (default: {@link MGPlayer MGPlayer.class}
	 *
	 * @return the static class to use for player storage (e.g.
	 * CustomPlayer.class)
	 * @since 0.1.0
	 * @deprecated Use metadata instead.
	 */
	@Deprecated
	public Class<? extends MGPlayer> getPlayerClass() {
		return playerClass;
	}

	/**
	 * Sets the {@link Class class} object used to store information about
	 * players in minigame rounds. (default: {@link MGPlayer MGPlayer.class})
	 * This may be used to add additional fields to MGLib's default {@link
	 * MGPlayer} class.
	 *
	 * @param clazz the static class to use for player storage (e.g.
	 *              CustomPlayer.class)
	 * @since 0.1.0
	 * @deprecated Use metadata instead.
	 */
	@Deprecated
	public void setPlayerClass(Class<? extends MGPlayer> clazz) {
		if (!clazz.equals(MGPlayer.class)) {
			this.playerClass = clazz;
			this.getMinigame().customPlayerClass = true;
		}
	}

	/**
	 * Retrieves the {@link Class class} object used to store information about
	 * minigame rounds. (default: {@link Round Round.class}
	 *
	 * @return the static class to use for round storage (e.g.
	 * CustomRound.class)
	 * @since 0.3.0
	 * @deprecated Use metadata instead.
	 */
	@Deprecated
	public Class<? extends Round> getRoundClass() {
		return roundClass;
	}

	/**
	 * Sets the {@link Class class} object used to store information about
	 * minigame rounds. (default: {@link Round Round.class}) This may be used to
	 * add additional fields or methods to MGLib's default {@link Round} class.
	 *
	 * @param clazz the static class to use for round storage (e.g.
	 *              CustomRound.class)
	 * @since 0.3.0
	 * @deprecated Use metadata instead.
	 */
	@Deprecated
	public void setRoundClass(Class<? extends Round> clazz) {
		if (!clazz.equals(Round.class)) {
			this.roundClass = clazz;
			this.getMinigame().customRoundClass = true;
		}
	}

	/**
	 * Retrieves the default {@link GameMode gamemode} for players entering
	 * minigame rounds. (default: {@link GameMode#SURVIVAL})
	 *
	 * @return the default {@link GameMode gamemode} for players entering
	 * minigame rounds
	 * @since 0.1.0
	 */
	public GameMode getDefaultGameMode() {
		return gameMode;
	}

	/**
	 * Sets the default {@link GameMode gamemode} for players entering minigame
	 * rounds. (default: {@link GameMode#SURVIVAL}
	 *
	 * @param gameMode the default {@link GameMode gamemode} for players
	 *                 entering minigame rounds
	 * @since 0.1.0
	 */
	public void setDefaultGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	/**
	 * Retrieves whether PvP is allowed by default. (default:
	 * <code>true</code>)
	 *
	 * @return whether PvP is allowed by default
	 * @since 0.1.0
	 */
	public boolean isPvPAllowed() {
		return pvp;
	}

	/**
	 * Sets whether PvP is allowed by default. (default: <code>true</code>)
	 *
	 * @param allowed whether PvP is allowed by default
	 * @since 0.1.0
	 */
	public void setPvPAllowed(boolean allowed) {
		this.pvp = allowed;
	}

	/**
	 * Retrieves whether players in rounds may receive damage. (default:
	 * <code>true</code>)
	 *
	 * @return whether players in rounds may receive damage
	 * @since 0.1.0
	 */
	public boolean isDamageAllowed() {
		return damage;
	}

	/**
	 * Sets whether players in rounds may receive damage by default. (default:
	 * true)
	 *
	 * @param allowed whether players in rounds may receive damage by default
	 * @since 0.1.0
	 */
	public void setDamageAllowed(boolean allowed) {
		this.damage = allowed;
	}

	/**
	 * Retrieves whether rollback is enabled by default. (default:
	 * <code>true</code>)
	 *
	 * @return whether rollback is enabled by default
	 * @since 0.2.0
	 */
	public boolean isRollbackEnabled() {
		return rollback;
	}

	/**
	 * Sets whether rollback is enabled by default. (default:
	 * <code>true</code>)
	 *
	 * @param enabled whether rollback is enabled by default
	 * @since 0.2.0
	 */
	public void setRollbackEnabled(boolean enabled) {
		this.rollback = enabled;
	}

	/**
	 * Retrieves whether spectators' names are displayed on lobby signs.
	 * (defualt: true)
	 *
	 * @return whether spectators' names are displayed on lobby signs
	 * @since 0.2.0
	 */
	public boolean areSpectatorsOnLobbySigns() {
		return spectatorsOnSigns;
	}

	/**
	 * Sets whether spectators' names are displayed on lobby signs. (default:
	 * true)
	 *
	 * @param displayed whether spectators' names are displayed on lobby signs
	 * @since 0.2.0
	 */
	public void setSpectatorsOnLobbySigns(boolean displayed) {
		this.spectatorsOnSigns = displayed;
	}

	/**
	 * Retrieves whether spectators are permitted to fly. (default:
	 * <code>true</code>) This will be overridden if allow-flight is set to
	 * false in the server.properties file, or if {@link
	 * ConfigManager#isUsingVanillaSpectating()} returns true.
	 *
	 * @return whether spectators are permitted to fly
	 * @since 0.2.0
	 */
	public boolean isSpectatorFlightAllowed() {
		return spectatorFlight;
	}

	/**
	 * Sets whether spectators are permitted to fly. (default:
	 * <code>true</code>) This will be overridden if allow-flight is set to
	 * false in the server.properties file, or if {@link
	 * ConfigManager#isUsingVanillaSpectating()} returns true.
	 *
	 * @param allowed whether spectators are permitted to fly
	 * @since 0.2.0
	 */
	public void setSpectatorFlightAllowed(boolean allowed) {
		this.spectatorFlight = allowed;
	}

	/**
	 * Retrieves whether players are permitted to damage teammates. (default:
	 * true)
	 *
	 * @return whether players are permitted to damage teammates
	 * @since 0.3.0
	 */
	public boolean isTeamDamageAllowed() {
		return teamDamage;
	}

	/**
	 * Sets whether players are permitted to damage teammates. (default:
	 * <code>true</code>)
	 *
	 * @param allowed whether players are permitted to damage teammates
	 * @since 0.3.0
	 */
	public void setTeamDamageAllowed(boolean allowed) {
		this.teamDamage = allowed;
	}

	/**
	 * Retrieves the locale to fall back to if the one defined in the MGLib
	 * config cannot be loaded. (default: enUS)
	 *
	 * @return the locale to fall back to if the one defined in the MGLib config
	 * cannot be loaded
	 * @since 0.3.0
	 */
	public String getDefaultLocale() {
		return locale;
	}

	/**
	 * Sets the locale to fall back to if the one defined in the MGLib config
	 * cannot be loaded. (default: enUS)
	 *
	 * @param locale the locale to fall back to if the one defined in the MGLib
	 *               config cannot be loaded
	 * @since 0.3.0
	 */
	public void setDefaultLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Retrieves whether players are sent to a random spawn when entered into an
	 * arena. (default: <code>true</code>)
	 *
	 * @return whether players should be sent to a random spawn
	 * @since 0.3.0
	 */
	public boolean isRandomSpawning() {
		return randomSpawns;
	}

	/**
	 * Sets whether players are sent to a random spawn when entered into an
	 * arena. (default: <code>true</code>) When false, the first player to join
	 * will be sent to spawn 0, the second to spawn 1, etc.
	 *
	 * @param random whether players should be sent to a random spawn
	 * @since 0.3.0
	 */
	public void setRandomSpawning(boolean random) {
		this.randomSpawns = random;
	}

	/**
	 * Retrieves whether Bukkit's {@link PlayerDeathEvent} will be overridden
	 * for players participating in minigame rounds. (default:
	 * <code>false</code>) If true, the death event will be cancelled and a
	 * custom MGLib event will be thrown instead.
	 *
	 * @return whether Bukkit's {@link PlayerDeathEvent} will be overridden
	 * @since 0.3.0
	 */
	public boolean isOverrideDeathEvent() {
		return overrideDeathEvent;
	}

	/**
	 * Sets whether Bukkit's {@link PlayerDeathEvent} will be overridden for
	 * players participating in minigame rounds. (default: <code>false</code>)
	 * If true, the death event will be cancelled and a custom MGLib event will
	 * be thrown instead.
	 *
	 * @param override whether Bukkit's {@link PlayerDeathEvent} will be
	 *                 overridden
	 * @since 0.3.0
	 */
	public void setOverrideDeathEvent(boolean override) {
		this.overrideDeathEvent = override;
	}

	/**
	 * Retrieves whether hunger should drain from players in rounds. (default:
	 * false)
	 *
	 * @return whether hunger should drain from players in rounds
	 * @since 0.3.0
	 */
	public boolean isHungerEnabled() {
		return hunger;
	}

	/**
	 * Retrieves whether hunger should drain from players in rounds. (default:
	 * false)
	 *
	 * @param enabled whether hunger should drain from players in rounds
	 * @since 0.3.0
	 */
	public void setHungerEnabled(boolean enabled) {
		this.hunger = enabled;
	}

	/**
	 * Retrieves whether chat should be handled on a per-round basis (e.g.
	 * players in an arena only see messages sent by players in the same arena).
	 * (default: <code>true</code>)
	 *
	 * @return whether chat should be handled on a per-round basis (e.g. players
	 * in an arena only see messages sent by players in the same arena).
	 * @since 0.3.0
	 */
	public boolean isPerRoundChatEnabled() {
		return perRoundChat;
	}

	/**
	 * Sets whether chat should be handled on a per-round basis (e.g. players in
	 * an arena only see messages sent by players in the same arena). (default:
	 * true)
	 *
	 * @param enabled whether chat should be handled on a per-round basis (e.g.
	 *                players in an arena only see messages sent by players in
	 *                the same arena).
	 * @since 0.3.0
	 */
	public void setPerRoundChatEnabled(boolean enabled) {
		this.perRoundChat = enabled;
	}

	/**
	 * Retrieves whether teams in a round should have separate chat channels.
	 * (default: <code>false</code>) (default: <code>false</code>)
	 *
	 * @return whether teams in a round should have separate chat channels
	 * @since 0.3.0
	 */
	public boolean isTeamChatEnabled() {
		return teamChat;
	}

	/**
	 * Sets whether teams in a round should have separate chat channels.
	 * (default: <code>false</code>)
	 *
	 * @param enabled whether teams in a round should have separate chat
	 *                channels
	 * @since 0.3.0
	 */
	public void setTeamChatEnabled(boolean enabled) {
		this.teamChat = enabled;
	}

	/**
	 * Retrieves whether mob spawning is permitted in worlds containing an
	 * arena. (default: <code>true</code>)
	 *
	 * @return whether mob spawning is permitted in worlds containing an arena
	 * @since 0.3.0
	 */
	public boolean isMobSpawningAllowed() {
		return mobSpawning;
	}

	/**
	 * Sets whether mob spawning is permitted in worlds containing an arena.
	 * (default: <code>true</code>)
	 *
	 * @param allowed whether mob spawning is permitted in worlds containing an
	 *                arena
	 * @since 0.3.0
	 */
	public void setMobSpawningAllowed(boolean allowed) {
		this.mobSpawning = allowed;
	}

	/**
	 * Retrieves whether entities are permitted to target players in arenas.
	 * (default: <code>false</code>)
	 *
	 * @return whether entities are permitted to target players in arenas
	 * @since 0.3.0
	 */
	public boolean isEntityTargetingEnabled() {
		return targeting;
	}

	/**
	 * Sets whether entities are permitted to target players in arenas.
	 * (default: <code>false</code>)
	 *
	 * @param enabled whether entities are permitted to target players in
	 *                arenas
	 * @since 0.3.0
	 */
	public void setEntityTargetingEnabled(boolean enabled) {
		this.targeting = enabled;
	}

	/**
	 * Retrieves whether spectators are placed in a separate chat channel from
	 * active players. (default: <code>true</code>)
	 *
	 * @return whether spectators are placed in a separate chat channel from
	 * active players
	 * @since 0.3.0
	 */
	public boolean isSpectatorChatSeparate() {
		return spectatorChat;
	}

	/**
	 * Sets whether spectators are placed in a separate chat channel from active
	 * players. (default: <code>true</code>)
	 *
	 * @param separate whether spectators are placed in a separate chat channel
	 *                 from active players
	 * @since 0.3.0
	 */
	public void setSpectatorChatSeparate(boolean separate) {
		this.spectatorChat = separate;
	}

	/**
	 * Retrieves whether players are made vanilla spectators when {@link
	 * MGPlayer#setSpectating(boolean)} is called. (default: <code>true</code>)
	 * If false, the player will be manually made invisible and flying.
	 *
	 * @return whether players are made vanilla spectators when {@link
	 * MGPlayer#setSpectating(boolean)} is called
	 * @since 0.3.0
	 */
	public boolean isUsingVanillaSpectating() {
		return vanillaSpectating;
	}

	/**
	 * Sets whether players are made vanilla spectators when {@link
	 * MGPlayer#setSpectating(boolean)} is called. (default: <code>true</code>)
	 * If false, the player will be manually made invisible and flying.
	 *
	 * @param vanillaSpectating whether players are made vanilla spectators when
	 *                          {@link MGPlayer#setSpectating(boolean)} is
	 *                          called.
	 * @since 0.3.0
	 */
	public void setUsingVanillaSpectating(boolean vanillaSpectating) {
		this.vanillaSpectating = vanillaSpectating;
	}

	/**
	 * Retrieves whether spectators should be displayed on the tab list.
	 * (default: <code>true</code>)
	 *
	 * @return whether spectators should be displayed on the tab list
	 * @since 0.3.0
	 */
	public boolean areSpectatorsInTabList() {
		return spectatorsInTabList;
	}

	/**
	 * Sets whether spectators should be displayed on the tab list. (default:
	 * true)
	 *
	 * @param allowed whether spectators should be displayed on the tab list
	 * @since 0.3.0
	 */
	public void setSpectatorsInTabList(boolean allowed) {
		this.spectatorsInTabList = allowed;
	}
}
