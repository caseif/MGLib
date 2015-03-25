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

/**
 * A platform-independent enumeration of available chat colors in Minecraft.
 *
 * @since 0.4.0
 */
public enum Color {

	BLACK('0'),
	DARK_BLUE('1'),
	DARK_GREEN('2'),
	DARK_AQUA('3'),
	DARK_RED('4'),
	DARK_PURPLE('5'),
	GOLD('6'),
	GRAY('7'),
	DARK_GRAY('8'),
	BLUE('9'),
	GREEN('a'),
	AQUA('b'),
	RED('c'),
	LIGHT_PURPLE('d'),
	YELLOW('e'),
	WHITE('f'),
	MAGIC('k'),
	BOLD('l'),
	STRIKETHROUGH('m'),
	UNDERLINE('n'),
	ITALIC('o'),
	RESET('r');

	private char code;

	Color(char code) {
		this.code = code;
	}

	/**
	 * Gets the code associated with this {@link Color}.
	 * @return the code associated with this {@link Color}
	 * @deprecated Raw character codes are being phased out of Minecraft
	 * @since 0.4.0
	 */
	@Deprecated
	public char getCode() {
		return code;
	}

	/**
	 * Returns the color associated with the given char code, or null if not
	 * found.
	 * @param code the code to lookup
	 * @return the {@link Color} color associated with the code
	 * @deprecated Raw character codes are being phased out of Minecraft
	 * @since 0.4.0
	 */
	@Deprecated
	public static Color fromCode(char code) {
		for (Color c : Color.values()) {
			if (c.getCode() == code) {
				return c;
			}
		}
		return null;
	}

}
