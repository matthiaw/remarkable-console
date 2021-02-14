/*
 * Remarkable API - Copyright (C) 2021 Matthias Wegner
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
package org.rogatio.remarkable.console.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.rogatio.remarkable.api.io.PropertiesCache;
import org.rogatio.remarkable.console.server.servlet.DownloadPageServlet;
import org.rogatio.remarkable.console.server.servlet.HomeServlet;
import org.rogatio.remarkable.console.server.servlet.NotebookServlet;
import org.rogatio.remarkable.console.server.servlet.PageNavigationServlet;
import org.rogatio.remarkable.console.server.servlet.PageServlet;
import org.rogatio.remarkable.console.server.servlet.ProcessorServlet;

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
	
	public boolean isStarted() {
		return server.isStarted();
	}
	
	/**
	 * Start the server.
	 *
	 * @throws Exception the exception
	 */
	public void start() throws Exception {
		server = new Server();
		ServerConnector connector = new ServerConnector(server);
		
		int port = PropertiesCache.getInstance().getInt(PropertiesCache.SERVERPORT);
		connector.setPort(port);
		
		server.setConnectors(new Connector[] { connector });

		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(HomeServlet.class, "/");
		servletHandler.addServletWithMapping(NotebookServlet.class, "/notebook");
		servletHandler.addServletWithMapping(PageNavigationServlet.class, "/navigation");
		servletHandler.addServletWithMapping(PageServlet.class, "/page");
		servletHandler.addServletWithMapping(DownloadPageServlet.class, "/download");
		servletHandler.addServletWithMapping(ProcessorServlet.class, "/processor");
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
