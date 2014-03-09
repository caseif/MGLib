package net.amigocraft.mglib.event;

import net.amigocraft.mglib.round.Round;

/**
 * Fired when an MGLib round begins.
 * @since 0.1
 */
public class MinigameRoundStartEvent extends MinigameEvent {
	
	private boolean prepared;
	
	/**
	 * Creates a new instance of this {@link Event event}.
	 * @param round The {@link Round} associated with this {@link MinigameRoundStartEvent event}.
	 * @param prepared Whether the round start was on account of its preparation period ending.
	 * @since 0.1
	 */
	public MinigameRoundStartEvent(Round round, boolean prepared){
		super(round);
		this.prepared = prepared;
	}
	
	/**
	 * Creates an instance of this {@link Event event}.
	 * @param round The {@link Round} associated with this {@link MinigameRoundStartEvent}.
	 * @since 0.1
	 */
	public MinigameRoundStartEvent(Round round){
		super(round);
		this.prepared = false;
	}

	/**
	 * Gets whether the round start was on account of its preparation period ending.
	 * @return Whether the round start was on account of its preparation period ending.
	 * @since 0.1
	 */
	public boolean wasPrepared(){
		return prepared;
	}

}
