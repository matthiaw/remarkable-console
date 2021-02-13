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
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.io.web.RequestClient;
import org.rogatio.productivity.remarkable.model.web.ContentMetaData;
import org.rogatio.productivity.remarkable.model.web.Credentials;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.jlarriba.jrmapi.http.Net;
import es.jlarriba.jrmapi.model.DeleteDocument;
import es.jlarriba.jrmapi.model.Document;
import es.jlarriba.jrmapi.model.MetadataDocument;
import es.jlarriba.jrmapi.model.UploadDocumentRequest;
import es.jlarriba.jrmapi.model.UploadDocumentResponse;
import es.jlarriba.jrmapi.util.FilenameUtils;
import es.jlarriba.jrmapi.util.JRmApiUtils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * The Class RemarkableClient.
 */
public class RemarkableClient extends RequestClient {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(RemarkableClient.class);

	/** The device authentication url, see https://akeil.de/posts/remarkable-cloud-api/ */
	private final String DEVICE_AUTH_URL = "https://my.remarkable.com/token/json/2/device/new";

	/** The user authentication url, see https://akeil.de/posts/remarkable-cloud-api/ */
	private final String USER_AUTH_URL = "https://my.remarkable.com/token/json/2/user/new";

	/** The base url */
	private final String BASE_URL = "https://document-storage-production-dot-remarkable-production.appspot.com";

	/** The list documents request url */
	private final String LIST_DOCS = BASE_URL + "/document-storage/json/2/docs";

	/** The prefix of the authentication token. */
	private final String PREFIXAUTHTOKEN = "Bearer";

	/** The update status url */
	private final String UPDATE_STATUS = BASE_URL + "/document-storage/json/2/upload/update-status";

	/** The upload request url */
	private final String UPLOAD_REQUEST = BASE_URL + "/document-storage/json/2/upload/request";

	/** The delete request url */
	private final String DELETE = BASE_URL + "/document-storage/json/2/delete";

	private Net net;
	private Gson gson;

	public RemarkableClient() {
		net = new Net();
		gson = new Gson();
		File workdir = new File(JRmApiUtils.WORKDIR);
		if (!workdir.exists()) {
			workdir.mkdir();
		}
	}

	/**
	 * New device token.
	 *
	 * @param oneTimeCode Must be code from
	 *                    https://my.remarkable.com/connect/desktop
	 * @return New created device token for client-connection
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String newDeviceToken(String oneTimeCode) throws IOException {

		if (oneTimeCode != null) {
			Credentials credentials = new Credentials(oneTimeCode);
			logger.info("Creating new device token");
			return post(PREFIXAUTHTOKEN, DEVICE_AUTH_URL, credentials.toString());
		} else {
			logger.error(
					"Error creating device token. Creating a new token requires a one-time-only-code from https://my.remarkable.com/connect/desktop");
			return null;
		}
	}

	/**
	 * New user token.
	 *
	 * @param deviceToken the device token
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String newUserToken(String deviceToken) throws IOException {
		logger.info("Receiving actual user token for session");

		String authToken = PREFIXAUTHTOKEN + " " + deviceToken;

		return post(authToken, USER_AUTH_URL, "");
	}

	/**
	 * Gets the meta data notebook.
	 *
	 * @param docId     the doc id
	 * @param userToken the user token
	 * @return the meta data notebook
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ContentMetaData getMetaDataNotebook(String docId, String userToken) throws IOException {
		logger.info("Receiving notebook meta-data for " + docId);

		String json = get(LIST_DOCS + "?withBlob=true&doc=" + docId, PREFIXAUTHTOKEN + " " + userToken);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, ContentMetaData[].class)[0];
	}

	/**
	 * List meta data notebooks.
	 *
	 * @param userToken the user token
	 * @return the meta data notebook[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ContentMetaData[] listMetaDataNotebooks(String userToken) throws IOException {
		return listMetaDataNotebooks(userToken, false);
	}

	/**
	 * Save document.
	 *
	 * @param document  the document
	 * @param userToken the user token
	 * @param file      the file
	 */
	public void saveDocument(ContentMetaData document, String userToken, File file) {
		getStream(document.blobURLGet, userToken, file);
	}

