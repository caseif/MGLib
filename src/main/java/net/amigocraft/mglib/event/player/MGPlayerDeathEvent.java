package net.amigocraft.mglib.event.player;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.amigocraft.mglib.api.MGPlayer;

/**
 * Thrown in place of a {@link PlayerDeathEvent} if the hooking plugin specifies such.
 * @since 0.3.0
 */
public class MGPlayerDeathEvent extends MGPlayerEvent {

	private DamageCause cause;
	
	/**
	 * Creates a new instance of this event.
	 * @param player the player who has died.
	 * @param cause the cause of death (i.e. the cause of the damage resulting in death).
	 * @since 0.3.0
	 */
	public MGPlayerDeathEvent(MGPlayer player, DamageCause cause){
		super(player);
		this.cause = cause;
	}
	
	/**
	 * Retrieves the cause of death (i.e. the cause of the damage resulting in death).
	 * @return the cause of death.
	 * @since 0.3.0
	 */
	public DamageCause getCause(){
		return cause;
	}

}
