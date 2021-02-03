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
package org.rogatio.productivity.remarkable.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.rogatio.productivity.remarkable.RemarkableManager;
import org.rogatio.productivity.remarkable.terminal.ServletAppender;
import org.rogatio.productivity.remarkable.terminal.TerminalAppender;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/processor")
public class ProcessorServlet extends BaseServlet {

	private static final long serialVersionUID = -5957420611509472672L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		ServletAppender appender = (ServletAppender) config.getAppender("ServletAppender");
		appender.setWriter(out);

		RemarkableManager rm = RemarkableManager.getInstance();
		String download = request.getParameter("download");
		String read = request.getParameter("read");
		String export = request.getParameter("export");

		setTitle("Remarkable Console - Processor");

		if (download != null) {
			System.out.println("Download notebooks");
			rm.downloadContents();
		}
		if (read != null) {
			System.out.println("Read notebooks");
			rm.readContents();
		}
		if (export != null) {
			System.out.println("Export notebooks");
			rm.exportNotebooks();
		}
	}
}
