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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.io.RemarkableClient;
import org.rogatio.productivity.remarkable.terminal.Prompt;
import org.rogatio.productivity.remarkable.terminal.TerminalHeader;
import org.rogatio.productivity.remarkable.terminal.command.CommandlineCommands;

import picocli.CommandLine;
import picocli.shell.jline3.PicocliJLineCompleter;

/**
 * The Class Application.
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

		// build and configure terminal
		buildTerminal();

		// close application
		close();
	}

	/**
	 * Builds the terminal.
	 */
	private static void buildTerminal() {
		try {
			// set up the completion
			CommandlineCommands commands = new CommandlineCommands();
			CommandLine cmd = new CommandLine(commands);
			Terminal terminal = TerminalBuilder.builder().build();
			LineReader reader = LineReaderBuilder.builder().terminal(terminal)
					.completer(new PicocliJLineCompleter(cmd.getCommandSpec())).parser(new DefaultParser()).build();
			commands.setReader(reader);
			// String prompt = "prompt> ";
			String rightPrompt = null;

			// start the shell and process input until the user quits with Ctl-D
			String line;
			while (true) {
				try {
					line = reader.readLine(Prompt.getPrefix(" "), rightPrompt, (MaskingCallback) null, null);
					ParsedLine pl = reader.getParser().parse(line, 0);
					String[] arguments = pl.words().toArray(new String[0]);
					CommandLine.run(commands, arguments);
				} catch (UserInterruptException e) {
					// Ignore
				} catch (EndOfFileException e) {
					return;
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Authenticate application against remarkable web application
	 *
	 * @return the remarkable manager
	 */
	private static RemarkableManager auth() {
		// check if deviceToken for this remarkable client exists
		boolean deviceTokenExists = PropertiesCache.getInstance().propertyExists(DEVICETOKEN);

		if (!deviceTokenExists) {
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

				logger.info("New Device Token for client created");
			} catch (IOException e) {
				logger.error("Error creating device token");
			} finally {
				sc.close();
			}
		}
		return RemarkableManager.getInstance();
	}

	/**
	 * Inits the application
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
	 * Download svg templates.
	 *
	 * @param rm the remarkable manager
	 */
	private static void downloadTemplates(RemarkableManager rm) {
		// read template folder from properties
		String templateDir = PropertiesCache.getInstance().getProperty(PropertiesCache.TEMPLATEFOLDER);

		// download templates via ssh if not existing
		if (!new File(templateDir).exists()) {

			boolean hostIpExists = PropertiesCache.getInstance().propertyExists(PropertiesCache.SSHHOST);
			boolean hostPswdExists = PropertiesCache.getInstance().propertyExists(PropertiesCache.SSHPSWD);

			if (!hostIpExists || !hostPswdExists) {
				System.out.println(
						"Open in remarkable Settings > About > Copyrights and licenses > General information (scroll down):");
				Scanner sc = new Scanner(System.in);

				if (!hostIpExists) {
					System.out.println("Enter IP-Host-Adress:");
					PropertiesCache.getInstance().setProperty(PropertiesCache.SSHHOST, sc.nextLine().trim());
				}

				if (!hostPswdExists) {
					System.out.println("Enter Password (for root):");
					PropertiesCache.getInstance().setProperty(PropertiesCache.SSHPSWD, sc.nextLine().trim());
				}

				try {
					PropertiesCache.getInstance().flush();
				} catch (FileNotFoundException e) {
					logger.error("Error setting ssh properties", e);
				} catch (IOException e) {
					logger.error("Error setting ssh properties", e);
				} finally {
					sc.close();
				}
			}

			logger.info("New SSH configuration created");

			// download templates with ssh-connection
			rm.downloadTemplates();
		}
	}

	/**
	 * Close application.
	 */
	private static void close() {
		// close scanner
		scanner.close();

		// uninstall ansi colors
		AnsiConsole.systemUninstall();
	}
}
