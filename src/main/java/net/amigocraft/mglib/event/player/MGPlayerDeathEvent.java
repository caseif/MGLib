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

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Called alongside {@link PlayerDeathEvent} if the hooking plugin specifies
 * such.
 *
 * @since 0.3.0
 */
public class MGPlayerDeathEvent extends MGPlayerEvent {

	private DamageCause cause;
	private Entity killer;

	/**
	 * Creates a new instance of this event.
	 *
	 * @param player the player who has died
	 * @param cause  the cause of death (i.e. the cause of the damage resulting
	 *               in death)
	 * @param killer the entity which killed this player (null may be used in
	 *               the event that there is none
	 * @since 0.3.0
	 */
	public MGPlayerDeathEvent(MGPlayer player, DamageCause cause, Entity killer) {
		super(player);
		this.cause = cause;
		this.killer = killer;
	}

	/**
	 * Retrieves the cause of death (i.e. the cause of the damage resulting in
	 * death).
	 *
	 * @return the cause of death
	 * @since 0.3.0
	 */
	public DamageCause getCause() {
		return cause;
	}

	/**
	 * Retrieves the entity which killed the player, if applicable.
	 *
	 * @return the entity which killed the player, or null if none applies
	 * @since 0.3.0
	 */
	public Entity getKiller() {
		return killer;
	}

}
