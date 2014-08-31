package net.amigocraft.mglib.event.round;

import net.amigocraft.mglib.api.Round;
import org.bukkit.event.Cancellable;

/**
 * Fired when an {@link Round MGLib round}'s preparation period begins.
 * @since 0.1.0
 */
public class MinigameRoundPrepareEvent extends MGRoundEvent implements Cancellable {

	private boolean cancelled;

	/**
	 * Creates a new instance of this event.
	 * @param round The round associated with this event.
	 * @since 0.1.0
	 */
	public MinigameRoundPrepareEvent(Round round){
		super(round);
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
