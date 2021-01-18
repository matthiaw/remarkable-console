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
package org.rogatio.productivity.remarkable.terminal;

/**
 * The Class Prompt provides a simple prompt to the terminal
 */
public class Prompt {

	/** The Constant PREFIX. */
	private static final String PREFIX = "RM >";

	/**
	 * Gets the prefix.
	 *
	 * @return the prefix
	 */
	public static String getPrefix() {
		return TerminalColor.WHITE_BRIGHT + PREFIX + TerminalColor.RESET;
	}

	/**
	 * Gets the prefix.
	 *
	 * @param text the text
	 * @return the prefix
	 */
	public static String getPrefix(String text) {
		return getPrefix() + text;
	}

}
