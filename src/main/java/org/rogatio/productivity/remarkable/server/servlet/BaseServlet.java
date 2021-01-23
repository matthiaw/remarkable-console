package org.rogatio.productivity.remarkable.server.servlet;

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.document;
import static j2html.TagCreator.footer;
import static j2html.TagCreator.head;
import static j2html.TagCreator.header;
import static j2html.TagCreator.html;
import static j2html.TagCreator.img;
import static j2html.TagCreator.styleWithInlineFile;
import static j2html.TagCreator.title;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.rogatio.productivity.remarkable.io.file.Util;

import j2html.tags.ContainerTag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static ContainerTag style;

	private String title = "";

	static {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File styleFile = new File(classLoader.getResource("styles.css").getFile());
		style = styleWithInlineFile(styleFile.getAbsolutePath());
	}

	protected void render(HttpServletResponse response, ContainerTag ct) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(document(html(head(title(getTitle())), body(style, header(), ct, footer()))));

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected ContainerTag image(File imgFile, String href) {
		return a(img().withSrc(Util.imgToBase64String(imgFile))).withHref(href);
	}

	protected String getTitle() {
		return this.title;
	}

	protected void setTitle(String title) {
		this.title = title;
	}

}
