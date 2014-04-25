package net.amigocraft.mglib;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import net.amigocraft.mglib.api.LobbySign;
import net.amigocraft.mglib.api.LobbyType;
import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.exception.ArenaNotExistsException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Lists;

public class LobbyManager {

	private String plugin;

	private HashMap<Location, LobbySign> signs = new HashMap<Location, LobbySign>();

	/**
	 * Creates a new lobby manager instance.
	 * @param plugin The plugin to create the manager for.
	 * @since 0.1
	 */
	public LobbyManager(String plugin){
		this.plugin = plugin;
	}

	/**
	 * Retrieves a hashmap mapping locations to lobby signs registered with this lobby manager.
	 * @return a hashmap of signs registered with this lobby manager.
	 * @since 0.1
	 */
	public HashMap<Location, LobbySign> getSigns(){
		return signs;
	}

	/**
	 * Retrieves a list of lobby signs registered with this lobby manager.
	 * @return a list of lobby signs registered with this lobby manager.
	 * @since 0.1
	 */
	public List<LobbySign> getSignList(){
		return Lists.newArrayList(signs.values());
	}

	/**
	 * Creates a new LobbySign to be managed
	 * @param l The location to create the sign at.
	 * @param arena The name of the arena the sign will be linked to.
	 * @param type The type of the sign ("status" or "players")
	 * @param index The number of the sign (applicable only for "players" signs)
	 * @throws IllegalArgumentException if the specified location does not contain a sign.
	 * @throws ArenaNotExistsException if the specified arena does not exist.
	 * @throws IllegalArgumentException if {@code type} is null.
	 * @throws IllegalArgumentException if the specified index for a player sign is less than 1.
	 * @since 0.1
	 */
	public void add(Location l, String arena, LobbyType type, int index) throws ArenaNotExistsException, IllegalArgumentException {
		if (l.getBlock().getState() instanceof Sign){
			if (MGUtil.loadArenaYaml(plugin).getConfigurationSection(arena) != null){
				LobbySign ls;
				if (type == LobbyType.STATUS)
					ls = new LobbySign(l.getBlockX(), l.getBlockY(), l.getBlockZ(), plugin, l.getWorld().getName(), arena, 0, type);
				else if (type == LobbyType.PLAYERS){
					if (index >= 1)
						ls = new LobbySign(l.getBlockX(), l.getBlockY(), l.getBlockZ(), plugin, l.getWorld().getName(), arena, index, type);
					else
						throw new IllegalArgumentException("Invalid player sign index for arena " + arena);
				}
				else
					throw new IllegalArgumentException("No sign type provided!");
				save(ls);
				signs.put(l, ls);
				update(arena);
			}
			else
				throw new ArenaNotExistsException();
		}
		else
			throw new IllegalArgumentException("Specified location does not contain a sign");
	}

	/**
	 * Updates all lobby signs linked to a specific arena.
	 * @param arena The arena to update signs for.
	 * @since 0.1
	 */
	public void update(String arena){
		for (LobbySign s : signs.values()){
			if (s.getArena().equals(arena)){
				s.update();
			}
		}
	}

	/**
	 * Resets all lobby signs to their default state ({@link Stage#WAITING waiting stage} for {@link LobbyType#STATUS status} signs,
	 * blank for {@link LobbyType#PLAYERS player signs}).
	 * @since 0.1
	 */
	public void reset(){
		for (LobbySign s : signs.values()){
			s.reset();
		}
	}

	/**
	 * Removes the given lobby sign.
	 * @param s the lobby sign to remove.
	 * @since 0.1
	 */
	public void remove(LobbySign s){
		try {
			YamlConfiguration y = new YamlConfiguration();
			File f = new File(Minigame.getMinigameInstance(plugin).getPlugin().getDataFolder(), "lobbies.yml");
			if (!f.exists())
				f.createNewFile();
			y.load(f);
			y.set(Integer.toString(s.getIndex()), null);
			y.save(f);
			signs.remove(s);
		}
		catch (Exception ex){
			ex.printStackTrace();
			Main.log.warning("Failed to unregister lobby sign for plugin " + plugin);
		}
	}

	/**
	 * Saves a lobby sign's data to disk.
	 * @param l the lobby sign to save to disk.
	 * @return the index of the sign in the YAML file used for storage.
	 * @since 0.1
	 */
	public int save(LobbySign l){
		int nextKey = 0;
		try {
			YamlConfiguration y = new YamlConfiguration();
			File f = new File(Minigame.getMinigameInstance(plugin).getPlugin().getDataFolder(), "lobbies.yml");
			if (!f.exists())
				f.createNewFile();
			y.load(f);
			for (String k : y.getKeys(false))
				if (MGUtil.isInteger(k) && Integer.parseInt(k) >= nextKey)
					nextKey = Integer.parseInt(k) + 1;
			String key = Integer.toString(nextKey);
			y.set(key + ".world", l.getWorld());
			y.set(key + ".x", l.getX());
			y.set(key + ".y", l.getY());
			y.set(key + ".z", l.getZ());
			y.set(key + ".arena", l.getArena());
			y.set(key + ".type", l.getType().toString());
			y.set(key + ".number", l.getNumber());
			l.setIndex(nextKey);
			y.save(f);
		}
		catch (Exception ex){
			ex.printStackTrace();
			Main.log.warning("An error occurred while creating a lobby sign for plugin " + plugin);
		}
		return nextKey;
	}

	/**
	 * Retrieves the lobby sign at the specified location, or null if it does not exist.
	 * @param location the location to search for a lobby sign at.
	 * @return the lobby sign at the specified location, or null if it does not exist.
	 * @since 0.1
	 */
	public LobbySign getSign(Location location){
		return signs.get(location);
	}

	/**
	 * Loads lobby signs from disk.
	 * @since 0.1
	 */
	public void loadSigns(){
		YamlConfiguration y = new YamlConfiguration();
		File f = new File(Minigame.getMinigameInstance(plugin).getPlugin().getDataFolder(), "lobbies.yml");
		try {
			if (!f.exists())
				f.createNewFile();
			y.load(f);
			for (String k : y.getKeys(false)){
				World w = Bukkit.getWorld(y.getString(k + ".world"));
				if (w != null){
					if (
							y.get(k + ".arena") != null && 
							y.get(k + ".x") != null && 
							y.get(k + ".y") != null && 
							y.get(k + ".z") != null &&
							y.get(k + ".type") != null &&
							LobbyType.fromString(y.getString(k + ".type").toUpperCase()) != null &&
							(LobbyType.fromString(y.getString(k + ".type").toUpperCase()) == LobbyType.STATUS || y.get(k + ".number") != null)){
						Location l = new Location(w, y.getInt(k + ".x"), y.getInt(k + ".y"), y.getInt(k + ".z"));
						signs.put(l, new LobbySign(l.getBlockX(), l.getBlockY(), l.getBlockZ(), plugin, w.getName(),
								y.getString(k + ".arena"), y.getInt(k + ".number"), LobbyType.fromString(y.getString(k + ".type").toUpperCase())));
					}
					else
						Main.log.warning("Incomplete data for lobby sign with index of " + k + "! Removing from disk...");
				}
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
			Main.log.severe("An exception occurred while loading lobby signs for plugin " + plugin);
		}
	}

}
