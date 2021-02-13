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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.rogatio.remarkable.api.RemarkableManager;
import org.rogatio.remarkable.api.io.PropertiesCache;
import org.rogatio.remarkable.api.io.file.Util;
import org.rogatio.remarkable.api.model.web.ContentMetaData;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * The Class NotebookCommand.
 */
@Command(name = "notebook", description = "Handle Notebook", version = "1.0", mixinStandardHelpOptions = true)
public class NotebookCommand implements Callable<Object> {

	/** The Constant DOCUMENT_STORAGE. */
	private static final String DOCUMENT_STORAGE = PropertiesCache.getInstance()
			.getValue(PropertiesCache.NOTEBOOKFOLDER);

	/** The parent. */
	@ParentCommand
	private CommandlineCommands parent;

	/** The download. */
	@Option(names = { "-d", "--download" }, description = "Download notebook")
	boolean download;

	/** The read. */
	@Option(names = { "-r", "--read" }, description = "Read notebook")
	boolean read;

	/** The export. */
	@Option(names = { "-e", "--export" }, description = "Export notebook")
	boolean export;

	/** The full. */
	@Option(names = { "-f", "--full" }, description = "Download, Read and Export notebook")
	boolean full;

	/**
	 * The Class NotebookCompletionCandidates.
	 */
	private static class NotebookCompletionCandidates implements Iterable<String> {
		
		/**
		 * Iterator.
		 *
		 * @return the iterator
		 */
		public Iterator<String> iterator() {
			ArrayList<File> files = Util.listFiles(new File(DOCUMENT_STORAGE), "meta");
			ArrayList<String> notebookStrings = new ArrayList<>();
			for (File file : files) {
				String f = file.toString().toString().replace(DOCUMENT_STORAGE + File.separatorChar, "")
						.replace(".meta", "");

				f = f.replace("" + File.separatorChar, "_");

				notebookStrings.add(f);
			}

			return notebookStrings.iterator();
		}
	}

	/** The files. */
	@Parameters(description = "Name of notebook", arity = "1", completionCandidates = NotebookCompletionCandidates.class)
	private List<String> files = new ArrayList<String>();

	/**
	 * Call.
	 *
	 * @return the void
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Void call() throws IOException {

		if (files.size() == 1) {

			String name = files.get(0);

			RemarkableManager rm = RemarkableManager.getInstance();

			ContentMetaData meta = rm.getMetaDataByFolderAndName(name);

			if (download || full) {
				rm.downloadContent(meta);
			}
			if (read || full) {
				rm.readContent(meta);
			}
			if (export || full) {
				rm.exportNotebook(meta);
			}
		}
		return null;
	}

}