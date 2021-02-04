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

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.document;
import static j2html.TagCreator.footer;
import static j2html.TagCreator.head;
import static j2html.TagCreator.header;
import static j2html.TagCreator.html;
import static j2html.TagCreator.img;
import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.style;
import static j2html.TagCreator.title;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.rogatio.productivity.remarkable.io.file.Util;

import j2html.tags.ContainerTag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class BaseServlet.
 */
public class BaseServlet extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The style. */
	protected static ContainerTag style;

	/** The title. */
	private String title = "";

	static {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		InputStream is = classLoader.getResourceAsStream("styles.css");

		String stylesheet = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines()
				.collect(Collectors.joining("\n"));
		style = style().with(rawHtml(stylesheet));

		// ALTERNATIVE
		// File styleFile = new File(classLoader.getResource("styles.css").getFile());
		// style = styleWithInlineFile(styleFile.getAbsolutePath());

	}

	/**
	 * Render.
	 *
	 * @param response the response
	 * @param ct the ct
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void render(HttpServletResponse response, ContainerTag ct) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(document(html(head(title(getTitle())), body(style, header(), ct, footer()))));

	}

	/**
	 * Do post.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * Image.
	 *
	 * @param imgFile the img file
	 * @param href the href
	 * @return the container tag
	 */
	protected ContainerTag image(File imgFile, String href) {
		return a(img().withSrc(Util.imgToBase64String(imgFile))).withHref(href);
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	protected String getTitle() {
		return this.title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	protected void setTitle(String title) {
		this.title = title;
	}

}
