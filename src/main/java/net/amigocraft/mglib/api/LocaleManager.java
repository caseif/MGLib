package net.amigocraft.mglib.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;

import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.Main;

import org.bukkit.Bukkit;

/**
 * MGLib's locale API. It can be used for easy localization; you need only supply the translations
 * themselves in the form of CSV files using "|" symbols as separators.
 * @since 0.3.0
 */
public class LocaleManager {

	/**
	 * The name of the plugin this locale manager belongs to.
	 * @since 0.3.0
	 */
	public String plugin;
	/**
	 * The messages stored by this local manager.
	 * @since 0.3.0
	 */
	public HashMap<String, String> messages = new HashMap<String, String>();
	private String prefix;
	
	/**
	 * Creates a new locale manager for the given plugin (yours). MGLib attempts to load locales first from the
	 * "locales" directory in the plugin's data folder, then from the locales directory in the plugin JAR's root.
	 * @param plugin the plugin to create a locale manager for.
	 * @since 0.3.0
	 */
	public LocaleManager(String plugin){
		this.plugin = plugin;
		prefix = plugin.equals("MGLib") ? "" : "[" + plugin + "]";
	}

	/**
	 * Retrieves a message from a given key.
	 * @param key the key to search for.
	 * @return the message associated with the given key.
	 * @since 0.3.0
	 */
	public String getMessage(String key){
		String message = messages.get(key.toLowerCase());
		if (message != null)
			return message;
		return key;
	}

	/**
	 * Initializes the locale manager. This must be called, or {@link LocaleManager#getMessage(String)} will always return its parameter.
	 * @since 0.3.0
	 */
	public void initialize(){
		InputStream is = null;
		InputStream defaultIs = null;
		String defaultLocale = Minigame.getMinigameInstance(plugin) != null ?
				Minigame.getMinigameInstance(plugin).getConfigManager().getDefaultLocale() : "enUS";
				try {
					defaultIs = LocaleManager.class.getResourceAsStream("/locales/" +
							defaultLocale + ".csv");
					File file = new File(Bukkit.getPluginManager().getPlugin(plugin).getDataFolder() + File.separator + "locales" + File.separator +
							Main.plugin.getConfig().getString("locale") + ".csv");
					is = new FileInputStream(file);
					if (Main.LOGGING_LEVEL >= 1)
						Main.log.info("Loaded locale from " + file.getAbsolutePath());

				}
				catch (Exception ex){
					is = Bukkit.getPluginManager().getPlugin(plugin).getClass().getResourceAsStream("/locales/" +
							Main.plugin.getConfig().getString("locale") + ".csv");
					if (is == null){
						Main.log.warning(prefix + "Locale defined in config not found in JAR or plugin folder; defaulting to " + defaultLocale);
						is = defaultIs;
					}
				}
				try {
					if (is != null){
						BufferedReader br;
						String line;
						br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
						while ((line = br.readLine()) != null) {
							String[] params = line.split("\\|");
							if (params.length > 1){
								messages.put(params[0], params[1]);
							}
						}
					}
					if (defaultIs != null){
						BufferedReader br;
						String line;
						br = new BufferedReader(new InputStreamReader(defaultIs, Charset.forName("UTF-8")));
						while ((line = br.readLine()) != null) {
							String[] params = line.split("\\|");
							if (params.length > 1){
								if (!messages.containsKey(params[0])){
									messages.put(params[0].toLowerCase(), params[1]);
								}
							}
						}
					}
					else if (is == null)
						Main.log.severe("Neither the defined nor default locale could be loaded. Localized messages will be displayed only as their keys!");
				}
				catch (IOException e){
					e.printStackTrace();
				}
				finally {
					try {is.close();}
					catch (Exception ex){ex.printStackTrace();}
				}
	}
}
