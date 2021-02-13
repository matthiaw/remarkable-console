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

import java.util.HashMap;
import java.util.Map;

import picocli.CommandLine;

/**
 * The Class TerminalCommander. Not used anymore.
 */
@Deprecated
public class TerminalCommander {

	/** The commands. */
	private Map<String, CommandLine> commands = new HashMap<>();

	/**
	 * Adds the.
	 *
	 * @param command the command
	 */
	public void add(CommandLine command) {
		String name = command.getCommandName();
		commands.put(name.toLowerCase(), command);
	}

	/**
	 * Excecute the command.
	 *
	 * @param command the command
	 * @return the int
	 */
	public int excecute(String command) {
		int idx = command.indexOf(" ");
		if (idx != -1) {
			String name = command.substring(0, idx).trim();
			String args = command.substring(idx, command.length()).trim();
			CommandLine cmdLine = commands.get(name);
			if (cmdLine != null) {
				return cmdLine.execute(args.split(" "));
			}
		}
		return -1;
	}

}
