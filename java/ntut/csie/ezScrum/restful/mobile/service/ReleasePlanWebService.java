package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ReleasePlanWebService extends ProjectWebService {
	private ReleasePlanHelper mReleasePlanHelper;
	private ProjectObject mProject;
	
	public ReleasePlanWebService(AccountObject user, String projectName) throws LogonException {
		super(user, projectName);
		initialize();
	}

	public ReleasePlanWebService(String username, String userPwd, String projectName) throws LogonException {
		super(username, userPwd, projectName);
		initialize();
	}

	private void initialize() {
		mProject = getAllProjects().get(0);
		mReleasePlanHelper = new ReleasePlanHelper(mProject);
	}

	/**
	 * 取得專案底下所有的Release plan
	 * @return
	 * @throws JSONException 
	 */
	public String getAllReleasePlan() throws JSONException {
		ArrayList<ReleaseObject> releases = mReleasePlanHelper.getReleases();
		JSONArray releaseJsonArray = new JSONArray();
		for (ReleaseObject release : releases) {
			releaseJsonArray.put(release.toJSON());
		}
		return releaseJsonArray.toString();
	}
	
	/**
	 * 取得專案底下所有的Release plan with all item
	 * @return
	 * @throws SQLException 
	 * @throws JSONException 
	 */
	public String getAllReleasePlanWithAllItem() throws JSONException {
		ArrayList<ReleaseObject> releases = mReleasePlanHelper.getReleases();
		JSONObject wholeJson = new JSONObject();
		JSONArray releaseJsonArray = new JSONArray();
		for (ReleaseObject release : releases) {
			releaseJsonArray.put(release.toJSON());
		}
		wholeJson.put("releases", releaseJsonArray);
		return wholeJson.toString();
	}

	/**
	 * 取得 ReleasePlan
	 * 
	 * @param releaseId
	 * @return
	 * @throws SQLException 
	 */
	public String getReleasePlan(long releaseId) throws SQLException {
		ReleaseObject release = mReleasePlanHelper.getReleasePlan(releaseId);
		return release.toString();
	}
}
