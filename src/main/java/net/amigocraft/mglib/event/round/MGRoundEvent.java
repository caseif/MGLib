package net.amigocraft.mglib.event.round;

import org.bukkit.Bukkit;

import net.amigocraft.mglib.Main;
import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.event.MGLibEvent;

/**
 * Fired when an event involving an active {@link Round minigame round} occurs.
 * @since 0.2.0
 */
public class MGRoundEvent extends MGLibEvent {

	protected Round round;

	/**
	 * Creates a new instance of this event.
	 * @param round The {@link Round} associated with this event.
	 * @since 0.2.0
	 */
	public MGRoundEvent(final Round round){
		super(round.getPlugin());
		this.round = round;
		if (Main.plugin.isEnabled()){
			Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
				public void run(){
					round.getMinigame().getLobbyManager().update(round.getArena());
				}
			}, 2L);
		}
	}

	/**
	 * Retrieves the {@link Round} associated with this event.
	 * @return the {@link Round} associated with this event.
	 * @since 0.2.0
	 */
	public Round getRound(){
		return round;
	}

}
