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
package org.rogatio.productivity.remarkable.model.template;

import java.util.List;

import org.rogatio.productivity.remarkable.model.BaseObject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * See https://remarkablewiki.com/tips/templates See
 * https://remarkablewiki.com/tech/ssh
 * 
 * @author Matthias Wegner
 *
 */
public class Template extends BaseObject {

	/** The name. */
	@JsonProperty("name")
	public String name;

	/** The filename. */
	@JsonProperty("filename")
	public String filename;

	/** The icon code. */
	@JsonProperty("iconCode")
	public String iconCode;

	/** The landscape. */
	@JsonProperty("landscape")
	public String landscape;

	/** The categories. */
	@JsonProperty("categories")
	public List<String> categories;
}
