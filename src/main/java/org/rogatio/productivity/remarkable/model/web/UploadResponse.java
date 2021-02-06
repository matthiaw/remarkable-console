package org.rogatio.productivity.remarkable.model.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadResponse {
	@JsonProperty("ID")
	public String iD;
	@JsonProperty("Version")
	public int version;
	@JsonProperty("Message")
	public String message;
	@JsonProperty("Success")
	public boolean success;
	@JsonProperty("BlobURLPut")
	public String blobURLPut;
	@JsonProperty("BlobURLPutExpires")
	public String blobURLPutExpires;
}