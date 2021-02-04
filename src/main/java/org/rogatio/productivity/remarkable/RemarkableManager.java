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
package org.rogatio.productivity.remarkable;

import static org.rogatio.productivity.remarkable.io.PropertiesCache.DEVICETOKEN;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.io.RemarkableClient;
import org.rogatio.productivity.remarkable.io.file.Util;
import org.rogatio.productivity.remarkable.model.content.Content;
import org.rogatio.productivity.remarkable.model.content.ContentData;
import org.rogatio.productivity.remarkable.model.content.Page;
import org.rogatio.productivity.remarkable.model.content.Type;
import org.rogatio.productivity.remarkable.model.web.ContentMetaData;
import org.rogatio.productivity.remarkable.ssh.SshClient;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * The Class RemarkableManager provides the main functions for the remarkable
 * console.
 *
 * @author Matthias Wegner
 */
public class RemarkableManager {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(RemarkableManager.class);

	/** The storage folder of the notebooks. */
	private final String DOCUMENT_STORAGE = PropertiesCache.getInstance().getValue(PropertiesCache.NOTEBOOKFOLDER);

	/** The remarkable client to the remarkable web application. */
	private RemarkableClient client;

	/** The user token for the session. */
	private String userToken;

	/** The instance. */
	private static RemarkableManager INSTANCE;

	/**
	 * Gets the single instance of RemarkableManager.
	 *
	 * @return single instance of RemarkableManager
	 */
	public static RemarkableManager getInstance() {

		if (INSTANCE == null) {
			boolean deviceTokenExists = PropertiesCache.getInstance().propertyExists(DEVICETOKEN);

			if (!deviceTokenExists) {
				logger.error("Device token not set. Close remarkable console application.");
				System.exit(0);
			}

			String deviceToken = PropertiesCache.getInstance().getValue(DEVICETOKEN);
			INSTANCE = new RemarkableManager(deviceToken);
		}

		return INSTANCE;
	}

	/** The metadata notebooks. */
	private ContentMetaData[] metadataNotebooks;

	/**
	 * Instantiates a new remarkable manager.
	 *
	 * @param deviceToken the device token
	 */
	private RemarkableManager(String deviceToken) {
		// instantiates the remarkable client
		client = new RemarkableClient();

		// load new user token
		try {
			userToken = client.newUserToken(deviceToken);
		} catch (IOException e) {
			logger.error("Error creating user token", e);
		}

		metadataNotebooks = readNotebookMetaDatas();

		if (metadataNotebooks.length == 0) {
			metadataNotebooks = downloadMetaDatas();
		}

		this.readContents();

	}

	/**
	 * Download svg background templates through ssh connection.
	 */
	public void downloadTemplates() {
		// get ssh-properties
		String host = PropertiesCache.getInstance().getValue(PropertiesCache.SSHHOST);
		String pswd = PropertiesCache.getInstance().getValue(PropertiesCache.SSHPSWD);
		String targetDir = PropertiesCache.getInstance().getValue(PropertiesCache.TEMPLATEFOLDER);
		String templateSourceDir = PropertiesCache.TEMPLATEDIRHOST;

		// instantiates SSH-client
		SshClient client = new SshClient("root", pswd, host, 22);

		// download svg templates via ssh
		if (client.isConnected()) {
			client.downloadDir(templateSourceDir, targetDir);
			client.disconnect();
		} else {
			logger.warn(
					"SSH-Client not connectable. Try again. Check if Remarkable is on. Check if ssh.host, ssh.password are set in properties. Is found at Settings > About > Copyrights and licenses > General information (scroll down)");
		}
	}

	/**
	 * Gets the meta data notebook by id.
	 *
	 * @param id the id
	 * @return the meta data notebook by id
	 */
	public ContentMetaData getMetaDataById(String id) {
		for (ContentMetaData document : metadataNotebooks) {
			if (document.iD.equals(id)) {
				return document;
			}
		}
		return null;
	}

