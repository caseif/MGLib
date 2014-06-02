package net.amigocraft.mglib.api;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import net.amigocraft.mglib.LobbyManager;
import net.amigocraft.mglib.Main;

public class LobbySign {

	private static DecimalFormat df = new DecimalFormat("##");

	private int x;
	private int y;
	private int z;
	private String plugin;
	private String world;
	private String arena;
	private int number;
	private LobbyType type;
	private int index;

	/**
	 * Creates a new {@link LobbySign} object.
	 * @param x the x-coordinate of the physical sign.
	 * @param y the y-coordinate of the physical sign.
	 * @param z the z-coordinate of the physical sign.
	 * @param plugin the name of the plugin the sign is to be associated with.
	 * @param world the world containing the physical sign.
	 * @param arena the arena the sign should track.
	 * @param number the number of the sign (used for {@link LobbyType#PLAYERS player signs}).
	 * @param type the {@link LobbyType type} of the sign.
	 * @since 0.1.0
	 */
	public LobbySign(int x, int y, int z, String plugin, String world, String arena, int number, LobbyType type){
		this.x = x;
		this.y = y;
		this.z = z;
		this.plugin = plugin;
		this.world = world;
		this.arena = arena;
		this.number = number;
		this.type = type;
	}

	/**
	 * Retrieves the physical x-coordinate of this lobby sign.
	 * @return the physical x-coordinate of this lobby sign.
	 * @since 0.1.0
	 */
	public int getX(){
		return x;
	}

	/**
	 * Sets the physical x-coordinate of this lobby sign.
	 * @param x the physical x-coordinate of this lobby sign.
	 * @since 0.1.0
	 */
	public void setX(int x){
		this.x = x;
	}

	/**
	 * Retrieves the physical y-coordinate of this lobby sign.
	 * @return the physical y-coordinate of this lobby sign.
	 * @since 0.1.0
	 */
	public int getY(){
		return y;
	}

	/**
	 * Sets the physical y-coordinate of this lobby sign.
	 * @param y the physical y-coordinate of this lobby sign.
	 * @since 0.1.0
	 */
	public void setY(int y){
		this.y = y;
	}

	/**
	 * Retrieves the physical z-coordinate of this lobby sign.
	 * @return the physical z-coordinate of this lobby sign.
	 * @since 0.1.0
	 */
	public int getZ(){
		return z;
	}

	/**
	 * Sets the physical z-coordinate of this lobby sign.
	 * @param z the physical z-coordinate of this lobby sign.
	 * @since 0.1.0
	 */
	public void setZ(int z){
		this.z = z;
	}

	/**
	 * Retrieves the plugin this lobby sign is associated with.
	 * @return the plugin this lobby sign is associated with.
	 * @since 0.1.0
	 */
	public String getPlugin(){
		return plugin;
	}

	/**
	 * Retrieves the name of the world containing this physical lobby sign.
	 * @return the name of the world containing this physical lobby sign.
	 * @since 0.1.0
	 */
	public String getWorld(){
		return world;
	}

	/**
	 * Sets the name of the world containing this physical lobby sign.
	 * @param world the name of the world containing this physical lobby sign.
	 * @since 0.1.0
	 */
	public void setWorld(String world){
		this.world = world;
	}

	/**
	 * Retrieves the arena associated with this lobby sign.
	 * @return the arena associated with this lobby sign.
	 * @since 0.1.0
	 */
	public String getArena(){
		return arena;
	}

	/**
	 * Sets the arena associated with this lobby sign.
	 * @param arena the arena associated with this lobby sign.
	 * @since 0.1.0
	 */
	public void setArena(String arena){
		this.arena = arena;
	}

	/**
	 * Retrieves the number of this lobby sign.
	 * @return the number of this lobby sign.
	 * @since 0.1.0
	 */
	public int getNumber(){
		return number;
	}

	/**
	 * Sets the number of this lobby sign.
	 * @param number the number of this lobby sign.
	 * @since 0.1.0
	 */
	public void setNumber(int number){
		this.number = number;
	}

	/**
	 * Retrieves the {@link LobbyType type} of this lobby sign.
	 * @return the {@link LobbyType type} of this lobby sign.
	 * @since 0.1.0
	 */
	public LobbyType getType(){
		return type;
	}

	/**
	 * Sets the {@link LobbyType type} of this lobby sign.
	 * @param type the {@link LobbyType type} of this lobby sign.
	 * @since 0.1.0
	 */
	public void setType(LobbyType type){
		this.type = type;
	}

	/**
	 * Retrieves the internal index of this lobby sign.
	 * @return the internal index of this lobby sign.
	 * @since 0.1.0
	 */
	public int getIndex(){
		return index;
	}

	/**
	 * Sets the internal index of this lobby sign.
	 * @param index the internal index of this lobby sign.
	 * @since 0.1.0
	 */
	public void setIndex(int index){
		this.index = index;
	}

