package org.rogatio.productivity.remarkable.terminal.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.rogatio.productivity.remarkable.terminal.LogLevel;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(name = "log", mixinStandardHelpOptions = true, version = "1.0", description = "Chance log level")
public class LogLevelCommand implements Runnable {

	@Option(names = { "-l", "--level" })
	private LogLevel level;

	@ParentCommand
	CommandlineCommands parent;

	public void run() {
		
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		
		if (level==null) {
			System.out.println("Log Level is "+loggerConfig.getLevel());
			return;
		}
		
		loggerConfig.setLevel(level.getLevel());
		ctx.updateLoggers();
	}
}
