package net.amigocraft.mglib.event.round;

import net.amigocraft.mglib.api.Round;

/**
 * Fired when an {@link Round MGLib round}'s preparation period begins.
 * @since 0.1.0
 */
public class MinigameRoundPrepareEvent extends MinigameEvent {
	
	/**
	 * Creates a new instance of this event.
	 * @param round The round associated with this event.
	 * @since 0.1.0
	 */
	public MinigameRoundPrepareEvent(Round round){
		super(round);
	}

}
