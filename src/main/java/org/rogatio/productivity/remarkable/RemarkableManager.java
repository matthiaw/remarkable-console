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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.io.RemarkableClient;
import org.rogatio.productivity.remarkable.io.file.Util;
import org.rogatio.productivity.remarkable.model.notebook.Notebook;
import org.rogatio.productivity.remarkable.model.notebook.Page;
import org.rogatio.productivity.remarkable.model.notebook.Type;
import org.rogatio.productivity.remarkable.model.web.MetaDataNotebook;
import org.rogatio.productivity.remarkable.ssh.SshClient;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class RemarkableManager provides the main functions for the remarkable
 * console
 * 
 * @author Matthias Wegner
 */
public class RemarkableManager {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(RemarkableManager.class);

	/** The storage folder of the notebooks */
	private final String DOCUMENT_STORAGE = PropertiesCache.getInstance().getProperty(PropertiesCache.NOTEBOOKFOLDER);

	/** The remarkable client to the remarkable web application */
	private RemarkableClient client;

	/** The user token for the session */
	private String userToken;

	private static RemarkableManager INSTANCE;

	public static RemarkableManager getInstance() {

		if (INSTANCE == null) {
			boolean deviceTokenExists = PropertiesCache.getInstance().propertyExists(DEVICETOKEN);

			if (!deviceTokenExists) {
				logger.error("Device token not set. Close remarkable console application.");
				System.exit(0);
			}

			String deviceToken = PropertiesCache.getInstance().getProperty(DEVICETOKEN);
			INSTANCE = new RemarkableManager(deviceToken);
		}

		return INSTANCE;
	}

	private MetaDataNotebook[] metadataNotebooks;

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

		// metadataNotebooks = readNotebookMetaDatas();

