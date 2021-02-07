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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	 * @param postUrl   the post url
	 * @param data      the data
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
			logger.info("Post Request at " + postUrl);
			return this.read(con.getInputStream());
		} else {
			return null;
		}
	}

	protected String put(String putUrl, String authToken, String data) throws IOException {
		URL url = new URL(putUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("PUT");
		con.setRequestProperty("Authorization", authToken);
		con.setDoOutput(true);
		this.sendData(con, data);
//		System.out.println(con.getResponseCode());
//		System.out.println(con.getResponseMessage());
		if (con.getResponseCode() == 200) {
			logger.info("Put Request at " + putUrl);
			return this.read(con.getInputStream());
		} else {
			return null;
		}
	}

	protected String put(String putUrl, String authToken, File data) throws IOException {
		URL url = new URL(putUrl);

		FileInputStream fis = new FileInputStream(data);
		byte[] fileContents = IOUtils.toByteArray(fis);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("PUT");
		con.setRequestProperty("Authorization", authToken);
		con.setDoOutput(true);

		// System.out.println(fileContents.length);
		this.sendData(con, fileContents);
		System.out.println(con.getResponseCode());
		System.out.println(con.getResponseMessage());
		if (con.getResponseCode() == 200) {
			logger.info("Put Request at " + putUrl);
			return this.read(con.getInputStream());
		} else {
			return this.read(con.getErrorStream());
		}
	}

	public String put(String url, String token, List<Object> payload) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		//DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'+02:00'");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'+02:00'");
		mapper.setDateFormat(df);

		String json = "";
		try {
			json = mapper.writeValueAsString(payload);
			System.out.println(json);
		} catch (JsonProcessingException e) {
		}

		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).PUT(BodyPublishers.ofString(json))
				.header("Authorization", token).build();
		return sendRequest(request);
	}

	public String putStream(String url, String token, File file) {
		try {
			HttpRequest request = HttpRequest.newBuilder(URI.create(url)).PUT(BodyPublishers.ofFile(file.toPath()))
					.header("Authorization", token).build();
			return sendRequest(request);
		} catch (FileNotFoundException e) {
			logger.error("file does not exist", e);
		}
		return "";
	}

	private String sendRequest(HttpRequest request) {
		String res = null;

		try {
			logger.debug(request.uri());
			logger.debug(Arrays.asList(request.headers()));
			HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			logger.debug(response.statusCode());
			if (response.statusCode() == 200) {
				res = response.body();
			} else {
				logger.warn(response.body());
			}
		} catch (IOException | InterruptedException e) {
			logger.error("Error while launching request", e);
		}

		return res;
	}

	/**
	 * Gets the.
	 *
	 * @param getUrl    the get url
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
		logger.info("Get Request at " + getUrl);
		return read(connection.getInputStream());
	}

	protected void sendData(HttpURLConnection con, byte[] data) throws IOException {
		OutputStream bw = null;
		try {
			bw = con.getOutputStream();
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
	 * Send data.
	 *
	 * @param con  the con
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
	 * @param url       the url
	 * @param authToken the auth token
	 * @param file      the file
	 * @return the stream
	 */
	protected void getStream(String url, String authToken, File file) {
		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).header("Authorization", authToken).build();
		sendRequest(request, file);
	}

	protected void sendRequest(HttpRequest request, File file) {
		try {
			HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
			HttpResponse<Path> response = client.send(request, BodyHandlers.ofFile(file.toPath()));
			logger.debug("Status " + response.statusCode() + " of streaming");
		} catch (IOException | InterruptedException e) {
			logger.error("Error streaming file", e);
		}
	}

}