	/**
	 * List meta data notebooks.
	 *
	 * @param userToken the user token
	 * @param withBlob  the with blob
	 * @return the meta data notebook[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ContentMetaData[] listMetaDataNotebooks(String userToken, boolean withBlob) throws IOException {

		String url = LIST_DOCS;
		if (withBlob) {
			url = url + "?withBlob=true";
			logger.info("Receiving meta-data of all notebooks (with blobUrl)");
		} else {
			logger.info("Receiving meta-data of all notebooks");
		}

		String json = get(url, PREFIXAUTHTOKEN + " " + userToken);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, ContentMetaData[].class);
	}

	public void createDir(String name, String parentID, String userToken) {
		String id = UUID.randomUUID().toString();
		UploadDocumentRequest docRequest = new UploadDocumentRequest();
		docRequest.setID(id);
		docRequest.setType("CollectionType");
		docRequest.setVersion(1);

		List<Object> uploadRequest = new ArrayList<>();
		uploadRequest.add(docRequest);
		String response = net.put(UPLOAD_REQUEST, userToken, uploadRequest);

		List<UploadDocumentResponse> docResponse = gson.fromJson(response,
				new TypeToken<List<UploadDocumentResponse>>() {
				}.getType());
		net.putStream(docResponse.get(0).getBlobURLPut(), userToken, JRmApiUtils.createZipDirectory(id));

		MetadataDocument metadataDoc = new MetadataDocument();
		metadataDoc.setID(id);
		metadataDoc.setModifiedClient(
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'+02:00'")));
		metadataDoc.setParent(parentID);
		metadataDoc.setType("CollectionType");
		metadataDoc.setVersion(1);
		metadataDoc.setVissibleName(name);

		List<Object> uploadMetadataDoc = new ArrayList<>();
		uploadMetadataDoc.add(metadataDoc);

		net.put(UPDATE_STATUS, userToken, uploadMetadataDoc);
	}

	public void uploadDoc(File file, String parentID, String userToken) {
		String id = UUID.randomUUID().toString();
		UploadDocumentRequest docRequest = new UploadDocumentRequest();
		docRequest.setID(id);
		docRequest.setType("DocumentType");
		docRequest.setVersion(1);

		List<Object> uploadRequest = new ArrayList<>();
		uploadRequest.add(docRequest);
		String response = net.put(UPLOAD_REQUEST, userToken, uploadRequest);

		List<UploadDocumentResponse> docResponse = gson.fromJson(response,
				new TypeToken<List<UploadDocumentResponse>>() {
				}.getType());
		File doc = JRmApiUtils.createZipDocument(id, file);
		logger.debug("Doc: " + doc);
		net.putStream(docResponse.get(0).getBlobURLPut(), userToken, doc);
		JRmApiUtils.clean(id, file);

		MetadataDocument metadataDoc = new MetadataDocument();
		metadataDoc.setID(id);
		metadataDoc.setModifiedClient(
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'+02:00'")));
		metadataDoc.setParent(parentID);
		metadataDoc.setType("DocumentType");
		metadataDoc.setVersion(1);
		metadataDoc.setVissibleName(FilenameUtils.getBaseName(file.getAbsolutePath()));

		List<Object> uploadMetadataDoc = new ArrayList<>();
		uploadMetadataDoc.add(metadataDoc);

		net.put(UPDATE_STATUS, userToken, uploadMetadataDoc);
	}

	public void deleteEntry(String id, int version, String userToken) {
		DeleteDocument deleteDoc = new DeleteDocument();
		deleteDoc.setVersion(version);
		deleteDoc.setID(id);

		List<Object> uploadDeleteDoc = new ArrayList<>();
		uploadDeleteDoc.add(deleteDoc);

		net.put(DELETE, userToken, uploadDeleteDoc);
	}

	public void fetchDoc(Document doc, String path, String userToken) {
		String response = net.get(LIST_DOCS, userToken, doc.getID());
		List<Document> docs = gson.fromJson(response, new TypeToken<List<Document>>() {
		}.getType());
		if (path.charAt(path.length() - 1) == '/')
			path += "";
		else
			path += "/";
		File file = new File(JRmApiUtils.WORKDIR + doc.getVissibleName() + ".zip");
		logger.debug("Download file to " + JRmApiUtils.WORKDIR + doc.getVissibleName() + ".zip");
		net.getStream(docs.get(0).getBlobURLGet(), userToken, file);

		try {
			new ZipFile(file.getAbsolutePath()).extractFile(doc.getID() + ".epub", path,
					doc.getVissibleName() + ".epub");
			logger.debug("Unzipped epub to " + path + doc.getVissibleName() + ".epub");
			file.delete();
		} catch (ZipException e) {
			logger.debug("No epub, trying pdf...");
			try {
				new ZipFile(file.getAbsolutePath()).extractFile(doc.getID() + ".pdf", path,
						doc.getVissibleName() + ".pdf");
				logger.debug("Unzipped pdf to " + path + doc.getVissibleName() + ".pdf");
				file.delete();
			} catch (ZipException ze) {
				logger.error("Error unzipping file", e);
			}
		}
	}

	public void exportPdf(Document doc, String path, String filename, String userToken) {
		String response = net.get(LIST_DOCS, userToken, doc.getID());
		List<Document> docs = gson.fromJson(response, new TypeToken<List<Document>>() {
		}.getType());

		File zipFile = new File(JRmApiUtils.WORKDIR + doc.getVissibleName() + ".zip");
		logger.debug("Download zip to " + JRmApiUtils.WORKDIR + doc.getVissibleName() + ".zip");
		net.getStream(docs.get(0).getBlobURLGet(), userToken, zipFile);

		try {
			new ZipFile(zipFile.getAbsolutePath()).extractFile(doc.getID() + ".pdf", path, filename);
			logger.debug("Unzipped pdf to " + path + doc.getVissibleName() + ".pdf");
			// zipFile.delete();
		} catch (ZipException e) {
			logger.error("Error unzipping file", e);
		}
	}

}
