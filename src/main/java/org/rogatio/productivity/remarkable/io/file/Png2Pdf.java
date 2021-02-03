package org.rogatio.productivity.remarkable.io.file;

import java.io.File;
import java.io.FileNotFoundException;
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

public class Png2Pdf {

	private static final Logger logger = LogManager.getLogger(Png2Pdf.class);

	private static final String EXPORTFOLDER = PropertiesCache.getInstance().getValue(PropertiesCache.EXPORTFOLDER);

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
