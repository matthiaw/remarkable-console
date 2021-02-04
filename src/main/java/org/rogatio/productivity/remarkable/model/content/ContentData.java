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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The Class ContentData.
 */
public class ContentData {

	/** The root data. */
	private Map<String, Object> rootData = new HashMap<>();

	/** The transform. */
	private Map<String, Integer> transform = new HashMap<>();

	/** The extra metadata. */
	private Map<String, Object> extraMetadata = new HashMap<>();

	/** The pages. */
	private List<String> pages = new ArrayList<>();

	/**
	 * Instantiates a new content data.
	 *
	 * @param contentJson the content json
	 */
	public ContentData(String contentJson) {

		if (contentJson == null) {
			return;
		}

		if (contentJson.length() > 5) {

			JSONObject contentData = new JSONObject(contentJson);

			for (String key : contentData.keySet()) {
				Object object = contentData.get(key);
				if (!(object instanceof JSONObject) && !(object instanceof JSONArray)) {
					rootData.put(key, object);
				}
			}

			JSONObject transformData = contentData.getJSONObject("transform");
			for (String key : transformData.keySet()) {
				transform.put(key, transformData.getInt(key));
			}

			JSONArray pagesJson = contentData.getJSONArray("pages");
			for (int i = 0; i < pagesJson.length(); i++) {
				pages.add(pagesJson.getString(i));
			}

			JSONObject extraMetadata = contentData.getJSONObject("extraMetadata");

			for (String key : extraMetadata.keySet()) {
				this.extraMetadata.put(key, extraMetadata.get(key));
			}

		}

	}

	/**
	 * Gets the orientation.
	 *
	 * @return the orientation
	 */
	public String getOrientation() {
		Object orientation = rootData.get("orientation");

		if (orientation != null) {
			return orientation.toString();
		} else {
			return "unknown";
		}

	}

}
