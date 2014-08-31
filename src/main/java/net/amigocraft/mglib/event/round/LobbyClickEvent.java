package net.amigocraft.mglib.event.round;

import net.amigocraft.mglib.api.LobbySign;
import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.event.player.PlayerJoinMinigameRoundEvent;
import net.amigocraft.mglib.misc.JoinResult;
import org.bukkit.event.Cancellable;

/**
 * Called when a player clicks a lobby sign in order to join a minigame round. <br><br> <strong>Note:</strong> If you
 * wish to cancel this event, you must instead listen to the {@link PlayerJoinMinigameRoundEvent} and cancel that.
 * @since 0.3.0
 */
public class LobbyClickEvent extends MGRoundEvent implements Cancellable {

	private String player;
	private LobbySign lobbySign;
	private JoinResult result;
	private boolean cancelled;

	/**
	 * Creates a new instance of this event.
	 * @param player    the name of the player who clicked a lobby sign.
	 * @param round     the round which the lobby sign is linked to.
	 * @param lobbySign the {@link LobbySign lobby sign} which has been clicked.
	 * @param result    the result of the player being added to the round.
	 * @since 0.3.0
	 */
	public LobbyClickEvent(String player, Round round, LobbySign lobbySign, JoinResult result){
		super(round);
		this.player = player;
		this.lobbySign = lobbySign;
		this.result = result;
	}

	/**
	 * Retrieves the name of the player involved in this event.
	 * @return the name of the player involved in this event.
	 * @since 0.3.0
	 */
	public String getPlayer(){
		return player;
	}

	/**
	 * Retrieves the {@link LobbySign lobby sign} object involved in this event.
	 * @return the {@link LobbySign lobby sign} object involved in this event.
	 * @since 0.3.0
	 */
	public LobbySign getLobbySign(){
		return lobbySign;
	}

	/**
	 * Retrieves the result of the event.
	 * @return the result of this event.
	 * @since 0.3.0
	 */
	public JoinResult getResult(){
		return result;
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
