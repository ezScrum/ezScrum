package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprint;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

import com.google.gson.Gson;

public class SprintPlanWebService extends ProjectWebService{
	SprintPlanHelper mSprintPlanHelper;
	
	public SprintPlanWebService(AccountObject user, String projectID) throws LogonException {
		super(user, projectID);
		mSprintPlanHelper = new SprintPlanHelper(getAllProjects().get(0));
	}

	/**
	 * 新增 sprint
	 */
	public void createSprint(SprintObject sprint) {
		mSprintPlanHelper.createSprint(sprint);
	}

	/**
	 * 刪除 sprint
	 */
	public void deleteSprint(String id) {
		mSprintPlanHelper.deleteSprint(id);
	}
	
	/**
	 * 更新修改後的 sprint 資料
	 */
	public void updateSprint(SprintObject sprint) {
		mSprintPlanHelper.updateSprint(sprint);
	}

	/**
	 * 取得 Project 內所有 Sprint 以及當前 Sprint ID
	 **/
	public String getAllSprint() throws JSONException{
		List<SprintObject> sprintList = mSprintPlanHelper.getAllSprint();
		return ConvertSprint.convertSprintListToJsonString(sprintList);
	}

	public String getCurrentSprint() throws JSONException{
		ConvertSprintBacklog csb = new ConvertSprintBacklog();
		// 以當前日期找進行中的Sprint，若無進行中的Sprint，則往後找未過期的Sprint.
		ISprintPlanDesc currentSprint = mSprintPlanHelper.loadCurrentPlan();
		// 如果 project 中沒有 sprint 則回傳空字串
		if (currentSprint != null)
			return csb.readSprintInformationList(currentSprint);
		else 
			return "";
	}

	/**
	 * 取得某個 sprint 包含 story 和 task
	 * @param sprintID
	 * @return
	 * @throws SQLException 
	 */
	public String getSprintWithAllItem(String sprintID) throws SQLException {
		Gson gson = new Gson();
		return gson.toJson(mSprintPlanHelper.getSprint(sprintID));
	}
	
	/**
	 * 取得某個 sprint 包含 story 和 task
	 * @param sprintID
	 * @return
	 * @throws SQLException 
	 */
	public String getSprintWithAllItem() throws SQLException {
		Gson gson = new Gson();
		List<SprintObject> sprintList = mSprintPlanHelper.getAllSprint();
		List<SprintObject> result = new ArrayList<SprintObject>();
		for (SprintObject sprint : sprintList) {
			result.add(mSprintPlanHelper.getSprint(sprint.id));
		}
		return gson.toJson(result);
	}
	
	/****
	 * 取得所有Sprint information. 以及當前Sprint ID.
	 */
	public String getRESTFulResponseString() throws JSONException {
		List<ISprintPlanDesc> iSprintPlanDescList = mSprintPlanHelper.loadListPlans();
		ConvertSprintBacklog csb = new ConvertSprintBacklog();

		// 以當前日期找進行中的Sprint ID，若無進行中的Sprint，則往後找未過期的Sprint ID.
		int currentSprintID = mSprintPlanHelper.getCurrentSprintID();
		
		return csb.readSprintInformationList(iSprintPlanDescList, currentSprintID);
	}
}