		// if (metadataNotebooks.length == 0) {
		metadataNotebooks = downloadMetaDatas();
		// }

	}

	/**
	 * Download svg background templates through ssh connection
	 */
	public void downloadTemplates() {
		// get ssh-properties
		String host = PropertiesCache.getInstance().getProperty(PropertiesCache.SSHHOST);
		String pswd = PropertiesCache.getInstance().getProperty(PropertiesCache.SSHPSWD);
		String targetDir = PropertiesCache.getInstance().getProperty(PropertiesCache.TEMPLATEFOLDER);
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
	public MetaDataNotebook getMetaDataById(String id) {
		for (MetaDataNotebook document : metadataNotebooks) {
			if (document.iD.equals(id)) {
				return document;
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
	public MetaDataNotebook getMetaDataByName(String name) {
		if (metadataNotebooks != null) {
			for (MetaDataNotebook document : metadataNotebooks) {
				if (document.vissibleName.equals(name)) {
					return document;
				}
			}
		}
		return null;
	}

	/** The notebooks. */
	private List<Notebook> notebooks = new ArrayList<Notebook>();

	/**
	 * Gets the notebooks.
	 *
	 * @return the notebooks
	 */
	public List<Notebook> getNotebooks() {
		return notebooks;// new ArrayList<>(notebooks.values());
	}

	public List<Notebook> getDocuments() {
		List<Notebook> files = new ArrayList<Notebook>();

		for (Notebook notebook : notebooks) {
			if (notebook.getType() == Type.DOCUMENT) {
				// System.out.println(notebook.getName());
				files.add(notebook);
			}
		}

		return files;
	}

	public Notebook getNotebookById(String id) {
		for (Notebook n : notebooks) {
			if (n.getId().equals(id)) {
				return n;
			}
		}
		return null;
	}
	
	/**
	 * Gets the first entry of notebook with same name
	 *
	 * @param name the name
	 * @return the notebook
	 */
	public Notebook getNotebookByName(String name) {
		for (Notebook n : notebooks) {
			if (n.getName().equals(name)) {
				return n;
			}
		}
		return null;
	}

	/**
	 * Read notebook.
	 *
	 * @param notebookName the notebook name
	 */
	public void readNotebook(File file) {

		ZipFile zf = null;

		String notebookName = file.getName().replace(".zip", "");

		try {
			zf = new ZipFile(file.getAbsolutePath());

			Enumeration<? extends ZipEntry> entries = zf.entries();

			Notebook rNotebook = null;

			String notebookID = null;
			// iterate through all files inside zip
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				// extract id for a single ocurrence file
				if (entry.getName().endsWith(".content")) {
					// get uid of notebook
					notebookID = entry.getName().replace(".content", "");
					// Instantiates notebook
					rNotebook = new Notebook(notebookID, notebookName);
					logger.info("Read notebook '" + notebookName + "' (id=" + notebookID + ")");
				} else if (entry.getName().endsWith(".pagedata")) {
					Scanner s = new Scanner(zf.getInputStream(zf.getEntry(entry.getName())));
					ArrayList<String> list = new ArrayList<String>();
					while (s.hasNext()) {
						list.add(s.nextLine());
					}
					rNotebook.setTemplateNames(list);
					logger.info("Found '" + list.size() + "' templates (default is set as '"
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

						logger.info("Load " + page);
					}
				} else {
					// logger.debug("No importer defined yet for '" +
					// entry.getName().replace(notebookID + "/", "") + "'");
				}

			}

			MetaDataNotebook metadataNotebook = getMetaDataById(notebookID);
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
			logger.debug("Path of '" + rNotebook.getName() + "' is " + rNotebook.getFolders() + "");

			if (!notebooks.contains(rNotebook)) {
				notebooks.add(rNotebook);
			}
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
	 * Read notebooks from local storage to memory
	 */
	public void readNotebooks() {
		ArrayList<File> files = Util.listFiles(new File(DOCUMENT_STORAGE), "zip");
		for (File file : files) {
			if (!file.isDirectory()) {
				this.readNotebook(file);
			}
		}
	}

	/**
	 * Download notebook from web to local
	 *
	 * @param notebookName the notebook name
	 */
	public void downloadNotebook(String notebookName) {
		MetaDataNotebook metaDataNotebook = getMetaDataByName(notebookName);
		downloadNotebook(metaDataNotebook);
	}

	/**
	 * Download notebooks from web to local
	 */
	public void downloadNotebooks() {
		MetaDataNotebook[] metaDataNotebooks = downloadMetaDatas(true);
		for (MetaDataNotebook metaDataNotebook : metaDataNotebooks) {
			downloadNotebook(metaDataNotebook);
		}
	}

	/**
	 * Download notebook.
	 *
	 * @param document the document
	 */
	public void downloadNotebook(MetaDataNotebook document) {
		List<String> p = this.getParentFolders(document.iD);
		String folders = "";
		if (p.size() > 0) {
			for (String f : p) {
				folders += f + File.separatorChar;
			}
			// File ff = new File(folders);
			// ff.mkdirs();
		}

		File zip = new File(DOCUMENT_STORAGE + File.separatorChar + folders + document.vissibleName + ".zip");

		if (!zip.exists()) {
			zip.getParentFile().mkdirs();
		}

		downloadNotebook(document, zip);
	}

	/**
	 * Download notebook from web to local
	 *
	 * @param document the document
	 * @param file     the file
	 */
	public void downloadNotebook(MetaDataNotebook document, File file) {
		logger.info("Save/Download document " + document.vissibleName + " to " + file.getName());

		client.saveDocument(document, userToken, file);
		// saveMetaDataNotebook(document);
	}

	/**
	 * Gets the page object
	 *
	 * @param notebookName the notebook name
	 * @param pageNumber   the page number
	 * @return the page
	 */
	public Page getPage(String notebookName, int pageNumber) {
		return getNotebookByName(notebookName).getPage(pageNumber);
	}

//	public MetaDataNotebook[] readNotebookMetaDatas() {
//		ArrayList<File> files = Util.listFiles(new File(DOCUMENT_STORAGE), "meta");
//
//		MetaDataNotebook[] metaDataNotebooks = new MetaDataNotebook[files.size()];
//		for (int i = 0; i < metaDataNotebooks.length; i++) {
//			if (!files.get(i).isDirectory()) {
//
//				try {
//					ObjectMapper mapper = new ObjectMapper();
//					metaDataNotebooks[i] = mapper.readValue(files.get(i), MetaDataNotebook.class);
//				} catch (JsonParseException e) {
//				} catch (JsonMappingException e) {
//				} catch (IOException e) {
//				}
//			}
//		}
//
//		logger.info("Read Notebook MetaData (" + metaDataNotebooks.length + " entries)");
//
//		return metaDataNotebooks;
//	}

	/**
	 * Export all notebooks.
	 */
	public void exportNotebooks() {
		for (Notebook notebook : getNotebooks()) {
			exportNotebook(notebook);
		}
	}

	/**
	 * Export notebook to SVG, PNG, PDF into export folder
	 *
	 * @param notebookName the notebook name
	 */
	public void exportNotebook(Notebook notebook) {
		Util.createSvg(notebook);

		double scale = PropertiesCache.getInstance().getPropertyDouble(PropertiesCache.PNGEXPORTSCALE);
		Util.createPng(notebook, scale);

		Util.createPdf(notebook);
	}

	public MetaDataNotebook[] downloadMetaDatas() {
		return downloadMetaDatas(false);
	}

	public MetaDataNotebook[] downloadMetaDatas(boolean blobUrl) {
		try {
			MetaDataNotebook[] metadataNotebooks = client.listMetaDataNotebooks(userToken, blobUrl);

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

	public List<String> getParentFolders(String notebookId) {
		List<String> folders = new ArrayList<String>();

		MetaDataNotebook mData = this.getMetaDataById(notebookId);

		getParentFolders(mData, folders);

		if (folders.size() >= 1) {
			folders.remove(folders.size() - 1);
		}

		return folders;
	}

//	private void saveMetaDataNotebook(MetaDataNotebook meta) {
//
//		List<String> p = this.getParentFolders(meta.iD);
//		String folders = "";
//		if (p.size() > 0) {
//			for (String f : p) {
//				folders += f + File.separatorChar;
//			}
//		}
//
//		File f = new File(DOCUMENT_STORAGE + File.separatorChar + folders + meta.vissibleName + ".meta");
//		logger.debug("Save " + f.getAbsolutePath());
//
//		if (!f.exists()) {
//			f.getParentFile().mkdirs();
//		}
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		try {
//			objectMapper.writeValue(f, meta);
//		} catch (JsonGenerationException e) {
//		} catch (JsonMappingException e) {
//		} catch (IOException e) {
//		}
//
//	}

	private void getParentFolders(MetaDataNotebook item, List<String> list) {

		if (item == null) {
			return;
		}

		list.add(0, item.vissibleName);

		if (item.parent.length() > 0) {
			MetaDataNotebook parent = this.getMetaDataByName(item.parent);
			if (parent == null) {
				parent = this.getMetaDataById(item.parent);
			}

			getParentFolders(parent, list);
		}

	}

}
