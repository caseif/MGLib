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

import com.google.common.base.Objects;
import net.amigocraft.mglib.util.vector.Vector2f;
import net.amigocraft.mglib.util.vector.Vector3f;
import org.bukkit.Location;

/**
 * Represents a three-dimensional point in a world with orientation. This class
 * is more reliable for comparisons than the vanilla Bukkit {@link Location}
 * class.
 *
 * @since 0.3.0
 */
public class Location3D {

	private String world = "";
	private Vector3f position;
	private Vector2f rotation;

	/**
	 * Creates a new Location3D from the given points.
	 *
	 * @param world the name of the world containing the location.
	 * @param x     the x-coordinate of the location
	 * @param y     the y-coordinate of the location
	 * @param z     the z-coordinate of the location
	 * @param pitch the pitch of the location
	 * @param yaw   the yaw of the location
	 * @since 0.3.0
	 */
	public Location3D(String world, float x, float y, float z, float pitch, float yaw) {
		this.world = world;
		this.position = new Vector3f(x, y, z);
		this.rotation = new Vector2f(pitch, yaw);
	}

	/**
	 * Creates a new Location3D from the given points.
	 *
	 * @param world the name of the world containing the location.
	 * @param x     the x-coordinate of the location
	 * @param y     the y-coordinate of the location
	 * @param z     the z-coordinate of the location
	 * @since 0.3.0
	 */
	public Location3D(String world, float x, float y, float z) {
		this(world, x, y, z, 0f, 0f);
	}

	/**
	 * Creates a new Location3D from the given points.
	 *
	 * @param world the name of the world containing the location.
	 * @param x     the x-coordinate of the location.
	 * @param y     the y-coordinate of the location.
	 * @param z     the z-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location3D(String world, double x, double y, double z) {
		this(world, (float)x, (float)y, (float)z);
	}

	/**
	 * Creates a new Location3D from the given points.
	 *
	 * @param world the name of the world containing the location.
	 * @param x     the x-coordinate of the location.
	 * @param y     the y-coordinate of the location.
	 * @param z     the z-coordinate of the location.
	 * @since 0.3.0
	 */
	public Location3D(String world, int x, int y, int z) {
		this(world, (float)x, (float)y, (float)z);
	}

	/**
	 * Creates a new Location3D from the given points.
	 *
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @param z the z-coordinate of the location.
	 * @deprecated Use {@link Vector3f}
	 * @since 0.3.0
	 */
	@Deprecated
	public Location3D(float x, float y, float z) {
		this(null, x, y, z);
	}

	/**
	 * Creates a new Location3D from the given points.
	 *
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @param z the z-coordinate of the location.
	 * @deprecated Use {@link Vector3f}
	 * @since 0.3.0
	 */
	@Deprecated
	public Location3D(double x, double y, double z) {
		this(null, (float)x, (float)y, (float)z);
	}

	/**
	 * Creates a new Location3D from the given points.
	 *
	 * @param x the x-coordinate of the location.
	 * @param y the y-coordinate of the location.
	 * @param z the z-coordinate of the location.
	 * @deprecated Use {@link Vector3f}
	 * @since 0.3.0
	 */
	@Deprecated
	public Location3D(int x, int y, int z) {
		this(null, (float)x, (float)y, (float)z);
	}

	/**
	 * Retrieves the name of the world of this location.
	 *
	 * @return the name of the world of this location, or an empty string if one
	 * was not provided.
	 * @since 0.3.0
	 */
	public String getWorld() {
		return world;
	}

	/**
	 * Sets the name of the world of this location.
	 *
	 * @param world the name of the world of this location, or null if one was
	 *              not provided.
	 * @since 0.3.0
	 */
	public void setWorld(String world) {
		this.world = world;
	}

	/**
	 * Returns the position of this location.
	 *
	 * @return the position of this location as a {@link Vector3f}
	 * @since 0.3.1
	 */
	public Vector3f getPosition() {
		return this.position;
	}

	/**
	 * Sets the position of this location.
	 *
	 * @param position the new position of this locaiton
	 * @since 0.3.1
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	/**
	 * Retrieves the x-coordinate of this location.
	 *
	 * @return the x-coordinate of this location.
	 * @since 0.3.0
	 */
	public float getX() {
		return position.getX();
	}

	/**
	 * Retrieves the y-coordinate of this location.
	 *
	 * @return the y-coordinate of this location.
	 * @since 0.3.0
	 */
	public float getY() {
		return position.getY();
	}

	/**
	 * Retrieves the z-coordinate of this location.
	 *
	 * @return the z-coordinate of this location.
	 * @since 0.3.0
	 */
	public float getZ() {
		return position.getZ();
	}

