package net.amigocraft.mglib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.amigocraft.mglib.api.LogLevel;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.event.MGLibEvent;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utility methods for use within MGLib. You probably shouldn't call them from your plugin, since this isn't an API class per se.
 * @since 0.1.0
 */
public class MGUtil {

	/**
	 * Loads and returns the given plugin's arenas.yml file.
	 * @param plugin The plugin to load the YAML file from.
	 * @return The loaded {@link YamlConfiguration}.
	 * @since 0.1.0
	 */
	public static YamlConfiguration loadArenaYaml(String plugin){
		JavaPlugin jp = Minigame.getMinigameInstance(plugin).getPlugin();
		File f = new File(jp.getDataFolder(), "arenas.yml");
		try {
			if (!jp.getDataFolder().exists())
				jp.getDataFolder().mkdirs();
			if (!f.exists())
				f.createNewFile();
			YamlConfiguration y = new YamlConfiguration();
			y.load(f);
			return y;
		}
		catch (Exception ex){
			ex.printStackTrace();
			Main.log.severe("An exception occurred while loading arena data for plugin " + plugin);
			return null;
		}
	}

	/**
	 * Saves the given plugin's arenas.yml file.
	 * @param plugin The plugin to save the given {@link YamlConfiguration} to.
	 * @param y The {@link YamlConfiguration} to save.
	 */
	public static void saveArenaYaml(String plugin, YamlConfiguration y){
		JavaPlugin jp = Minigame.getMinigameInstance(plugin).getPlugin();
		File f = new File(jp.getDataFolder(), "arenas.yml");
		try {
			if (!f.exists())
				f.createNewFile();
			y.save(f);
		}
		catch (Exception ex){
			ex.printStackTrace();
			Main.log.severe("An exception occurred while saving arena data for plugin " + plugin);
		}
	}

	public static boolean isInteger(String s){
		try {
			Integer.parseInt(s);
			return true;
		}
		catch (NumberFormatException ex){}
		return false;
	}

	/**
	 * Retrieves worlds registered with MGLib's event listener.
	 * @return worlds registered with MGLib's event listener.
	 * @since 0.1.0
	 */
	public static List<String> getWorlds(){
		List<String> worlds = new ArrayList<String>();
		for (List<String> l : MGListener.worlds.values())
			for (String w : l)
				if (!worlds.contains(w))
					worlds.add(w);
		return worlds;
	}

	/**
	 * Retrieves worlds registered with MGLib's event listener for the given plugin.
	 * @param plugin the plugin to retrieve worlds for.
	 * @return worlds registered with MGLib's event listener for the given plugin.
	 * @since 0.2.0
	 */
	public static List<String> getWorlds(String plugin){
		if (MGListener.worlds.containsKey(plugin))
			return MGListener.worlds.get(plugin);
		else {
			List<String> l = new ArrayList<String>();
			MGListener.worlds.put(plugin, l);
			return l;
		}
	}

	/**
	 * Logs the given message if verbose logging is enabled.
	 * @param message the message to log.
	 * @param prefix the prefix to place in front of the message. This will automatically be placed within brackets.
	 * @param level the {@link LogLevel level} at which to log the message.
	 * @since 0.3.0
	 */
	public static void log(String message, String prefix, LogLevel level){
		if (Main.LOGGING_LEVEL.compareTo(level) <= 0)
			System.out.println("[" + level.toString() + "][" + prefix + "] " + message);
	}

	/**
	 * Calls an event, but sends it only to the appropriate plugin. <strong>Please do not call this from your plugin unless you are
	 * aware of the implications.</strong>
	 * <br><br>
	 * <marquee>CONGRATULATIONS YOU FOUND THE EASTER EGG IN THE DOCUMENTATION ARE YOU PROUD OF YOURSELF?</marquee>
	 * @param event the event to call.
	 * @since 0.3.0
	 */
	public static void callEvent(MGLibEvent event){
		HandlerList hl = event.getHandlers();
		for (RegisteredListener rl : hl.getRegisteredListeners())
			if (rl.getPlugin().getName().equals(event.getPlugin())){
				try {
					rl.callEvent(event);
				}
				catch (EventException ex){
					ex.printStackTrace();
				}
			}
	}

}
