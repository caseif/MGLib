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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.amigocraft.mglib.api.Locale;
import net.amigocraft.mglib.api.Localizable;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;

public class BukkitLocale extends Locale {

	private final Map<String, BukkitLocalizable> messages = new HashMap<String, BukkitLocalizable>();

	/**
	 * The name of the plugin this locale manager belongs to.
	 *
	 * @since 0.3.0
	 */
	public String plugin;

	/**
	 * An enumeration of message keys found in the default locale, but not the
	 * defined one.
	 *
	 * @since 0.4.0
	 */
	public final List<String> undefinedMessages = new ArrayList<String>();

	private String prefix;

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
		return this.getMessage(key).localize(replacements);
	}

	@Override
	public Localizable getMessage(String key) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isLegacy() {
		return false;
	}

	@Override
	public void initialize() {
		File external = new File(Bukkit.getPluginManager().getPlugin(this.plugin).getDataFolder(), "locales");
		if (external.exists() && external.isDirectory()) {
			loadContainer(external);
		}
		URL internal = Bukkit.getPluginManager().getPlugin(plugin).getClass().getClassLoader().getResource("locales");
		if (internal != null) {
			try {
				File internalContainer = new File(internal.toURI());
				loadContainer(internalContainer);
			}
			catch (URISyntaxException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void loadContainer(File container) {
		if (container.isDirectory()) {
			final List<File> localeFiles = new ArrayList<File>();
			final List<File> legacyFiles = new ArrayList<File>();
			// files are indexed into appropriate lists first so non-legacy locales take priority
			//noinspection ConstantConditions
			for (File f : container.listFiles()) {
				if (f.isFile()) {
					if (f.getName().endsWith(".csv")) {
						localeFiles.add(f);
					}
					else if (f.getName().endsWith(".properties")) {
						legacyFiles.add(f);
					}
				}
			}
			for (File f : localeFiles) {
				String name = f.getName().substring(0, f.getName().lastIndexOf("."));
				try {
					loadLanguage(name, new FileInputStream(f), false);
				}
				catch (IOException ex) {
					ex.printStackTrace();
					System.out.println("Failed to load locale " + name + "! Legacy: false");
				}
			}
			for (File f : legacyFiles) {
				String name = f.getName().substring(0, f.getName().lastIndexOf("."));
				try {
					loadLanguage(name, new FileInputStream(f), false);
				}
				catch (IOException ex) {
					ex.printStackTrace();
					System.out.println("Failed to load locale " + name + "! Legacy: true");
				}
			}
		}
		else {
			throw new IllegalArgumentException("Invalid locale container: " + container.getPath());
		}
	}

	private void loadLanguage(String language, InputStream stream, boolean legacy) throws IOException {
		language = language.replace("_", "").replace("-", ""); // normalize locale names
		if (legacy) {
			throw new NotImplementedException(); //TODO
		}
		else {
			Properties props = new Properties();
			props.load(stream);
			for (Map.Entry e : props.entrySet()) {
				String key = e.getKey().toString();
				if (!messages.containsKey(key)) {
					messages.put(key, new BukkitLocalizable(key));
				}
				messages.get(key).addLocale(language, e.getValue().toString());
			}
		}
	}
}
