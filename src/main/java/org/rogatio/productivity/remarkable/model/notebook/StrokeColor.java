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

import org.rogatio.productivity.remarkable.io.PropertiesCache;

/**
 * The Enum StrokeColor.
 */
public enum StrokeColor {

	/** The primary. */
	PRIMARY(0, PropertiesCache.getInstance().getProperty(PropertiesCache.SVGPRIMARYCOLOR)), 
	
	/** The secondary. */
	SECONDARY(1, PropertiesCache.getInstance().getProperty(PropertiesCache.SVGSECONDARYCOLOR)), 
	
	/** The backround. */
	BACKROUND(2, PropertiesCache.getInstance().getProperty(PropertiesCache.SVGBACKGROUNDCOLOR)),
	
	/** The highlight. */
	HIGHLIGHT(3, PropertiesCache.getInstance().getProperty(PropertiesCache.SVGHIGHLIGHTCOLOR));

	/** The type. */
	private final int type;
	
	/** The name. */
	private final String name;

	/**
	 * Instantiates a new stroke color.
	 *
	 * @param type the type
	 * @param name the name
	 */
	private StrokeColor(int type, String name) {
		this.type = type;
		this.name = name;
	}

	/**
	 * Gets the.
	 *
	 * @param type the type
	 * @return the stroke color
	 */
	public static StrokeColor get(int type) {
		StrokeColor[] colors = StrokeColor.values();
		for (StrokeColor color : colors) {
			if (type == color.getType()) {
				return color;
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
	 * Gets the type.
	 *
	 * @return the type
	 */
	public int getType() {
		return type;
	}

}
