package net.amigocraft.mglib.event.round;

import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.api.Stage;

/**
 * Fired when the stage of an {@link net.amigocraft.mglib.api.Round MGLib round} changes.
 *
 * @since 0.3.0
 */
public class MinigameRoundStageChangeEvent extends MGRoundEvent {

	private Stage before;
	private Stage after;

	/**
	 * Creates a new instance of this event.
	 *
	 * @param round  The round associated with this event.
	 * @param before the stage before the change.
	 * @param after  the stage after the change.
	 * @since 0.3.0
	 */
	public MinigameRoundStageChangeEvent(Round round, Stage before, Stage after){
		super(round);
		this.before = before;
		this.after = after;
	}

	/**
	 * Gets the stage of the round before the event.
	 *
	 * @return the stage of the round before the event.
	 * @since 0.3.0
	 */
	public Stage getStageBefore(){
		return before;
	}

	/**
	 * Gets the stage of the round after the event.
	 *
	 * @return the stage of the round after the event.
	 * @since 0.3.0
	 */
	public Stage getStageAfter(){
		return after;
	}

}
