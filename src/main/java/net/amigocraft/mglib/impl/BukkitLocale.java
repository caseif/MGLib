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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.amigocraft.mglib.api.Locale;
import net.amigocraft.mglib.api.Localizable;
import org.bukkit.Bukkit;

public class BukkitLocale extends Locale {

	private final Map<String, BukkitLocalizable> messages = new HashMap<String, BukkitLocalizable>();

	/**
	 * The name of the plugin this locale manager belongs to.
	 */
	public String plugin;

	/**
	 * An enumeration of message keys found in the default locale, but not the
	 * defined one.
	 */
	public final List<String> undefinedMessages = new ArrayList<String>();

	/**
	 * Creates a new locale manager for the given plugin.
	 *
	 * <p>MGLib attempts to load locales first from the "locales"
	 * directory in the plugin's data folder, then from the locales
	 * directory in the plugin JAR's root.</p>
	 *
	 * @param plugin the plugin to create a locale manager for
	 */
	public BukkitLocale(String plugin) {
		this.plugin = plugin;
		loadFromDataFolder();
		loadFromInternal();
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
		return this.messages.containsKey(key) ? this.messages.get(key) : new BukkitLocalizable(this, key);
	}

	@Override
	public boolean isLegacy() {
		return false;
	}

	private void loadFromDataFolder() {
		File external = new File(Bukkit.getPluginManager().getPlugin(this.plugin).getDataFolder(), "locales");
		if (external.exists()) {
			if (external.isDirectory()) {
				// files are indexed into appropriate lists first so non-legacy locales take priority
				final List<File> localeFiles = new ArrayList<File>();
				final List<File> legacyFiles = new ArrayList<File>();
				//noinspection ConstantConditions
				for (File f : external.listFiles()) {
					if (f.isFile()) {
						if (f.getName().endsWith(".properties")) {
							localeFiles.add(f);
						}
						else if (f.getName().endsWith(".csv")) {
							legacyFiles.add(f);
						}
					}
				}
				for (File f : localeFiles) {
					String name = f.getName().substring(0, f.getName().lastIndexOf(".")).replace("_", "").replace("-", "");
					try {
						loadLanguage(name, new FileInputStream(f), false);
					}
					catch (IOException ex) {
						ex.printStackTrace();
						System.out.println("Failed to load locale " + name + "! Legacy: false");
					}
				}
				for (File f : legacyFiles) {
					String name = f.getName().substring(0, f.getName().lastIndexOf(".")).replace("_", "").replace("-", "");
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
				throw new IllegalArgumentException("Invalid locale container: " + external.getPath());
			}
		}
	}

	private void loadFromInternal() {
		// files are indexed into appropriate lists first so non-legacy locales take priority
		final List<JarEntry> localeFiles = new ArrayList<JarEntry>();
		final List<JarEntry> legacyFiles = new ArrayList<JarEntry>();
		try {
			Class<?> mainClass = Class.forName(Bukkit.getPluginManager().getPlugin(this.plugin).getDescription().getMain());
			JarFile jar = new JarFile(mainClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				if (!e.isDirectory() && e.getName().startsWith("locales")) {
					if (e.getName().endsWith(".properties")) {
						localeFiles.add(e);
					}
					else if (e.getName().endsWith(".csv")) {
						legacyFiles.add(e);
					}
				}
			}
			for (JarEntry e : localeFiles) {
				String name = e.getName().substring(0, e.getName().lastIndexOf(".")).replace("locales/", "").replace("_", "").replace("-", "");
				try {
					loadLanguage(name, jar.getInputStream(e), false);
				}
				catch (IOException ex) {
					ex.printStackTrace();
					System.out.println("Failed to load locale " + name + "! Legacy: false");
				}
			}
			for (JarEntry e : legacyFiles) {
				String name = e.getName().substring(0, e.getName().lastIndexOf(".")).replace("locales/", "").replace("_", "").replace("-", "");
				try {
					loadLanguage(name, jar.getInputStream(e), false);
				}
				catch (IOException ex) {
					ex.printStackTrace();
					System.out.println("Failed to load locale " + name + "! Legacy: true");
				}
			}
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		catch (URISyntaxException ex) {
			ex.printStackTrace();
		}
	}

	private void loadLanguage(String language, InputStream stream, boolean legacy) throws IOException {
		language = language.replace("_", "").replace("-", ""); // normalize locale names
		if (legacy) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					if (line.contains("\\|") && line.indexOf("\\|") != 0) {
						String[] entry = line.split("\\|");
						if (!messages.containsKey(entry[0])) {
							messages.put(entry[0], new BukkitLocalizable(this, entry[0]));
						}
						messages.get(entry[0]).addLocale(language, entry[1]);
					}
				}
			}
		}
		else {
			Properties props = new Properties();
			props.load(stream);
			for (Map.Entry e : props.entrySet()) {
				String key = e.getKey().toString();
				if (!messages.containsKey(key)) {
					messages.put(key, new BukkitLocalizable(this, key));
				}
				messages.get(key).addLocale(language, e.getValue().toString());
			}
		}
	}
}
