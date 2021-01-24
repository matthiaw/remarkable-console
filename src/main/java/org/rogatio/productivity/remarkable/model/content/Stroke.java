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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Stroke.
 */
public class Stroke {

	/** The stroke number. */
	private int strokeNumber;

	/** The pencil type. */
	private int pencilType;

	/** The stroke color. */
	private int strokeColor;

	/** The penwidth. */
	private float penwidth;

	/** The segments. */
	private List<Segment> segments = new ArrayList<Segment>();

	/**
	 * Instantiates a new stroke.
	 *
	 * @param strokeNumber the stroke number
	 * @param pencilType   the pencil type
	 * @param strokeColor  the stroke color
	 * @param penwidth     the penwidth
	 * @param segments     the segments
	 */
	public Stroke(int strokeNumber, int pencilType, int strokeColor, float penwidth, List<Segment> segments) {
		this.strokeNumber = strokeNumber;
		this.pencilType = pencilType;
		this.strokeColor = strokeColor;
		this.penwidth = penwidth;
		this.segments = segments;
	}

	/**
	 * Gets the stroke number.
	 *
	 * @return the stroke number
	 */
	public int getStrokeNumber() {
		return strokeNumber;
	}

	/**
	 * Gets the pencil type.
	 *
	 * @return the pencil type
	 */
	public PencilType getPencilType() {
		return PencilType.get(pencilType);
	}

	/**
	 * Gets the stroke color.
	 *
	 * @return the stroke color
	 */
	public StrokeColor getStrokeColor() {
		return StrokeColor.get(strokeColor);
	}

	/**
	 * Gets the pen width.
	 *
	 * @return the pen width
	 */
	public float getPenWidth() {
		return penwidth;
	}

	/**
	 * Gets the first segment.
	 *
	 * @return the first segment
	 */
	public Segment getFirstSegment() {
		return segments.get(0);
	}

	/**
	 * Gets the segments.
	 *
	 * @return the segments
	 */
	public List<Segment> getSegments() {
		return segments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((segments == null) ? 0 : segments.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stroke other = (Stroke) obj;
		if (segments == null) {
			if (other.segments != null)
				return false;
		} else if (!segments.equals(other.segments))
			return false;
		return true;
	}

}
