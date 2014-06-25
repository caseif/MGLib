package net.amigocraft.mglib.event.player;

import net.amigocraft.mglib.api.LobbySign;
import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.api.Round;

/**
 * Called when a player clicks a lobby sign in order to join a minigame round.
 * @since 0.3.0
 */
public class LobbyClickEvent extends MGPlayerEvent {

	private Round round;
	private LobbySign lobbySign;
	
	/**
	 * Creates a new instance of this event.
	 * @param player the player who has clicked a lobby sign.
	 * @param round the round which the lobby sign is linked to.
	 * @param lobbySign the {@link LobbySign lobby sign} which has been clicked.
	 * @since 0.3.0
	 */
	public LobbyClickEvent(MGPlayer player, Round round, LobbySign lobbySign){
		super(player);
		this.round = round;
		this.lobbySign = lobbySign;
	}
	
	/**
	 * Retrieves the round involved in this event.
	 * @return the round involved in this event.
	 * @since 0.3.0
	 */
	public Round getRound(){
		return round;
	}
	
	/**
	 * Retrieves the {@link LobbySign lobby sign} object involved in this event.
	 * @return the {@link LobbySign lobby sign} object involved in this event.
	 * @since 0.3.0
	 */
	public LobbySign getLobbySign(){
		return lobbySign;
	}
	
}
