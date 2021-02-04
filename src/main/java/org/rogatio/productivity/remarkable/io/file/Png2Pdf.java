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
package org.rogatio.productivity.remarkable.io.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.model.content.Content;
import org.rogatio.productivity.remarkable.model.content.Page;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * The Class Png2Pdf converts a PNG to PDF
 */
public class Png2Pdf {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(Png2Pdf.class);

	/** The Constant EXPORTFOLDER. */
	private static final String EXPORTFOLDER = PropertiesCache.getInstance().getValue(PropertiesCache.EXPORTFOLDER);

	/**
	 * Convert.
	 *
	 * @param page the page
	 * @throws DocumentException the document exception
	 * @throws MalformedURLException the malformed URL exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("deprecation")
	public static void convert(Page page) throws DocumentException, MalformedURLException, IOException {
		String pngFile = Util.getFilename(page, "png");
		String pdfFile = Util.getFilename(page, "pdf");

		String orientation = page.getNotebook().getContentData().getOrientation();

		Rectangle paperSize = PageSize.A4;

		if (orientation.equals("landscape")) {
			paperSize = PageSize.A4.rotate();
		}

		Document document = new Document(paperSize, 0, 0, 0, 0);
		PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
		document.open();
		Image image = Image.getInstance(new File(pngFile).toURL());

		image.scaleToFit(paperSize);

		document.add(image);

		logger.debug("Convert PNG to PDF: " + pngFile);

		document.close();
	}

	/**
	 * Merge.
	 *
	 * @param notebook the notebook
	 * @throws DocumentException the document exception
	 * @throws MalformedURLException the malformed URL exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("deprecation")
	public static void merge(Content notebook) throws DocumentException, MalformedURLException, IOException {

		String orientation = notebook.getContentData().getOrientation();

		Rectangle paperSize = PageSize.A4;

		if (orientation.equals("landscape")) {
			paperSize = PageSize.A4.rotate();
		}

		String folders = "";
		if (notebook.getFolders().size() > 0) {
			for (String f : notebook.getFolders()) {
				folders += f + File.separatorChar;
			}
			File ff = new File(EXPORTFOLDER + File.separatorChar + folders);
			ff.mkdirs();
		}

		String name = EXPORTFOLDER + File.separatorChar + folders + notebook.getName() + ".pdf";

		Document document = new Document(paperSize, 0, 0, 0, 0);
		PdfWriter.getInstance(document, new FileOutputStream(name));

		document.open();

		for (Page page : notebook.getPages()) {
			String pngFile = Util.getFilename(page, "png");
			Image image = Image.getInstance(new File(pngFile).toURL());
			image.scaleToFit(paperSize);
			document.add(image);
			logger.debug("Add PNG to PDF: " + pngFile);
		}

		document.close();

	}
}
