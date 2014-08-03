package net.amigocraft.mglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import net.amigocraft.mglib.api.Locale;
import net.amigocraft.mglib.api.LogLevel;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.event.MGLibEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * MGLib's primary (central) class.
 * @author Maxim Roncacé
 * @version 0.2.0
 * @since 0.1.0
 */
public class Main extends JavaPlugin {

	/**
	 * The current instance of the plugin.
	 * <br><br>
	 * This is for use within the library; please do not modify this in your plugin or everything will break.
	 * @since 0.1.0
	 */
	public static Main plugin;

	/**
	 * MGLib's logger.
	 * <br><br>
	 * This is for use within the library; please do not use this in your plugin or you'll confuse the server owner.
	 * @since 0.1.0
	 */
	public static Logger log;
	
	/**
	 * Whether block changes should be logged immediately.
	 */
	public static boolean IMMEDIATE_LOGGING;
	
	/**
	 * The minimum level at which messages should be logged.
	 */
	public static LogLevel LOGGING_LEVEL;
	
	/**
	 * The locale for MGLib itself.
	 */
	public static Locale locale;

	/**
	 * Standard {@link JavaPlugin#onEnable()} override.
	 * @since 0.1.0
	 */
	public void onEnable(){
		
		plugin = this;
		log = getLogger();
		Bukkit.getPluginManager().registerEvents(new MGListener(), this);
		saveDefaultConfig();
		IMMEDIATE_LOGGING = getConfig().getBoolean("immediate-logging");
		LOGGING_LEVEL = LogLevel.valueOf(getConfig().getString("logging-level").toUpperCase());
		if (LOGGING_LEVEL == null)
			LOGGING_LEVEL = LogLevel.WARNING;
		Main.log("The configured logging level is invalid!", LogLevel.WARNING);
		
		locale = new Locale("MGLib");
		locale.initialize();
		
		// updater
		if (getConfig().getBoolean("enable-updater")){
			new Updater(this, 74979, this.getFile(), Updater.UpdateType.DEFAULT, true);
		}

		// submit metrics
		if (getConfig().getBoolean("enable-metrics")){
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
			}
			catch (IOException ex){
				log.warning(locale.getMessage("metrics-fail"));
			}
		}
		if (this.getDescription().getVersion().contains("dev"))
			log.warning(locale.getMessage("dev-build"));
		
		// store UUIDs of online players
		List<String> names = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers())
			names.add(p.getName());
		try {
			new UUIDFetcher(names).call();
		}
		catch (Exception ex){
			ex.printStackTrace();
			Main.log.severe(locale.getMessage("uuid-fail"));
		}
		
		log.info(this + " " + locale.getMessage("enabled"));
	}

	/**
	 * Standard {@link JavaPlugin#onDisable()} override.
	 * @since 0.1.0
	 */
	public void onDisable(){
		Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "[MGLib] " + locale.getMessage("ending-rounds"));
		for (Minigame mg : Minigame.getMinigameInstances())
			for (Round r : mg.getRoundList())
				r.end(false);
		Minigame.uninitialize();
		MGLibEvent.uninitialize();
		UUIDFetcher.uninitialize();
		log.info(this + " " + locale.getMessage("disabled"));
		Main.uninitialize();
	}
	
	/**
	 * This method should not be called from your plugin. So don't use it. Please.
	 * @param plugin the name of the plugin to register worlds for.
	 */
	public static void registerWorlds(String plugin){
		MGListener.addWorlds(plugin);
	}

	private static void uninitialize(){
		log = null;
		plugin = null;
	}
	
	/**
	 * Internal convenience method for logging. <strong>Please do not call this from your plugin.</strong>
	 * @param message the message to log.
	 * @param level the {@link LogLevel level} at which to log the message.
	 * @since 0.3.0
	 */
	public static void log(String message, LogLevel level){
		MGUtil.log(message, "MGLib", level);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (label.equalsIgnoreCase("mglib")){
			sender.sendMessage(ChatColor.LIGHT_PURPLE +
					"This server is using MGLib version " + this.getDescription().getVersion() + " by Maxim Roncacé");
			return true;
		}
		return false;
	}
	
	/**
	 * Retrieves a hashmap mapping the names of online players to their respective UUIDs.
	 * @return a hashmap mapping the names of online players to their respective UUIDs.
	 * @since 0.3.0
	 */
	public static HashMap<String, UUID> getOnlineUUIDs(){
		return UUIDFetcher.uuids;
	}

}
