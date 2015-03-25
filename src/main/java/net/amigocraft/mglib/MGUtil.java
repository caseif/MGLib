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

import net.amigocraft.mglib.api.Color;
import net.amigocraft.mglib.api.Localizable;
import net.amigocraft.mglib.api.Location3D;
import net.amigocraft.mglib.api.LogLevel;
import net.amigocraft.mglib.api.MGYamlConfiguration;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.event.MGLibEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Utility methods for use within MGLib. Developers are advised not to use them
 * in a separate plugin, since this isn't an API class and as such is subject to
 * removals and refactors.
 *
 * @since 0.1.0
 */
public class MGUtil {

	static Main plugin;

	/**
	 * Returns the {@link JavaPlugin} instance for MGLib.
	 *
	 * <p><strong>This should not be called</strong> from a hooking plugin under
	 * any circumstance.</p>
	 *
	 * @return the {@link JavaPlugin} instance for MGLib.
	 * @since 0.4.0
	 */
	public static Main getPlugin() {
		return plugin;
	}

	/**
	 * Loads and returns the given plugin's arenas.yml file.
	 *
	 * @param plugin The plugin to load the YAML file from.
	 * @return The loaded {@link YamlConfiguration} object.
	 * @since 0.1.0
	 */
	public static MGYamlConfiguration loadArenaYaml(String plugin) {
		@SuppressWarnings("deprecation")
		JavaPlugin jp = Minigame.getMinigameInstance(plugin).getPlugin();
		File f = new File(jp.getDataFolder(), "arenas.yml");
		try {
			if (!jp.getDataFolder().exists()) {
				jp.getDataFolder().mkdirs();
			}
			if (!f.exists()) {
				f.createNewFile();
			}
			MGYamlConfiguration y = new MGYamlConfiguration();
			y.load(f);
			return y;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Main.log.severe("An exception occurred while loading arena data for plugin " + plugin);
			return null;
		}
	}

