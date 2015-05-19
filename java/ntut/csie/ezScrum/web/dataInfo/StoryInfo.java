package ntut.csie.ezScrum.web.dataInfo;

import ntut.csie.ezScrum.web.dataObject.StoryObject;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class StoryInfo {
	public long id = -1;
	public String name = "";
	public String notes = "";
	public String howToDemo = "";
	public int importance = 0;
	public int value = 0;
	public int estimate = 0;
	public int status = StoryObject.STATUS_UNCHECK;
	public long sprintId = -1;
	public String tags = ""; // aaa,bbb,ccc
	
	public StoryInfo() {
	}
	
	public StoryInfo(String jsonString) throws JSONException {
		loadJSON(jsonString);
	}

	public void loadJSON(String jsonString) throws JSONException {
		JSONObject story = new JSONObject(jsonString);
		try {
			id = story.getLong("id");
		} catch (JSONException e) {
			id = -1;
		}
		name = story.getString("name");
		notes = story.getString("notes");
		howToDemo = story.getString("how_to_demo");
		importance = story.getInt("importance");
		value = story.getInt("value");
		estimate = story.getInt("estimate");
		status = story.getInt("status");
		sprintId = story.getLong("sprint_id");
		tags = story.getString("tags");
	}
}
