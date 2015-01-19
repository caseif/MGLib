/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Maxim Roncacé
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
import net.amigocraft.mglib.api.Stage;
import org.bukkit.event.Cancellable;

/**
 * Fired when the stage of an {@link net.amigocraft.mglib.api.Round MGLib round} changes.
 * @since 0.3.0
 */
public class MinigameRoundStageChangeEvent extends MGRoundEvent implements Cancellable {

	private Stage before;
	private Stage after;

	private boolean cancelled = false;

	/**
	 * Creates a new instance of this event.
	 * @param round  the round associated with this event
	 * @param before the stage before the change
	 * @param after  the stage after the change
	 * @since 0.3.0
	 */
	public MinigameRoundStageChangeEvent(Round round, Stage before, Stage after){
		super(round);
		this.before = before;
		this.after = after;
	}

	/**
	 * Gets the stage of the round before the event.
	 * @return the stage of the round before the event
	 * @since 0.3.0
	 */
	public Stage getStageBefore(){
		return before;
	}

	/**
	 * Gets the stage of the round after the event.
	 * @return the stage of the round after the event
	 * @since 0.3.0
	 */
	public Stage getStageAfter(){
		return after;
	}

	@Override
	public boolean isCancelled(){
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled){
		this.cancelled = cancelled;
	}

}
