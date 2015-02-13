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
package net.amigocraft.mglib;

import net.amigocraft.mglib.api.Locale;
import net.amigocraft.mglib.api.LogLevel;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.event.MGLibEvent;
import net.amigocraft.mglib.util.NmsUtil;

import net.gravitydevelopment.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * MGLib's primary (central) class.
 *
 * @author Maxim Roncacé
 * @version 0.3.1-SNAPSHOT
 * @since 0.1.0
 */
public class Main extends JavaPlugin {

	/**
	 * The current instance of the plugin.
	 *
	 * <p><strong>This is for use within the library; please do not modify this
	 * in your plugin or everything will break.</strong></p>
	 *
	 * @since 0.1.0
	 */
	public static Main plugin;

	/**
	 * MGLib's logger.
	 *
	 * <p><strong>This is for use within the library; please do not use this in
	 * your plugin or you'll confuse the server owner.</strong></p>
	 *
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
	 * Whether vanilla spectating is globally disabled.
	 */
	private static boolean VANILLA_SPECTATING_DISABLED;

	/**
	 * The locale for MGLib itself.
	 */
	public static Locale locale;

	private static boolean disabling = false;

	/**
	 * Standard {@link JavaPlugin#onEnable()} override.
	 *
	 * @since 0.1.0
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void onEnable() {

		plugin = this;
		log = getLogger();
		Bukkit.getPluginManager().registerEvents(new MGListener(), this);
		saveDefaultConfig();
		IMMEDIATE_LOGGING = getConfig().getBoolean("immediate-logging");
		LOGGING_LEVEL = LogLevel.valueOf(getConfig().getString("logging-level").toUpperCase());
		if (LOGGING_LEVEL == null) {
			LOGGING_LEVEL = LogLevel.WARNING;
			Main.log("The configured logging level is invalid!", LogLevel.WARNING);
		}
		VANILLA_SPECTATING_DISABLED = getConfig().getBoolean("disable-vanilla-spectating");

		locale = new Locale("MGLib");
		locale.initialize();

		// updater
		if (getConfig().getBoolean("enable-updater")) {
			new Updater(this, 74979, this.getFile(), Updater.UpdateType.DEFAULT, true);
		}

		// submit metrics
		if (getConfig().getBoolean("enable-metrics")) {
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
			}
			catch (IOException ex) {
				log.warning(locale.getMessage("plugin.alert.metrics-fail"));
			}
		}
		if (this.getDescription().getVersion().contains("dev")) {
			log.warning(locale.getMessage("plugin.alert.dev-build"));
		}

		// store UUIDs of online players
		List<String> names = new ArrayList<String>();
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
				for (Player pl : (Collection<? extends Player>)Bukkit.class.getMethod("getOnlinePlayers",
						new Class<?>[0]).invoke(null)) {
					names.add(pl.getName());
				}
			}
			else {
				for (Player pl : (Player[])Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null)) {
					names.add(pl.getName());
				}
			}
		}
		catch (NoSuchMethodException ex) { // can never happen
			ex.printStackTrace();
		}
		catch (InvocationTargetException ex) { // can also never happen
			ex.printStackTrace();
		}
		catch (IllegalAccessException ex) { // can still never happen
			ex.printStackTrace();
		}
		try {
			new UUIDFetcher(names).call();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Main.log.severe(locale.getMessage("plugin.alert.uuid-fail"));
		}

		log.info(locale.getMessage("plugin.event.enable", this.toString()));
	}

	/**
	 * Standard {@link JavaPlugin#onDisable()} override.
	 *
	 * @since 0.1.0
	 */
	@Override
	public void onDisable() {
		disabling = true;
		Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "[MGLib] " + locale.getMessage("plugin.event.restart"));
		for (Minigame mg : Minigame.getMinigameInstances()) {
			for (Round r : mg.getRoundList()) {
				r.end(false);
			}
		}
		Minigame.uninitialize();
		MGLibEvent.uninitialize();
		NmsUtil.uninitialize();
		UUIDFetcher.uninitialize();
		log.info(locale.getMessage("plugin.event.disable", this.toString()));
		Main.uninitialize();
	}

	/**
	 * <p>This method should not be called from your plugin. So don't use it.
	 * Please.</p>
	 *
	 * @param plugin the name of the plugin to register worlds for
	 */
	public static void registerWorlds(String plugin) {
		MGListener.addWorlds(plugin);
	}

	private static void uninitialize() {
		log = null;
		plugin = null;
	}

	/**
	 * Internal convenience method for logging. <strong>Please do not call this
	 * from your plugin.</strong>
	 *
	 * @param message the message to log.
	 * @param level   the {@link LogLevel level} at which to log the message
	 * @since 0.3.0
	 */
	public static void log(String message, LogLevel level) {
		MGUtil.log(message, "MGLib", level);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("mglib")) {
			sender.sendMessage(ChatColor.LIGHT_PURPLE +
					locale.getMessage("plugin.event.info", getDescription().getVersion(), "Maxim Roncacé"));
			return true;
		}
		return false;
	}

	/**
	 * Retrieves worlds registered with MGLib's event listener for the given
	 * plugin.
	 *
	 * @param plugin the plugin to retrieve worlds for
	 * @return worlds registered with MGLib's event listener for the given
	 * plugin
	 * @since 0.3.1
	 */
	public static List<String> getWorlds(String plugin) {
		return MGListener.getWorlds();
	}

	/**
	 * Retrieves a hashmap mapping the names of online players to their
	 * respective UUIDs.
	 *
	 * @return a hashmap mapping the names of online players to their
	 * respective UUIDs
	 * @since 0.3.0
	 */
	public static HashMap<String, UUID> getOnlineUUIDs() {
		return UUIDFetcher.uuids;
	}

	/**
	 * Retrieves whether vanilla spectating has been globally disabled by
	 * MGLib's config.yml file.
	 *
	 * @return whether vanilla spectating has been globally disabled by MGLib's
	 * config.yml file
	 * @since 0.3.0
	 */
	public static boolean isVanillaSpectatingDisabled() {
		return VANILLA_SPECTATING_DISABLED;
	}

	/**
	 * Determines whether MGLib is in the process of disabling.
	 * This is to provide security when unsetting static objects.
	 *
	 * @return whether MGLib is in the process of disabling.
	 * @since 0.3.1
	 */
	public static boolean isDisabling() {
		return disabling;
	}

}
