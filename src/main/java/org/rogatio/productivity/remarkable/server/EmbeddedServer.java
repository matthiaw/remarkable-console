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
package org.rogatio.productivity.remarkable.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.server.servlet.DownloadPageServlet;
import org.rogatio.productivity.remarkable.server.servlet.HomeServlet;
import org.rogatio.productivity.remarkable.server.servlet.NotebookServlet;
import org.rogatio.productivity.remarkable.server.servlet.PageServlet;

/**
 * The Class EmbeddedServer.
 */
public class EmbeddedServer {

	/** The server. */
	private Server server;

	/**
	 * Stop.
	 *
	 * @throws Exception the exception
	 */
	public void stop() throws Exception {
		server.stop();
	}
	
	/**
	 * Start the server.
	 *
	 * @throws Exception the exception
	 */
	public void start() throws Exception {
		server = new Server();
		ServerConnector connector = new ServerConnector(server);
		
		int port = PropertiesCache.getInstance().getPropertyInt(PropertiesCache.SERVERPORT);
		connector.setPort(port);
		
		server.setConnectors(new Connector[] { connector });

		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(HomeServlet.class, "/");
		servletHandler.addServletWithMapping(NotebookServlet.class, "/notebook");
		servletHandler.addServletWithMapping(PageServlet.class, "/page");
		servletHandler.addServletWithMapping(DownloadPageServlet.class, "/download");
		server.setHandler(servletHandler);

		server.start();
	}
	
	/**
	 * Join server thread
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	public void join() throws InterruptedException {
		server.join();
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		EmbeddedServer server = new EmbeddedServer();
		server.start();
	}

}
