package com.mcmmorpg.common.util;

/**
 * A 2-dimensional vector.
 */
public class Vector2D {

	private double x;
	private double y;

	/**
	 * Creates a new 2-dimensional vector with the specified x and y components.
	 */
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the x-component of this vector.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the x-component of this vector.
	 */
	public Vector2D setX(int x) {
		this.x = x;
		return this;
	}

	/**
	 * Returns the y-component of this vector.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the y-component of this vector.
	 */
	public Vector2D setY(double y) {
		this.y = y;
		return this;
	}

	/**
	 * Adds the other vector to this vector.
	 */
	public Vector2D add(Vector2D other) {
		this.x += other.getX();
		this.y += other.getY();
		return this;

	}

	/**
	 * Subtracts the other vector from this vector.
	 */
	public Vector2D subtract(Vector2D other) {
		this.x -= other.getX();
		this.y -= other.getY();
		return this;
	}

	/**
	 * Multiplies this vector by the specified scalar.
	 */
	public Vector2D multiply(double scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	/**
	 * Normalizes this vector.
	 */
	public Vector2D normalize() {
		double magnitude = magnitude();
		this.x /= magnitude;
		this.y /= magnitude;
		return this;
	}

	/**
	 * Returns the square magnitude of this vector.
	 */
	public double magnitudeSquared() {
		return x * x + y * y;
	}

	/**
	 * Returns the magnitude of this vector.
	 */
	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
	}

	/**
	 * Returns a new Vector2D with the x and y components of this vector.
	 */
	public Vector2D copy() {
		return new Vector2D(x, y);
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	/**
	 * Returns true if the other object is a Vector2D and has the same x and y
	 * components of this vector. Returns false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector2D)) {
			return false;
		}
		Vector2D other = (Vector2D) obj;
		return this.x == other.x && this.y == other.y;
	}
}
