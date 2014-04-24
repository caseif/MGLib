package net.amigocraft.mglib.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ConfigManager {

	private String plugin;

	private Location exitLocation;
	private int maxPlayers = 32;
	private String signId;
	private int roundPrepareTime = 90;
	private int roundPlayTime = 300;
	
	public ConfigManager(String plugin){
		this.plugin = plugin;
		this.exitLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
		this.signId = "[" + plugin + "]";
	}
	
	//TODO: Lots of documentation -_-
	
	public Minigame getMinigame(){
		return Minigame.getMinigameInstance(plugin);
	}

	public String getPlugin(){
		return plugin;
	}

	public void setPlugin(String plugin){
		this.plugin = plugin;
	}

	public Location getDefaultExitLocation(){
		return exitLocation;
	}

	public void setDefaultExitLocation(Location exitLocation){
		this.exitLocation = exitLocation;
	}

	public int getMaxPlayers(){
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers){
		this.maxPlayers = maxPlayers;
	}

	public String getSignId(){
		return signId;
	}

	public void setSignId(String signId){
		this.signId = signId;
	}

	public int getDefaultPreparationTime(){
		return roundPrepareTime;
	}

	public void setDefaultPreparationTime(int preparationTime){
		this.roundPrepareTime = preparationTime;
	}

	public int getDefaultPlayingTime(){
		return roundPlayTime;
	}

	public void setDefaultPlayingTime(int playingTime){
		this.roundPlayTime = playingTime;
	}
	
	//TODO: More lobby sign configuration
	//TODO: And white/blacklisted actions
	
}
