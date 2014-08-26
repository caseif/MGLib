package net.amigocraft.mglib.event.player;

import net.amigocraft.mglib.api.MGPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Thrown in place of a {@link PlayerDeathEvent} if the hooking plugin specifies such.
 *
 * @since 0.3.0
 */
public class MGPlayerDeathEvent extends MGPlayerEvent {

	private DamageCause cause;
	private Entity killer;

	/**
	 * Creates a new instance of this event.
	 *
	 * @param player the player who has died.
	 * @param cause  the cause of death (i.e. the cause of the damage resulting in death).
	 * @param killer the entity which killed this player (null may be used in the event that there is none.
	 * @since 0.3.0
	 */
	public MGPlayerDeathEvent(MGPlayer player, DamageCause cause, Entity killer){
		super(player);
		this.cause = cause;
		this.killer = killer;
	}

	/**
	 * Retrieves the cause of death (i.e. the cause of the damage resulting in death).
	 *
	 * @return the cause of death.
	 * @since 0.3.0
	 */
	public DamageCause getCause(){
		return cause;
	}

	/**
	 * Retrieves the entity which killed the player, if applicable.
	 *
	 * @return the entity which killed the player, or null if none applies.
	 * @since 0.3.0
	 */
	public Entity getKiller(){
		return killer;
	}

}
