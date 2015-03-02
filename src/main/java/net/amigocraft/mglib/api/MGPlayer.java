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

import static net.amigocraft.mglib.Main.locale;

import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.Main;
import net.amigocraft.mglib.UUIDFetcher;
import net.amigocraft.mglib.event.player.MGPlayerSpectateEvent;
import net.amigocraft.mglib.exception.NoSuchPlayerException;
import net.amigocraft.mglib.exception.PlayerOfflineException;
import net.amigocraft.mglib.exception.PlayerPresentException;
import net.amigocraft.mglib.exception.RoundFullException;
import net.amigocraft.mglib.misc.JoinResult;
import net.amigocraft.mglib.misc.Metadatable;
import net.amigocraft.mglib.util.NmsUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

/**
 * Represents a player participating in a minigame.
 *
 * @since 0.1.0
 */
public class MGPlayer implements Metadatable {

	HashMap<String, Object> metadata = new HashMap<String, Object>();

	private String plugin;
	private String name;
	private String arena;
	private boolean spectating = false;
	private String prefix = "";
	private GameMode prevGameMode;
	private String team = null;
	private boolean frozen = false;

	/**
	 * Creates a new MGPlayer instance.
	 *
	 * @param plugin the plugin to associate the MGPlayer with
	 * @param name   the username of the player
	 * @param arena  the arena of the player
	 * @since 0.1.0
	 */
	public MGPlayer(String plugin, String name, String arena) {
		this.plugin = plugin;
		this.name = name;
		this.arena = arena;
	}

	/**
	 * Gets the minigame plugin associated with this {@link MGPlayer}.
	 *
	 * @return the minigame plugin associated with this {@link MGPlayer}
	 * @since 0.1.0
	 */
	public String getPlugin() {
		return plugin;
	}

	/**
	 * Gets the MGLib API instance registered by the minigame plugin associated
	 * with this {@link MGPlayer}.
	 *
	 * @return the MGLib API instance registered by the minigame plugin
	 * associated with this {@link MGPlayer}
	 * @since 0.1.0
	 */
	public Minigame getMinigame() {
		return Minigame.getMinigameInstance(plugin);
	}

	/**
	 * Gets the username of this {@link MGPlayer}.
	 *
	 * @return the username of this {@link MGPlayer}
	 * @since 0.1.0
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the arena associated with this {@link MGPlayer}.
	 *
	 * @return the arena associated with this {@link MGPlayer}
	 * @since 0.1.0
	 */
	public String getArena() {
		return arena;
	}

	/**
	 * Retrieves the prefix of this player (used on lobby signs).
	 *
	 * @return the prefix of this player
	 * @since 0.1.0
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Retrieves the name of the team this player is on, or null if they are not
	 * on a team.
	 *
	 * @return the name of the team this player is on, or null if they are not
	 * on a team
	 * @since 0.3.0
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * Sets the name of the team this player is on.
	 *
	 * @param team the name of the team this player is on. Set to null for no
	 *             team.
	 * @since 0.3.0
	 */
	public void setTeam(String team) {
		this.team = team;
	}

	/**
	 * Sets the arena of this {@link MGPlayer}. Please do not call this method
	 * unless you understand the implications of doing so.
	 *
	 * @param arena the new arena of this {@link MGPlayer}
	 * @since 0.1.0
	 */
	public void setArena(String arena) {
		this.arena = arena;
	}

	/**
	 * Gets the {@link Round} associated with this player.
	 *
	 * @return the {@link Round} associated with this player
	 * @since 0.1.0
	 */
	public Round getRound() {
		return Minigame.getMinigameInstance(plugin).getRound(arena.toLowerCase());
	}

	/**
	 * Gets whether this player is spectating their round, as opposed to
	 * participating in it.
	 *
	 * @return whether this player is spectating their round (can return true
	 * even if {@link Player#isDead()} returns false).
	 * @since 0.1.0
	 */
	public boolean isSpectating() {
		return spectating;
	}

