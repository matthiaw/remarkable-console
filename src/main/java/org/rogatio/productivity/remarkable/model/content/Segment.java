/*
 * Remarkable Console - Copyright (C) 2021 Matthias Wegner
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.rogatio.productivity.remarkable.model.content;

/**
 * The Class Segment.
 */
public class Segment {

	/** The segment number. */
	int segmentNumber;

	/** The horizontal axis. */
	float horizontalAxis;

	/** The vertikal axis. */
	float vertikalAxis;

	/** The pen speed. */
	float penSpeed;

	/** The stroke direction. */
	float strokeDirection;

	/** The stroke width. */
	float strokeWidth;

	/** The pen pressure. */
	float penPressure;

	/**
	 * Instantiates a new segment.
	 *
	 * @param segmentNumber   the segment number
	 * @param horizontalAxis  the horizontal axis
	 * @param vertikalAxis    the vertikal axis
	 * @param penSpeed        the pen speed
	 * @param strokeDirection the stroke direction
	 * @param strokeWidth     the stroke width
	 * @param penPressure     the pen pressure
	 */
	public Segment(int segmentNumber, float horizontalAxis, float vertikalAxis, float penSpeed, float strokeDirection,
			float strokeWidth, float penPressure) {
		this.segmentNumber = segmentNumber;
		this.horizontalAxis = horizontalAxis;
		this.vertikalAxis = vertikalAxis;
		this.penSpeed = penSpeed;
		this.strokeDirection = strokeDirection;
		this.strokeWidth = strokeWidth;
		this.penPressure = penPressure;
	}

	/**
	 * Gets the segment number.
	 *
	 * @return the segment number
	 */
	public int getSegmentNumber() {
		return segmentNumber;
	}

	/**
	 * Gets the horizontal axis.
	 *
	 * @return the horizontal axis
	 */
	public float getHorizontalAxis() {
		return horizontalAxis;
	}

	/**
	 * Gets the vertikal axis.
	 *
	 * @return the vertikal axis
	 */
	public float getVertikalAxis() {
		return vertikalAxis;
	}

	/**
	 * Gets the pen speed.
	 *
	 * @return the pen speed
	 */
	public float getPenSpeed() {
		return penSpeed;
	}

	/**
	 * Gets the stroke direction.
	 *
	 * @return the stroke direction
	 */
	public float getStrokeDirection() {
		return strokeDirection;
	}

	/**
	 * Gets the stroke width.
	 *
	 * @return the stroke width
	 */
	public float getStrokeWidth() {
		return strokeWidth;
	}

	/**
	 * Gets the pen pressure.
	 *
	 * @return the pen pressure
	 */
	public float getPenPressure() {
		return penPressure;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(horizontalAxis);
		result = prime * result + Float.floatToIntBits(penPressure);
		result = prime * result + Float.floatToIntBits(penSpeed);
		result = prime * result + segmentNumber;
		result = prime * result + Float.floatToIntBits(strokeDirection);
		result = prime * result + Float.floatToIntBits(strokeWidth);
		result = prime * result + Float.floatToIntBits(vertikalAxis);
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Segment other = (Segment) obj;
		if (Float.floatToIntBits(horizontalAxis) != Float.floatToIntBits(other.horizontalAxis))
			return false;
		if (Float.floatToIntBits(penPressure) != Float.floatToIntBits(other.penPressure))
			return false;
		if (Float.floatToIntBits(penSpeed) != Float.floatToIntBits(other.penSpeed))
			return false;
		if (segmentNumber != other.segmentNumber)
			return false;
		if (Float.floatToIntBits(strokeDirection) != Float.floatToIntBits(other.strokeDirection))
			return false;
		if (Float.floatToIntBits(strokeWidth) != Float.floatToIntBits(other.strokeWidth))
			return false;
		if (Float.floatToIntBits(vertikalAxis) != Float.floatToIntBits(other.vertikalAxis))
			return false;
		return true;
	}

}
