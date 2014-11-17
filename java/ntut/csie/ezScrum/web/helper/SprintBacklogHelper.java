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
import ntut.csie.ezScrum.web.dataObject.SprintBacklogDateColumn;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SprintBacklogTreeStructure;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import com.google.gson.Gson;

public class SprintBacklogHelper {
	private IProject project;
	private IUserSession userSession;
	private SprintBacklogLogic sprintBacklogLogic;
	private SprintBacklogMapper sprintBacklogMapper;
	private String sprintID;

	/**
	 * for web service
	 * 
	 * @param project
	 * @param userSession
	 */
	public SprintBacklogHelper(IProject project, IUserSession userSession) {
		this.project = project;
		this.userSession = userSession;
		// this.sprintBacklogMapper = new SprintBacklogMapper(project, userSession);
		this.sprintBacklogLogic = new SprintBacklogLogic(this.project, this.userSession, null);
		this.sprintBacklogMapper = this.sprintBacklogLogic.getSprintBacklogMapper();
	}

	public SprintBacklogHelper(IProject project, IUserSession userSession, String sprintID) {
		this.project = project;
		this.userSession = userSession;
		this.sprintID = sprintID;
		this.sprintBacklogLogic = new SprintBacklogLogic(this.project, this.userSession, this.sprintID);
		this.sprintBacklogMapper = this.sprintBacklogLogic.getSprintBacklogMapper();
	}

	public IIssue getIssue(long issueID) {
		return this.sprintBacklogMapper.getIssue(issueID);
	}

	public IIssue[] getStoryInSprint(String sprintID) {
		return sprintBacklogMapper.getStoryInSprint(Long.parseLong(sprintID));
	}

	public IIssue[] getTaskInStory(String storyID) {
		if (sprintBacklogMapper != null) return sprintBacklogMapper.getTaskInStory(Long.parseLong(storyID));
		return null;
	}

	public void addExistedTask(String storyID, String[] selectedTaskIDs) {
		long[] taskIDs = new long[selectedTaskIDs.length];
		for (int i = 0; i < selectedTaskIDs.length; i++)
			taskIDs[i] = Long.parseLong(selectedTaskIDs[i]);
		if (sprintBacklogMapper != null) {
			Date date = DateUtil.dayFillter(DateUtil.getToday(), DateUtil._8DIGIT_DATE_2);
			sprintBacklogMapper.addExistedTask(taskIDs, Long.parseLong(storyID), date);
		}
	}

	public IIssue createTaskInStory(String storyID, TaskObject task) {
		IIssue issue = null;
		if (sprintBacklogMapper != null) {
			String specificTime = task.specificTime;
			Date date = null;
			if (specificTime.length() == 0) {
				date = DateUtil.dayFillter(DateUtil.getToday(), DateUtil._8DIGIT_DATE_2);
			} else {
				date = DateUtil.dayFillter(specificTime, DateUtil._8DIGIT_DATE_2);
			}
			long issueID = this.sprintBacklogLogic.addTask(task.name, task.notes, task.estimation, task.handler, "", task.notes, Long.parseLong(storyID), date);
			issue = this.sprintBacklogMapper.getIssue(issueID);
		}
		return issue;
	}

	/**
	 * 根據 id 取得 task
	 * 
	 * @param id
	 * @return
	 */
	public IIssue getTaskById(long id) {
		List<IIssue> tasks = this.sprintBacklogMapper.getTasks();
		for (IIssue task : tasks) {
			if (task.getIssueID() == id) return task;
		}
		return null;
	}

	public void deleteTask(String taskID, String storyID) {
		if (sprintBacklogMapper != null) sprintBacklogMapper.deleteTask(Long.parseLong(taskID), Long.parseLong(storyID));
	}

	public void dropTask(String taskID, String storyID) {
		if (sprintBacklogMapper != null) sprintBacklogMapper.removeTask(Long.parseLong(taskID), Long.parseLong(storyID));
	}

	public boolean editTask(TaskObject task) {
		if (sprintBacklogMapper != null) {
			Date date = DateUtil.dayFillter(DateUtil.getToday(), DateUtil._8DIGIT_DATE_2);
			// partner 暫時沒寫
			return this.sprintBacklogLogic.editTask(Long.parseLong(task.id),
			        task.name, task.estimation, task.remains,
			        task.handler, "", task.actual,
			        task.notes, date);
		}
		return false;
	}

	/**
	 * Task從Check Out->Not Check Out
	 * path: ResetTask.do
	 * class: ResetTaskAction
	 * Test class: ResetTaskActionTest
	 */
	public void resetTask(long id, String name, String bugNote, String changeDate) {
		if (sprintBacklogMapper != null) sprintBacklogMapper.resetTask(id, name, bugNote, changeDate);
		TaskObject taskObj = new TaskObject(getIssue(id));
		editTask(taskObj);
	}

	public List<IIssue> getStoriesByImportance() {
		return this.sprintBacklogLogic.getStoriesByImp();
	}

