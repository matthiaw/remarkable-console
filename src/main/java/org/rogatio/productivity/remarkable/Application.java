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
package org.rogatio.productivity.remarkable;

import static org.rogatio.productivity.remarkable.io.PropertiesCache.DEVICETOKEN;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.io.RemarkableClient;
import org.rogatio.productivity.remarkable.terminal.Prompt;
import org.rogatio.productivity.remarkable.terminal.TerminalCommander;
import org.rogatio.productivity.remarkable.terminal.TerminalHeader;
import org.rogatio.productivity.remarkable.terminal.command.DownloadNotebooks;
import org.rogatio.productivity.remarkable.terminal.command.ExportNotebooks;
import org.rogatio.productivity.remarkable.terminal.command.ReadNotebooks;

import picocli.CommandLine;

/**
 * The Class Application starts the main application of the remarkable console
 * 
 * @author Matthias Wegner
 */
public class Application {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(Application.class);

	/** The Constant scanner. */
	private static final Scanner scanner = new Scanner(System.in);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		// instantiates the remarkable manager
		RemarkableManager rm = init();

		// download templates via ssh from remarkable (need both in same network)
		// if templates not exist
		downloadTemplates(rm);

		// rm.downloadNotebook("Test");
		// rm.readNotebook("Skizzen");
		// RemarkablePage page = rm.getPage("Skizzen", 3);
		// Util.createSvg(rm.getNotebook("Test"));

		// instantiates terminal commander and add valid commands
		TerminalCommander commander = new TerminalCommander();
		commander.add(new CommandLine(new DownloadNotebooks(rm)));
		commander.add(new CommandLine(new ExportNotebooks(rm)));
		commander.add(new CommandLine(new ReadNotebooks(rm)));

		// write first prompt
		System.out.printf(Prompt.getPrefix(" "));

		// wait for entry on console
		while (scanner.hasNext()) {
			// read entered line
			String line = scanner.nextLine();

			// exit application if exit is typed
			if (line.equalsIgnoreCase("exit")) {
				break;
			}

			// execute commandline
			commander.excecute(line);

			// refresh prompt after excecution
			System.out.printf(Prompt.getPrefix(" "));
		}

		// close application
		close();
	}

	/**
	 * Inits the remarkable manager
	 *
	 * @return the remarkable manager
	 */
	private static RemarkableManager init() {
		// install ansi colors for console
		AnsiConsole.systemInstall();

		// print header to console
		System.out.println(TerminalHeader.getHeader());

		// authenticate
		return auth();
	}

	/**
	 * Close application
	 */
	private static void close() {
		// close scanner
		scanner.close();

		// uninstall ansi colors
		AnsiConsole.systemUninstall();
	}

	/**
	 * Download templates.
	 *
	 * @param rm the rm
	 */
	private static void downloadTemplates(RemarkableManager rm) {
		// read template folder from properties
		String templateDir = PropertiesCache.getInstance().getProperty(PropertiesCache.TEMPLATEFOLDER);

		// download templates via ssh if not existing
		if (!new File(templateDir).exists()) {
			rm.downloadTemplates();
		}
	}

	/**
	 * Authenticate application to web cloud
	 *
	 * @return the remarkable manager
	 */
	private static RemarkableManager auth() {
		// check if deviceToken for this remarkable client exists
		boolean deviceTokenExists = PropertiesCache.getInstance().propertyExists(DEVICETOKEN);

		RemarkableManager rm = null;
		if (deviceTokenExists) {
			// load token from properties
			String deviceToken = PropertiesCache.getInstance().getProperty(DEVICETOKEN);
			// instantiates remarkable manager
			rm = new RemarkableManager(deviceToken);
			logger.info("Device Token found");
		} else {
			// instantiates remarkable client
			RemarkableClient rc = new RemarkableClient();

			// prepare input for one-time-code
			System.out.println(
					"Device not registered yet. Please input OneTimeCode from https://my.remarkable.com/connect/desktop:");
			Scanner sc = new Scanner(System.in);
			try {
				// get new created device token for this client
				String createdToken = rc.newDeviceToken(sc.nextLine().trim());

				// write token to client
				PropertiesCache.getInstance().setProperty(DEVICETOKEN, createdToken);
				PropertiesCache.getInstance().flush();

				// instantiates remarkable client
				rm = new RemarkableManager(createdToken);
				logger.info("New Device Token for client created");
			} catch (IOException e) {
				logger.error("Error creating device token");
			} finally {
				sc.close();
			}
		}
		return rm;
	}

}
