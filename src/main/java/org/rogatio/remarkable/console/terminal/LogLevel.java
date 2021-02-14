/*
 * Remarkable API - Copyright (C) 2021 Matthias Wegner
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
package org.rogatio.remarkable.console.terminal;

import org.apache.logging.log4j.Level;

/**
 * The Enum LogLevel.
 */
public enum LogLevel {

	/** The info. */
	INFO(Level.INFO),
	/** The warn. */
	WARN(Level.WARN),
	/** The off. */
	OFF(Level.OFF),
	/** The trace. */
	TRACE(Level.TRACE),
	/** The fatal. */
	FATAL(Level.FATAL),
	/** The error. */
	ERROR(Level.ERROR),
	/** The debug. */
	DEBUG(Level.DEBUG),
	/** The all. */
	ALL(Level.ALL);

	/** The level. */
	private final Level level;

	/**
	 * Instantiates a new log level.
	 *
	 * @param level the level
	 */
	private LogLevel(Level level) {
		this.level = level;
	}

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public Level getLevel() {
		return level;
	}

}
