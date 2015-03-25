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
package net.amigocraft.mglib.util;

import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.Main;
import net.amigocraft.mglib.api.LogLevel;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * Utility methods for accessing NMS resources. Developers are strongly advised
 * not to use this class within their plugin as it is highly subject to
 * non-backwards compatible modifications.
 *
 * @since 0.4.0
 */
public class NmsUtil {

	private static final String VERSION_STRING;
	private static final boolean NMS_SUPPORT;
	public static final boolean SPECTATOR_SUPPORT;

	// general classes for sending packets
	public static Method craftPlayer_getHandle;
	public static Field playerConnection;
	public static Method playerConnection_sendPacket;
	public static Method playerConnection_a_packetPlayInClientCommand;

	// for respawning players automatically
	public static Object clientCommandPacketInstance;

	// for removing players from the player list
	private static Constructor<?> packetPlayOutPlayerInfo;
	private static Object enumPlayerInfoAction_addPlayer;
	private static Object enumPlayerInfoAction_removePlayer;
	private static Field entityPlayer_ping;

	private static Method getOnlinePlayers;
	public static boolean newOnlinePlayersMethod = false;

	static {
		SPECTATOR_SUPPORT = GameMode.valueOf("SPECTATOR") != null;
		boolean nmsException = false;
		String[] array = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
		VERSION_STRING = array.length == 4 ? array[3] + "." : "";
		try {
			getOnlinePlayers = Bukkit.class.getMethod("getOnlinePlayers");
			if (getOnlinePlayers.getReturnType() == Collection.class) {
				newOnlinePlayersMethod = true;
			}

			// field for player ping
			entityPlayer_ping = getNmsClass("EntityPlayer").getDeclaredField("ping");
			// get method for recieving CraftPlayer's EntityPlayer
			craftPlayer_getHandle = getCraftClass("entity.CraftPlayer").getMethod("getHandle");
			// get the PlayerConnection of the EntityPlayer
			playerConnection = getNmsClass("EntityPlayer").getDeclaredField("playerConnection");
			// method to send the packet
			playerConnection_sendPacket = getNmsClass("PlayerConnection").getMethod("sendPacket", getNmsClass("Packet"));
			playerConnection_a_packetPlayInClientCommand = getNmsClass("PlayerConnection")
					.getMethod("a", getNmsClass("PacketPlayInClientCommand"));

			//get the constructor of the packet
			try {
				try {
					// 1.8.x and above
					@SuppressWarnings("unchecked")
					Class<? extends Enum> enumPlayerInfoAction =
							(Class<? extends Enum>)getNmsClass("EnumPlayerInfoAction");
					enumPlayerInfoAction_addPlayer = Enum.valueOf(enumPlayerInfoAction, "ADD_PLAYER");
					enumPlayerInfoAction_removePlayer = Enum.valueOf(enumPlayerInfoAction, "REMOVE_PLAYER");
					packetPlayOutPlayerInfo = getNmsClass("PacketPlayOutPlayerInfo").getConstructor(
							enumPlayerInfoAction_addPlayer.getClass(),
							Array.newInstance(craftPlayer_getHandle.getReturnType(), 0).getClass()
					);
				}
				catch (ClassNotFoundException ex1) {
					try {
						// 1.7.x
						packetPlayOutPlayerInfo = getNmsClass("PacketPlayOutPlayerInfo").getConstructor(
								String.class, boolean.class, int.class
						);
					}
					catch (ClassNotFoundException ex2) {
						// 1.6.x and below
						packetPlayOutPlayerInfo = getNmsClass("Packet201PlayerInfo").getConstructor(
								String.class, boolean.class, int.class
						);
					}
				}
			}
			catch (ClassNotFoundException ex) {
				Main.log(Main.locale.getMessage("plugin.alert.nms.player-info").localize(), LogLevel.WARNING);
			}
			try {
				try {
					@SuppressWarnings("unchecked")
					Object performRespawn = Enum.valueOf(
							(Class<? extends Enum>)getNmsClass("EnumClientCommand"), "PERFORM_RESPAWN"
					);
					clientCommandPacketInstance = getNmsClass("PacketPlayInClientCommand")
							.getConstructor(performRespawn.getClass())
							.newInstance(performRespawn);
				}
				catch (ClassNotFoundException ex) {
					clientCommandPacketInstance = getNmsClass("Packet205ClientCommand").getConstructor().newInstance();
					clientCommandPacketInstance.getClass().getDeclaredField("a").set(clientCommandPacketInstance, 1);
				}
			}
			catch (ClassNotFoundException ex) {
				Main.log(Main.locale.getMessage("plugin.alert.nms.client-command").localize(), LogLevel.WARNING);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Main.log(Main.locale.getMessage("plugin.alert.nms.fail").localize(), LogLevel.WARNING);
			nmsException = true;
		}

		NMS_SUPPORT = !nmsException;
	}

	/**
	 * Retrieves a class by the given name from the package
	 * <code>net.minecraft.server</code>.
	 *
	 * @param name the class to retrieve
	 * @return the class object from the package
	 * <code>net.minecraft.server</code>
	 * @throws ClassNotFoundException if the class does not exist in the
	 * package
	 */
	public static Class<?> getNmsClass(String name) throws ClassNotFoundException {
		String className = "net.minecraft.server." + VERSION_STRING + name;
		return Class.forName(className);
	}

	/**
	 * Retrieves a class by the given name from the package
	 * <code>org.bukkit.craftbukkit</code>.
	 *
	 * @param name the class to retrieve
	 * @return the class object from the package
	 * <code>org.bukkit.craftbukkit</code>
	 * @throws ClassNotFoundException if the class does not exist in the
	 * package
	 */
	public static Class<?> getCraftClass(String name) throws ClassNotFoundException {
		String className = "org.bukkit.craftbukkit." + VERSION_STRING + name;
		return Class.forName(className);
	}

	/**
	 * Adds each player from <code>subjects</code> to <code>recipient</code>'s
	 * tablist.
	 *
	 * @param recipient the player whose tablist will be modified
	 * @param subjects  the players added to the tablist
	 * @return whether the info packet was successfully sent
	 * @since 0.4.0
	 */
	public static boolean addPlayers(Player recipient, Collection<? extends Player> subjects) {
		return sendPlayerInfoPacket(recipient, subjects, true);
	}
	/**
	 * Adds <code>subject</code> to <code>recipient</code>'s tablist.
	 *
	 * @param recipient the player whose tablist will be modified
	 * @param subject   the player added to the tablist
	 * @return whether the info packet was successfully sent
	 * @since 0.4.0
	 */
	public static boolean addPlayer(Player recipient, Player subject) {
		return addPlayers(recipient, Arrays.asList(subject));
	}

	/**
	 * Removes each player from <code>subjects</code> from
	 * <code>recipient</code>'s tablist.
	 *
	 * @param recipient the player whose tablist will be modified
	 * @param subjects  the players removed from the tablist
	 * @return whether the info packet was successfully sent
	 * @since 0.4.0
	 */
	public static boolean removePlayers(Player recipient, Collection<? extends Player> subjects) {
		return sendPlayerInfoPacket(recipient, subjects, false);
	}

	/**
	 * Removes <code>subject</code> from <code>recipient</code>'s tablist.
	 *
	 * @param recipient the player whose tablist will be modified
	 * @param subject   the player removed from the tablist
	 * @return whether the info packet was successfully sent
	 * @since 0.4.0
	 */
	public static boolean removePlayer(Player recipient, Player subject) {
		return removePlayers(recipient, Arrays.asList(subject));
	}

	private static boolean sendPlayerInfoPacket(final Player recipient, final Collection<? extends Player> subjects, boolean visible) {
		if (NMS_SUPPORT) {
			try {
				if ((visible && enumPlayerInfoAction_addPlayer != null) ||
						(!visible && enumPlayerInfoAction_removePlayer != null)) {
					Object array = Array.newInstance(craftPlayer_getHandle.getReturnType(), subjects.size());
					int i = 0;
					for (Player subject : subjects) {
						Array.set(array, i, craftPlayer_getHandle.invoke(subject));
						++i;
					}
					Object packet = packetPlayOutPlayerInfo.newInstance(
							visible ? enumPlayerInfoAction_addPlayer : enumPlayerInfoAction_removePlayer,
							array
					);
					playerConnection_sendPacket.invoke(
							playerConnection.get(craftPlayer_getHandle.invoke(recipient)), packet);
				}
				else {
					for (Player subject : subjects) {
						if (subject == null) {
							continue;
						}
						int ping = (Integer)entityPlayer_ping.get(craftPlayer_getHandle.invoke(subject));
						Object packet = packetPlayOutPlayerInfo.newInstance(subject.getName(), visible, ping);
						playerConnection_sendPacket.invoke(
								playerConnection.get(craftPlayer_getHandle.invoke(recipient)), packet);
					}
				}
				return true;
			}
			catch (Exception ex) { // just in case
				ex.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Sends a PlayInClientCommand packet to the given player.
	 *
	 * @param player the {@link Player} to send the packet to
	 * @throws Exception if an exception occurs while sending the packet
	 */
	public static void sendRespawnPacket(Player player) throws Exception {
		Object nmsPlayer = NmsUtil.craftPlayer_getHandle.invoke(player);
		Object conn = NmsUtil.playerConnection.get(nmsPlayer);
		NmsUtil.playerConnection_a_packetPlayInClientCommand.invoke(conn, NmsUtil.clientCommandPacketInstance);
	}

	/**
	 * Version-independent getOnlinePlayers() method.
	 *
	 * @return a list of online players
	 * @since 0.4.0
	 */
	@SuppressWarnings("unchecked")
	public static Collection<? extends Player> getOnlinePlayers() {
		try {
			if (newOnlinePlayersMethod) {
				return (Collection<? extends Player>)getOnlinePlayers.invoke(null);
			}
			else {
				return Arrays.asList((Player[])getOnlinePlayers.invoke(null));
			}
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
			Main.log(Main.locale.getMessage("plugin.alert.nms.online-players").localize(), LogLevel.SEVERE);
		}
		catch (InvocationTargetException ex) {
			ex.printStackTrace();
			Main.log(Main.locale.getMessage("plugin.alert.nms.online-players").localize(), LogLevel.SEVERE);
		}
		return null;
	}

	/**
	 * Unsets all static objects in this class.
	 *
	 * @throws UnsupportedOperationException if MGLib is not currently disabling
	 *
	 * @since 0.4.0
	 */
	public static void uninitialize() {
		MGUtil.verifyDisablingStatus();
		packetPlayOutPlayerInfo = null;
		entityPlayer_ping = null;
		craftPlayer_getHandle = null;
		playerConnection = null;
		playerConnection_sendPacket = null;
	}
}
