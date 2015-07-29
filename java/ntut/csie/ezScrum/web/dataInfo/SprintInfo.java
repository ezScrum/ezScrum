package ntut.csie.ezScrum.web.dataInfo;

import ntut.csie.ezScrum.web.databasEnum.SprintEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class SprintInfo {
	public long id = -1;
	public int interval = 0;
	public int membersAmount = 0;
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
		interval = sprint.getInt(SprintEnum.INTERVAL);
		membersAmount = sprint.getInt(SprintEnum.MEMBERS);
		hoursCanCommit = sprint.getInt(SprintEnum.AVAILABLE_HOURS);
		focusFactor = sprint.getInt(SprintEnum.FOCUS_FACTOR);
		sprintGoal = sprint.getString(SprintEnum.GOAL);
		startDate = sprint.getString(SprintEnum.START_DATE);
		demoDate = sprint.getString(SprintEnum.DEMO_DATE);
		demoPlace = sprint.getString(SprintEnum.DEMO_PLACE);
		dailyInfo = sprint.getString(SprintEnum.DAILY_INFO);
	}
}
