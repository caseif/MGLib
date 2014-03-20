package net.amigocraft.mglib.event;

import net.amigocraft.mglib.api.MGPlayer;

/**
 * Fired when an {@link Event event} involving a {@link MGPlayer player} occurs.
 * @since 0.1
 */
public class MGPlayerEvent extends MGLibEvent {

	protected MGPlayer player;
	
	/**
	 * Creates a new instance of this event.
	 * @param player The {@link MGPlayer player} involved in this {@link MGPlayerEvent event}.
	 * @since 0.1
	 */
	public MGPlayerEvent(MGPlayer player){
		super(player.getPlugin());
		this.player = player;
	}
	
	/**
	 * Retrieves the {@link MGPlayer player} involved in this {@link MGPlayerEvent event}.
	 * @return the {@link MGPlayer player} involved in this {@link MGPlayerEvent event}.
	 * @since 0.1
	 */
	public MGPlayer getPlayer(){
		return player;
	}
	
}
