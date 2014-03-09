package net.amigocraft.mglib.event;

import net.amigocraft.mglib.round.Round;

/**
 * Fired when an {@link Round MGLib round} is rolled back. When fired from MGLib, it always follows a {@link MinigameRoundEndEvent}.
 * @since 0.1
 */
public class MinigameRoundRollbackEvent extends MinigameEvent {
	
	/**
	 * Creates a new instance of this {@link Event event}.
	 * @param round The {@link Round} associated with this {@link MinigameRoundRollbackEvent event}.
	 * @since 0.1
	 */
	public MinigameRoundRollbackEvent(Round round){
		super(round);
	}

}
