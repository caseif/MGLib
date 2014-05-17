package net.amigocraft.mglib.event.player;

import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.api.Round;

/**
 * Thrown when a {@link MGPlayer player} is marked as dead.
 * @since 0.2.0
 */
public class MGPlayerSpectateEvent extends MGPlayerEvent {

	protected Round round;
	
	/**
	 * Creates a new instance of this event.
	 * @param round the round the player has joined.
	 * @param player the player involved in this event.
	 * @since 0.2.0
	 */
	public MGPlayerSpectateEvent(Round round, MGPlayer player){
		super(player);
		this.round = round;
	}
	
	/**
	 * Returns the {@link Round round} involved in this event.
	 * @return the {@link Round round} involved in this event.
	 * @since 0.2.0
	 */
	public Round getRound(){
		return round;
	}
	
}
