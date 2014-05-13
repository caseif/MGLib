package net.amigocraft.mglib.event.player;

import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.event.round.MGRoundEvent;

/**
 * Thrown when a {@link MGPlayer player} joins an {@link Round MGLib round}.
 * @since 0.1.0
 */
public class PlayerJoinMinigameRoundEvent extends MGRoundEvent {

	protected MGPlayer player;
	
	/**
	 * Creates a new instance of this event.
	 * @param round the round the player has joined.
	 * @param player the player involved in this event.
	 * @since 0.1.0
	 */
	public PlayerJoinMinigameRoundEvent(Round round, MGPlayer player){
		super(round);
		this.player = player;
	}
	
	/**
	 * Returns the {@link Round round} involved in this event.
	 * @return the {@link Round round} involved in this event.
	 * @since 0.1.0
	 */
	public Round getRound(){
		return round;
	}
	
}
