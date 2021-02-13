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
package org.rogatio.remarkable.console.terminal.command;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.rogatio.remarkable.api.RemarkableManager;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/**
 * The Class NotebooksCommand.
 */
@Command(name = "notebooks", description = "Handle Notebooks", version = "1.0", mixinStandardHelpOptions = true)
public class NotebooksCommand implements Callable<Object> {

	/** The parent. */
	@ParentCommand
	private CommandlineCommands parent;

	/** The update. */
	@Option(names = { "-u", "--update" }, description = "Update notebooks")
	boolean update;

	/** The download. */
	@Option(names = { "-d", "--download" }, description = "Download notebooks")
	boolean download;

	/** The read. */
	@Option(names = { "-r", "--read" }, description = "Read notebooks")
	boolean read;

	/** The export. */
	@Option(names = { "-e", "--export" }, description = "Export notebooks")
	boolean export;

	/** The full. */
	@Option(names = { "-f", "--full" }, description = "Download, Read and Export notebooks")
	boolean full;

	/**
	 * Call.
	 *
	 * @return the void
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Void call() throws IOException {
		if (update) {
			RemarkableManager.getInstance().updateContents();
		}
		if (download || full) {
			RemarkableManager.getInstance().downloadContents();
		}
		if (read || full) {
			RemarkableManager.getInstance().readContents();
		}
		if (export || full) {
			RemarkableManager.getInstance().exportNotebooks();
		}
		return null;
	}

}