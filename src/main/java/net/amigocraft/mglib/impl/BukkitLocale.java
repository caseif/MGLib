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
package net.amigocraft.mglib.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.Main;
import net.amigocraft.mglib.api.Locale;
import net.amigocraft.mglib.api.Localizable;
import net.amigocraft.mglib.api.LogLevel;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.exception.PlayerOfflineException;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitLocale extends Locale {

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

	/**
	 * An enumeration of message keys found in the default locale, but not the
	 * defined one.
	 *
	 * @since 0.4.0
	 */
	public final List<String> undefinedMessages = new ArrayList<String>();

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
	public BukkitLocale(String plugin) {
		this.plugin = plugin;
		prefix = plugin.equals("MGLib") ? null : "[" + plugin + "]";
	}

	@Override
	public String _INVALID_getMessage(String key) {
		return _INVALID_getMessage(key, new String[0]);
	}

	@Override
	public String _INVALID_getMessage(String key, String... replacements) {
		String message = messages.get(key.toLowerCase());
		if (message != null) {
			for (int i = 0; i < replacements.length; i++) {
				message = message.replace("%" + (i + 1), replacements[i]);
			}
			return message;
		}
		return key;
	}

	@Override
	public Localizable getMessage(String key, String... replacements) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isLegacy() {
		return this.legacy;
	}

	@Override
	public void initialize() {
		if (Bukkit.getPluginManager().getPlugin(plugin).getClass().getClassLoader().getResource("locales") != null) {
			InputStream is;
			InputStream defaultIs = null;
			String defaultLocale = Minigame.getMinigameInstance(plugin) != null ?
			                       Minigame.getMinigameInstance(plugin).getConfigManager().getDefaultLocale() :
			                       "enUS";
			String locale = MGUtil.getPlugin().getConfig().getString("locale");
			String loc = null;
			try {
				defaultIs = Bukkit.getPluginManager().getPlugin(plugin).getClass()
						.getResourceAsStream("/locales/" + defaultLocale + ".properties");
				File file = new File(Bukkit.getPluginManager().getPlugin(plugin).getDataFolder() + File.separator +
						"locales" + File.separator + locale + ".properties");
				is = new FileInputStream(file);
				loc = file.getAbsolutePath();

			}
			catch (Exception ex) {
				is = Bukkit.getPluginManager().getPlugin(plugin).getClass()
						.getResourceAsStream("/locales/" + locale + ".properties");
				if (is == null) {
					try {
						if (defaultIs == null) {
							try {
								defaultIs = Bukkit.getPluginManager().getPlugin(plugin).getClass()
										.getResourceAsStream("/locales/" + defaultLocale + ".csv");
							}
							catch (Exception swallow) {
								// meh
							}
							File file = new File(Bukkit.getPluginManager().getPlugin(plugin).getDataFolder() +
									File.separator + "locales" + File.separator + locale + ".csv");
							is = new FileInputStream(file);
							loc = file.getAbsolutePath();
							legacy = true;
						}
						else {
							MGUtil.log("Locale defined in config not found in JAR or plugin folder; defaulting to " +
									defaultLocale, prefix, LogLevel.WARNING);
							is = defaultIs;
						}
					}
					catch (Exception ex2) {
						is = Bukkit.getPluginManager().getPlugin(plugin).getClass()
								.getResourceAsStream("/locales/" + locale + ".csv");
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
				if (is != null) {
					if (!legacy) {
						Properties props = new Properties();
						props.load(is);
						for (Map.Entry<Object, Object> e : props.entrySet()) {
							messages.put(e.getKey().toString(), e.getValue().toString());
						}
					}
					else {
						BufferedReader br;
						String line;
						br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
						while ((line = br.readLine()) != null) {
							String[] params = line.split("\\|");
							if (params.length > 1) {
								messages.put(params[0], params[1]);
							}
						}
					}
					if (loc != null) {
						Main.log(this.getMessage("plugin.event.loaded-locale", loc), LogLevel.INFO);
					}
				}
				if (defaultIs != null) {
					if (!legacy) {
						Properties props = new Properties();
						props.load(defaultIs);
						for (Map.Entry<Object, Object> e : props.entrySet()) {
							if (!messages.containsKey(e.getKey().toString())) {
								messages.put(e.getKey().toString(), e.getValue().toString());
								undefinedMessages.add(e.getKey().toString());
							}
						}
					}
					else {
						BufferedReader br;
						String line;
						br = new BufferedReader(new InputStreamReader(defaultIs, Charset.forName("UTF-8")));
						while ((line = br.readLine()) != null) {
							String[] params = line.split("\\|");
							if (params.length > 1) {
								if (!messages.containsKey(params[0])) {
									messages.put(params[0], params[1]);
									undefinedMessages.add(params[0]);
								}
							}
						}
					}
					if (loc != null) {
						Main.log(this.getMessage("plugin.event.loaded-locale", loc), LogLevel.INFO);
					}
				}
				if (is == null && defaultIs == null) {
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
