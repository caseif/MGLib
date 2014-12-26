package net.amigocraft.mglib;

import net.amigocraft.mglib.api.LogLevel;
import net.amigocraft.mglib.api.MGYamlConfiguration;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.event.MGLibEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods for use within MGLib. Developers are advised not to use them in a separate plugin, since this isn't
 * an API class and as such is subject to removals and refactors.
 * @since 0.1.0
 */
public class MGUtil {

	private static String VERSION_STRING;

	private static boolean NMS_SUPPORT = true;
	private static Constructor<?> packetPlayOutPlayerInfo;
	private static Field pingField;
	public static Method getHandle;
	public static Field playerConnection;
	public static Method sendPacket;

	private static Method getOnlinePlayers;
	public static boolean newOnlinePlayersMethod = false;

	public static Object clientCommandPacket;

	public static GameMode SPECTATE_GAMEMODE = null;

	static {
		try {
			getOnlinePlayers = Bukkit.class.getMethod("getOnlinePlayers");
			if (getOnlinePlayers.getReturnType() == Collection.class){
				newOnlinePlayersMethod = true;
			}

			String[] array = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",");
			VERSION_STRING = array.length == 4 ? array[3] + "." : "";

			//get the constructor of the packet
			try {
				packetPlayOutPlayerInfo = getMCClass("PacketPlayOutPlayerInfo")
						.getConstructor(String.class, boolean.class, int.class); // 1.7.x and above
				Object PERFORM_RESPAWN = Enum.valueOf(
						(Class<? extends Enum>)MGUtil.getMCClass("EnumClientCommand"), "PERFORM_RESPAWN"
				);
				clientCommandPacket = getMCClass("PacketPlayInClientCommand")
						.getConstructor(PERFORM_RESPAWN.getClass())
						.newInstance(PERFORM_RESPAWN);
			}
			catch (ClassNotFoundException ex){
				packetPlayOutPlayerInfo = getMCClass("Packet201PlayerInfo")
						.getConstructor(String.class, boolean.class, int.class); // 1.6.x and below
				clientCommandPacket = MGUtil.getMCClass("Packet205ClientCommand").getConstructor().newInstance();
				clientCommandPacket.getClass().getDeclaredField("a").set(clientCommandPacket, 1);
			}
			// field for player ping
			pingField = getNMSClass("EntityPlayer").getDeclaredField("ping");
			// get method for recieving CraftPlayer's EntityPlayer
			getHandle = getCraftClass("entity.CraftPlayer").getMethod("getHandle");
			// get the PlayerConnection of the EntityPlayer
			playerConnection = getMCClass("EntityPlayer").getDeclaredField("playerConnection");
			// method to send the packet
			sendPacket = getMCClass("PlayerConnection").getMethod("sendPacket", getMCClass("Packet"));
		}
		catch (Exception e){
			Main.log("Cannot access NMS codebase! Packet manipulation disabled.", LogLevel.WARNING);
			NMS_SUPPORT = false;
		}

