package net.amigocraft.mglib.api;

import static net.amigocraft.mglib.Main.locale;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

/**
 * Represents a player participating in a minigame.
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
	 * @param plugin the plugin to associate the MGPlayer with.
	 * @param name the username of the player.
	 * @param arena the arena of the player.
	 * @since 0.1.0
	 */
	public MGPlayer(String plugin, String name, String arena){
		this.plugin = plugin;
		this.name = name;
		this.arena = arena;
	}

	/**
	 * Gets the minigame plugin associated with this {@link MGPlayer}.
	 * @return the minigame plugin associated with this {@link MGPlayer}.
	 * @since 0.1.0
	 */
	public String getPlugin(){
		return plugin;
	}

	/**
	 * Gets the MGLib API instance registered by the minigame plugin associated with this {@link MGPlayer}.
	 * @return the MGLib API instance registered by the minigame plugin associated with this {@link MGPlayer}.
	 * @since 0.1.0
	 */
	public Minigame getMinigame(){
		return Minigame.getMinigameInstance(plugin);
	}

	/**
	 * Gets the username of this {@link MGPlayer}.
	 * @return the username of this {@link MGPlayer}.
	 * @since 0.1.0
	 */
	public String getName(){
		return name;
	}

	/**
	 * Gets the arena associated with this {@link MGPlayer}.
	 * @return the arena associated with this {@link MGPlayer}.
	 * @since 0.1.0
	 */
	public String getArena(){
		return arena;
	}

	/**
	 * Retrieves the prefix of this player (used on lobby signs).
	 * @return the prefix of this player.
	 * @since 0.1.0
	 */
	public String getPrefix(){
		return prefix;
	}

	/**
	 * Retrieves the name of the team this player is on, or null if they are not on a team.
	 * @return the name of the team this player is on, or null if they are not on a team.
	 * @since 0.3.0
	 */
	public String getTeam(){
		return team;
	}

	/**
	 * Sets the name of the team this player is on.
	 * @param team the name of the team this player is on. Set to null for no team.
	 * @since 0.3.0
	 */
	public void setTeam(String team){
		this.team = team;
	}

	/**
	 * Sets the arena of this {@link MGPlayer}. Please do not call this method unless you understand the implications of doing so.
	 * @param arena the new arena of this {@link MGPlayer}.
	 * @since 0.1.0
	 */
	public void setArena(String arena){
		this.arena = arena;
	}

	/**
	 * Gets whether this player is spectating their round, as opposed to participating in it.
	 * @return whether this player is spectating their round (can return true even if {@link Player#isDead()} returns
	 * false).
	 * @since 0.1.0
	 */
	public boolean isSpectating(){
		return spectating;
	}

	/**
	 * Gets the {@link Round} associated with this player.
	 * @return the {@link Round} associated with this player.
	 * @since 0.1.0
	 */
	public Round getRound(){
		return Minigame.getMinigameInstance(plugin).getRound(arena);
	}

	/**
	 * Sets whether this player is spectating or not.
	 * @param spectating whether the player is spectating.
	 * @since 0.1.0
	 */
	@SuppressWarnings("unchecked")
	public void setSpectating(boolean spectating){
		this.spectating = spectating;
		if (spectating){
			MGUtil.callEvent(new MGPlayerSpectateEvent(this.getRound(), this));
			Player p = getBukkitPlayer();
			if (p != null){ // check that player is online
				p.closeInventory(); // close any inventory they have open
				try {
					if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class)
						for (Player pl :
							(Collection<? extends Player>)Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]))
							pl.hidePlayer(p);
					else
						for (Player pl :
							(Player[])Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]))
							pl.hidePlayer(p);
				}
				catch (NoSuchMethodException ex){} // can never happen
				catch (InvocationTargetException ex){} // can also never happen
				catch (IllegalAccessException ex){} // can still never happen
				//TODO: Set gamemode to SPECTATOR if supported (after 1.8 comes out)
				p.setGameMode(GameMode.ADVENTURE); // disable block breaking
				String message = ChatColor.DARK_PURPLE + Main.locale.getMessage("spectating"); // tell them
				if (Bukkit.getAllowFlight() && getRound().getConfigManager().isSpectatorFlightAllowed()){
					p.setAllowFlight(true); // enable flight
					message += " " + locale.getMessage("flight-enabled");
				}
				p.sendMessage(message);
			}
		}
		else {
			Player p = getBukkitPlayer();
			if (p != null){ // check that player is online
				try {
					if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class)
						for (Player pl :
							(Collection<? extends Player>)Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]))
							pl.showPlayer(p);
					else
						for (Player pl :
							(Player[])Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]))
							pl.showPlayer(p);
				}
				catch (NoSuchMethodException ex){} // can never happen
				catch (InvocationTargetException ex){} // can also never happen
				catch (IllegalAccessException ex){} // can still never happen
				if (getRound() != null)
					p.setGameMode(getRound().getConfigManager().getDefaultGameMode()); // set them to the default gamemode for arenas
				p.setFlying(false); // disable flight
			}
		}
		Minigame.getMinigameInstance(plugin).getLobbyManager().update(this.getArena());
	}

	/**
	 * Sets the prefix of this player (used on lobby signs).
	 * @param prefix the new prefix of this player.
	 * @since 0.1.0
	 */
	public void setPrefix(String prefix){
		this.prefix = prefix;
	}

	/**
	 * Adds this {@link MGPlayer} to the given {@link Round round}.
	 * @param round The name of the round to add the player to.
	 * @return the result of this player being added to the round.
	 * @throws PlayerOfflineException if the player is not online.
	 * @throws PlayerPresentException if the player is already in a round.
	 * @throws RoundFullException if the round is full.
	 * @since 0.1.0
	 */
	public JoinResult addToRound(String round) throws PlayerOfflineException, PlayerPresentException, RoundFullException {
		return Minigame.getMinigameInstance(plugin).getRound(round).addPlayer(name);
	}

	/**
	 * Removes this {@link MGPlayer} from the round they are currently in.
	 * @param location the location to teleport this player to. Please omit it if you wish to teleport them to the round's default exit point.
	 * @throws NoSuchPlayerException if the given player is not in a round.
	 * @throws PlayerOfflineException if the given player is not online.
	 * @since 0.1.0
	 */
	public void removeFromRound(Location location) throws NoSuchPlayerException, PlayerOfflineException {
		getRound().removePlayer(name, location);
	}

	/**
	 * Removes this {@link MGPlayer} from the round they are currently in.
	 * @throws NoSuchPlayerException if the player is not in a round.
	 * @throws PlayerOfflineException if the player is not online.
	 * @since 0.1.0
	 */
	public void removeFromRound() throws NoSuchPlayerException, PlayerOfflineException {
		removeFromRound(Minigame.getMinigameInstance(plugin).getConfigManager().getDefaultExitLocation());
	}

	/**
	 * Resets the {@link Player Bukkit player} after they've left a round.
	 * @param location The location to teleport the player to, or null to skip teleportation.
	 * @since 0.1.0
	 */
	@SuppressWarnings("deprecation")
	public void reset(Location location){
		final Player p = getBukkitPlayer();
		if (p == null) // check that the specified player is online
			return;
		// uncommenting this causes the method to stop dead in its tracks
		/*for (String k : this.getAllMetadata().keySet()){
			this.removeMetadata(k);
		}*/
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		for (PotionEffect pe : p.getActivePotionEffects())
			p.removePotionEffect(pe.getType()); // remove any potion effects before sending them back to the lobby
		try {
			final File invF = new File(Main.plugin.getDataFolder() + File.separator + "inventories"
					+ File.separator + UUIDFetcher.getUUIDOf(p.getName()) + ".dat");
			if (invF.exists()){
				YamlConfiguration invY = new YamlConfiguration();
				invY.load(invF);
				ItemStack[] invI = new ItemStack[36];
				PlayerInventory pInv = (PlayerInventory)p.getInventory();
				for (String k : invY.getKeys(false)){
					if (MGUtil.isInteger(k))
						invI[Integer.parseInt(k)] = invY.getItemStack(k);
					else if (k.equalsIgnoreCase("h"))
						pInv.setHelmet(invY.getItemStack(k));
					else if (k.equalsIgnoreCase("c"))
						pInv.setChestplate(invY.getItemStack(k));
					else if (k.equalsIgnoreCase("l"))
						pInv.setLeggings(invY.getItemStack(k));
					else if (k.equalsIgnoreCase("b"))
						pInv.setBoots(invY.getItemStack(k));
				}
				invF.delete();
				p.getInventory().setContents(invI);
				p.updateInventory();
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
			p.sendMessage(ChatColor.RED + locale.getMessage("inv-load-fail"));
		}
		if (location != null)
			p.teleport(location, TeleportCause.PLUGIN); // teleport the player
	}

	/**
	 * Resets the {@link Player Bukkit player} after they've left a round.
	 * @throws PlayerOfflineException if the player is not online.
	 * @since 0.1.0
	 */
	public void reset() throws PlayerOfflineException {
		reset(Minigame.getMinigameInstance(plugin).getConfigManager().getDefaultExitLocation());
	}

	/**
	 * You probably shouldn't use this unless you know what it does.
	 * @return the player's previous gamemode.
	 * @since 0.1.0
	 */
	public GameMode getPrevGameMode(){
		return prevGameMode;
	}

	/**
	 * You probably shouldn't use this unless you know what it does.
	 * @param gameMode the player's previous gamemode.
	 * @since 0.1.0
	 */
	public void setPrevGameMode(GameMode gameMode){
		this.prevGameMode = gameMode;
	}

	/**
	 * Retrieves the {@link Bukkit Player} object for this {@link MGPlayer}.
	 * @return the {@link Bukkit Player} object for this {@link MGPlayer}.
	 * @since 0.2.0
	 */
	@SuppressWarnings("deprecation")
	public Player getBukkitPlayer(){
		return Bukkit.getPlayer(name);
	}
	
	/**
	 * Convenience method for {@link MGPlayer#getBukkitPlayer()}. Use this only if aesthetic ambiguity is not a point of concern.
	 * @return the {@link Bukkit Player} object for this {@link MGPlayer}.
	 * @since 0.3.0
	 */
	public Player b(){
		return getBukkitPlayer();
	}

	/**
	 * Retrieves whether the player is frozen.
	 * @return whether the player is frozen.
	 * @since 0.3.0
	 */
	public boolean isFrozen(){
		return frozen;
	}

	/**
	 * Cleanly freezes or unfreezes the player.
	 * The library will automatically revert the player to their previous speed when unfrozen so as to let them go, <i>let them go!</i>
	 * @param frozen whether the player should be frozen.
	 * @since 0.3.0
	 */
	public void setFrozen(boolean frozen){
		if (frozen){
			if (!this.isFrozen()){
				this.setMetadata("prev-walk-speed", this.getBukkitPlayer().getWalkSpeed());
				this.setMetadata("prev-fly-speed", this.getBukkitPlayer().getFlySpeed());
				for (PotionEffect pe : getBukkitPlayer().getActivePotionEffects())
					if (pe.getType() == PotionEffectType.JUMP){
						this.setMetadata("prev-jump-level", pe.getAmplifier());
						this.setMetadata("prev-jump-duration", pe.getDuration());
					}
				this.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
				this.getBukkitPlayer().setWalkSpeed(0f);
				this.getBukkitPlayer().setFlySpeed(0f);
			}
		}
		else if (this.isFrozen()){
			this.getBukkitPlayer().setWalkSpeed(this.hasMetadata("prev-walk-speed") ? (Float)this.getMetadata("prev-walk-speed") : 0.2f);
			this.getBukkitPlayer().setFlySpeed(this.hasMetadata("prev-fly-speed") ? (Float)this.getMetadata("prev-fly-speed") : 0.2f);
			this.getBukkitPlayer().removePotionEffect(PotionEffectType.JUMP);
			if (this.hasMetadata("prev-jump-level")){
				this.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP,
						(Integer)this.getMetadata("prev-jump-duration"), (Integer)this.getMetadata("prev-jump-level")));
			}
			this.removeMetadata("prev-walk-speed");
			this.removeMetadata("prev-fly-speed");
			this.removeMetadata("prev-jump-level");
			this.removeMetadata("prev-jump-duration");
		}
		this.frozen = frozen;
	}

	public boolean equals(Object p){
		MGPlayer t = (MGPlayer)p;
		return name.equals(t.getName()) && arena.equals(t.getArena()) && isSpectating() == t.isSpectating();
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + name.hashCode() + arena.hashCode() + Boolean.valueOf(isSpectating()).hashCode() + 41);
	}

	public Object getMetadata(String key){
		return metadata.get(key);
	}

	public void setMetadata(String key, Object value){
		metadata.put(key, value);
	}

	public void removeMetadata(String key){
		metadata.remove(key);
	}

	public boolean hasMetadata(String key){
		return metadata.containsKey(key);
	}

	public HashMap<String, Object> getAllMetadata(){
		return metadata;
	}

}
