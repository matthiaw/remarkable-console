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
package org.rogatio.remarkable.console.server.servlet;

import static j2html.TagCreator.fileAsString;
import static j2html.TagCreator.main;

import java.io.IOException;

import org.rogatio.remarkable.api.RemarkableManager;
import org.rogatio.remarkable.api.io.file.Util;
import org.rogatio.remarkable.api.model.content.Content;
import org.rogatio.remarkable.api.model.content.Page;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class PageServlet.
 */
@WebServlet("/page")
public class PageServlet extends BaseServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5957420611509472672L;

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
		Content nb = rm.getContentById(request.getParameter("notebook"));
		Page p = nb.getPage(Integer.parseInt(request.getParameter("no")));
		String svgPath = Util.getFilename(p, "svg");

		setTitle("Remarkable Console - Notebook '" + nb.getName() + "' - Page No. " + p.getPageNumber());

		render(response, main(fileAsString(svgPath)));

	}
}
