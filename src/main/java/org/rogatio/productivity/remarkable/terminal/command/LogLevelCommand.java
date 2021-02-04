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

/**
 * The Class LogLevelCommand.
 */
@Command(name = "log", mixinStandardHelpOptions = true, version = "1.0", description = "Chance log level")
public class LogLevelCommand implements Runnable {

	/**
	 * The Class LogLevelCandidates.
	 */
	private static class LogLevelCandidates implements Iterable<String> {

		/**
		 * Iterator.
		 *
		 * @return the iterator
		 */
		public Iterator<String> iterator() {
			ArrayList<String> list = new ArrayList<>();

			LogLevel[] xLevels = LogLevel.values();
			for (int i = 0; i < xLevels.length; i++) {
				list.add(xLevels[i].name());
			}

			return list.iterator();
		}
	}

	/** The levels. */
	@Parameters(description = "Logging level", completionCandidates = LogLevelCandidates.class)
	private List<String> levels = new ArrayList<String>();

	/** The parent. */
	@ParentCommand
	CommandlineCommands parent;

	/**
	 * Run.
	 */
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
			System.out.println("Log Level is set to '" + loggerConfig.getLevel() + "'");
			ctx.updateLoggers();
		} else {
			System.out.println("Log Level is " + loggerConfig.getLevel());
			return;
		}
	}
}
