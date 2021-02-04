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
package org.rogatio.productivity.remarkable.terminal.command;

import java.io.IOException;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

/**
 * The Class ExitTerminalCommand.
 */
@Command(name = "exit", description = "Exit the terminal", version = "1.0", mixinStandardHelpOptions = true)
public class ExitTerminalCommand implements Callable<Object> {

	/** The parent. */
	@ParentCommand
	private CommandlineCommands parent;

	/**
	 * Call.
	 *
	 * @return the void
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Void call() throws IOException {
		System.exit(0);
		return null;
	}

}