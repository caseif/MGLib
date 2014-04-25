package net.amigocraft.mglib.event.round;

import net.amigocraft.mglib.api.Round;

/**
 * Fired when an {@link Round MGLib round} is rolled back.
 * @since 0.1
 */
public class MinigameRoundRollbackEvent extends MinigameEvent {
	
	/**
	 * Creates a new instance of this event.
	 * @param round The round associated with this event.
	 * @since 0.1
	 */
	public MinigameRoundRollbackEvent(Round round){
		super(round);
	}

}