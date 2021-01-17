package org.rogatio.productivity.remarkable.terminal.command;

import java.io.PrintWriter;

import org.jline.reader.LineReader;
import org.jline.reader.impl.LineReaderImpl;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "", description = "Interactive remarkable console with completion", footer = { "",
		"Use 'exit' to end console." }, subcommands = { ExitTerminalCommand.class,
				ClearScreenCommand.class, NotebooksCommand.class })
public class CliCommands implements Runnable {
	public LineReaderImpl reader;
	public PrintWriter out;

	public CliCommands() {
	}

	public void setReader(LineReader reader) {
		this.reader = (LineReaderImpl) reader;
		out = reader.getTerminal().writer();
	}

	public void run() {
		out.println(new CommandLine(this).getUsageMessage());
	}
}