	/**
	 * Saves the given plugin's arenas.yml file.
	 *
	 * @param plugin the plugin to save the given {@link YamlConfiguration} to
	 * @param y      the {@link YamlConfiguration} to save
	 */
	public static void saveArenaYaml(String plugin, YamlConfiguration y) {
		@SuppressWarnings("deprecation")
		JavaPlugin jp = Minigame.getMinigameInstance(plugin).getPlugin();
		File f = new File(jp.getDataFolder(), "arenas.yml");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			y.save(f);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Main.log.severe("An exception occurred while saving arena data for plugin " + plugin);
		}
	}

	/**
	 * Determines whether the provided string can be parsed to an integer.
	 *
	 * @param s the string to check
	 * @return whether the provided string can be parsed to an integer
	 */
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		}
		catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * Logs the given message if verbose logging is enabled.
	 *
	 * @param message the message to log
	 * @param prefix  the prefix to place in front of the message. This will
	 *                automatically be placed within brackets
	 * @param level   the {@link LogLevel level} at which to log the message
	 * @since 0.3.0
	 */
	public static void log(String message, String prefix, LogLevel level) {
		if (Main.LOGGING_LEVEL.compareTo(level) >= 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("[").append(level.toString()).append("]");
			if (prefix != null) {
				sb.append("[").append(prefix).append("]");
			}
			sb.append(" ").append(message);
			if (level == LogLevel.SEVERE) {
				System.err.println(sb.toString());
			}
			else {
				System.out.println(sb.toString());
			}
		}
	}

	/**
	 * Calls an event, but sends it only to the appropriate plugin.
	 * <strong>Please do not call this from your pluginv unless you are aware of
	 * the implications.</strong>
	 *
	 * @param event the event to call
	 * @since 0.3.0
	 */
	public static void callEvent(MGLibEvent event) {
		HandlerList hl = event.getHandlers();
		for (RegisteredListener rl : hl.getRegisteredListeners()) {
			if (rl.getPlugin().getName().equals(event.getPlugin()) || rl.getPlugin().getName().equals("MGLib")) {
				try {
					rl.callEvent(event);
				}
				catch (EventException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Retrieves the sign attached to a given block, or null if ones does not
	 * exist.
	 *
	 * @param block the block to check for an attached sign
	 * @return the sign attached to a given block, or null if ones does not
	 * exist
	 */
	public static Block getAttachedSign(Block block) {
		BlockFace[] faces = new BlockFace[]{
				BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP
		};
		for (BlockFace face : faces) {
			Block adjBlock = block.getRelative(face);
			if (adjBlock.getState() instanceof Sign) {
				if (face != BlockFace.UP) {
					@SuppressWarnings("deprecation")
					byte data = adjBlock.getData();
					byte north = 0x2;
					byte south = 0x3;
					byte west = 0x4;
					byte east = 0x5;
					BlockFace attached = null;
					if (data == east) {
						attached = BlockFace.WEST;
					}
					else if (data == west) {
						attached = BlockFace.EAST;
					}
					else if (data == north) {
						attached = BlockFace.SOUTH;
					}
					else if (data == south) {
						attached = BlockFace.NORTH;
					}
					if (adjBlock.getType() == Material.SIGN_POST) {
						attached = BlockFace.DOWN;
					}
					if (block.getX() == adjBlock.getRelative(attached).getX() &&
							block.getY() == adjBlock.getRelative(attached).getY() &&
							block.getZ() == adjBlock.getRelative(attached).getZ()) {
						return adjBlock;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Determines the environment of the given world based on its folder
	 * structure.
	 *
	 * @param world the name of the world to determine the environment of
	 * @return the environment of the given world
	 * @since 0.3.0
	 */
	public static Environment getEnvironment(String world) {
		File worldFolder = new File(Bukkit.getWorldContainer(), world);
		if (worldFolder.exists()) {
			for (File f : worldFolder.listFiles()) {
				if (f.getName().equals("region")) {
					return Environment.NORMAL;
				}
				else if (f.getName().equals("DIM1")) {
					return Environment.THE_END;
				}
				else if (f.getName().equals("DIM-1")) {
					return Environment.NETHER;
				}
			}
		}
		return null;
	}

	/**
	 * Deletes a folder recursively.
	 *
	 * @param folder the folder to delete
	 * @since 0.3.0
	 */
	public static void deleteFolder(File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				deleteFolder(f);
			}
			else {
				f.delete();
			}
		}
	}

	/**
	 * Throws an {@link UnsupportedOperationException} if MGLib is not
	 * disabling.
	 *
	 * @throws UnsupportedOperationException if MGLib is not currently
	 *                                       disabling
	 * @since 0.4.0
	 */
	public static void verifyDisablingStatus() throws UnsupportedOperationException {
		if (!Main.isDisabling()) {
			throw new UnsupportedOperationException(Main.locale.getMessage("plugin.alert.not-disabling").localize());
		}
	}

	/**
	 * Converts a Bukkit {@link Location} to a {@link Location3D}.
	 *
	 * @param location the {@link Location} to convert
	 * @return the new {@link Location3D}
	 * @since 0.4.0
	 */
	public static Location3D fromBukkitLocation(Location location) {
		return fromBukkitLocation(location, false);
	}

	/**
	 * Converts a Bukkit {@link Location} to a {@link Location3D}.
	 *
	 * @param location the {@link Location} to convert
	 * @param copyOrientation whether the pitch and yaw of <code>location</code>
	 *                        will be stored in the new {@link Location3D}
	 *                        (defaults to <code>false</code> if omitted).
	 * @return the new {@link Location3D}
	 * @since 0.4.0
	 */
	public static Location3D fromBukkitLocation(Location location, boolean copyOrientation) {
		if (copyOrientation) {
			return new Location3D(location.getWorld().getName(),
					(float)location.getX(), (float)location.getY(), (float)location.getZ(),
					location.getPitch(), location.getYaw());
		}
		else {
			return new Location3D(location.getWorld().getName(),
					(float)location.getX(), (float)location.getY(), (float)location.getZ());
		}
	}

	/**
	 * Converts a {@link Location3D} to a Bukkit {@link org.bukkit.Location}.
	 *
	 * @param location the {@link Location3D} to convert
	 *                        (defaults to <code>false</code> if omitted).
	 * @return the new {@link org.bukkit.Location}
	 * @since 0.4.0
	 */
	public static Location toBukkitLocation(Location3D location) {
			return new Location(Bukkit.getWorld(location.getWorld()),
					location.getX(), location.getY(), location.getZ(),
					location.getPitch(), location.getYaw());
	}

	/**
	 * Sends a {@link Localizable} to a {@link CommandSender}.
	 * @param sender the CommandSender to send the message to
	 * @param message the message to send
	 * @param color the color to prefix the message with
	 * @param replacements an array or vararg list of
	 * strings to replace placeholder sequences (%i) with
	 * @since 0.5.0
	 */
	public static void sendToSender(CommandSender sender, Localizable message, Color color, String... replacements) {
		if (sender instanceof Player) {
			message.sendTo(((Player)sender).getUniqueId(), color, replacements);
		}
		else {
			sender.sendMessage((color != null ? ChatColor.valueOf(color.name()) : "") + message.localize(replacements));
		}
	}

	/**
	 * Sends a {@link Localizable} to a {@link CommandSender}.
	 * @param sender the CommandSender to send the message to
	 * @param message the message to send
	 * @param replacements an array or vararg list of
	 * strings to replace placeholder sequences (%i) with
	 * @since 0.5.0
	 */
	public static void sendToSender(CommandSender sender, Localizable message, String... replacements) {
		sendToSender(sender, message, null, replacements);
	}
}
