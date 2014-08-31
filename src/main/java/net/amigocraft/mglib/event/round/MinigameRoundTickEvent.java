package net.amigocraft.mglib.event.round;

import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.api.Stage;

/**
 * Fired once per second or 20 ticks, when a round "ticks"
 */
public class MinigameRoundTickEvent extends MGRoundEvent {

	private int oldTime;
	private boolean stageChange;

	/**
	 * You probably shouldn't call this from your plugin. Let MGLib handle that.
	 * @param round       the round which has ticked.
	 * @param oldTime     the round time before the tick.
	 * @param stageChange whether the tick resulted in a stage change (e.g. from {@link Stage#PREPARING} to {@link
	 *                    Stage#PLAYING}.
	 * @since 0.1.0
	 */
	public MinigameRoundTickEvent(Round round, int oldTime, boolean stageChange){
		super(round);
		this.oldTime = oldTime;
		this.stageChange = stageChange;
	}

	/**
	 * Returns the round time before the tick.
	 * @return the time remaining in the round before the tick.
	 * @since 0.1.0
	 */
	public int getTimeBefore(){
		return oldTime;
	}

	/**
	 * Returns whether the tick resulted in a stage change for the round (e.g. from "PREPARING" to "PLAYING").
	 * @return whether the tick resulted in a stage change for the round.
	 * @since 0.1.0
	 */
	public boolean isStageChange(){
		return stageChange;
	}

}