	/**
	 * Sets whether this player is spectating or not.
	 *
	 * @param spectating whether the player is spectating
	 * @since 0.1.0
	 */
	@SuppressWarnings("unchecked")
	public void setSpectating(boolean spectating) {
		this.spectating = spectating;
		if (spectating) {
			MGPlayerSpectateEvent event = new MGPlayerSpectateEvent(this.getRound(), this);
			MGUtil.callEvent(event);
			if (event.isCancelled()) {
				return;
			}
			@SuppressWarnings("deprecation")
			final Player p = Bukkit.getPlayer(this.getName());
			if (p != null) { // check that player is online
				p.closeInventory(); // close any inventory they have open
				for (final Player pl : Bukkit.getWorld(this.getRound().getWorld()).getPlayers()) {
					pl.hidePlayer(p);
					if (this.getRound().getConfigManager().areSpectatorsInTabList()) {
						NmsUtil.addPlayer(pl, p);
					}
					else {
						NmsUtil.removePlayer(pl, p);
					}
				}

				if (!Main.isVanillaSpectatingDisabled() &&
						this.getRound().getConfigManager().isUsingVanillaSpectating() &&
						NmsUtil.SPECTATOR_SUPPORT) {
					p.setGameMode(org.bukkit.GameMode.SPECTATOR);
					Main.locale.getMessage("info.personal.spectating").sendTo(p.getName(), Color.DARK_PURPLE); // tell them
				}
				else {
					p.setGameMode(org.bukkit.GameMode.ADVENTURE); // disable block breaking
					Main.locale.getMessage("info.personal.spectating").sendTo(p.getName(), Color.DARK_PURPLE); // tell them
					if (Bukkit.getAllowFlight() && getRound().getConfigManager().isSpectatorFlightAllowed()) {
						p.setAllowFlight(true); // enable flight
					}
				}
			}
		}
		else {
			@SuppressWarnings("deprecation")
			Player p = Bukkit.getPlayer(this.getName());
			if (p != null) { // check that player is online
				if (!Main.isVanillaSpectatingDisabled() &&
						this.getRound().getConfigManager().isUsingVanillaSpectating()) {
					p.setGameMode(org.bukkit.GameMode.valueOf(this.getRound().getConfigManager().getDefaultGameMode().name()));
				}
				for (Player pl : NmsUtil.getOnlinePlayers()) {
					pl.showPlayer(p);
				}
				if (getRound() != null) {
					// set them to the default gamemode for arenas
					p.setGameMode(org.bukkit.GameMode.valueOf(getRound().getConfigManager().getDefaultGameMode().name()));
				}
				p.setFlying(false); // disable flight
			}
		}
		Minigame.getMinigameInstance(plugin).getLobbyManager().update(this.getArena());
	}

	/**
	 * Sets the prefix of this player (used on lobby signs).
	 *
	 * @param prefix the new prefix of this player
	 * @since 0.1.0
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Adds this {@link MGPlayer} to the given {@link Round round}.
	 *
	 * @param round the name of the round to add the player to
	 * @return the result of this player being added to the round
	 * @throws PlayerOfflineException if the player is not online
	 * @throws PlayerPresentException if the player is already in a round
	 * @throws RoundFullException     if the round is full
	 * @since 0.1.0
	 */
	public JoinResult addToRound(String round)
			throws PlayerOfflineException, PlayerPresentException, RoundFullException {
		return Minigame.getMinigameInstance(plugin).getRound(round).addPlayer(name);
	}

	/**
	 * Removes this {@link MGPlayer} from the round they are currently in.
	 *
	 * @param location the location to teleport this player to. Please omit it
	 *                 if you wish to teleport them to the round's default exit
	 *                 point.
	 * @throws NoSuchPlayerException  if the given player is not in a round
	 * @throws PlayerOfflineException if the given player is not online
	 * @since 0.4.0
	 */
	public void removeFromRound(Location3D location) throws NoSuchPlayerException, PlayerOfflineException {
		getRound().removePlayer(name, location);
	}

	/**
	 * Removes this {@link MGPlayer} from the round they are currently in.
	 *
	 * @param location the location to teleport this player to. Please omit it
	 *                 if you wish to teleport them to the round's default exit
	 *                 point.
	 * @throws NoSuchPlayerException  if the given player is not in a round
	 * @throws PlayerOfflineException if the given player is not online
	 * @deprecated Use {@link MGPlayer#removeFromRound(Location3D)}
	 * @since 0.1.0
	 */
	@Deprecated
	@SuppressWarnings("deprecation")
	public void removeFromRound(Location location) throws NoSuchPlayerException, PlayerOfflineException {
		getRound().removePlayer(name, location);
	}

	/**
	 * Removes this {@link MGPlayer} from the round they are currently in.
	 *
	 * @throws NoSuchPlayerException  if the player is not in a round
	 * @throws PlayerOfflineException if the player is not online
	 * @since 0.1.0
	 */
	public void removeFromRound() throws NoSuchPlayerException, PlayerOfflineException {
		this.getRound().removePlayer(this.getName());
	}

