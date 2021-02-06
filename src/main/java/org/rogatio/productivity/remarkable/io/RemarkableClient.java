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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.io.file.Util;
import org.rogatio.productivity.remarkable.io.web.RequestClient;
import org.rogatio.productivity.remarkable.model.web.ContentMetaData;
import org.rogatio.productivity.remarkable.model.web.Credentials;
import org.rogatio.productivity.remarkable.model.web.UploadRequest;
import org.rogatio.productivity.remarkable.model.web.UploadResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class RemarkableClient.
 */
public class RemarkableClient extends RequestClient {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(RemarkableClient.class);

	/** The device auth url, see https://akeil.de/posts/remarkable-cloud-api/ */
	private final String DEVICE_AUTH_URL = "https://my.remarkable.com/token/json/2/device/new";

	/** The user auth url, see https://akeil.de/posts/remarkable-cloud-api/ */
	private final String USER_AUTH_URL = "https://my.remarkable.com/token/json/2/user/new";

	/** The base url. */
	private final String BASE_URL = "https://document-storage-production-dot-remarkable-production.appspot.com";

	/** The list docs. */
	private final String LIST_DOCS = BASE_URL + "/document-storage/json/2/docs";

	/** The prefixauthtoken. */
	private final String PREFIXAUTHTOKEN = "Bearer";

	/** The update status. */
	@SuppressWarnings("unused")
	private final String UPDATE_STATUS = BASE_URL + "/document-storage/json/2/upload/update-status";

	/** The upload request. */
	@SuppressWarnings("unused")
	private final String UPLOAD_REQUEST = BASE_URL + "/document-storage/json/2/upload/request";

	/** The delete. */
	@SuppressWarnings("unused")
	private final String DELETE = BASE_URL + "/document-storage/json/2/delete";

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
		UploadRequest dr = new UploadRequest("CollectionType");

		List<Object> uploadRequest = new ArrayList<>();
		uploadRequest.add(dr);

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String rdata = objectMapper.writeValueAsString(uploadRequest);
			String response = put(UPLOAD_REQUEST, PREFIXAUTHTOKEN + " " + userToken, rdata);

			System.out.println(response);

			ObjectMapper mapper = new ObjectMapper();
			UploadResponse[] responses = mapper.readValue(response, UploadResponse[].class);

			put(responses[0].blobURLPut, PREFIXAUTHTOKEN + " " + userToken, Util.createZipDirectory(id));

			ContentMetaData metadataDoc = new ContentMetaData();
			metadataDoc.iD = id;
			metadataDoc.modifiedClient = new Date();
			metadataDoc.parent = parentID;
			metadataDoc.type = "CollectionType";
			metadataDoc.version = 1;
			metadataDoc.vissibleName = name;

			List<Object> uploadMetadataDoc = new ArrayList<>();
			uploadMetadataDoc.add(metadataDoc);

			String json = mapper.writeValueAsString(uploadMetadataDoc);

			put(UPDATE_STATUS, PREFIXAUTHTOKEN + " " + userToken, json);

			Util.deleteTemporaryDirectory();

		} catch (JsonProcessingException e) {
			logger.error("Error creating directory on remarkable", e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error creating directory on remarkable", e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Error creating directory on remarkable", e);
			e.printStackTrace();
		}

	}

}