		try {
			SPECTATE_GAMEMODE = GameMode.valueOf("SPECTATOR");
		}
		catch (IllegalArgumentException ex1){
			try {
				SPECTATE_GAMEMODE = GameMode.valueOf("SPECTATING");
			}
			catch (IllegalArgumentException ex2){
				try {
					SPECTATE_GAMEMODE = GameMode.valueOf("SPECTATE");
				}
				catch (IllegalArgumentException ex3){} // guess we don't have spectator support then
			}
		}
	}

	/**
	 * Loads and returns the given plugin's arenas.yml file.
	 * <br><br>
	 * <strong>Note:</strong> arena names are converted to lower case when saved. The custom class will automatically take this
	 * into account should you choose to use it. However, if you store the returned object as a vanilla
	 * {@link YamlConfiguration}, you will need to do so yourself, as the methods will not be overridden.
	 * @param plugin The plugin to load the YAML file from.
	 * @return The loaded {@link YamlConfiguration} object.
	 * @since 0.1.0
	 */
	public static MGYamlConfiguration loadArenaYaml(String plugin){
		JavaPlugin jp = Minigame.getMinigameInstance(plugin).getPlugin();
		File f = new File(jp.getDataFolder(), "arenas.yml");
		try {
			if (!jp.getDataFolder().exists()){
				jp.getDataFolder().mkdirs();
			}
			if (!f.exists()){
				f.createNewFile();
			}
			MGYamlConfiguration y = new MGYamlConfiguration();
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
	 * @param plugin the plugin to save the given {@link YamlConfiguration} to
	 * @param y      the {@link YamlConfiguration} to save
	 */
	public static void saveArenaYaml(String plugin, YamlConfiguration y){
		JavaPlugin jp = Minigame.getMinigameInstance(plugin).getPlugin();
		File f = new File(jp.getDataFolder(), "arenas.yml");
		try {
			if (!f.exists()){
				f.createNewFile();
			}
			y.save(f);
		}
		catch (Exception ex){
			ex.printStackTrace();
			Main.log.severe("An exception occurred while saving arena data for plugin " + plugin);
		}
	}

	/**
	 * Determines whether the provided string can be parsed to an integer.
	 * @param s the string to check
	 * @return whether the provided string can be parsed to an integer
	 */
	public static boolean isInteger(String s){
		try {
			Integer.parseInt(s);
			return true;
		}
		catch (NumberFormatException ex){
			return false;
		}
	}

	/**
	 * Retrieves worlds registered with MGLib's event listener.
	 * @return worlds registered with MGLib's event listener
	 * @since 0.1.0
	 */
	public static List<String> getWorlds(){
		List<String> worlds = new ArrayList<String>();
		for (List<String> l : MGListener.worlds.values()){
			for (String w : l){
				if (!worlds.contains(w)){
					worlds.add(w);
				}
			}
		}
		return worlds;
	}

	/**
	 * Retrieves worlds registered with MGLib's event listener for the given plugin.
	 * @param plugin the plugin to retrieve worlds for
	 * @return worlds registered with MGLib's event listener for the given plugin
	 * @since 0.2.0
	 */
	public static List<String> getWorlds(String plugin){
		if (MGListener.worlds.containsKey(plugin)){
			return MGListener.worlds.get(plugin);
		}
		else {
			List<String> l = new ArrayList<String>();
			MGListener.worlds.put(plugin, l);
			return l;
		}
	}

	/**
	 * Logs the given message if verbose logging is enabled.
	 * @param message the message to log
	 * @param prefix  the prefix to place in front of the message. This will automatically be placed within brackets
	 * @param level   the {@link LogLevel level} at which to log the message
	 * @since 0.3.0
	 */
	public static void log(String message, String prefix, LogLevel level){
		if (Main.LOGGING_LEVEL.compareTo(level) >= 0){
			System.out.println("[" + level.toString() + "][" + prefix + "] " + message);
		}
	}

	/**
	 * Calls an event, but sends it only to the appropriate plugin.
	 * <strong>Please do not call this from your pluginv unless you are aware of the implications.</strong>
	 * @param event the event to call
	 * @since 0.3.0
	 */
	public static void callEvent(MGLibEvent event){
		HandlerList hl = event.getHandlers();
		for (RegisteredListener rl : hl.getRegisteredListeners()){
			if (rl.getPlugin().getName().equals(event.getPlugin()) || rl.getPlugin().getName().equals("MGLib")){
				try {
					rl.callEvent(event);
				}
				catch (EventException ex){
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Retrieves the sign attached to a given block, or null if ones does not exist.
	 * @param block the block to check for an attached sign
	 * @return the sign attached to a given block, or null if ones does not exist
	 */
	public static Block getAttachedSign(Block block){
		BlockFace[] faces = new BlockFace[]{
				BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP
		};
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
					if (block.getX() == adjBlock.getRelative(attached).getX() && block.getY() == adjBlock.getRelative(attached).getY() && block.getZ() == adjBlock.getRelative(attached).getZ()){
						return adjBlock;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Retrieves a class by the given name from the package <code>net.minecraft.server</code>.
	 * @param name the class to retrieve
	 * @return the class object from the package <code>net.minecraft.server</code>
	 * @throws ClassNotFoundException if the class does not exist in the package
	 */
	public static Class<?> getMCClass(String name) throws ClassNotFoundException{
		String className = "net.minecraft.server." + VERSION_STRING + name;
		return Class.forName(className);
	}

	/**
	 * Retrieves a class by the given name from the package <code>net.minecraft.server</code>.
	 * @param name the class to retrieve
	 * @return the class object from the package <code>net.minecraft.server</code>
	 * @throws ClassNotFoundException if the class does not exist in the package
	 */
	public static Class<?> getNMSClass(String name) throws ClassNotFoundException{
		return getMCClass(name);
	}

	/**
	 * Retrieves a class by the given name from the package <code>org.bukkit.craftbukkit</code>.
	 * @param name the class to retrieve
	 * @return the class object from the package <code>org.bukkit.craftbukkit</code>
	 * @throws ClassNotFoundException if the class does not exist in the package
	 */
	public static Class<?> getCraftClass(String name) throws ClassNotFoundException{
		String className = "org.bukkit.craftbukkit." + VERSION_STRING + name;
		return Class.forName(className);
	}

	/**
	 * Determines the environment of the given world based on its folder structure.
	 * @param world the name of the world to determine the environment of
	 * @return the environment of the given world
	 * @since 0.3.0
	 */
	public static Environment getEnvironment(String world){
		File worldFolder = new File(Bukkit.getWorldContainer(), world);
		if (worldFolder.exists()){
			boolean nether = false;
			boolean end = false;
			for (File f : worldFolder.listFiles()){
				if (f.getName().equals("region")){
					return Environment.NORMAL;
				}
				else if (f.getName().equals("DIM1")){
					return Environment.THE_END;
				}
				else if (f.getName().equals("DIM-1")){
					return Environment.NETHER;
				}
			}
		}
		return null;
	}

	/**
	 * Sends a PlayerInfoPacket to the specified {@link Player}.
	 * @param recipient the player to receive the packet
	 * @param subject   the player addressed by the packet
	 * @return whether the packet was successfully sent
	 * @since 0.3.0
	 */
	public static boolean sendPlayerInfoPacket(final Player recipient, final Player subject){
		if (NMS_SUPPORT){
			try {
				int ping = (Integer)pingField.get(getHandle.invoke(subject));
				Object packet = packetPlayOutPlayerInfo.newInstance(subject.getName(), true, (Integer)ping);
				sendPacket.invoke(
						playerConnection.get(getHandle.invoke(recipient)), packet);
				return true;
			}
			catch (Exception ex){ // just in case
				ex.printStackTrace();
				Main.log("Failed to send player info packet!",
						LogLevel.WARNING);
			}
		}
		return false;
	}

	/**
	 * Deletes a folder recursively.
	 * @param folder the folder to delete
	 * @since 0.3.0
	 */
	public static void deleteFolder(File folder){
		for (File f : folder.listFiles()){
			if (f.isDirectory())
				deleteFolder(f);
			else
				f.delete();
		}
	}

	/**
	 * Version-independent getOnlinePlayers() method.
	 * @return a list of online players in the form of a {@link Player} array or {@link List}, depending on the server software version.
	 * @since 0.3.1
	 */
	public static Object getOnlinePlayers(){
		try {
			return getOnlinePlayers.invoke(null);
		}
		catch (IllegalAccessException ex){
			ex.printStackTrace();
			Main.log("Failed to get online player list!", LogLevel.SEVERE);
		}
		catch (InvocationTargetException ex){
			ex.printStackTrace();
			Main.log("Failed to get online player list!", LogLevel.SEVERE);
		}
		return null;
	}

	/**
	 * Unsets all static objects in this class.
	 * This method will not do anything unless MGLib is in the process of disabling.
	 * @since 0.3.1
	 */
	public static void uninitialize(){
		if (Main.isDisabling()){
			packetPlayOutPlayerInfo = null;
			pingField = null;
			getHandle = null;
			playerConnection = null;
			sendPacket = null;
		}
	}

}
