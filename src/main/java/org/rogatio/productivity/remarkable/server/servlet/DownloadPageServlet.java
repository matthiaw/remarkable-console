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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.rogatio.productivity.remarkable.RemarkableManager;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.io.file.Util;
import org.rogatio.productivity.remarkable.model.content.Content;
import org.rogatio.productivity.remarkable.model.content.Page;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class DownloadPageServlet.
 */
@WebServlet("/page/download")
public class DownloadPageServlet extends BaseServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4791464950094086863L;

	/**
	 * Do get.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RemarkableManager rm = RemarkableManager.getInstance();
		String type = request.getParameter("type");
		Content nb = rm.getContentById(request.getParameter("notebook"));

		String no = request.getParameter("no");

		Page p = null;
		String filePath = null;
		if (no != null) {
			p = nb.getPage(Integer.parseInt(request.getParameter("no")));

			if (!type.equals("pdfhd")) {
				filePath = Util.getFilename(p, type);
			} else {
				filePath = Util.getFilename(p, "_HD", "pdf");
			}
		} else {

			String EXPORTFOLDER = PropertiesCache.getInstance().getValue(PropertiesCache.EXPORTFOLDER);

			String folders = "";
			if (nb.getFolders().size() > 0) {
				for (String f : nb.getFolders()) {
					folders += f + File.separatorChar;
				}
				File ff = new File(EXPORTFOLDER + File.separatorChar + folders);
				ff.mkdirs();
			}

			if (type.equals("pdf")) {
				filePath = EXPORTFOLDER + File.separatorChar + folders + nb.getName() + ".pdf";
			}

			if (type.equals("pdfhd")) {
				filePath = EXPORTFOLDER + File.separatorChar + folders + nb.getName() + "_HD.pdf";
			}
		}

		// reads input file from an absolute path
		File downloadFile = new File(filePath);
		FileInputStream inStream = new FileInputStream(downloadFile);

		// obtains ServletContext
		ServletContext context = getServletContext();

		// gets MIME type of the file
		String mimeType = context.getMimeType(filePath);
		if (mimeType == null) {
			// set to binary type if MIME mapping not found
			mimeType = "application/octet-stream";
		}
		// System.out.println("MIME type: " + mimeType);

		// modifies response
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());

		// forces download
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
		response.setHeader(headerKey, headerValue);

		// obtains response's output stream
		OutputStream outStream = response.getOutputStream();

		byte[] buffer = new byte[4096];
		int bytesRead = -1;

		while ((bytesRead = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}

		inStream.close();
		outStream.close();
	}

}
