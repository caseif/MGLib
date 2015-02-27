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
package net.amigocraft.mglib.util.vector;

import com.google.common.base.Objects;

/**
 * Represents a two-dimensional point.
 *
 * @since 0.4.0
 */
public class Vector2f {

	private float x;
	private float y;

	/**
	 * Creates a new Vector2f.
	 *
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @since 0.4.0
	 */
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the x-coordinate of this vector.
	 *
	 * @return the x-coordinate
	 * @since 0.4.0
	 */
	public float getX() {
		return this.x;
	}

	/**
	 * Gets the y-coordinate of this vector.
	 *
	 * @return the y-coordinate
	 * @since 0.4.0
	 */
	public float getY() {
		return this.y;
	}

	/**
	 * Sets the x-coordinate of this vector.
	 *
	 * @param x he x-coordinate
	 * @since 0.4.0
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Sets the y-coordinate of this vector.
	 *
	 * @param y the y-coordinate
	 * @since 0.4.0
	 */
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object otherVector) {
		return otherVector instanceof Vector2f &&
				this.x == ((Vector2f)otherVector).getX() &&
				this.y == ((Vector2f)otherVector).getY();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(x, y);
	}

}
