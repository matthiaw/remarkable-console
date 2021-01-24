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
package org.rogatio.productivity.remarkable.model.content;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.RemarkableManager;
import org.rogatio.productivity.remarkable.io.file.SvGTemplateLoader;
import org.rogatio.productivity.remarkable.io.file.Util;
import org.rogatio.productivity.remarkable.model.web.ContentMetaData;

/**
 * The Class Notebook.
 * 
 * @author Matthias Wegner
 */
public class Content {

	protected static final Logger logger = LogManager.getLogger(Content.class);

	/** The id. */
	private String id;

	/** The name. */
	private String name;

	private String type;

	private int currentPage;

	/** The template names. */
	private List<String> templateNames = new ArrayList<>();

	private ContentMetaData metaData;

	private ContentData contentData;

	public Content(ContentMetaData metaData) {
		this.id = metaData.iD;
		this.name = metaData.vissibleName;
		setCurrentPageNumber(metaData.currentPage);
		this.metaData = metaData;
	}

	/**
	 * Instantiates a new notebook.
	 *
	 * @param id   the id
	 * @param name the name
	 */
	public Content(String id, String name) {
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

	public ContentMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(ContentMetaData metaData) {
		this.metaData = metaData;
	}

	public ContentData getContentData() {
		return contentData;
	}

	public void setContentData(ContentData contentData) {
		this.contentData = contentData;
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
		page.setNotebook(this);
		pages.add(page);
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;

		for (Page page : pages) {
			page.setNotebook(this);
		}
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

	public File getThumbnail() {
		Page p = this.getPage(currentPage);
		if (p == null) {
			p = this.getPage(0);
			if (p == null) {
				return null;
			}
		}
		String f = Util.getFilename(p, "_thumbnail", "png");
		if (new File(f).exists()) {
			return new File(f);
		} else {
			logger.info("Thumbnail image of " + new File(f).getAbsolutePath() + " not exists. Use export.");
		}
		return null;
	}

	public Type getType() {
		return Type.get(type);
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((pages == null) ? 0 : pages.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Content other = (Content) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (pages == null) {
			if (other.pages != null)
				return false;
		} else if (!pages.equals(other.pages))
			return false;
		return true;
	}
	
	

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Content other = (Content) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}

}
