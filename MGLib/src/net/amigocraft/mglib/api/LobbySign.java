package net.amigocraft.mglib.api;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

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

	//TODO: Document all this. I *really* don't feel like doing that right now.
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

	public int getX(){
		return x;
	}

	public void setX(int x){
		this.x = x;
	}

	public int getY(){
		return y;
	}

	public void setY(int y){
		this.y = y;
	}

	public int getZ(){
		return z;
	}

	public void setZ(int z){
		this.z = z;
	}

	public String getPlugin(){
		return plugin;
	}

	public String getWorld(){
		return world;
	}

	public void setWorld(String world){
		this.world = world;
	}

	public String getArena(){
		return arena;
	}

	public void setArena(String arena){
		this.arena = arena;
	}

	public int getNumber(){
		return number;
	}

	public void setNumber(int number){
		this.number = number;
	}

	public LobbyType getType(){
		return type;
	}

	public void setType(LobbyType type){
		this.type = type;
	}

	public int getIndex(){
		return index;
	}

	public void setIndex(int index){
		this.index = index;
	}

	public void save(){
		Minigame.getMinigameInstance(plugin).getLobbyManager().save(this);
	}

	public void remove(){
		Minigame.getMinigameInstance(plugin).getLobbyManager().save(this);
	}

	public void update(){
		Round r = Minigame.getMinigameInstance(plugin).getRound(arena);
		if (r != null){
			World w = Bukkit.getWorld(this.getWorld());
			if (w != null){
				Block b = w.getBlockAt(this.getX(), this.getY(), this.getZ());
				if (b != null){
					if (b.getState() instanceof Sign){
						final Sign sign = (Sign)b.getState();
						if (this.getType() == LobbyType.STATUS){
							sign.setLine(0, "§4" + this.getArena());
							String max = Minigame.getMinigameInstance(plugin).getConfigManager().getMaxPlayers() + "";
							if (Minigame.getMinigameInstance(plugin).getConfigManager().getMaxPlayers() <= 0)
								max = "∞";
							String playerCount = r != null ? (r.getPlayers().size() + "/" + max) : (playerCount = "0/" + max);
							if (!max.equals("∞")){
								if (r != null && r.getPlayers().size() >= Minigame.getMinigameInstance(plugin).getConfigManager().getMaxPlayers())
									playerCount = "§c" + playerCount;
								else
									playerCount = "§a" + playerCount;
							}
							else
								playerCount = "§6" + playerCount;
							sign.setLine(1, playerCount);
							Stage status = r == null ? Stage.WAITING : r.getStage();
							String color = null;
							if (status == Stage.PLAYING)
								color = "§5";
							else if (status == Stage.WAITING || status == Stage.RESETTING)
								color = "§7";
							else if (status == Stage.PREPARING)
								color = "§c";
							sign.setLine(2, color + status.toString());
							String time = "";
							if (status != Stage.WAITING && status != Stage.RESETTING){
								if (r.getRemainingTime() == -1)
									time = "§a" + "∞:∞";
								else {
									String seconds = Integer.toString(r.getRemainingTime() % 60);
									if (seconds.length() == 1)
										seconds = "0" + seconds;
									time = df.format(r.getRemainingTime() / 60) + ":" + seconds;
									if (r.getRemainingTime() <= 60)
										time = "§c" + time;
									else
										time = "§a" + time;
								}
							}
							sign.setLine(3, time);
						}
						else if (this.getType() == LobbyType.PLAYERS && this.getNumber() > 0){
							for (int i = 0; i <= 3; i++){
								if (r != null){
									if (r.getPlayers().size() >= (this.getNumber() - 1) * 4 + i + 1){
										MGPlayer p = r.getPlayerList().get((this.getNumber() - 1) * 4 + i);
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

	public void reset(){
		World w = Bukkit.getWorld(this.getWorld());
		if (w != null){
			Block b = w.getBlockAt(this.getX(), this.getY(), this.getZ());
			if (b != null){
				if (b.getState() instanceof Sign){
					final Sign sign = (Sign)b.getState();
					if (this.getType() == LobbyType.STATUS){
						sign.setLine(0, "§4" + this.getArena());
						String max = Minigame.getMinigameInstance(plugin).getConfigManager().getMaxPlayers() + "";
						if (max.equals("-1"))
							max = "∞";
						sign.setLine(1, "§a0/" + max);
						sign.setLine(2, "§7WAITING");
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
