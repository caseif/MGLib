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
package net.amigocraft.mglib;

import com.google.common.collect.Lists;
import net.amigocraft.mglib.api.*;
import net.amigocraft.mglib.exception.InvalidLocationException;
import net.amigocraft.mglib.exception.NoSuchArenaException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class LobbyManager {

	private String plugin;

	HashMap<Location3D, LobbySign> signs = new HashMap<Location3D, LobbySign>();

	/**
	 * Creates a new lobby manager instance.
	 *
	 * @param plugin The plugin to create the manager for.
	 * @since 0.1.0
	 */
	public LobbyManager(String plugin) {
		this.plugin = plugin;
	}

	/**
	 * Retrieves a hashmap mapping locations to lobby signs registered with this lobby manager.
	 *
	 * @return a hashmap of signs registered with this lobby manager
	 * @since 0.1.0
	 */
	public HashMap<Location3D, LobbySign> getSigns() {
		return signs;
	}

	/**
	 * Retrieves a list of lobby signs registered with this lobby manager.
	 *
	 * @return a list of lobby signs registered with this lobby manager
	 * @since 0.1.0
	 */
	public List<LobbySign> getSignList() {
		return Lists.newArrayList(signs.values());
	}

	/**
	 * Creates a new LobbySign to be managed
	 *
	 * @param l     the location to create the sign at
	 * @param arena the name of the arena the sign will be linked to
	 * @param type  the type of the sign ("status" or "players")
	 * @param index the number of the sign (applicable only for "players" signs)
	 * @throws NoSuchArenaException     if the specified arena does not exist
	 * @throws InvalidLocationException if the specified location does not contain a sign
	 * @throws IllegalArgumentException if the specified index for a player sign is less than 1
	 * @since 0.1.0
	 */
	public void add(Location l, String arena, LobbyType type, int index)
			throws NoSuchArenaException, InvalidLocationException, IllegalArgumentException {
		if (l.getBlock().getState() instanceof Sign) {
			ConfigurationSection cs = MGUtil.loadArenaYaml(plugin).getConfigurationSection(arena);
			if (cs != null) {
				LobbySign ls;
				if (type == LobbyType.STATUS) {
					ls = new LobbySign(
							l.getBlockX(), l.getBlockY(), l.getBlockZ(), plugin, l.getWorld().getName(),
							cs.getString("displayname"), 0, type
					);
				}
				else if (type == LobbyType.PLAYERS) {
					if (index >= 1) {
						ls = new LobbySign(
								l.getBlockX(), l.getBlockY(), l.getBlockZ(), plugin, l.getWorld().getName(),
								cs.getString("displayname"), index, type
						);
					}
					else {
						throw new IllegalArgumentException(
								Main.locale.getMessage("lobby.alert.invalid-index.passive", arena)
						);
					}
				}
				else {
					throw new NullPointerException();
				}
				save(ls);
				signs.put(Location3D.valueOf(l), ls);
				update(arena);
			}
			else {
				throw new NoSuchArenaException();
			}
		}
		else {
			throw new InvalidLocationException();
		}
	}

	/**
	 * Updates all lobby signs linked to a specific arena.
	 *
	 * @param arena the arena to update signs for
	 * @since 0.1.0
	 */
	public void update(String arena) {
		for (LobbySign s : signs.values()) {
			if (s.getArena().equalsIgnoreCase(arena)) {
				s.update();
			}
		}
	}

	/**
	 * Resets all lobby signs to their default state ({@link Stage#WAITING waiting stage} for {@link LobbyType#STATUS
	 * status} signs, blank for {@link LobbyType#PLAYERS player signs}).
	 *
	 * @since 0.1.0
	 */
	public void reset() {
		for (LobbySign s : signs.values()) {
			s.reset();
		}
	}

	/**
	 * Removes the given lobby sign.
	 *
	 * @param s the lobby sign to remove
	 * @since 0.1.0
	 */
	public void remove(LobbySign s) {
		try {
			YamlConfiguration y = new YamlConfiguration();
			File f = new File(Minigame.getMinigameInstance(plugin).getPlugin().getDataFolder(), "lobbies.yml");
			if (!f.exists()) {
				f.createNewFile();
			}
			y.load(f);
			Location3D l = new Location3D(
					y.getString(s.getIndex() + ".world"), y.getInt(s.getIndex() + ".x"), y.getInt(s.getIndex() + ".y"),
					y.getInt(s.getIndex() + ".z")
			);
			y.set(Integer.toString(s.getIndex()), null);
			y.save(f);
			signs.remove(l);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Main.log.warning("Failed to unregister lobby sign for plugin " + plugin);
		}
	}

	/**
	 * Saves a lobby sign's data to disk.
	 *
	 * @param l the lobby sign to save to disk
	 * @return the index of the sign in the YAML file used for storage
	 * @since 0.1.0
	 */
	public int save(LobbySign l) {
		int key = l.getIndex();
		try {
			YamlConfiguration y = new YamlConfiguration();
			File f = new File(Minigame.getMinigameInstance(plugin).getPlugin().getDataFolder(), "lobbies.yml");
			if (!f.exists()) {
				f.createNewFile();
			}
			y.load(f);
			if (key == -1) {
				for (String k : y.getKeys(false)) {
					if (MGUtil.isInteger(k) && Integer.parseInt(k) >= key) {
						key = Integer.parseInt(k) + 1;
					}
				}
			}
			y.set(key + ".world", l.getWorld());
			y.set(key + ".x", l.getX());
			y.set(key + ".y", l.getY());
			y.set(key + ".z", l.getZ());
			y.set(key + ".arena", l.getArena());
			y.set(key + ".type", l.getType().toString());
			y.set(key + ".number", l.getNumber());
			l.setIndex(key);
			y.save(f);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Main.log.warning(Main.locale.getMessage("lobby.alert.create", plugin));
		}
		return key;
	}

	/**
	 * Retrieves the lobby sign at the specified location, or null if it does not exist.
	 *
	 * @param location the location to search for a lobby sign at
	 * @return the lobby sign at the specified location, or null if it does not exist
	 * @since 0.1.0
	 */
	public LobbySign getSign(Location3D location) {
		return signs.get(location);
	}

	/**
	 * Loads lobby signs from disk.
	 *
	 * @since 0.1.0
	 */
	public void loadSigns() {
		YamlConfiguration y = new YamlConfiguration();
		File f = new File(Minigame.getMinigameInstance(plugin).getPlugin().getDataFolder(), "lobbies.yml");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			y.load(f);
			for (String k : y.getKeys(false)) {
				World w = Bukkit.getWorld(y.getString(k + ".world"));
				if (w != null) {
					if (y.get(k + ".arena") != null &&
							y.get(k + ".x") != null &&
							y.get(k + ".y") != null &&
							y.get(k + ".z") != null &&
							y.get(k + ".type") != null &&
							LobbyType.fromString(y.getString(k + ".type").toUpperCase()) != null &&
							(LobbyType.fromString(y.getString(k + ".type").toUpperCase()) == LobbyType.STATUS ||
									y.get(k + ".number") != null)) {
						Location l = new Location(w, y.getInt(k + ".x"), y.getInt(k + ".y"), y.getInt(k + ".z"));
						LobbySign ls = new LobbySign(
								l.getBlockX(), l.getBlockY(), l.getBlockZ(), plugin, w.getName(),
								y.getString(k + ".arena"), y.getInt(k + ".number"),
								LobbyType.fromString(y.getString(k + ".type").toUpperCase()));
						ls.setIndex(Integer.parseInt(k));
						signs.put(Location3D.valueOf(l), ls);
					}
					else {
						Main.log.warning(Main.locale.getMessage("lobby.alert.incomplete", plugin, k));
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Main.log.warning(Main.locale.getMessage("lobby.alert.load", plugin));
		}
	}

}
