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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.model.content.Content;
import org.rogatio.productivity.remarkable.model.content.Page;
import org.rogatio.productivity.remarkable.model.web.ContentMetaData;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.itextpdf.text.DocumentException;

import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * The Class Util.
 */
public class Util {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(Util.class);

	private static final boolean EXPORT_PDF_HD = PropertiesCache.getInstance().getBoolean(PropertiesCache.PDFHDEXPORT);
	private static final boolean EXPORT_PDF_ALL = PropertiesCache.getInstance()
			.getBoolean(PropertiesCache.PDFPAGESMERGED);
	private static final boolean EXPORT_PDF_PAGES = PropertiesCache.getInstance()
			.getBoolean(PropertiesCache.PDFPAGESINGLE);

	/**
	 * File exists.
	 *
	 * @param filename the filename
	 * @return true, if successful
	 */
	public static boolean fileExists(String filename) {
		File f = new File(filename);
		if (f.exists()) {
			return true;
		}
		return false;
	}

	private static String createFolderStructure(Page page) {
		String folders = "";
		if (page.getNotebook().getFolders().size() > 0) {
			for (String f : page.getNotebook().getFolders()) {
				folders += f + File.separatorChar;
			}
			File ff = new File(EXPORTFOLDER + File.separatorChar + folders);
			ff.mkdirs();
		}
		return folders;
	}

	public static ArrayList<File> listFiles(File dir, String ending) {
		if (null == dir || !dir.isDirectory()) {
			return new ArrayList<>();
		}
		boolean recursive = true;

		final Set<File> fileTree = new HashSet<File>();
		FileFilter fileFilter = new FileFilter() {
			private final String[] acceptedExtensions = new String[] { ending };

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				}
				for (String extension : acceptedExtensions) {
					if (file.getName().toLowerCase().endsWith(extension)) {
						return true;
					}
				}
				return false;
			}
		};
		File[] listed = dir.listFiles(fileFilter);
		if (listed != null) {
			for (File entry : listed) {
				if (entry.isFile()) {
					fileTree.add(entry);
				} else if (recursive) {
					fileTree.addAll(listFiles(entry, ending));
				}
			}
		}
		return new ArrayList<>(fileTree);
	}

	public static String getFilename(Page page, String suffix, String ending) {

		if (suffix == null) {
			suffix = "";
		}

		String folders = createFolderStructure(page);

		String no = String.format("%03d", page.getPageNumber());
		String name = EXPORTFOLDER + File.separatorChar + folders + page.getNotebook().getName() + File.separatorChar
				+ "Page_" + no + suffix + "." + ending;
		return name;
	}

	public static String getFilename(Page page, String ending) {
		return getFilename(page, null, ending);
	}

	/**
	 * Stream to bytes.
	 *
	 * @param in the in
	 * @return the byte[]
	 */
	public static byte[] streamToBytes(InputStream in) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		try {
			while ((nRead = in.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
		} catch (IOException e) {
		}

		return buffer.toByteArray();
	}

	/** The Constant EXPORTFOLDER. */
	private static final String EXPORTFOLDER = PropertiesCache.getInstance().getValue(PropertiesCache.EXPORTFOLDER);

	/**
	 * Creates the pdf.
	 *
	 * @param notebook the notebook
	 */
	public static void createPdf(Content notebook) {
		for (Page page : notebook.getPages()) {
			if (EXPORT_PDF_HD) {
				Svg2Pdf.convert(page);
			}
			if (EXPORT_PDF_PAGES) {
				try {
					Png2Pdf.convert(page);
				} catch (MalformedURLException e) {
				} catch (DocumentException e) {
				} catch (IOException e) {
				}
			}
		}
		if (EXPORT_PDF_HD) {
			Svg2Pdf.merge(notebook);
		}
		if (EXPORT_PDF_ALL) {
			try {
				Png2Pdf.merge(notebook);
			} catch (MalformedURLException e) {
			} catch (DocumentException e) {
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Creates the png.
	 *
	 * @param notebook the notebook
	 */
	public static void createPng(Content notebook, double scale) {
		for (Page page : notebook.getPages()) {
			try {
				Svg2Png.createPng(page, scale);
				Svg2Png.createThumbnail(page);
			} catch (TranscoderException | IOException e) {
				logger.error("Error creating png", e);
			}
		}
	}

	/**
	 * Creates the svg.
	 *
	 * @param notebook the notebook
	 */
	public static void createSvg(Content notebook) {
		for (Page page : notebook.getPages()) {

//			SvgDocument.createPortrait(page);
//			SvgDocument.createLandscape(page);

			String orientation = page.getNotebook().getContentData().getOrientation();
			if (orientation.equals("portrait")) {
				SvgDocument.createPortrait(page);
			} else {
				SvgDocument.createLandscape(page);
			}

			SvgMerger.merge(page, page.getTemplateName(), new File(getFilename(page, "svg")));
		}
	}

	public static String imgToBase64String(File imgFile) {
		BufferedImage image = null;

		if (imgFile == null) {
			return "";
		}

		try {
			image = ImageIO.read(imgFile);
		} catch (IOException e) {

		}

		String ending = imgFile.getName().substring(imgFile.getName().indexOf(".") + 1, imgFile.getName().length());

		return imgToBase64String(image, ending);
	}

	public static String imgToBase64String(final RenderedImage img, final String formatName) {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			ImageIO.write(img, formatName, os);
			return "data:image/" + formatName.toLowerCase() + ";base64,"
					+ Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (final IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	public static void addMetaToZip(ContentMetaData document, File zip) {
		try {
			net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(zip);

			File fileToZip = File.createTempFile(document.iD + "-", ".meta");

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			objectMapper.writeValue(fileToZip, document);

			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

			zipFile.addFile(fileToZip, parameters);
		} catch (ZipException e) {
		} catch (IOException e) {
		} catch (net.lingala.zip4j.exception.ZipException e) {
		}
	}

	public static String getFileContent(File zipFile, String ending) {
		ZipFile zf = null;
		try {
			zf = new ZipFile(zipFile);

			Enumeration<? extends ZipEntry> entries = zf.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				if (entry.getName().endsWith("." + ending)) {
					// System.out.println(entry.getName());
					InputStream inputStream = zf.getInputStream(entry);
//					StringWriter writer = new StringWriter();
//					IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8.name());

					// System.out.println("!!"+writer.toString());
					return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
				}
			}

		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zf != null) {
				try {
					zf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

}
