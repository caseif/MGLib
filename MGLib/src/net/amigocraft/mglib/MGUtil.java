package net.amigocraft.mglib;

import java.io.File;
import java.util.List;

import net.amigocraft.mglib.api.Minigame;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utility methods for use within MGLib. You probably shouldn't call them from your plugin, since this isn't an API class per se.
 * @since 0.1
 */
public class MGUtil {

	/**
	 * Loads and returns the given plugin's arenas.yml file.
	 * @param plugin The plugin to load the YAML file from.
	 * @return The loaded {@link YamlConfiguration}.
	 * @since 0.1
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
	 * Retrieves worlds registered with MGLib's event listener
	 * @return worlds registered with MGLib's event listener
	 * @since 0.1
	 */
	public static List<String> getWorlds(){
		return MGListener.worlds;
	}

}
