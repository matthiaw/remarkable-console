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
package org.rogatio.remarkable.console.terminal;

import java.util.Random;

/**
 * The Class TerminalColor.
 */
public class TerminalColor {

	// Reset

	/** The Constant RESET. */
	public static final String RESET = "\033[0m"; // Text Reset

	// Regular Colors

	/** The Constant BLACK. */
	public static final String BLACK = "\033[0;30m"; // BLACK

	/** The Constant RED. */
	public static final String RED = "\033[0;31m"; // RED

	/** The Constant GREEN. */
	public static final String GREEN = "\033[0;32m"; // GREEN

	/** The Constant YELLOW. */
	public static final String YELLOW = "\033[0;33m"; // YELLOW

	/** The Constant BLUE. */
	public static final String BLUE = "\033[0;34m"; // BLUE

	/** The Constant PURPLE. */
	public static final String PURPLE = "\033[0;35m"; // PURPLE

	/** The Constant CYAN. */
	public static final String CYAN = "\033[0;36m"; // CYAN

	/** The Constant WHITE. */
	public static final String WHITE = "\033[0;37m"; // WHITE

	// Bold

	/** The Constant BLACK_BOLD. */
	public static final String BLACK_BOLD = "\033[1;30m"; // BLACK

	/** The Constant RED_BOLD. */
	public static final String RED_BOLD = "\033[1;31m"; // RED

	/** The Constant GREEN_BOLD. */
	public static final String GREEN_BOLD = "\033[1;32m"; // GREEN

	/** The Constant YELLOW_BOLD. */
	public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW

	/** The Constant BLUE_BOLD. */
	public static final String BLUE_BOLD = "\033[1;34m"; // BLUE

	/** The Constant PURPLE_BOLD. */
	public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE

	/** The Constant CYAN_BOLD. */
	public static final String CYAN_BOLD = "\033[1;36m"; // CYAN

	/** The Constant WHITE_BOLD. */
	public static final String WHITE_BOLD = "\033[1;37m"; // WHITE

	// Underline

	/** The Constant BLACK_UNDERLINED. */
	public static final String BLACK_UNDERLINED = "\033[4;30m"; // BLACK

	/** The Constant RED_UNDERLINED. */
	public static final String RED_UNDERLINED = "\033[4;31m"; // RED

	/** The Constant GREEN_UNDERLINED. */
	public static final String GREEN_UNDERLINED = "\033[4;32m"; // GREEN

	/** The Constant YELLOW_UNDERLINED. */
	public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW

	/** The Constant BLUE_UNDERLINED. */
	public static final String BLUE_UNDERLINED = "\033[4;34m"; // BLUE

	/** The Constant PURPLE_UNDERLINED. */
	public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE

	/** The Constant CYAN_UNDERLINED. */
	public static final String CYAN_UNDERLINED = "\033[4;36m"; // CYAN

	/** The Constant WHITE_UNDERLINED. */
	public static final String WHITE_UNDERLINED = "\033[4;37m"; // WHITE

	// Background

	/** The Constant BLACK_BACKGROUND. */
	public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK

	/** The Constant RED_BACKGROUND. */
	public static final String RED_BACKGROUND = "\033[41m"; // RED

	/** The Constant GREEN_BACKGROUND. */
	public static final String GREEN_BACKGROUND = "\033[42m"; // GREEN

	/** The Constant YELLOW_BACKGROUND. */
	public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW

	/** The Constant BLUE_BACKGROUND. */
	public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE

	/** The Constant PURPLE_BACKGROUND. */
	public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE

	/** The Constant CYAN_BACKGROUND. */
	public static final String CYAN_BACKGROUND = "\033[46m"; // CYAN

	/** The Constant WHITE_BACKGROUND. */
	public static final String WHITE_BACKGROUND = "\033[47m"; // WHITE

	// High Intensity

	/** The Constant BLACK_BRIGHT. */
	public static final String BLACK_BRIGHT = "\033[0;90m"; // BLACK

	/** The Constant RED_BRIGHT. */
	public static final String RED_BRIGHT = "\033[0;91m"; // RED

	/** The Constant GREEN_BRIGHT. */
	public static final String GREEN_BRIGHT = "\033[0;92m"; // GREEN

	/** The Constant YELLOW_BRIGHT. */
	public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW

	/** The Constant BLUE_BRIGHT. */
	public static final String BLUE_BRIGHT = "\033[0;94m"; // BLUE

	/** The Constant PURPLE_BRIGHT. */
	public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE

	/** The Constant CYAN_BRIGHT. */
	public static final String CYAN_BRIGHT = "\033[0;96m"; // CYAN

	/** The Constant WHITE_BRIGHT. */
	public static final String WHITE_BRIGHT = "\033[0;97m"; // WHITE

	// Bold High Intensity

	/** The Constant BLACK_BOLD_BRIGHT. */
	public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK

	/** The Constant RED_BOLD_BRIGHT. */
	public static final String RED_BOLD_BRIGHT = "\033[1;91m"; // RED

	/** The Constant GREEN_BOLD_BRIGHT. */
	public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN

	/** The Constant YELLOW_BOLD_BRIGHT. */
	public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW

	/** The Constant BLUE_BOLD_BRIGHT. */
	public static final String BLUE_BOLD_BRIGHT = "\033[1;94m"; // BLUE

	/** The Constant PURPLE_BOLD_BRIGHT. */
	public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE

	/** The Constant CYAN_BOLD_BRIGHT. */
	public static final String CYAN_BOLD_BRIGHT = "\033[1;96m"; // CYAN

	/** The Constant WHITE_BOLD_BRIGHT. */
	public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

	// High Intensity backgrounds

	/** The Constant BLACK_BACKGROUND_BRIGHT. */
	public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK

	/** The Constant RED_BACKGROUND_BRIGHT. */
	public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED

	/** The Constant GREEN_BACKGROUND_BRIGHT. */
	public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN

	/** The Constant YELLOW_BACKGROUND_BRIGHT. */
	public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW

	/** The Constant BLUE_BACKGROUND_BRIGHT. */
	public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE

	/** The Constant PURPLE_BACKGROUND_BRIGHT. */
	public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE

	/** The Constant CYAN_BACKGROUND_BRIGHT. */
	public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m"; // CYAN

	/** The Constant WHITE_BACKGROUND_BRIGHT. */
	public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m"; // WHITE

	/**
	 * See
	 * https://stackoverflow.com/questions/59373280/is-it-possible-to-color-java-output-in-terminal-using-rgb-or-hex-colors
	 *
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @return the foreground
	 */
	public static String getForeground(int r, int g, int b) {
		return "\033[38;2;" + r + ";" + g + ";" + b + "m";
	}

	/**
	 * Gets the random foreground color.
	 *
	 * @return the random foreground color
	 */
	public static String getRandomForegroundColor() {
		
		Random random = new Random();
		int r = random.nextInt(255);
		int g = random.nextInt(255);
		int b = random.nextInt(255);
		
		return getForeground(r,g,b);
	}
	
	/**
	 * See
	 * https://stackoverflow.com/questions/59373280/is-it-possible-to-color-java-output-in-terminal-using-rgb-or-hex-colors
	 *
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @return the background
	 */
	public static String getBackground(int r, int g, int b) {
		return "\033[48;2;" + r + ";" + g + ";" + b + "m";
	}

}
