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
package net.amigocraft.mglib.misc;

import java.util.HashMap;

public interface Metadatable {

	/**
	 * Retrieves a given value from this object's metadata by its key.
	 *
	 * @param key the key to retrieve.
	 * @return the key's mapped value, or null if it is not mapped.
	 * @since 0.3.0
	 */
	public Object getMetadata(String key);

	/**
	 * Adds a key-value pair to this object's metadata.
	 *
	 * <p><strong>Note:</strong> This method consists of a single call to
	 * {@link HashMap#put(Object, Object)}, so existing keys will be overwritten.</p>
	 *
	 * @param key   the key to store in the round's metadata.
	 * @param value the value to assign to the given key.
	 * @since 0.3.0
	 */
	public void setMetadata(String key, Object value);

	/**
	 * Removes the given key from this object's metadata.
	 *
	 * @param key the key to remove from this object's metadata.
	 * @since 0.3.0
	 */
	public void removeMetadata(String key);

	/**
	 * Checks whether a given key is present in this object's metadata.
	 *
	 * @param key the key to test for.
	 * @return whether the key is present in this object's metadata.
	 * @since 0.3.0
	 */
	public boolean hasMetadata(String key);

	/**
	 * Retrieves a {@link HashMap} representing this object's complete metadata.
	 *
	 * @return this object's metadata in the form of a {@link HashMap}.
	 * @since 0.3.0
	 */
	public HashMap<String, Object> getAllMetadata();

}
