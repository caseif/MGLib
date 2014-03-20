package net.amigocraft.mglib;

import java.io.File;
import java.io.IOException;

import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.api.Round;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class RollbackManager {

	private static boolean IMMEDIATE_LOGGING = true;
	private static File f = new File(MGLib.plugin.getDataFolder(), "rollback.yml"); //TODO: idiot-proof
	private static YamlConfiguration y = null;

	public static void initialize() throws IOException, InvalidConfigurationException {
		y.load(f);
		IMMEDIATE_LOGGING = MGLib.plugin.getConfig().getBoolean("immediate-logging");
	}

	/**
	 * Logs a block change.
	 * @param block The block which was changed.
	 * @param origType The original type of the block
	 * @param arena The arena in which the block is contained
	 */
	public static void logBlockChange(Block block, String origType, String arena){
		ConfigurationSection cs = y.getConfigurationSection(arena);
		cs = cs.getConfigurationSection("blockChanges." +
				block.getX() + "," + block.getY() + "," + block.getZ());
		cs.set("world", block.getWorld().getName());
		if (!cs.isSet("type")) // make sure it hasn't already been changed
			cs.set("type", origType);
		if (IMMEDIATE_LOGGING){
			try {
				y.save(f);
			}
			catch (Exception ex){
				MGLib.log.severe("An exception occurred while saving data for arena " + arena);
			}
		}
	}

	/**
	 * Logs an inventory change
	 * @param inventory The inventory to log
	 * @param block The block containing the inventory
	 * @param arena The arena in which the block is contained
	 * @since 0.1
	 */
	public static void logInventoryChange(Inventory inventory, Block block, String arena){
		ConfigurationSection cs = y.getConfigurationSection(arena);
		cs = cs.getConfigurationSection("inventoryChanges." +
				block.getX() + "," + block.getY() + "," + block.getZ());
		cs.set("world", block.getWorld().getName());
		if (!cs.isSet("inventory")) // make sure it hasn't already been changed
			cs.set("inventory", InventorySerializer.InventoryToString(inventory));
		if (IMMEDIATE_LOGGING){
			try {
				y.save(f);
			}
			catch (Exception ex){
				MGLib.log.severe("An exception occurred while saving data for arena " + arena);
			}
		}
	}

	/**
	 * Rolls back the given arena.
	 * <br><br>
	 * This method <b>should not</b> be called from your plugin unless you understand the implications.
	 * @param arena The arena to roll back.
	 * @since 0.1
	 */
	public static void rollback(String arena){
		Round r = null;
		for (Minigame mg : Minigame.getMinigameInstances())
			r = mg.getRounds().get(arena);
		if (r != null)
			r.setStage(Stage.RESETTING);
		ConfigurationSection cs = y.getConfigurationSection(arena + ".blockChanges");
		for (String k : cs.getKeys(false)){
			String[] coords = k.split(",");
			double x = Double.NaN, y = Double.NaN, z = Double.NaN;
			if (MGUtil.isInteger(coords[0]))
				x = Integer.parseInt(coords[0]);
			if (MGUtil.isInteger(coords[1]))
				y = Integer.parseInt(coords[1]);
			if (MGUtil.isInteger(coords[2]))
				z = Integer.parseInt(coords[2]);
			World w = Bukkit.getWorld(cs.getString(k + ".world"));
			if (w != null && x != Double.NaN && y != Double.NaN && z != Double.NaN){
				Location l = new Location(w, x, y, z);
				l.getBlock().setType(Material.getMaterial(k + ".type"));
			}
		}
		ConfigurationSection cs2 = y.getConfigurationSection(arena + ".inventoryChanges");
		for (String k : cs2.getKeys(false)){
			String[] coords = k.split(",");
			double x = Double.NaN, y = Double.NaN, z = Double.NaN;
			if (MGUtil.isInteger(coords[0]))
				x = Integer.parseInt(coords[0]);
			if (MGUtil.isInteger(coords[1]))
				y = Integer.parseInt(coords[1]);
			if (MGUtil.isInteger(coords[2]))
				z = Integer.parseInt(coords[2]);
			World w = Bukkit.getWorld(cs2.getString(k + ".world"));
			if (w != null && x != Double.NaN && y != Double.NaN && z != Double.NaN){
				Location l = new Location(w, x, y, z);
				if (l.getBlock() instanceof InventoryHolder)
					((InventoryHolder)l.getBlock()).getInventory().setContents(InventorySerializer.StringToInventory(
							cs2.getString(k + ".inventory")).getContents());
			}
		}
		y.set(arena, null);
		try {
			y.save(f);
		}
		catch (Exception ex){
			ex.printStackTrace();
			MGLib.log.severe("An exception occurred while saving data for arena " + arena);
		}
		if (r != null)
			r.setStage(Stage.WAITING);
	}
	
	/**
	 * Rolls back worlds which have not been rolled back due to a crash or unclean shutdown
	 * @since 0.1
	 */
	public static void checkRollbacks(){
		for (String k : y.getKeys(false))
			rollback(k);
	}

}
