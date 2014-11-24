package net.amigocraft.mglib.event;

import net.amigocraft.mglib.Main;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MGLibEvent extends Event {

	private static HandlerList handlers = new HandlerList();

	protected String plugin;

	/**
	 * Creates a new instance of this event.
	 * @param plugin the name of the plugin involved in this {@link MGLibEvent}
	 * @since 0.1.0
	 */
	public MGLibEvent(String plugin){
		this.plugin = plugin;
	}

	/**
	 * Retrieves the name of the plugin involved in this {@link MGLibEvent}.
	 * @return the name of the plugin involved in this {@link MGLibEvent}
	 * @since 0.1.0
	 */
	public String getPlugin(){
		return plugin;
	}

	public HandlerList getHandlers(){
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

	/**
	 * Unsets all static objects in this class.
	 * This method will not do anything unless MGLib is in the process of disabling.
	 * @since 0.1.0
	 */
	public static void uninitialize(){
		if (Main.isDisabling()){
			handlers = null;
		}
	}

}
