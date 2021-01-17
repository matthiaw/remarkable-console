package org.rogatio.productivity.remarkable.terminal.command;

import java.io.IOException;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "cls", aliases = "clear", mixinStandardHelpOptions = true, description = "Clears the screen", version = "1.0")
public class ClearScreenCommand implements Callable {

	@ParentCommand
	private CliCommands parent;

	public Void call() throws IOException {
		parent.reader.clearScreen();
		return null;
	}
}
