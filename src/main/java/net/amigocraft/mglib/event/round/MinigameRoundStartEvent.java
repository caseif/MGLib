package net.amigocraft.mglib.event.round;

import net.amigocraft.mglib.api.Round;
import org.bukkit.event.Cancellable;

/**
 * Fired when an MGLib round begins.
 * @since 0.1.0
 */
public class MinigameRoundStartEvent extends MGRoundEvent implements Cancellable {

	private boolean prepared;
	private boolean cancelled;

	/**
	 * Creates a new instance of this event.
	 * @param round    the {@link Round} associated with this event
	 * @param prepared whether the round start was on account of its preparation period ending
	 * @since 0.1.0
	 */
	public MinigameRoundStartEvent(Round round, boolean prepared){
		super(round);
		this.prepared = prepared;
	}

	/**
	 * Creates an instance of this event.
	 * @param round the {@link Round} associated with this {@link org.bukkit.event.Event}
	 * @since 0.1.0
	 */
	public MinigameRoundStartEvent(Round round){
		super(round);
		this.prepared = false;
	}

	/**
	 * Gets whether the round start was on account of its preparation period ending.
	 * @return whether the round start was on account of its preparation period ending
	 * @since 0.1.0
	 */
	public boolean wasPrepared(){
		return prepared;
	}

	@Override
	public boolean isCancelled(){
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel){
		this.cancelled = cancel;
	}

}
