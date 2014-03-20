package net.amigocraft.mglib;

import java.io.File;

import net.amigocraft.mglib.round.MGPlayer;
import net.amigocraft.mglib.round.Round;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Discrete event listener class.
 * <br><br>
 * This is not an API class. Please do not use it.
 */
public class MGListener implements Listener {

	/**
	 * NO DOCUMENTATION AMYWHERE >:D
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		final String p = e.getPlayer().getName();
		Bukkit.getScheduler().runTaskAsynchronously(MGLib.plugin, new Runnable(){
			public void run(){
				for (Minigame mg : Minigame.getMinigameInstances())
					for (Round r : mg.getRounds())
						if (r.getPlayers().containsKey(p)){
							try {
								r.removePlayer(p);
								YamlConfiguration y = new YamlConfiguration();
								File f = new File(MGLib.plugin.getDataFolder(), "offlineplayers.yml");
								if (!f.exists())
									f.createNewFile();
								y.load(f);
								y.set(p + ".w", mg.getExitLocation().getWorld().getName());
								y.set(p + ".x", mg.getExitLocation().getX());
								y.set(p + ".y", mg.getExitLocation().getY());
								y.set(p + ".z", mg.getExitLocation().getZ());
								y.save(f);
							}
							catch (Exception ex){
								ex.printStackTrace();
								MGLib.log.severe("An exception occurred while saving data for " + p);
							}
						}
			}
		});
	}
	
	/**
	 * HAHAHAHAHAHAHAHAHA >:D
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		final String p = e.getPlayer().getName();
		Bukkit.getScheduler().runTaskAsynchronously(MGLib.plugin, new Runnable(){
			public void run(){
				for (Minigame mg : Minigame.getMinigameInstances())
					for (Round r : mg.getRounds())
						if (r.getPlayers().containsKey(p)){
							try {
								YamlConfiguration y = new YamlConfiguration();
								File f = new File(MGLib.plugin.getDataFolder(), "offlineplayers.yml");
								if (!f.exists())
									f.createNewFile();
								y.load(f);
								if (y.isSet("players")){
									if (y.isSet(p))
										MGPlayer.resetPlayer(p, new Location(
												Bukkit.getWorld(y.getString(p + ".w")),
												y.getDouble(p + ".x"),
												y.getDouble(p + ".y"),
												y.getDouble(p + ".z")));
								}
							}
							catch (Exception ex){
								ex.printStackTrace();
								MGLib.log.severe("An exception occurred while loading data for " + p);
							}
						}
			}
		});
	}

}
