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
package org.rogatio.productivity.remarkable.model.web;

import java.util.Date;

import org.rogatio.productivity.remarkable.model.BaseObject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class MetaDataNotebook.
 * 
 * @author Matthias Wegner
 */
public class ContentMetaData extends BaseObject {

	/** The iD. */
	@JsonProperty("ID")
	public String iD;

	/** The version. */
	@JsonProperty("Version")
	public int version;

	/** The message. */
	@JsonProperty("Message")
	public String message;

	/** The success. */
	@JsonProperty("Success")
	public Boolean success;

	/** The blob URL get. */
	@JsonProperty("BlobURLGet")
	public String blobURLGet = null;

	/** The blob URL get expires. */
	@JsonProperty("BlobURLGetExpires")
	public Date blobURLGetExpires = null;

	/** The modified client. */
	@JsonProperty("ModifiedClient")
	public Date modifiedClient;

	/** The type. */
	@JsonProperty("Type")
	public String type;

	/** The visible name. */
	@JsonProperty("VissibleName")
	public String vissibleName;

	/** The current page. */
	@JsonProperty("CurrentPage")
	public Integer currentPage = null;

	/** The bookmarked. */
	@JsonProperty("Bookmarked")
	public Boolean bookmarked = null;

	/** The parent. */
	@JsonProperty("Parent")
	public String parent = null;
}
