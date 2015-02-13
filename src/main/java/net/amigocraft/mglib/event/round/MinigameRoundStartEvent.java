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
package net.amigocraft.mglib.event.round;

import net.amigocraft.mglib.api.Round;

import org.bukkit.event.Cancellable;

/**
 * Called when an MGLib round begins.
 *
 * @since 0.1.0
 */
public class MinigameRoundStartEvent extends MGRoundEvent implements Cancellable {

	private boolean prepared;
	private boolean cancelled;

	/**
	 * Creates a new instance of this event.
	 *
	 * @param round    the {@link Round} associated with this event
	 * @param prepared whether the round start was on account of its preparation
	 *                 period ending
	 * @since 0.1.0
	 */
	public MinigameRoundStartEvent(Round round, boolean prepared) {
		super(round);
		this.prepared = prepared;
	}

	/**
	 * Creates an instance of this event.
	 *
	 * @param round the {@link Round} associated with this {@link
	 *              org.bukkit.event.Event}
	 * @since 0.1.0
	 */
	public MinigameRoundStartEvent(Round round) {
		super(round);
		this.prepared = false;
	}

	/**
	 * Gets whether the round start was on account of its preparation period
	 * ending.
	 *
	 * @return whether the round start was on account of its preparation period
	 * ending
	 * @since 0.1.0
	 */
	public boolean wasPrepared() {
		return prepared;
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
