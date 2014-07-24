package net.amigocraft.mglib.api;

import org.bukkit.Location;

/**
 * Represents an arbitrary two-dimensional point.
 * This class is more reliable for comparisons than the vanilla Bukkit {@link Location} class.
 * @since 0.3.0
 */
public class Location2D {

	private String world = "";
	private float x;
	private float y;

	/**
	 * Creates a new Location2D from the given points.
	 * @param world the name of the world containing the location.
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location2D(String world, float x, float y){
		this.world = world;
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a new Location2D from the given points.
	 * @param world the name of the world containing the location.
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location2D(String world, double x, double y){
		this.world = world;
		this.x = (float)x;
		this.y = (float)y;
	}

	/**
	 * Creates a new Location2D from the given points.
	 * @param world the name of the world containing the location.
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location2D(String world, int x, int y){
		this.world = world;
		this.x = (float)x;
		this.y = (float)y;
	}

	/**
	 * Creates a new Location2D from the given points.
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location2D(float x, float y){
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a new Location2D from the given points.
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location2D(double x, double y){
		this.x = (float)x;
		this.y = (float)y;
	}

	/**
	 * Creates a new Location2D from the given points.
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location2D(int x, int y){
		this.x = (float)x;
		this.y = (float)y;
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
	 * Creates a Location2D from the x- and y-coordinates of the given {@link Location Bukkit location}.
	 * @param location the {@link Location Bukkit location} to create a Location2D from.
	 * @return the new Location2D.
	 * @since 0.3.0
	 */
	public static Location2D valueOfXY(Location location){
		return new Location2D(location.getWorld().getName(), location.getX(), location.getY());
	}

	/**
	 * Creates a Location2D from the x- and z-coordinates of the given {@link Location Bukkit location}.
	 * @param location the {@link Location Bukkit location} to create a Location2D from.
	 * @return the new Location2D.
	 * @since 0.3.0
	 */
	public static Location2D valueOfXZ(Location location){
		return new Location2D(location.getWorld().getName(), location.getX(), location.getZ());
	}

	/**
	 * Creates a Location2D from the y- and z-coordinates of the given {@link Location Bukkit location}.
	 * @param location the {@link Location Bukkit location} to create a Location2D from.
	 * @return the new Location2D.
	 * @since 0.3.0
	 */
	public static Location2D valueOfYZ(Location location){
		return new Location2D(location.getWorld().getName(), location.getY(), location.getZ());
	}

	@Override
	public boolean equals(Object otherObject){
		return otherObject instanceof Location2D &&
				((Location2D)otherObject).getWorld().equals(world) &&
				((Location2D)otherObject).getX() == x &&
				((Location2D)otherObject).getY() == y;
	}
	
	@Override
	public int hashCode(){
		return 47 + (world.hashCode() * 61 +
				(Float.valueOf(x).hashCode() * 53 + Float.valueOf(y).hashCode() * 67) * 93);	
	}

}
