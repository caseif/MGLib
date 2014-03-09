package net.amigocraft.mglib.event;

import net.amigocraft.mglib.round.Round;

/**
 * Fired when an {@link Event event} involving an active {@link Round minigame round} occurs.
 * @since 0.1
 */
public class MinigameEvent extends MGLibEvent {

	protected Round round;
	
	/**
	 * Creates a new instance of this event.
	 * @param round The {@link Round} associated with this {@link MinigameEvent event}.
	 * @since 0.1
	 */
	public MinigameEvent(Round round){
		super(round.getPlugin());
		this.round = round;
	}
	
	/**
	 * Retrieves the {@link Round} associated with this {@link MinigameEvent event}.
	 * @return the {@link Round} associated with this {@link MinigameEvent event}.
	 * @since 0.1
	 */
	public Round getRound(){
		return round;
	}
	
}