	/**
	 * Saves this lobby sign's data to disk.
	 * @since 0.1.0
	 */
	public void save(){
		Minigame.getMinigameInstance(plugin).getLobbyManager().save(this);
	}

	/**
	 * Saves this lobby sign's data to disk and removes it from memory.
	 * @since 0.1.0
	 */
	public void remove(){
		LobbyManager lm = Minigame.getMinigameInstance(plugin).getLobbyManager();
		lm.save(this);
		lm.remove(this);
	}

	/**
	 * Updates this lobby sign's text based on its arena's current status.
	 * @since 0.1.0
	 */
	public void update(){
		Round r = Minigame.getMinigameInstance(plugin).getRound(arena);
		if (r != null){
			World w = Bukkit.getWorld(this.getWorld());
			if (w != null){
				Block b = w.getBlockAt(this.getX(), this.getY(), this.getZ());
				if (b != null){
					if (b.getState() instanceof Sign){
						final Sign sign = (Sign)b.getState();
						ConfigManager cm = Minigame.getMinigameInstance(plugin).getConfigManager();
						if (this.getType() == LobbyType.STATUS){
							sign.setLine(0, cm.getLobbyArenaColor() + this.getArena());
							String max = Minigame.getMinigameInstance(plugin).getConfigManager().getMaxPlayers() + "";
							if (Minigame.getMinigameInstance(plugin).getConfigManager().getMaxPlayers() <= 0)
								max = "∞";
							String playerCount = r != null ? (r.getPlayers().size() + "/" + max) : (playerCount = "0/" + max);
							if (!max.equals("∞")){
								if (r != null && r.getPlayers().size() >= Minigame.getMinigameInstance(plugin).getConfigManager().getMaxPlayers())
									playerCount = cm.getLobbyPlayerCountFullColor() + playerCount;
								else
									playerCount = cm.getLobbyPlayerCountColor() + playerCount;
							}
							else
								playerCount = cm.getLobbyPlayerCountColor() + playerCount;
							sign.setLine(1, playerCount);
							Stage status = r == null ? Stage.WAITING : r.getStage();
							ChatColor color = null;
							switch (status){
							case WAITING:
								color = cm.getLobbyWaitingColor();
							case PREPARING:
								color = cm.getLobbyPreparingColor();
							case PLAYING:
								color = cm.getLobbyPlayingColor();
							case RESETTING:
								color = cm.getLobbyResettingColor();
							}
							sign.setLine(2, color + status.toString());
							String time = "";
							if (status != Stage.WAITING && status != Stage.RESETTING){
								if (r.getRemainingTime() == -1)
									time = cm.getLobbyTimeInfiniteColor() + "∞:∞";
								else {
									String seconds = Integer.toString(r.getRemainingTime() % 60);
									if (seconds.length() == 1)
										seconds = "0" + seconds;
									time = df.format(r.getRemainingTime() / 60) + ":" + seconds;
									if (r.getRemainingTime() <= 60)
										time = cm.getLobbyTimeWarningColor() + time;
									else
										time = cm.getLobbyTimeColor() + time;
								}
							}
							sign.setLine(3, time);
						}
						else if (this.getType() == LobbyType.PLAYERS && this.getNumber() > 0){
							for (int i = 0; i <= 3; i++){
								if (r != null){
									List<MGPlayer> players = Minigame.getMinigameInstance(plugin).getConfigManager().areSpectatorsOnLobbySigns() ?
											r.getPlayerList() : r.getAlivePlayerList();
									if (players.size() >= (this.getNumber() - 1) * 4 + i + 1){
										MGPlayer p = players.get((this.getNumber() - 1) * 4 + i);
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
						remove();
				}
			}
		}
		else
			reset();
	}

	/**
	 * Resets this lobby sign's text as if its arena was empty and in {@link Stage#WAITING}.
	 * @since 0.1.0
	 */
	public void reset(){
		World w = Bukkit.getWorld(this.getWorld());
		if (w != null){
			Block b = w.getBlockAt(this.getX(), this.getY(), this.getZ());
			if (b != null){
				if (b.getState() instanceof Sign){
					final Sign sign = (Sign)b.getState();
					ConfigManager cm = Minigame.getMinigameInstance(plugin).getConfigManager();
					if (this.getType() == LobbyType.STATUS){
						sign.setLine(0, cm.getLobbyArenaColor() + this.getArena());
						String max = Minigame.getMinigameInstance(plugin).getConfigManager().getMaxPlayers() + "";
						if (Minigame.getMinigameInstance(plugin).getConfigManager().getMaxPlayers() <= 0)
							max = "∞";
						sign.setLine(1, cm.getLobbyPlayerCountColor() + "0/" + max);
						sign.setLine(2, cm.getLobbyWaitingColor() + "WAITING");
						sign.setLine(3, "");
					}
					else if (this.getType() == LobbyType.PLAYERS && this.getNumber() > 0){
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
					remove();
			}
		}
	}

}
