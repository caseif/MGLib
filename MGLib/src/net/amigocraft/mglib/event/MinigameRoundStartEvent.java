package net.amigocraft.mglib.event;

import net.amigocraft.mglib.round.Round;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when an MGLib round begins.
 * @since 0.1
 */
public class MinigameRoundStartEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	private String plugin;
	private Round round;
	private boolean prepared;
	
	/**
	 * Creates an instance of this event.
	 * @param round The round which has started.
	 * @param prepared Whether the round start was on account of its preparation period ending.
	 * @since 0.1
	 */
	public MinigameRoundStartEvent(Round round, boolean prepared){
		this.round = round;
		this.plugin = round.getPlugin();
	}
	
	/**
	 * Creates an instance of this event.
	 * @param round The round which has started.
	 * @since 0.1
	 */
	public MinigameRoundStartEvent(Round round){
		new MinigameRoundStartEvent(round, false);
	}
	
	/**
	 * Gets the round which has been started.
	 * @return The round which has been started.
	 * @since 0.1
	 */
	public Round getRound(){
		return round;
	}
	
	/**
	 * Gets whether the round start was on account of its preparation period ending.
	 * @return Whether the round start was on account of its preparation period ending.
	 * @since 0.1
	 */
	public boolean wasPrepared(){
		return prepared;
	}
	
	/**
	 * Gets the name of the plugin which triggered this event.
	 * @return The name of the plugin which triggered this event.
	 * @since 0.1
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

}
