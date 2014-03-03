package net.amigocraft.mglib.event;

import net.amigocraft.mglib.round.Round;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when an MGLib round ends.
 * @since 0.1
 */
public class MinigameRoundEndEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	private String plugin;
	private Round round;
	private boolean outOfTime;
	
	/**
	 * Creates an instance of this event.
	 * @param round The round which has ended.
	 * @param outOfTime Whether the round ended because its timer reached 0.
	 * @since 0.1
	 */
	public MinigameRoundEndEvent(Round round, boolean outOfTime){
		this.round = round;
		this.plugin = round.getPlugin();
		this.outOfTime = outOfTime;
	}
	
	/**
	 * Gets the round which has ended.
	 * @return The round which has ended.
	 * @since 0.1
	 */
	public Round getRound(){
		return round;
	}
	
	/**
	 * Gets whether the round ended because its timer reached 0.
	 * @return Whether the round ended because its timer reached 0.
	 * @since 0.1
	 */
	public boolean wasOutOfTime(){
		return outOfTime;
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
