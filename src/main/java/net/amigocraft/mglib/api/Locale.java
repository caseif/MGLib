/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Maxim Roncacé
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.amigocraft.mglib.api;

import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.Main;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * MGLib's locale API. It can be used for easy localization; you
 * need only supply the translations themselves in the form of
 * *.properties files.
 *
 * @since 0.3.0
 */
public class Locale {

	/**
	 * The name of the plugin this locale manager belongs to.
	 *
	 * @since 0.3.0
	 */
	public String plugin;

	/**
	 * The messages stored by this local manager.
	 *
	 * @since 0.3.0
	 */
	public final Map<String, String> messages = new HashMap<String, String>();
	private String prefix;

	private boolean legacy = false;

	/**
	 * Creates a new locale manager for the given plugin (yours).
	 * MGLib attempts to load locales first from the "locales"
	 * directory in the plugin's data folder, then from the locales
	 * directory in the plugin JAR's root.
	 *
	 * @param plugin the plugin to create a locale manager for
	 * @since 0.3.0
	 */
	public Locale(String plugin) {
		this.plugin = plugin;
		prefix = plugin.equals("MGLib") ? "" : "[" + plugin + "]";
	}

	/**
	 * Retrieves the message with the given key from the
	 * current locale, and replaces placeholder sequences
	 * (<code>%i</code>) with the corresponding vararg
	 * parameter.
	 *
	 * <p><i>Note: placeholder sequences should start at
	 * index 1.</i></p>
	 *
	 * @param key the key of the message to retrieve
	 * @param replacements an array or vararg list of
	 * strings to replace placeholder sequences (%i) with
	 * @return the message associated with the given key,
	 * or the key if the message is not defined
	 * @since 0.3.1 (exists in 0.3.0 without vararg parameter)
	 */
	public String getMessage(String key, String... replacements) {
		String message = messages.get(key.toLowerCase());
		if (message != null) {
			for (int i = 0; i < replacements.length; i++) {
				message.replace("%" + (i + 1), replacements[i]);
			}
			return message;
		}
		return key;
	}

	/**
	 * Returns whether this object was loaded from a legacy
	 * locale file.
	 *
	 * @return whether this object was loaded from a legacy
	 * locale file.
	 * @since 0.3.1
	 */
	public boolean isLegacy() {
		return this.legacy;
	}

	/**
	 * Initializes the locale manager. This must be called, or
	 * {@link Locale#getMessage(String, String...)} will always return its
	 * parameter.
	 *
	 * @since 0.3.0
	 */
	public void initialize() {
		if (Locale.class.getClassLoader().getResource("locales") != null) {
			InputStream is;
			InputStream defaultIs = null;
			String defaultLocale = Minigame.getMinigameInstance(plugin) != null ?
					Minigame.getMinigameInstance(plugin).getConfigManager().getDefaultLocale() :
					"enUS";
			try {
				defaultIs = Locale.class.getResourceAsStream("/locales/" +
						defaultLocale + ".properties");
				File file = new File(Bukkit.getPluginManager().getPlugin(plugin).getDataFolder() + File.separator +
						"locales" + File.separator +
						Main.plugin.getConfig().getString("locale") + ".properties");
				is = new FileInputStream(file);
				Main.log("Loaded locale from " + file.getAbsolutePath(), LogLevel.INFO);

			}
			catch (Exception ex) {
				is = Bukkit.getPluginManager().getPlugin(plugin).getClass().getResourceAsStream("/locales/" +
						Main.plugin.getConfig().getString("locale") + ".properties");
				if (is == null) {
					try {
						defaultIs = Locale.class.getResourceAsStream("/locales/" +
								defaultLocale + ".csv");
						File file = new File(Bukkit.getPluginManager().getPlugin(plugin).getDataFolder() +
								File.separator + "locales" + File.separator +
								Main.plugin.getConfig().getString("locale") + ".csv");
						is = new FileInputStream(file);
						Main.log("Loaded locale from " + file.getAbsolutePath(), LogLevel.INFO);
						legacy = true;
					}
					catch (Exception ex2) {
						is = Bukkit.getPluginManager().getPlugin(plugin).getClass().getResourceAsStream("/locales/" +
								Main.plugin.getConfig().getString("locale") + ".csv");
						legacy = true;
						if (is == null) {
							MGUtil.log("Locale defined in config not found in JAR or plugin folder; defaulting to " +
									defaultLocale, prefix, LogLevel.WARNING);
							is = defaultIs;
						}
					}
				}
			}
			try {
				InputStream is2 = is != null ? is : defaultIs;
				if (is2 != null) {
					if (!legacy) {
						Properties props = new Properties();
						props.load(is2);
						for (Map.Entry<Object, Object> e : props.entrySet()) {
							messages.put(e.getKey().toString(), e.getValue().toString());
						}
					}
					else {
						BufferedReader br;
						String line;
						br = new BufferedReader(new InputStreamReader(is2, Charset.forName("UTF-8")));
						while ((line = br.readLine()) != null) {
							String[] params = line.split("\\|");
							if (params.length > 1) {
								messages.put(params[0], params[1]);
							}
						}
					}
				}
				else {
					MGUtil.log("Neither the defined nor default locale could be loaded. " +
							"Localized messages will be displayed only as their keys!", prefix, LogLevel.SEVERE);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if (is != null) {
					try {
						is.close();
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
}
