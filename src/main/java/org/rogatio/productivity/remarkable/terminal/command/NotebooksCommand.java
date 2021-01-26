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

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/**
 * The Class DownloadNotebooks.
 */
@Command(name = "notebooks",description = "Handle Notebooks",  version = "1.0", mixinStandardHelpOptions = true)
public class NotebooksCommand implements Callable {

	@ParentCommand
	private CommandlineCommands parent;

	@Option(names = { "-u", "--update" }, description = "Update notebooks")
	boolean update;

	@Option(names = { "-d", "--download" }, description = "Download notebooks")
	boolean download;

	@Option(names = { "-r", "--read" }, description = "Read notebooks")
	boolean read;

	@Option(names = { "-e", "--export" }, description = "Export notebooks")
	boolean export;

	@Option(names = { "-f", "--full" }, description = "Download, Read and Export notebooks")
	boolean full;
	
	public Void call() throws IOException {
		//System.out.print(Prompt.getPrefix(" "));
		if (update) {
			RemarkableManager.getInstance().updateContents();
		}
		if (download||full) {
			RemarkableManager.getInstance().downloadContents();
		}
		if (read||full) {
			RemarkableManager.getInstance().readContents();
		}
		if (export||full) {
			RemarkableManager.getInstance().exportNotebooks();
		}
		//System.out.println(Prompt.getPrefix(" "));
		return null;
	}

}