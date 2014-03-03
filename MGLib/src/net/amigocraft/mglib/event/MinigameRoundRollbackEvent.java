package net.amigocraft.mglib.event;

import net.amigocraft.mglib.round.Round;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when an MGLib round is rolled back. When fired from MGLib, it always follows a MinigameRoundEndEvent.
 * @since 0.1
 */
public class MinigameRoundRollbackEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	private String plugin;
	private Round round;
	
	/**
	 * Creates an instance of this event.
	 * @param round The round which has been rolled back.
	 * @since 0.1
	 */
	public MinigameRoundRollbackEvent(Round round){
		this.round = round;
		this.plugin = round.getPlugin();
	}
	
	/**
	 * Gets the round which has been rolled back.
	 * @return The round which has been rolled back.
	 * @since 0.1
	 */
	public Round getRound(){
		return round;
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
