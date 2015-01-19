/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Maxim Roncacé
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.amigocraft.mglib.event.player;

import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.api.Round;
import org.bukkit.event.Cancellable;

/**
 * Called when a {@link MGPlayer player} becomes a spectator.
 *
 * @since 0.2.0
 */
public class MGPlayerSpectateEvent extends MGPlayerEvent implements Cancellable {

	protected Round round;
	private boolean cancelled;

	/**
	 * Creates a new instance of this event.
	 *
	 * @param round  the round the player has joined
	 * @param player the player involved in this event
	 * @since 0.2.0
	 */
	public MGPlayerSpectateEvent(Round round, MGPlayer player) {
		super(player);
		this.round = round;
	}

	/**
	 * Returns the {@link Round round} involved in this event.
	 *
	 * @return the {@link Round round} involved in this event
	 * @since 0.2.0
	 */
	public Round getRound() {
		return round;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

}
