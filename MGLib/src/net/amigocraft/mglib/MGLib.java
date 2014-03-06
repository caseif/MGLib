package net.amigocraft.mglib;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * MGLib's primary (central) class.
 * @author Maxim Roncacé
 * @version 0.1-dev8
 * @since 0.1
 */
public class MGLib extends JavaPlugin {
	
	/**
	 * A list containing compatible versions of MGLib.
	 * @since 0.1
	 */
	public static List<String> approved = new ArrayList<String>();
	
	/**
	 * The current (or last, if the current version is a release) development version of MGLib.
	 * @since 0.1
	 */
	public static final int lastDev = 8;

	/**
	 * The current instance of the plugin.
	 * <br><br>
	 * This is for use within the library; please do not modify this in your plugin or everything will break.
	 * @since 0.1
	 */
	public static MGLib plugin;

	/**
	 * MGLib's logger.
	 * <br><br>
	 * This is for use within the library; please do not use this in your plugin or you'll confuse the server owner.
	 * @since 0.1
	 */
	public static Logger log;

	/**
	 * Standard {@link JavaPlugin#onEnable()} override.
	 * @since 0.1
	 */
	public void onEnable(){
		plugin = this;
		log = getLogger();
		initialize();
		if (this.getDescription().getVersion().contains("dev"))
			log.warning("You are running a development build of MGLib. As such, plugins using the library may not " +
					"work correctly.");
		log.info(this + " is now ready!");
	}

	/**
	 * Standard {@link JavaPlugin#onDisable()} override.
	 * @since 0.1
	 */
	public void onDisable(){
		plugin = null;
		log.info(this + " has been disabled!");
		log = null;
	}
	
	private static void initialize(){
		approved.add("0.1");
	}

}