	/**
	 * 取得尚未被施工的所有Story
	 * 
	 * @param releaseID
	 * @return
	 */
	public List<IStory> getExistedStories(String releaseID) throws SQLException {
		List<IStory> stories = null;
		ProductBacklogLogic productBacklogHelper = new ProductBacklogLogic(this.userSession, this.project);
		if ((sprintID != null) && (!sprintID.isEmpty()) && (!sprintID.equals("-1"))) {
			if (Integer.parseInt(sprintID) > 0) {
				// get release ID by sprint ID
				ReleasePlanHelper rphelper = new ReleasePlanHelper(project);
				releaseID = rphelper.getReleaseID(sprintID);
				// get stories that exist in
				stories = productBacklogHelper.getAddableStories(sprintID, releaseID);
			}
		} else if ((releaseID != null) && (!releaseID.isEmpty()) && (!releaseID.equals("-1"))) {
			// Select from Release Plan
			// get stories that exist in
			if (Integer.parseInt(releaseID) > 0) {
				stories = productBacklogHelper.getAddableStories();
			}
		} else {
			stories = null;
		}
		return stories;
	}

	/**
	 * 新增以存在的Story至專案中
	 * 
	 * @param list
	 * @param releaseID
	 */
	public void addExistedStory(List<Long> list, String releaseID) {
		ProductBacklogLogic helper = new ProductBacklogLogic(this.userSession, this.project);

		if ((sprintID != null) && (!sprintID.isEmpty()) && (!sprintID.equals("-1"))) {
			// 將Story加入Sprint當中
			helper.addIssueToSprint(list, sprintID);

			// 檢查Sprint是否有存在於某個Release中
			ReleasePlanHelper releasePlan = new ReleasePlanHelper(project);
			String sprintReleaseID = releasePlan.getReleaseID(sprintID);

			// 如果有的話，將所有Story加入Release
			if (!(sprintReleaseID.equals("0"))) {
				helper.addReleaseTagToIssue(list, sprintReleaseID);
			}
		} else {
			helper.addReleaseTagToIssue(list, releaseID);
		}
	}

	/**
	 * ------ 處理Action的response information ------
	 */

