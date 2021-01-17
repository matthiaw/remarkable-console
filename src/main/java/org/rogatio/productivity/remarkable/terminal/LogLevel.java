package org.rogatio.productivity.remarkable.terminal;

import org.apache.logging.log4j.Level;

public enum LogLevel {

	INFO(Level.INFO), WARN(Level.WARN), OFF(Level.OFF), TRACE(Level.TRACE), FATAL(Level.FATAL), ERROR(Level.ERROR),
	DEBUG(Level.DEBUG), ALL(Level.ALL);

	private final Level level;

	private LogLevel(Level level) {
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

}
