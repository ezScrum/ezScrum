package ntut.csie.ezScrum.web.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic.SprintBacklogDateColumn;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SprintBacklogTreeStructure;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import com.google.gson.Gson;

public class SprintBacklogHelper {
	private IProject mIProject;
	private ProjectObject mProject;
	private IUserSession mUserSession;
	private SprintBacklogLogic mSprintBacklogLogic;
	private SprintBacklogMapper mSprintBacklogMapper;
	private long mSprintId;

	/**
	 * 待刪
	 */
	@Deprecated
	public SprintBacklogHelper(IProject project, IUserSession userSession) {
		mIProject = project;
		mUserSession = userSession;
		mSprintBacklogLogic = new SprintBacklogLogic(mIProject, mUserSession,
				null);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	// 等 IProject 拿掉，就可以用此建構子
	public SprintBacklogHelper(ProjectObject project, IUserSession userSession) {
		mProject = project;
		mUserSession = userSession;
		mSprintBacklogLogic = new SprintBacklogLogic(project, userSession, 0);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	/**
	 * 待刪
	 */
	@Deprecated
	public SprintBacklogHelper(IProject project, IUserSession userSession,
			String sprintId) {
		mIProject = project;
		mUserSession = userSession;
		mSprintId = Long.parseLong(sprintId);
		mSprintBacklogLogic = new SprintBacklogLogic(mIProject, mUserSession,
				sprintId);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	// 等 IProject 拿掉，就可以用此建構子
	public SprintBacklogHelper(ProjectObject project, IUserSession userSession,
			long sprintId) {
		mProject = project;
		mUserSession = userSession;
		mSprintId = sprintId;
		mSprintBacklogLogic = new SprintBacklogLogic(mProject, mUserSession,
				mSprintId);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	/**
	 * ----- Story -----
	 */

	/**
	 * Add exist story to sprint
	 * 
	 * @param storiesId
	 * @param releaseId
	 */
	public void addExistingStory(List<Long> storiesId, String releaseId) {
		ProductBacklogLogic helper = new ProductBacklogLogic(mUserSession,
				mIProject);

		if ((mSprintId != 0) && (mSprintId != -1)) {
			// 將 Story 加入 Sprint 當中
			helper.addIssueToSprint(storiesId, String.valueOf(mSprintId));

			// 檢查 Sprint 是否有存在於某個 Release 中
			ReleasePlanHelper releasePlan = new ReleasePlanHelper(mIProject);
			String sprintReleaseId = releasePlan.getReleaseID(String
					.valueOf(mSprintId));

			// 如果有的話，將所有 Story 加入 Release
			if (!(sprintReleaseId.equals("0"))) {
				helper.addReleaseTagToIssue(storiesId, sprintReleaseId);
			}
		} else {
			helper.addReleaseTagToIssue(storiesId, releaseId);
		}
	}

	public IIssue getStory(long issueId) {
		return mSprintBacklogMapper.getStory(issueId);
	}

	public IIssue[] getStoryInSprint(long sprintId) {
		return mSprintBacklogMapper.getStoriesBySprintId(sprintId);
	}

	public ArrayList<IIssue> getStoriesByImportance() {
		return mSprintBacklogLogic.getStoriesByImp();
	}

	/**
	 * Get existing stories by release id
	 * 
	 * @param releaseId
	 * @return IStory list
	 */
	public ArrayList<IStory> getExistingStories(String releaseId)
			throws SQLException {
		ArrayList<IStory> stories = null;
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(
				mUserSession, mIProject);
		if ((mSprintId != 0) && (mSprintId != -1)) {
			// get release ID by sprint ID
			ReleasePlanHelper rphelper = new ReleasePlanHelper(mIProject);
			releaseId = rphelper.getReleaseID(String.valueOf(mSprintId));
			// get stories that exist in
			stories = productBacklogLogic.getAddableStories(
					String.valueOf(mSprintId), releaseId);
		} else if ((releaseId != null) && (!releaseId.isEmpty())
				&& (!releaseId.equals("-1"))) {
			// Select from Release Plan
			// get stories that exist in
			if (Integer.parseInt(releaseId) > 0) {
				stories = productBacklogLogic.getAddableStories();
			}
		} else {
			stories = null;
		}
		return stories;
	}

	/**
	 * ----- Task -----
	 */

	public TaskObject addTask(long projectId, TaskInfo taskInfo) {
		long taskId = mSprintBacklogMapper.addTask(projectId, taskInfo);
		return TaskObject.get(taskId);
	}

	public TaskObject getTask(long taskId) {
		return mSprintBacklogMapper.getTask(taskId);
	}

	public ArrayList<TaskObject> getTasksByStoryId(long storyId) {
		return mSprintBacklogMapper.getTasksByStoryId(storyId);
	}

	public ArrayList<TaskObject> getTasksWithNoParent(long projectId) {
		return mSprintBacklogMapper.getTasksWithNoParent(projectId);
	}

	public void deleteTask(long taskId) {
		mSprintBacklogMapper.deleteTask(taskId);
	}

	public void dropTask(long taskId) {
		TaskObject task = TaskObject.get(taskId);
		// reset status, handler
		resetTask(taskId, task.getName(), task.getNotes(), null);
		// remove relation
		mSprintBacklogMapper.dropTask(taskId);
	}

	public void updateTask(TaskInfo taskInfo, String handlerUsername,
			String rawPartnersUsername) {
		AccountObject handler = AccountObject.get(handlerUsername);
		long handlerId = -1;
		if (handler != null) {
			handlerId = handler.getId();
		}
		ArrayList<Long> partnersId = new ArrayList<Long>();
		// split raw partners' user name by symbol ;
		String[] partnersUsername = rawPartnersUsername.split(";");
		for (String partnerUsername : partnersUsername) {
			AccountObject partner = AccountObject.get(partnerUsername);
			if (partner != null) {
				partnersId.add(partner.getId());
			}
		}
		taskInfo.handlerId = handlerId;
		taskInfo.partnersId = partnersId;
		mSprintBacklogMapper.updateTask(taskInfo);
	}

	public void addExistingTask(String[] selectedTasksStringId, long storyId) {
		ArrayList<Long> tasksId = new ArrayList<Long>();
		for (String taskStringId : selectedTasksStringId) {
			long taskId = Long.parseLong(taskStringId);
			tasksId.add(taskId);
		}
		mSprintBacklogMapper.addExistingTasksToStory(tasksId, storyId);
	}

	public void closeStory(long id, String notes, String changeDate) {
		mSprintBacklogMapper.closeStory(id, notes, changeDate);
	}

	public void reopenStory(long issueId, String name, String bugNote,
			String changeDate) {
		mSprintBacklogLogic.reopenStory(issueId, name, bugNote, changeDate);
	}

	/**
	 * Task 從 Not Check Out -> Check Out path: CheckOutTask.do class:
	 * CheckOutTaskAction Test class: CheckOutTaskActionTest
	 */
	public void checkOutTask(long taskId, String name, String handler,
			String partners, String notes, String changeDate) {
		mSprintBacklogLogic.checkOutTask(taskId, name, handler, partners,
				notes, changeDate);
	}

	public void closeTask(long id, String name, String notes, String changeDate) {
		mSprintBacklogLogic.closeTask(id, name, notes, changeDate);
	}

	public void reopenTask(long taskId, String name, String notes,
			String changeDate) {
		mSprintBacklogLogic.reopenTask(taskId, name, notes, changeDate);
	}

	/**
	 * Task 從 Check Out -> Not Check Out path: ResetTask.do class:
	 * ResetTaskAction Test class: ResetTaskActionTest
	 */
	public void resetTask(long id, String name, String notes, String changeDate) {
		mSprintBacklogLogic.resetTask(id, name, notes, changeDate);
	}

	/**
	 * ------ 處理 Action 的 response information ------
	 */

	/**
	 * path: showSprintBacklogTreeListInfo.do class:
	 * ntut.csie.ezScrum.web.action.backlog.ShowSprintBacklogListInfoAction
	 * 
	 * @return
	 */
	public String getSprintBacklogListInfoText() {
		ArrayList<SprintBacklogTreeStructure> SBtree = new ArrayList<SprintBacklogTreeStructure>();
		if (mSprintBacklogMapper != null) {
			// 取得工作天數
			int availableDays = mSprintBacklogLogic
					.getSprintAvailableDays(mSprintId);

			if (this.mSprintBacklogMapper.getSprintPlanId() > 0) {
				ArrayList<IIssue> stories = getStoriesByImportance();
				Map<Long, ArrayList<TaskObject>> storyTaskMap = mSprintBacklogMapper
						.getTasksMap();

				// 取得 Sprint 日期的 Column
				ArrayList<SprintBacklogDateColumn> cols = null;
				if (mSprintBacklogLogic.getCurrentDateColumns() == null)
					cols = mSprintBacklogLogic.calculateSprintBacklogDateList(
							mSprintBacklogMapper.getSprintStartDate(),
							availableDays);
				else
					cols = mSprintBacklogLogic.getCurrentDateColumns();

				for (IIssue story : stories) {
					SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(
							story, storyTaskMap.get(Long.valueOf(story
									.getIssueID())),
							mSprintBacklogLogic.getCurrentDateList());
					SBtree.add(tree);
				}
			} else {
				// null sprint backlog
				SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure();
				SBtree.add(tree);
			}
		}

		return (new Gson()).toJson(SBtree);
	}

	/**
	 * path: showSprintBacklog2.do class:
	 * ntut.csie.ezScrum.web.action.backlog.ShowSprintBacklogAction
	 */
	public String getShowSprintBacklogText() {
		String result;
		// 建立 this Sprint Store 的資料
		List<IIssue> issues = null;
		int currentSprintId = 0;
		int releaseId = 0;
		double currentPoint = 0.0d;
		double limitedPoint = 0.0d;
		double taskPoint = 0.0d;
		String sprintGoal = "";
		if ((mSprintBacklogMapper != null)
				&& (mSprintBacklogMapper.getSprintPlanId() > 0)) {
			// 存在一 current sprint
			issues = getStoriesByImportance();
			currentSprintId = mSprintBacklogMapper.getSprintPlanId();
			currentPoint = mSprintBacklogLogic
					.getCurrentPoint(ScrumEnum.STORY_ISSUE_TYPE);
			limitedPoint = mSprintBacklogMapper.getLimitedPoint();
			taskPoint = mSprintBacklogLogic
					.getCurrentPoint(ScrumEnum.TASK_ISSUE_TYPE);

			ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(
					mIProject);
			releaseId = Integer.parseInt(releasePlanHelper.getReleaseID(Integer
					.toString(currentSprintId)));

			sprintGoal = mSprintBacklogMapper.getSprintGoal();

			result = new Translation().translateStoryToJson(issues,
					currentSprintId, currentPoint, limitedPoint, taskPoint,
					releaseId, sprintGoal);
		} else {
			issues = new ArrayList<IIssue>();
			result = new Translation().translateStoryToJson(issues,
					currentSprintId, currentPoint, limitedPoint, taskPoint,
					releaseId, sprintGoal);
		}
		return result;
	}

	/**
	 * path: AjaxGetSprintBacklogDateInfo.do class:
	 * ntut.csie.ezScrum.web.action.backlog.AjaxGetSprintBacklogDateInfoAction
	 */
	public String getAjaxGetSprintBacklogDateInfo() {
		String result = "";
		// 建立 DateColumnStore 的資料
		if ((mSprintBacklogMapper != null)
				&& (mSprintBacklogMapper.getSprintPlanId() > 0)) {
			Date StartDate = mSprintBacklogMapper.getSprintStartDate();
			// 取得工作天數
			int availableDays = mSprintBacklogLogic
					.getSprintAvailableDays(mSprintId);

			List<SprintBacklogDateColumn> cols = mSprintBacklogLogic
					.calculateSprintBacklogDateList(StartDate, availableDays);

			result = (new Gson()).toJson(cols);
			result = "{\"Dates\":" + result + "}";
		} else {
			// default data for null sprint backlog
			result = "";
		}
		return result;
	}

	/**
	 * path: AjaxShowStoryfromSprint.do class: AjaxShowStoryFromSprintAction
	 */
	public StringBuilder getStoriesInSprintResponseText(
			List<? extends IIssue> stories) {
		StringBuilder sb = new StringBuilder();
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		sb.append("<ExistingStories>");

		for (IIssue issue : stories) {
			String releaseId = issue.getReleaseID();
			if (releaseId.equals("") || releaseId.equals("0")
					|| releaseId.equals("-1"))
				releaseId = "None";

			String sprintId = issue.getSprintID();
			if (sprintId.equals("") || sprintId.equals("0")
					|| sprintId.equals("-1"))
				sprintId = "None";
			sb.append("<Story>");
			sb.append("<Id>" + issue.getIssueID() + "</Id>");
			sb.append("<Link>" + tsc.TranslateXMLChar(issue.getIssueLink())
					+ "</Link>");
			sb.append("<Name>" + tsc.TranslateXMLChar(issue.getSummary())
					+ "</Name>");
			sb.append("<Value>" + issue.getValue() + "</Value>");
			sb.append("<Importance>" + issue.getImportance() + "</Importance>");
			sb.append("<Estimate>" + issue.getEstimated() + "</Estimate>");
			sb.append("<Status>" + issue.getStatus() + "</Status>");
			sb.append("<Notes>" + tsc.TranslateXMLChar(issue.getNotes())
					+ "</Notes>");
			sb.append("<HowToDemo>"
					+ tsc.TranslateXMLChar(issue.getHowToDemo())
					+ "</HowToDemo>");
			sb.append("<Release>" + releaseId + "</Release>");
			sb.append("<Sprint>" + sprintId + "</Sprint>");
			sb.append("<Tag>"
					+ tsc.TranslateXMLChar(new Translation().Join(
							issue.getTags(), ",")) + "</Tag>");
			sb.append("</Story>");
		}
		sb.append("</ExistingStories>");

		return sb;
	}

}
