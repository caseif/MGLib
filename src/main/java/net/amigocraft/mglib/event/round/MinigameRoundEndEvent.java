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
 * Called when an {@link Round MGLib round} ends.
 *
 * @since 0.1.0
 */
public class MinigameRoundEndEvent extends MGRoundEvent implements Cancellable {

	private boolean outOfTime;
	private boolean cancelled;

	/**
	 * Creates a new instance of this event.
	 *
	 * @param round     the {@link Round} which has ended
	 * @param outOfTime whether the round ended because its timer reached 0
	 * @since 0.1.0
	 */
	public MinigameRoundEndEvent(Round round, boolean outOfTime) {
		super(round);
		this.outOfTime = outOfTime;
	}

	/**
	 * Gets whether the round ended because its timer reached 0.
	 *
	 * @return whether the round ended because its timer reached 0
	 * @since 0.1.0
	 */
	public boolean wasOutOfTime() {
		return outOfTime;
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
