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
import static j2html.TagCreator.br;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.main;

import java.io.IOException;
import java.util.List;

import org.rogatio.remarkable.api.RemarkableManager;
import org.rogatio.remarkable.api.io.PropertiesCache;
import org.rogatio.remarkable.api.model.content.Content;

import j2html.tags.ContainerTag;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class HomeServlet.
 */
@WebServlet("/home")
public class HomeServlet extends BaseServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -512109101238411127L;

	/** The Constant EXPORT_PDF_HD. */
	private static final boolean EXPORT_PDF_HD = PropertiesCache.getInstance().getBoolean(PropertiesCache.PDFHDEXPORT);
	
	/** The Constant EXPORT_PDF_ALL. */
	private static final boolean EXPORT_PDF_ALL = PropertiesCache.getInstance()
			.getBoolean(PropertiesCache.PDFPAGESMERGED);

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
		rm.readContents();
		List<Content> docs = rm.getNotebooks();

		setTitle("Remarkable Console - Home");

		ContainerTag processor = a("Export").withHref("processor?download=true&read=true&export=true");

		render(response, main(div(processor).attr("style", "text-align:right"), br(), each(docs, d ->

		div(table(
				tbody(tr(td(image(d.getThumbnail(), "notebook?id=" + d.getId()))), tr(td(attrs(".title"), d.getName())),
						tr(td(attrs(".pages"), d.getPages().size() + " Seiten"), tr(td(attrs(".downloads"),

								iff(EXPORT_PDF_ALL, a("PDF").withHref("download?type=pdf&notebook=" + d.getId())),

								iff(EXPORT_PDF_HD, text(" ")),
								iff(EXPORT_PDF_HD, a("HD").withHref("download?type=pdfhd&notebook=" + d.getId()))

						))))))

		)));

	}

}
