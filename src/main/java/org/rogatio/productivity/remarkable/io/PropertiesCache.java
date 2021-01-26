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
package org.rogatio.productivity.remarkable.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.Application;

/**
 * The Class PropertiesCache.
 */
public class PropertiesCache {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(PropertiesCache.class);

	/** The config prop. */
	private final Properties configProp = new Properties();

	/** The version. Only works in build. */
	private final String VERSIONNO = Application.class.getPackage().getImplementationVersion();

	/** The Constant TEMPLATEDIRHOST. */
	public final static String TEMPLATEDIRHOST = "/usr/share/remarkable/templates/";

	/** The exportdir. */
	private final String EXPORTDIR = "exports";

	/** The notebookdir. */
	private final String NOTEBOOKDIR = "notebooks";

	/** The templatedir. */
	private final String TEMPLATEDIR = "templates";

	/** The svg1. */
	private final String SVG1 = "black";

	/** The svg2. */
	private final String SVG2 = "#b93059";

	/** The svgh. */
	private final String SVGH = "#76b72a";
	
	private final String PORT = "8090";

	/** The svgb. */
	private final String SVGB = "white";

	private final String EXPORTSCALE = "1.8";

	/** The svgg. */
	private final String SVGG = "gray"; // #C7E1A8 //#263C0E

	/** The propertyfile. */
	private final String PROPERTYFILE = "application.properties";

	/** The Constant VERSION. */
	public static final String VERSION = "version";

	public static final String PNGEXPORTSCALE = "png.export.scale";

	/** The Constant DEVICETOKEN. */
	public static final String DEVICETOKEN = "device.token";

	/** The Constant EXPORTFOLDER. */
	public static final String EXPORTFOLDER = "folder.exports";

	/** The Constant NOTEBOOKFOLDER. */
	public static final String NOTEBOOKFOLDER = "folder.notebooks";

	/** The Constant TEMPLATEFOLDER. */
	public static final String TEMPLATEFOLDER = "folder.templates";

	/** The Constant SVGPRIMARYCOLOR. */
	public static final String SVGPRIMARYCOLOR = "svg.color.primary";

	/** The Constant SVGSECONDARYCOLOR. */
	public static final String SVGSECONDARYCOLOR = "svg.color.secondary";

	/** The Constant SVGBACKGROUNDCOLOR. */
	public static final String SVGBACKGROUNDCOLOR = "svg.color.background";

	/** The Constant SVGHIGHLIGHTCOLOR. */
	public static final String SVGHIGHLIGHTCOLOR = "svg.color.highlight";

	/** The Constant SVGGRIDCOLOR. */
	public static final String SVGGRIDCOLOR = "svg.color.grid";

	/** The Constant SSHHOST. */
	public static final String SSHHOST = "ssh.host";

	/** The Constant SSHPSWD. */
	public static final String SSHPSWD = "ssh.password";
	
	public static final String SERVERPORT = "server.port";

	/**
	 * Creates the.
	 */
	private void create() {
		try {
			File file = new File(PROPERTYFILE);
			file.createNewFile();
			setProperty(VERSION, VERSIONNO);
			setProperty(EXPORTFOLDER, EXPORTDIR);
			setProperty(NOTEBOOKFOLDER, NOTEBOOKDIR);
			setProperty(TEMPLATEFOLDER, TEMPLATEDIR);
			setProperty(SVGPRIMARYCOLOR, SVG1);
			setProperty(SVGSECONDARYCOLOR, SVG2);
			setProperty(SVGHIGHLIGHTCOLOR, SVGH);
			setProperty(SVGBACKGROUNDCOLOR, SVGB);
			setProperty(SVGGRIDCOLOR, SVGG);
			setProperty(PNGEXPORTSCALE, EXPORTSCALE);
			setProperty(SERVERPORT, PORT);
			flush();
			logger.error("Propertyfile '" + PROPERTYFILE + "' created");
		} catch (FileNotFoundException e1) {
		} catch (IOException e1) {
		}
	}

	/**
	 * Instantiates a new properties cache.
	 */
	private PropertiesCache() {

		try {
			File propertyFile = new File(PROPERTYFILE);

			if (propertyFile.exists()) {
				logger.debug("Propertyfile '" + PROPERTYFILE + "' found");
			} else {
				create();
			}

		} catch (java.lang.NullPointerException e) {
			logger.debug("Propertyfile '" + PROPERTYFILE + "' not found");

			create();
		}

		try {
			File file = new File(PROPERTYFILE);
			InputStream in = new FileInputStream(file);
			logger.debug("Reading all properties from '" + PROPERTYFILE + "'");

			configProp.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The Class LazyHolder provides a Bill Pugh Solution for singleton pattern, see
	 * https://howtodoinjava.com/design-patterns/creational/singleton-design-pattern-in-java/
	 */
	private static class LazyHolder {
		/** The Constant INSTANCE. */
		private static final PropertiesCache INSTANCE = new PropertiesCache();
	}

	/**
	 * Gets the single instance of PropertiesCache.
	 *
	 * @return single instance of PropertiesCache
	 */
	public static PropertiesCache getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * Sets the property.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	public void setProperty(String key, String value) {
		configProp.setProperty(key, value);
	}

	/**
	 * Flush.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException           Signals that an I/O exception has occurred.
	 */
	public void flush() throws FileNotFoundException, IOException {
		try (final OutputStream outputstream = new FileOutputStream(PROPERTYFILE);) {
			configProp.store(outputstream, "File Updated");
			outputstream.close();
		}
	}

	/**
	 * Property exists.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean propertyExists(String key) {
		String s = getProperty(key);

		if (s != null) {
			if (!s.trim().equals("")) {
				return true;
			}
		}

		return false;
	}

	public Double getPropertyDouble(String key) {
		return Double.parseDouble(configProp.getProperty(key));
	}
	
	public Integer getPropertyInt(String key) {
		return Integer.parseInt(configProp.getProperty(key));
	}

	public String getProperty(String key) {
		return configProp.getProperty(key);
	}

	/**
	 * Gets the all property names.
	 *
	 * @return the all property names
	 */
	public Set<String> getAllPropertyNames() {
		return configProp.stringPropertyNames();
	}

	/**
	 * Contains key.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean containsKey(String key) {
		return configProp.containsKey(key);
	}
}
