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

@WebServlet("/page/download")
public class DownloadPageServlet extends BaseServlet {

	private static final long serialVersionUID = 4791464950094086863L;

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
