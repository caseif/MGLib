package net.amigocraft.mglib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MGLibEvent extends Event {

	private static HandlerList handlers = new HandlerList();

	protected String plugin;

	/**
	 * Creates a new instance of this event.
	 * @param plugin The name of the plugin involved in this {@link MGLibEvent}.
	 * @since 0.1.0
	 */
	public MGLibEvent(String plugin){
		this.plugin = plugin;
	}

	/**
	 * Retrieves the name of the plugin involved in this {@link MGLibEvent}.
	 * @return the name of the plugin involved in this {@link MGLibEvent}.
	 * @since 0.1.0
	 */
	public String getPlugin(){
		return plugin;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Unsets all static variables in this class. <b>Please do not call this from your plugin unless you want to ruin
	 * everything for everyone.</b>
	 */
	public static void uninitialize(){
		handlers = null;
	}

}
