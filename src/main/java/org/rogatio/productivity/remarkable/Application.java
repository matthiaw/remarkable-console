package org.rogatio.productivity.remarkable;

import static org.rogatio.productivity.remarkable.io.PropertiesCache.DEVICETOKEN;

import java.io.File;
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
import org.rogatio.productivity.remarkable.terminal.command.CliCommands;

import picocli.CommandLine;
import picocli.shell.jline3.PicocliJLineCompleter;

public class Application {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(Application.class);

	/** The Constant scanner. */
	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		// instantiates the remarkable manager
		RemarkableManager rm = init();

		// download templates via ssh from remarkable (need both in same network)
		// if templates not exist
		downloadTemplates(rm);

		try {
			// set up the completion
			CliCommands commands = new CliCommands();
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

		// close application
		close();
	}

	private static RemarkableManager auth() {
		// check if deviceToken for this remarkable client exists
		boolean deviceTokenExists = PropertiesCache.getInstance().propertyExists(DEVICETOKEN);

//		RemarkableManager rm = null;
		if (!deviceTokenExists) {
//			 instantiates remarkable manager
//			rm = RemarkableManager.getInstance();
//			logger.info("Device Token found");
//		} else {
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

//				 instantiates remarkable client
//				rm = new RemarkableManager(createdToken);
				logger.info("New Device Token for client created");
			} catch (IOException e) {
				logger.error("Error creating device token");
			} finally {
				sc.close();
			}
		}
		return RemarkableManager.getInstance();
	}

	private static RemarkableManager init() {
		// install ansi colors for console
		AnsiConsole.systemInstall();

		// print header to console
		System.out.println(TerminalHeader.getHeader());

		// authenticate
		return auth();
	}

	private static void downloadTemplates(RemarkableManager rm) {
		// read template folder from properties
		String templateDir = PropertiesCache.getInstance().getProperty(PropertiesCache.TEMPLATEFOLDER);

		// download templates via ssh if not existing
		if (!new File(templateDir).exists()) {
			rm.downloadTemplates();
		}
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
}
