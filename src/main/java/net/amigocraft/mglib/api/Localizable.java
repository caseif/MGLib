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
package net.amigocraft.mglib.api;

import java.util.UUID;

import net.amigocraft.mglib.exception.PlayerOfflineException;

/**
 * Represents a message which may be retrieved in multiple locales.
 *
 * @since 0.4.1
 */
public interface Localizable {

	/**
	 * Gets the key associated with this {@link Localizable}.
	 *
	 * @return the key associated with this {@link Localizable}
	 * @since 0.4.1
	 */
	public String getKey();

	/**
	 * Attempts to localize this message based on the server's defined locale.
	 *
	 * @return the message localized as requested if possible; otherwise the
	 *         message localized in the default locale if possible; otherwise
	 *         the raw localization key.
	 * @since 0.4.1
	 */
	public String localize();

	/**
	 * Attempts to localize this message in the given locale.
	 *
	 * @param locale the name of the locale to localize in. This should follow
	 *               the ISO 639-1 and ISO 3166-1 standards, respectively (e.g.
	 *               en_US or enUS).
	 * @return the message localized as requested if possible; otherwise the
	 *         message localized in the default locale if possible; otherwise
	 *         the raw localization key.
	 * @since 0.4.1
	 */
	public String localize(String locale);

	/**
	 * Attempts to localize this message for the player with the given username.
	 *
	 * @param playerName the username of the player to localize this message
	 *                   for.
	 * @return the message localized as requested if possible; otherwise the
	 *         message localized in the default locale if possible; otherwise
	 *         the raw localization key.
	 * @throws IllegalArgumentException if a player with the given username cannot
	 *                                  be found
	 * @since 0.4.1
	 */
	public String localizeFor(String playerName) throws IllegalArgumentException;

	/**
	 * Attempts to localize this message for the player with the given UUID.
	 *
	 * @param playerUuid the UUID of the player to localize this message for.
	 * @return the message localized as requested if possible; otherwise the
	 *         message localized in the default locale if possible; otherwise
	 *         the raw localization key.
	 * @throws IllegalArgumentException if a player with the given UUID cannot be
	 *                                  found
	 * @since 0.4.1
	 */
	public String localizeFor(UUID playerUuid) throws IllegalArgumentException;

	/**
	 * Attempts to localize this message for the given {@link MGPlayer}.
	 *
	 * @param player the {@link MGPlayer} to localize this message for
	 * @return the message localized as requested if possible; otherwise the
	 *         message localized in the default locale if possible; otherwise
	 *         the raw localization key.
	 * @throws IllegalArgumentException if a player with the given username cannot
	 *                                  be found
	 * @since 0.4.1
	 */
	public String localizeFor(MGPlayer player) throws IllegalArgumentException;

	/**
	 * Attempts to localize this message for the player with the given username
	 * and send it to them.
	 *
	 * @param playerName the username of the player to send the localized
	 *                   message to
	 * @throws IllegalArgumentException if a player with the given username cannot
	 *                                  be found
	 * @since 0.4.1
	 */
	public void sendTo(String playerName) throws IllegalArgumentException;

	/**
	 * Attempts to localize this message for the player with the given username
	 * and send it to them prefixed by the specified chat color.
	 *
	 * @param playerName the username of the player to send the localized
	 *                   message to
	 * @param color the color to prefix the localized string with
	 * @throws IllegalArgumentException if a player with the given username cannot
	 *                                  be found
	 * @since 0.4.1
	 */
	public void sendTo(String playerName, Color color) throws IllegalArgumentException;

	/**
	 * Attempts to localize this message for the player with the given username
	 * and send it to them.
	 *
	 * @param playerUuid the username of the player to send the localized
	 *                   message to
	 * @throws IllegalArgumentException if a player with the given username cannot
	 *                                  be found
	 * @since 0.4.1
	 */
	public void sendTo(UUID playerUuid) throws IllegalArgumentException;

	/**
	 * Attempts to localize this message for the player with the given UUID and
	 * send it to them prefixed by the specified chat color.
	 *
	 * @param playerUuid the UUID of the player to send the localized message
	 *                   to
	 * @param color the color to prefix the localized string with
	 * @throws IllegalArgumentException if a player with the given username cannot
	 *                                  be found
	 * @since 0.4.1
	 */
	public void sendTo(UUID playerUuid, Color color) throws IllegalArgumentException;
}
