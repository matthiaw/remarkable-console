package org.rogatio.productivity.remarkable.model.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class ContentData {

	private Map<String, Object> rootData = new HashMap<>();
	private Map<String, Integer> transform = new HashMap<>();
	private Map<String, Object> extraMetadata = new HashMap<>();
	private List<String> pages = new ArrayList<>();

	public ContentData(String contentJson) {
		// System.out.println(contentJson.trim().length());
		// System.out.println(contentJson);

		if (contentJson==null) {
			return;
		}
		
		if (contentJson.length() > 5) {

			JSONObject contentData = new JSONObject(contentJson);

			for (String key : contentData.keySet()) {
				Object object = contentData.get(key);
				if (!(object instanceof JSONObject) && !(object instanceof JSONArray)) {
					// System.out.println(key + ": " + object + " - " + object.getClass());
					rootData.put(key, object);
				}
			}

			JSONObject transformData = contentData.getJSONObject("transform");
			for (String key : transformData.keySet()) {
				// System.out.println(key + ": " + transformData.getInt(key));
				transform.put(key, transformData.getInt(key));
			}

			JSONArray pagesJson = contentData.getJSONArray("pages");
			for (int i = 0; i < pagesJson.length(); i++) {
				// System.out.println();
				pages.add(pagesJson.getString(i));
			}

//					int coverPageNumber = contentData.getInt("coverPageNumber");
//					boolean dummyDocument = contentData.getBoolean("dummyDocument");
			JSONObject extraMetadata = contentData.getJSONObject("extraMetadata");

			for (String key : extraMetadata.keySet()) {
				// System.out.println(key + ": " + extraMetadata.getString(key));
				this.extraMetadata.put(key, extraMetadata.get(key));
			}

		}

	}

	public String getOrientation() {
		Object orientation = rootData.get("orientation");

		if (orientation != null) {
			return orientation.toString();
		} else {
			return "unknown";
		}

	}

}
