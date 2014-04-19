package net.amigocraft.mglib;

import net.amigocraft.mglib.api.Minigame;

public class LobbySign {

	private int x;
	private int y;
	private int z;
	private String plugin;
	private String world;
	private String arena;
	private int number;
	private String type;
	private int index;
	
	public LobbySign(int x, int y, int z, String plugin, String world, String arena, int number, String type){
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
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public void save(){
		Minigame.getMinigameInstance(plugin).getLobbyManager().saveSign(this);
	}
	
	public void remove(){
		Minigame.getMinigameInstance(plugin).getLobbyManager().saveSign(this);
	}
	
}
