package net.amigocraft.mglib.event.round;

import net.amigocraft.mglib.api.Round;

/**
 * Fired when an {@link Round MGLib round} ends.
 * @since 0.1
 */
public class MinigameRoundEndEvent extends MinigameEvent {
	private boolean outOfTime;
	
	/**
	 * Creates a new instance of this event.
	 * @param round The {@link Round} which has ended.
	 * @param outOfTime Whether the round ended because its timer reached 0.
	 * @since 0.1
	 */
	public MinigameRoundEndEvent(Round round, boolean outOfTime){
		super(round);
		this.outOfTime = outOfTime;
	}

	/**
	 * Gets whether the round ended because its timer reached 0.
	 * @return Whether the round ended because its timer reached 0.
	 * @since 0.1
	 */
	public boolean wasOutOfTime(){
		return outOfTime;
	}


}
