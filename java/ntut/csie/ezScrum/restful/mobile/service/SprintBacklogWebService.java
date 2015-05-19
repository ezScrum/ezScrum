package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

public class SprintBacklogWebService extends ProjectWebService {
	SprintBacklogMapper mSprintBacklogMapper;
	SprintBacklogLogic mSprintBacklogLogic;

	public SprintBacklogWebService(String username, String userpwd,
			String projectID, int iteration) throws LogonException {
		super(username, userpwd, projectID);
		mSprintBacklogLogic = new SprintBacklogLogic(super.getAllProjects()
				.get(0), iteration);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	public SprintBacklogWebService(String username, String userpwd,
			String projectID) throws LogonException {
		super(username, userpwd, projectID);
		// sprintBacklog = new SprintBacklogMapper( super.getProjectList().get(
		// 0 ) , new UserSession( super.getAccount() ) ) ;
		mSprintBacklogLogic = new SprintBacklogLogic(super.getAllProjects()
				.get(0), -1);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	public String getRESTFulResponseString() throws JSONException {
		ConvertSprintBacklog csb = new ConvertSprintBacklog();
		return csb.readStoryIDList(mSprintBacklogLogic);
	}

	public String getTaskIDList(long storyId) throws JSONException {
		ConvertSprintBacklog csb = new ConvertSprintBacklog();
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getStory(storyId).getTasks();
		String taskIDListJsonString = csb
				.convertTaskIdList(storyId, tasks);
		return taskIDListJsonString;
	}

	/**
	 * 取得task history list
	 * 
	 * @param taskID
	 * @return
	 * @throws JSONException
	 * @throws SQLException
	 */
	public String getTaskHsitoryList(String taskID) throws JSONException,
			SQLException {
		ConvertSprintBacklog csb = new ConvertSprintBacklog();
		TaskObject task = TaskObject.get(Long.parseLong(taskID));

		List<HistoryObject> taskHistories = task.getHistories();
		List<String> remainsList = new ArrayList<String>();
		String lastRemainsHour = "0";
		for (HistoryObject history : taskHistories) {
			String date = parseDate(history.getCreateTime());
			lastRemainsHour = task.getRemains(new Date(date)) + "";
			remainsList.add(lastRemainsHour);
		}
		String taskHistoryJsonString = csb.convertTaskHistory(taskHistories,
				remainsList);
		return taskHistoryJsonString;
	}

	/**
	 * 根據日期取得task 的 remains hour
	 * 
	 * @param date
	 * @param task
	 * @return
	 */
	private String getTaskRemains(Date date, TaskObject task) {
		ArrayList<HistoryObject> histories = task.getHistories();;
		
		String remains = "0";
		for (HistoryObject history : histories) {
			if (history.getCreateTime() <= date.getTime() && history.getHistoryType() == HistoryObject.TYPE_REMAIMS) {
				remains = history.getNewValue();
			}
		}
		return remains;
	}

	/**
	 * 轉換date顯示格式:yyyy/MM/dd-hh:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	private String parseDate(long date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date d = new Date(date);

		String modifiedDate = sdf.format(d);
		return modifiedDate;
	}

	/**
	 * 取得task information
	 * 
	 * @param taskId
	 * @return
	 * @throws JSONException
	 */
	public String getTaskInformation(long taskId) throws JSONException {
		ConvertSprintBacklog csb = new ConvertSprintBacklog();
		TaskObject currentTask = mSprintBacklogMapper.getTask(taskId);
		return csb.readTaskInformationList(currentTask);
	}

	/**
	 * 取得Sprint的Sprint Backlog(Sprint底下的Story及Task)
	 * 
	 * @return
	 * @throws JSONException
	 */
	public String getSprintBacklog(String handlerID) throws JSONException {
		ConvertSprintBacklog csb = new ConvertSprintBacklog();
		return csb.convertStoryTaskInformationList(mSprintBacklogLogic,
				mSprintBacklogMapper, handlerID);
		// return csb.convertStoryTaskInformationList( sprintBacklog, handlerID
		// );
	}
}
