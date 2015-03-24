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

import net.amigocraft.mglib.exception.NoSuchPlayerException;
import net.amigocraft.mglib.exception.PlayerOfflineException;

//TODO: rewrite this class to be less chaotic
/**
 * MGLib's locale API. It can be used for easy localization; you
 * need only supply the translations themselves in the form of
 * *.properties files.
 *
 * @since 0.3.0
 */
public abstract class Locale {

	/**
	 * Retrieves the message with the given key from the
	 * current locale.
	 *
	 * <p>This method is included for the sake of reverse-compatibility. It will
	 * not exist at runtime and thus <strong>should not be used under any
	 * circumstances.</strong></p>
	 *
	 * @param key the key of the message to retrieve
	 * @return the message associated with the given key, or the key if the
	 *         message is not defined
	 * @deprecated This method is included for the sake of reverse-compatibility
	 * and <strong>will not exist at runtime.</strong>
	 * @since 0.3.0
	 */
	@Deprecated
	public abstract String _INVALID_getMessage(String key);

	/**
	 * Retrieves the message with the given key from the
	 * current locale, and replaces placeholder sequences
	 * (<code>%i</code>) with the corresponding vararg
	 * parameter.
	 *
	 * <p>This method is included for the sake of reverse-compatibility. It will
	 * not exist at runtime and thus <strong>should not be used under any
	 * circumstances.</strong></p>
	 *
	 * @param key the key of the message to retrieve
	 * @param replacements an array or vararg list of
	 * strings to replace placeholder sequences (%i) with
	 * @return the message associated with the given key,
	 * or the key if the message is not defined
	 * @deprecated This method is included for the sake of reverse-compatibility
	 * and <strong>will not exist at runtime.</strong>
	 * @since 0.4.0
	 */
	@Deprecated
	public abstract String _INVALID_getMessage(String key, String... replacements);

	/**
	 * Retrieves the message with the given key from the
	 * current locale, and replaces placeholder sequences
	 * (<code>%i</code>) with the corresponding vararg
	 * parameter.
	 *
	 * <p><strong>Note:</strong> placeholder sequences should start at
	 * index 1.</p>
	 *
	 * @param key the key of the message to retrieve
	 * @return the {@link Localizable} associated with the given key,
	 * @since 0.5.0
	 */
	public abstract Localizable getMessage(String key);

	/**
	 * Returns whether this object was loaded from a legacy
	 * locale file.
	 *
	 * @return whether this object was loaded from a legacy
	 * locale file.
	 * @deprecated Always returns false due to changes to locale API
	 * @since 0.4.0
	 */
	@Deprecated
	public abstract boolean isLegacy();

	/**
	 * Initializes the locale manager. This must be called, or
	 * {@link Locale#getMessage(String)} will always return its
	 * parameter.
	 *
	 * @since 0.3.0
	 */
	public abstract void initialize();
}
