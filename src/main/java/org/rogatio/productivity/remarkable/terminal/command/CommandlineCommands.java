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

import java.io.PrintWriter;

import org.jline.reader.LineReader;
import org.jline.reader.impl.LineReaderImpl;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * The Class CommandlineCommands.
 */
@Command(name = "", description = "Interactive remarkable console with completion", footer = { "",
		"Use 'exit' to end console." }, subcommands = { ExitTerminalCommand.class, ClearScreenCommand.class,
				LogLevelCommand.class, NotebookCommand.class, NotebooksCommand.class, ServerCommand.class })
public class CommandlineCommands implements Runnable {
	
	/** The reader. */
	public LineReaderImpl reader;
	
	/** The out. */
	public PrintWriter out;

	/**
	 * Instantiates a new commandline commands.
	 */
	public CommandlineCommands() {
	}

	/**
	 * Sets the reader.
	 *
	 * @param reader the new reader
	 */
	public void setReader(LineReader reader) {
		this.reader = (LineReaderImpl) reader;
		out = reader.getTerminal().writer();
	}

	/**
	 * Run.
	 */
	public void run() {
		out.println(new CommandLine(this).getUsageMessage());
	}
}
