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
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.model.content.Content;
import org.rogatio.productivity.remarkable.model.content.Page;
import org.rogatio.productivity.remarkable.model.web.ContentMetaData;

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

	/** The Constant EXPORT_PDF_HD. */
	private static final boolean EXPORT_PDF_HD = PropertiesCache.getInstance().getBoolean(PropertiesCache.PDFHDEXPORT);

	private static final String TMP_DIR = "temp";

	/** The Constant EXPORT_PDF_ALL. */
	private static final boolean EXPORT_PDF_ALL = PropertiesCache.getInstance()
			.getBoolean(PropertiesCache.PDFPAGESMERGED);

	/** The Constant EXPORT_PDF_PAGES. */
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

	/**
	 * Creates the folder structure.
	 *
	 * @param page the page
	 * @return the string
	 */
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

	/**
	 * List files.
	 *
	 * @param dir    the dir
	 * @param ending the ending
	 * @return the array list
	 */
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

	/**
	 * Gets the filename.
	 *
	 * @param page   the page
	 * @param suffix the suffix
	 * @param ending the ending
	 * @return the filename
	 */
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

	/**
	 * Gets the filename.
	 *
	 * @param page   the page
	 * @param ending the ending
	 * @return the filename
	 */
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
	 * @param scale    the scale
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

			String orientation = page.getNotebook().getContentData().getOrientation();
			if (orientation.equals("portrait")) {
				SvgDocument.createPortrait(page);
			} else {
				SvgDocument.createLandscape(page);
			}

			SvgMerger.merge(page, page.getTemplateName(), new File(getFilename(page, "svg")));
		}
	}

	/**
	 * Img to base 64 string.
	 *
	 * @param imgFile the img file
	 * @return the string
	 */
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

	/**
	 * Img to base 64 string.
	 *
	 * @param img        the img
	 * @param formatName the format name
	 * @return the string
	 */
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

	/**
	 * Adds the meta to zip.
	 *
	 * @param document the document
	 * @param zip      the zip
	 */
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

	/**
	 * Gets the file content.
	 *
	 * @param zipFile the zip file
	 * @param ending  the ending
	 * @return the file content
	 */
	public static String getFileContent(File zipFile, String ending) {
		ZipFile zf = null;
		try {
			zf = new ZipFile(zipFile);

			Enumeration<? extends ZipEntry> entries = zf.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				if (entry.getName().endsWith("." + ending)) {
					InputStream inputStream = zf.getInputStream(entry);
					return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
				}
			}

		} catch (ZipException e) {
			logger.error("Error receiving content in '" + zipFile + "'", e);
			// e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error receiving content in '" + zipFile + "'", e);
		} finally {
			if (zf != null) {
				try {
					zf.close();
				} catch (IOException e) {
					logger.error("Error receiving content in '" + zipFile + "'", e);
				}
			}
		}

		return null;
	}

	public static void deleteTemporaryDirectory() {
		deleteDirectory(new File(TMP_DIR));
	}

	public static void deleteDirectory(File directory) {
		try {
			FileUtils.deleteDirectory(directory);
		} catch (IOException e) {
		}
	}

	/**
	 * See
	 * https://github.com/jlarriba/jrmapi/blob/master/src/main/java/es/jlarriba/jrmapi/Utils.java
	 * 
	 * @param id
	 * @return
	 */
	public static File createZipDirectory(String id) {

		File dir = new File(TMP_DIR);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {
			String sourceFile = TMP_DIR + File.separatorChar + id + ".content";

			File content = new File(sourceFile);
			content.createNewFile();

			BufferedWriter writer = new BufferedWriter(new FileWriter(content));
			writer.write("{}");
			writer.close();

			String zipFileName = TMP_DIR + File.separatorChar + id + ".zip";

//			System.out.println(sourceFile);
//			System.out.println(zipFileName);

//			FileOutputStream fos = new FileOutputStream(zipFile);
//			ZipOutputStream zipOut = new ZipOutputStream(fos);
//			File fileToZip = new File(sourceFile);
//			FileInputStream fis = new FileInputStream(zipFile);
//			ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
//			zipOut.putNextEntry(zipEntry);

//			byte[] bytes = new byte[1024];
//			int length;
//			while ((length = fis.read(bytes)) >= 0) {
//				zipOut.write(bytes, 0, length);
//			}

//			zipOut.close();
//			fis.close();
//			fos.close();

			net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(zipFileName);

			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

			zipFile.addFile(content, parameters);

			return new File(zipFileName);
		} catch (IOException e) {
			logger.error("Problem creating ZIP file: ", e);
			e.printStackTrace();
		} catch (net.lingala.zip4j.exception.ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}

		return null;
	}

}
