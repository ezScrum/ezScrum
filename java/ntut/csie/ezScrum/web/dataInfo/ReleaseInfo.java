package ntut.csie.ezScrum.web.dataInfo;

import ntut.csie.ezScrum.web.databaseEnum.ReleaseEnum;
import ntut.csie.ezScrum.web.databaseEnum.SprintEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ReleaseInfo {
	public long id = -1;
	public String name = "";
	public String description = "";
	public String startDate = "";
	public String endDate = "";
	
	public ReleaseInfo() {}
	
	public ReleaseInfo(String jsonString) throws JSONException {
		loadJSON(jsonString);
	}
	
	private void loadJSON(String jsonString) throws JSONException {
		JSONObject release = new JSONObject(jsonString);
		try {
			id = release.getLong("id");
		} catch (JSONException e) {
			id = -1;
		}
		name = release.getString(ReleaseEnum.NAME);
		description = release.getString(ReleaseEnum.DESCRIPTION);
		startDate = release.getString(SprintEnum.START_DATE);
		endDate = release.getString(SprintEnum.END_DATE);
	}
}
