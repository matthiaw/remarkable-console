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
package org.rogatio.productivity.remarkable.io.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class RequestClient.
 */
public class RequestClient {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(RequestClient.class);
	
	/**
	 * Post.
	 *
	 * @param authToken the auth token
	 * @param postUrl the post url
	 * @param data the data
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected String post(String authToken, String postUrl, String data) throws IOException {
		URL url = new URL(postUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", authToken);
		con.setDoOutput(true);
		this.sendData(con, data);
		if (con.getResponseCode() == 200) {
			logger.info("Post Request at "+postUrl);
			return this.read(con.getInputStream());
		} else {
			return null;
		}
	}

	/**
	 * Gets the.
	 *
	 * @param getUrl the get url
	 * @param authToken the auth token
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected String get(String getUrl, String authToken) throws IOException {
		URL url = new URL(getUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", authToken);
		connection.setDoInput(true);
		connection.connect();
		logger.info("Get Request at "+getUrl);
		return read(connection.getInputStream());
	}

	/**
	 * Send data.
	 *
	 * @param con the con
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void sendData(HttpURLConnection con, String data) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
			bw.write(data);
			bw.flush();
			bw.close();
		} catch (IOException exception) {
			logger.error("Error sending data", exception);
		} finally {
			this.closeQuietly(bw);
		}
	}

	/**
	 * Read.
	 *
	 * @param is the is
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected String read(InputStream is) throws IOException {
		BufferedReader in = null;
		String inputLine;
		StringBuilder body;
		try {
			in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			body = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				body.append(inputLine);
			}
			in.close();

			return body.toString();
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			this.closeQuietly(in);
		}
	}

	/**
	 * Close quietly.
	 *
	 * @param closeable the closeable
	 */
	protected void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ex) {
			logger.error("Error closing stream", ex);
		}
	}

	/**
	 * Gets the stream.
	 *
	 * @param url the url
	 * @param authToken the auth token
	 * @param file the file
	 * @return the stream
	 */
	protected void getStream(String url, String authToken, File file) {
		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).header("Authorization", authToken)
				.build();
		sendRequest(request, file);
	}

	/**
	 * Send request.
	 *
	 * @param request the request
	 * @param file the file
	 */
	protected void sendRequest(HttpRequest request, File file) {
		try {
			HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
			HttpResponse<Path> response = client.send(request, BodyHandlers.ofFile(file.toPath()));
			logger.info("Status "+response.statusCode() +" of streaming");
		} catch (IOException | InterruptedException e) {
			logger.error("Error streaming file", e);
		}
	}

}
