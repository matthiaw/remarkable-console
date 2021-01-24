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

import static j2html.TagCreator.fileAsString;
import static j2html.TagCreator.main;

import java.io.IOException;

import org.rogatio.productivity.remarkable.RemarkableManager;
import org.rogatio.productivity.remarkable.io.file.Util;
import org.rogatio.productivity.remarkable.model.content.Content;
import org.rogatio.productivity.remarkable.model.content.Page;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class HomeServlet.
 */
@WebServlet("/page")
public class PageServlet extends BaseServlet {

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

//				script("var svg = document.querySelector('svg');\r\n"
//				+ "\r\n"
//				+ "var svgWidth = parseFloat(svg.getAttributeNS(null, \"width\"));\r\n"
//				+ "var svgHeight = parseFloat(svg.getAttributeNS(null, \"height\"));\r\n"
//				+ "\r\n"
//				+ "function scaleSVG(percent)\r\n"
//				+ "{\r\n"
//				+ "	svg.setAttributeNS(null, \"width\", svgWidth * percent);\r\n"
//				+ "	svg.setAttributeNS(null, \"height\", svgHeight * percent);\r\n"
//				+ "	svg.setAttributeNS(null, \"viewBox\", \"0 0 \" + svgWidth + \" \" + svgHeight);\r\n"
//				+ "	\r\n"
//				+ "}\r\n"
//				+ "\r\n"
//				+ "var inputs = document.querySelectorAll('input');\r\n"
//				+ "\r\n"
//				+ "var inputPercent = inputs[0];\r\n"
//				+ "\r\n"
//				+ "inputPercent.oninput = event =>\r\n"
//				+ "{\r\n"
//				+ "	var percent = parseFloat(event.target.value);\r\n"
//				+ "	\r\n"
//				+ "	scaleSVG( percent)\r\n"
//				+ "};"),div(attrs("#panel"), span(attrs(".label"), text("percent:")), input()
//						.attr("type", "number").attr("min", "0.1").attr("max", "2.0")
//						.attr("step", "0.1")
//						.attr("value", "1")),

	}

}
