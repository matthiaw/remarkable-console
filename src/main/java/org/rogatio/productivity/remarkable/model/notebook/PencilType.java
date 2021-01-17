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
package org.rogatio.productivity.remarkable.model.notebook;

/**
 * The Enum PencilType.
 */
public enum PencilType {

	/** The ballpointpen. */
	BALLPOINTPEN(15, "Ballpoint pen", 1.0), /** The calligrapgypen. */
 CALLIGRAPGYPEN(21, "Calligraphy pen", 1.0),
	
	/** The highlighter. */
	HIGHLIGHTER(18, "Highlighter", 0.2), 
 /** The paintbrush. */
 PAINTBRUSH(12, "Paintbrush", 1.0),
	
	/** The mechanicalpencil. */
	MECHANICALPENCIL(13, "Mechanical pencil", 1.0), 
 /** The pencil. */
 PENCIL(14, "Pencil", 1.0), 
 /** The marker. */
 MARKER(16, "Marker", 0.9),
	
	/** The fineline. */
	FINELINE(17, "Fineliner", 1.0), 
 /** The eraser. */
 ERASER(6, "Eraser", 0.0);

	/** The type. */
	private final int type;
	
	/** The name. */
	private final String name;
	
	/** The opacity. */
	private final double opacity;

	/**
	 * Instantiates a new pencil type.
	 *
	 * @param type the type
	 * @param name the name
	 * @param opacity the opacity
	 */
	private PencilType(int type, String name, double opacity) {
		this.type = type;
		this.name = name;
		this.opacity = opacity;
	}

	/**
	 * Gets the.
	 *
	 * @param type the type
	 * @return the pencil type
	 */
	public static PencilType get(int type) {
		PencilType[] types = PencilType.values();
		for (PencilType pencilType : types) {
			if (type == pencilType.getType()) {
				return pencilType;
			}
		}

		return null;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the opacity.
	 *
	 * @return the opacity
	 */
	public double getOpacity() {
		return opacity;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public int getType() {
		return type;
	}

}
