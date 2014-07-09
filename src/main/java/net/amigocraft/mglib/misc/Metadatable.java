package net.amigocraft.mglib.misc;

import java.util.HashMap;

public interface Metadatable {
	
	HashMap<String, Object> metadata = new HashMap<String, Object>();
	
	/**
	 * Retrieves a given value from this object's metadata by its key.
	 * @param key the key to retrieve.
	 * @return the key's mapped value, or null if it is not mapped.
	 * @since 0.3.0
	 */
	public Object getMetadata(String key);
	
	/**
	 * Adds a key-value pair to this object's metadata.
	 * <br><br>
	 * <b>Note:</b> This method consists of a single call to {@link HashMap#put(Object, Object)}, so existing keys will be overwritten.
	 * @param key the key to store in the round's metadata.
	 * @param value the value to assign to the given key.
	 * @since 0.3.0
	 */
	public void setMetadata(String key, Object value);
	
	/**
	 * Removes the given key from this object's metadata.
	 * @param key the key to remove from this object's metadata.
	 * @since 0.3.0
	 */
	public void removeMetadata(String key);
	
	/**
	 * Checks whether a given key is present in this object's metadata.
	 * @param key the key to test for.
	 * @return whether the key is present in this object's metadata.
	 * @since 0.3.0
	 */
	public boolean hasMetadata(String key);
	
	/**
	 * Retrieves a {@link HashMap} representing this object's complete metadata.
	 * @return this object's metadata in the form of a {@link HashMap}.
	 * @since 0.3.0 
	 */
	public HashMap<String, Object> getAllMetadata();

}
