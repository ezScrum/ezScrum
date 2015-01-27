package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

public class SprintBacklogWebService extends ProjectWebService {
	SprintBacklogMapper sprintBacklog;
	SprintBacklogLogic sprintBacklogLogic;

	public SprintBacklogWebService(String username, String userpwd,
			String projectID, int iteration) throws LogonException {
		super(username, userpwd, projectID);
		// sprintBacklog = new SprintBacklogMapper( super.getProjectList().get(
		// 0 ) , new UserSession( super.getAccount() ) , iteration ) ;
		this.sprintBacklogLogic = new SprintBacklogLogic(super.getProjectList()
				.get(0), new UserSession(super.getAccount()),
				String.valueOf(iteration));
		this.sprintBacklog = this.sprintBacklogLogic.getSprintBacklogMapper();
	}

	public SprintBacklogWebService(String username, String userpwd,
			String projectID) throws LogonException {
		super(username, userpwd, projectID);
		// sprintBacklog = new SprintBacklogMapper( super.getProjectList().get(
		// 0 ) , new UserSession( super.getAccount() ) ) ;
		this.sprintBacklogLogic = new SprintBacklogLogic(super.getProjectList()
				.get(0), new UserSession(super.getAccount()), null);
		this.sprintBacklog = this.sprintBacklogLogic.getSprintBacklogMapper();
	}

	public String getRESTFulResponseString() throws JSONException {
		ConvertSprintBacklog csb = new ConvertSprintBacklog();
		return csb.readStoryIDList(this.sprintBacklogLogic);
		// return csb.readStoryIDList( sprintBacklog );
	}

	public String getTaskIDList(String storyID) throws JSONException {
		ConvertSprintBacklog csb = new ConvertSprintBacklog();
		IIssue[] taskIDList = sprintBacklog.getTasksMap().get(
				Long.parseLong(storyID));
		String taskIDListJsonString = csb
				.convertTaskIDList(storyID, taskIDList);
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
		IIssue taskByID = this.sprintBacklogLogic.getTaskById(Long
				.parseLong(taskID));

		List<HistoryObject> taskHistoryList = taskByID.getHistories();
		List<String> remainsList = new ArrayList<String>();
		String lastRemainsHour = "0";
		for (HistoryObject history : taskHistoryList) {
			String date = parseDate(history.getModifiedTime());
			lastRemainsHour = getTaskRemains(new Date(date), taskByID);
			remainsList.add(lastRemainsHour);
		}
		String taskHistoryJsonString = csb.convertTaskHistory(taskHistoryList,
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
	private String getTaskRemains(Date date, IIssue task) {
		ArrayList<HistoryObject> histories = null;
		try {
			histories = task.getHistories();
		} catch (SQLException e) {
		}
		
		String remains = "0";
		for (HistoryObject history : histories) {
			if (history.getModifiedTime() <= date.getTime() && history.getHistoryType() == HistoryObject.TYPE_REMAIMS) {
				remains = history.getNewValue();
			}
		}
//		double point = 0;
//
//		try {
//			point = Double.parseDouble(task
//					.getTagValue(ScrumEnum.REMAINS, date));
//		} catch (Exception e) {
//			try {
//				// 表示這個Task沒有REMAINS，那麼就取得ESTIMATION八
//				point = Double.parseDouble(task.getTagValue(
//						ScrumEnum.ESTIMATION, date));
//			} catch (Exception e1) {
//				// 如果還是沒有，那就回傳 0
//				return "0.0";
//			}
//		}
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
	 * @param taskID
	 * @return
	 * @throws JSONException
	 */
	public String getTaskInformation(String taskID) throws JSONException {
		ConvertSprintBacklog csb = new ConvertSprintBacklog();
		IIssue currentTask = sprintBacklog.getStory(Long.parseLong(taskID));
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
		return csb.convertStoryTaskInformationList(this.sprintBacklogLogic,
				this.sprintBacklog, handlerID);
		// return csb.convertStoryTaskInformationList( sprintBacklog, handlerID
		// );
	}
}
