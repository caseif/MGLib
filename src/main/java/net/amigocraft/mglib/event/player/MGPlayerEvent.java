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
import net.amigocraft.mglib.event.MGLibEvent;

/**
 * Called when an event involving a {@link MGPlayer player} occurs.
 *
 * @since 0.1.0
 */
public class MGPlayerEvent extends MGLibEvent {

	protected MGPlayer player;

	/**
	 * Creates a new instance of this event.
	 *
	 * @param player the {@link MGPlayer player} involved in this {@link
	 *               MGPlayerEvent event}
	 * @since 0.1.0
	 */
	public MGPlayerEvent(MGPlayer player) {
		super(player.getPlugin());
		this.player = player;
	}

	/**
	 * Retrieves the {@link MGPlayer player} involved in this {@link
	 * MGPlayerEvent event}.
	 *
	 * @return the {@link MGPlayer player} involved in this {@link MGPlayerEvent
	 * event}
	 * @since 0.1.0
	 */
	public MGPlayer getPlayer() {
		return player;
	}

}
