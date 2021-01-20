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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.model.notebook.Notebook;
import org.rogatio.productivity.remarkable.model.notebook.Page;

/**
 * The Class Util.
 */
public class Util {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(Util.class);

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
			File ff = new File(EXPORTFOLDER + File.separatorChar +folders);
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
            private final String[] acceptedExtensions = new String[]{ending};

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
        if(listed!=null){
            for (File entry : listed) {
                if (entry.isFile()) {
                    fileTree.add(entry);
                } else if(recursive){
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
	private static final String EXPORTFOLDER = PropertiesCache.getInstance().getProperty(PropertiesCache.EXPORTFOLDER);

	/**
	 * Creates the pdf.
	 *
	 * @param notebook the notebook
	 */
	public static void createPdf(Notebook notebook) {
		for (Page page : notebook.getPages()) {
			Svg2Pdf.convert(page);
		}
		Svg2Pdf.merge(notebook);
	}

	/**
	 * Creates the png.
	 *
	 * @param notebook the notebook
	 */
	public static void createPng(Notebook notebook, double scale) {
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
	public static void createSvg(Notebook notebook) {
		for (Page page : notebook.getPages()) {
			SvgDocument.create(page);
			SvgMerger.merge(page, page.getTemplateName(), new File(getFilename(page, "svg")));
		}
	}

}
