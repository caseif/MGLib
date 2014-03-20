package net.amigocraft.mglib;

import java.io.File;
import java.io.IOException;

import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.event.MinigameRoundRollbackEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class RollbackManager {

	private static boolean IMMEDIATE_LOGGING = true;
	private static File f = new File(MGLib.plugin.getDataFolder(), "rollback.yml"); //TODO: idiot-proof
	private static YamlConfiguration y = null;

	public static void initialize() throws IOException, InvalidConfigurationException {
		y.load(f);
		IMMEDIATE_LOGGING = MGLib.plugin.getConfig().getBoolean("immediate-logging");
	}

	public static void logPhysical(Block block, String origType, String arena){
		ConfigurationSection cs = y.getConfigurationSection(arena);
		cs = cs.getConfigurationSection("blockchanges." +
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

	//TODO: Inventory stuff

	/**
	 * Rolls back the given arena.
	 * <br><br>
	 * This method <b>should not</b> be called from your plugin unless you understand the implications.
	 * @param arena The arena to roll back.
	 */
	public static void rollback(Round round){
		Bukkit.getPluginManager().callEvent(new MinigameRoundRollbackEvent(round));
		ConfigurationSection cs = y.getConfigurationSection(round.getArena() + ".blockchanges");
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
		try {
			y.save(f);
		}
		catch (Exception ex){
			ex.printStackTrace();
			MGLib.log.severe("An exception occurred while saving data for arena " + round.getArena() + " for plugin " +
			round.getPlugin());
		}
	}

}
