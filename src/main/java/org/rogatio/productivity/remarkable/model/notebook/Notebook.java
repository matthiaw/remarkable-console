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
package org.rogatio.productivity.remarkable.model.notebook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.rogatio.productivity.remarkable.io.file.SvGTemplateLoader;
import org.rogatio.productivity.remarkable.io.file.Util;

/**
 * The Class Notebook.
 * 
 * @author Matthias Wegner
 */
public class Notebook {

	/** The id. */
	private String id;

	/** The name. */
	private String name;

	private String type;

	private int currentPage;

	/** The template names. */
	private List<String> templateNames = new ArrayList<>();

	/**
	 * Instantiates a new notebook.
	 *
	 * @param id   the id
	 * @param name the name
	 */
	public Notebook(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getCurrentPageNumber() {
		return currentPage;
	}

	public void setCurrentPageNumber(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	private List<String> folders = new ArrayList<String>();

	/** The pages. */
	private List<Page> pages = new ArrayList<Page>();

	public List<String> getFolders() {
		return folders;
	}

	public void setFolders(List<String> folders) {
		this.folders = folders;
	}

	/**
	 * Gets the template file.
	 *
	 * @param page the page
	 * @return the template file
	 */
	public File getTemplateFile(Page page) {
		return SvGTemplateLoader.getInstance().getFile(getTemplateName(page));
	}

	/**
	 * Gets the default template.
	 *
	 * @return the default template
	 */
	public String getDefaultTemplate() {
		if (templateNames.size() > 0) {
			return templateNames.get(templateNames.size() - 1);
		}
		return null;
	}

	/**
	 * Gets the template name.
	 *
	 * @param page the page
	 * @return the template name
	 */
	public String getTemplateName(Page page) {
		String name = templateNames.get(page.getPageNumber());
		return name;
	}

	/**
	 * Gets the template names.
	 *
	 * @return the template names
	 */
	public List<String> getTemplateNames() {
		return templateNames;
	}

	/**
	 * Sets the template names.
	 *
	 * @param templateNames the new template names
	 */
	public void setTemplateNames(List<String> templateNames) {
		this.templateNames = templateNames;
	}

	/**
	 * Gets the page.
	 *
	 * @param number the number
	 * @return the page
	 */
	public Page getPage(int number) {
		for (Page remarkablePage : pages) {
			if (remarkablePage.getPageNumber() == number) {
				return remarkablePage;
			}
		}
		return null;
	}

	/**
	 * Adds the.
	 *
	 * @param page the page
	 */
	public void add(Page page) {
		pages.add(page);
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the pages.
	 *
	 * @return the pages
	 */
	public List<Page> getPages() {
		return pages;
	}

	public String getCurrentPageFile() {
		Page p = this.getPage(currentPage);
		if (p == null) {
			return null;
		}
		return Util.getFilename(p, "_thumbnail", "png");
	}

	public Type getType() {
		return Type.get(type);
	}

	public void setType(String type) {
		this.type = type;
	}

}
