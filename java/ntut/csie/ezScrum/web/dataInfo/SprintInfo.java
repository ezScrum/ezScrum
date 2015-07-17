package ntut.csie.ezScrum.web.dataInfo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class SprintInfo {
	public long id = -1;
	public int interval = 0;
	public int members = 0;
	public int hoursCanCommit = 0;
	public int focusFactor = 0;
	public String sprintGoal = "";
	public String startDate = "";
	public String dueDate = "";
	public String demoDate = "";
	public String demoPlace = "";
	public String dailyInfo = "";
	
	public SprintInfo() {}
	
	public SprintInfo(String jsonString) throws JSONException {
		loadJSON(jsonString);
	}
	
	private void loadJSON(String jsonString) throws JSONException {
		JSONObject sprint = new JSONObject(jsonString);
		try {
			id = sprint.getLong("id");
		} catch (JSONException e) {
			id = -1;
		}
		interval = sprint.getInt("interval");
		members = sprint.getInt("members");
		hoursCanCommit = sprint.getInt("hours_can_commit");
		focusFactor = sprint.getInt("focus_factor");
		sprintGoal = sprint.getString("sprint_goal");
		startDate = sprint.getString("start_date");
		demoDate = sprint.getString("demo_date");
		demoPlace = sprint.getString("demo_place");
		dailyInfo = sprint.getString("daily_info");
	}
}
