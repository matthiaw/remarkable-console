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

import static j2html.TagCreator.*;
import static j2html.TagCreator.main;

import java.io.IOException;
import java.util.List;

import org.rogatio.remarkable.api.RemarkableManager;
import org.rogatio.remarkable.api.io.PropertiesCache;
import org.rogatio.remarkable.api.model.content.Content;
import org.rogatio.remarkable.api.model.content.Page;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class NotebookServlet.
 */
@WebServlet("/notebook")
public class NotebookServlet extends BaseServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8455645168122367344L;

	/** The Constant EXPORT_PDF_HD. */
	private static final boolean EXPORT_PDF_HD = PropertiesCache.getInstance().getBoolean(PropertiesCache.PDFHDEXPORT);
	
	/** The Constant EXPORT_PDF_PAGES. */
	private static final boolean EXPORT_PDF_PAGES = PropertiesCache.getInstance()
			.getBoolean(PropertiesCache.PDFPAGESINGLE);

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
		Content nb = rm.getContentById(request.getParameter("id"));

		List<Page> pages = nb.getPages();

		setTitle("Remarkable Console - Notebook '" + nb.getName() + "'");

		render(response, main(each(pages, p ->

		div(table(tbody(tr(td(image(p.getThumbnail(), "navigation?notebook=" + nb.getId() + "&no=" + p.getPageNumber()))),
				tr(td(attrs(".downloads"),
						a("SVG").withHref("download?type=svg&notebook=" + nb.getId() + "&no=" + p.getPageNumber()),
						text(" "),
						a("PNG").withHref("download?type=png&notebook=" + nb.getId() + "&no=" + p.getPageNumber()),
						
						iff(EXPORT_PDF_PAGES, text(" ")), iff(EXPORT_PDF_PAGES, a("PDF")
								.withHref("download?type=pdf&notebook=" + nb.getId() + "&no=" + p.getPageNumber())),
						
						iff(EXPORT_PDF_HD, text(" ")), iff(EXPORT_PDF_HD, a("HD")
								.withHref("download?type=pdfhd&notebook=" + nb.getId() + "&no=" + p.getPageNumber()))

				)))))

		)));

	}

}
