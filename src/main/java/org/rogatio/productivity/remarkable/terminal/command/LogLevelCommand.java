package org.rogatio.productivity.remarkable.terminal.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.rogatio.productivity.remarkable.terminal.LogLevel;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Command(name = "log", mixinStandardHelpOptions = true, version = "1.0", description = "Chance log level")
public class LogLevelCommand implements Runnable {

//	@Option(names = { "-l", "--level" })
//	private LogLevel level;

	private static class LogLevelCandidates implements Iterable<String> {
		public Iterator<String> iterator() {
			ArrayList<String> list = new ArrayList<>();

			LogLevel[] xLevels = LogLevel.values();
			for (int i = 0; i < xLevels.length; i++) {
				list.add(xLevels[i].name());
			}

			return list.iterator();
		}
	}

	@Parameters(description = "Logging level", completionCandidates = LogLevelCandidates.class)
	private List<String> levels = new ArrayList<String>();

	@ParentCommand
	CommandlineCommands parent;

	public void run() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);

		if (levels.size() > 0) {
			String level = levels.get(0);

			LogLevel logLevel = LogLevel.valueOf(level);

			if (logLevel == null) {
				System.out.println("Log Level is " + loggerConfig.getLevel());
				return;
			}

			loggerConfig.setLevel(logLevel.getLevel());
			System.out.println("Log Level is set to '" + loggerConfig.getLevel()+"'");
			ctx.updateLoggers();
		} else {
			System.out.println("Log Level is " + loggerConfig.getLevel());
			return;
		}
	}
}
