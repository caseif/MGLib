package net.amigocraft.mglib.event.player;

import org.bukkit.event.entity.PlayerDeathEvent;

import net.amigocraft.mglib.api.MGPlayer;

/**
 * Thrown in place of a {@link PlayerDeathEvent} if the hooking plugin specifies such.
 * @since 0.3.0
 */
public class MGPlayerDeathEvent extends MGPlayerEvent {

	/**
	 * Creates a new instance of this event.
	 * @param player the player who has "died."
	 * @since 0.3.0
	 */
	public MGPlayerDeathEvent(MGPlayer player){
		super(player);
	}

}
