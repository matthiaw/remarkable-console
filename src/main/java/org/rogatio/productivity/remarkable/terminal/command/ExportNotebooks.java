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

import org.rogatio.productivity.remarkable.RemarkableManager;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * The Class ExportNotebooks.
 */
@Command(name = "Export", version = "Export 1.0", mixinStandardHelpOptions = true)
public class ExportNotebooks implements Runnable {

	/** The remarkable manager. */
	private RemarkableManager remarkableManager;

	/**
	 * Instantiates a new export notebooks.
	 *
	 * @param remarkableManager the remarkable manager
	 */
	public ExportNotebooks(RemarkableManager remarkableManager) {
		this.remarkableManager = remarkableManager;
	}

	/** The all. */
	@Option(names = { "-n", "--notebook" }, description = "Export notebooks")
	boolean all;

	/**
	 * Run.
	 */
	@Override
	public void run() {
		if (all) {
			remarkableManager.exportNotebooks();
		}
	}

}