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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.model.template.Template;
import org.rogatio.productivity.remarkable.model.template.Templates;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class SvGTemplateLoader.
 */
public class SvGTemplateLoader {

	/** The Constant TYPE_SVG. */
	public final static String TYPE_SVG = ".svg";

	/** The Constant TYPE_PNG. */
	public final static String TYPE_PNG = ".png";

	/** The template folder. */
	File templateFolder = new File(PropertiesCache.getInstance().getValue(PropertiesCache.TEMPLATEFOLDER));

	/** The templates. */
	private List<Template> _templates = new ArrayList<Template>();

	/**
	 * The Class LazyHolder.
	 */
	private static class LazyHolder {
		/** The Constant INSTANCE. */
		private static final SvGTemplateLoader INSTANCE = new SvGTemplateLoader();
	}

	/**
	 * Gets the single instance of SvGTemplateLoader.
	 *
	 * @return single instance of SvGTemplateLoader
	 */
	public static SvGTemplateLoader getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * Instantiates a new sv G template loader.
	 */
	private SvGTemplateLoader() {

		ObjectMapper mapper = new ObjectMapper();

		try {
			Templates templates = mapper.readValue(templateFolder, Templates.class);
			for (Template template : templates.templates) {
				_templates.add(template);
			}
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * Load.
	 *
	 * @param name the name
	 * @return the string
	 */
	@SuppressWarnings("unused")
	private String load(String name) {
		for (Template template : _templates) {
			if (template.name.trim().equals(name.trim())) {
				return load(template);
			}
		}
		return null;
	}

	/**
	 * Load.
	 *
	 * @param template the template
	 * @return the string
	 */
	public String load(Template template) {
		return loadFile(template.filename);
	}

	/**
	 * Gets the file.
	 *
	 * @param name the name
	 * @return the file
	 */
	public File getFile(String name) {
		String path = templateFolder.getPath();
		for (File f : new File(path).listFiles()) {
			if (f.getName().endsWith(name + TYPE_SVG)) {
				return f;
			}
		}
		return null;
	}

	/**
	 * Load file.
	 *
	 * @param name the name
	 * @return the string
	 */
	private String loadFile(String name) {

		File f = getFile(name);
		if (f != null) {
			try {
				byte[] encoded = Files.readAllBytes(Paths.get(f.toURI()));
				return new String(encoded, "UTF-8");
			} catch (IOException e) {
			}
		}
		return null;
	}

	/**
	 * Gets the templates.
	 *
	 * @return the templates
	 */
	public List<Template> getTemplates() {
		return _templates;
	}

}
