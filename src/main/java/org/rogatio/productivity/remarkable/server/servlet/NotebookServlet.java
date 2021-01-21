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

import static j2html.TagCreator.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.rogatio.productivity.remarkable.RemarkableManager;
import org.rogatio.productivity.remarkable.io.file.Util;
import org.rogatio.productivity.remarkable.model.notebook.Notebook;
import org.rogatio.productivity.remarkable.model.notebook.Page;

import j2html.tags.DomContent;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class HomeServlet.
 */
@WebServlet("/notebook")
public class NotebookServlet extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new home servlet.
	 */
	public NotebookServlet() {
	}

	/**
	 * Do get.
	 *
	 * @param request  the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException      Signals that an I/O exception has occurred.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RemarkableManager rm = RemarkableManager.getInstance();
		Notebook nb = rm.getNotebookById(request.getParameter("id"));
		
		List<Page> pages = nb.getPages();
		
		render(response, head(title("Remarkable Console - Notebook '"+nb.getName()+"'")),

				body(header(), main(each(pages, p ->

				div(table(tbody(
						tr(td(a(img().attr("border", "1").withSrc(Util.imgToBase64String(p.getThumbnail())))
								.withHref("page?notebook="+nb.getId()+"&no=" + p.getPageNumber()))))))
								.withStyle("float: left;margin-right:10px")

				)), footer()));

	}

	/**
	 * Do post.
	 *
	 * @param request  the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException      Signals that an I/O exception has occurred.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * Render.
	 *
	 * @param response the response
	 * @param dc       the dom content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void render(HttpServletResponse response, DomContent... dc) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(document(html(dc)));
	}

}
