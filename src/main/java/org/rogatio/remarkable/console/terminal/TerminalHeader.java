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

import org.rogatio.remarkable.api.io.PropertiesCache;

/**
 * The Class TerminalHeader.
 */
public class TerminalHeader {

	/** The Constant HEADER. */
	private static final String HEADER = 
			  "__________               _________                \r\n"
			+ "\\______   \\ ____   _____ \\_   ___ \\  ____   ____  \r\n"
			+ " |       _// __ \\ /     \\/    \\  \\/ /  _ \\ /    \\ \r\n"
			+ " |    |   \\  ___/|  Y Y  \\     \\___(  <_> )   |  \\\r\n"
			+ " |____|_  /\\___  >__|_|  /\\______  /\\____/|___|  /\r\n"
			+ "        \\/     \\/      \\/        \\/            \\/ \r\n";

	/** The Constant HEADERFOOTER. */
	private static final String HEADERFOOTER =

			"RemCom - Remarkable Console (" + PropertiesCache.getInstance().getValue(PropertiesCache.VERSION)
					+ ")\r\n";

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public static String getHeader() {

		String h = new String();
		for (char letter : HEADER.toCharArray()) {
			h += TerminalColor.getRandomForegroundColor() + letter + TerminalColor.RESET;
		}

		return h + TerminalColor.BLACK_BACKGROUND + TerminalColor.WHITE_BRIGHT + HEADERFOOTER + TerminalColor.RESET;
	}
}
