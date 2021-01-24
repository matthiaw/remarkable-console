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
 * The Class Layer.
 */
public class Layer {

	/** The layer number. */
	private int layerNumber;

	/** The strokes. */
	private List<Stroke> strokes = new ArrayList<Stroke>();

	/**
	 * Instantiates a new layer.
	 *
	 * @param layerNumber the layer number
	 */
	public Layer(int layerNumber) {
		this.layerNumber = layerNumber;
	}

	/**
	 * Adds the.
	 *
	 * @param stroke the stroke
	 */
	public void add(Stroke stroke) {
		strokes.add(stroke);
	}

	/**
	 * Gets the layer number.
	 *
	 * @return the layer number
	 */
	public int getLayerNumber() {
		return layerNumber;
	}

	/**
	 * Gets the strokes.
	 *
	 * @return the strokes
	 */
	public List<Stroke> getStrokes() {
		return strokes;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		return "Layer [no=" + this.getLayerNumber() + ", Strokes (" + getStrokes().size() + ")]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + layerNumber;
		result = prime * result + ((strokes == null) ? 0 : strokes.hashCode());
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
		Layer other = (Layer) obj;
		if (layerNumber != other.layerNumber)
			return false;
		if (strokes == null) {
			if (other.strokes != null)
				return false;
		} else if (!strokes.equals(other.strokes))
			return false;
		return true;
	}

}
