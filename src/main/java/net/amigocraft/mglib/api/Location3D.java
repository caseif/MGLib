package net.amigocraft.mglib.api;

import org.bukkit.Location;

/**
 * Represents an arbitrary three-dimensional point. This class is more reliable for comparisons than the vanilla Bukkit
 * {@link Location} class.
 * @since 0.3.0
 */
public class Location3D {

	private String world = "";
	private float x;
	private float y;
	private float z;

	/**
	 * Creates a new Location3D from the given points.
	 * @param world the name of the world containing the location.
	 * @param x     the x-coordinate of the location.
	 * @param y     the y-coordinate of the location.
	 * @param z     the z-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location3D(String world, float x, float y, float z){
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a new Location3D from the given points.
	 * @param world the name of the world containing the location.
	 * @param x     the x-coordinate of the location.
	 * @param y     the y-coordinate of the location.
	 * @param z     the z-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location3D(String world, double x, double y, double z){
		this.world = world;
		this.x = (float)x;
		this.y = (float)y;
		this.z = (float)z;
	}

	/**
	 * Creates a new Location3D from the given points.
	 * @param world the name of the world containing the location.
	 * @param x     the x-coordinate of the location.
	 * @param y     the y-coordinate of the location.
	 * @param z     the z-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location3D(String world, int x, int y, int z){
		this.world = world;
		this.x = (float)x;
		this.y = (float)y;
		this.z = (float)z;
	}

	/**
	 * Creates a new Location3D from the given points.
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @param z the z-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location3D(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a new Location3D from the given points.
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @param z the z-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location3D(double x, double y, double z){
		this.x = (float)x;
		this.y = (float)y;
		this.z = (float)z;
	}

	/**
	 * Creates a new Location3D from the given points.
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @param z the z-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location3D(int x, int y, int z){
		this.x = (float)x;
		this.y = (float)y;
		this.z = (float)z;
	}

	/**
	 * Retrieves the name of the world of this location.
	 * @return the name of the world of this location, or an empty string if one was not provided.
	 * @since 0.3.0
	 */
	public String getWorld(){
		return world;
	}

	/**
	 * Sets the name of the world of this location.
	 * @param world the name of the world of this location, or null if one was not provided.
	 * @since 0.3.0
	 */
	public void setWorld(String world){
		this.world = world;
	}

	/**
	 * Retrieves the x-coordinate of this location.
	 * @return the x-coordinate of this location.
	 * @since 0.3.0
	 */
	public float getX(){
		return x;
	}

	/**
	 * Retrieves the y-coordinate of this location.
	 * @return the y-coordinate of this location.
	 * @since 0.3.0
	 */
	public float getY(){
		return y;
	}

	/**
	 * Retrieves the z-coordinate of this location.
	 * @return the z-coordinate of this location.
	 * @since 0.3.0
	 */
	public float getZ(){
		return z;
	}

	/**
	 * Sets the x-coordinate of this location.
	 * @param x the new x-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setX(float x){
		this.x = x;
	}

	/**
	 * Sets the x-coordinate of this location.
	 * @param x the new x-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setX(double x){
		this.x = (float)x;
	}

	/**
	 * Sets the x-coordinate of this location.
	 * @param x the new x-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setX(int x){
		this.x = (float)x;
	}

	/**
	 * Sets the y-coordinate of this location.
	 * @param y the new y-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setY(float y){
		this.y = y;
	}

	/**
	 * Sets the y-coordinate of this location.
	 * @param y the new y-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setY(double y){
		this.y = (float)y;
	}

	/**
	 * Sets the y-coordinate of this location.
	 * @param y the new y-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setY(int y){
		this.y = (float)y;
	}

	/**
	 * Sets the z-coordinate of this location.
	 * @param z the new z-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setZ(float z){
		this.z = z;
	}

	/**
	 * Sets the z-coordinate of this location.
	 * @param z the new z-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setZ(double z){
		this.z = (float)z;
	}

	/**
	 * Sets the z-coordinate of this location.
	 * @param z the new z-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setZ(int z){
		this.z = (float)z;
	}

	/**
	 * Creates a Location3D from the given {@link Location Bukkit location}.
	 * @param location the {@link Location Bukkit location} to create a Location3D from.
	 * @return the new Location3D.
	 * @since 0.3.0
	 */
	public static Location3D valueOf(Location location){
		return new Location3D(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
	}

	@Override
	public boolean equals(Object otherObject){
		return otherObject instanceof Location3D &&
				((Location3D)otherObject).getWorld().equals(world) &&
				((Location3D)otherObject).getX() == x &&
				((Location3D)otherObject).getY() == y &&
				((Location3D)otherObject).getZ() == z;
	}

	@Override
	public int hashCode(){
		return 47 + (world.hashCode() * 61 + (Float.valueOf(x).hashCode() * 53 + Float.valueOf(y).hashCode() * 67 + Float.valueOf(z).hashCode() * 17) * 93);
	}

}
