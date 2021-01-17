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
import org.rogatio.productivity.remarkable.model.web.MetaDataNotebook;
import org.rogatio.productivity.remarkable.ssh.SshClient;

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
	public MetaDataNotebook getMetaDataNotebookById(String id) {
		try {
			return client.getMetaDataNotebook(id, userToken);
		} catch (IOException e) {
			logger.error("error receiving meta-data for notebook " + id);
		}
		return null;
	}

	/**
	 * Gets the meta data notebook by name.
	 *
	 * @param name the name
	 * @return the meta data notebook by name
	 */
	public MetaDataNotebook getMetaDataNotebookByName(String name) {
		MetaDataNotebook[] docs = listMetaDataNotebooks();
		for (MetaDataNotebook document : docs) {
			if (document.vissibleName.equals(name)) {
				return document;
			}
		}
		return null;
	}

	/** The notebooks. */
	private Map<String, Notebook> notebooks = new HashMap<String, Notebook>();

	/**
	 * Gets the notebooks.
	 *
	 * @return the notebooks
	 */
	public List<Notebook> getNotebooks() {
		return new ArrayList<>(notebooks.values());
	}

	/**
	 * Gets the notebook.
	 *
	 * @param name the name
	 * @return the notebook
	 */
	public Notebook getNotebook(String name) {
		return notebooks.get(name);
	}

	/**
	 * Read notebook.
	 *
	 * @param notebookName the notebook name
	 */
	public void readNotebook(String notebookName) {

		ZipFile zf = null;

		try {
			zf = new ZipFile(new File(DOCUMENT_STORAGE + File.separatorChar + notebookName + ".zip").getAbsolutePath());

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

			notebooks.put(notebookName, rNotebook);

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

		MetaDataNotebook[] metas = listMetaDataNotebooks(true);

		if (metas == null) {
			logger.warn("No MetaData of Notebooks found");
			return;
		}

		if (metas.length == 0) {
			logger.warn("No MetaData of Notebooks found");
			return;
		}

		for (MetaDataNotebook metaDataNotebook : metas) {
			this.readNotebook(metaDataNotebook.vissibleName);
		}
	}

	/**
	 * List meta data notebooks.
	 *
	 * @return the meta data notebook[]
	 */
	public MetaDataNotebook[] listMetaDataNotebooks() {
		return listMetaDataNotebooks(false);
	}

	/**
	 * Download notebook from web to local
	 *
	 * @param notebookName the notebook name
	 */
	public void downloadNotebook(String notebookName) {
		MetaDataNotebook metaDataNotebook = getMetaDataNotebookByName(notebookName);
		downloadNotebook(metaDataNotebook);
	}

	/**
	 * Download notebooks from web to local
	 */
	public void downloadNotebooks() {
		MetaDataNotebook[] metaDataNotebooks = listMetaDataNotebooks(true);
		for (MetaDataNotebook metaDataNotebook : metaDataNotebooks) {
			downloadNotebook(metaDataNotebook);
		}
	}

	/**
	 * Download meta data notebook.
	 *
	 * @param nameOfNotebook the name of notebook
	 */
	public void downloadMetaDataNotebook(String nameOfNotebook) {
		MetaDataNotebook mdn = getMetaDataNotebookByName(nameOfNotebook);
		downloadNotebook(mdn);
	}

	/**
	 * Download notebook.
	 *
	 * @param document the document
	 */
	public void downloadNotebook(MetaDataNotebook document) {
		File zip = new File(DOCUMENT_STORAGE + File.separatorChar + document.vissibleName + ".zip");

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
	}

	/**
	 * Gets the page object
	 *
	 * @param notebookName the notebook name
	 * @param pageNumber   the page number
	 * @return the page
	 */
	public Page getPage(String notebookName, int pageNumber) {
		return getNotebook(notebookName).getPage(pageNumber);
	}

	/**
	 * Export all notebooks.
	 */
	public void exportNotebooks() {
		for (Notebook notebook : getNotebooks()) {
			exportNotebook(notebook.getName());
		}
	}

	/**
	 * Export notebook to SVG, PNG, PDF into export folder
	 *
	 * @param notebookName the notebook name
	 */
	public void exportNotebook(String notebookName) {
		Notebook nb = getNotebook(notebookName);
		Util.createSvg(nb);
		Util.createPng(nb);
		Util.createPdf(nb);
	}

	/**
	 * List meta data notebooks.
	 *
	 * @param blobUrl the blob url
	 * @return the meta data notebook[]
	 */
	public MetaDataNotebook[] listMetaDataNotebooks(boolean blobUrl) {
		try {
			return client.listMetaDataNotebooks(userToken, true);
		} catch (IOException e) {
			logger.error("Error getting meta-data notebooks", e);
		}
		return null;
	}

}