	/**
	 * path: showSprintBacklogTreeListInfo.do
	 * class: ntut.csie.ezScrum.web.action.backlog.ShowSprintBacklogListInfoAction
	 * 
	 * @return
	 */
	public String getSprintBacklogListInfoText() {
		List<SprintBacklogTreeStructure> SBtree = new ArrayList<SprintBacklogTreeStructure>();
		if (this.sprintBacklogMapper != null) {
			// 取得工作天數
			int availableDays = sprintBacklogLogic.getSprintAvailableDays(this.sprintID);

			if (this.sprintBacklogMapper.getSprintPlanId() > 0) {
				// List<IIssue> stories = this.sprintBacklogMapper.getStoriesByImp();
				List<IIssue> stories = this.getStoriesByImportance();
				Map<Long, IIssue[]> map = this.sprintBacklogMapper.getTasksMap();

				// 取得 Sprint 日期的 Column
				List<SprintBacklogDateColumn> cols = null;
				if (this.sprintBacklogLogic.getCurrentDateColumns() == null) cols = this.sprintBacklogLogic
				        .calculateSprintBacklogDateList(this.sprintBacklogMapper.getSprintStartDate(), availableDays);
				else cols = this.sprintBacklogLogic.getCurrentDateColumns();

				for (IIssue story : stories) {
					SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(story, map.get(Long.valueOf(story.getIssueID())), this.sprintBacklogLogic.getCurrentDateList());
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
	 * path: showSprintBacklog2.do
	 * class: ntut.csie.ezScrum.web.action.backlog.ShowSprintBacklogAction
	 * 
	 * @return
	 */
	public String getShowSprintBacklogText() {
		String result;
		// 建立 this Sprint Store 的資料
		List<IIssue> issues = null;
		int currentSprintID = 0;
		int releaseID = 0;
		double currentPoint = 0.0d;
		double limitedPoint = 0.0d;
		double taskPoint = 0.0d;
		String sprintGoal = "";
		if ((this.sprintBacklogMapper != null) && (this.sprintBacklogMapper.getSprintPlanId() > 0)) {
			// 存在一 current sprint
			issues = this.getStoriesByImportance();
			currentSprintID = this.sprintBacklogMapper.getSprintPlanId();
			currentPoint = this.sprintBacklogLogic.getCurrentPoint(ScrumEnum.STORY_ISSUE_TYPE);
			limitedPoint = this.sprintBacklogMapper.getLimitedPoint();
			taskPoint = this.sprintBacklogLogic.getCurrentPoint(ScrumEnum.TASK_ISSUE_TYPE);

			ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
			releaseID = Integer.parseInt(releasePlanHelper.getReleaseID(Integer.toString(currentSprintID)));

			sprintGoal = this.sprintBacklogMapper.getSprintGoal();

			result = new Translation().translateStoryToJson(issues, currentSprintID, currentPoint, limitedPoint, taskPoint, releaseID, sprintGoal);
		} else {
			issues = new ArrayList<IIssue>();
			result = new Translation().translateStoryToJson(issues, currentSprintID, currentPoint, limitedPoint, taskPoint, releaseID, sprintGoal);
		}
		return result;
	}

	/**
	 * path: AjaxGetSprintBacklogDateInfo.do
	 * class: ntut.csie.ezScrum.web.action.backlog.AjaxGetSprintBacklogDateInfoAction
	 * 
	 * @return
	 */
	public String getAjaxGetSprintBacklogDateInfo() {
		String result = "";
		// 建立 DateColumnStore 的資料
		if ((this.sprintBacklogMapper != null) && (this.sprintBacklogMapper.getSprintPlanId() > 0)) {
			Date StartDate = sprintBacklogMapper.getSprintStartDate();
			// 取得工作天數
			int availableDays = this.sprintBacklogLogic.getSprintAvailableDays(sprintID);

			List<SprintBacklogDateColumn> cols = this.sprintBacklogLogic.calculateSprintBacklogDateList(StartDate, availableDays);

			result = (new Gson()).toJson(cols);
			result = "{\"Dates\":" + result + "}";
		} else {
			// default data for null sprint backlog
			result = "";
		}
		return result;
	}

	/**
	 * path: AjaxShowStoryfromSprint.do
	 * class: AjaxShowStoryFromSprintAction
	 * 
	 * @return
	 */
	public StringBuilder getStoriesInSprintResponseText(List<? extends IIssue> stories) {
		StringBuilder sb = new StringBuilder();
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		sb.append("<ExistingStories>");

		for (IIssue issue : stories) {
			String releaseId = issue.getReleaseID();
			if (releaseId.equals("") || releaseId.equals("0") || releaseId.equals("-1")) releaseId = "None";

			String sprintId = issue.getSprintID();
			if (sprintId.equals("") || sprintId.equals("0") || sprintId.equals("-1")) sprintId = "None";
			sb.append("<Story>");
			sb.append("<Id>" + issue.getIssueID() + "</Id>");
			sb.append("<Link>" + tsc.TranslateXMLChar(issue.getIssueLink()) + "</Link>");
			sb.append("<Name>" + tsc.TranslateXMLChar(issue.getSummary()) + "</Name>");
			sb.append("<Value>" + issue.getValue() + "</Value>");
			sb.append("<Importance>" + issue.getImportance() + "</Importance>");
			sb.append("<Estimate>" + issue.getEstimated() + "</Estimate>");
			sb.append("<Status>" + issue.getStatus() + "</Status>");
			sb.append("<Notes>" + tsc.TranslateXMLChar(issue.getNotes()) + "</Notes>");
			sb.append("<HowToDemo>" + tsc.TranslateXMLChar(issue.getHowToDemo()) + "</HowToDemo>");
			sb.append("<Release>" + releaseId + "</Release>");
			sb.append("<Sprint>" + sprintId + "</Sprint>");
			sb.append("<Tag>" + tsc.TranslateXMLChar(new Translation().Join(issue.getTags(), ",")) + "</Tag>");
			sb.append("</Story>");
		}
		sb.append("</ExistingStories>");

		return sb;
	}

	/**
	 * path: AjaxRemoveSprintTask.do
	 * class: AjaxRemoveSprintTaskAction
	 * Test class: AjaxRemoveSprintTaskTest
	 */
	public void removeTask(long issueID, long parentID) {
		String name = getIssue(issueID).getSummary();

		// reset status, handler
		resetTask(issueID, name, null, "");
		// remove relation
		dropTask(String.valueOf(issueID), String.valueOf(parentID));
	}

	/**
	 * Task從Not Check Out->Check Out
	 * path: CheckOutTask.do
	 * class: CheckOutTaskAction
	 * Test class: CheckOutTaskActionTest
	 */
	public void checkOutTask(long issueID, String name, String handler, String partners, String bugNote, String changeDate) {
		sprintBacklogLogic.checkOutTask(issueID, name, handler, partners, bugNote, changeDate);
	}

	/**
	 * Story從Not Check Out->Done 或是 Task從Check Out->Done
	 * path: DoneIssue.do
	 * class: DoneIssueAction
	 * Test class: DoneIssueActionTest
	 */
	public void doneIssue(long issueID, String name, String bugNote, String changeDate, String ActualHour) {
		sprintBacklogLogic.doneIssue(issueID, name, bugNote, changeDate, ActualHour);
	}

	/**
	 * Story從Done->Not Check Out 或是 Task從Done->Check Out
	 * path: ReopenIssue.do
	 * class: ReopenIssueAction
	 * Test class: ReopenIssueActionTest
	 */
	public void reopenIssue(long issueID, String name, String bugNote, String changeDate) {
		sprintBacklogMapper.reopenIssue(issueID, name, bugNote, changeDate);
	}
}
