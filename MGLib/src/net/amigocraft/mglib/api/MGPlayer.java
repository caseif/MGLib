package net.amigocraft.mglib.api;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.Main;
import net.amigocraft.mglib.UUIDFetcher;
import net.amigocraft.mglib.event.player.MGPlayerSpectateEvent;
import net.amigocraft.mglib.event.player.PlayerLeaveMinigameRoundEvent;
import net.amigocraft.mglib.exception.PlayerNotPresentException;
import net.amigocraft.mglib.exception.PlayerOfflineException;

/**
 * Represents a player participating in a minigame.
 * @since 0.1.0
 */
public class MGPlayer {

	private String plugin;
	private String name;
	private String arena;
	private boolean spectating = false;
	private String prefix = "";
	private GameMode prevGameMode;

	/**
	 * Creates a new MGPlayer instance.
	 * @param plugin the plugin to associate the MGPlayer with.
	 * @param name the username of the player.
	 * @param arena the arena of the player (this argument is subject to change).
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
	public void setSpectating(boolean spectating){
		this.spectating = spectating;
		if (spectating){
			Bukkit.getPluginManager().callEvent(new MGPlayerSpectateEvent(this.getRound(), this));
			Player p = Bukkit.getPlayer(name);
			p.closeInventory();
			for (Player pl : Bukkit.getOnlinePlayers())
				pl.hidePlayer(p); //TODO: Set gamemode to 3 (SPECTATOR) if supported
			String message = "You are now spectating! You have been hidden from all players";
			if (Bukkit.getAllowFlight()){
				p.setFlying(true);
				message += " and are capable of flight.";
			}
			else
				message += ".";
			p.sendMessage(message);
		}
		else {
			Player p = Bukkit.getPlayer(name);
			for (Player pl : Bukkit.getOnlinePlayers())
				pl.showPlayer(p);
			p.setFlying(false);
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
	 * @throws PlayerOfflineException if the player is not online.
	 * @since 0.1.0
	 */
	public void addToRound(String round) throws PlayerOfflineException {
		Minigame.getMinigameInstance(plugin).getRound(round).addPlayer(name);
	}

	/**
	 * Removes this {@link MGPlayer} from the round they are currently in.
	 * @param location the location to teleport this player to. Please omit it if you wish to teleport them to the round's default exit point.
	 * @throws PlayerNotPresentException if the given player is not in a round.
	 * @since 0.1.0
	 */
	public void removeFromRound(final Location location) throws PlayerNotPresentException {
		Player p = Bukkit.getPlayer(name);
		for (Round r : Minigame.getMinigameInstance(plugin).getRoundList()) // reuse the old MGPlayer if it exists
			if (r.getPlayers().containsKey(name)){
				setArena(null); // clear the arena from the object
				setSpectating(false); // make sure they're not effectively dead when they join a new round (or invisible)
				r.getPlayers().remove(name); // remove the player from the round object
				p.setGameMode(getPrevGameMode());
				Bukkit.getScheduler().runTask(Main.plugin, new Runnable(){
					public void run(){
						if (Bukkit.getPlayer(name) != null)
							reset(location); // reset the object and send the player to the exit point (and reset the player's inventory
					}
				});

				Bukkit.getPluginManager().callEvent(new PlayerLeaveMinigameRoundEvent(r, this));
				return;
			}
		throw new PlayerNotPresentException();
	}

	/**
	 * Removes this {@link MGPlayer} from the round they are currently in.
	 * @throws PlayerNotPresentException if the player is not in a round.
	 * @since 0.1.0
	 */
	public void removeFromRound() throws PlayerNotPresentException {
		removeFromRound(Minigame.getMinigameInstance(plugin).getConfigManager().getDefaultExitLocation());
	}

	/**
	 * Resets the {@link Player Bukkit player} after they've left a round.
	 * @param location The location to teleport the player to, or null to skip teleportation.
	 * @since 0.1.0
	 */
	@SuppressWarnings("deprecation")
	public void reset(Location location){
		final Player p = Bukkit.getPlayer(name);
		if (p == null) // check that the specified player is online
			return;
		p.getInventory().clear();
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
			p.sendMessage(ChatColor.RED + "Failed to load inventory from disk!");
		}
		if (location != null){
			p.teleport(location, TeleportCause.PLUGIN); // teleport the player
		}
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

	public boolean equals(Object p){
		MGPlayer t = (MGPlayer)p;
		return name.equals(t.getName()) && arena.equals(t.getArena()) && isSpectating() == t.isSpectating();
	}

	public int hashCode(){
		return 41 * (plugin.hashCode() + name.hashCode() + arena.hashCode() + Boolean.valueOf(isSpectating()).hashCode() + 41);
	}

}