	/**
	 * Resets the {@link Player Bukkit player} after they've left a round.
	 *
	 * @param location the location to teleport the player to, or null to skip
	 *                 teleportation
	 * @since 0.1.0
	 */
	@SuppressWarnings("deprecation")
	public void reset(Location3D location) {
		final Player p = getBukkitPlayer();
		if (p == null) { // check that the specified player is online
			return;
		}
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		for (PotionEffect pe : p.getActivePotionEffects()) {
			p.removePotionEffect(pe.getType()); // remove any potion effects before sending them back to the lobby
		}
		try {
			final File invF = new File(MGUtil.getPlugin().getDataFolder() + File.separator + "inventories" + File.separator +
					UUIDFetcher.getUUIDOf(p.getName()) + ".dat");
			if (invF.exists()) {
				YamlConfiguration invY = new YamlConfiguration();
				invY.load(invF);
				ItemStack[] invI = new ItemStack[36];
				PlayerInventory pInv = p.getInventory();
				for (String k : invY.getKeys(false)) {
					if (MGUtil.isInteger(k)) {
						invI[Integer.parseInt(k)] = invY.getItemStack(k);
					}
					else if (k.equalsIgnoreCase("h")) {
						pInv.setHelmet(invY.getItemStack(k));
					}
					else if (k.equalsIgnoreCase("c")) {
						pInv.setChestplate(invY.getItemStack(k));
					}
					else if (k.equalsIgnoreCase("l")) {
						pInv.setLeggings(invY.getItemStack(k));
					}
					else if (k.equalsIgnoreCase("b")) {
						pInv.setBoots(invY.getItemStack(k));
					}
				}
				invF.delete();
				p.getInventory().setContents(invI);
				p.updateInventory();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			locale.getMessage("error.personal.inv-load-fail").sendTo(p.getName(), Color.RED);
		}
		if (location != null) {
			// teleport the player
			p.teleport(MGUtil.toBukkitLocation(location), TeleportCause.PLUGIN);
		}
	}

	/**
	 * Resets the {@link Player Bukkit player} after they've left a round.
	 *
	 * @param location the location to teleport the player to, or null to skip
	 *                 teleportation
	 * @deprecated Use {@link MGPlayer#reset(Location3D)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void reset(Location location) {
		reset(MGUtil.fromBukkitLocation(location, true));
	}

	/**
	 * Resets the {@link Player Bukkit player} after they've left a round.
	 *
	 * @throws PlayerOfflineException if the player is offline
	 * @since 0.1.0
	 */
	public void reset() throws PlayerOfflineException {
		reset(MGUtil.fromBukkitLocation(Minigame.getMinigameInstance(plugin).getConfigManager().getDefaultExitLocation(), true));
	}

	/**
	 * Gets the gamemode MGLib will impose upon the player upon round exit.
	 *
	 * <p>This method typically <strong>should not</strong> be called from
	 * outside the library.</p>
	 *
	 * @return the gamemode MGLib will impose upon the player upon round
	 *                 exit.
	 * @since 0.1.0
	 */
	//TODO: deprecate
	public GameMode getPrevGameMode() {
		return prevGameMode;
	}

	/**
	 * Sets the gamemode MGLib will impose upon the player upon round exit.
	 *
	 * <p>This method typically <strong>should not</strong> be called from
	 * outside the library.</p>
	 *
	 * @param gameMode the gamemode MGLib will impose upon the player upon round
	 *                 exit.
	 * @since 0.4.0
	 */
	public void setPrevGameMode(GameMode gameMode) {
		this.prevGameMode = gameMode;
	}

	/**
	 * Sets the gamemode MGLib will impose upon the player upon round exit.
	 *
	 * <p>This method typically <strong>should not</strong> be called from
	 * outside the library.</p>
	 *
	 * @param gameMode the gamemode MGLib will impose upon the player upon round
	 *                 exit.
	 * @deprecated Use {@link MGPlayer#setPrevGameMode(GameMode)}
	 * @since 0.1.0
	 */
	@Deprecated
	public void setPrevGameMode(org.bukkit.GameMode gameMode) {
		setPrevGameMode(GameMode.getGameMode(gameMode.name()));
	}

	/**
	 * Retrieves the {@link Bukkit Player} object for this {@link MGPlayer}.
	 *
	 * @return the {@link Bukkit Player} object for this {@link MGPlayer}
	 * @deprecated Use {@link Bukkit#getPlayer(String)}
	 * @since 0.2.0
	 */
	@Deprecated
	@SuppressWarnings("deprecation")
	public Player getBukkitPlayer() {
		return Bukkit.getPlayer(name);
	}

	/**
	 * Convenience method for {@link MGPlayer#getBukkitPlayer()}. Use this only
	 * if aesthetic ambiguity is not a point of concern.
	 *
	 * @return the {@link Bukkit Player} object for this {@link MGPlayer}
	 * @deprecated Encourages poor coding practice; please use
	 * {@link Bukkit#getPlayer(String)}
	 * @since 0.3.0
	 */
	@Deprecated
	@SuppressWarnings("deprecation")
	public Player b() {
		return getBukkitPlayer();
	}

	/**
	 * Retrieves whether the player is frozen.
	 *
	 * @return whether the player is frozen
	 * @since 0.3.0
	 */
	public boolean isFrozen() {
		return frozen;
	}

	/**
	 * Cleanly freezes or unfreezes the player. The library will automatically
	 * revert the player to their previous speed when unfrozen and let it go,
	 * <em>let it go!</em>
	 *
	 * @param frozen whether the player should be frozen
	 * @since 0.3.0
	 */
	public void setFrozen(boolean frozen) {
		@SuppressWarnings("deprecation")
		Player p = Bukkit.getPlayer(this.getName());
		if (frozen) {
			if (!this.isFrozen()) {
				this.setMetadata("prev-walk-speed", p.getWalkSpeed());
				this.setMetadata("prev-fly-speed", p.getFlySpeed());
				for (PotionEffect pe : p.getActivePotionEffects()) {
					if (pe.getType() == PotionEffectType.JUMP) {
						this.setMetadata("prev-jump-level", pe.getAmplifier());
						this.setMetadata("prev-jump-duration", pe.getDuration());
					}
				}
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
				p.setWalkSpeed(0f);
				p.setFlySpeed(0f);
			}
		}
		else if (this.isFrozen()) {
			p.setWalkSpeed(this.hasMetadata("prev-walk-speed") ? (Float)this.getMetadata("prev-walk-speed") : 0.2f);
			p.setFlySpeed(this.hasMetadata("prev-fly-speed") ? (Float)this.getMetadata("prev-fly-speed") : 0.2f);
			p.removePotionEffect(PotionEffectType.JUMP);
			if (this.hasMetadata("prev-jump-level")) {
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.JUMP,
						(Integer)this.getMetadata("prev-jump-duration"),
						(Integer)this.getMetadata("prev-jump-level")
				));
			}
			this.removeMetadata("prev-walk-speed");
			this.removeMetadata("prev-fly-speed");
			this.removeMetadata("prev-jump-level");
			this.removeMetadata("prev-jump-duration");
		}
		this.frozen = frozen;
	}

	/**
	 * Respawns the player at the given spawn.
	 *
	 * @param spawn the index of the spawn to send the player to
	 * @since 0.3.0
	 */
	public void spawnIn(int spawn) {
		Round r = this.getRound();
		@SuppressWarnings("deprecation")
		Player p = Bukkit.getPlayer(this.getName());
		if (r != null) {
			Location sp = (spawn >= 0 && r.getSpawns().size() > spawn) ?
			              r.getSpawns().get(spawn) :
			              r.getConfigManager().isRandomSpawning() ?
			              r.getSpawns().get(new Random().nextInt(r.getSpawns().size())) :
			              r.getSpawns().get(r.getPlayerList().size() % r.getSpawns().size());
			p.teleport(sp, TeleportCause.PLUGIN); // teleport the player to it
		}
	}

	/**
	 * Respawns the player at a random or sequential spawn, depending on your
	 * configuration.
	 *
	 * @since 0.3.0
	 */
	public void spawnIn() {
		spawnIn(-1);
	}

	public boolean equals(Object p) {
		if (p instanceof MGPlayer) {
			MGPlayer t = (MGPlayer)p;
			return name.equals(t.getName()) && arena.equals(t.getArena()) && isSpectating() == t.isSpectating();
		}
		return false;
	}

	public int hashCode() {
		return 41 * (plugin.hashCode() + name.hashCode() + arena.hashCode() +
				Boolean.valueOf(isSpectating()).hashCode() + 41);
	}

	public Object getMetadata(String key) {
		return metadata.get(key);
	}

	public void setMetadata(String key, Object value) {
		metadata.put(key, value);
	}

	public void removeMetadata(String key) {
		metadata.remove(key);
	}

	public boolean hasMetadata(String key) {
		return metadata.containsKey(key);
	}

	public HashMap<String, Object> getAllMetadata() {
		return metadata;
	}

}
