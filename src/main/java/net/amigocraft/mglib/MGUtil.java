package net.amigocraft.mglib;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.amigocraft.mglib.api.LogLevel;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.event.MGLibEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utility methods for use within MGLib. You probably shouldn't call them from your plugin, since this isn't an API class per se.
 * @since 0.1.0
 */
public class MGUtil {

	private static boolean NMS_SUPPORT = true;
	private static Constructor<?> packetPlayOutAnimation;
	private static Method getHandle;
	private static Field playerConnection;
	private static Method sendPacket;

	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_WHITE = "\u001B[37m";

	static {
		try {
			//get the constructor of the packet
			packetPlayOutAnimation = getMCClass("PacketPlayOutAnimation").getConstructor(getMCClass("Entity"), int.class);
			//get method for recieving craftplayer's entityplayer
			getHandle = getCraftClass("entity.CraftPlayer").getMethod("getHandle");
			//get the playerconnection of the entityplayer
			playerConnection = getMCClass("EntityPlayer").getDeclaredField("playerConnection");
			//method to send the packet
			sendPacket = getMCClass("PlayerConnection").getMethod("sendPacket", getMCClass("Packet"));
		}
		catch (Exception e){
			Main.log.warning("Cannot access NMS codebase! Packet effects disabled.");
			NMS_SUPPORT = false;
		}
	}

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
		if (Main.LOGGING_LEVEL.compareTo(level) >= 0)
			System.out.println("[" + level.toString() + "][" + prefix + "] " + message);
	}

	/**
	 * Calls an event, but sends it only to the appropriate plugin. <strong>Please do not call this from your plugin unless you are
	 * aware of the implications.</strong>
	 * @param event the event to call.
	 * @since 0.3.0
	 */
	public static void callEvent(MGLibEvent event){
		HandlerList hl = event.getHandlers();
		for (RegisteredListener rl : hl.getRegisteredListeners())
			if (rl.getPlugin().getName().equals(event.getPlugin()) || rl.getPlugin().getName().equals("MGLib")){
				try {
					rl.callEvent(event);
				}
				catch (EventException ex){
					ex.printStackTrace();
				}
			}
	}

	/**
	 * Retrieves the sign attached to a given block, or null if ones does not exist.
	 * @param block the block to check for an attached sign.
	 * @return the sign attached to a given block, or null if ones does not exist.
	 */
	public static Block getAttachedSign(Block block){
		BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
				BlockFace.WEST, BlockFace.UP};
		for (BlockFace face : faces){
			Block adjBlock = block.getRelative(face);
			if (adjBlock.getState() instanceof Sign){
				if (face != BlockFace.UP){
					@SuppressWarnings("deprecation")
					byte data = adjBlock.getData();
					byte north = 0x2;
					byte south = 0x3;
					byte west = 0x4;
					byte east = 0x5;
					BlockFace attached = null;
					if (data == east){
						attached = BlockFace.WEST;
					}
					else if (data == west){
						attached = BlockFace.EAST;
					}
					else if (data == north){
						attached = BlockFace.SOUTH;
					}
					else if (data == south){
						attached = BlockFace.NORTH;
					}
					if (adjBlock.getType() == Material.SIGN_POST){
						attached = BlockFace.DOWN;
					}
					if (block.getX() == adjBlock.getRelative(attached).getX() && block.getY() == 
							adjBlock.getRelative(attached).getY() && block.getZ() ==
							adjBlock.getRelative(attached).getZ()){
						return adjBlock;
					}
				}
			}
		}
		return null;
	}

	public static void damage(Player p){
		if (NMS_SUPPORT){
			try {
				Object nms_entity = getHandle.invoke(p);
				Object packet = packetPlayOutAnimation.newInstance(nms_entity, 1);

				for (Player pl : p.getWorld().getPlayers()){
					if (pl.getName().equals(p.getName())){
						continue;
					}
					if (pl.getLocation().distance(p.getLocation()) <= 50){
						Object nms_player = getHandle.invoke(pl);
						Object nms_connection = playerConnection.get(nms_player);
						sendPacket.invoke(nms_connection, packet);
					}
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static Class<?> getMCClass(String name) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String className = "net.minecraft.server." + version + name;
		return Class.forName(className);
	}

	public static Class<?> getCraftClass(String name) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String className = "org.bukkit.craftbukkit." + version + name;
		return Class.forName(className);
	}

}
