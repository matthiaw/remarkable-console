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

import org.rogatio.productivity.remarkable.RemarkableManager;
import org.rogatio.productivity.remarkable.server.EmbeddedServer;
import org.rogatio.productivity.remarkable.terminal.Prompt;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/**
 * The Class DownloadNotebooks.
 */
@Command(name = "server", description = "Manage embedded server", version = "1.0", mixinStandardHelpOptions = true)
public class ServerCommand implements Callable {

	private EmbeddedServer server = new EmbeddedServer();

	@ParentCommand
	private CommandlineCommands parent;

	@Option(names = { "--start" }, description = "Start Server")
	boolean start;

	@Option(names = { "--stop" }, description = "Stop Server")
	boolean stop;

	public Void call() throws IOException {
		//System.out.print(Prompt.getPrefix(" "));
		try {

			if (start) {
				server.start();
				//System.out.println(Prompt.getPrefix(" "));
			}
			if (stop) {
				server.stop();
			}
		} catch (Exception e) {
		}
		
		//System.out.println(Prompt.getPrefix(" "));
		return null;
	}

}