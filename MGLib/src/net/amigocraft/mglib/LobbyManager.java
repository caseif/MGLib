package net.amigocraft.mglib;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;

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

public class LobbyManager {

	private static DecimalFormat df = new DecimalFormat("##");

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
	 * Creates a new LobbySign to be managed
	 * @param l The location to create the sign at.
	 * @param arena The name of the arena the sign will be linked to.
	 * @param type The type of the sign ("status" or "players")
	 * @param number The number of the sign (applicable only for "players" signs)
	 * @throws ArenaNotExistsException  if the specified arena does not exist.
	 * @throws IllegalArgumentException if the specified location does not contain a sign.
	 * @throws IllegalArgumentException if the specified sign type is not valid.
	 * @throws IllegalArgumentException if the specified index for a player sign is less than 1.
	 * @since 0.1
	 */
	public void addSign(Location l, String arena, String type, int number) throws ArenaNotExistsException {
		if (l.getBlock().getState() instanceof Sign){
			if (MGUtil.loadArenaYaml(plugin).getConfigurationSection(arena) != null){
				LobbySign ls;
				if (type.equalsIgnoreCase("status"))
					ls = new LobbySign(l.getBlockX(), l.getBlockY(), l.getBlockZ(), plugin, l.getWorld().getName(), arena, 0, type.toLowerCase());
				else if (type.equalsIgnoreCase("players")){
					if (number >= 1)
						ls = new LobbySign(l.getBlockX(), l.getBlockY(), l.getBlockZ(), plugin, l.getWorld().getName(), arena, 0, type.toLowerCase());
					else
						throw new IllegalArgumentException("Invalid player sign index for arena " + arena);
				}
				else
					throw new IllegalArgumentException("Invalid sign type for arena " + arena);
				saveSign(ls);
				signs.put(l, ls);
				updateSigns(arena);
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
	public void updateSigns(String arena){
		Round r = Minigame.getMinigameInstance(plugin).getRound(arena);
		for (LobbySign s : signs.values()){
			if (s.getArena().equals(arena)){
				World w = Bukkit.getWorld(s.getWorld());
				if (w != null){
					Block b = w.getBlockAt(s.getX(), s.getY(), s.getZ());
					if (b != null){
						if (b.getState() instanceof Sign){
							final Sign sign = (Sign)b.getState();
							if (s.getType().equals("status")){
								sign.setLine(0, "§4" + s.getArena());
								String max = Minigame.getMinigameInstance(plugin).getMaxPlayers() + "";
								if (Minigame.getMinigameInstance(plugin).getMaxPlayers() <= 0)
									max = "∞";
								String playerCount = r != null ? (r.getPlayers().size() + "/" + max) : (playerCount = "0/" + max);
								if (!max.equals("∞")){
									if (r != null && r.getPlayers().size() >= Minigame.getMinigameInstance(plugin).getMaxPlayers())
										playerCount = "§c" + playerCount;
									else
										playerCount = "§a" + playerCount;
								}
								else
									playerCount = "§6" + playerCount;
								sign.setLine(1, playerCount);
								Stage status = r == null ? Stage.WAITING : r.getStage();
								String color = null;
								if (status.equals("PLAYING"))
									color = "§c";
								else if (status == Stage.WAITING || status == Stage.RESETTING)
									color = "§7";
								else if (status.equals("PREPARING"))
									color = "§a";
								sign.setLine(2, color + status.toString());
								String time = "";
								if (status != Stage.WAITING && status != Stage.RESETTING){
									String seconds = Integer.toString(r.getTime() % 60);
									if (seconds.length() == 1)
										seconds = "0" + seconds;
									time = df.format(r.getTime() / 60) + ":" + seconds;
									if (r.getTime() <= 60)
										time = "§c" + time;
									else if (r.getPlayingTime() <= 0)
										time = "§a" + "∞:∞";
									else
										time = "§a" + time;
								}
								sign.setLine(3, time);
							}
							else if (s.getType().equals("players") && s.getNumber() > 0){
								for (int i = 0; i <= 3; i++){
									if (r != null){
										if (r.getPlayers().size() >= (s.getNumber() - 1) * 4 + i + 1){
											MGPlayer p = r.getPlayerList().get((s.getNumber() - 1) * 4 + i);
											String name = p.getPrefix() + p.getName();
											if (name.length() > 16)
												name = name.substring(0, 16);
											sign.setLine(i, name);
										}
										else
											sign.setLine(i, "");
									}
									else
										sign.setLine(i, "");
								}
							}
							if (Main.plugin.isEnabled())
								Bukkit.getScheduler().runTask(Main.plugin, new Runnable(){
									public void run(){
										sign.update();
									}
								});
						}
						else
							removeSign(s);
					}
				}
			}
		}
	}

	public void resetSigns(){
		for (LobbySign s : signs.values()){
			World w = Bukkit.getWorld(s.getWorld());
			if (w != null){
				Block b = w.getBlockAt(s.getX(), s.getY(), s.getZ());
				if (b != null){
					if (b.getState() instanceof Sign){
						final Sign sign = (Sign)b.getState();
						if (s.getType().equals("status")){
							sign.setLine(0, "§4" + s.getArena());
							String max = Minigame.getMinigameInstance(plugin).getMaxPlayers() + "";
							if (max.equals("-1"))
								max = "∞";
							String playerCount = "0/" + max;
							if (!max.equals("∞") && 0 >= Integer.parseInt(max))
								playerCount = "§c" + playerCount;
							else
								playerCount = "§a" + playerCount;
							sign.setLine(1, playerCount);
							String status = "§7" + "WAITING";
							sign.setLine(2, status);
							sign.setLine(3, "");								
						}
						else if (s.getType().equals("players") && s.getNumber() > 0){
							for (int i = 0; i <= 3; i++){
								sign.setLine(i, "");
							}
						}
						Bukkit.getScheduler().runTask(Main.plugin, new Runnable(){
							public void run(){
								sign.update();
							}
						});
					}
					else
						removeSign(s);
				}
			}
		}
	}

	public void removeSign(LobbySign s){
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

	public int saveSign(LobbySign l){
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
			y.set(key + ".type", l.getType());
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
					Location l = new Location(w, y.getInt(k + ".x"), y.getInt(k + ".y"), y.getInt(k + ".z"));
					signs.put(l, new LobbySign(l.getBlockX(), l.getBlockY(), l.getBlockZ(), plugin, w.getName(),
							y.getString(k + ".arena"), y.getInt(k + ".number"), y.getString(k + ".type")));
				}
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
			Main.log.severe("An exception occurred while loading lobby signs for plugin " + plugin);
		}
	}

}
