package net.amigocraft.mglib;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utility methods for use within MGLib. You probably shouldn't call them from yours.
 * @since 0.1
 */
public class MGUtil {

	/**
	 * Loads and returns the given plugin's arenas.yml file.
	 * @param plugin The plugin to load the YAML file from.
	 * @return The loaded {@link YamlConfiguration}.
	 * @throws IOException If an exception occurs while loading the file from disk.
	 * @throws InvalidConfigurationException if an exception occurs while loading the configuration from the file.
	 * @since 0.1
	 */
	public static YamlConfiguration loadArenaYaml(String plugin) throws IOException, InvalidConfigurationException {
		JavaPlugin jp = Minigame.getMinigameInstance(plugin).getPlugin();
		File f = new File(jp.getDataFolder(), "arenas.yml");
		if (!f.exists())
			f.createNewFile();
		YamlConfiguration y = new YamlConfiguration();
		y.load(f);
		return y;
	}
	
	/**
	 * Saves the given plugin's arenas.yml file.
	 * @param plugin The plugin to save the given {@link YamlConfiguration} to.
	 * @return The {@link YamlConfiguration} to save.
	 * @throws IOException If an exception occurs while saving the file to disk.
	 */
	public static void saveArenaYaml(String plugin, YamlConfiguration y) throws IOException {
		JavaPlugin jp = Minigame.getMinigameInstance(plugin).getPlugin();
		File f = new File(jp.getDataFolder(), "arenas.yml");
		if (!f.exists())
			f.createNewFile();
		y.save(f);
	}
	
}
