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
package org.rogatio.productivity.remarkable.terminal;

import java.io.PrintWriter;
import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sound.midi.VoiceStatus;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Plugin(name = "ServletAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class ServletAppender extends AbstractAppender {

	/** The event map. */
	private ConcurrentMap<String, LogEvent> eventMap = new ConcurrentHashMap<>();

	/**
	 * Instantiates a new terminal appender.
	 *
	 * @param name   the name
	 * @param filter the filter
	 */
	@SuppressWarnings("deprecation")
	protected ServletAppender(String name, Filter filter) {
		super(name, filter, null);
	}

	@SuppressWarnings("deprecation")
	protected ServletAppender(String name, Filter filter, Layout<? extends Serializable> layout,
			boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
	}

	@PluginFactory
	public static ServletAppender createAppender(@PluginAttribute("name") String name,
			@PluginElement("Filter") Filter filter) {
		return new ServletAppender(name, filter);
	}

	/**
	 * Append log to terminal appender
	 *
	 * @param event the event
	 */
	@Override
	public void append(LogEvent event) {
		eventMap.put(Instant.now().toString(), event);

		if (writer != null) {
			String color = getColor(event);
			writer.println("<font face=\"arial,helvetica\" color=\"" + color + "\"><b>" + event.getLevel() + "</b></font> "+
					"<font face=\"arial,helvetica\">"+ event.getMessage().getFormattedMessage() + "</font><br>");
			writer.flush();
		}

	}

	private PrintWriter writer;

	public void setWriter(PrintWriter pw) {
		writer = pw;
	}

	private String getColor(LogEvent event) {
		String color = "#000000";
		if (event.getLevel() == Level.INFO) {
			color = "#00FF00";
		} else if (event.getLevel() == Level.WARN) {
			color = "#FFFF00";
		} else if (event.getLevel() == Level.ERROR) {
			color = "#FF0000";
		} else if (event.getLevel() == Level.DEBUG) {
			color = "#0000FF";
		}
		return color;
	}

}
