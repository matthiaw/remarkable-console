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
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.div;
import static j2html.TagCreator.iff;
import static j2html.TagCreator.img;
import static j2html.TagCreator.main;

import java.io.File;
import java.io.IOException;

import org.rogatio.productivity.remarkable.RemarkableManager;
import org.rogatio.productivity.remarkable.io.file.Util;
import org.rogatio.productivity.remarkable.model.content.Content;
import org.rogatio.productivity.remarkable.model.content.Page;

import j2html.tags.ContainerTag;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/navigation")
public class PageNavigationServlet extends BaseServlet {

	private static final long serialVersionUID = -5957420611509472672L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RemarkableManager rm = RemarkableManager.getInstance();
		Content nb = rm.getContentById(request.getParameter("notebook"));
		Page p = nb.getPage(Integer.parseInt(request.getParameter("no")));

		setTitle("Remarkable Console - Notebook '" + nb.getName() + "' - Page No. " + p.getPageNumber());

		render(response,
				main(div(attrs("#container"), refLeft(p), refRight(p)),
						a(img(attrs(".center-fit")).withSrc(Util.imgToBase64String(p.getPng())))
								.withHref("page?notebook=" + nb.getId() + "&no=" + p.getPageNumber()))

		);
	}

	private ContainerTag refLeft(Page p) {
		try {
			Content nb = p.getNotebook();
			int no = p.getPageNumber() - 1;

			File file = nb.getPage(no).getPng();

			ContainerTag leftTag = div(attrs(".page .left"),
					a("" + (no + 1)).withHref("navigation?notebook=" + nb.getId() + "&no=" + no));
			return iff(file.exists(), iff(p.getPageNumber() > 0, leftTag));
		} catch (Exception e) {
			return iff(false, null);
		}
	}

	private ContainerTag refRight(Page p) {
		try {
			Content nb = p.getNotebook();
			int no = p.getPageNumber() + 1;

			File file = nb.getPage(no).getPng();

			ContainerTag rightTag = div(attrs(".page .right"),
					a("" + (no + 1)).withHref("navigation?notebook=" + nb.getId() + "&no=" + no));
			return iff(file.exists(), iff(p.getPageNumber() < p.getNotebook().getPages().size(), rightTag));
		} catch (Exception e) {
			return iff(false, null);
		}
	}

}