	/**
	 * Sets the x-coordinate of this location.
	 *
	 * @param x the new x-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setX(float x) {
		this.position.setX(x);
	}

	/**
	 * Sets the x-coordinate of this location.
	 *
	 * @param x the new x-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setX(double x) {
		setX((float)x);
	}

	/**
	 * Sets the x-coordinate of this location.
	 *
	 * @param x the new x-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setX(int x) {
		setX((float)x);
	}

	/**
	 * Sets the y-coordinate of this location.
	 *
	 * @param y the new y-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setY(float y) {
		this.position.setY(y);
	}

	/**
	 * Sets the y-coordinate of this location.
	 *
	 * @param y the new y-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setY(double y) {
		setY((float)y);
	}

	/**
	 * Sets the y-coordinate of this location.
	 *
	 * @param y the new y-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setY(int y) {
		setZ((float)y);
	}

	/**
	 * Sets the z-coordinate of this location.
	 *
	 * @param z the new z-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setZ(float z) {
		this.position.setZ(z);
	}

	/**
	 * Sets the z-coordinate of this location.
	 *
	 * @param z the new z-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setZ(double z) {
		setZ((float)z);
	}

	/**
	 * Sets the z-coordinate of this location.
	 *
	 * @param z the new z-coordinate of this location.
	 * @since 0.3.0
	 */
	public void setZ(int z) {
		setZ((float)z);
	}

	/**
	 * Returns the rotation of this location.
	 *
	 * @return the rotation of this location as a {@link Vector2f}
	 * @since 0.3.1
	 */
	public Vector2f getRotation() {
		return this.rotation;
	}

	/**
	 * Sets the rotation of this location.
	 *
	 * @param rotation the new rotation of this locaiton
	 * @since 0.3.1
	 */
	public void setRotation(Vector2f rotation) {
		this.rotation = rotation;
	}

	/**
	 * Retrieves the pitch of this location.
	 *
	 * @return the pitch of this location.
	 * @since 0.3.0
	 */
	public float getPitch() {
		return rotation.getX();
	}

	/**
	 * Retrieves the yaw of this location.
	 *
	 * @return the yaw of this location.
	 * @since 0.3.0
	 */
	public float getYaw() {
		return rotation.getY();
	}

	/**
	 * Sets the pitch of this location.
	 *
	 * @param pitch the new pitch of this location.
	 * @since 0.3.0
	 */
	public void setPitch(float pitch) {
		this.rotation.setX(pitch);
	}

	/**
	 * Sets the pitch of this location.
	 *
	 * @param pitch the new pitch of this location.
	 * @since 0.3.0
	 */
	public void setPitch(double pitch) {
		setPitch((float)pitch);
	}

	/**
	 * Sets the pitch of this location.
	 *
	 * @param pitch the new pitch of this location.
	 * @since 0.3.0
	 */
	public void setPitch(int pitch) {
		setPitch((float)pitch);
	}

	/**
	 * Sets the yaw of this location.
	 *
	 * @param yaw the new yaw of this location.
	 * @since 0.3.0
	 */
	public void setYaw(float yaw) {
		this.position.setY(yaw);
	}

	/**
	 * Sets the yaw of this location.
	 *
	 * @param yaw the new yaw of this location.
	 * @since 0.3.0
	 */
	public void setYaw(double yaw) {
		setY((float)yaw);
	}

	/**
	 * Sets the yaw of this location.
	 *
	 * @param yaw the new yaw of this location.
	 * @since 0.3.0
	 */
	public void setYaw(int yaw) {
		setZ((float)yaw);
	}

	/**
	 * Creates a Location3D from the given {@link Location Bukkit location}.
	 *
	 * @param location the {@link Location Bukkit location} to create a
	 *                 Location3D from.
	 * @return the new Location3D.
	 * @deprecated Depends on Bukkit
	 * @since 0.3.0
	 */
	@Deprecated
	@SuppressWarnings("deprecation")
	public static Location3D valueOf(Location location) {
		return valueOf(location, false);
	}

	/**
	 * Creates a Location3D from the given {@link Location Bukkit location}.
	 *
	 * @param location the {@link Location Bukkit location} to create a
	 *                 Location3D from.
	 * @param copyOrientation whether the pitch and yaw of <code>location</code>
	 *                        will be stored in the new {@link Location3D}.
	 * @return the new Location3D.
	 * @deprecated Depends on Bukkit
	 * @since 0.3.0
	 */
	@Deprecated
	public static Location3D valueOf(Location location, boolean copyOrientation) {
		if (copyOrientation) {
			return new Location3D(location.getWorld().getName(),
					(float)location.getX(), (float)location.getY(), (float)location.getZ(),
					location.getPitch(), location.getYaw());
		}
		else {
			return new Location3D(location.getWorld().getName(),
					(float)location.getX(), (float)location.getY(), (float)location.getZ());
		}
	}

	@Override
	public boolean equals(Object otherObject) {
		return otherObject instanceof Location3D &&
				((Location3D)otherObject).getWorld().equals(world) &&
				position.equals(((Location3D)otherObject).getPosition()) &&
				rotation.equals(((Location3D)otherObject).getRotation());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(world, position, rotation);
	}

}
