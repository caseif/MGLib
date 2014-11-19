package net.amigocraft.mglib.event.player;

import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.event.round.MGRoundEvent;
import org.bukkit.event.Cancellable;

/**
 * Thrown when a {@link MGPlayer player} leaves an {@link Round MGLib round}.
 * @since 0.1.0
 */
public class PlayerLeaveMinigameRoundEvent extends MGRoundEvent implements Cancellable {

	protected MGPlayer player;
	private boolean cancelled;

	/**
	 * Creates a new instance of this event.
	 * @param round  the round the player has left
	 * @param player the player involved in this event
	 * @since 0.1.0
	 */
	public PlayerLeaveMinigameRoundEvent(Round round, MGPlayer player){
		super(round);
		this.player = player;
	}

	/**
	 * Retrieves the {@link Round round} involved in this event.
	 * @return the {@link Round round} involved in this event
	 * @since 0.1.0
	 */
	public MGPlayer getPlayer(){
		return player;
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