	/**
	 * Gets the meta data by folder and name.
	 *
	 * @param name the name
	 * @return the meta data by folder and name
	 */
	public ContentMetaData getMetaDataByFolderAndName(String name) {
		if (metadataNotebooks != null) {
			for (ContentMetaData meta : metadataNotebooks) {

				List<String> p = this.getParentFolders(meta.iD);
				String folders = "";
				if (p.size() > 0) {
					for (String f : p) {
						folders += f + "_";
					}
				}

				String folderAndName = folders + meta.vissibleName;
				if (folderAndName.equals(name)) {
					return meta;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the meta data notebook by name.
	 *
	 * @param name the name
	 * @return the meta data notebook by name
	 */
	public ContentMetaData getMetaDataByName(String name) {
		if (metadataNotebooks != null) {
			for (ContentMetaData document : metadataNotebooks) {
				if (document.vissibleName.equals(name)) {
					return document;
				}
			}
		}
		return null;
	}

	/** The notebooks. */
	private List<Content> contents = new ArrayList<Content>();

	/**
	 * Gets the notebooks.
	 *
	 * @return the notebooks
	 */
	public List<Content> getContents() {
		return contents;// new ArrayList<>(notebooks.values());
	}

	/**
	 * Gets the folders.
	 *
	 * @return the folders
	 */
	public List<Content> getFolders() {
		List<Content> files = new ArrayList<Content>();

		for (Content notebook : contents) {
			if (notebook.getType() == Type.COLLECTION) {
				files.add(notebook);
			}
		}

		return files;
	}

	/**
	 * Gets the notebooks.
	 *
	 * @return the notebooks
	 */
	public List<Content> getNotebooks() {
		List<Content> files = new ArrayList<Content>();

		for (Content notebook : contents) {
			if (notebook.getType() == Type.DOCUMENT) {
				// System.out.println(notebook.getName());
				files.add(notebook);
			}
		}

		return files;
	}

	/**
	 * Gets the content by id.
	 *
	 * @param id the id
	 * @return the content by id
	 */
	public Content getContentById(String id) {
		for (Content n : contents) {
			if (n.getId().equals(id)) {
				return n;
			}
		}
		return null;
	}

	/**
	 * Gets the first entry of notebook with same name.
	 *
	 * @param name the name
	 * @return the notebook
	 */
	public Content getContentByName(String name) {
		for (Content n : contents) {
			if (n.getName().equals(name)) {
				return n;
			}
		}
		return null;
	}

//	public void readContent(File zipFile) {
//
//		ObjectMapper objectMapper = new ObjectMapper();
//
//		try {
//
//			String metaJson = Util.getFileContent(zipFile, "meta");
//			System.out.println(metaJson);
//			ContentMetaData metaData = objectMapper.readValue(metaJson, ContentMetaData.class);
//
//			Content content = new Content(metaData);
//			
//			List<Page> pages = getPages(zipFile);
//			content.setPages(pages);
//
//			String contentJson = Util.getFileContent(zipFile, "content");
//			JSONObject contentData = new JSONObject(contentJson);
//			
//			int coverPageNumber = contentData.getInt("coverPageNumber");
//			boolean dummyDocument = contentData.getBoolean("contentData");
//			JSONObject extraMetadata = contentData.getJSONObject("extraMetadata");
//			
//			for (String key : extraMetadata.keySet()) {
//				System.out.println(extraMetadata.getString(key));
//			}
//			
//			String pageJson = Util.getFileContent(zipFile, "pagedata");
//			ArrayList<String> listPageLayouts = getPageData(pageJson);
//			content.setTemplateNames(listPageLayouts);
//			// rNotebook.setTemplateNames(list);
////			logger.info("Found '" + list.size() + "' templates (default is set as '"
////					+ rNotebook.getDefaultTemplate() + "')");
//
//			
//			
//		} catch (JsonMappingException e) {
//		} catch (JsonProcessingException e) {
//		}
//
//	}
//
//	public ArrayList<String> getPageData(String pagedata) {
//		Scanner s = new Scanner(pagedata);
//		ArrayList<String> list = new ArrayList<String>();
//		while (s.hasNext()) {
//			list.add(s.nextLine());
//		}
//		s.close();
//		return list;
//	}
//
//	private List<Page> getPages(File file) {
//		ZipFile zf = null;
//
//		List<Page> pages = new ArrayList<Page>();
//
//		try {
//			zf = new ZipFile(file.getAbsolutePath());
//
//			String notebookID = file.getName().replace(".zip", "");
//
//			Enumeration<? extends ZipEntry> entries = zf.entries();
//
//			// iterate through all files inside zip
//			while (entries.hasMoreElements()) {
//				ZipEntry entry = entries.nextElement();
//
//				if (entry.getName().endsWith(".rm")) {
//					InputStream in = zf.getInputStream(zf.getEntry(entry.getName()));
//					// read byte representation of page
//					byte[] bytes = Util.streamToBytes(in);
//					// extract page number from filename
//					String no = entry.getName().replace(notebookID + "/", "").replace(".rm", "");
//					// parse number to int
//					int number = Integer.parseInt(no);
//
//					if (bytes.length == 0) {
//						logger.error("Could not read content of '" + entry.getName() + "'");
//					} else {
//						// Instantiates page
//						Page page = new Page(number, bytes);
//
//						// add page
//						pages.add(page);
//
//						logger.info("Load " + page);
//					}
//				}
//			}
//
//			return pages;
//		} catch (IOException e) {
//			// logger.error("Error extracting file "+fileWithPathInsideZip +" from
//			// "+zipFile.getName());
//		} finally {
//			try {
//				if (zf != null) {
//					zf.close();
//				}
//			} catch (IOException e) {
//			}
//		}
//		return null;
//	}

	/**
	 * Read content.
	 *
	 * @param file the file
	 */
	public void readContent(File file) {

		String contentJson = Util.getFileContent(file, "content");
		ContentData contentData = new ContentData(contentJson);

		ZipFile zf = null;

		String notebookName = file.getName().replace(".zip", "");

		try {
			zf = new ZipFile(file.getAbsolutePath());

			Enumeration<? extends ZipEntry> entries = zf.entries();

			Content rNotebook = null;

			String notebookID = null;
			// iterate through all files inside zip
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				// extract id for a single ocurrence file
				if (entry.getName().endsWith(".content")) {
					// get uid of notebook
					notebookID = entry.getName().replace(".content", "");
					// Instantiates notebook
					rNotebook = new Content(notebookID, notebookName);
					logger.info("Read content '" + notebookName + "' (id=" + notebookID + ")");
				} else if (entry.getName().endsWith(".pagedata")) {
					Scanner s = new Scanner(zf.getInputStream(zf.getEntry(entry.getName())));
					ArrayList<String> list = new ArrayList<String>();
					while (s.hasNext()) {
						list.add(s.nextLine());
					}
					rNotebook.setTemplateNames(list);
					logger.debug("Found '" + list.size() + "' templates (default is set as '"
							+ rNotebook.getDefaultTemplate() + "')");
					s.close();
				} else

				// read remarkable file
				if (entry.getName().endsWith(".rm")) {
					InputStream in = zf.getInputStream(zf.getEntry(entry.getName()));
					// read byte representation of page
					byte[] bytes = Util.streamToBytes(in);
					// extract page number from filename
					String no = entry.getName().replace(notebookID + "/", "").replace(".rm", "");
					// parse number to int
					int number = Integer.parseInt(no);

					if (bytes.length == 0) {
						logger.error("Could not read content of '" + entry.getName() + "'");
					} else {
						// Instantiates page
						Page page = new Page(number, bytes, rNotebook);

						// add page to notebook
						rNotebook.add(page);

						logger.debug("Load " + page);
					}
				} else {
					// logger.debug("No importer defined yet for '" +
					// entry.getName().replace(notebookID + "/", "") + "'");
				}

			}

			rNotebook.setContentData(contentData);
			logger.debug("Notebook '" + rNotebook.getName() + "' has orientation '"
					+ rNotebook.getContentData().getOrientation() + "'");

			ContentMetaData metadataNotebook = getMetaDataById(notebookID);
			rNotebook.setMetaData(metadataNotebook);

			rNotebook.setCurrentPageNumber(metadataNotebook.currentPage);
			logger.debug("Current page of '" + rNotebook.getName() + "' is " + rNotebook.getCurrentPageNumber() + "");

			File currentPageFile = rNotebook.getThumbnail();
			if (currentPageFile != null) {
				logger.debug("Current page of '" + rNotebook.getName() + "' is " + rNotebook.getThumbnail() + "");
			}

			rNotebook.setType(metadataNotebook.type);
			logger.debug("Type of '" + rNotebook.getName() + "' is " + rNotebook.getType() + "");

			List<String> parents = getParentFolders(notebookID);
			rNotebook.setFolders(parents);

			if (rNotebook.getFolders().size() > 0) {
				logger.debug("Path of '" + rNotebook.getName() + "' is " + rNotebook.getFolders() + "");
			} else {
				logger.debug("Path of '" + rNotebook.getName() + "' is ROOT");
			}

			addContent(rNotebook);
		} catch (IOException e) {
			// logger.error("Error extracting file "+fileWithPathInsideZip +" from
			// "+zipFile.getName());
		} finally {
			try {
				if (zf != null) {
					zf.close();
				}
			} catch (IOException e) {
			}
		}

	}

	/**
	 * Removes the content.
	 *
	 * @param content the content
	 */
	public void removeContent(Content content) {
		Content contentToRemove = null;
		for (Content c : contents) {
			if (c.getId().equals(content.getId())) {
				contentToRemove = c;
			}
		}
		if (contentToRemove != null) {
			contents.remove(contentToRemove);
		}
	}

	/**
	 * Adds the content.
	 *
	 * @param content the content
	 */
	public void addContent(Content content) {
		removeContent(content);
		if (!contents.contains(content)) {
			contents.add(content);
		}
	}

	/**
	 * Read notebooks from local storage to memory.
	 */
	public void readContents() {
		ArrayList<File> files = Util.listFiles(new File(DOCUMENT_STORAGE), "zip");
		for (File file : files) {
			if (!file.isDirectory()) {
				this.readContent(file);
			}
		}
	}

	/**
	 * Download notebook from web to local.
	 *
	 * @param notebookName the notebook name
	 */
	public void downloadContent(String notebookName) {
		ContentMetaData metaDataNotebook = getMetaDataByName(notebookName);
		downloadContent(metaDataNotebook);
	}

	/**
	 * Download notebooks from web to local.
	 */
	public void downloadContents() {
		ContentMetaData[] metaDataNotebooks = downloadMetaDatas(true);
		for (ContentMetaData metaDataNotebook : metaDataNotebooks) {
			downloadContent(metaDataNotebook);
		}
	}

	/**
	 * Download notebook.
	 *
	 * @param document the document
	 */
	public void downloadContent(ContentMetaData document) {
		List<String> p = this.getParentFolders(document.iD);
		String folders = "";
		if (p.size() > 0) {
			for (String f : p) {
				folders += f + File.separatorChar;
			}
		}

		File zip = new File(DOCUMENT_STORAGE + File.separatorChar + folders + document.vissibleName + ".zip");

		if (!zip.exists()) {
			zip.getParentFile().mkdirs();
		}

		downloadContent(document, zip);

	}

	/**
	 * Download notebook from web to local.
	 *
	 * @param document the document
	 * @param file     the file
	 */
	private void downloadContent(ContentMetaData document, File file) {
		logger.info("Save/Download content " + document.vissibleName + " to " + file.getName());
		client.saveDocument(document, userToken, file);
		saveMetaDataNotebook(document);
	}

	/**
	 * Gets the page object.
	 *
	 * @param notebookName the notebook name
	 * @param pageNumber   the page number
	 * @return the page
	 */
	public Page getPage(String notebookName, int pageNumber) {
		return getContentByName(notebookName).getPage(pageNumber);
	}

	/**
	 * Read notebook meta datas.
	 *
	 * @return the content meta data[]
	 */
	public ContentMetaData[] readNotebookMetaDatas() {
		ArrayList<File> files = Util.listFiles(new File(DOCUMENT_STORAGE), "meta");

		ContentMetaData[] metaDataNotebooks = new ContentMetaData[files.size()];
		for (int i = 0; i < metaDataNotebooks.length; i++) {
			if (!files.get(i).isDirectory()) {

				try {
					ObjectMapper mapper = new ObjectMapper();
					metaDataNotebooks[i] = mapper.readValue(files.get(i), ContentMetaData.class);
				} catch (JsonParseException e) {
				} catch (JsonMappingException e) {
				} catch (IOException e) {
				}
			}
		}

		logger.info("Read Content MetaData (" + metaDataNotebooks.length + " entries)");

		return metaDataNotebooks;
	}

	/**
	 * Export all notebooks.
	 */
	public void exportNotebooks() {
		for (Content notebook : getNotebooks()) {
			exportNotebook(notebook);
		}
	}

	/**
	 * Export notebook.
	 *
	 * @param meta the meta
	 */
	public void exportNotebook(ContentMetaData meta) {
		Content content = getContentById(meta.iD);
		if (content != null) {
			exportNotebook(content);
		}
	}

	/**
	 * Read content.
	 *
	 * @param meta the meta
	 */
	public void readContent(ContentMetaData meta) {
		List<String> p = this.getParentFolders(meta.iD);
		String folders = "";
		if (p.size() > 0) {
			for (String f : p) {
				folders += f + File.separatorChar;
			}
		}

		File zip = new File(DOCUMENT_STORAGE + File.separatorChar + folders + meta.vissibleName + ".zip");

		if (zip.exists()) {
			this.readContent(zip);
		} else {
			logger.error("Content '"+zip.toString()+"' not exists and could not be read.");
		}
	}

	/**
	 * Export notebook to SVG, PNG, PDF into export folder.
	 *
	 * @param notebook the notebook
	 */
	public void exportNotebook(Content notebook) {
		Util.createSvg(notebook);

		double scale = PropertiesCache.getInstance().getDouble(PropertiesCache.PNGEXPORTSCALE);
		Util.createPng(notebook, scale);

		Util.createPdf(notebook);
	}

	/**
	 * Update contents.
	 */
	public void updateContents() {
		boolean repositoryOutdated = isOutdated();

		if (repositoryOutdated) {
			this.downloadContents();
			this.readContents();
			this.exportNotebooks();
			logger.info("Update all contents");
		} else {
			for (ContentMetaData meta : this.metadataNotebooks) {
				if (isOutdated(meta)) {
					this.downloadContent(meta);
					this.readContents();
					Content content = getContentById(meta.iD);
					this.exportNotebook(content);
					logger.info("Update content '" + meta.vissibleName + "'");
				}
			}
		}

	}

	/**
	 * Checks if is outdated.
	 *
	 * @param meta the meta
	 * @return true, if is outdated
	 */
	public boolean isOutdated(ContentMetaData meta) {
		try {
			ContentMetaData newData = client.getMetaDataNotebook(meta.iD, userToken);
			if (meta.version != newData.version) {
				logger.debug("Document '" + meta.vissibleName + "' is outdated (version=" + meta.version + " -> "
						+ newData.version + ")");
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}

	/**
	 * Checks if is outdated.
	 *
	 * @return true, if is outdated
	 */
	public boolean isOutdated() {
		ContentMetaData[] oldContentMetaDatas = readNotebookMetaDatas();

		ContentMetaData[] newContentMetaDatas = downloadMetaDatas();

		if (oldContentMetaDatas.length != newContentMetaDatas.length) {
			return true;
		}

//		for (int i = 0; i < oldContentMetaDatas.length; i++) {
//			ContentMetaData oldData = oldContentMetaDatas[i];
//
////			boolean status = isOutdated(oldData);
////			if (status==true) {
////				return true;
////			}
//			for (int j = 0; j < newContentMetaDatas.length; j++) {
//				ContentMetaData newData = newContentMetaDatas[j];
//
//				if (oldData.iD.equals(newData.iD)) {
//					if (oldData.version != newData.version) {
//						logger.debug("Document '" + oldData.vissibleName + "' is outdated (version=" + oldData.version
//								+ " -> " + newData.version + ")");
//						return true;
//					}
//				}
//			}
//		}

		return false;
	}

	/**
	 * Download meta datas.
	 *
	 * @return the content meta data[]
	 */
	public ContentMetaData[] downloadMetaDatas() {
		return downloadMetaDatas(false);
	}

	/**
	 * Download meta datas.
	 *
	 * @param blobUrl the blob url
	 * @return the content meta data[]
	 */
	public ContentMetaData[] downloadMetaDatas(boolean blobUrl) {
		try {
			ContentMetaData[] metadataNotebooks = client.listMetaDataNotebooks(userToken, blobUrl);

//			if (!blobUrl) {
//				for (MetaDataNotebook metaDataNotebook : metadataNotebooks) {
//					saveMetaDataNotebook(metaDataNotebook);
//				}
//				logger.info("Download Notebook MetaData (" + metadataNotebooks.length + " entries) with blob");
//			} else {
//				logger.info("Download Notebook MetaData (" + metadataNotebooks.length + " entries)");
//			}

			return metadataNotebooks;
		} catch (IOException e) {
			logger.error("Error getting meta-data notebooks", e);
		}
		return null;
	}

	/**
	 * Gets the parent folders.
	 *
	 * @param notebookId the notebook id
	 * @return the parent folders
	 */
	public List<String> getParentFolders(String notebookId) {
		List<String> folders = new ArrayList<String>();

		ContentMetaData mData = this.getMetaDataById(notebookId);

		getParentFolders(mData, folders);

		if (folders.size() >= 1) {
			folders.remove(folders.size() - 1);
		}

		return folders;
	}

	/**
	 * Save meta data notebook.
	 *
	 * @param meta the meta
	 */
	private void saveMetaDataNotebook(ContentMetaData meta) {

		List<String> p = this.getParentFolders(meta.iD);
		String folders = "";
		if (p.size() > 0) {
			for (String f : p) {
				folders += f + File.separatorChar;
			}
		}

		File f = new File(DOCUMENT_STORAGE + File.separatorChar + folders + meta.vissibleName + ".meta");
		logger.debug("Save " + f.getAbsolutePath());

		if (!f.exists()) {
			f.getParentFile().mkdirs();
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			objectMapper.writeValue(f, meta);
		} catch (JsonGenerationException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}

	}

	/**
	 * Gets the parent folders.
	 *
	 * @param item the item
	 * @param list the list
	 * @return the parent folders
	 */
	private void getParentFolders(ContentMetaData item, List<String> list) {

		if (item == null) {
			return;
		}

		list.add(0, item.vissibleName);

		if (item.parent.length() > 0) {
			ContentMetaData parent = this.getMetaDataByName(item.parent);
			if (parent == null) {
				parent = this.getMetaDataById(item.parent);
			}

			getParentFolders(parent, list);
		}

	}

}
