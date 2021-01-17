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

import java.util.UUID;

import org.rogatio.productivity.remarkable.model.BaseObject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Credentials.
 * 
 * @author Matthias Wegner
 */
public class Credentials extends BaseObject {

	/** The code. */
	@JsonProperty("code")
	public String code;

	/** The device id. */
	@JsonProperty("deviceId")
	public String deviceId = UUID.randomUUID().toString();

	/** The device desc. */
	@JsonProperty("deviceDesc")
	public String deviceDesc = "desktop-windows";

	/**
	 * Instantiates a new credentials.
	 *
	 * @param code the code
	 */
	public Credentials(String code) {
		this.code = code;
	}
}
